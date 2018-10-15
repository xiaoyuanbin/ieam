package com.hm.ieam.widght;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hm.ieam.R;
import com.hm.ieam.bean.YwpAddressBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wepon on 2017/12/4.
 * 自定义仿京东地址选择器
 */

public class AddressPickerView extends RelativeLayout implements View.OnClickListener {
    // recyclerView 选中Item 的颜色
    private int defaultSelectedColor = Color.parseColor("#50AA00");
    // recyclerView 未选中Item 的颜色
    private int defaultUnSelectedColor = Color.parseColor("#262626");
    // 确定字体不可以点击时候的颜色
    private int defaultSureUnClickColor = Color.parseColor("#7F7F7F");
    // 确定字体可以点击时候的颜色
    private int defaultSureCanClickColor = Color.parseColor("#50AA00");

    private Context mContext;
    private int defaultTabCount = 6; //tab 的数量
    private TabLayout mTabLayout; // tabLayout
    private RecyclerView mRvList; // 显示数据的RecyclerView
    private String defaultArea = "片区"; //显示在上面tab中的省份
    private String defaultCommunity = "小区"; //显示在上面tab中的城市
    private String defaultBuilding = "楼栋"; //显示在上面tab中的区县
    private String defaultUnit = "单元"; //显示在上面tab中的区县
    private String defaultFloor = "楼层"; //显示在上面tab中的区县
    private String defaultHouse = "房屋"; //显示在上面tab中的区县



    private List<YwpAddressBean.AddressItemBean> mRvData; // 用来在recyclerview显示的数据
    private AddressAdapter mAdapter;   // recyclerview 的 adapter

    private YwpAddressBean mYwpAddressBean; // 总数据
    private YwpAddressBean.AddressItemBean mSelectArea; //选中 片区 bean
    private YwpAddressBean.AddressItemBean mSelectCommunity;//选中 小区  bean
    private YwpAddressBean.AddressItemBean mSelectBuilding;//选中 楼栋  bean
    private YwpAddressBean.AddressItemBean mSelectUnit;//选中 单元  bean
    private YwpAddressBean.AddressItemBean mSelectFloor;//选中 楼层  bean
    private YwpAddressBean.AddressItemBean mSelectHouse;//选中 房屋  bean

    private int mSelectAreaPosition = 0; //选中 片区 位置
    private int mSelectCommunityPosition = 0;//选中 小区  位置
    private int mSelectBuildingPosition = 0;//选中 楼栋  位置
    private int mSelectUnitPosition = 0;//选中 单元  位置
    private int mSelectFloorPosition = 0;//选中 楼层  位置
    private int mSelectHousePosition = 0;//选中 房屋  位置



    private OnAddressPickerSureListener mOnAddressPickerSureListener;
    private TextView mTvSure; //确定

