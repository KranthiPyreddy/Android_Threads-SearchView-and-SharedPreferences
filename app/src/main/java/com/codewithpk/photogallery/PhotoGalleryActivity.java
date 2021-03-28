package com.codewithpk.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PhotoGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
//to check whether a fragment is already hosted in the fragment container
        boolean isFragmentContainerEmpty = savedInstanceState == null;
        if (isFragmentContainerEmpty) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance())
                    .commit();
        }
    }
}

