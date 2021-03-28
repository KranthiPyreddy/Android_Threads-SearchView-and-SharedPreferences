package com.codewithpk.photogallery;

import com.google.gson.annotations.SerializedName;

//Creating a model object class
public class GalleryItem {
    String title;
    String id;
    //Overriding default name-property mapping
    @SerializedName("url_s")
    String url;
    GalleryItem() {
        title = "";
        id = "";
        url = "";
    }
}
