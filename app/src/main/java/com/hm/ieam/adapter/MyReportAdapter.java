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

import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.bean.RepairBean;

import java.util.List;

public class MyReportAdapter extends BaseAdapter {
    List<RepairBean> list;
    Context context;
    LayoutInflater inflater;
    MyGlideUtils myGlideUtils;
//    public MyReportAdapter(Context context, List<RepairBean> list,MyReportAdapter.OnItemDeleteClickListener onItemDeleteClickListener){
//        this.onItemDeleteClickListener=onItemDeleteClickListener;
//        this.list=list;
//        this.context=context;
//        inflater = LayoutInflater.from(context);
//        myGlideUtils=new MyGlideUtils();
//    }
    public MyReportAdapter(Context context, List<RepairBean> list){
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
        ViewHodler viewHodler;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.pop_my_report_listview_item,null,false);
            // 初始化 ViewHolder 方便重用
            viewHodler = new ViewHodler();
            viewHodler.report_image = convertView.findViewById(R.id.report_list_item_img);
            viewHodler.report_address = convertView.findViewById(R.id.report_list_item_address);
            viewHodler.report_state = convertView.findViewById(R.id.report_list_item_state);
            viewHodler.report_position = convertView.findViewById(R.id.report_list_item_position);
    //        viewHodler.btn_delete=convertView.findViewById(R.id.report_list_item_delete);
            convertView.setTag(viewHodler);
        }else{ // 否则进行重用
            viewHodler = (ViewHodler)convertView.getTag();
        }

        final RepairBean repairBean=list.get(position);
        String[] images=repairBean.getImages().split(",");
        myGlideUtils.loadImage(context,60,60, viewHodler.report_image,images[0]);
        viewHodler.report_address.setText(repairBean.getReport_address());
        viewHodler.report_state.setText(repairBean.getRepair_state());
        viewHodler.report_position.setText(repairBean.getReport_position());
//        if(repairBean.getRepair_state().equals("报修")){
//            viewHodler.btn_delete.setVisibility(View.VISIBLE);
//        }
//        else{
//            viewHodler.btn_delete.setVisibility(View.INVISIBLE);
//        }
//        viewHodler.btn_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (onItemDeleteClickListener != null){
//                    Log.i("delete","delete");
//                    onItemDeleteClickListener.onItemDeleteClick(repairBean.getId(),position);
//                }
//                else{
//                    Log.i("onItem","null");
//                }
//
//            }
//        });


        return convertView;
    }
    class ViewHodler{
        ImageView report_image;
        TextView report_address;
        TextView report_state;
        TextView report_position;
    //    Button btn_delete;
    }
//    private MyReportAdapter.OnItemDeleteClickListener onItemDeleteClickListener;
//    public void setOnItemDeleteClickListener(MyReportAdapter.OnItemDeleteClickListener onItemDeleteClickListener) {
//        this.onItemDeleteClickListener = onItemDeleteClickListener;
//
//    }
//    public interface OnItemDeleteClickListener {
//        void onItemDeleteClick(String id,int position);
//    }
}
