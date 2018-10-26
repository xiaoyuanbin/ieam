package com.hm.ieam.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.hm.ieam.adapter.MyAddressAdapter;
import com.hm.ieam.bean.AddressBean;
import com.hm.ieam.bean.RepairBean;

import com.hm.ieam.utils.Contans;
import com.hm.ieam.utils.JsonUtils;
import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.utils.MyLoadDialog;
import com.hm.ieam.utils.MyOkhttpClient;
import com.hm.ieam.adapter.MyGridViewAdapter;
import com.hm.ieam.utils.PhotoUtils;
import com.hm.ieam.utils.PopWindowUtils;
import com.hm.ieam.R;

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

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> mDatas;    //存放所有图片的集合
    private List<String> imgs;    //上传的所有图片的集合
    String[] mPermissions=new String[]{Manifest.permission.CAMERA
            ,Manifest.permission.READ_PHONE_STATE
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    GridView report_gridview;
    MyGridViewAdapter myAdapter;      //显示图片的adapter

    MyGlideUtils myGlideUtils;
    private static final int CAREMA = 2;   //调用相机请求码
    SharedPreferences sp;

    ArrayMap<String,String> firmMap;
    PopWindowUtils popWindowUtils;
    PhotoUtils photoUtils;   //拍照工具类
    Uri imageUri;    //拍照时获取图片uri
    int imageId;    //编辑图片时的id
  //  private List<String>  imageDescription;    //图片编辑文字信息

    RepairBean repairBean;
    HashMap<String, ArrayList<HashMap<String, String>>> jsonMap; //存放解析json数据的结果
 //   List<AddressBean.AddressItemBean> areaList;
    List<AddressBean.AddressItemBean> communityList;
    List<AddressBean.AddressItemBean> buildingList;
    List<AddressBean.AddressItemBean> unitList;
    List<AddressBean.AddressItemBean> floorList;
    List<AddressBean.AddressItemBean> houseList;
    ListView addresslistview;
    MyAddressAdapter myAddressAdapter;
    int count=1;   //计算地址选择层数
    List<Integer> positions;

    MyLoadDialog myLoadDialog;

    int choiceId=-1;    //报修公司选择
    List<String> firmList;
    ArrayAdapter listAdapter;

    MyOkhttpClient myOkhttpClient;
    LinearLayout report_ll_address;

    LinearLayout report_ll_firm;
    LinearLayout report_ll_type;
    LinearLayout report_ll_position;
    LinearLayout report_ll_time;


    private PopupWindow addressPopWindow;    //报修地址

    private PopupWindow firmPopWindow;    //报修单位
    private PopupWindow typePopWindow;    //报修类型
    private PopupWindow positionPopWindow;    //报修部位
    private PopupWindow imgPopWindow;    //报修部位


    Button report_back;        //巡查页面返回按钮
    Button report_submit;       //提交按钮

    TextView report_address;
    TextView report_firm;
    TextView report_type;
    TextView report_position;
    TextView report_time;


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
//    ImageView address_select_img_go5;

 //   TextView pop_inspect_image_text;  //图片描述


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initDate();
        initView();
        initPopWindow();
        photoUtils=new PhotoUtils(this);
        myOkhttpClient=new MyOkhttpClient(this);
        myLoadDialog=new MyLoadDialog(this);
        myGlideUtils=new MyGlideUtils();
    }



    private void initDate() {

        sp=getSharedPreferences("date",MODE_PRIVATE);
//        String images="";
//        Intent intent=getIntent();
//        if(intent!=null) {
//            repairBean = (RepairBean) intent.getSerializableExtra("RepairBean");
//        }


        imgs=new ArrayList<>();
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
//        for(int i=0;i<20;i++){
//            imageDescription.add("");
//        }
        mDatas=new ArrayList<>();

//        if(repairBean!=null){
//            images=repairBean.getImages();
//
//            if(!"".equals(images)){
//                String[] img=images.split(",");
//                for(int i=0;i<img.length;i++){
//                    Log.i("image",img[i]);
//                    mDatas.add(img[i]);
//                    imgs.add(img[i]);
//                }
//            }
//
//
//            //   imgs.add(inspectBean.getImages());
//        }
//
//        Log.i("size",mDatas.size()+"");
//


    }



    private void initView() {
        report_gridview=findViewById(R.id.report_gridview);

        report_ll_address=findViewById(R.id.report_ll_address);

        report_ll_firm=findViewById(R.id.report_ll_firm);
        report_ll_time=findViewById(R.id.report_ll_time);

        report_ll_type=findViewById(R.id.report_ll_type);
        report_ll_position=findViewById(R.id.report_ll_position);

        report_back=findViewById(R.id.report_back);
        report_submit=findViewById(R.id.report_submit);
        report_address=findViewById(R.id.report_address);

        report_firm=findViewById(R.id.report_firm);
        report_type=findViewById(R.id.report_type);
        report_position=findViewById(R.id.report_position);
        report_time=findViewById(R.id.report_time);

//
//        if(repairBean!=null){
//            report_address.setText(repairBean.getReport_address());
//            report_firm.setText(repairBean.getReport_firm());
//            report_type.setText(repairBean.getReport_type());
//            report_position.setText(repairBean.getReport_position());
//            report_time.setText(repairBean.getReport_date());
//
//
//        }

        report_back.setOnClickListener(this);
        report_submit.setOnClickListener(this);
        report_ll_address.setOnClickListener(this);

        report_ll_firm.setOnClickListener(this);
        report_ll_time.setOnClickListener(this);
        report_ll_type.setOnClickListener(this);
        report_ll_position.setOnClickListener(this);

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
        startActivityForResult(intent, requestCode);
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
                if(imageId<imgs.size()){
                    imgs.remove(imageId);
                }
   //             imageDescription.remove(imageDescription.get(imageId));
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
                String billid="";
                String tbname="";

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

            case R.id.report_ll_firm:
                if(firmList==null||firmList.size()==0){

                    getReportFirm();
                }
                else{
                    initFirmPop(firmList);
                }

                break;
            case R.id.report_ll_type:

                initTypePop();
                popWindowUtils.showPopupWindow(typePopWindow);
                break;
            case R.id.report_ll_position:
                initPositionPop();
                popWindowUtils.showfullPopupWindow(positionPopWindow);
                break;
            case R.id.report_ll_time:
                setDate();
                break;

                default:
                    break;
        }
    }


    private void startUpload(final String billid, final String tbname) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ReportActivity.this);
        normalDialog.setTitle("温馨提示");
        normalDialog.setMessage("确认提交吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        if(imgs.size()>0&&mDatas!=null){
//                            for(int i=0;i<imgs.size();i++){
//
//                                Log.i("imgs",imgs.get(i));
//                                photoUtils.savePhoto();
//                                mDatas.remove(i);
//                                String oldPath=myGlideUtils.getCacheFile(imgs.get(i),ReportActivity.this).toString();
//                                String newPath=photoUtils.copyFile(oldPath,photoUtils.getPhotoFile().toString());
//                                Log.i("oldPath",oldPath);
//                                Log.i("newPath",newPath);
//
//                                if(!newPath.equals("")) {
//                                    mDatas.add(i, newPath);
//                                }
//
//                            }
//
//
//                        }
                        String compid="";
                        if(firmMap!=null&&firmMap.size()>0)
                        {
                            for(String firmname:firmMap.keySet()){
                                if(report_firm.getText().toString().equals(firmname)){
                                    compid=firmMap.get(firmname);
                                }
                            }
                        }
                        Log.i("提交compid，billid，tbname",compid+"-"+billid+"-"+tbname);

                        Map<String,String> params=new HashMap<>();
                  //      if(repairBean==null||repairBean.getId().equals("")){

                            params.put("r_id ","");
                            params.put("r_state","报修");
                            params.put("r_addr",report_address.getText().toString());
                            params.put("r_companyid",compid);
                            params.put("r_type",report_type.getText().toString());
                            params.put("r_billid",billid);
                            params.put("r_tbname",tbname);
                            params.put("r_date",report_time.getText().toString());
                            params.put("r_part",report_position.getText().toString());
                            params.put("r_usersign",sp.getString("cu_userid",""));

                            params.put("r_cmpid", sp.getString("cu_cmpid","001"));
                            params.put("r_depid", sp.getString("cu_depid","001004"));
                            params.put("r_depname", sp.getString("dep_name","物业单位"));
                            params.put("r_createuid", sp.getString("cu_userid",""));
                            params.put("r_createmen", sp.getString("cu_username",""));
                            params.put("r_createdate", report_time.getText().toString());
                            params.put("r_updatedate", report_time.getText().toString());

                            params.put("sqlcmd","mobile_repair_ins");
                            params.put("datatype","json");


//                        }
//                        else if(billid.equals("")){
//
//                            params.put("r_id ",repairBean.getId());
//
//                            params.put("r_companyid",report_firm.getText().toString());
//                            params.put("r_type",report_type.getText().toString());
//
//                            params.put("r_date",report_time.getText().toString());
//                            params.put("r_part",report_position.getText().toString());
//
//                            params.put("sqlcmd","repair_edt");
//                            params.put("datatype","json");
//
//                        }
//                        else {
//                            params.put("r_id ",repairBean.getId());
//                            params.put("r_addr",report_address.getText().toString());
//                            params.put("r_companyid",report_firm.getText().toString());
//                            params.put("r_type",report_type.getText().toString());
//                            params.put("r_billid",billid);
//                            params.put("r_tbname",tbname);
//                            params.put("r_date",report_time.getText().toString());
//                            params.put("r_part",report_position.getText().toString());
//
//
//                            params.put("sqlcmd","repair_edt");
//                            params.put("datatype","json");
//
//
//                        }

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
        final View typeView= LayoutInflater.from(this).inflate(R.layout.pop_report_type,null,false);

        typePopWindow=new PopupWindow(typeView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        RadioGroup type=typeView.findViewById(R.id.pop_report_type_rg);
        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radbtn = typeView.findViewById(checkedId);
                radbtn.setChecked(true);
                Log.i("RadioButton","checkedId为"+checkedId);
                report_type.setText(radbtn.getText());
                popWindowUtils.dissPopupWindow(typePopWindow);
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
//        final View firmView= LayoutInflater.from(this).inflate(R.layout.pop_report_firm, null, false);
//        firmPopWindow=new PopupWindow(firmView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        RadioGroup radioGroup=firmView.findViewById(R.id.pop_report_firm_rg);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton radbtn = firmView.findViewById(checkedId);
//                radbtn.setChecked(true);
//                Log.i("RadioButton","checkedId为"+checkedId);
//                report_firm.setText(radbtn.getText());
//                popWindowUtils.dissPopupWindow(firmPopWindow);
//            }
//        });


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
                    initFirmPop(firmList);
                }
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


            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,communityList);
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

            buildingList.removeAll(buildingList);
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

            unitList.removeAll(unitList);
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
            floorList.removeAll(floorList);
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

            houseList.removeAll(houseList);
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

            myAddressAdapter=new MyAddressAdapter(ReportActivity.this,houseList);
            addresslistview.setAdapter(myAddressAdapter);
            myAddressAdapter.notifyDataSetChanged();


        }
    }

    private void getAddressData(String tbname, String id, final List<AddressBean.AddressItemBean> addressList) {

        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_search_list");
        params.addBodyParameter("datatype","json");

        params.addBodyParameter("tbname",tbname);

        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("id",id);
        params.addBodyParameter("depid",sp.getString("cu_depid",""));
        params.addBodyParameter("pageindex","1");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");

        params.addBodyParameter("compid", sp.getString("cu_compid",""));


        Log.i("compid", sp.getString("cu_compid",""));
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
                report_time.setText(year+"/"+month+"/"+day);
                Log.i("date","当前日期"+year+"/"+month+"/"+day);
            }
        }, sysYear, sysMonth, sysDay);//初始年份，初始月份，初始日期
        dp.show();
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
    /**
     *
     * 调用相机返回结果
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAREMA && resultCode == RESULT_OK) {
//            mDatas.add(photoUtils.getPhotoFile().toString());
//
//            notifyDataSetChanged();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Luban.with(ReportActivity.this)
                            .load(photoUtils.getPhotoFile())                                   // 传入要压缩的图片列表
                            .ignoreBy(100)                                  // 忽略不压缩图片的大小
                            .setCompressListener(new OnCompressListener() { //设置回调
                                @Override
                                public void onStart() {
                                    // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                }

                                @Override
                                public void onSuccess(File file) {
                                //    imgs.add(file.toString());
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