    public AddressPickerView(Context context) {
        super(context);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        mRvData = new ArrayList<>();
        // UI
        View rootView = inflate(mContext, R.layout.address_picker_view, this);
        // 确定
        mTvSure = rootView.findViewById(R.id.tvSure);
        mTvSure.setTextColor(defaultSureUnClickColor);
        mTvSure.setOnClickListener(this);
        // tablayout初始化
        mTabLayout = rootView.findViewById(R.id.tlTabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultArea));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultCommunity));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultBuilding));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultUnit));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultFloor));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultHouse));

        mTabLayout.addOnTabSelectedListener(tabSelectedListener);

        // recyclerview adapter的绑定
        mRvList = rootView.findViewById(R.id.rvList);
        mRvList.setLayoutManager(new LinearLayoutManager(context));
        mRvList.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        mAdapter = new AddressAdapter();
        mRvList.setAdapter(mAdapter);
        // 初始化默认的本地数据  也提供了方法接收外面数据
        mRvList.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }


    /**
     * 初始化数据
     * 拿assets下的json文件
     */
    private void initData() {
        String json1="{\"area\":[{\"i\":\"01\",\"n\":\"四川省\",\"p\":\"86000000\"},{\"i\":\"02\",\"n\":\"广东省\",\"p\":\"86000000\"}]"+","+
                "\"city\":[{\"i\":\"0101\",\"n\":\"成都市\",\"p\":\"01\"},{\"i\":\"0102\",\"n\":\"绵阳市\",\"p\":\"01\"}]"+","+
                "\"district\":[{\"i\":\"010101\",\"n\":\"高新区\",\"p\":\"0101\"},{\"i\":\"010102\",\"n\":\"成华区\",\"p\":\"0101\"}]"+","+
                "\"community\":[{\"i\":\"010103\",\"n\":\"环球中心\",\"p\":\"010101\"},{\"i\":\"010104\",\"n\":\"天府广场\",\"p\":\"010101\"}]"+","+
                "\"building\":[{\"i\":\"010105\",\"n\":\"住宿楼1\",\"p\":\"010103\"},{\"i\":\"010106\",\"n\":\"住宿楼2\",\"p\":\"010103\"}]}";
//        initData(json);

      //  String json={"area":[{"i":"01","n":"四川省","p":"86000000"},{"i":"02","n":"北京市","p":"86000000"}],  "city":[{"i":"0101","n":"成都市","p":"01"},{"i":"0102","n":"绵阳市","p":"01"}],  "district":[{"i":"010101","n":"高新区","p":"0101"},{"i":"010102","n":"成华区","p":"0101"}],  "community":[{"i":"010103","n":"环球中心","p":"010101"},{"i":"010104","n":"天府广场","p":"010101"}],  "building":[{"i":"010105","n":"住宿楼1","p":"010103"},{"i":"010106","n":"住宿楼2","p":"010103"}]};

        StringBuilder jsonSB = new StringBuilder();
        try {
            BufferedReader addressJsonStream = new BufferedReader(new InputStreamReader(mContext.getAssets().open("address.json")));
            String line;
            while ((line = addressJsonStream.readLine()) != null) {
                jsonSB.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json=jsonSB.toString();
        Log.i("json",json);
        // 将数据转换为对象
        mYwpAddressBean = new Gson().fromJson(json, YwpAddressBean.class);
        if (mYwpAddressBean != null) {
            Log.i("city",mYwpAddressBean.getCommunity().size()+"");
            mRvData.clear();
            mRvData.addAll(mYwpAddressBean.getArea());
            mAdapter.notifyDataSetChanged();
        }
    }
   /* public void initData(String json) {
        Log.i("json",json);
        // 将数据转换为对象

        mYwpAddressBean = new Gson().fromJson(json, YwpAddressBean.class);





        if (mYwpAddressBean != null) {

            Log.i("city",mYwpAddressBean.getarea().size()+"");

            mRvData.clear();
            mRvData.addAll(mYwpAddressBean.getarea());
            mAdapter.notifyDataSetChanged();
        }
    }*/
    /**
     * 开放给外部传入数据
     * 暂时就用这个Bean模型，如果数据不一致就需要各自根据数据来生成这个bean了
     */
   /* public void initData(YwpAddressBean bean) {
        if (bean != null) {
            mSelectBuilding = null;
            mSelectCommunity = null;
            mSelectArea = null;
            mTabLayout.getTabAt(0).select();

            mYwpAddressBean = bean;
            mRvData.clear();
            mRvData.addAll(mYwpAddressBean.getArea());
            mAdapter.notifyDataSetChanged();

        }
    }

*/
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvSure) {
            sure();
        }
    }

    //点确定
    private void sure() {
        if (mSelectArea != null&& mSelectCommunity!=null&&mOnAddressPickerSureListener != null){
//
//                if(mSelectCommunity==null)mSelectCommunity != null &&
//                mSelectBuilding != null&&
//                mSelectUnit != null&&
//                mSelectFloor != null&&
//                mSelectHouse != null)

            if(mSelectBuilding==null){
                mOnAddressPickerSureListener.onSureClick(mSelectArea.getN() + " " + mSelectCommunity.getN(),
                        mSelectArea.getI(),  mSelectCommunity.getI(),null,null,null,null);
            }
            else if(mSelectUnit==null){
                mOnAddressPickerSureListener.onSureClick(mSelectArea.getN() + " " + mSelectCommunity.getN() + " " + mSelectBuilding.getN(),
                        mSelectArea.getI(),  mSelectCommunity.getI(),mSelectBuilding.getI(),null,null,null);
            }
            else if(mSelectFloor==null){
                mOnAddressPickerSureListener.onSureClick(mSelectArea.getN() + " " + mSelectCommunity.getN() + " " + mSelectBuilding.getN() + " " + mSelectUnit.getN(),
                        mSelectArea.getI(),  mSelectCommunity.getI(),mSelectBuilding.getI(),mSelectUnit.getI(),null,null);
            }
            else if(mSelectHouse==null){
                mOnAddressPickerSureListener.onSureClick(mSelectArea.getN() + " " + mSelectCommunity.getN() + " " + mSelectBuilding.getN() + " " + mSelectUnit.getN() + " " + mSelectFloor.getN(),
                        mSelectArea.getI(),  mSelectCommunity.getI(),mSelectBuilding.getI(),mSelectUnit.getI(), mSelectBuilding.getN(),null);
            }
            else{
                mOnAddressPickerSureListener.onSureClick(mSelectArea.getN() + " " + mSelectCommunity.getN() + " " + mSelectBuilding.getN() + " " + mSelectUnit.getN() + " " + mSelectFloor.getN() + " "+ mSelectHouse.getN() + " ",
                        mSelectArea.getI(), mSelectCommunity.getI(), mSelectBuilding.getI(), mSelectUnit.getI(), mSelectFloor.getI(),mSelectHouse.getI());
            }
        }
        else {
            Toast.makeText(mContext, "请先选择片区和小区", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mYwpAddressBean = null;
    }

    /**
     * TabLayout 切换事件
     */
    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mRvData.clear();
            switch (tab.getPosition()) {
                case 0:
                    mRvData.addAll(mYwpAddressBean.getArea());
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectAreaPosition);
                    break;
                case 1:
                    // 点到城市的时候要判断有没有选择省份
                    if (mSelectArea != null) {
                        for (YwpAddressBean.AddressItemBean itemBean : mYwpAddressBean.getCommunity()) {
                            if (itemBean.getP().equals(mSelectArea.getI()))
                                mRvData.add(itemBean);
                        }
                    } else {
                        Toast.makeText(mContext, "请您先选择片区", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectCommunityPosition);
                    break;
                case 2:
                    // 点到区的时候要判断有没有选择省份与城市
                    if (mSelectArea != null && mSelectCommunity != null) {
                        for (YwpAddressBean.AddressItemBean itemBean : mYwpAddressBean.getBuilding()) {
                            if (itemBean.getP().equals(mSelectCommunity.getI()))
                                mRvData.add(itemBean);
                        }
                    } else {
                        Toast.makeText(mContext, "请您先选择小区", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectBuildingPosition);
                    break;
                case 3:
                    // 点到区的时候要判断有没有选择省份与城市
                    if (mSelectArea != null && mSelectCommunity != null&& mSelectBuilding != null) {
                        for (YwpAddressBean.AddressItemBean itemBean : mYwpAddressBean.getUnit()) {
                            if (itemBean.getP().equals(mSelectBuilding.getI()))
                                mRvData.add(itemBean);
                        }
                    } else {
                        Toast.makeText(mContext, "请您先选择楼栋", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectUnitPosition);
                    break;
                case 4:
                    // 点到区的时候要判断有没有选择省份与城市
                    if (mSelectArea != null && mSelectCommunity != null&& mSelectBuilding != null&& mSelectUnit != null) {
                        for (YwpAddressBean.AddressItemBean itemBean : mYwpAddressBean.getFloor()) {
                            if (itemBean.getP().equals(mSelectUnit.getI()))
                                mRvData.add(itemBean);
                        }
                    } else {
                        Toast.makeText(mContext, "请您先选择单元", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectFloorPosition);
                    break;
                case 5:
                    // 点到区的时候要判断有没有选择省份与城市
                    if (mSelectArea != null && mSelectCommunity != null&& mSelectBuilding != null&& mSelectUnit != null&& mSelectFloor != null) {
                        for (YwpAddressBean.AddressItemBean itemBean : mYwpAddressBean.getHouse()) {
                            if (itemBean.getP().equals(mSelectFloor.getI()))
                                mRvData.add(itemBean);
                        }
                    } else {
                        Toast.makeText(mContext, "请您先选择楼层", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectHousePosition);
                    break;
            }


        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };


    /**
     * 下面显示数据的adapter
     */
    class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_address_text, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int tabSelectPosition = mTabLayout.getSelectedTabPosition();
            holder.mTitle.setText(mRvData.get(position).getN());
            holder.mTitle.setTextColor(defaultUnSelectedColor);
            // 设置选中效果的颜色
            switch (tabSelectPosition) {
                case 0:
                    if (mRvData.get(position) != null &&
                            mSelectArea != null &&
                            mRvData.get(position).getI().equals(mSelectArea.getI())) {
                        holder.mTitle.setTextColor(defaultUnSelectedColor);
                    }
                    break;
                case 1:
                    if (mRvData.get(position) != null &&
                            mSelectCommunity != null &&
                            mRvData.get(position).getI().equals(mSelectCommunity.getI())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 2:
                    if (mRvData.get(position) != null &&
                            mSelectBuilding != null &&
                            mRvData.get(position).getI().equals(mSelectBuilding.getI())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 3:
                    if (mRvData.get(position) != null &&
                            mSelectUnit != null &&
                            mRvData.get(position).getI().equals(mSelectUnit.getI())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 4:
                    if (mRvData.get(position) != null &&
                            mSelectFloor != null &&
                            mRvData.get(position).getI().equals(mSelectFloor.getI())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 5:
                    if (mRvData.get(position) != null &&
                            mSelectHouse != null &&
                            mRvData.get(position).getI().equals(mSelectHouse.getI())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;

            }
            // 设置点击之后的事件
            holder.mTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击 分类别
                    switch (tabSelectPosition) {
                        case 0:
                            mSelectArea = mRvData.get(position);
                            // 清空后面两个的数据
                            mSelectCommunity = null;
                            mSelectBuilding = null;
                            mSelectUnit=null;
                            mSelectFloor=null;
                            mSelectHouse=null;

                            mSelectCommunityPosition = 0;
                            mSelectBuildingPosition = 0;
                            mSelectUnitPosition = 0;
                            mSelectFloorPosition = 0;
                            mSelectHousePosition = 0;

                            mTabLayout.getTabAt(1).setText(defaultCommunity);
                            mTabLayout.getTabAt(2).setText(defaultBuilding);
                            mTabLayout.getTabAt(3).setText(defaultUnit);
                            mTabLayout.getTabAt(4).setText(defaultFloor);
                            mTabLayout.getTabAt(5).setText(defaultHouse);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(0).setText(mSelectArea.getN());
                            // 跳到下一个选择
                            mTabLayout.getTabAt(1).select();
                            // 灰掉确定按钮
                            mTvSure.setTextColor(defaultSureUnClickColor);
                            mSelectAreaPosition = position;
                            break;
                        case 1:
                            mSelectCommunity = mRvData.get(position);
                            // 清空后面一个的数据
                            mSelectBuilding = null;
                            mSelectUnit=null;
                            mSelectFloor=null;
                            mSelectHouse=null;

                            mSelectBuildingPosition = 0;
                            mSelectUnitPosition = 0;
                            mSelectFloorPosition = 0;
                            mSelectHousePosition = 0;

                            mTabLayout.getTabAt(2).setText(defaultBuilding);
                            mTabLayout.getTabAt(3).setText(defaultUnit);
                            mTabLayout.getTabAt(4).setText(defaultFloor);
                            mTabLayout.getTabAt(5).setText(defaultHouse);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(1).setText(mSelectCommunity.getN());
                            // 跳到下一个选择
                            mTabLayout.getTabAt(2).select();
                            // 灰掉确定按钮
                            mTvSure.setTextColor(defaultSureCanClickColor);
                            mSelectCommunityPosition = position;
                            break;
                        case 2:
                            mSelectBuilding = mRvData.get(position);
                            // 清空后面一个的数据

                            mSelectUnit=null;
                            mSelectFloor=null;
                            mSelectHouse=null;

                            mSelectUnitPosition = 0;
                            mSelectFloorPosition = 0;
                            mSelectHousePosition=0;

                            mTabLayout.getTabAt(3).setText(defaultUnit);
                            mTabLayout.getTabAt(4).setText(defaultFloor);
                            mTabLayout.getTabAt(5).setText(defaultHouse);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(2).setText(mSelectBuilding.getN());
                            // 跳到下一个选择
                            mTabLayout.getTabAt(3).select();
                            // 灰掉确定按钮
                            mTvSure.setTextColor(defaultSureCanClickColor);
                            mSelectBuildingPosition = position;
                            break;
                        case 3:
                            mSelectUnit = mRvData.get(position);
                            // 清空后面一个的数据
                            mSelectFloor=null;
                            mSelectHouse=null;

                            mSelectFloorPosition = 0;
                            mSelectHousePosition = 0;

                            mTabLayout.getTabAt(4).setText(defaultFloor);
                            mTabLayout.getTabAt(5).setText(defaultHouse);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(3).setText(mSelectUnit.getN());
                            // 跳到下一个选择
                            mTabLayout.getTabAt(4).select();
                            // 灰掉确定按钮
                            mTvSure.setTextColor(defaultSureCanClickColor);
                            mSelectUnitPosition= position;
                            break;
                        case 4:
                            mSelectFloor = mRvData.get(position);
                            // 清空后面一个的数据
                            mSelectHouse=null;

                            mSelectHousePosition = 0;

                            mTabLayout.getTabAt(5).setText(defaultHouse);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(4).setText(mSelectFloor.getN());
                            // 跳到下一个选择
                            mTabLayout.getTabAt(5).select();
                            // 灰掉确定按钮
                            mTvSure.setTextColor(defaultSureCanClickColor);
                            mSelectFloorPosition= position;
                            break;


                        case 5:
                            mSelectHouse = mRvData.get(position);
                            // 没了，选完了，这个时候可以点确定了
                            mTabLayout.getTabAt(5).setText(mSelectHouse.getN());
                            notifyDataSetChanged();
                            // 确定按钮变亮
                            mTvSure.setTextColor(defaultSureCanClickColor);
                            mSelectHousePosition = position;
                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRvData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTitle;

            ViewHolder(View itemView) {
                super(itemView);
                mTitle =itemView.findViewById(R.id.itemTvTitle);
            }

        }
    }


    /**
     * 点确定回调这个接口
     */
    public interface OnAddressPickerSureListener {
        void onSureClick(String address, String areaCode, String communityCode, String buildingCode,String unitCode,String floorCode,String houseCode);
    }

    public void setOnAddressPickerSure(OnAddressPickerSureListener listener) {
        this.mOnAddressPickerSureListener = listener;
    }


}
