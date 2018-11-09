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

    public MyAssetsAdapter(Context context, List<AssetsBean> list){
        this.list=list;
        this.context=context;
        inflater = LayoutInflater.from(context);

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
            viewHodler.assets_address = convertView.findViewById(R.id.assets_address);

            convertView.setTag(viewHodler);
        }else{ // 否则进行重用
            viewHodler = (ViewHodler)convertView.getTag();
        }

        final AssetsBean assetsBean=list.get(position);
        viewHodler.assets_address.setText(assetsBean.getName());

        return convertView;
    }
    class ViewHodler{

        TextView assets_address;

    }


}
