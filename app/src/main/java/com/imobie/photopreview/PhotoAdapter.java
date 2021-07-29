package com.imobie.photopreview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private List<PhotoBean> photoBeans;

    public PhotoAdapter(Context context, List<PhotoBean> photoBeans) {
        this.context = context;
        this.photoBeans = photoBeans;
    }

    @Override
    public int getCount() {
        return photoBeans.size();
    }

    @Override
    public PhotoBean getItem(int position) {
        return photoBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return photoBeans.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_photo, parent, false);
            convertView.getLayoutParams().width = (DensityUtils.getScreenW(context) / 4) - DensityUtils.dp2px(4);
            convertView.getLayoutParams().height = convertView.getLayoutParams().width;
        }

        ImageLoader.getInstance().displayImage("file://" + getItem(position).get_data()
                , (ImageView) convertView, new DisplayImageOptions.Builder().considerExifParams(true).showImageOnLoading(R.mipmap.ic_launcher).build());

        return convertView;
    }

    public void setData(List<PhotoBean> beans) {
        this.photoBeans=beans;
        notifyDataSetChanged();
    }
}
