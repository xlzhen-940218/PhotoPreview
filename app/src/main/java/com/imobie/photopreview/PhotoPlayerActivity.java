package com.imobie.photopreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.imobie.photolib.view.PhotoPreviewView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class PhotoPlayerActivity extends Activity implements View.OnClickListener {

    private PhotoPreviewView photoPreviewView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_player);
        photoPreviewView= findViewById(R.id.photo_pre_view);
        photoPreviewView.setCallback(new PhotoPreviewView.Callback() {
            @Override
            public void vFinish() {
                finish();
            }

            @Override
            public void loadImage(String filePath, ImageView imageView) {
                ImageLoader.getInstance().displayImage("file://" + filePath
                        , imageView
                        , new DisplayImageOptions.Builder()
                                .considerExifParams(true)
                                .build());
            }

            @Override
            public void clickToolLeft() {
                photoPreviewView.onBackPressed();
            }

            @Override
            public void clickToolRight() {

            }

            @Override
            public void clickToolLeft2() {

            }

            @Override
            public void loadFirstImage(String filePath, ImageView imageView) {
                ImageLoader.getInstance().displayImage("file://" + filePath
                        , (ImageView) imageView
                        , new DisplayImageOptions.Builder()
                                .considerExifParams(true)
                                .build(), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                photoPreviewView.loadFirstImageAnim();
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });
            }
        });

        photoPreviewView.setData(getIntent().getIntExtra("previewPosition", -1));
    }

    @Override
    public void onBackPressed() {
        if (photoPreviewView.containCurrentRect()) {
            photoPreviewView.onBackPressed();
        } else
            super.onBackPressed();
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }
}
