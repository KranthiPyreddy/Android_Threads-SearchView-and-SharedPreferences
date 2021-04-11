package com.codewithpk.photogallery;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {
    LiveData<List<GalleryItem>> galleryItemLiveData;
// Storing the most recent query in PhotoGalleryViewModel
    private FlickrFetchr mFlickrFetchr = new FlickrFetchr();
    private MutableLiveData<String> mMutableSearchTerm = new MutableLiveData<String>();

    public PhotoGalleryViewModel() {
        mMutableSearchTerm.setValue("llama");
        galleryItemLiveData = Transformations.switchMap(mMutableSearchTerm,
                new Function<String, LiveData<List<GalleryItem>>>() {
                    @Override
                    public LiveData<List<GalleryItem>> apply(String searchTerm) {
    //Fetching interesting photos when the query is blank
                        if (searchTerm.isEmpty()) {
                            return mFlickrFetchr.fetchPhotos();
                        } else {
                            return mFlickrFetchr.searchPhotos(searchTerm);
                        }
                        //return mFlickrFetchr.searchPhotos(searchTerm);
                    }
                });
    }
    public void fetchPhotos(String query) {
        mMutableSearchTerm.setValue(query);
    }
}
