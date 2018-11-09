package com.hm.ieam.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
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
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hm.ieam.bean.RepairBean;
import com.hm.ieam.utils.Contans;

import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.utils.MyOkhttpClient;

import com.hm.ieam.adapter.MyGridViewAdapter;
import com.hm.ieam.utils.PhotoUtils;
import com.hm.ieam.utils.PopWindowUtils;
import com.hm.ieam.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.xutils.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class RepairWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> mDatas;    //显示所有图片的集合
    private List<String> imgs;    //缓存的所有图片的集合
    private List<String> imgUps;    //上传的所有图片的集合
    private List<String> mPhotots;    //相册的所有图片的集合
    private static final int CAREMA = 2;   //调用相机请求码
    public static final int REQUEST_CODE_CHOOSE=CAREMA+1;    //上传图片

    PopWindowUtils popWindowUtils;
    PhotoUtils photoUtils;   //拍照工具类
    int imageId;    //编辑图片时的id

    SharedPreferences datasp;
    MyOkhttpClient myOkhttpClient;
    GridView repair_write_gridview;
    MyGridViewAdapter adapter;      //显示图片的adapter
    String[] mPermissions=new String[]{Manifest.permission.CAMERA
            ,Manifest.permission.READ_PHONE_STATE
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    Button repair_write_btn_back;
    Button repair_write_btn_submit;

    LinearLayout repair_write_ll_repair_type;
    LinearLayout repair_write_ll_repair_state;
    LinearLayout repair_write_ll_repair_deal;
    LinearLayout repair_write_ll_repair_laborbudget;
    LinearLayout repair_write_ll_repair_stuffbudget;
    LinearLayout repair_write_ll_repair_costbudget;
    LinearLayout repair_write_ll_repair_remark;

    TextView repair_write_repair_address;
    TextView repair_write_repair_type;
    TextView repair_write_repair_state;
    TextView repair_write_repair_deal;
    TextView repair_write_repair_laborbudget;
    TextView repair_write_repair_stuffbudget;
    TextView repair_write_repair_costbudget;
    TextView repair_write_repair_remark;

    TextView repair_write_report_firm;
    TextView repair_write_report_type;
    TextView repair_write_report_address;
    TextView repair_write_report_position;
    TextView repair_write_report_date;


    RepairBean repairBean;


    PopupWindow typePopWindow;    //维修类型
    PopupWindow statePopWindow;
    PopupWindow dealPopWindow;
    PopupWindow laborPopWindow;
    PopupWindow stuffPopWindow;
    PopupWindow costPopWindow;
    PopupWindow remarkPopWindow;
    MyGlideUtils myGlideUtils;

    private PopupWindow imgPopWindow;    //编辑图片
    TextView pop_inspect_image_text;    //图片描述


   // private List<String> imageDescription;    //图片编辑文字信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_write);
        initData();
        initView();
        initPopWindow();
        photoUtils=new PhotoUtils(this);
        myOkhttpClient=new MyOkhttpClient(this);
        myGlideUtils=new MyGlideUtils();
    }

    private void initPopWindow() {
        popWindowUtils=new PopWindowUtils(this);
    }

    private void initData() {
        datasp=getSharedPreferences("date",MODE_PRIVATE);
        String images="";
        Intent intent=getIntent();
        if(intent!=null) {
            repairBean = (RepairBean) intent.getSerializableExtra("RepairBean");
        }


    //    imageDescription=new ArrayList<>();
        imgs=new ArrayList<>();
//        for(int i=0;i<20;i++){
//            imageDescription.add("");
//
//        }
        imgUps=new ArrayList<>();

        mDatas=new ArrayList<>();
        mPhotots=new ArrayList<>();
        if(repairBean!=null){
            images=repairBean.getImages();

            if(!"".equals(images)){
                String[] img=images.split(",");
                for(String s:img){
                    imgs.add(s);
                }
            }


        }




    }

    private void initView() {
        repair_write_gridview=findViewById(R.id.repair_write_gridview);
        repair_write_btn_back=findViewById(R.id.repair_write_btn_back);
        repair_write_btn_submit=findViewById(R.id.repair_write_btn_submit);


        repair_write_ll_repair_type=findViewById(R.id.repair_write_ll_repair_type);
        repair_write_ll_repair_state=findViewById(R.id.repair_write_ll_repair_state);
        repair_write_ll_repair_deal=findViewById(R.id.repair_write_ll_repair_deal);
        repair_write_ll_repair_laborbudget=findViewById(R.id.repair_write_ll_repair_laborbudget);
        repair_write_ll_repair_stuffbudget=findViewById(R.id.repair_write_ll_repair_stuffbudget);
        repair_write_ll_repair_costbudget=findViewById(R.id.repair_write_ll_repair_costbudget);
        repair_write_ll_repair_remark=findViewById(R.id.repair_write_ll_repair_remark);

        repair_write_repair_address=findViewById(R.id.repair_write_repair_address);
        repair_write_repair_type=findViewById(R.id.repair_write_repair_type);
        repair_write_repair_state=findViewById(R.id.repair_write_repair_state);
        repair_write_repair_deal=findViewById(R.id.repair_write_repair_deal);
        repair_write_repair_laborbudget=findViewById(R.id.repair_write_repair_laborbudget);
        repair_write_repair_stuffbudget=findViewById(R.id.repair_write_repair_stuffbudget);
        repair_write_repair_costbudget=findViewById(R.id.repair_write_repair_costbudget);
        repair_write_repair_remark=findViewById(R.id.repair_write_repair_remark);

        repair_write_report_firm=findViewById(R.id.repair_write_report_firm);
        repair_write_report_type=findViewById(R.id.repair_write_report_type);
        repair_write_report_address=findViewById(R.id.repair_write_report_address);
        repair_write_report_position=findViewById(R.id.repair_write_report_position);
        repair_write_report_date=findViewById(R.id.repair_write_report_date);






        adapter=new MyGridViewAdapter(this,mDatas);
        repair_write_gridview.setAdapter(adapter);
        repair_write_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("position", position + "");
                imageId = position;
                if (position == mDatas.size()||mDatas.size()==0) {

                    applyPermission();

                } else {
                    startEditImage(mDatas.get(position));

                }
            }

        });

        if(repairBean!=null){
            repair_write_report_firm.setText(repairBean.getReport_firm());
            repair_write_report_type.setText(repairBean.getReport_type());
            repair_write_report_address.setText(repairBean.getReport_address());
            repair_write_report_position.setText(repairBean.getReport_position());
            repair_write_report_date.setText(repairBean.getReport_date());
            repair_write_repair_address.setText(repairBean.getReport_address());
            repair_write_repair_type.setText(repairBean.getRepair_type());
            repair_write_repair_state.setText(repairBean.getRepair_state());
            repair_write_repair_deal.setText(repairBean.getRepair_deal());
            repair_write_repair_laborbudget.setText(repairBean.getRepair_laborbudget());
            repair_write_repair_stuffbudget.setText(repairBean.getRepair_stuffbudget());
            repair_write_repair_costbudget.setText(repairBean.getRepair_costbudget());
            repair_write_repair_remark.setText(repairBean.getRepair_remark());

        }else{
            repair_write_repair_type.setText("入户");
        }


        repair_write_btn_back.setOnClickListener(this);
        repair_write_btn_submit.setOnClickListener(this);

        repair_write_ll_repair_type.setOnClickListener(this);
        repair_write_ll_repair_state.setOnClickListener(this);
        repair_write_ll_repair_deal.setOnClickListener(this);
        repair_write_ll_repair_laborbudget.setOnClickListener(this);
        repair_write_ll_repair_stuffbudget.setOnClickListener(this);
        repair_write_ll_repair_costbudget.setOnClickListener(this);
        repair_write_ll_repair_remark.setOnClickListener(this);



    }
    //展示图片编辑窗口

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
   //     pop_inspect_image_text.setText(imageDescription.get(imageId));

        pop_inspect_image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(imgPopWindow);
           //     imageDescription.set(imageId,pop_inspect_image_text.getText().toString().trim());
            }
        });
        pop_inspect_image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(imgPopWindow);
                mDatas.remove(imageId);


           //     imageDescription.remove(imageDescription.get(imageId));
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
//    //图片编辑
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
//                imageDescription.set(imageId,pop_edit_img_edasit.getText().toString().trim());
//                pop_inspect_image_text.setText(pop_edit_img_edit.getText().toString().trim());
//            }
//        });
//
//    }

    //拍照时动态申请权限
    public void applyPermission() {
        if (EasyPermissions.hasPermissions(this, mPermissions)){
            showListDialog();
        }else{
//第二个参数是提示信息
            EasyPermissions.requestPermissions(this,"请给予照相机权限,否则app无法正常运行",CAREMA,mPermissions);
        }

    }
    private void showListDialog() {
        final String[] items = { "拍照","从相册选择"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(RepairWriteActivity.this);
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

    //调用相机拍照
    public void startCarema() {
        Uri imageUri=photoUtils.getPhotoUri();
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAREMA);
    }

    private void upPhoto() {
        Matisse.from(RepairWriteActivity.this)
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.repair_write_btn_back:
                finish();
                break;
            case R.id.repair_write_btn_submit:
                if(repair_write_repair_deal.getText().equals("")||repair_write_repair_state.getText().equals("报修")
                        ||repair_write_repair_type.getText().equals("")){
                    Toast.makeText(RepairWriteActivity.this,"请填写完后再提交",Toast.LENGTH_SHORT).show();

                }
                else{
                    imgUps.clear();
                    startUpload();
                }

                break;


            case R.id.repair_write_ll_repair_type:

                initTypePop();
                popWindowUtils.showPopupWindow(typePopWindow);
                break;
            case R.id.repair_write_ll_repair_state:

                initStatePop();
                popWindowUtils.showPopupWindow(statePopWindow);

                break;
            case R.id.repair_write_ll_repair_deal:

                initDealPop();
                popWindowUtils.showfullPopupWindow(dealPopWindow);

                break;
            case R.id.repair_write_ll_repair_laborbudget:

                initLaborPop();
                popWindowUtils.showfullPopupWindow(laborPopWindow);
                break;
            case R.id.repair_write_ll_repair_stuffbudget:

                initStuffPop();
                popWindowUtils.showfullPopupWindow(stuffPopWindow);
                break;
            case R.id.repair_write_ll_repair_costbudget:

                initCostPop();
                popWindowUtils.showfullPopupWindow(costPopWindow);
                break;
            case R.id.repair_write_ll_repair_remark:
                initRemarkPop();
                popWindowUtils.showfullPopupWindow(remarkPopWindow);
                break;

                default:
                    break;


        }

    }


    //维修类型
    private void initTypePop() {
        final View typeView= LayoutInflater.from(this).inflate(R.layout.pop_repair_write_repair_type, null, false);
        typePopWindow=new PopupWindow(typeView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        RadioGroup radioGroup=typeView.findViewById(R.id.pop_repair_rg_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = typeView.findViewById(checkedId);
                Log.i("RadioButton","checkedId为"+checkedId);
                repair_write_repair_type.setText(radbtn.getText());
                popWindowUtils.dissPopupWindow(typePopWindow);

            }
        });

    }
    //维修状态
    private void initStatePop() {
        final View stateView= LayoutInflater.from(this).inflate(R.layout.pop_repair_write_repair_state, null, false);
        statePopWindow=new PopupWindow(stateView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        RadioGroup radioGroup=stateView.findViewById(R.id.pop_repair_rg_state);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = stateView.findViewById(checkedId);
                Log.i("RadioButton","checkedId为"+checkedId);
                repair_write_repair_state.setText(radbtn.getText());
                popWindowUtils.dissPopupWindow(statePopWindow);

            }
        });

    }
    //故障原因及处理方法
    private void initDealPop() {
        View view= LayoutInflater.from(this).inflate(R.layout.pop_repair_write_reason, null, false);
        dealPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_repair_write_reson_btn_back=view.findViewById(R.id.pop_repair_write_reson_btn_back);
        Button pop_repair_write_reson_btn_save=view.findViewById(R.id.pop_repair_write_reson_btn_save);
        final EditText pop_repair_write_reason_edit=view.findViewById(R.id.pop_repair_write_reason_edit);

        pop_repair_write_reason_edit.setText(repair_write_repair_deal.getText());

        pop_repair_write_reson_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(dealPopWindow);
            }
        });
        pop_repair_write_reson_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(dealPopWindow);
                repair_write_repair_deal.setText(pop_repair_write_reason_edit.getText().toString().trim());
            }
        });
    }
    //维修用功预算
    private void initLaborPop() {
        View empView= LayoutInflater.from(this).inflate(R.layout.pop_repair_write_emp, null, false);
        laborPopWindow=new PopupWindow(empView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_repair_write_emp_btn_back=empView.findViewById(R.id.pop_repair_write_emp_btn_back);
        Button pop_repair_write_emp_btn_save=empView.findViewById(R.id.pop_repair_write_emp_btn_save);
        final EditText pop_repair_write_emp_edit=empView.findViewById(R.id.pop_repair_write_emp_edit);

        pop_repair_write_emp_edit.setText(repair_write_repair_laborbudget.getText().toString());

        pop_repair_write_emp_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(laborPopWindow);
            }
        });
        pop_repair_write_emp_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(laborPopWindow);
                repair_write_repair_laborbudget.setText(pop_repair_write_emp_edit.getText().toString().trim());
            }
        });
    }
    //维修材料预算
    private void initStuffPop() {
        View view= LayoutInflater.from(this).inflate(R.layout.pop_repair_write_material, null, false);
        stuffPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_repair_write_material_btn_back=view.findViewById(R.id.pop_repair_write_material_btn_back);
        Button pop_repair_write_material_btn_save=view.findViewById(R.id.pop_repair_write_material_btn_save);
        final EditText pop_repair_write_material_edit=view.findViewById(R.id.pop_repair_write_material_edit);

        pop_repair_write_material_edit.setText(repair_write_repair_stuffbudget.getText().toString());

        pop_repair_write_material_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(stuffPopWindow);
            }
        });
        pop_repair_write_material_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(stuffPopWindow);
                repair_write_repair_stuffbudget.setText(pop_repair_write_material_edit.getText().toString().trim());
            }
        });

    }
    //维修费用预算
    private void initCostPop() {
        View view= LayoutInflater.from(this).inflate(R.layout.pop_repair_write_repair_cost, null, false);
        costPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_repair_write_cost_btn_back=view.findViewById(R.id.pop_repair_write_cost_btn_back);
        Button pop_repair_write_cost_btn_save=view.findViewById(R.id.pop_repair_write_cost_btn_save);
        final EditText pop_repair_write_cost_edit=view.findViewById(R.id.pop_repair_write_cost_edit);

        pop_repair_write_cost_edit.setText(repair_write_repair_costbudget.getText().toString());

        pop_repair_write_cost_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(costPopWindow);
            }
        });
        pop_repair_write_cost_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(costPopWindow);
                repair_write_repair_costbudget.setText(pop_repair_write_cost_edit.getText().toString().trim());
            }
        });
    }
    //维修备注
    private void initRemarkPop() {
        View view= LayoutInflater.from(this).inflate(R.layout.pop_repair_write_repair_remark, null, false);
        remarkPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_repair_write_remark_btn_back=view.findViewById(R.id.pop_repair_write_remark_btn_back);
        Button pop_repair_write_remark_btn_save=view.findViewById(R.id.pop_repair_write_remark_btn_save);
        final EditText pop_repair_write_remark_edit=view.findViewById(R.id.pop_repair_write_remark_edit);

        pop_repair_write_remark_edit.setText(repair_write_repair_remark.getText());

        pop_repair_write_remark_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(remarkPopWindow);
            }
        });
        pop_repair_write_remark_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(remarkPopWindow);
                repair_write_repair_remark.setText(pop_repair_write_remark_edit.getText().toString().trim());
            }
        });

    }

    //开始上传数据到服务器
    private void startUpload() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(RepairWriteActivity.this);

        normalDialog.setTitle("温馨提示");
        normalDialog.setMessage("确认提交吗？");
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imgUps.addAll(mDatas);
//                        if(mDatas.size()>0){
//                            for(int i=0;i<mDatas.size();i++)
//                            imgUps.add(mDatas.get(i));
//                        }
                        if(imgs.size()>0){
                            for(int i=0;i<imgs.size();i++){

                                photoUtils.savePhoto();
                         //       mDatas.remove(i);
                                File oldPath=myGlideUtils.getCacheFile(imgs.get(i),RepairWriteActivity.this);
                                String newPath="";
                                if(oldPath!=null){
                                    newPath=photoUtils.copyFile(oldPath.toString(),new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                            + "/ieam/image/"+datasp.getString("c_name","")+System.currentTimeMillis() + ".png").toString());
                                }

                                Log.i("newPath",newPath);

                                if(!newPath.equals("")) {
                                    imgUps.add(i, newPath);
                                }

                            }
                        }
                        Log.i("size",imgUps.size()+"");
                        Map<String,String> params=new HashMap<>();
                        params.put("r_id ",repairBean.getId());
                        params.put("r_state",repair_write_repair_state.getText().toString());

                        params.put("r_faultdispos",repair_write_repair_deal.getText().toString());

                        params.put("r_addr",repair_write_report_address.getText().toString());
                        params.put("r_rptype",repair_write_repair_type.getText().toString());
                        params.put("r_laborbudget",repair_write_repair_laborbudget.getText().toString());


                        params.put("r_stuffbudget",repair_write_repair_stuffbudget.getText().toString());
                        params.put("r_costbudget",repair_write_repair_costbudget.getText().toString());

                        params.put("r_remark",repair_write_repair_remark.getText().toString());

                        params.put("sqlcmd","repair_edt");
                        params.put("datatype","json");



                        Log.i("提交uri",Contans.uri+"?"+params.toString());
                        myOkhttpClient.uploadImage(params,imgUps);


                    }
                });
        normalDialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }


    //权限申请回掉
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
    //申请权限后自动回掉
    @AfterPermissionGranted(CAREMA)//请求码
    private void after() {
        if (EasyPermissions.hasPermissions(this, mPermissions)){
            showListDialog();
        }else{
            EasyPermissions.requestPermissions(this,"请给予照相机权限,否则app无法正常运行",CAREMA,mPermissions);
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
                Luban.with(RepairWriteActivity.this)
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
                Luban.with(RepairWriteActivity.this)
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
        adapter=new MyGridViewAdapter(RepairWriteActivity.this, mDatas);
        repair_write_gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myGlideUtils.clearImageAllCache(RepairWriteActivity.this);
    }
}
