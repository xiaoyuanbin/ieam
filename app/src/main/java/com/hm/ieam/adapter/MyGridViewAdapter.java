package com.hm.ieam.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hm.ieam.R;
import com.hm.ieam.utils.MyGlideUtils;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    private Context context;
    int maxCount=4;     //最多显示4张图片
    private List<String> mImages;
    Uri addUri;
    MyGlideUtils myGlideUtils;
    LayoutInflater layoutInflater;
    public MyGridViewAdapter(Context context, List<String> mImages) {
        this.context = context;
        this.mImages=mImages;

        layoutInflater = LayoutInflater.from(context);
        myGlideUtils=new MyGlideUtils();
        addUri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.getResources().getResourcePackageName(R.drawable.add) + "/"
                + context.getResources().getResourceTypeName(R.drawable.add) + "/"
                + context.getResources().getResourceEntryName(R.drawable.add));

    }


    @Override
    public int getCount() {
        if(mImages.size()==0){
            return 1;
        }

        return mImages.size()<maxCount? mImages.size()+1:maxCount;
    }
    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHodler viewHodler;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.image_gridview_item,null,false);
            // 初始化 ViewHolder 方便重用
            viewHodler = new ViewHodler();
            viewHodler.imageView = convertView.findViewById(R.id.inspect_img_item);
            convertView.setTag(viewHodler);
        }else{ // 否则进行重用
            viewHodler = (ViewHodler)convertView.getTag();
        }

        Log.i("adpter",position+"");
        if(mImages.size()==0){
            myGlideUtils.loadImage(context, 80, 80, viewHodler.imageView,addUri);
            viewHodler.imageView.setScaleType(ImageView.ScaleType.CENTER);
        }
        else if(position<mImages.size()) {
            myGlideUtils.loadImage(context, 80, 80, viewHodler.imageView, mImages.get(position));
        }
        else {
            myGlideUtils.loadImage(context, 80, 80, viewHodler.imageView,addUri);
            viewHodler.imageView.setScaleType(ImageView.ScaleType.CENTER);
         //   Glide.with(context).load(addUri).into(viewHodler.imageView);
        }



        return convertView;
    }
    class ViewHodler{
        ImageView imageView;
    }
}
