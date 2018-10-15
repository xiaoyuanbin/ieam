package com.hm.ieam.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.ieam.R;
import com.hm.ieam.bean.AssetsBean;
import com.hm.ieam.bean.InspectBean;
import com.hm.ieam.utils.MyGlideUtils;

import java.util.List;

public class MyAssetsAdapter extends BaseAdapter{
    List<AssetsBean> list;
    Context context;
    LayoutInflater inflater;
    MyGlideUtils myGlideUtils;
    public MyAssetsAdapter(Context context, List<AssetsBean> list,OnItemDeleteClickListener onItemDeleteClickListener){
        this.onItemDeleteClickListener = onItemDeleteClickListener;
        this.list=list;
        this.context=context;
        inflater = LayoutInflater.from(context);
        myGlideUtils=new MyGlideUtils();
    }
    public MyAssetsAdapter(Context context, List<AssetsBean> list){
        this.list=list;
        this.context=context;
        inflater = LayoutInflater.from(context);
        myGlideUtils=new MyGlideUtils();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public List<AssetsBean> getDate(){
        return list;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHodler viewHodler;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.pop_my_assets_listview_item,null,false);
            // 初始化 ViewHolder 方便重用
            viewHodler = new ViewHodler();
            viewHodler.inspect_img = convertView.findViewById(R.id.assets_list_item_img);
            viewHodler.assets_list_item_position = convertView.findViewById(R.id.assets_list_item_address);
            viewHodler.assets_list_item_name = convertView.findViewById(R.id.assets_list_item_name);
            viewHodler.assets_list_item_date = convertView.findViewById(R.id.assets_list_item_date);
            viewHodler.btn_delete = convertView.findViewById(R.id.assets_list_item_delete);
            convertView.setTag(viewHodler);
        }else{ // 否则进行重用
            viewHodler = (ViewHodler)convertView.getTag();
        }

        final AssetsBean assetsBean=list.get(position);
        String[] images=assetsBean.getImages().split(",");

        myGlideUtils.loadImage(context,60,60, viewHodler.inspect_img,images[0]);

        viewHodler.assets_list_item_position.setText(assetsBean.getPosition());
        viewHodler.assets_list_item_name.setText(assetsBean.getName());
        viewHodler.assets_list_item_date.setText(assetsBean.getEntrydate());
        viewHodler.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onItemDeleteClickListener != null){
                    Log.i("delete","delete");
                    onItemDeleteClickListener.onItemDeleteClick(assetsBean.getId(),position);
                }
                else{
                    Log.i("onItem","null");
                }

            }
        });

        return convertView;
    }
    class ViewHodler{
        ImageView inspect_img;
        TextView assets_list_item_position;
        TextView assets_list_item_name;
        TextView assets_list_item_date;
        Button btn_delete;



    }

    private OnItemDeleteClickListener onItemDeleteClickListener;
    public void setOnItemDeleteClickListener(OnItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;

    }
    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(String id,int position);
    }

}
