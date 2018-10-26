package com.hm.ieam.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.hm.ieam.R;
import com.hm.ieam.adapter.MyAddressAdapter;
import com.hm.ieam.adapter.MyGridViewAdapter;
import com.hm.ieam.bean.AddressBean;
import com.hm.ieam.bean.AssetsBean;
import com.hm.ieam.bean.InspectBean;
import com.hm.ieam.utils.Contans;
import com.hm.ieam.utils.JsonUtils;
import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.utils.MyLoadDialog;
import com.hm.ieam.utils.MyOkhttpClient;
import com.hm.ieam.utils.PhotoUtils;
import com.hm.ieam.utils.PopWindowUtils;
import com.hm.ieam.widght.AddressPickerView;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class AssetsActivity extends AppCompatActivity implements View.OnClickListener {
    PopWindowUtils popWindowUtils;
    Button assets_back;    //返回按钮
    Button assets_submit;   //提交annual
    GridView assets_gridview;
    LinearLayout assets_ll_address;
    LinearLayout assets_ll_cardid;
    LinearLayout assets_ll_id;
    LinearLayout assets_ll_name;
    LinearLayout assets_ll_original_value;
    LinearLayout assets_ll_position;
    LinearLayout assets_ll_start_date;
    LinearLayout assets_ll_use_year;
    LinearLayout assets_ll_depreciation_method;
    LinearLayout assets_ll_use_department;
    LinearLayout assets_ll_entry_date;
    LinearLayout assets_ll_accumulated_depreciation;
    LinearLayout assets_ll_specification_model;
    LinearLayout assets_ll_use_status;
    LinearLayout assets_ll_remark;


    TextView assets_address;
    TextView assets_cardid;
    TextView assets_id;
    TextView assets_name;
    TextView assets_original_value;
    TextView assets_position;
    TextView assets_start_date;
    TextView assets_use_year;
    TextView assets_depreciation_method;
    TextView assets_use_department;
    TextView assets_entry_date;
    TextView assets_accumulated_depreciation;
    TextView assets_specification_model;
    TextView assets_use_status;
    TextView assets_remark;

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
  //  ImageView address_select_img_go5;



    PopupWindow addressPopWindow;
    PopupWindow cardidPopWindow;
    PopupWindow idPopWindow;
    PopupWindow namePopWindow;
    PopupWindow orValuePopWindow;
    PopupWindow positionPopWindow;
 //   PopupWindow departmentPopWindow;
    PopupWindow useYearPopWindow;
    PopupWindow depMethodPopWindow;
    PopupWindow acDepreciationPopWindow;
    PopupWindow spModelPopWindow;
    PopupWindow useStatusPopWindow;
    PopupWindow remarkPopWindow;
    private PopupWindow imgPopWindow;    //巡查图片

    HashMap<String, ArrayList<HashMap<String, String>>> jsonMap; //存放解析json数据的结果
    MyGridViewAdapter adapter;
    private List<String> mDatas;    //存放所有图片的集合
    private List<String> imgs;    //上传的所有图片的集合
    String[] mPermissions=new String[]{Manifest.permission.CAMERA
            ,Manifest.permission.READ_PHONE_STATE
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};
//    private List<String>  imageDescription;    //图片编辑文字信息
    MyOkhttpClient myOkhttpClient;
    private static final int CAREMA = 2;   //调用相机请求码
    MyGlideUtils myGlideUtils;    //加载图片工具类

 //   List<AddressBean.AddressItemBean> areaList;
    List<AddressBean.AddressItemBean> communityList;
    List<AddressBean.AddressItemBean> buildingList;
    List<AddressBean.AddressItemBean> unitList;
    List<AddressBean.AddressItemBean> floorList;
    List<AddressBean.AddressItemBean> houseList;

    ListView addresslistview;
    MyAddressAdapter myAddressAdapter;

    int choiceDep=-1;    //部门选择
    List<String> depList;
    ArrayAdapter listAdapter;
    int choiceStatus=-1;    //状态选择
    List<String> statusList;



    MyLoadDialog myLoadDialog;

    AssetsBean assetsBean;
    int count=1;   //计算地址选择层数
    List<Integer> positions;



    SharedPreferences sp;
    PhotoUtils photoUtils;   //拍照工具类
    Uri imageUri;    //拍照时获取图片uri
    int imageId;    //编辑图片时的id

    TextView pop_inspect_image_text;    //图片描述


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);
        initData();
        initView();
        popWindowUtils=new PopWindowUtils(this);
        photoUtils=new PhotoUtils(this);
        myGlideUtils=new MyGlideUtils();
        myLoadDialog=new MyLoadDialog(this);
        myOkhttpClient=new MyOkhttpClient(AssetsActivity.this);
    }
    private void initData() {
        sp= getSharedPreferences("date",MODE_PRIVATE);
        String images="";
        Intent assetsDate=getIntent();

        if(assetsDate!=null) {
            assetsBean = (AssetsBean) assetsDate.getSerializableExtra("AssetsBean");
        }
        depList=new ArrayList<>();
        statusList=new ArrayList<>();
//        imageDescription=new ArrayList<>();
//        for(int i=0;i<20;i++){
//            imageDescription.add("");
//
//        }

        imgs=new ArrayList<>();
        mDatas=new ArrayList<>();
  //      areaList=new ArrayList<>();
        communityList=new ArrayList<>();
        buildingList=new ArrayList<>();
        unitList=new ArrayList<>();
        floorList=new ArrayList<>();
        houseList=new ArrayList<>();
        positions=new ArrayList<>();


        if(assetsBean!=null){
            images=assetsBean.getImages();

            if(!"".equals(images)){
                String[] img=images.split(",");
                Log.i("images",img.length+"");
                for(int i=0;i<img.length;i++){

                    mDatas.add(img[i]);
                }
            }

        }






    }

    private void initView() {
        assets_back=findViewById(R.id.assets_back);
        assets_submit=findViewById(R.id.assets_submit);
        assets_gridview=findViewById(R.id.assets_gridview);

        assets_back.setOnClickListener(this);
        assets_submit.setOnClickListener(this);

        assets_ll_address=findViewById(R.id.assets_ll_address);
        assets_ll_cardid=findViewById(R.id.assets_ll_cardid);
        assets_ll_id=findViewById(R.id.assets_ll_id);
        assets_ll_name=findViewById(R.id.assets_ll_name);
        assets_ll_original_value=findViewById(R.id.assets_ll_original_value);
        assets_ll_position=findViewById(R.id.assets_ll_position);
        assets_ll_start_date=findViewById(R.id.assets_ll_start_date);
        assets_ll_use_year=findViewById(R.id.assets_ll_use_year);
        assets_ll_depreciation_method=findViewById(R.id.assets_ll_depreciation_method);
        assets_ll_use_department=findViewById(R.id.assets_ll_use_department);
        assets_ll_entry_date=findViewById(R.id.assets_ll_entry_date);
        assets_ll_accumulated_depreciation=findViewById(R.id.assets_ll_accumulated_depreciation);
        assets_ll_specification_model=findViewById(R.id.assets_ll_specification_model);
        assets_ll_use_status=findViewById(R.id.assets_ll_use_status);
        assets_ll_remark=findViewById(R.id.assets_ll_remark);


        assets_ll_address.setOnClickListener(this);
        assets_ll_cardid.setOnClickListener(this);
        assets_ll_id.setOnClickListener(this);
        assets_ll_name.setOnClickListener(this);
        assets_ll_original_value.setOnClickListener(this);
        assets_ll_position.setOnClickListener(this);
        assets_ll_start_date.setOnClickListener(this);
        assets_ll_use_year.setOnClickListener(this);
        assets_ll_depreciation_method.setOnClickListener(this);
        assets_ll_use_department.setOnClickListener(this);
        assets_ll_entry_date.setOnClickListener(this);
        assets_ll_accumulated_depreciation.setOnClickListener(this);
        assets_ll_specification_model.setOnClickListener(this);
        assets_ll_use_status.setOnClickListener(this);
        assets_ll_remark.setOnClickListener(this);




        assets_address=findViewById(R.id.assets_address);
        assets_cardid=findViewById(R.id.assets_cardid);
        assets_id=findViewById(R.id.assets_id);
        assets_name=findViewById(R.id.assets_name);
        assets_original_value=findViewById(R.id.assets_original_value);
        assets_position=findViewById(R.id.assets_position);
        assets_start_date=findViewById(R.id.assets_start_date);
        assets_use_year=findViewById(R.id.assets_use_year);
        assets_depreciation_method=findViewById(R.id.assets_depreciation_method);
        assets_use_department=findViewById(R.id.assets_use_department);
        assets_entry_date=findViewById(R.id.assets_entry_date);
        assets_accumulated_depreciation=findViewById(R.id.assets_accumulated_depreciation);
        assets_specification_model=findViewById(R.id.assets_specification_model);
        assets_use_status=findViewById(R.id.assets_use_status);
        assets_remark=findViewById(R.id.assets_remark);

        if(assetsBean!=null){
            assets_address.setText(assetsBean.getAddress());
            assets_cardid.setText(assetsBean.getCard());
            assets_id.setText(assetsBean.getId());
            assets_name.setText(assetsBean.getName());
            assets_original_value.setText(assetsBean.getValue());
            assets_position.setText(assetsBean.getPosition());
            assets_start_date.setText(assetsBean.getStartdate());
            assets_use_year.setText(assetsBean.getUsemonth());
            assets_depreciation_method.setText(assetsBean.getWay());
            assets_use_department.setText(assetsBean.getUsedep());
            assets_entry_date.setText(assetsBean.getEntrydate());
            assets_accumulated_depreciation.setText(assetsBean.getDeprecia());
            assets_specification_model.setText(assetsBean.getModel());
            assets_use_status.setText(assetsBean.getStatus());
            assets_remark.setText(assetsBean.getRemark());

        }



        adapter=new MyGridViewAdapter(this,mDatas);
        assets_gridview.setAdapter(adapter);
        assets_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.assets_back:
                finish();
                break;
            case R.id.assets_submit:
                String billid="";
                String tbname="";

                if(address_select_tv_community!=null){
                    if((address_select_tv_community.getText().equals("")||address_select_tv_community.getText().equals("小区"))||address_select_tv_community.getText().equals("位置类型")){

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
                if(assets_position.getText().equals("")){
                    Toast.makeText(AssetsActivity.this,"位置类型不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("id",billid);
                    Log.i("tbname",tbname);
                    startUpload(billid,tbname);
                }
                break;
            case R.id.assets_ll_position:
                if(positions.size()>0&&positions!=null){
                    Log.i("positions.size()",positions.size()+"");

                    if(positions.size()==1&&buildingList.size()==0){
                        buildingList.clear();
                        getAddressData("mps_building", "", buildingList);
                    }
                    if(positions.size()==2&&unitList.size()==0){
                        unitList.clear();
                        getAddressData("mps_unit", "", unitList);
                    }
                    if(positions.size()==3&&floorList.size()==0){
                        floorList.clear();
                        getAddressData("mps_floor", "", floorList);
                    }
                    if(positions.size()==4&&houseList.size()==0){
                        houseList.clear();
                        getAddressData("mps_room", "", houseList);
                    }
                }
                else if(communityList.size()==0) {
                    getAddressData("mps_uptown", "", communityList);
                }
                Log.i("打印顺序","后");
                initAddressPop();
                popWindowUtils.showfullPopupWindow(addressPopWindow);
                break;
            case R.id.assets_ll_cardid:
                initCardIdPop();
                popWindowUtils.showfullPopupWindow(cardidPopWindow);
                break;
            case R.id.assets_ll_id:
                initIdPop();
                popWindowUtils.showfullPopupWindow(idPopWindow);
                break;
            case R.id.assets_ll_name:
                initNamePop();
                popWindowUtils.showfullPopupWindow(namePopWindow);
                break;
            case R.id.assets_ll_original_value:
                initValuePop();
                popWindowUtils.showfullPopupWindow(orValuePopWindow);
                break;
            case R.id.assets_ll_address:
                initPositionPop();
                popWindowUtils.showfullPopupWindow(positionPopWindow);
                break;
            case R.id.assets_ll_start_date:
                setDate(assets_start_date);
                break;
            case R.id.assets_ll_use_year:
                initUseYearPop();
                popWindowUtils.showfullPopupWindow(useYearPopWindow);
                break;
            case R.id.assets_ll_depreciation_method:
                initDepPop();
                popWindowUtils.showfullPopupWindow(depMethodPopWindow);
                break;
            case R.id.assets_ll_use_department:
                if(depList.size()==0||depList==null) {
                //    depList.clear();
                    getDepData();
                }
                else{
                    initDepartmentPop(depList);
                }
             //   initDepartmentPop();
            //    popWindowUtils.showPopupWindow(departmentPopWindow);
                break;
            case R.id.assets_ll_entry_date:
                setDate(assets_entry_date);
                break;
            case R.id.assets_ll_accumulated_depreciation:
                initAccumPop();
                popWindowUtils.showfullPopupWindow(acDepreciationPopWindow);
                break;
            case R.id.assets_ll_specification_model:
                initModelPop();
                popWindowUtils.showfullPopupWindow(spModelPopWindow);
                break;
            case R.id.assets_ll_use_status:
                if(statusList==null||statusList.size()==0){
                    getStatus();
                }
                else {
                    initStatusPop(statusList);
                }
   //             popWindowUtils.showPopupWindow(useStatusPopWindow);
                break;
            case R.id.assets_ll_remark:
                initRemarkPop();
                popWindowUtils.showfullPopupWindow(remarkPopWindow);
                break;
        }

    }

    //查看图片
    private void startEditImage(String imgPath) {
     //   Log.i("imageDescription","开始编辑");
        View imgView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_image_item, null, false);
        imgPopWindow=new PopupWindow(imgView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(imgPopWindow);


        Button pop_inspect_image_back=imgView.findViewById(R.id.pop_inspect_image_back);
        ImageView pop_inspect_image=imgView.findViewById(R.id.pop_inspect_image);
        Button pop_inspect_image_delete=imgView.findViewById(R.id.pop_inspect_image_delete);
  //      pop_inspect_image_text=imgView.findViewById(R.id.pop_inspect_image_edittext);
        Glide.with(this).load(imgPath).into(pop_inspect_image);

        Log.i("imageId", imageId + "");
  //      Log.i("开始编辑imageDescription",imageDescription.get(imageId));
   //     pop_inspect_image_text.setText(imageDescription.get(imageId));

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
         //       imageDescription.remove(imageDescription.get(imageId));
                adapter.notifyDataSetChanged();
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
//    }


    //拍照时动态申请权限
    public void applyPermission(int requestCode) {
        if (EasyPermissions.hasPermissions(this, mPermissions)){
            startCarema(requestCode);
        }else{
//第二个参数是提示信息
            EasyPermissions.requestPermissions(this,"请给予照相机权限,否则app无法正常运行",CAREMA,mPermissions);
        }

    }
    //调用相机拍照
    public void startCarema(int requestCode) {
        imageUri=photoUtils.getPhotoUri();
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, requestCode);
    }

    //位置类型
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
                address_select_tv_floor.setText(unitList.get(positions.get(3)).getN());
                count=5;
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
                    assets_position.setText("");

                }
                else if(address_select_tv_building.getText().equals("楼栋")){
                    address_select_tv_building.setText("");
                    assets_position.setText(address_select_tv_community.getText());

                }
                else if(address_select_tv_unit.getText().equals("单元")){
                    address_select_tv_unit.setText("");
                    assets_position.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText());
                }
                else if(address_select_tv_floor.getText().equals("楼层")){
                    address_select_tv_floor.setText("");
                    assets_position.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText() + " " + address_select_tv_unit.getText());
                }
                else if(address_select_tv_house.getText().equals("房屋")){
                    address_select_tv_house.setText("");
                    assets_position.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText()
                            + " " + address_select_tv_unit.getText() + " " + address_select_tv_floor.getText());
                }
                else {
                    assets_position.setText(address_select_tv_community.getText() + " " + address_select_tv_building.getText()
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


            //     address_select_tv_community.setText("");
            address_select_tv_building.setText("");
            address_select_tv_unit.setText("");
            address_select_tv_floor.setText("");
            address_select_tv_house.setText("");


            myAddressAdapter=new MyAddressAdapter(AssetsActivity.this,communityList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }
//        if(count==1){
//            address_select_img_go.setVisibility(View.VISIBLE);
//            address_select_img_go2.setVisibility(View.INVISIBLE);
//            address_select_img_go3.setVisibility(View.INVISIBLE);
//            address_select_img_go4.setVisibility(View.INVISIBLE);
//         //   address_select_img_go5.setVisibility(View.INVISIBLE);
//
//
//            address_select_tv_community.setText(communityList.get(position).getN());
//            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
//            address_select_tv_community.setTextColor(getResources().getColor(R.color.gray));
//            address_select_tv_community.setText("小区");
//            address_select_tv_building.setText("");
//            address_select_tv_unit.setText("");
//            address_select_tv_floor.setText("");
//            address_select_tv_house.setText("");
//
//
//            communityList.removeAll(communityList);
//            getAddressData("mps_uptown",communityList.get(position).getI(),communityList);
//
//
//            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,communityList);
//            addresslistview.setAdapter(myAddressAdapter);
//            myAddressAdapter.notifyDataSetChanged();
//
//        }
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

            myAddressAdapter=new MyAddressAdapter(AssetsActivity.this,buildingList);
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

            myAddressAdapter=new MyAddressAdapter(AssetsActivity.this,unitList);
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

            myAddressAdapter=new MyAddressAdapter(AssetsActivity.this,floorList);
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

            myAddressAdapter=new MyAddressAdapter(AssetsActivity.this,houseList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }
        if(count==5){
            address_select_img_go.setVisibility(View.VISIBLE);
            address_select_img_go2.setVisibility(View.VISIBLE);
            address_select_img_go3.setVisibility(View.VISIBLE);
            address_select_img_go4.setVisibility(View.VISIBLE);
            //       address_select_img_go5.setVisibility(View.VISIBLE);

            address_select_tv_community.setTextColor(getResources().getColor(R.color.green));
            //         address_select_tv_area.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_building.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_unit.setTextColor(getResources().getColor(R.color.green));
            address_select_tv_floor.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_house.setTextColor(getResources().getColor(R.color.green));

            address_select_tv_house.setText(houseList.get(position).getN());
            myAddressAdapter=new MyAddressAdapter(AssetsActivity.this,houseList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();

        }
    }



    //卡片编号
    private void initCardIdPop() {
        View cardView= LayoutInflater.from(this).inflate(R.layout.pop_assets_card, null, false);
        cardidPopWindow=new PopupWindow(cardView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_card_btn_back=cardView.findViewById(R.id.pop_assets_card_btn_back);
        Button pop_assets_card_btn_save=cardView.findViewById(R.id.pop_assets_card_btn_save);
        final EditText pop_assets_card_edit=cardView.findViewById(R.id.pop_assets_card_edit);
        pop_assets_card_edit.setText(assets_cardid.getText().toString());

        pop_assets_card_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(cardidPopWindow);
            }
        });
        pop_assets_card_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_cardid.setText(pop_assets_card_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(cardidPopWindow);
            }
        });


    }

    //固定资产编号
    private void initIdPop() {
        View idView= LayoutInflater.from(this).inflate(R.layout.pop_assets_id, null, false);
        idPopWindow=new PopupWindow(idView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_id_btn_back=idView.findViewById(R.id.pop_assets_id_btn_back);
        Button pop_assets_id_btn_save=idView.findViewById(R.id.pop_assets_id_btn_save);
        final EditText pop_assets_id_edit=idView.findViewById(R.id.pop_assets_id_edit);
        pop_assets_id_edit.setText(assets_id.getText().toString());

        pop_assets_id_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(idPopWindow);
            }
        });
        pop_assets_id_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_id.setText(pop_assets_id_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(idPopWindow);
            }
        });


    }

    //固定资产名称
    private void initNamePop() {
        View nameView= LayoutInflater.from(this).inflate(R.layout.pop_assets_name, null, false);
        namePopWindow=new PopupWindow(nameView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_name_btn_back=nameView.findViewById(R.id.pop_assets_name_btn_back);
        Button pop_assets_name_btn_save=nameView.findViewById(R.id.pop_assets_name_btn_save);
        final EditText pop_assets_name_edit=nameView.findViewById(R.id.pop_assets_name_edit);
        pop_assets_name_edit.setText(assets_name.getText().toString());

        pop_assets_name_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(namePopWindow);
            }
        });
        pop_assets_name_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_name.setText(pop_assets_name_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(namePopWindow);
            }
        });


    }


    //原值
    private void initValuePop() {
        View valueView= LayoutInflater.from(this).inflate(R.layout.pop_assets_value, null, false);
        orValuePopWindow=new PopupWindow(valueView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_value_btn_back=valueView.findViewById(R.id.pop_assets_value_btn_back);
        Button pop_assets_value_btn_save=valueView.findViewById(R.id.pop_assets_value_btn_save);
        final EditText pop_assets_value_edit=valueView.findViewById(R.id.pop_assets_value_edit);
        pop_assets_value_edit.setText(assets_original_value.getText().toString());

        pop_assets_value_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(orValuePopWindow);
            }
        });
        pop_assets_value_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_original_value.setText(pop_assets_value_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(orValuePopWindow);
            }
        });


    }

    //具体位置
    private void initPositionPop() {
        View positionView= LayoutInflater.from(this).inflate(R.layout.pop_assets_position, null, false);
        positionPopWindow=new PopupWindow(positionView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_position_btn_back=positionView.findViewById(R.id.pop_assets_position_btn_back);
        Button pop_assets_position_btn_save=positionView.findViewById(R.id.pop_assets_position_btn_save);
        final EditText pop_assets_position_edit=positionView.findViewById(R.id.pop_assets_position_edit);
        pop_assets_position_edit.setText(assets_address.getText().toString());

        pop_assets_position_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(positionPopWindow);
            }
        });
        pop_assets_position_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_address.setText(pop_assets_position_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(positionPopWindow);
            }
        });

    }

    //使用年限
    private void initUseYearPop() {
        View useView= LayoutInflater.from(this).inflate(R.layout.pop_assets_use_year, null, false);
        useYearPopWindow=new PopupWindow(useView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_use_year_btn_back=useView.findViewById(R.id.pop_assets_use_year_btn_back);
        Button pop_assets_use_year_btn_save=useView.findViewById(R.id.pop_assets_use_year_btn_save);
        final EditText pop_assets_use_year_edit=useView.findViewById(R.id.pop_assets_use_year_edit);
        pop_assets_use_year_edit.setText(assets_use_year.getText().toString());

        pop_assets_use_year_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(useYearPopWindow);
            }
        });
        pop_assets_use_year_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_use_year.setText(pop_assets_use_year_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(useYearPopWindow);
            }
        });

    }

    //折旧方法
    private void initDepPop() {
        View depMethodView= LayoutInflater.from(this).inflate(R.layout.pop_assets_dep_method, null, false);
        depMethodPopWindow=new PopupWindow(depMethodView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_dep_method_btn_back=depMethodView.findViewById(R.id.pop_assets_dep_method_btn_back);
        Button pop_assets_dep_method_btn_save=depMethodView.findViewById(R.id.pop_assets_dep_method_btn_save);
        final EditText pop_assets_dep_method_edit=depMethodView.findViewById(R.id.pop_assets_dep_method_edit);
        pop_assets_dep_method_edit.setText(assets_depreciation_method.getText().toString());

        pop_assets_dep_method_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(depMethodPopWindow);
            }
        });
        pop_assets_dep_method_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_depreciation_method.setText(pop_assets_dep_method_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(depMethodPopWindow);
            }
        });

    }

    //使用部门
    private void initDepartmentPop(final List depList) {
//        final View departmentView= LayoutInflater.from(this).inflate(R.layout.pop_assets_department, null, false);
//        departmentPopWindow=new PopupWindow(departmentView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        RadioGroup radioGroup=departmentView.findViewById(R.id.pop_assets_rg);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton radbtn = departmentView.findViewById(checkedId);
//                Log.i("RadioButton","checkedId为"+checkedId);
//                assets_use_department.setText(radbtn.getText());
//                popWindowUtils.dissPopupWindow(departmentPopWindow);
//
//            }
//        });

        listAdapter=new ArrayAdapter(AssetsActivity.this, android.R.layout.simple_list_item_single_choice, depList);

        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(AssetsActivity.this);
        singleChoiceDialog.setTitle("报修单位");
        // 第二个参数是默认选项，此处设置为0

        singleChoiceDialog.setSingleChoiceItems(listAdapter, choiceDep,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiceDep=which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("which",which+"");
                        Log.i("choiceId",choiceDep+"");
                        if(choiceDep!=-1)
                            assets_use_department.setText(depList.get(choiceDep)+"");

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

   //获取部门信息
    private void getDepData(){

        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlname","get_assets_unit");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("pageindex","1");
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

                            //取出想要的数据
                            for(String key:dsMap.keySet()) {

                                if(key.equals("bc_name")){
                                    depList.add(dsMap.get(key));
                                }


                            }

                        }
                    }
                    initDepartmentPop(depList);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(AssetsActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(AssetsActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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

    //累计折旧
    private void initAccumPop() {
        final View accumulateView= LayoutInflater.from(this).inflate(R.layout.pop_assets_accumulate, null, false);
        acDepreciationPopWindow=new PopupWindow(accumulateView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_assets_accumulate_btn_back=accumulateView.findViewById(R.id.pop_assets_accumulate_btn_back);
        Button pop_assets_accumulate_btn_save=accumulateView.findViewById(R.id.pop_assets_accumulate_btn_save);
        final EditText pop_assets_accumulate_edit=accumulateView.findViewById(R.id.pop_assets_accumulate_edit);
        pop_assets_accumulate_edit.setText(assets_accumulated_depreciation.getText().toString());

        pop_assets_accumulate_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(acDepreciationPopWindow);
            }
        });
        pop_assets_accumulate_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_accumulated_depreciation.setText(pop_assets_accumulate_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(acDepreciationPopWindow);
            }
        });

    }

    //规格型号
    private void initModelPop() {
        final View modelView= LayoutInflater.from(this).inflate(R.layout.pop_assets_model, null, false);
        spModelPopWindow=new PopupWindow(modelView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_assets_model_btn_back=modelView.findViewById(R.id.pop_assets_model_btn_back);
        Button pop_assets_model_btn_save=modelView.findViewById(R.id.pop_assets_model_btn_save);
        final EditText pop_assets_model_edit=modelView.findViewById(R.id.pop_assets_model_edit);
        pop_assets_model_edit.setText(assets_specification_model.getText().toString());

        pop_assets_model_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(spModelPopWindow);
            }
        });
        pop_assets_model_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_specification_model.setText(pop_assets_model_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(spModelPopWindow);
            }
        });

    }

    //使用状况
    private void initStatusPop(final List statusList) {
//        final View statusView= LayoutInflater.from(this).inflate(R.layout.pop_assets_status, null, false);
//        useStatusPopWindow=new PopupWindow(statusView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        RadioGroup radioGroup=statusView.findViewById(R.id.pop_assets_rg);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton radbtn = statusView.findViewById(checkedId);
//                Log.i("RadioButton","checkedId为"+checkedId);
//                assets_use_status.setText(radbtn.getText());
//                popWindowUtils.dissPopupWindow(useStatusPopWindow);
//
//            }
//        });
        listAdapter=new ArrayAdapter(AssetsActivity.this, android.R.layout.simple_list_item_single_choice, statusList);

        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(AssetsActivity.this);
        singleChoiceDialog.setTitle("报修单位");
        // 第二个参数是默认选项，此处设置为0

        singleChoiceDialog.setSingleChoiceItems(listAdapter, choiceStatus,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiceStatus=which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("which",which+"");
                        Log.i("choiceId",choiceStatus+"");
                        if(choiceStatus!=-1)
                            assets_use_department.setText(statusList.get(choiceStatus)+"");

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

    //获取使用状况
    public void getStatus(){
        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlname","get_assets_status");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("pageindex","1");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                Log.i("部门数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){

                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        Log.i("数组大小", list.size()+"");
                        for(HashMap<String, String> dsMap:list){

                            //取出想要的数据
                            for(String key:dsMap.keySet()) {

                                if(key.equals("bc_name")){
                                    statusList.add(dsMap.get(key));
                                }


                            }

                        }
                    }
                    initStatusPop(statusList);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(AssetsActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(AssetsActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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


    //备注
    private void initRemarkPop() {
        View remarkView= LayoutInflater.from(this).inflate(R.layout.pop_assets_remark, null, false);
        remarkPopWindow=new PopupWindow(remarkView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_assets_remark_btn_back=remarkView.findViewById(R.id.pop_assets_remark_btn_back);
        Button pop_assets_remark_btn_save=remarkView.findViewById(R.id.pop_assets_remark_btn_save);
        final EditText pop_assets_remark_edit=remarkView.findViewById(R.id.pop_assets_remark_edit);
        pop_assets_remark_edit.setText(assets_remark.getText().toString());

        pop_assets_remark_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(remarkPopWindow);
            }
        });
        pop_assets_remark_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assets_remark.setText(pop_assets_remark_edit.getText().toString().trim());
                popWindowUtils.dissPopupWindow(remarkPopWindow);
            }
        });

    }
    /**
     *
     * 设置日期
     * */
    private void setDate(final TextView view) {
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int sysYear = calendar.get(Calendar.YEAR);
        //月
        int sysMonth = calendar.get(Calendar.MONTH);
        //日
        int sysDay = calendar.get(Calendar.DAY_OF_MONTH);

        //日历控件

        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                int year=y;
                int month=m+1;
                int day=d;
                view.setText(year+"/"+month+"/"+day);
                Log.i("date","当前日期"+year+"/"+month+"/"+day);
            }
        }, sysYear, sysMonth, sysDay);//初始年份，初始月份，初始日期
        dp.show();
    }

    //上传数据
    private void startUpload(final String billid, final String tbname) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AssetsActivity.this);

        normalDialog.setTitle("温馨提示");
        normalDialog.setMessage("确认提交吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i("id:tbname",billid+":"+tbname);
                        Map<String,String> params=new HashMap<>();
                        params.put("a_id", "");

                        params.put("a_card", assets_cardid.getText().toString());
                        params.put("a_number", assets_id.getText().toString());
                        params.put("a_name", assets_name.getText().toString());
                        params.put("a_value", assets_original_value.getText().toString());
                        params.put("a_deprecia", assets_accumulated_depreciation.getText().toString());
                        params.put("a_startdate", assets_start_date.getText().toString());
                        params.put("a_usemonth", assets_use_year.getText().toString());
                        params.put("a_way", assets_depreciation_method.getText().toString());
                        params.put("a_usedep", assets_use_department.getText().toString());
                        params.put("a_residue", "");
                        params.put("a_position", assets_position.getText().toString());
                        params.put("a_entrydate", assets_entry_date.getText().toString());
                        params.put("a_address", assets_address.getText().toString());
                        params.put("a_model", assets_specification_model.getText().toString());
                        params.put("a_status", assets_use_status.getText().toString());
                        params.put("a_remark", assets_remark.getText().toString());

                        params.put("a_cmpid", sp.getString("cu_cmpid","001"));
                        params.put("a_depid", sp.getString("cu_depid","001004"));
                        params.put("a_depname", sp.getString("dep_name","新居工程"));
                        params.put("a_createuid", sp.getString("cu_userid",""));
                        params.put("a_createmen", sp.getString("cu_username",""));
                        params.put("a_createdate", assets_entry_date.getText().toString());
                        params.put("a_updatedate", assets_entry_date.getText().toString());
                        params.put("a_state", "0");
                        params.put("a_billid", billid);
                        params.put("a_tbname", tbname);
                        params.put("sqlcmd", "assets_ins");
                        params.put("datatype", "json");


                        Log.i("upload", "开始上传:" + Contans.uri + "?" + params.toString());
                        myOkhttpClient.uploadImage(params,imgs);


                    }
                });
        //192.168.0.161:8023/sys/aspx/mobilecmd.ashx?sqlname=assets_ins&datatype=json&_validme_=48EB53FC2FAD50F2568ACC7F&pagesize=20&pageindex=1&qry=1&id=1


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

    //获取地址数据
    private void getAddressData(String tbname, String billid, final List<AddressBean.AddressItemBean> addressList) {
        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_search_list");
        params.addBodyParameter("datatype","json");

        params.addBodyParameter("tbname",tbname);
        params.addBodyParameter("depid",sp.getString("cu_depid",""));
        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("id",billid);
        params.addBodyParameter("pageindex","1");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");
        params.addBodyParameter("compid", sp.getString("cu_compid",""));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                Log.i("维修数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        for(HashMap<String, String> dsMap:list){

                            AddressBean.AddressItemBean abean=new AddressBean.AddressItemBean();
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

                            }
                            addressList.add(abean);

                            Log.i("分隔符","------------------------");
                        }
                        Log.i("打印顺序","前");
