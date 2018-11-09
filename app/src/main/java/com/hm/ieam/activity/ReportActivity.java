package com.hm.ieam.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.ieam.MyApplication;
import com.hm.ieam.adapter.MyAddressAdapter;
import com.hm.ieam.adapter.MyExpandableAdapter;

import com.hm.ieam.bean.AddressBean;
import com.hm.ieam.bean.RepairBean;

import com.hm.ieam.bean.ReportType;
import com.hm.ieam.utils.Contans;
import com.hm.ieam.utils.JsonUtils;
import com.hm.ieam.utils.LocationUtils;
import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.utils.MyLoadDialog;
import com.hm.ieam.utils.MyOkhttpClient;
import com.hm.ieam.adapter.MyGridViewAdapter;
import com.hm.ieam.utils.PhotoUtils;
import com.hm.ieam.utils.PopWindowUtils;
import com.hm.ieam.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> mDatas;    //存放所有图片的集合
 //   private List<String> imgs;    //上传的所有图片的集合
    String[] mPermissions=new String[]{Manifest.permission.CAMERA
            ,Manifest.permission.READ_PHONE_STATE
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    GridView report_gridview;
    MyGridViewAdapter myAdapter;      //显示图片的adapter

    MyGlideUtils myGlideUtils;
    private static final int CAREMA = 2;   //调用相机请求码
    public static final int REQUEST_CODE_CHOOSE=CAREMA+1;    //上传图片
    SharedPreferences sp;
    SharedPreferences datasp;
    SharedPreferences.Editor editor;
    ArrayMap<String,String> firmMap;
    ArrayMap<String,String> typeMap;

    PopWindowUtils popWindowUtils;
    PhotoUtils photoUtils;   //拍照工具类
    Uri imageUri;    //拍照时获取图片uri
    int imageId;    //编辑图片时的id
  //  private List<String>  imageDescription;    //图片编辑文字信息

    HashMap<String, ArrayList<HashMap<String, String>>> jsonMap; //存放解析json数据的结果
 //   List<AddressBean.AddressItemBean> areaList;
    List<AddressBean> communityList;
    List<AddressBean> buildingList;
    List<AddressBean> unitList;
    List<AddressBean> floorList;
    List<AddressBean> houseList;
    ListView addresslistview;
    MyAddressAdapter myAddressAdapter;
    int count=1;   //计算地址选择层数
    List<Integer> positions;

    MyLoadDialog myLoadDialog;

    int choiceId=-1;    //报修公司选择
    List<String> firmList;
    ArrayAdapter listAdapter;

    int choiceType=-1;    //报修公司选择
    List<ReportType> type_group;
    List<List<ReportType>> type_child;
    List<ReportType> type_child_tujian;
    List<ReportType> type_child_anzhuang;
    List<ReportType> type_child_xf;
    List<ReportType> type_child_dt;
    List<ReportType> type_child_rd;
    List<ReportType> type_child_qd;
    List<ReportType> type_child_sb;

  //  String[] type_group={"土建工程","安装工程","消防工程","电梯工程","弱电工程","强电工程"};
    ExpandableListView typeListview;
    MyExpandableAdapter myExpandableAdapter;
    MyOkhttpClient myOkhttpClient;
    LinearLayout report_ll_address;

    LinearLayout report_ll_firm;
    LinearLayout report_ll_type;
    LinearLayout report_ll_position;
 //   LinearLayout report_ll_time;
    LinearLayout report_ll_user;
    LinearLayout report_ll_phone;
    LinearLayout report_ll_opinion;


    private PopupWindow addressPopWindow;    //报修地址

    private PopupWindow firmPopWindow;    //报修单位
    private PopupWindow typePopWindow;    //报修类型
    private PopupWindow positionPopWindow;    //报修部位
    private PopupWindow imgPopWindow;    //报修部位
    private PopupWindow userPopWindow;    //报修部位
    private PopupWindow phonePopWindow;    //报修部位
    private PopupWindow opinionPopWindow;    //报修部位


    Button report_back;        //巡查页面返回按钮
    Button report_submit;       //提交按钮

    TextView report_address;
    TextView report_firm;
    TextView report_type;
    TextView report_position;
    TextView report_time;
    TextView report_user;
    TextView report_phone;
    TextView report_opinion;


//    TextView address_select_tv_area;
    TextView address_select_tv_community;
    TextView address_select_tv_building;
    TextView address_select_tv_unit;
    TextView address_select_tv_floor;
    TextView address_select_tv_house;


    ImageView address_select_img_go;
    ImageView address_select_img_go2;
    ImageView address_select_img_go3;
    ImageView address_select_img_go4;

    Gson gson;
    double lon1;  //当前位置的经度
    double lat1;   //当前位置的纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initDate();
        initView();
        initPopWindow();

    }



    private void initDate() {

        sp=getSharedPreferences("report",MODE_PRIVATE);
        datasp=getSharedPreferences("date",MODE_PRIVATE);
        gson = new Gson();
        photoUtils=new PhotoUtils(this);
        myOkhttpClient=new MyOkhttpClient(this);
        myLoadDialog=new MyLoadDialog(this);
        myGlideUtils=new MyGlideUtils();


   //     imageDescription=new ArrayList<>();
   //     areaList=new ArrayList<>();
        communityList=new ArrayList<>();
        buildingList=new ArrayList<>();
        unitList=new ArrayList<>();
        floorList=new ArrayList<>();
        houseList=new ArrayList<>();
        positions=new ArrayList<>();
        firmList=new ArrayList<>();
        firmMap=new ArrayMap<>();


        type_group=new ArrayList<>();
        type_child=new ArrayList<>();
        type_child_xf=new ArrayList<>();
        type_child_dt=new ArrayList<>();
        type_child_rd=new ArrayList<>();
        type_child_qd=new ArrayList<>();
        type_child_sb=new ArrayList<>();

        type_child_anzhuang=new ArrayList<>();
        type_child_tujian=new ArrayList<>();
        typeMap=new ArrayMap<>();


//        for(int i=0;i<20;i++){
//            imageDescription.add("");
//        }
        mDatas=new ArrayList<>();
        lon1= MyApplication.lon;
        lat1=MyApplication.lat;

        if(sp.getInt("communityListid",-1)>-1){
            positions.add(sp.getInt("communityListid",0));
        }
        if(sp.getInt("buildingListid",-1)>-1){
            positions.add(sp.getInt("buildingListid",0));
        }
        if(sp.getInt("unitListid",-1)>-1){
            positions.add(sp.getInt("unitListid",0));
        }
        if(sp.getInt("floorListid",-1)>-1){
            positions.add(sp.getInt("floorListid",0));
        }
        if(sp.getInt("houseListid",-1)>-1){
            positions.add(sp.getInt("houseListid",0));
        }
        initAddressData();



    }

    public void initAddressData(){
        Type type = new TypeToken<List<AddressBean>>(){}.getType();
        if(sp.getString("communityList", null)!=null){
            String community=sp.getString("communityList", null);
            communityList= gson.fromJson(community, type);
        }
        if(sp.getString("buildingList", null)!=null){
            String building=sp.getString("buildingList", null);
            buildingList= gson.fromJson(building, type);
        }
        if(sp.getString("unitList", null)!=null){
            String unit=sp.getString("unitList", null);
            unitList= gson.fromJson(unit, type);
        }
        if(sp.getString("floorList", null)!=null){
            String floor=sp.getString("floorList", null);
            floorList= gson.fromJson(floor, type);
        }
        if(sp.getString("houseList", null)!=null){
            String house=sp.getString("houseList", null);
            houseList= gson.fromJson(house, type);
        }
        Log.i("地址集合大小",communityList.size()+"/"+buildingList.size()+"/"+unitList.size()+"/"+floorList.size()
                +"/"+houseList.size());
    }


    private void initView() {
        report_gridview=findViewById(R.id.report_gridview);

        report_ll_address=findViewById(R.id.report_ll_address);

        report_ll_firm=findViewById(R.id.report_ll_firm);
    //    report_ll_time=findViewById(R.id.report_ll_time);

        report_ll_type=findViewById(R.id.report_ll_type);
        report_ll_position=findViewById(R.id.report_ll_position);
        report_ll_user=findViewById(R.id.report_ll_user);
        report_ll_phone=findViewById(R.id.report_ll_phone);
        report_ll_opinion=findViewById(R.id.report_ll_opinion);

        report_back=findViewById(R.id.report_back);
        report_submit=findViewById(R.id.report_submit);
        report_address=findViewById(R.id.report_address);
        report_firm=findViewById(R.id.report_firm);
        report_type=findViewById(R.id.report_type);
        report_position=findViewById(R.id.report_position);
        report_time=findViewById(R.id.report_time);
        report_user=findViewById(R.id.report_user);
        report_phone=findViewById(R.id.report_phone);
        report_opinion=findViewById(R.id.report_opinion);


        report_address.setText(sp.getString("r_addr",""));
        report_firm.setText(sp.getString("r_companyname","金信源公司"));
        report_type.setText(sp.getString("r_type","设备报修"));
        report_position.setText(sp.getString("r_part",""));
        report_user.setText(sp.getString("r_usersign",""));
        report_phone.setText(sp.getString("r_userphone",""));
        report_opinion.setText(sp.getString("r_propertyview",""));

        setDate();



        report_back.setOnClickListener(this);
        report_submit.setOnClickListener(this);
        report_ll_address.setOnClickListener(this);

        report_ll_firm.setOnClickListener(this);
   //     report_ll_time.setOnClickListener(this);
        report_ll_type.setOnClickListener(this);
        report_ll_position.setOnClickListener(this);
        report_ll_user.setOnClickListener(this);
        report_ll_phone.setOnClickListener(this);
        report_ll_opinion.setOnClickListener(this);

        Log.i("reportactivity",mDatas.size()+"");

        myAdapter=new MyGridViewAdapter(this,mDatas);
        report_gridview.setAdapter(myAdapter);
        report_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("position", position + "");
                imageId = position;

                if (position == mDatas.size()||mDatas.size()==0) {

                    applyPermission(CAREMA);

                } else {
                    startEditImage(mDatas.get(position));

                }
            }

        });
    }

    private void showListDialog() {
        final String[] items = { "拍照","从相册选择"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(ReportActivity.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    startCarema();
                }else{
                    upPhoto();
                }
            }
        });
        listDialog.show();
    }

    //拍照时动态申请权限
    public void applyPermission(int requestCode) {
        if (EasyPermissions.hasPermissions(ReportActivity.this, mPermissions)){
            showListDialog();
        }else{
//第二个参数是提示信息
            EasyPermissions.requestPermissions(ReportActivity.this,"请给予照相机权限,否则app无法正常运行",requestCode,mPermissions);
        }


    }
    //调用相机拍照
    public void startCarema() {
        imageUri=photoUtils.getPhotoUri();
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAREMA);
    }

    private void upPhoto() {
        Matisse.from(ReportActivity.this)
                .choose(MimeType.ofAll())//ofAll()
                .theme(R.style.Matisse_Zhihu)//主题，夜间模式R.style.Matisse_Dracula
                .countable(true)//是否显示选中数字
                .capture(false)//是否提供拍照功能
                .captureStrategy(new CaptureStrategy(true, "com.hm.ieam.provider"))//存储地址
                .maxSelectable(4-mDatas.size())//最大选择数
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))//筛选条件
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//屏幕方向
                .thumbnailScale(0.85f)//缩放比例
                .imageEngine(myGlideUtils)//图片加载方式
                .forResult(REQUEST_CODE_CHOOSE);//请求码

    }

    //开始编辑图片

    private void startEditImage(String imgPath) {
   //     Log.i("imageDescription","开始编辑");
        View imgView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_image_item, null, false);
        imgPopWindow=new PopupWindow(imgView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        popWindowUtils.showfullPopupWindow(imgPopWindow);

        Button pop_inspect_image_back=imgView.findViewById(R.id.pop_inspect_image_back);
        ImageView pop_inspect_image=imgView.findViewById(R.id.pop_inspect_image);
        Button pop_inspect_image_delete=imgView.findViewById(R.id.pop_inspect_image_delete);

   //     pop_inspect_image_text=imgView.findViewById(R.id.pop_inspect_image_edittext);

        Glide.with(this).load(imgPath).into(pop_inspect_image);

   //     Log.i("开始编辑imageDescription",imageDescription.get(imageId));
   //     pop_inspect_image_text.setText(imageDescription.get(imageId));



        pop_inspect_image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(imgPopWindow);
         //       imageDescription.set(imageId,pop_inspect_image_text.getText().toString().trim());
            }
        });
        pop_inspect_image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(imgPopWindow);
                mDatas.remove(mDatas.get(imageId));

                notifyDataSetChanged();

            }
        });
