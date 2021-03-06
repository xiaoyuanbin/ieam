package com.hm.ieam.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hm.ieam.R;
import com.hm.ieam.bean.AddressBean;
import com.hm.ieam.utils.MyGlideUtils;

import java.util.List;

public class MyAddressAdapter extends BaseAdapter{
    List<AddressBean> addressList;
    LayoutInflater layoutInflater;
    Context context;
    private  int currentItem=-1;

    public MyAddressAdapter(Context context, List<AddressBean> addressList) {
        this.context = context;
        this.addressList=addressList;

        layoutInflater = LayoutInflater.from(context);

    }
    public void setCurrentItem(int currentItem){
        this.currentItem=currentItem;
    }
    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHodler viewHodler;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.address_select_listview_item,null,false);
            // 初始化 ViewHolder 方便重用
            viewHodler = new ViewHodler();

            viewHodler.address = convertView.findViewById(R.id.address_name);

            convertView.setTag(viewHodler);
        }else{ // 否则进行重用
            viewHodler = (ViewHodler)convertView.getTag();
        }

        viewHodler.address.setText(addressList.get(position).getN());

        if(currentItem==position){
            Log.i("currentItem is",currentItem+"");
            viewHodler.address.setSelected(true);
            viewHodler.address.setTextColor(context.getResources().getColor(R.color.green));
        }
        else{
            viewHodler.address.setSelected(false);
            viewHodler.address.setTextColor(context.getResources().getColor(R.color.black));
        }


        return convertView;
    }
//    public void setSelectItem(int selectItem) {
//        this.selectItem = selectItem;
//    }
    class ViewHodler{

        TextView address;


    }
}
