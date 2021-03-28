package com.codewithpk.photogallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {
    LiveData<List<GalleryItem>> galleryItemLiveData;

    public PhotoGalleryViewModel() {
        galleryItemLiveData = new FlickrFetchr().fetchPhotos();
    }
}