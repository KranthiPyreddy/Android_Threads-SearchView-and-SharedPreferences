package com.codewithpk.photogallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

    //Using the Retrofit object to create an instance of the API
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel.class);

    }
    @Override
    // Inflate the layout you just created and initialize a member variable referencing the RecyclerView.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = view.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new
                GridLayoutManager(getContext(), 3));
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
        TextView mTitleTextView;
        public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_gallery, parent, false));
            mTitleTextView = (TextView) itemView.findViewById(R.id.photo_title);
        }
        public void bind(String text) {
            mTitleTextView.setText(text);
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
            holder.bind(galleryItem.title);
        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
