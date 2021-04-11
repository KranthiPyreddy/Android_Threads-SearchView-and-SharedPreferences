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
import com.codewithpk.photogallery.api.PhotoInterceptor;
import com.codewithpk.photogallery.api.PhotoResponse;

import okhttp3.OkHttpClient;
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
    //Adding an interceptor to Retrofit configuration
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new PhotoInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/") //Updating the base URL/// but the endpoints we want to hit are at com.codewithpk.photogallery.api.flickr.com/services/rest which is defined in path
                .addConverterFactory(GsonConverterFactory.create()) //Updating FlickrFetchr for Gson
                .client(client)
                .build();
        mFlickrApi = retrofit.create(FlickrApi.class);
    }
    public LiveData<List<GalleryItem>> fetchPhotos() {
        return fetchPhotoMetadata(mFlickrApi.fetchPhotos());
    }
    public LiveData<List<GalleryItem>> searchPhotos(String query) {
        return fetchPhotoMetadata(mFlickrApi.searchPhotos(query));
    }
//Adding fetchContents() Here to call it from PhotoGalleryFragment
//Updating the base URL and call fetchPhotos
//Updating FlickrFetchr for Gson
    private LiveData<List<GalleryItem>> fetchPhotoMetadata(Call<FlickrResponse>
                                                               flickrRequest) {
        MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>(); ////Updating FlickrFetchr for Gson

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
