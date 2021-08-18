package com.imobie.photolib.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.views.GestureImageView;

import java.util.List;

public class PhotoPreviewAdapter extends RecyclePagerAdapter<PhotoPreviewAdapter.ViewHolder> {
    private Callback callback;
    private ViewPager viewPager;
    private List<String> files;

    public PhotoPreviewAdapter(ViewPager pager, List<String> files,Callback callback) {
        this.viewPager = pager;
        this.files = files;
        this.callback=callback;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container) {
        final ViewHolder holder = new ViewHolder(container);

        // Applying custom settings
        /*holder.image.getController().getSettings()
                .setMaxZoom(6f)
                .setDoubleTapZoom(3f);*/

        holder.image.getController().enableScrollInViewPager(viewPager);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /*MainApplication.imageLoader.displayImage("file://" + files.get(position), holder.image, new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .build());*/
        callback.loadImage(files.get(position), holder.image);
    }

    @Override
    public void onRecycleViewHolder(ViewHolder holder) {
        holder.image.setImageDrawable(null);
        holder.image.setImageBitmap(null);
    }

    public void setData(List<String> files) {
        this.files=files;
    }

    static class ViewHolder extends RecyclePagerAdapter.ViewHolder {
        final GestureImageView image;

        ViewHolder(ViewGroup container) {
            super(new GestureImageView(container.getContext()));
            image = (GestureImageView) itemView;
        }
    }

    public interface Callback{
        void loadImage(String filePath, ImageView imageView);
    }
}
