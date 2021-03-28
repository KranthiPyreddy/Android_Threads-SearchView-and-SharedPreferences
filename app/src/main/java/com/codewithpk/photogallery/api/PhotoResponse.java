package com.codewithpk.photogallery.api;

import com.codewithpk.photogallery.GalleryItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoResponse {
    //Adding PhotoResponse
    @SerializedName("photo")
    public List<GalleryItem> galleryItems;

}
