package com.codewithpk.photogallery.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FlickrApi {
   /* @GET("/")
    Call<String> fetchContents(); */
//Defining the fetch recent interesting photos request
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=135bc1f10ea622debf7768c72bc06df8" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    //Updating fetchPhoto()’s return type
    Call<FlickrResponse> fetchPhotos();
}