//        pop_inspect_image_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                initEditImgPop();
//            }
//        });





    }

//    private void initEditImgPop() {
//        View editView= LayoutInflater.from(this).inflate(R.layout.pop_edit_img, null, false);
//        final PopupWindow editPopWindow=new PopupWindow(editView, ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT, true);
//        popWindowUtils.showfullPopupWindow(editPopWindow);
//        Button pop_edit_img_btn_back=editView.findViewById(R.id.pop_edit_img_btn_back);
//        Button pop_edit_img_btn_save=editView.findViewById(R.id.pop_edit_img_btn_save);
//        final EditText pop_edit_img_edit=editView.findViewById(R.id.pop_edit_img_edit);
//
//        pop_edit_img_edit.setText(pop_inspect_image_text.getText());
//        pop_edit_img_btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popWindowUtils.dissPopupWindow(editPopWindow);
//            }
//        });
//        pop_edit_img_btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popWindowUtils.dissPopupWindow(editPopWindow);
//                imageDescription.set(imageId,pop_edit_img_edit.getText().toString().trim());
//                pop_inspect_image_text.setText(pop_edit_img_edit.getText().toString().trim());
//            }
//        });
//
//
//
//    }



    private void initPopWindow() {
        popWindowUtils=new PopWindowUtils(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.report_back:
                finish();
                break;
            case R.id.report_submit:
                String billid =sp.getString("billid","");
                String tbname =sp.getString("tbname","");

//                if(address_select_tv_area!=null){
//                    if(address_select_tv_area.getText().equals("")||address_select_tv_area.getText().equals("片区")||address_select_tv_area.getText().equals("位置类型")){
//
//                        Log.i("area","片区");
//
//                    }
                if(address_select_tv_community!=null){
                    if((address_select_tv_community.getText().equals("")||address_select_tv_community.getText().equals("小区"))||address_select_tv_community.getText().equals("位置类型")){
//                        Log.i("area","小区");
//                        billid=areaList.get(positions.get(0)).getI();
//                        tbname="mps_area";
                    }
                    else if((address_select_tv_building.getText().equals("")||address_select_tv_building.getText().equals("楼栋"))&&positions.size()>0){
                      //  Log.i("area","楼栋");
                        billid=communityList.get(positions.get(0)).getI();
                        tbname="mps_uptown";
                    }
                    else if((address_select_tv_unit.getText().equals("")||address_select_tv_unit.getText().equals("单元"))&&positions.size()>1){
                        billid=buildingList.get(positions.get(1)).getI();
                        tbname="mps_building";
                    }
                    else if((address_select_tv_floor.getText().equals("")||address_select_tv_floor.getText().equals("楼层"))&&positions.size()>2){
                        billid=unitList.get(positions.get(2)).getI();
                        tbname="mps_unit";
                    }
                    else if((address_select_tv_house.getText().equals("")||address_select_tv_house.getText().equals("房屋"))&&positions.size()>3){
                        billid=floorList.get(positions.get(3)).getI();
                        tbname="mps_floor";
                    }
                    else if(positions.size()>4){
                        billid=houseList.get(positions.get(4)).getI();
                        tbname="mps_room";
                    }
                }
                if(report_address.getText().equals("")){
                    Toast.makeText(ReportActivity.this,"报修地点不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("billid",billid);
                    Log.i("tbname",tbname);
                    startUpload(billid,tbname);
                }

                break;
            case R.id.report_ll_address:

                initAddressPop();
                popWindowUtils.showfullPopupWindow(addressPopWindow);
                break;

            case R.id.report_ll_firm:
                if(firmList==null||firmList.size()==0){

                    getReportFirm();
                }
                else{
                    initFirmPop(firmList);
                }

                break;
            case R.id.report_ll_type:

                if(type_group==null||type_group.size()==0){
                    getReportType();
                }
                initTypePop();
                popWindowUtils.showfullPopupWindow(typePopWindow);
                break;
            case R.id.report_ll_position:
                initPositionPop();
                popWindowUtils.showfullPopupWindow(positionPopWindow);
                break;
//            case R.id.report_ll_time:
//                setDate();
//                break;
            case R.id.report_ll_user:
                initUserPop();
                popWindowUtils.showfullPopupWindow(userPopWindow);
                break;
            case R.id.report_ll_phone:
                iniPhonePop();
                popWindowUtils.showfullPopupWindow(phonePopWindow);
                break;
            case R.id.report_ll_opinion:
                initOpinionPop();
                popWindowUtils.showfullPopupWindow(opinionPopWindow);
                break;

                default:
                    break;
        }
    }

    //报修用户
    private void initUserPop() {
        View userView=LayoutInflater.from(this).inflate(R.layout.pop_report_user,null,false);
        userPopWindow=new PopupWindow(userView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_report_user_btn_back=userView.findViewById(R.id.pop_report_user_btn_back);
        Button pop_report_user_btn_save=userView.findViewById(R.id.pop_report_user_btn_save);
        final EditText pop_report_user_edit=userView.findViewById(R.id.pop_report_user_edit);

        pop_report_user_edit.setText(report_user.getText().toString());
        pop_report_user_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(userPopWindow);
            }
        });
        pop_report_user_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(userPopWindow);
                report_user.setText(pop_report_user_edit.getText().toString().trim());
            }
        });
    }
    //用户电话
    private void iniPhonePop() {
        View phoneView=LayoutInflater.from(this).inflate(R.layout.pop_report_phone,null,false);
        phonePopWindow=new PopupWindow(phoneView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_report_phone_btn_back=phoneView.findViewById(R.id.pop_report_phone_btn_back);
        Button pop_report_phone_btn_save=phoneView.findViewById(R.id.pop_report_phone_btn_save);
        final EditText pop_report_phone_edit=phoneView.findViewById(R.id.pop_report_phone_edit);

        pop_report_phone_edit.setText(report_phone.getText().toString());
        pop_report_phone_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(phonePopWindow);
            }
        });
        pop_report_phone_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(phonePopWindow);
                report_phone.setText(pop_report_phone_edit.getText().toString().trim());
            }
        });
    }
    //物业单位意见
    private void initOpinionPop() {
        View opinionView=LayoutInflater.from(this).inflate(R.layout.pop_report_opinion,null,false);
        opinionPopWindow=new PopupWindow(opinionView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_report_opinion_btn_back=opinionView.findViewById(R.id.pop_report_opinion_btn_back);
        Button pop_report_opinion_btn_save=opinionView.findViewById(R.id.pop_report_opinion_btn_save);
        final EditText pop_report_opinion_edit=opinionView.findViewById(R.id.pop_report_opinion_edit);

        pop_report_opinion_edit.setText(report_opinion.getText().toString());
        pop_report_opinion_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(opinionPopWindow);
            }
        });
        pop_report_opinion_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(opinionPopWindow);
                report_opinion.setText(pop_report_opinion_edit.getText().toString().trim());
            }
        });
    }


    //提交
    private void startUpload(final String billid, final String tbname) {
        editor=sp.edit();
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ReportActivity.this);
        normalDialog.setTitle("温馨提示");
        normalDialog.setMessage("确认提交吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                        editor.putString("r_addr", report_address.getText().toString());
                        editor.putString("r_part",report_position.getText().toString());
                        editor.putString("r_type", report_type.getText().toString());
                        editor.putString("r_usersign", report_user.getText().toString());
                        editor.putString("r_userphone", report_phone.getText().toString());
                        editor.putString("r_propertyview", report_opinion.getText().toString());
                        editor.putString("r_companyname", report_firm.getText().toString());

                        editor.putString("billid", billid);
                        editor.putString("tbname", tbname);
                        if(positions!=null&&positions.size()>0){
                            if(positions.size()==1){
                                String community= gson.toJson(communityList);
                                Log.d("InspectActivity", "saved json is "+ community);
                                editor.putString("communityList", community);
                                editor.putInt("communityListid", positions.get(0));
                                editor.remove("buildingListid");
                                editor.remove("unitListid");
                                editor.remove("floorListid");
                                editor.remove("houseListid");
                            }
                            if(positions.size()==2){
                                String community= gson.toJson(communityList);
                                String building = gson.toJson(buildingList);
                                Log.d("InspectActivity", "saved json is "+ building);
                                editor.putString("communityList", community);
                                editor.putString("buildingList", building);
                                editor.putInt("communityListid", positions.get(0));
                                editor.putInt("buildingListid", positions.get(1));
                                editor.remove("unitListid");
                                editor.remove("floorListid");
                                editor.remove("houseListid");

                            }
                            if(positions.size()==3){
                                String community= gson.toJson(communityList);
                                String building = gson.toJson(buildingList);
                                String unit = gson.toJson(unitList);
                                Log.d("InspectActivity", "saved json is "+ unit);
                                editor.putString("communityList", community);
                                editor.putString("buildingList", building);
                                editor.putString("unitList", unit);
                                editor.putInt("communityListid", positions.get(0));
                                editor.putInt("buildingListid", positions.get(1));
                                editor.putInt("unitListid", positions.get(2));
                                editor.remove("floorListid");
                                editor.remove("houseListid");
                            }
                            if(positions.size()==4){
                                String community= gson.toJson(communityList);
                                String building = gson.toJson(buildingList);
                                String unit = gson.toJson(unitList);
                                String floor = gson.toJson(floorList);
                                Log.d("InspectActivity", "saved json is "+ floor);
                                editor.putString("communityList", community);
                                editor.putString("buildingList", building);
                                editor.putString("unitList", unit);
                                editor.putString("floorList", floor);
                                editor.putInt("communityListid", positions.get(0));
                                editor.putInt("buildingListid", positions.get(1));
                                editor.putInt("unitListid", positions.get(2));
                                editor.putInt("floorListid", positions.get(3));
                                editor.remove("houseListid");
                            }
                            if(positions.size()==5){
                                String community= gson.toJson(communityList);
                                String building = gson.toJson(buildingList);
                                String unit = gson.toJson(unitList);
                                String floor = gson.toJson(floorList);
                                String house = gson.toJson(houseList);
                                Log.d("InspectActivity", "saved json is "+ house);
                                editor.putString("communityList", community);
                                editor.putString("buildingList", building);
                                editor.putString("unitList", unit);
                                editor.putString("floorList", floor);
                                editor.putString("houseList", house);
                                editor.putInt("communityListid", positions.get(0));
                                editor.putInt("buildingListid", positions.get(1));
                                editor.putInt("unitListid", positions.get(2));
                                editor.putInt("floorListid", positions.get(3));
                                editor.putInt("houseListid", positions.get(4));
                            }
                        }



                        String compid=sp.getString("r_companyid","201810230005");

                        if(firmMap!=null&&firmMap.size()>0)
                        {
                            for(String firmname:firmMap.keySet()){
                                Log.i("firmMap",firmname+":"+firmMap.get(firmname));
                                if(report_firm.getText().toString().equals(firmname)){
                                    Log.i("firmMap相等",firmMap.get(firmname));
                                    compid=firmMap.get(firmname);
                                    editor.putString("r_companyid", compid);
                                }
                            }
                        }
                        String typeid=sp.getString("r_typeid","201811020001");
                        if(typeMap!=null&&typeMap.size()>0)
                        {
                            for(String typename:typeMap.keySet()){
                                if(report_type.getText().toString().equals(typename)){
                                    Log.i("typeMap",typeMap.get(typename));
                                    typeid=typeMap.get(typename);
                                    editor.putString("r_typeid", typeid);
                                }
                            }
                        }



                        editor.commit();


                        Map<String,String> params=new HashMap<>();
                  //      if(repairBean==null||repairBean.getId().equals("")){

                        params.put("r_id ","");
                        params.put("r_state","报修");

                        params.put("r_addr",report_address.getText().toString());
                        params.put("r_type",typeid);
                        params.put("r_billid",billid);
                        params.put("r_tbname",tbname);
                        params.put("r_date",report_time.getText().toString());
                        params.put("r_part",report_position.getText().toString());
                        params.put("r_usersign",report_user.getText().toString());
                        params.put("r_userphone",report_phone.getText().toString());
                        params.put("r_propertyview",report_opinion.getText().toString());
                        params.put("r_companyid",compid);

                        params.put("r_propertysign",datasp.getString("cu_username",""));
                        params.put("r_propertyphone",datasp.getString("cu_mobile",""));
                        params.put("r_cmpid", datasp.getString("cu_cmpid",""));
                        params.put("r_depid", datasp.getString("cu_depid",""));
                        params.put("r_depname", datasp.getString("dep_name",""));
                        params.put("r_createuid", datasp.getString("cu_userid",""));
                        params.put("r_createmen", datasp.getString("cu_username",""));
                        params.put("r_createdate", report_time.getText().toString());
                        params.put("r_updatedate", report_time.getText().toString());

                        params.put("sqlcmd","repair_ins");
                        params.put("datatype","json");

                        Log.i("提交uri",Contans.uri+"?"+params.toString());


                        myOkhttpClient.uploadImage(params,mDatas);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }
    /**
     * 报修部位
     * */
    private void initPositionPop() {
        View positionView=LayoutInflater.from(this).inflate(R.layout.pop_report_position,null,false);
        positionPopWindow=new PopupWindow(positionView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_report_position_btn_back=positionView.findViewById(R.id.pop_report_position_btn_back);
        Button pop_report_position_btn_save=positionView.findViewById(R.id.pop_report_position_btn_save);
        final EditText pop_report_position_edittext=positionView.findViewById(R.id.pop_report_position_edittext);

        pop_report_position_edittext.setText(report_position.getText().toString());
        pop_report_position_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(positionPopWindow);
            }
        });
        pop_report_position_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(positionPopWindow);
                report_position.setText(pop_report_position_edittext.getText().toString().trim());
            }
        });


    }
    /**
     * 报修类型
     * */
    private void initTypePop() {
        View typeView=LayoutInflater.from(this).inflate(R.layout.pop_report_type,null,false);
        typePopWindow=new PopupWindow(typeView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_report_type_btn_back=typeView.findViewById(R.id.pop_report_type_btn_back);
      //  Button pop_report_type_btn_save=typeView.findViewById(R.id.pop_report_type_btn_save);
        typeListview=typeView.findViewById(R.id.pop_report_type_list);
        myExpandableAdapter=new MyExpandableAdapter(type_group,type_child,ReportActivity.this);
        typeListview.setAdapter(myExpandableAdapter);

        typeListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.i(""+ReportActivity.this, "group " + groupPosition + " child " + childPosition);
                popWindowUtils.dissPopupWindow(typePopWindow);
                report_type.setText(type_child.get(groupPosition).get(childPosition).getName());
                return false;
            }
        });

        typeListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.i("" + ReportActivity.this, "group " + groupPosition );

                if(groupPosition>1) {
                    popWindowUtils.dissPopupWindow(typePopWindow);
                    report_type.setText(type_group.get(groupPosition).getName());
                }
                return false;
            }
        });


        pop_report_type_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(typePopWindow);
            }
        });
