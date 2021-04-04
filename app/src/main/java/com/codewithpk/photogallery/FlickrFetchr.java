package com.codewithpk.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import com.codewithpk.photogallery.api.FlickrApi;
import com.codewithpk.photogallery.api.FlickrResponse;
import com.codewithpk.photogallery.api.PhotoResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Creating FlickrFetchr
public class FlickrFetchr {
    private static String TAG = "FlickrFetchr";
    private FlickrApi mFlickrApi;

    FlickrFetchr() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/") //Updating the base URL/// but the endpoints we want to hit are at com.codewithpk.photogallery.api.flickr.com/services/rest which is defined in path
                .addConverterFactory(GsonConverterFactory.create()) //Updating FlickrFetchr for Gson
                .build();
        mFlickrApi = retrofit.create(FlickrApi.class);
    }
//Adding fetchContents() Here to call it from PhotoGalleryFragment
//Updating the base URL and call fetchPhotos
//Updating FlickrFetchr for Gson
    LiveData<List<GalleryItem>>fetchPhotos() {
        MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>(); ////Updating FlickrFetchr for Gson
        Call<FlickrResponse> flickrRequest = mFlickrApi.fetchPhotos(); //Updating the base URL and call fetchPhotos
        flickrRequest.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                Log.d(TAG, "Response received");
                //responseLiveData.setValue(response.body());
                FlickrResponse flickrResponse = response.body();
                PhotoResponse photoResponse = flickrResponse.photos;
                List<GalleryItem> galleryItems = photoResponse.galleryItems;
                responseLiveData.setValue(galleryItems);
            }
            @Override
            public void onFailure(Call<FlickrResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch photos", t);
            }
        });
        return responseLiveData;
    }
//Adding image downloading to FlickrFetchr
    @WorkerThread
    Bitmap fetchPhoto(String url) {
        try {
            Response<ResponseBody> response = mFlickrApi.fetchUrlBytes(url).execute();
            Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            Log.i(TAG, "Decoded bitmap from Response");
            return bitmap;
        } catch (Exception exception) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            return Bitmap.createBitmap(150, 150, conf);
        }
    }
}
