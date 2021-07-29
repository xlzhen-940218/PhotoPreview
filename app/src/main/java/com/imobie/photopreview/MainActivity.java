package com.imobie.photopreview;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private GridView photoGridView;
    private PhotoAdapter photoAdapter;

    private PreviewPhotoView previewPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoGridView = findViewById(R.id.photo_grid_view);
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                previewPhotoView = new PreviewPhotoView(MainActivity.this);
                previewPhotoView.setCallback(new PreviewPhotoView.Callback() {
                    @Override
                    public void removeView(View view) {
                        ((ViewGroup) findViewById(android.R.id.content)).removeView(view);
                    }
                });
                ((ViewGroup) findViewById(android.R.id.content)).addView(previewPhotoView);

                previewPhotoView.startPreview(view, photoAdapter.getItem(position));
            }
        });
        imageLoaderConfig();
        boolean authorized = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, PERMISSION_REQUEST_CODE);
                authorized = false;
            } else {
                authorized = true;
            }
        }

        if (authorized) {
            queryPhoto();
        }
    }

    private void queryPhoto() {
        photoAdapter = new PhotoAdapter(this, new ArrayList<>());
        photoGridView.setAdapter(photoAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                IModel<PhotoBean> model = new IModel<>(MainActivity.this, "content://media/external/images/media", PhotoBean.class);
                List<PhotoBean> beans = model.queryAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        photoAdapter.setData(beans);

                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                queryPhoto();
            }
        }
    }

    private void imageLoaderConfig() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(getExternalCacheDir() == null ? getCacheDir() : getExternalCacheDir()))
                .diskCacheFileCount(10000)
                .diskCacheSize(1024 * 1024 * 1024)
                .threadPoolSize(10)
                .build();
        com.nostra13.universalimageloader.utils.L.writeLogs(false);
        com.nostra13.universalimageloader.utils.L.writeDebugLogs(false);
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onBackPressed() {
        if (previewPhotoView != null && previewPhotoView.onBack()) {
            return;
        }
        super.onBackPressed();
    }
}