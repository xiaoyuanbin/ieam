package com.hm.ieam.widght;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hm.ieam.R;
import com.hm.ieam.adapter.MyAddressAdapter;
import com.hm.ieam.bean.AddressBean;

import java.util.ArrayList;
import java.util.List;

public class AddressSelect extends LinearLayout implements View.OnClickListener {
    private Context mContext;

    ListView listView;
    MyAddressAdapter adapter;


    List<String> address;

    Button btn_save;
  //  TextView tv_area;
    TextView tv_community;
    TextView tv_building;
    TextView tv_unit;
    TextView tv_floor;
    TextView tv_house;

    ImageView img_go;
    ImageView img_go2;
    ImageView img_go3;
    ImageView img_go4;
 //   ImageView img_go5;

    public AddressSelect(Context context) {
        super(context);
        init(context);
    }

    public AddressSelect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressSelect(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        initData();
        mContext = context;


        // UI
        View rootView = inflate(mContext, R.layout.address_select, this);


        listView=rootView.findViewById(R.id.address_select_listview);

        btn_save=rootView.findViewById(R.id.address_select_btn_save);
   //     tv_area=rootView.findViewById(R.id.address_select_tv_area);
        tv_community=rootView.findViewById(R.id.address_select_tv_community);
        tv_building=rootView.findViewById(R.id.address_select_tv_building);
        tv_unit=rootView.findViewById(R.id.address_select_tv_unit);
        tv_floor=rootView.findViewById(R.id.address_select_tv_floor);
        tv_house=rootView.findViewById(R.id.address_select_tv_house);

        img_go=rootView.findViewById(R.id.address_select_img_go);
        img_go2=rootView.findViewById(R.id.address_select_img_go2);
        img_go3=rootView.findViewById(R.id.address_select_img_go3);
        img_go4=rootView.findViewById(R.id.address_select_img_go4);
  //      img_go5=rootView.findViewById(R.id.address_select_img_go5);

        btn_save.setOnClickListener(this);
    //    tv_area.setOnClickListener(this);
        tv_community.setOnClickListener(this);
        tv_building.setOnClickListener(this);
        tv_unit.setOnClickListener(this);
        tv_floor.setOnClickListener(this);
        tv_house.setOnClickListener(this);


        adapter=new MyAddressAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });

    }

    private void initData() {

        address=new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.address_select_tv_area:
//                tv_building.setText("");
//                img_go.setVisibility(INVISIBLE);
//                img_go2.setVisibility(INVISIBLE);
//                img_go3.setVisibility(INVISIBLE);
//                img_go4.setVisibility(INVISIBLE);
//                img_go5.setVisibility(INVISIBLE);
//
//
//
//
//                break;
            case R.id.address_select_tv_community:
                tv_building.setText("");
                img_go.setVisibility(INVISIBLE);
                img_go2.setVisibility(INVISIBLE);
                img_go3.setVisibility(INVISIBLE);
                img_go4.setVisibility(INVISIBLE);
                break;
            case R.id.address_select_tv_building:
                break;
            case R.id.address_select_tv_unit:
                break;
            case R.id.address_select_tv_floor:
                break;
            case R.id.address_select_tv_house:
                break;
            case R.id.address_select_btn_save:
                onSure();
                break;


        }
    }

    private void onSure() {



    }

    class MyAddressAdapter extends BaseAdapter {
        List<String> addressList;
        LayoutInflater layoutInflater;


        public MyAddressAdapter(Context context, List<String> addressList) {

            this.addressList = addressList;

            layoutInflater = LayoutInflater.from(context);

        }
        public MyAddressAdapter() {


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
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.address_select_listview_item, null, false);
                // 初始化 ViewHolder 方便重用
                viewHodler = new ViewHodler();

                viewHodler.address = convertView.findViewById(R.id.address_name);

                convertView.setTag(viewHodler);
            } else { // 否则进行重用
                viewHodler = (ViewHodler) convertView.getTag();
            }

            viewHodler.address.setText(addressList.get(position));


            return convertView;
        }

        class ViewHodler {

            TextView address;


        }
    }

}
