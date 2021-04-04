package com.codewithpk.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class PhotoGalleryFragment extends Fragment {
    private RecyclerView mPhotoRecyclerView;
    private static String TAG = "PhotoGalleryFragment";
    private PhotoGalleryViewModel mPhotoGalleryViewModel;
    //Creating a ThumbnailDownloader
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    //Using the Retrofit object to create an instance of the API
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel.class);
        //Retaining PhotoGalleryFragment
        setRetainInstance(true);
    //Hooking up to response Handler
        Handler responseHandler = new Handler();
        //Creating a ThumbnailDownloader
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        //Hooking up to response Handler
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder,
                                                      Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );
        //Registering the view lifecycle observer
        getLifecycle().addObserver(mThumbnailDownloader.mFragmentLifecycleObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    //Unregistering the view lifecycle observer
        getViewLifecycleOwner().getLifecycle().removeObserver(mThumbnailDownloader.mViewLifecycleObserver);
    }

    //destroying a ThumbnailDownloader
    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mThumbnailDownloader.mFragmentLifecycleObserver);//Unregistering the view lifecycle observer
    }
    @Override
    // Inflate the layout you just created and initialize a member variable referencing the RecyclerView.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = view.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new
                GridLayoutManager(getContext(), 3));
        //Registering the view lifecycle observer
        getViewLifecycleOwner().getLifecycle().addObserver(mThumbnailDownloader.mViewLifecycleObserver);
        return view;
    }
    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }
//Observing the ViewModel’s live data
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPhotoGalleryViewModel.galleryItemLiveData.observe(
                getViewLifecycleOwner(),
                new Observer<List<GalleryItem>>() {
                    @Override
                    public void onChanged(List<GalleryItem> galleryItems) {
                        //Log.d(TAG, "Have gallery items from ViewModel " + galleryItems);
                        // Eventually, update data backing the recycler view
                        mPhotoRecyclerView.setAdapter(new PhotoAdapter(galleryItems));
                    }
                });
    }
//Adding a ViewHolder implementation
    private class PhotoHolder extends RecyclerView.ViewHolder {
       private ImageView mItemImageView;;
        public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_gallery, parent, false));

            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

    public void bindDrawable(Drawable drawable) {
        mItemImageView.setImageDrawable(drawable);
    }
    }

//Adding a RecyclerView.Adapter implementation
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;;
        }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PhotoHolder(layoutInflater, parent);
        }
        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
           // holder.bind(galleryItem.title);
            //Binding the default image
            Drawable placeholder = ContextCompat.getDrawable(requireContext(), R.drawable.bill_up_close);
            holder.bindDrawable(placeholder);
           // Hooking up ThumbnailDownloader
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.url);
        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
