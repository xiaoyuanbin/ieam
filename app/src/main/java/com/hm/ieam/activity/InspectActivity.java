package com.hm.ieam.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.hm.ieam.adapter.MyAssetsAdapter;
import com.hm.ieam.bean.AddressBean;
import com.hm.ieam.bean.AssetsBean;
import com.hm.ieam.utils.Contans;
import com.hm.ieam.utils.JsonUtils;
import com.hm.ieam.utils.LocationUtils;
import com.hm.ieam.utils.MyLoadDialog;
import com.hm.ieam.utils.MyOkhttpClient;

import com.hm.ieam.utils.MyGlideUtils;
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

public class InspectActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> mDatas;    //存放所有图片的集合
 //   private List<String> imgs;    //上传的所有图片的集合

    HashMap<String, ArrayList<HashMap<String, String>>> jsonMap; //存放解析json数据的结果
//    List<AddressBean.AddressItemBean> areaList;
    List<AddressBean> communityList;
    List<AddressBean> buildingList;
    List<AddressBean> unitList;
    List<AddressBean> floorList;
    List<AddressBean> houseList;

    List<AssetsBean> assetsList;
    String inspectType;

    SharedPreferences sp;
    SharedPreferences datasp;
    SharedPreferences.Editor editor;
    int count=1;   //计算地址选择层数
    List<Integer> positions;
    String[] mPermissions=new String[]{Manifest.permission.CAMERA
            ,Manifest.permission.READ_PHONE_STATE
            ,Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};

 //   private List<String>  imageDescription;    //图片编辑文字信息
    MyOkhttpClient myOkhttpClient;
    private static final int CAREMA = 2;   //调用相机请求码
 //   private static final int maxImg = 6;     //最大上传照片数量
    public static final int REQUEST_CODE_CHOOSE=CAREMA+1;    //上传图片
    PopWindowUtils popWindowUtils;
    PhotoUtils photoUtils;   //拍照工具类
    Uri imageUri;    //拍照时获取图片uri
    int imageId;    //编辑图片时的id
    MyLoadDialog myLoadDialog;

    MyGlideUtils myGlideUtils;    //加载图片工具类
    GridView inspect_gridview;   //显示图片的列表
    MyGridViewAdapter adapter;      //显示图片的adapter
    ListView addresslistview;
    MyAddressAdapter myAddressAdapter;    //地址适配器
    Button inspect_back;        //巡查页面返回按钮
    Button inspect_submit;       //提交按钮
    ListView assetsListview;
    MyAssetsAdapter myAssetsAdapter;    //固定资产适配器

    int assetsId;

    LinearLayout inspect_ll_obj;
    LinearLayout inspect_ll_content;
    LinearLayout inspect_ll_result;
    LinearLayout inspect_ll_time;
    LinearLayout inspect_ll_remark;

    TextView inspect_object;
    TextView inspect_content;
    TextView inspect_result;
    TextView inspect_time;
    TextView inspect_remark;

 //   TextView address_select_tv_area;
    TextView address_select_tv_community;
    TextView address_select_tv_building;
    TextView address_select_tv_unit;
    TextView address_select_tv_floor;
    TextView address_select_tv_house;

    ImageView address_select_img_go;
    ImageView address_select_img_go2;
    ImageView address_select_img_go3;
    ImageView address_select_img_go4;
 //   ImageView address_select_img_go5;

    private PopupWindow resultPopWindow;    //巡查结果选择
    private PopupWindow contentPopWindow;    //巡查内容窗口
    private PopupWindow addressPopWindow;    //巡查对象窗口
    private PopupWindow remarkPopWindow;    //巡查备注窗口
    private PopupWindow imgPopWindow;    //巡查图片
    private PopupWindow photoPopWindow;    //巡查图片

    Gson gson;
    double lon1;  //当前位置的经度
    double lat1;   //当前位置的纬度
    //   TextView pop_inspect_image_text;    //图片描述
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect);
        initData();
        initView();
        initPopWindow();




    }

    //初始化图片，只有一张提示拍照的的图片


    private void initData() {
        sp=getSharedPreferences("inspect",MODE_PRIVATE);
        datasp=getSharedPreferences("date",MODE_PRIVATE);
        gson = new Gson();
        photoUtils=new PhotoUtils(this);
        myGlideUtils=new MyGlideUtils();
        myLoadDialog=new MyLoadDialog(this);
        myOkhttpClient=new MyOkhttpClient(InspectActivity.this);

        Intent inspectDate=getIntent();

        inspectType=inspectDate.getStringExtra("inspectType");

        lon1=MyApplication.lon;
        lat1=MyApplication.lat;
 //       imgs=new ArrayList<>();
        mDatas=new ArrayList<>();
  //      areaList=new ArrayList<>();
        communityList=new ArrayList<>();
        buildingList=new ArrayList<>();
        unitList=new ArrayList<>();
        floorList=new ArrayList<>();
        houseList=new ArrayList<>();
        assetsList=new ArrayList<>();
        positions=new ArrayList<>();


        if(!inspectType.equals("固定资产")) {

            if (sp.getInt("communityListid", -1) > -1) {
                positions.add(sp.getInt("communityListid", 0));
            }
            if (sp.getInt("buildingListid", -1) > -1) {
                positions.add(sp.getInt("buildingListid", 0));
            }
            if (sp.getInt("unitListid", -1) > -1) {
                positions.add(sp.getInt("unitListid", 0));
            }
            if (sp.getInt("floorListid", -1) > -1) {
                positions.add(sp.getInt("floorListid", 0));
            }
            if (sp.getInt("houseListid", -1) > -1) {
                positions.add(sp.getInt("houseListid", 0));
            }
            initAddressData();
        }


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
    //初始化巡查页面的按钮
    private void initView() {
        inspect_object=findViewById(R.id.inspect_object);
        inspect_content=findViewById(R.id.inspect_content);
        inspect_result=findViewById(R.id.inspect_result);
        inspect_time=findViewById(R.id.inspect_time);
        inspect_remark=findViewById(R.id.inspect_remark);

        inspect_gridview=findViewById(R.id.inspect_gridview);
        inspect_back=findViewById(R.id.inspect_back);
        inspect_submit=findViewById(R.id.inspect_submit);

        inspect_ll_obj=findViewById(R.id.inspect_ll_obj);
        inspect_ll_content=findViewById(R.id.inspect_ll_content);
        inspect_ll_remark=findViewById(R.id.inspect_ll_remark);
        inspect_ll_result=findViewById(R.id.inspect_ll_result);
        inspect_ll_time=findViewById(R.id.inspect_ll_time);

        if(inspectType.equals("固定资产")){
            inspect_object.setText(sp.getString("a_object",""));
            inspect_content.setText(sp.getString("a_content",""));
            inspect_result.setText(sp.getString("a_result","正常"));
            inspect_remark.setText(sp.getString("a_remark",""));
        }else{
            inspect_object.setText(sp.getString("p_object",""));
            inspect_content.setText(sp.getString("p_content",""));
            inspect_result.setText(sp.getString("p_result","正常"));
            inspect_remark.setText(sp.getString("p_remark",""));
        }
        setDate();

        //以下需要设置点击事件监听
        inspect_back.setOnClickListener(this);
        inspect_submit.setOnClickListener(this);


        inspect_ll_obj.setOnClickListener(this);
        inspect_ll_content.setOnClickListener(this);
        inspect_ll_remark.setOnClickListener(this);
        inspect_ll_result.setOnClickListener(this);
        inspect_ll_time.setOnClickListener(this);


        adapter=new MyGridViewAdapter(this,mDatas);
        inspect_gridview.setAdapter(adapter);
        inspect_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position", position + "");
                Log.i("mDatas.size()", mDatas.size() + "");
               imageId = position;
                if (position == mDatas.size()||mDatas.size()==0) {

                   applyPermission();

                } else {
                    startEditImage(mDatas.get(position));

                }

            }

        });



    }
    private void showListDialog() {
        final String[] items = { "拍照","从相册选择"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(InspectActivity.this);
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
    //开始编辑图片

    private void startEditImage(String imgPath) {
    //    Log.i("imageDescription","开始编辑");
        View imgView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_image_item, null, false);
        imgPopWindow=new PopupWindow(imgView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(imgPopWindow);


        Button pop_inspect_image_back=imgView.findViewById(R.id.pop_inspect_image_back);
        ImageView pop_inspect_image=imgView.findViewById(R.id.pop_inspect_image);
        Button pop_inspect_image_delete=imgView.findViewById(R.id.pop_inspect_image_delete);
    //    pop_inspect_image_text=imgView.findViewById(R.id.pop_inspect_image_edittext);
        Glide.with(this).load(imgPath).into(pop_inspect_image);

        Log.i("imageId", imageId + "");
  //      Log.i("开始编辑imageDescription",imageDescription.get(imageId));
     //   pop_inspect_image_text.setText(imageDescription.get(imageId));

        pop_inspect_image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //图片编辑
                popWindowUtils.dissPopupWindow(imgPopWindow);
        //        imageDescription.set(imageId,pop_inspect_image_text.getText().toString().trim());

            }
        });
        pop_inspect_image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(imgPopWindow);
                mDatas.remove(mDatas.get(imageId));
     //           imageDescription.remove(imageDescription.get(imageId));
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




    //拍照时动态申请权限
    public void applyPermission() {
        if (EasyPermissions.hasPermissions(this, mPermissions)){
            showListDialog();
        }else{
//第二个参数是提示信息
            EasyPermissions.requestPermissions(this,"请给予照相机权限,否则app无法正常运行",CAREMA,mPermissions);
        }

    }
    //调用相机拍照
    public void startCarema() {
        imageUri=photoUtils.getPhotoUri();
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, CAREMA);
    }


    //初始化pop工具类
    private void initPopWindow() {

        popWindowUtils=new PopWindowUtils(this);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //巡查界面
            case R.id.inspect_back:
                finish();
                break;
            case R.id.inspect_submit:
                String billid ="";
                String tbname ="";
                if(inspectType.equals("固定资产")){
                    billid=sp.getString("a_billid","");
                    tbname=sp.getString("a_tbname","");
                }
                else{
                    billid=sp.getString("p_billid","");
                    tbname=sp.getString("p_tbname","");
                }

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
                else if(assetsList.size()>0){
                    billid=assetsList.get(assetsId).getId();
                    tbname="mps_assets";
                }

                if(inspect_object.getText().equals("")){
                    Toast.makeText(InspectActivity.this,"巡查对象不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("billid",billid);
                    Log.i("tbname",tbname);
                    startUpload(billid,tbname);
                }
                break;
            case R.id.inspect_ll_obj:


                if(inspectType.equals("固定资产")){
                    assetsList.clear();
                    getAssetsData();
                    initAssetsPop();
                }
                else{
                    initAddressPop();
                }
//                else if (positions.size() > 0 && positions != null) {
//                    Log.i("positions.size()", positions.size() + "");
//
//                    if (positions.size() == 1 && buildingList.size() == 0) {
//                        getAddressData("mps_building", "", buildingList);
//                    }
//                    if (positions.size() == 2 && unitList.size() == 0) {
//                        getAddressData("mps_unit", "", unitList);
//                    }
//                    if (positions.size() == 3 && floorList.size() == 0) {
//                        getAddressData("mps_floor", "", floorList);
//                    }
//                    if (positions.size() == 4 && houseList.size() == 0) {
//                        getAddressData("mps_room", "", houseList);
//                    }
//                    initAddressPop();
//                }
//                else if (positions.size()==0&&communityList.size() == 0) {
//                    Log.i("communityList.size()", communityList.size() + "");
//                    getAddressData("mps_uptown", "", communityList);
//                    initAddressPop();
//                }

                popWindowUtils.showfullPopupWindow(addressPopWindow);


                break;
            case R.id.inspect_ll_content:
                initComtentPop();
                Log.i("点击","进入填写巡查内容");
                popWindowUtils.showfullPopupWindow(contentPopWindow);


                break;
            case R.id.inspect_ll_result:
                initResultPop();
                popWindowUtils.showPopupWindow(resultPopWindow);
                break;


            case R.id.inspect_ll_remark:
                Log.i("点击","进入填写备注");
                initRemarkPop();
                popWindowUtils.showfullPopupWindow(remarkPopWindow);
                break;


                default:
                    break;

        }

    }

    private void upPhoto() {
        Matisse.from(InspectActivity.this)
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


    private void startUpload(final String billid, final String tbname) {

        editor=sp.edit();

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(InspectActivity.this);

        normalDialog.setTitle("温馨提示");
        normalDialog.setMessage("确认提交吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(inspectType.equals("固定资产")){
                            Log.i("提交","我是"+inspectType);
                            editor.putString("a_result",inspect_result.getText().toString());
                            editor.putString("a_content",inspect_content.getText().toString());
                            editor.putString("a_remark", inspect_remark.getText().toString());
                            editor.putString("a_object", inspect_object.getText().toString());
                            editor.putString("a_billid", billid);
                            editor.putString("a_tbname", tbname);

                        }else{
                            Log.i("提交","我是"+inspectType);
                            editor.putString("p_result",inspect_result.getText().toString());
                            editor.putString("p_content",inspect_content.getText().toString());
                            editor.putString("p_remark", inspect_remark.getText().toString());
                            editor.putString("p_object", inspect_object.getText().toString());
                            editor.putString("p_billid", billid);
                            editor.putString("p_tbname", tbname);
                        }

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
                        editor.commit();



                        Map<String, String> params = new HashMap<>();


                        params.put("p_id", "");

                        params.put("p_date", inspect_time.getText().toString());

                        params.put("p_billid", billid);
                        params.put("p_tbname", tbname);
                        params.put("p_result", inspect_result.getText().toString());
                        params.put("p_content", inspect_content.getText().toString());
                        params.put("p_remark", inspect_remark.getText().toString());

                        params.put("p_cmpid", datasp.getString("cu_cmpid","001"));
                        params.put("p_depid",datasp.getString("cu_depid","001004"));
                        params.put("p_depname", datasp.getString("dep_name","物业单位") );
                        params.put("p_createuid", datasp.getString("cu_userid","") );
                        params.put("p_createmen", datasp.getString("cu_username",""));
                        params.put("p_createdate", inspect_time.getText().toString());
                        params.put("p_updatedate", inspect_time.getText().toString());
                        params.put("p_state", "0");
                        params.put("p_type",inspectType);


                        params.put("sqlcmd", "patrol_ins");
                        params.put("datatype", "json");


                        Log.i("提交uri",Contans.uri+"?"+params.toString());
                        myOkhttpClient.uploadImage(params, mDatas);

                    }

                });


        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    //巡查固定资产对象
    private void initAssetsPop() {
        final View objView= LayoutInflater.from(this).inflate(R.layout.pop_assets_address, null, false);
        addressPopWindow=new PopupWindow(objView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        //   address_select_tv_area=objView.findViewById(R.id.address_select_tv_area);
        Button assets_address_btn_back=objView.findViewById(R.id.assets_address_btn_back);
        assetsListview=objView.findViewById(R.id.assets_address_listview);

        assets_address_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(addressPopWindow);
            }
        });
        myAssetsAdapter=new MyAssetsAdapter(InspectActivity.this,assetsList);
        assetsListview.setAdapter(myAssetsAdapter);
        assetsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                assetsId=position;
                popWindowUtils.dissPopupWindow(addressPopWindow);
                inspect_object.setText(assetsList.get(position).getName());
            }
        });
    }

    //巡查对象
    private void initAddressPop() {

        //巡查对象
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
            }


        }

  //      myAddressAdapter=new MyAddressAdapter(this,communityList);
   //     addresslistview.setAdapter(myAddressAdapter);
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
                    inspect_object.setText("");

                }
                else if(address_select_tv_building.getText().equals("楼栋")){
                    address_select_tv_building.setText("");
                    inspect_object.setText(address_select_tv_community.getText());

                }
                else if(address_select_tv_unit.getText().equals("单元")){
                    address_select_tv_unit.setText("");
                    inspect_object.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText());
                }
                else if(address_select_tv_floor.getText().equals("楼层")){
                    address_select_tv_floor.setText("");
                    inspect_object.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText() + " " + address_select_tv_unit.getText());
                }
                else if(address_select_tv_house.getText().equals("房屋")){
                    address_select_tv_house.setText("");
                    inspect_object.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText()
                            + " " + address_select_tv_unit.getText() + " " + address_select_tv_floor.getText());
                }
                else {
                    inspect_object.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText()
                            + " " + address_select_tv_unit.getText() + " " + address_select_tv_floor.getText() + " " + address_select_tv_house.getText());
                }
                popWindowUtils.dissPopupWindow(addressPopWindow);
            }
        });

    }


    //地址点击事件
    public void initAddress(int position){
        Log.i("initAddress中的count",count+"");
        Log.i("initAddress中的position",position+"");
        if(count==0){
            address_select_img_go.setVisibility(View.INVISIBLE);
            address_select_img_go2.setVisibility(View.INVISIBLE);
            address_select_img_go3.setVisibility(View.INVISIBLE);
            address_select_img_go4.setVisibility(View.INVISIBLE);
            //      address_select_img_go5.setVisibility(View.INVISIBLE);

            address_select_tv_community.setText("小区");
            address_select_tv_community.setTextColor(getResources().getColor(R.color.gray));


            //     address_select_tv_community.setText("");
            address_select_tv_building.setText("");
            address_select_tv_unit.setText("");
            address_select_tv_floor.setText("");
            address_select_tv_house.setText("");

            communityList.clear();
            getAddressData("mps_uptown", "", communityList);
            myAddressAdapter=new MyAddressAdapter(InspectActivity.this,communityList);
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

        //    buildingList.removeAll(buildingList);
            getAddressData("mps_building",communityList.get(position).getI(),buildingList);

            myAddressAdapter=new MyAddressAdapter(InspectActivity.this,buildingList);
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

        //    unitList.removeAll(unitList);
            getAddressData("mps_unit",buildingList.get(position).getI(),unitList);

            myAddressAdapter=new MyAddressAdapter(InspectActivity.this,unitList);
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
         //   floorList.removeAll(floorList);
            getAddressData("mps_floor",unitList.get(position).getI(),floorList);

            myAddressAdapter=new MyAddressAdapter(InspectActivity.this,floorList);
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

        //    houseList.removeAll(houseList);
            getAddressData("mps_room",floorList.get(position).getI(),houseList);

            myAddressAdapter=new MyAddressAdapter(InspectActivity.this,houseList);
            addresslistview.setAdapter(myAddressAdapter);

            myAddressAdapter.notifyDataSetChanged();

        }
        if(count==5){
            address_select_img_go.setVisibility(View.VISIBLE);
            address_select_img_go2.setVisibility(View.VISIBLE);
            address_select_img_go3.setVisibility(View.VISIBLE);
            address_select_img_go4.setVisibility(View.VISIBLE);
            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
            //         address_select_tv_area.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_building.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_unit.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_floor.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_house.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_house.setText(houseList.get(position).getN());

//            myAddressAdapter=new MyAddressAdapter(InspectActivity.this,houseList);
//            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.setCurrentItem(position);
            myAddressAdapter.notifyDataSetChanged();

        }
    }

    private void initRemarkPop() {
        //备注
        View remarkView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_remark, null, false);
        remarkPopWindow=new PopupWindow(remarkView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_inspect_remark_btn_back=remarkView.findViewById(R.id.pop_inspect_remark_btn_back);
        Button pop_inspect_remark_btn_save=remarkView.findViewById(R.id.pop_inspect_remark_btn_save);
        final EditText pop_inspect_remark_editText=remarkView.findViewById(R.id.pop_inspect_remark_editText);
        pop_inspect_remark_editText.setText(inspect_remark.getText().toString());

        pop_inspect_remark_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(remarkPopWindow);
            }
        });
        pop_inspect_remark_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(remarkPopWindow);
                inspect_remark.setText(pop_inspect_remark_editText.getText().toString().trim());
            }
        });


    }

    private void initResultPop() {
        //巡查结果
        final View resultView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_result, null, false);
        resultPopWindow=new PopupWindow(resultView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        RadioGroup radioGroup=resultView.findViewById(R.id.pop_inspect_rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = resultView.findViewById(checkedId);
                Log.i("RadioButton","checkedId为"+checkedId);
                inspect_result.setText(radbtn.getText());
                popWindowUtils.dissPopupWindow(resultPopWindow);

            }
        });

    }

    private void getAssetsData() {

        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);

        params.addBodyParameter("sqlname","get_assets_address");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");



        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                Log.i("地址数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0) {

                        for (HashMap<String, String> dsMap : list) {

                            AssetsBean assetsBean=new AssetsBean();
                            //取出想要的数据
                            for (String key : dsMap.keySet()) {
                                if (key.equals("a_id")) {
                                    Log.i("key-value", dsMap.get(key));
                                    assetsBean.setId(dsMap.get(key));
                                }
                                if (key.equals("a_name")) {
                                    Log.i("key-value", dsMap.get(key));
                                    assetsBean.setName(dsMap.get(key));
                                }
                                if (key.equals("a_address")) {
                                    Log.i("key-value", dsMap.get(key));
                                    assetsBean.setAddress(dsMap.get(key));
                                }

                            }
                            assetsList.add(assetsBean);
                        }
                    }

                }
                myAssetsAdapter.notifyDataSetChanged();
                Log.i("打印顺序","前");



            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(InspectActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(InspectActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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

    private void getAddressData(final String tbname, String id, final List<AddressBean> addressList) {
        addressList.clear();
        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);

        params.addBodyParameter("id",id);
        params.addBodyParameter("tbname",tbname);
        params.addBodyParameter("depid",datasp.getString("cu_depid",""));
        params.addBodyParameter("compid", datasp.getString("cu_compid",""));

        params.addBodyParameter("sqlcmd","moblie_search_list");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("pageindex","1");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");



        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                Log.i("地址数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        for(HashMap<String, String> dsMap:list){

                            AddressBean abean=new AddressBean();
                            //取出想要的数据
                            for(String key:dsMap.keySet()) {
                                if(key.equals("id")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setI(dsMap.get(key));
                                }
                                if(key.equals("name")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setN(dsMap.get(key));
                                }
                                if(key.equals("u_lon")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setLon(dsMap.get(key));
                                }
                                if(key.equals("u_lat")) {
                                    Log.i("key-value", dsMap.get(key));
                                    abean.setLat(dsMap.get(key));
                                }

                            }
                            double lon2=Double.parseDouble(abean.getLon());
                            double lat2=Double.parseDouble(abean.getLat());
                            if(tbname.equals("mps_uptown")){
                                double distance= LocationUtils.getDistance(lat1,lon1,lat2,lon2);
                                Log.i("距离为：",distance+"米");
                                if(distance<1000){
                                    addressList.add(abean);
                                }
                            }else{
                                addressList.add(abean);
                            }
                        }

                    }
                }
                Log.i("打印顺序","前");
                if(myAddressAdapter!=null) {
                    myAddressAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(InspectActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(InspectActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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
     * 巡查内容窗口
     *
     * */
    private void initComtentPop() {

        View contentView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_content, null, false);
        contentPopWindow=new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_inspect_content_btn_back=contentView.findViewById(R.id.pop_inspect_content_btn_back);
        Button pop_inspect_content_btn_save=contentView.findViewById(R.id.pop_inspect_content_btn_save);
        final EditText pop_inspect_content_editText=contentView.findViewById(R.id.pop_inspect_content_editText);

        pop_inspect_content_editText.setText(inspect_content.getText().toString());

        pop_inspect_content_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(contentPopWindow);
            }
        });
        pop_inspect_content_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(contentPopWindow);
                inspect_content.setText(pop_inspect_content_editText.getText().toString().trim());
            }
        });



    }

    /**
     *
     * 设置日期
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
        inspect_time.setText(sysYear+"/"+sysMonth+"/"+sysDay);
//        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
//                int year=y;
//                int month=m+1;
//                int day=d;
//                inspect_time.setText(year+"/"+month+"/"+day);
//                Log.i("date","当前日期"+year+"/"+month+"/"+day);
//            }
//        }, sysYear, sysMonth, sysDay);//初始年份，初始月份，初始日期
//        dp.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,InspectActivity.this);
    }
    @AfterPermissionGranted(CAREMA)//请求码
    private void after() {
        if (EasyPermissions.hasPermissions(InspectActivity.this, mPermissions)){
            showListDialog();
        }else{
            EasyPermissions.requestPermissions(InspectActivity.this,"请给予照相机权限,否则app无法正常运行",CAREMA,mPermissions);
        }
    }
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
                Luban.with(InspectActivity.this)
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
                Luban.with(InspectActivity.this)
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
        adapter=new MyGridViewAdapter(InspectActivity.this, mDatas);
        inspect_gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myGlideUtils.clearImageAllCache(InspectActivity.this);
    }

}
