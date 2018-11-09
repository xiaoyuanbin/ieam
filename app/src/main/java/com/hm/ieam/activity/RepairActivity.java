package com.hm.ieam.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hm.ieam.adapter.MyGridViewAdapter;
import com.hm.ieam.adapter.MyRepAdapter;
import com.hm.ieam.adapter.MyRepGridAdapter;
import com.hm.ieam.bean.RepairBean;
import com.hm.ieam.utils.Contans;
import com.hm.ieam.adapter.MyReportAdapter;
import com.hm.ieam.utils.JsonUtils;
import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.utils.MyLoadDialog;
import com.hm.ieam.utils.PhotoUtils;
import com.hm.ieam.utils.PopWindowUtils;
import com.hm.ieam.R;
import com.hm.ieam.widght.XListView;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class RepairActivity extends AppCompatActivity implements View.OnClickListener {

    List<RepairBean> wait_repair;    //待维修数据集合
    List<RepairBean> repaired;    //已维修数据集合
    List<RepairBean> repairList;    //已维修数据集合

    String p_state;     //数据的维修状态
    private SharedPreferences sp;
    MyLoadDialog myLoadDialog;
    HashMap<String, ArrayList<HashMap<String, String>>> repairMap;
    int all_state=0;
    int wait_state=all_state+1;
    int repaired_state=wait_state+1;
    int state=all_state;

    int imageId;    //编辑图片时的id
    private List<String> mDatas;    //存放所有图片的集合
    MyRepGridAdapter adapter;      //显示图片的adapter

    PhotoUtils photoUtils;   //拍照工具类
    Button repair_btn_back;
    XListView xlistView;
    TextView repair_tv_empty;

    int repair=1;                   //获取网络数据的页数
    boolean flag=false;
    int reportId;
    Button repair_btn_menu;

    MyRepAdapter myRepAdapter;

    PopWindowUtils popWindowUtils;
    PopupWindow detailPopWindow;           //查看维修详情
    PopupWindow menuPopWindow;

    MyGlideUtils myGlideUtils;
    PopupWindow imgPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);

        popWindowUtils=new PopWindowUtils(this);
        myLoadDialog=new MyLoadDialog(this);
        initDate();
        initView();
        photoUtils=new PhotoUtils(this);
        myGlideUtils=new MyGlideUtils();
    }

    private void initDate() {

        sp= getSharedPreferences("date",MODE_PRIVATE);
        wait_repair=new ArrayList<>();
        repaired=new ArrayList<>();
        mDatas=new ArrayList<>();
        repairList=new ArrayList<>();




        getRepairedDate(repair);

    }

    private void initView() {
        repair_btn_back=findViewById(R.id.repair_btn_back);
        repair_btn_back.setOnClickListener(this);
        repair_btn_menu=findViewById(R.id.repair_btn_menu);
        repair_btn_menu.setOnClickListener(this);

        xlistView=findViewById(R.id.repair_xlv);
        repair_tv_empty=findViewById(R.id.repair_tv_empty);


        myRepAdapter=new MyRepAdapter(this,repairList);
        xlistView.setAdapter(myRepAdapter);
        xlistView.setEmptyView(repair_tv_empty);


        xlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
                reportId=postion-1;
                Log.i("position",postion+"");
                initDetailPop();


            }
        });
        xlistView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                repairList.clear();
                flag = false;
                repair = 1;

                getRepairedDate(repair);
                xlistView.stopRefresh();

            }

            @Override
            public void onLoadMore() {

                flag=true;
                repair++;
                getRepairedDate(repair);
                xlistView.stopLoadMore();

            }
        });



    }


    //查看维修的详情
    private void initDetailPop() {
        mDatas.removeAll(mDatas);

        String images=repairList.get(reportId).getImages();

        if(!"".equals(images)){
            String[] img=images.split(",");
            for(String s:img){
                mDatas.add(s);
            }
        }

        final View view= LayoutInflater.from(this).inflate(R.layout.report_repair_layout,null,false);

        detailPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(detailPopWindow);

        GridView report_repair_gridview=view.findViewById(R.id.report_repair_gridview);


        Button report_repair_btn_back=view.findViewById(R.id.report_repair_btn_back);
        TextView report_repair_tv_address=view.findViewById(R.id.report_repair_tv_address);
        TextView report_repair_tv_firm=view.findViewById(R.id.report_repair_tv_firm);
        TextView report_repair_tv_type=view.findViewById(R.id.report_repair_tv_type);
        TextView report_repair_tv_time=view.findViewById(R.id.report_repair_tv_time);
        TextView report_repair_tv_position=view.findViewById(R.id.report_repair_tv_position);
        LinearLayout repair_report_ll_write=view.findViewById(R.id.repair_report_ll_write);
        TextView repair_report_tv_write=view.findViewById(R.id.repair_report_tv_write);

        report_repair_btn_back.setOnClickListener(this);
        repair_report_tv_write.setOnClickListener(this);

        report_repair_tv_address.setSingleLine(true);//设置单行显示
        report_repair_tv_address.setHorizontallyScrolling(true);//设置水平滚动效

        Log.i("mDatas.size()",mDatas.size()+"");

        report_repair_tv_address.setText(repairList.get(reportId).getReport_address());
        report_repair_tv_firm.setText(repairList.get(reportId).getReport_firm());
        report_repair_tv_type.setText(repairList.get(reportId).getReport_type());
        report_repair_tv_time.setText(repairList.get(reportId).getReport_date());
        report_repair_tv_position.setText(repairList.get(reportId).getReport_position());

        if(repairList.get(reportId).getRepair_state().equals("维修完成")){
            repair_report_ll_write.setVisibility(view.GONE);
        }
        else{
            repair_report_ll_write.setVisibility(view.VISIBLE);
        }

        adapter=new MyRepGridAdapter(this,mDatas);
        report_repair_gridview.setAdapter(adapter);
        report_repair_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position", position + "");
                Log.i("mDatas.size()", mDatas.size() + "");
                imageId = position;

                startEditImage(mDatas.get(position));

            }

        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repair_btn_back:

                finish();
                break;
            case R.id.repair_btn_menu:
                showMenuPop();
                break;
            case R.id.report_repair_btn_back:
                popWindowUtils.dissPopupWindow(detailPopWindow);
                break;

            case R.id.repair_report_tv_write:
                startWrite();
               // popWindowUtils.dissPopupWindow(detailPopWindow);

                break;

            default:
                break;
        }
    }



    private void startEditImage(String imgPath) {

        View imgView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_image_item, null, false);
        imgPopWindow=new PopupWindow(imgView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(imgPopWindow);


        Button pop_inspect_image_back=imgView.findViewById(R.id.pop_inspect_image_back);
        ImageView pop_inspect_image=imgView.findViewById(R.id.pop_inspect_image);
        Button pop_inspect_image_delete=imgView.findViewById(R.id.pop_inspect_image_delete);
        pop_inspect_image_delete.setVisibility(View.INVISIBLE);

        Glide.with(this).load(imgPath).into(pop_inspect_image);



        pop_inspect_image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(imgPopWindow);

            }
        });




    }

    private void showMenuPop() {
        View menuView = LayoutInflater.from(this).inflate(R.layout.repair_menu,null,false);
        menuPopWindow=new PopupWindow(menuView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        menuPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuPopWindow.showAsDropDown(repair_btn_menu, 0, 10);

        LinearLayout repair_menu_ll_all=menuView.findViewById(R.id.repair_menu_ll_all);
        LinearLayout repair_menu_ll_complete=menuView.findViewById(R.id.repair_menu_ll_complete);
        LinearLayout repair_menu_ll_wait=menuView.findViewById(R.id.repair_menu_ll_wait);

        repair_menu_ll_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RepairActivity.this,"选择全部",Toast.LENGTH_SHORT).show();
                state=all_state;
                repair=1;
                repairList.clear();
                myRepAdapter.clearAll();
                getRepairedDate(repair);

                menuPopWindow.dismiss();
            }
        });


        repair_menu_ll_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RepairActivity.this,"选择已维修",Toast.LENGTH_SHORT).show();
                state=repaired_state;
                repairList.clear();
                myRepAdapter.clearAll();
                repair=1;
                getRepairedDate(repair);
                menuPopWindow.dismiss();

            }
        });
        repair_menu_ll_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RepairActivity.this,"选择待维修",Toast.LENGTH_SHORT).show();
                state=wait_state;
                repair=1;
                repairList.clear();
                myRepAdapter.clearAll();
                getRepairedDate(repair);
                menuPopWindow.dismiss();

            }
        });


    }

    private void startWrite() {
        Intent intent=new Intent(this,RepairWriteActivity.class);
        intent.putExtra("RepairBean", repairList.get(reportId));
        startActivity(intent);

    }

    public void getRepairedDate(int index) {


     //   repairList=new ArrayList<>();
        if(state==all_state){
            p_state="";
        }
        else if(state==wait_state){
            p_state="报修";
        }
        else {
            p_state="维修";
        }

        myLoadDialog.showLoading("获取数据","正在获取数据，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_repair_list");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("depid",sp.getString("cu_depid",""));
        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("id",sp.getString("cu_compid",""));
        params.addBodyParameter("pageindex",index+"");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");
        params.addBodyParameter("state",p_state);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("获取数据成功：",result);
                int count=1;
                myLoadDialog.hideLoading();

                if(!result.equals("")) {
                    repairMap = JsonUtils.stringToJson(result);
                }

                for(String ds:repairMap.keySet()){

                    ArrayList<HashMap<String, String>> list=repairMap.get(ds);
                    if(list.size()>0){

                        for(HashMap<String, String> dsMap:list){

                            RepairBean repairBean=new RepairBean();

                            //取出想要的数据
                            for(String key:dsMap.keySet()) {
                                if(key.equals("r_id")) {

                                    repairBean.setId(dsMap.get(key));
                                }
                                if(key.equals("images")) {

                                    repairBean.setImages(dsMap.get(key));
                                }
                                if(key.equals("r_date")) {

                                    repairBean.setReport_date(dsMap.get(key));
                                }
                                if(key.equals("r_tbname")) {

                                    repairBean.setTbname(dsMap.get(key));
                                }
                                if(key.equals("r_state")) {

                                    repairBean.setRepair_state(dsMap.get(key));
                                }
                                if(key.equals("r_companyid")) {

                                    repairBean.setReport_firm(dsMap.get(key));

                                }
                                if(key.equals("r_type")) {

                                    repairBean.setReport_type(dsMap.get(key));
                                }
                                if(key.equals("r_position")) {

                                    repairBean.setRepair_address(dsMap.get(key));
                                }
                                if(key.equals("r_part")) {

                                    repairBean.setReport_position(dsMap.get(key));
                                }
                                if(key.equals("r_addr")) {

                                    repairBean.setReport_address(dsMap.get(key));
                                }
                                if(key.equals("r_rptype")) {

                                    repairBean.setRepair_type(dsMap.get(key));
                                }
                                if(key.equals("r_laborbudget")) {

                                    repairBean.setRepair_laborbudget(dsMap.get(key));
                                }
                                if(key.equals("r_stuffbudget")) {

                                    repairBean.setRepair_stuffbudget(dsMap.get(key));
                                }
                                if(key.equals("r_costbudget")) {

                                    repairBean.setRepair_costbudget(dsMap.get(key));

                                }
                                if(key.equals("r_faultdispos")) {

                                    repairBean.setRepair_deal(dsMap.get(key));
                                }
                                if(key.equals("r_reamrk")) {

                                    repairBean.setRepair_remark(dsMap.get(key));
                                }

                            }

                            repairList.add(repairBean);

                            count++;
                        }

                    }
                }

                if(count<20){
                    xlistView.setPullLoadEnable(false);
                }
                else{
                    xlistView.setPullLoadEnable(true);
                }

             //   notifyDataSetChanged();
                if(myRepAdapter!=null) {
                    myRepAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(RepairActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();


                } else { // 其他错误
                    Toast.makeText(RepairActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }
       //         notifyDataSetChanged();
                if(myRepAdapter!=null) {

                    myRepAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    //更新gridview
    private void notifyDataSetChanged() {
        myRepAdapter=new MyRepAdapter(RepairActivity.this, repairList);
        xlistView.setAdapter(myRepAdapter);
        myRepAdapter.notifyDataSetChanged();


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myGlideUtils.clearImageAllCache(RepairActivity.this);
    }


}