//
//                        if(addressBean.getArea().size()==0||addressBean.getArea()==null){
//                            addressBean.setArea(addressList);
//                        }else if(addressBean.getCommunity().size()==0||addressBean.getCommunity()==null){
//                            addressBean.setCommunity(addressList);
//                        }
//                        else if(addressBean.getBuilding().size()==0||addressBean.getBuilding()==null){
//                            addressBean.setBuilding(addressList);
//                        }
//                        else if(addressBean.getUnit().size()==0||addressBean.getUnit()==null){
//                            addressBean.setUnit(addressList);
//                        }
//                        else if(addressBean.getFloor().size()==0||addressBean.getFloor()==null){
//                            addressBean.setFloor(addressList);
//                        }
//                        else if(addressBean.getHouse().size()==0||addressBean.getHouse()==null){
//                            addressBean.setHouse(addressList);
//                        }


                    }
                }
                Log.i("打印顺序","前");
                myAddressAdapter.notifyDataSetChanged();


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(AssetsActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(AssetsActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
    @AfterPermissionGranted(CAREMA)//请求码
    private void after() {
        if (EasyPermissions.hasPermissions(this, mPermissions)){
            startCarema(CAREMA);
        }else{
            EasyPermissions.requestPermissions(this,"请给予照相机权限,否则app无法正常运行",CAREMA,mPermissions);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAREMA && resultCode == RESULT_OK) {
            mDatas.add(photoUtils.getPhotoFile().toString());
            notifyDataSetChanged();

            //     final String imagePath = PhotoUtils.parseString(this,imageUri);
            Log.i("开始压缩imagePath=",photoUtils.getPhotoFile().toString());
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Luban.with(AssetsActivity.this)
                            .load(photoUtils.getPhotoFile())                                   // 传入要压缩的图片列表
                            .ignoreBy(100)                                  // 忽略不压缩图片的大小
                            .setCompressListener(new OnCompressListener() { //设置回调
                                @Override
                                public void onStart() {
                                    // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                }

                                @Override
                                public void onSuccess(File file) {
                                    imgs.add(file.toString());

                                    Log.i("压缩成功",mDatas.size()+"");

                                }

                                @Override
                                public void onError(Throwable e) {
                                    // TODO 当压缩过程出现问题时调用
                                }
                            }).launch();    //启动压缩



                }
            }.start();


        }
    }
    private void notifyDataSetChanged() {
        adapter=new MyGridViewAdapter(AssetsActivity.this, mDatas);
        assets_gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

}
