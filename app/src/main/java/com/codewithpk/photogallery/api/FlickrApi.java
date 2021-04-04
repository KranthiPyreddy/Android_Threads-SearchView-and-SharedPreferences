package com.codewithpk.photogallery.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FlickrApi {
    //Updating FlickrApi
    @GET
    Call<ResponseBody> fetchUrlBytes(@Url String url);
//Defining the fetch recent interesting photos request
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=135bc1f10ea622debf7768c72bc06df8" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    //Updating fetchPhoto()’s return type
    Call<FlickrResponse> fetchPhotos();
}