//        pop_report_type_btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popWindowUtils.dissPopupWindow(typePopWindow);
//             //   report_type.setText();
//            }
//        });
//        typeAdapter=new ArrayAdapter(ReportActivity.this, android.R.layout.simple_list_item_single_choice, typeList);
//        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(ReportActivity.this);
//        singleChoiceDialog.setTitle("报修单位");
//        // 第二个参数是默认选项，此处设置为0
//
//        singleChoiceDialog.setSingleChoiceItems(typeAdapter, typeid,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        typeid=which;
//                    }
//                });
//
//        singleChoiceDialog.setPositiveButton("确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i("which",which+"");
//                        Log.i("choiceId",typeid+"");
//                        if(typeid!=-1){
//                            if(typeList.get(typeid).equals("土建工程")){
//                                getReportType("002012001");
//                            }else if(typeList.get(typeid).equals("安装工程")){
//                                getReportType("002012002");
//                            }
//                            else{
//                                report_firm.setText(typeList.get(typeid));
//                            }
//                        //    report_firm.setText(typeList.get(typeid).getName()+"");
//                        }
//
//
//                    }
//                });
//        singleChoiceDialog.setNegativeButton("取消",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        singleChoiceDialog.show();
    }

    private void getReportType(){
        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlname","get_report_type");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                Log.i("公司数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){

                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        Log.i("数组大小", list.size()+"");
                        for(HashMap<String, String> dsMap:list){
                            ReportType type=new ReportType();
                            //取出想要的数据
                            for(String key:dsMap.keySet()) {

                                if(key.equals("bc_code")){
                                    type.setId(dsMap.get(key));
                                }
                                if(key.equals("bc_treeid")){
                                    type.setDepid(dsMap.get(key));

                                }
                                if(key.equals("bc_name")){
                                    type.setName(dsMap.get(key));
                                }

                            }
                            typeMap.put(type.getName(),type.getId());
                            if(type.getDepid().equals("002012")) {
                                type_group.add(type);

                            }else if(type.getDepid().equals("002012001")){

                                type_child_tujian.add(type);
                            }
                            else if(type.getDepid().equals("002012002")){
                                type_child_anzhuang.add(type);
                            }

                        }
                    }

                }
                type_child.add(type_child_tujian);
                type_child.add(type_child_anzhuang);
                type_child.add(type_child_xf);
                type_child.add(type_child_dt);
                type_child.add(type_child_rd);
                type_child.add(type_child_qd);
                type_child.add(type_child_sb);
                myExpandableAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(ReportActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(ReportActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
                myLoadDialog.hideLoading();
            }

            @Override
            public void onFinished() {
                myLoadDialog.hideLoading();
            }
        });

    }
    /**
     * 报修单位
     * */
    private void initFirmPop(final List firmList) {

        listAdapter=new ArrayAdapter(ReportActivity.this, android.R.layout.simple_list_item_single_choice, firmList);

        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(ReportActivity.this);
        singleChoiceDialog.setTitle("报修单位");
        // 第二个参数是默认选项，此处设置为0

        singleChoiceDialog.setSingleChoiceItems(listAdapter, choiceId,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiceId=which;
                    }
                });

        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("which",which+"");
                        Log.i("choiceId",choiceId+"");
                        if(choiceId!=-1)
                        report_firm.setText(firmList.get(choiceId)+"");

                    }
                });
        singleChoiceDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        singleChoiceDialog.show();


    }

    //获取报修公司名单
    private void getReportFirm(){

        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_company_list");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("pageindex","1");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");

        params.addBodyParameter("c_type","201808020025");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                Log.i("公司数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){

                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        Log.i("数组大小", list.size()+"");
                        for(HashMap<String, String> dsMap:list){

                            String id="";
                            String name="";
                            //取出想要的数据
                            for(String key:dsMap.keySet()) {

                                if(key.equals("c_id")){
                                    id=dsMap.get(key);
                                }
                                if(key.equals("c_shortname")){
                                    name=dsMap.get(key);
                                    firmList.add(name);
                                }

                            }
                            firmMap.put(name,id);
                        }
                    }
                }
                initFirmPop(firmList);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(ReportActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(ReportActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
                myLoadDialog.hideLoading();
            }

            @Override
            public void onFinished() {
                myLoadDialog.hideLoading();
            }
        });

    }
    /**
     * 报修地址
     * */
    private void initAddressPop() {

        final View objView= LayoutInflater.from(this).inflate(R.layout.address_select, null, false);
        addressPopWindow=new PopupWindow(objView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
     //   address_select_tv_area=objView.findViewById(R.id.address_select_tv_area);
        address_select_tv_community=objView.findViewById(R.id.address_select_tv_community);
        address_select_tv_building=objView.findViewById(R.id.address_select_tv_building);
        address_select_tv_unit=objView.findViewById(R.id.address_select_tv_unit);
        address_select_tv_floor=objView.findViewById(R.id.address_select_tv_floor);
        address_select_tv_house=objView.findViewById(R.id.address_select_tv_house);

        address_select_img_go=objView.findViewById(R.id.address_select_img_go);
        address_select_img_go2=objView.findViewById(R.id.address_select_img_go2);
        address_select_img_go3=objView.findViewById(R.id.address_select_img_go3);
        address_select_img_go4=objView.findViewById(R.id.address_select_img_go4);
  //      address_select_img_go5=objView.findViewById(R.id.address_select_img_go5);


        addresslistview=objView.findViewById(R.id.address_select_listview);
        if(positions!=null) {
            if(positions.size()==0){

                count=0;
                initAddress(-1);

            }
            if(positions.size()==1){

                count=1;
                initAddress(positions.get(0));


            }
            if(positions.size()==2){

                address_select_tv_community.setText(communityList.get(positions.get(0)).getN());


                count=2;
                initAddress(positions.get(1));

            }
            if(positions.size()==3){

                address_select_tv_community.setText(communityList.get(positions.get(0)).getN());
                address_select_tv_building.setText(buildingList.get(positions.get(1)).getN());


                count=3;
                initAddress(positions.get(2));

            }
            if(positions.size()==4){

                address_select_tv_community.setText(communityList.get(positions.get(0)).getN());
                address_select_tv_building.setText(buildingList.get(positions.get(1)).getN());
                address_select_tv_unit.setText(unitList.get(positions.get(2)).getN());

                count=4;
                initAddress(positions.get(3));
                //            myAddressAdapter=new MyAddressAdapter(this,houseList);
            }
            if(positions.size()==5){
                address_select_tv_community.setText(communityList.get(positions.get(0)).getN());
                address_select_tv_building.setText(buildingList.get(positions.get(1)).getN());
                address_select_tv_unit.setText(unitList.get(positions.get(2)).getN());
                address_select_tv_floor.setText(floorList.get(positions.get(3)).getN());
                count=5;
                myAddressAdapter=new MyAddressAdapter(this,houseList);
                addresslistview.setAdapter(myAddressAdapter);
                initAddress(positions.get(4));
                //             myAddressAdapter=new MyAddressAdapter(this,houseList);
            }

        }

        addresslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                count++;
                if(count==1){
                    positions.clear();
                }
                if(count>5){
                    count=5;
                    positions.remove(4);
                    Log.i("position",positions.size()+"");
                }
                Log.i("count",count+"");
                Log.i("position",position+"");
                positions.add(position);
                initAddress(position);



            }
        });

