package com.imobie.photolib.cache;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewCache {
    public static List<String> files;
    public static List<PreviewRectImage> previewRectImages;

    public static void setCache(AdapterView adapterView,List<String> files) {
        int first = adapterView.getFirstVisiblePosition();
        int last = adapterView.getLastVisiblePosition();
        ImagePreviewCache.previewRectImages=new ArrayList<>();
        for (int i = first; i <= last; i++) {
            int[] rect = new int[4];
            int[] location = new int[2];
            View previewView = adapterView.getChildAt(i-first);
            if(previewView==null)
                continue;
            previewView.getLocationInWindow(location);
            rect[0] = location[0];
            rect[1] = location[1];
            rect[2] = previewView.getWidth();
            rect[3] = previewView.getHeight();
            ImagePreviewCache.previewRectImages.add(new PreviewRectImage(i,rect));
        }
        if (ImagePreviewCache.files == null || ImagePreviewCache.files.size() != files.size()) {

            ImagePreviewCache.files = files;
        }
    }
}
