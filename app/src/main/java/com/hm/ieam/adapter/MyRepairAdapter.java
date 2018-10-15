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
import com.hm.ieam.bean.RepairBean;
import com.hm.ieam.utils.MyGlideUtils;

import java.util.List;

public class MyRepairAdapter extends BaseAdapter{
    List<RepairBean> list;
    Context context;
    LayoutInflater inflater;
    MyGlideUtils myGlideUtils;

    public MyRepairAdapter(Context context, List<RepairBean> list){

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

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        MyRepairAdapter.ViewHodler viewHodler;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.pop_my_repair_listview_item,null,false);
            // 初始化 ViewHolder 方便重用
            viewHodler = new MyRepairAdapter.ViewHodler();
            viewHodler.repair_image = convertView.findViewById(R.id.repair_list_item_img);
            viewHodler.repair_deal = convertView.findViewById(R.id.repair_list_item_deal);
            viewHodler.repair_state = convertView.findViewById(R.id.repair_list_item_state);
            viewHodler.repair_address = convertView.findViewById(R.id.repair_list_item_address);

            convertView.setTag(viewHodler);
        }else{ // 否则进行重用
            viewHodler = (MyRepairAdapter.ViewHodler)convertView.getTag();
        }

        final RepairBean repairBean=list.get(position);
        String[] images=repairBean.getImages().split(",");
        myGlideUtils.loadImage(context,60,60, viewHodler.repair_image,images[0]);
        viewHodler.repair_deal.setText(repairBean.getRepair_deal());
        viewHodler.repair_state.setText(repairBean.getRepair_state());
        viewHodler.repair_address.setText(repairBean.getReport_address());



        return convertView;
    }
    class ViewHodler{
        ImageView repair_image;
        TextView repair_deal;
        TextView repair_state;
        TextView repair_address;

    }

}