//        address_select_tv_area.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(!address_select_tv_area.getText().equals("片区")){
//                    count=0;
//                    initAddress(0);
//                    for(int i=positions.size()-1;i>=0;i--){
//                        positions.remove(i);
//                    }
//
//
//                }
//
//
//            }
//        });
        address_select_tv_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!address_select_tv_community.getText().equals("小区")) {
                    count = 0;
                    initAddress(-1);
                    for(int i=positions.size()-1;i>=0;i--){
                        positions.remove(i);
                    }



                }

            }
        });
        address_select_tv_building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!address_select_tv_building.getText().equals("楼栋")) {
                    count = 1;
                    initAddress(positions.get(0));
                    for(int i=positions.size()-1;i>0;i--){
                        positions.remove(i);
                    }
                }
            }
        });
        address_select_tv_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!address_select_tv_unit.getText().equals("单元")) {
                    count = 2;
                    initAddress(positions.get(1));

                    for(int i=positions.size()-1;i>1;i--){
                        positions.remove(i);
                    }
                }
            }
        });
        address_select_tv_floor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!address_select_tv_floor.getText().equals("楼层")) {
                    count = 3;
                    initAddress(positions.get(2));

                    for(int i=positions.size()-1;i>2;i--){
                        positions.remove(i);
                    }
                }
            }
        });
        address_select_tv_house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!address_select_tv_house.getText().equals("房屋")) {
                    count = 4;

                    initAddress(positions.get(3));
                    for(int i=positions.size()-1;i>3;i--){
                        positions.remove(i);
                    }
                }
            }
        });



        Button address_select_btn_back=objView.findViewById(R.id.address_select_btn_back);
        address_select_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(addressPopWindow);
            }
        });
        Button address_select_btn_save=objView.findViewById(R.id.address_select_btn_save);
        address_select_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(address_select_tv_area.getText().equals("小区")||address_select_tv_area.getText().equals("位置类型")){
//                    address_select_tv_area.setText("");
//                    report_address.setText("");
//                }
                if(address_select_tv_community.getText().equals("小区")||address_select_tv_community.getText().equals("位置类型")){
                    address_select_tv_community.setText("");
                    report_address.setText("");

                }
                else if(address_select_tv_building.getText().equals("楼栋")){
                    address_select_tv_building.setText("");
                    report_address.setText(address_select_tv_community.getText());

                }
                else if(address_select_tv_unit.getText().equals("单元")){
                    address_select_tv_unit.setText("");
                    report_address.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText());
                }
                else if(address_select_tv_floor.getText().equals("楼层")){
                    address_select_tv_floor.setText("");
                    report_address.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText() + " " + address_select_tv_unit.getText());
                }
                else if(address_select_tv_house.getText().equals("房屋")){
                    address_select_tv_house.setText("");
                    report_address.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText()
                            + " " + address_select_tv_unit.getText() + " " + address_select_tv_floor.getText());
                }
                else {
                    report_address.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText()
                            + " " + address_select_tv_unit.getText() + " " + address_select_tv_floor.getText() + " " + address_select_tv_house.getText());
                }
                popWindowUtils.dissPopupWindow(addressPopWindow);
            }
        });

    }


    //地址点击事件
    public void initAddress(int position){
        if(count==0){
            address_select_img_go.setVisibility(View.INVISIBLE);
            address_select_img_go2.setVisibility(View.INVISIBLE);
            address_select_img_go3.setVisibility(View.INVISIBLE);
            address_select_img_go4.setVisibility(View.INVISIBLE);
      //      address_select_img_go5.setVisibility(View.INVISIBLE);

            address_select_tv_community.setText("小区");
            address_select_tv_community.setTextColor(getResources().getColor(R.color.gray));


            address_select_tv_building.setText("");
            address_select_tv_unit.setText("");
            address_select_tv_floor.setText("");
            address_select_tv_house.setText("");

            communityList.clear();
            getAddressData("mps_uptown", "", communityList);
            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,communityList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }

        if(count==1){
            address_select_img_go.setVisibility(View.VISIBLE);
            address_select_img_go2.setVisibility(View.INVISIBLE);
            address_select_img_go3.setVisibility(View.INVISIBLE);
            address_select_img_go4.setVisibility(View.INVISIBLE);
       //     address_select_img_go5.setVisibility(View.INVISIBLE);

            address_select_tv_community.setText(communityList.get(position).getN());
            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
      //      address_select_tv_area.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_building.setTextColor(getResources().getColor(R.color.gray));
            address_select_tv_building.setText("楼栋");
            address_select_tv_unit.setText("");
            address_select_tv_floor.setText("");
            address_select_tv_house.setText("");

            buildingList.clear();
            getAddressData("mps_building",communityList.get(position).getI(),buildingList);

            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,buildingList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }
        if(count==2){
            address_select_img_go.setVisibility(View.VISIBLE);
            address_select_img_go2.setVisibility(View.VISIBLE);
            address_select_img_go3.setVisibility(View.INVISIBLE);
            address_select_img_go4.setVisibility(View.INVISIBLE);
     //       address_select_img_go5.setVisibility(View.INVISIBLE);

            address_select_tv_building.setText(buildingList.get(position).getN());

            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
        //    address_select_tv_area.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_building.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_unit.setTextColor(getResources().getColor(R.color.gray));
            address_select_tv_unit.setText("单元");
            address_select_tv_floor.setText("");
            address_select_tv_house.setText("");

            unitList.clear();
            getAddressData("mps_unit",buildingList.get(position).getI(),unitList);

            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,unitList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }
        if(count==3){
            address_select_img_go.setVisibility(View.VISIBLE);
            address_select_img_go2.setVisibility(View.VISIBLE);
            address_select_img_go3.setVisibility(View.VISIBLE);
            address_select_img_go4.setVisibility(View.INVISIBLE);
  //          address_select_img_go5.setVisibility(View.INVISIBLE);

            address_select_tv_unit.setText(unitList.get(position).getN());

            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
      //      address_select_tv_area.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_building.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_unit.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_floor.setTextColor(getResources().getColor(R.color.gray));
            address_select_tv_floor.setText("楼层");
            address_select_tv_house.setText("");
            floorList.clear();
            getAddressData("mps_floor",unitList.get(position).getI(),floorList);

            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,floorList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }
        if(count==4){
            address_select_img_go.setVisibility(View.VISIBLE);
            address_select_img_go2.setVisibility(View.VISIBLE);
            address_select_img_go3.setVisibility(View.VISIBLE);
            address_select_img_go4.setVisibility(View.VISIBLE);
     //       address_select_img_go5.setVisibility(View.VISIBLE);

            address_select_tv_floor.setText(floorList.get(position).getN());
            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
      //      address_select_tv_area.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_building.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_unit.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_floor.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_house.setTextColor(getResources().getColor(R.color.gray));
            address_select_tv_house.setText("房屋");

            houseList.clear();
            getAddressData("mps_room",floorList.get(position).getI(),houseList);

            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,houseList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }
        if(count==5){
            address_select_img_go.setVisibility(View.VISIBLE);
            address_select_img_go2.setVisibility(View.VISIBLE);
            address_select_img_go3.setVisibility(View.VISIBLE);
            address_select_img_go4.setVisibility(View.VISIBLE);
       //     address_select_img_go5.setVisibility(View.VISIBLE);

            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
   //         address_select_tv_area.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_building.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_unit.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_floor.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_house.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_house.setText(houseList.get(position).getN());

//            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,houseList);
//            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.setCurrentItem(position);
            myAddressAdapter.notifyDataSetChanged();


        }
    }

    private void getAddressData(final String tbname, String id, final List<AddressBean> addressList) {

        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_search_list");
        params.addBodyParameter("datatype","json");

        params.addBodyParameter("tbname",tbname);

        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("id",id);
        params.addBodyParameter("depid",datasp.getString("cu_depid",""));
        params.addBodyParameter("pageindex","1");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");

        params.addBodyParameter("compid", datasp.getString("cu_compid",""));


        Log.i("compid", datasp.getString("cu_compid",""));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                Log.i("维修数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0) {

                        for (HashMap<String, String> dsMap : list) {

                            AddressBean abean = new AddressBean();
                            //取出想要的数据
                            for (String key : dsMap.keySet()) {
                                if (key.equals("id")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setI(dsMap.get(key));
                                }
                                if (key.equals("name")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setN(dsMap.get(key));
                                }
                                if (key.equals("u_lon")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setLon(dsMap.get(key));
                                }
                                if (key.equals("u_lat")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setLat(dsMap.get(key));
                                }

                            }
                            double lon2 = Double.parseDouble(abean.getLon());
                            double lat2 = Double.parseDouble(abean.getLat());
                            if (tbname.equals("mps_uptown")) {
                                double distance = LocationUtils.getDistance(lat1,lon1, lat2, lon2);
                                Log.i("距离为：", distance + "米");
                                if (distance < 1000) {
                                    addressList.add(abean);
                                }
                            } else {
                                addressList.add(abean);
                            }
                        }
                    }
                }
                myAddressAdapter.notifyDataSetChanged();


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(ReportActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(ReportActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
                myLoadDialog.hideLoading();
            }

            @Override
            public void onFinished() {
                myLoadDialog.hideLoading();
            }
        });
    }

    /**
     *
     * 报修时间设置
     * */
    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int sysYear = calendar.get(Calendar.YEAR);
        //月
        int sysMonth = calendar.get(Calendar.MONTH)+1;
        //日
        int sysDay = calendar.get(Calendar.DAY_OF_MONTH);

        //日历控件
        report_time.setText(sysYear+"/"+sysMonth+"/"+sysDay);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,ReportActivity.this);
    }
    @AfterPermissionGranted(CAREMA)//请求码
    private void after() {
        if (EasyPermissions.hasPermissions(ReportActivity.this, mPermissions)){
            showListDialog();
        }else{
            EasyPermissions.requestPermissions(ReportActivity.this,"请给予照相机权限,否则app无法正常运行",CAREMA,mPermissions);
        }
    }
    /**
     *
     * 调用相机返回结果
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_CHOOSE){
            if(resultCode==RESULT_OK) {

                luban(Matisse.obtainPathResult(data));

            }
        }
        if (requestCode == CAREMA && resultCode == RESULT_OK) {
            luban(photoUtils.getPhotoFile());

        }
    }


    //选择相册后压缩
    public void luban(final List<String> imgs){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Luban.with(ReportActivity.this)
                        .load(imgs)                                   // 传入要压缩的图片列表
                        .setRenameListener(new OnRenameListener() {
                            @Override
                            public String rename(String filePath) {
                                return datasp.getString("c_name","")+System.currentTimeMillis() + ".png";
                            }
                        })
                        .ignoreBy(100)                                  // 忽略不压缩图片的大小
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {
                                // TODO 压缩开始前调用，可以在方法内启动 loading UI
                            }

                            @Override
                            public void onSuccess(File file) {

                                mDatas.add(file.toString());

                                notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO 当压缩过程出现问题时调用
                            }
                        }).launch();    //启动压缩



            }
        }.start();

    }

    //拍照完成后压缩
    public void luban(final File file){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Luban.with(ReportActivity.this)
                        .load(file)                                   // 传入要压缩的图片列表
                        .setRenameListener(new OnRenameListener() {
                            @Override
                            public String rename(String filePath) {
                                return datasp.getString("c_name","")+System.currentTimeMillis() + ".png";
                            }
                        })
                        .ignoreBy(100)                                  // 忽略不压缩图片的大小
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {
                                // TODO 压缩开始前调用，可以在方法内启动 loading UI
                            }

                            @Override
                            public void onSuccess(File file) {
                                //      imgs.add(file.toString());
                                mDatas.add(file.toString());

                                Log.i("压缩成功",mDatas.size()+"");
                                //       adapter.notifyDataSetChanged();
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO 当压缩过程出现问题时调用
                            }
                        }).launch();    //启动压缩



            }
        }.start();

    }


    //更新gridview
    private void notifyDataSetChanged() {
        myAdapter=new MyGridViewAdapter(ReportActivity.this, mDatas);
        report_gridview.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myGlideUtils.clearImageAllCache(ReportActivity.this);
    }
}
