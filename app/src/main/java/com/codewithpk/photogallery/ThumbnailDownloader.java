package com.codewithpk.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//Initial thread code
public class ThumbnailDownloader <T> extends HandlerThread   {
    private static final String TAG = "ThumbnailDownloader";
    //Adding a constant and properties
    private static final int MESSAGE_DOWNLOAD = 0;
    private boolean mHasQuit = false;
    //Adding a constant and properties
    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
    private FlickrFetchr mFlickrFetchr = new FlickrFetchr();
//Adding the responseHandler
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
//Handling a message
    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
    //Obtaining and sending a message
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }
    //Handling a message
    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);
        if (url == null) {
            return;
        }
        Bitmap bitmap = new FlickrFetchr().fetchPhoto(url);
        Log.i(TAG, "Bitmap created");
    //Downloading and displaying images
        mResponseHandler.post(new Runnable() {
            public void run() {
                if (mRequestMap.get(target) != url ||
                        mHasQuit) {
                    return;
                }
                mRequestMap.remove(target);
                mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
            }
        });
    }
//Refactoring fragment lifecycle observer
LifecycleObserver mFragmentLifecycleObserver = new LifecycleObserver()
{
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void setup() {
        Log.i(TAG, "Starting background thread");
        start();
        getLooper();
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void tearDown() {
        Log.i(TAG, "Destroying background thread");
        quit();
    }
};
    //Adding a view lifecycle observer
    LifecycleObserver mViewLifecycleObserver = new LifecycleObserver() {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void clearQueue() {
            Log.i(TAG, "Clearing all requests from queue");
            mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
            mRequestMap.clear();
        }
    };
}
