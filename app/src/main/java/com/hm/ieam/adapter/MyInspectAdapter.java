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

import com.bumptech.glide.Glide;
import com.hm.ieam.R;
import com.hm.ieam.bean.InspectBean;

import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.utils.PhotoUtils;

import java.util.List;

public class MyInspectAdapter extends BaseAdapter{
    List<InspectBean> list;
    Context context;
    LayoutInflater inflater;
    MyGlideUtils myGlideUtils;
    public MyInspectAdapter(Context context, List<InspectBean> list,OnItemDeleteClickListener onItemDeleteClickListener){
        this.onItemDeleteClickListener = onItemDeleteClickListener;
        this.list=list;
        this.context=context;
        inflater = LayoutInflater.from(context);
        myGlideUtils=new MyGlideUtils();
    }
    public MyInspectAdapter(Context context, List<InspectBean> list){
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


    public List<InspectBean> getDate(){
        return list;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHodler viewHodler;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.pop_my_inspect_listview_item,null,false);
            // 初始化 ViewHolder 方便重用
            viewHodler = new ViewHodler();
            viewHodler.inspect_img = convertView.findViewById(R.id.inspect_list_item_img);
            viewHodler.inspect_list_item_address = convertView.findViewById(R.id.inspect_list_item_address);
            viewHodler.inspect_list_item_type = convertView.findViewById(R.id.inspect_list_item_type);
            viewHodler.inspect_list_item_content = convertView.findViewById(R.id.inspect_list_item_content);
            viewHodler.btn_delete = convertView.findViewById(R.id.inspect_list_item_delete);
            convertView.setTag(viewHodler);
        }else{ // 否则进行重用
            viewHodler = (ViewHodler)convertView.getTag();
        }

        final InspectBean inspectBean=list.get(position);
        String[] images=inspectBean.getImages().split(",");

        myGlideUtils.loadImage(context,60,60, viewHodler.inspect_img,images[0]);

        viewHodler.inspect_list_item_address.setText(inspectBean.getPosition());
        viewHodler.inspect_list_item_type.setText(inspectBean.getType());
        viewHodler.inspect_list_item_content.setText(inspectBean.getContent());
        viewHodler.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onItemDeleteClickListener != null){
                    Log.i("delete","delete");
                    onItemDeleteClickListener.onItemDeleteClick(inspectBean.getId(),position);
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
        TextView inspect_list_item_address;
        TextView inspect_list_item_type;
        TextView inspect_list_item_content;
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
