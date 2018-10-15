package com.hm.ieam.activity;


import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.PopupWindow;

import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

import com.baidu.mapapi.map.MapView;

import com.bumptech.glide.Glide;
import com.hm.ieam.adapter.MyAssetsAdapter;
import com.hm.ieam.adapter.MyRepGridAdapter;
import com.hm.ieam.adapter.MyRepairAdapter;
import com.hm.ieam.adapter.MyReportAdapter;
import com.hm.ieam.bean.AssetsBean;

import com.hm.ieam.bean.UpdateInfo;
import com.hm.ieam.utils.APKVersionCodeUtils;
import com.hm.ieam.utils.AppInnerDownLoder;
import com.hm.ieam.utils.Contans;
import com.hm.ieam.adapter.MyInspectAdapter;
import com.hm.ieam.bean.InspectBean;
import com.hm.ieam.utils.JsonUtils;
import com.hm.ieam.utils.MyBaiduMap;
import com.hm.ieam.utils.MyGlideUtils;
import com.hm.ieam.utils.MyLoadDialog;
import com.hm.ieam.utils.PopWindowUtils;
import com.hm.ieam.R;
import com.hm.ieam.bean.RepairBean;
import com.hm.ieam.widght.XListView;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,SearchView.OnQueryTextListener {
    XListView inspectListView;
    XListView reportListView;
    XListView repairListView;
    XListView assetsListView;

    List<String> mDatas;        //存放图片路径
    MyInspectAdapter myInspectAdapter;   //我的巡查适配器
    MyReportAdapter myReportAdapter;     //我的报修适配器
    MyRepairAdapter myRepairAdapter;     //我的维修适配器
    MyAssetsAdapter myAssetsAdapter;     //我的固定资产

    MyRepGridAdapter adapter;          //报修详情适配器
    String[] mPermissions=new String[]{Manifest.permission.READ_PHONE_STATE
            ,Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public final static int INSTALL_APK_REQUESTCODE = 123;   //安装权限code


    int imageId;

    String tb_name;    //删除巡查
    HashMap<String, ArrayList<HashMap<String, String>>> jsonMap; //存放解析json数据的结果
    public static final int SCAN=1;      //扫码
    public static final int REPORT=2;
    public static final int REPAIR=REPORT+1;
    public static final int INSPECT=REPAIR+1;
    public static final int LOGIN=INSPECT+1;
    public static final int REQUEST_CODE_CHOOSE=LOGIN+1;
    public static final int HOUSE=REQUEST_CODE_CHOOSE+1;
    public static final int EQUIPMENT=HOUSE+1;
    public static final int LOCATION=EQUIPMENT+1;
    int state=0;          //跳转界面
    int inspect=1;   //巡查列表页数
    int report=1;    //报修列表页数
    int repair=1;    //维修列表页数
    int assets=1;    //固定资产列表页数
    int obj=0;          //巡查对象

    boolean isUpdate=false;
    Uri iconUri;         //上传头像
    MyLoadDialog myLoadDialog;     //加载对话框
    private SharedPreferences sp;
    boolean isLogin=false;
    private MapView mMapView;       //显示地图
    PopWindowUtils popWindowUtils;    //窗口工具类


    TextView mine_tv_name;
    TextView mine_tv_firm_name;
    ImageView  pop_msg_img_photo;   //用户头像
    ImageView img_mine;
    private PopupWindow minePopWindow;   //弹出我的窗口

    private PopupWindow msgPopWindow;     //个人资料
    private PopupWindow inspectPopWindow;     //巡查弹出窗
    private PopupWindow mInspectPopWindow;     //我的巡查
    private PopupWindow mRepairPopWindow;     //我的维修
    private PopupWindow mReportPopWindow;     //我的报修
    private PopupWindow mAssetsPopWindow;     //我的小区
    private PopupWindow mSettingPopWindow;     //设置
    private PopupWindow mWebPopWindow;     //


    private PopupWindow imgPopWindow;
    private PopupWindow detailPopWindow;
    MyBaiduMap myBaiduMap;

    WebView web;
    SearchView main_searchView;
    MyGlideUtils myGlideUtils;

    RelativeLayout rl_top;

    ImageView main_iv_menu;
    List<RepairBean> reportList;    //维修记录
    List<RepairBean> repairList;    //维修记录
    List<InspectBean> inspectList;   //巡查记录
    List<AssetsBean> assetsList;   //巡查记录
    UpdateInfo info;
    AlertDialog.Builder mDialog;
    String versionName;   //版本名称
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

   //     String versionCode = APKVersionCodeUtils.getVersionCode(this) + "";
        versionName = APKVersionCodeUtils.getVerName(this);
        Log.i("versionName",versionName);
        check(versionName);


        initDate();
        initView();
        initMap();

        initDate();

        initPopupWindow();
        myGlideUtils=new MyGlideUtils();
        myLoadDialog=new MyLoadDialog(this);


    }

    //判断下载状态
    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //app更新
    private void forceUpdate(final Context context, final String downUrl, final String updateinfo) {
            mDialog = new AlertDialog.Builder(context);
            mDialog.setTitle("应用升级");
            mDialog.setMessage(updateinfo);

            mDialog.setCancelable(false);
            mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("更新", "开始更新" + downUrl);
//                fileDownloadManager=FileDownloadManager.getInstance(MainActivity.this);
//                long id=fileDownloadManager.startDownload(downUrl,"应用升级","正在下载安装包，请稍后",info.getName());
                    if (canDownloadState()) {

                        AppInnerDownLoder.downLoadApk(MainActivity.this, downUrl, info.getName());
                    } else {
                        Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }


                }
            });
            mDialog.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("以后再说", "以后再说" + downUrl);
//                if (!canDownloadState()) {
//                    showDownloadSetting();
//                    return;
//                }
//                //      DownLoadApk.download(MainActivity.this,downUrl,updateinfo,appName);
//                AppInnerDownLoder.downLoadApk(MainActivity.this,downUrl,appName);
                }
            });
            mDialog.show();

    }





    //检查app更新
    private void check(String versionName){
        info=new UpdateInfo();
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_version_list");
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("id","");
        params.addBodyParameter("rtnds","2");
        params.addBodyParameter("version",versionName);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Log.i("UpdateInfo",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){

                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        for(HashMap<String, String> dsMap:list){


                            //取出想要的数据
                            for(String key:dsMap.keySet()) {
                                if(key.equals("v_id")) {

                                    isUpdate=true;
                                    info.setId(dsMap.get(key));

                                }
                                if(key.equals("v_name")) {

                                    info.setName(dsMap.get(key));

                                }
                                if(key.equals("v_number")) {

                                    info.setVersion(dsMap.get(key));

                                }

                                if(key.equals("down_src")) {

                                    info.setUrl(dsMap.get(key));
                                }
                                if(key.equals("v_content")) {
                                    info.setContent(dsMap.get(key));

                                }
                                if(key.equals("v_size")) {
                                    info.setSize(dsMap.get(key));


                                }
                            }

                        }

                    }

                }

                if(isUpdate) {
                    forceUpdate(MainActivity.this,  info.getUrl(), info.getContent());
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(MainActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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


    private void initDate() {
        sp= getSharedPreferences("date",MODE_PRIVATE);
        repairList=new ArrayList<>();
        inspectList=new ArrayList<>();
        reportList=new ArrayList<>();
        assetsList=new ArrayList<>();


        mDatas=new ArrayList<>();



    }


    private void initView() {

        main_searchView=findViewById(R.id.main_searchView);
        if (main_searchView != null) {
            try {        //--拿到字节码
                Class<?> argClass = main_searchView.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(main_searchView);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
                mView.clearFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mMapView = findViewById(R.id.mmap);
        Button main_btn_inspect=findViewById(R.id.main_btn_inspect);
        ImageView main_btn_repair=findViewById(R.id.main_btn_repair);
        Button main_btn_report=findViewById(R.id.main_btn_report);
        Button main_btn_mine=findViewById(R.id.main_btn_mine);
        Button main_btn_scan=findViewById(R.id.main_btn_scan);
        rl_top=findViewById(R.id.rl_top);
   //     main_iv_msg=findViewById(R.id.main_iv_msg);
  //      main_iv_menu=findViewById(R.id.main_iv_menu);



        main_btn_inspect.setOnClickListener(this);
        main_btn_repair.setOnClickListener(this);
        main_btn_report.setOnClickListener(this);
        main_btn_mine.setOnClickListener(this);
        main_btn_scan.setOnClickListener(this);
//        main_iv_msg.setOnClickListener(this);
//        main_iv_menu.setOnClickListener(this);
        main_searchView.setOnQueryTextListener(this);

    }

    /**
     * 初始化地图
     * */
    private void initMap() {

        myBaiduMap=new MyBaiduMap(mMapView,getApplicationContext());
        myBaiduMap.initLocation();
        applyPermission(LOCATION);

    }
    /**
     *
     * 初始化popwindow
     * */
    private void initPopupWindow() {

        //     View slidewView=LayoutInflater.from(this).inflate(R.layout.pop_right, null, false);

        popWindowUtils=new PopWindowUtils(this);

    }
    //用户输入字符时激发该方法
    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText))
        {

            //内容为空进入
         //   Toast.makeText(this, "输入的字符", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //输了内容之后进入，还有当删除一个字符也会进入前提输入框了不为空。
         //   Toast.makeText(this, "你输入的内容为"+newText, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    //单击三角搜索按钮时激发该方法，如果输入框为空则不调用
    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("搜索",query);
        myBaiduMap.searchPosition(query);
     //   Toast.makeText(this, "你点击了搜索框！", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_btn_inspect:

                Log.i("点击","巡查");
                getIsLogin();
                if(isLogin) {
                    if(sp.getString("dep_name","0").equals("维修单位")){
                        Toast.makeText(MainActivity.this,"您没有权限执行该功能",Toast.LENGTH_SHORT).show();
                    }else {
                        initInspectPop();
                        state = INSPECT;
                        popWindowUtils.showPopupWindow(inspectPopWindow);
                    }
                }
                else {
                    goLogin();
                }
                break;
            case  R.id.main_btn_repair:
                getIsLogin();
                if(isLogin) {
                    if(sp.getString("dep_name","0").equals("物业单位")){
                        Toast.makeText(MainActivity.this,"您没有权限执行该功能",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        state = REPAIR;
                        start();
                    }
                }
                else {
                    goLogin();
                }
                break;
            case R.id.main_btn_report:
                getIsLogin();
                if(isLogin) {
                    if(sp.getString("dep_name","0").equals("维修单位")){
                        Toast.makeText(MainActivity.this,"您没有权限执行该功能",Toast.LENGTH_SHORT).show();
                    }else {
                        state = REPORT;
                        start();
                    }
                }
                else {
                    goLogin();
                }

                break;

            case R.id.main_btn_mine:
                getIsLogin();
                if(isLogin) {
                    Log.i("点击", "我的");
         //           getInspectDate();
                    initMinePop();
                    popWindowUtils.showfullPopupWindow(minePopWindow);
                }
                else {
                    goLogin();
                }
                break;

            case R.id.main_btn_scan:
               // initActionPop();
                Toast.makeText(this,"暂不支持该功能",Toast.LENGTH_SHORT).show();

              //  popWindowUtils.showPopupWindow(actionPopWindow);
                break;
//            case R.id.main_iv_msg:
//                Toast.makeText(this,"该功能正在开发中",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.main_iv_menu:
//                initWebPop();
//                popWindowUtils.showfullPopupWindow(mWebPopWindow);
//                break;

                /**
                 * 主界面窗口点击事件
                 * */

            case R.id.pop_main_inspect_house:
                Log.i("点击巡查","选择房屋");
                obj=HOUSE;
                popWindowUtils.dissPopupWindow(inspectPopWindow);
                start();
                break;
            case R.id.pop_main_inspect_equipment:
                Log.i("点击巡查","选择设备");
                obj=EQUIPMENT;
                popWindowUtils.dissPopupWindow(inspectPopWindow);
                start();
                break;
            case R.id.pop_main_inspect_property:
                Log.i("点击巡查","选择资产");
                popWindowUtils.dissPopupWindow(inspectPopWindow);
                startAssetsActivity();
                break;


/*
            case R.id.pop_btn_report:
                Log.i("点击","选择报修，开始扫码");
                applyPermission(SCAN);
                popWindowUtils.dissPopupWindow(actionPopWindow);
                break;
            case R.id.pop_btn_inspect:
                Log.i("点击","选择巡查，开始扫码");
                applyPermission(SCAN);
                popWindowUtils.dissPopupWindow(actionPopWindow);
                break;
            case R.id.pop_btn_repair:
                Log.i("点击","选择维修，开始扫码");
                applyPermission(SCAN);
                popWindowUtils.dissPopupWindow(actionPopWindow);
                break;
*/
                default:
        }

    }

    private void initWebPop() {
        View webView=LayoutInflater.from(this).inflate(R.layout.pop_main_webview, null, false);
        mWebPopWindow=new PopupWindow(webView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        web=webView.findViewById(R.id.pop_main_web);
        WebSettings webSettings = web.getSettings();        //设置WebView属性，能够执行Javascript脚本
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);// 页面支持缩放：
        web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web.setHorizontalScrollBarEnabled(false);
        web.setHorizontalScrollbarOverlay(true);

        web.loadUrl(Contans.statistical);          //设置Web视图
        web.setWebViewClient(new webViewClient ());


    }

    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView web, String url) {
            web.loadUrl(url);
            return true;
        }
    }

    /**
     *
     * 判断是否需要登录
     * */
    private void getIsLogin() {

        Log.i("login",sp.getString("login","null"));
        if(sp.getString("login","null").equals("null")){
            isLogin=false;
            Log.i("isLogin","false");

        }
        else{
            isLogin=true;
            Log.i("isLogin","true");
        }
    }

    /**
     *
     * 登录成功后保存登录状态
     * */
    private void setIsLogin(){


        sp.edit().putString("login","1").commit();


        Log.i("login",sp.getString("login","null"));

    }
    /**
     *
     * 前往登陆界面
     *
     * */
    private void goLogin() {

        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivityForResult(intent,LOGIN);

    }


    /**
     *
     * 初始化我的信息界面
     * */
    private void initMsgPop() {
        View msgView=LayoutInflater.from(this).inflate(R.layout.pop_mymsg, null, false);
        msgPopWindow=new PopupWindow(msgView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button msg_btn_back=msgView.findViewById(R.id.msg_btn_back);
        LinearLayout pop_msg_ll_photo=msgView.findViewById(R.id.pop_msg_ll_photo);
  //      LinearLayout pop_msg_ll_name=msgView.findViewById(R.id.pop_msg_ll_name);
  //      LinearLayout pop_msg_ll_firm=msgView.findViewById(R.id.pop_msg_ll_firm);

        pop_msg_img_photo=msgView.findViewById(R.id.pop_msg_img_photo);
        TextView  pop_msg_tv_name=msgView.findViewById(R.id.pop_msg_tv_name);
        TextView  pop_msg_tv_firm=msgView.findViewById(R.id.pop_msg_tv_firm);
        pop_msg_img_photo.setImageDrawable(img_mine.getDrawable());

        pop_msg_ll_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upPhoto();
            }
        });
        pop_msg_tv_name.setText(mine_tv_name.getText().toString());
        pop_msg_tv_firm.setText(mine_tv_firm_name.getText().toString());

  /*      pop_msg_ll_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        pop_msg_ll_firm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
     */
        msg_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(msgPopWindow);
            }
        });

    }

    /**
     *
     * 上传头像
     * */
    private void upPhoto() {
        Matisse.from(MainActivity.this)
                .choose(MimeType.ofAll())//ofAll()
                .theme(R.style.Matisse_Zhihu)//主题，夜间模式R.style.Matisse_Dracula
                .countable(true)//是否显示选中数字
                .capture(true)//是否提供拍照功能
                .captureStrategy(new CaptureStrategy(true, "com.hm.ieam.provider"))//存储地址
                .maxSelectable(1)//最大选择数
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))//筛选条件
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//屏幕方向
                .thumbnailScale(0.85f)//缩放比例
                .imageEngine(myGlideUtils)//图片加载方式
                .forResult(REQUEST_CODE_CHOOSE);//请求码

    }

  /*  private void initActionPop() {

        View actionView=LayoutInflater.from(this).inflate(R.layout.pop_action, null, false);
        actionPopWindow=new PopupWindow(actionView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        Button pop_btn_report=actionView.findViewById(R.id.pop_btn_report);
        Button pop_btn_inspect=actionView.findViewById(R.id.pop_btn_inspect);
        Button pop_btn_repair=actionView.findViewById(R.id.pop_btn_repair);

        pop_btn_report.setOnClickListener(this);
        pop_btn_inspect.setOnClickListener(this);
        pop_btn_repair.setOnClickListener(this);


    }

*/

    /**
     *
     * 初始化我的界面
     * */
    private void initMinePop() {
        View mineView= LayoutInflater.from(this).inflate(R.layout.pop_mine_layout, null, false);
        minePopWindow=new PopupWindow(mineView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button mine_btn_back=mineView.findViewById(R.id.mine_btn_back);
        View mine_rl_me=mineView.findViewById(R.id.mine_rl_me);
        View mine_ll_assets=mineView.findViewById(R.id.mine_ll_assets);
        View mine_ll_reapir=mineView.findViewById(R.id.mine_ll_reapir);
        View mine_ll_report=mineView.findViewById(R.id.mine_ll_report);
        View mine_ll_inspect=mineView.findViewById(R.id.mine_ll_inspect);
        View mine_ll_setting=mineView.findViewById(R.id.mine_ll_setting);
        mine_tv_name=mineView.findViewById(R.id.mine_tv_name);
        mine_tv_firm_name=mineView.findViewById(R.id.mine_tv_firm_name);
        img_mine=mineView.findViewById(R.id.img_mine);

        initIcon();
        mine_tv_name.setText(sp.getString("cu_username",""));
        if(sp.getString("cu_compid","").equals("201808070001")) {
            mine_tv_firm_name.setText("中锦公司");
        }
        else if(sp.getString("cu_compid","").equals("201808070002")){
            mine_tv_firm_name.setText("睿华公司");
        }
        else if(sp.getString("cu_compid","").equals("201808070003")){
            mine_tv_firm_name.setText("青羊公司");
        }
        else if(sp.getString("cu_compid","").equals("201808070004")){
            mine_tv_firm_name.setText("金信源公司");
        }
        else{
            mine_tv_firm_name.setText("土地公司");
        }
        mine_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popWindowUtils.dissPopupWindow(minePopWindow);
            }
        });
        mine_rl_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMsgPop();
                popWindowUtils.showfullPopupWindow(msgPopWindow);
            }
        });

        mine_ll_assets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.getString("dep_name","0").equals("维修单位")){
                    Toast.makeText(MainActivity.this,"您没有权限执行该功能",Toast.LENGTH_SHORT).show();
                }else {
                    assetsList.removeAll(assetsList);

                    getAssetsData(1);
                    initMyAssets();
                    popWindowUtils.showfullPopupWindow(mAssetsPopWindow);
                }
            }
        });
        mine_ll_reapir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.getString("dep_name","0").equals("物业单位")){
                    Toast.makeText(MainActivity.this,"您没有权限执行该功能",Toast.LENGTH_SHORT).show();
                }else {
                    repairList.removeAll(repairList);

                    getRepairDate(1);
                    initMyRepairPop();
                    popWindowUtils.showfullPopupWindow(mRepairPopWindow);
                }

            }
        });
        mine_ll_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.getString("dep_name","0").equals("维修单位")){
                    Toast.makeText(MainActivity.this,"您没有权限执行该功能",Toast.LENGTH_SHORT).show();
                }else {
                    reportList.removeAll(reportList);

                    getReportDate(1);
                    initMyReportPop();
                    popWindowUtils.showfullPopupWindow(mReportPopWindow);
                }
            }
        });
        mine_ll_inspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.getString("dep_name","0").equals("维修单位")){
                    Toast.makeText(MainActivity.this,"您没有权限执行该功能",Toast.LENGTH_SHORT).show();
                }else {
                    inspectList.removeAll(inspectList);

                    getInspectData(1);
                    initMyInspectPop();
                    popWindowUtils.showfullPopupWindow(mInspectPopWindow);
                }
            }
        });
        mine_ll_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSettingPop();
                popWindowUtils.showfullPopupWindow(mSettingPopWindow);
            }
        });


    }
    /**
     *
     * 初始化头像信息
     * */
    private void initIcon() {

        Log.i("login",sp.getString("MyIcon","null"));


        if(sp.getString("MyIcon","null").equals("null")){
            img_mine.setImageResource(R.drawable.defulticon);

        }
        else{
            iconUri=Uri.parse(sp.getString("MyIcon","null"));
            myGlideUtils.loadImage(this,60,60,img_mine,iconUri);
        }
    }
    /**
     *
     * 初始化我的固定资产界面
     * */
    private void initMyAssets() {
        View view=LayoutInflater.from(this).inflate(R.layout.pop_my_community, null, false);

        mAssetsPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_my_assets_btn_back=view.findViewById(R.id.pop_my_assets_btn_back);

        assetsListView=view.findViewById(R.id.my_assets_xlv);
        TextView tv_empty=view.findViewById(R.id.pop_my_assets_tv_empty);



        myAssetsAdapter=new MyAssetsAdapter(MainActivity.this, assetsList, new MyAssetsAdapter.OnItemDeleteClickListener() {
            @Override
            public void onItemDeleteClick(final String id, final int position) {
                Log.i("delete",id);
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(MainActivity.this);
                normalDialog.setTitle("温馨提示");
                normalDialog.setMessage("确定删除该记录？");
                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tb_name = "assets_del";
                        RequestParams params = new RequestParams(Contans.uri);
                        params.addBodyParameter("sqlcmd", "assets_del");
                        params.addBodyParameter("a_id", id);

                        deleteAssets(id, position, params);
                    }
                });
                normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
                // 显示
                normalDialog.show();




            }
        });
    //    myAssetsAdapter=new MyAssetsAdapter(MainActivity.this, assetsList);
        assetsListView.setAdapter(myAssetsAdapter);

        assetsListView.setPullRefreshEnable(true);
        assetsListView.setPullLoadEnable(true);

        assetsListView.setEmptyView(tv_empty);
        assetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Log.i("position",position+"");
                int assetsId=position-1;
                initAssetsDetailPop(assetsId);

//                Intent intent = new Intent(MainActivity.this, AssetsActivity.class);
//                intent.putExtra("AssetsBean", assetsList.get(position-1));
//                MainActivity.this.startActivity(intent);
            }
        });
        assetsListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                assetsList.removeAll(assetsList);
                assets=1;
                getAssetsData(assets);
                assetsListView.stopRefresh();


            }

            @Override
            public void onLoadMore() {

                assets++;
                getAssetsData(assets);
                assetsListView.stopLoadMore();

            }
        });






        pop_my_assets_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(mAssetsPopWindow);
            }
        });

    }

    //查看固定资产详情
    private void initAssetsDetailPop(int assetsId) {

        mDatas.removeAll(mDatas);

        String images=assetsList.get(assetsId).getImages();

        if(!"".equals(images)){
            String[] img=images.split(",");
            for(int i=0;i<img.length;i++){
                mDatas.add(img[i]);
            }
        }

        final View view= LayoutInflater.from(this).inflate(R.layout.pop_my_assets_detail,null,false);

        detailPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(detailPopWindow);

        GridView assets_detail_gridview=view.findViewById(R.id.assets_detail_gridview);



        Button assets_detail_btn_back=view.findViewById(R.id.assets_detail_btn_back);
        TextView assets_detail_position=view.findViewById(R.id.assets_detail_position);
        TextView assets_detail_card=view.findViewById(R.id.assets_detail_card);
        TextView assets_detail_id=view.findViewById(R.id.assets_detail_id);
        TextView assets_detail_name=view.findViewById(R.id.assets_detail_name);
        TextView assets_detail_value=view.findViewById(R.id.assets_detail_value);
        TextView assets_detail_address=view.findViewById(R.id.assets_detail_address);
        TextView assets_detail_start_date=view.findViewById(R.id.assets_detail_start_date);
        TextView assets_detail_use_year=view.findViewById(R.id.assets_detail_use_year);
        TextView inspect_detail_way=view.findViewById(R.id.inspect_detail_way);
        TextView assets_detail_use_dep=view.findViewById(R.id.assets_detail_use_dep);
        TextView assets_detail_entry_date=view.findViewById(R.id.assets_detail_entry_date);
        TextView assets_detail_deprecia=view.findViewById(R.id.assets_detail_deprecia);
        TextView assets_detail_model=view.findViewById(R.id.assets_detail_model);
        TextView assets_detail_status=view.findViewById(R.id.assets_detail_status);
        TextView assets_detail_remark=view.findViewById(R.id.assets_detail_remark);



        //     TextView inspect_detail_write=view.findViewById(R.id.inspect_detail_write);

        assets_detail_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(detailPopWindow);
            }
        });

//        inspect_detail_write.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, InspectActivity.class);
//                intent.putExtra("InspectBean", inspectList.get(inspectId));
//                MainActivity.this.startActivity(intent);
//            }
//        });

        assets_detail_position.setSingleLine(true);//设置单行显示
        assets_detail_position.setHorizontallyScrolling(true);//设置水平滚动效

        Log.i("mDatas.size()",mDatas.size()+"");

        assets_detail_position.setText(assetsList.get(assetsId).getPosition());
        assets_detail_card.setText(assetsList.get(assetsId).getCard());
        assets_detail_id.setText(assetsList.get(assetsId).getId());
        assets_detail_name.setText(assetsList.get(assetsId).getName());
        assets_detail_value.setText(assetsList.get(assetsId).getValue());
        assets_detail_address.setText(assetsList.get(assetsId).getAddress());
        assets_detail_start_date.setText(assetsList.get(assetsId).getStartdate());
        assets_detail_use_year.setText(assetsList.get(assetsId).getUsemonth());
        inspect_detail_way.setText(assetsList.get(assetsId).getWay());
        assets_detail_use_dep.setText(assetsList.get(assetsId).getUsedep());
        assets_detail_entry_date.setText(assetsList.get(assetsId).getEntrydate());
        assets_detail_deprecia.setText(assetsList.get(assetsId).getDeprecia());
        assets_detail_model.setText(assetsList.get(assetsId).getModel());
        assets_detail_status.setText(assetsList.get(assetsId).getStatus());
        assets_detail_remark.setText(assetsList.get(assetsId).getRemark());


        adapter=new MyRepGridAdapter(this,mDatas);
        assets_detail_gridview.setAdapter(adapter);
        assets_detail_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position", position + "");
                Log.i("mDatas.size()", mDatas.size() + "");
                imageId = position;

                startEditImage(mDatas.get(position));

            }

        });





    }




    /**
     *
     * 初始化我的维修界面
     * */
    private void initMyRepairPop() {

        View view=LayoutInflater.from(this).inflate(R.layout.pop_my_repair, null, false);

        mRepairPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_my_repair_btn_back=view.findViewById(R.id.pop_my_repair_btn_back);
        repairListView=view.findViewById(R.id.my_repair_xlv);
        TextView tv_empty=view.findViewById(R.id.pop_my_repair_tv_empty);


        myRepairAdapter=new MyRepairAdapter(MainActivity.this,repairList);
        repairListView.setAdapter(myRepairAdapter);
        repairListView.setEmptyView(tv_empty);

        repairListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("position",position+"");

                int repairId=position-1;
                initRepairDetailPop(repairId);
                popWindowUtils.showfullPopupWindow(detailPopWindow);
//                Intent intent = new Intent(MainActivity.this, RepairWriteActivity.class);
//                intent.putExtra("RepairBean", repairList.get(repairId));
//                MainActivity.this.startActivity(intent);



            }
        });
        repairListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

                repairList.removeAll(repairList);
                repair=1;
                getRepairDate(repair);
                repairListView.stopRefresh();


            }

            @Override
            public void onLoadMore() {

                repair++;
                getRepairDate(repair);
                repairListView.stopLoadMore();

            }
        });

        pop_my_repair_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(mRepairPopWindow);
            }
        });

    }

    //我的维修详情
    private void initRepairDetailPop(int repairId) {
        mDatas.removeAll(mDatas);

        String images=repairList.get(repairId).getImages();

        if(!"".equals(images)){
            String[] img=images.split(",");
            for(int i=0;i<img.length;i++){
                mDatas.add(img[i]);
            }
        }

        final View view= LayoutInflater.from(this).inflate(R.layout.pop_my_repair_detail,null,false);

        detailPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        GridView repair_detail_gridview=view.findViewById(R.id.repair_detail_gridview);

        Button repair_detail_btn_back=view.findViewById(R.id.repair_detail_btn_back);
        TextView repair_detail_repair_address=view.findViewById(R.id.repair_detail_repair_address);
        TextView repair_detail_report_firm=view.findViewById(R.id.repair_detail_report_firm);
        TextView repair_detail_report_type=view.findViewById(R.id.repair_detail_report_type);
        TextView repair_detail_report_address=view.findViewById(R.id.repair_detail_report_address);
        TextView repair_detail_report_position=view.findViewById(R.id.repair_detail_report_position);
        TextView repair_detail_report_date=view.findViewById(R.id.repair_detail_report_date);
        TextView repair_detail_repair_type=view.findViewById(R.id.repair_detail_repair_type);
        TextView repair_detail_repair_state=view.findViewById(R.id.repair_detail_repair_state);
        TextView repair_detail_repair_deal=view.findViewById(R.id.repair_detail_repair_deal);
        TextView repair_detail_repair_laborbudget=view.findViewById(R.id.repair_detail_repair_laborbudget);
        TextView repair_detail_repair_stuffbudget=view.findViewById(R.id.repair_detail_repair_stuffbudget);
        TextView repair_detail_repair_costbudget=view.findViewById(R.id.repair_detail_repair_costbudget);
        TextView repair_detail_repair_remark=view.findViewById(R.id.repair_detail_repair_remark);

        repair_detail_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(detailPopWindow);
            }
        });



        repair_detail_repair_address.setSingleLine(true);//设置单行显示
        repair_detail_repair_address.setHorizontallyScrolling(true);//设置水平滚动效

        Log.i("mDatas.size()",mDatas.size()+"");

        repair_detail_repair_address.setText(repairList.get(repairId).getRepair_address());
        repair_detail_report_firm.setText(repairList.get(repairId).getReport_firm());
        repair_detail_report_type.setText(repairList.get(repairId).getReport_type());
        repair_detail_report_address.setText(repairList.get(repairId).getReport_address());
        repair_detail_report_position.setText(repairList.get(repairId).getReport_position());
        repair_detail_repair_type.setText(repairList.get(repairId).getRepair_type());
        repair_detail_repair_state.setText(repairList.get(repairId).getRepair_state());
        repair_detail_report_date.setText(repairList.get(repairId).getReport_date());
        repair_detail_repair_deal.setText(repairList.get(repairId).getRepair_deal());
        repair_detail_repair_laborbudget.setText(repairList.get(repairId).getRepair_laborbudget());
        repair_detail_repair_stuffbudget.setText(repairList.get(repairId).getRepair_stuffbudget());
        repair_detail_repair_costbudget.setText(repairList.get(repairId).getRepair_costbudget());
        repair_detail_repair_remark.setText(repairList.get(repairId).getRepair_remark());


        adapter=new MyRepGridAdapter(this,mDatas);
        repair_detail_gridview.setAdapter(adapter);
        repair_detail_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position", position + "");
                Log.i("mDatas.size()", mDatas.size() + "");
                imageId = position;

                startEditImage(mDatas.get(position));

            }

        });

    }

    /**
     *
     * 初始化我的报修界面
     * */
    private void initMyReportPop() {
        View view=LayoutInflater.from(this).inflate(R.layout.pop_my_report, null, false);

        mReportPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        Button pop_my_report_btn_back=view.findViewById(R.id.pop_my_report_btn_back);
        reportListView=view.findViewById(R.id.my_report_xlv);
        TextView tv_empty=view.findViewById(R.id.pop_my_report_tv_empty);
//        myReportAdapter=new MyReportAdapter(MainActivity.this,reportList, new MyReportAdapter.OnItemDeleteClickListener() {
//            @Override
//            public void onItemDeleteClick(final String id, final int position) {
//                Log.i("id",id);
//                Log.i("position",position+"");
//                final AlertDialog.Builder normalDialog =
//                        new AlertDialog.Builder(MainActivity.this);
//                normalDialog.setTitle("温馨提示");
//                normalDialog.setMessage("确定删除该记录？");
//                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        tb_name = "repair_del";
//                        RequestParams params = new RequestParams(Contans.uri);
//                        params.addBodyParameter("sqlcmd", "repair_del");
//                        params.addBodyParameter("r_id", id);
//                        deleteReport(id,position,params);
//
//                    }
//                });
//                normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //...To-do
//                    }
//                });
//                // 显示
//                normalDialog.show();
//
//            }
//        });
        myReportAdapter=new MyReportAdapter(MainActivity.this,reportList);
        reportListView.setAdapter(myReportAdapter);
        reportListView.setEmptyView(tv_empty);

        reportListView.setPullRefreshEnable(true);
        reportListView.setPullLoadEnable(true);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("position",position+"");
                int reportId=position-1;
//                if(reportList.get(position-1).getRepair_state().equals("报修")){
//                    Intent intent = new Intent(MainActivity.this, ReportActivity.class);
//                    intent.putExtra("RepairBean", reportList.get(reportId));
//                    MainActivity.this.startActivity(intent);
//                }
//                else{
//                    initDetailPop();
//                }
                initDetailPop(reportId);



            }
        });
        reportListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                reportList.removeAll(reportList);
                report=1;
                getReportDate(report);
                reportListView.stopRefresh();


            }

            @Override
            public void onLoadMore() {
                report++;
                getReportDate(report);
                reportListView.stopLoadMore();

            }
        });


        pop_my_report_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(mReportPopWindow);
            }
        });




    }

    //报修的详情查看
    private void initDetailPop(int reportId) {
        mDatas.removeAll(mDatas);

        String images=reportList.get(reportId).getImages();

        if(!"".equals(images)){
            String[] img=images.split(",");
            for(int i=0;i<img.length;i++){
                mDatas.add(img[i]);
            }
        }

        final View view= LayoutInflater.from(this).inflate(R.layout.pop_my_report_detail,null,false);

        detailPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(detailPopWindow);

        GridView report_repair_gridview=view.findViewById(R.id.pop_my_report_detail_gridview);



        Button report_repair_btn_back=view.findViewById(R.id.pop_my_report_detail_btn_back);
        TextView report_repair_tv_address=view.findViewById(R.id.pop_my_report_detail_address);
        TextView report_repair_tv_firm=view.findViewById(R.id.pop_my_report_detail_firm);
        TextView report_repair_tv_type=view.findViewById(R.id.pop_my_report_detail_type);
        TextView report_repair_tv_time=view.findViewById(R.id.pop_my_report_detail_date);
        TextView report_repair_tv_position=view.findViewById(R.id.pop_my_report_detail_position);
 //       TextView pop_my_report_detail_write=view.findViewById(R.id.pop_my_report_detail_write);
 //       LinearLayout pop_my_report_detail_ll_write=view.findViewById(R.id.pop_my_report_detail_ll_write);

        report_repair_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(detailPopWindow);
            }
        });
//        if(!reportList.get(reportId).getRepair_state().equals("报修")){
//            pop_my_report_detail_ll_write.setVisibility(View.INVISIBLE);
//        }
//        else {
//            pop_my_report_detail_ll_write.setVisibility(View.VISIBLE);
//        }
//        pop_my_report_detail_write.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
//                intent.putExtra("RepairBean", reportList.get(reportId));
//                MainActivity.this.startActivity(intent);
//            }
//        });

        report_repair_tv_address.setSingleLine(true);//设置单行显示
        report_repair_tv_address.setHorizontallyScrolling(true);//设置水平滚动效

        Log.i("mDatas.size()",mDatas.size()+"");

        report_repair_tv_address.setText(reportList.get(reportId).getReport_address());
        report_repair_tv_firm.setText(reportList.get(reportId).getReport_firm());
        report_repair_tv_type.setText(reportList.get(reportId).getReport_type());
        report_repair_tv_time.setText(reportList.get(reportId).getReport_date());
        report_repair_tv_position.setText(reportList.get(reportId).getReport_position());


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

    //查看大图
    private void startEditImage(String imgPath) {
        Log.i("imageDescription","开始编辑");
        View imgView= LayoutInflater.from(this).inflate(R.layout.pop_inspect_image_item, null, false);
        imgPopWindow=new PopupWindow(imgView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(imgPopWindow);

        Button pop_inspect_image_back=imgView.findViewById(R.id.pop_inspect_image_back);
        ImageView pop_inspect_image=imgView.findViewById(R.id.pop_inspect_image);
        Button pop_inspect_image_delete=imgView.findViewById(R.id.pop_inspect_image_delete);
        pop_inspect_image_delete.setVisibility(View.GONE);

        Glide.with(this).load(imgPath).into(pop_inspect_image);

        Log.i("imageId", imageId + "");


        pop_inspect_image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(imgPopWindow);

            }
        });


    }

    /**
     *
     * 初始化我的巡查界面
     *
     * */
    private void initMyInspectPop() {
        View view=LayoutInflater.from(this).inflate(R.layout.pop_my_inspect, null, false);

        mInspectPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        Button pop_my_inspect_btn_back=view.findViewById(R.id.pop_my_inspect_btn_back);
        inspectListView=view.findViewById(R.id.my_inspect_xlv);
        myInspectAdapter=new MyInspectAdapter(MainActivity.this, inspectList, new MyInspectAdapter.OnItemDeleteClickListener() {
            @Override
            public void onItemDeleteClick(final String id, final int position) {
                Log.i("delete",id);
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(MainActivity.this);
                normalDialog.setTitle("温馨提示");
                normalDialog.setMessage("确定删除该记录？");
                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tb_name = "patrol_del";
                        RequestParams params = new RequestParams(Contans.uri);
                        params.addBodyParameter("sqlcmd", "patrol_del");
                        params.addBodyParameter("p_id", id);

                        deleteInspect(id, position, params);
                    }
                });
                normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
                // 显示
                normalDialog.show();


            }
        });
     //   myInspectAdapter=new MyInspectAdapter(MainActivity.this, inspectList);
        inspectListView.setAdapter(myInspectAdapter);
        TextView tv_empty=view.findViewById(R.id.pop_my_inspect_tv_empty);

        inspectListView.setEmptyView(tv_empty);

        inspectListView.setPullRefreshEnable(true);

        inspectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int inspectId=position-1;
                Log.i("position",position+"");

                initInspectDetailPop(inspectId);

            }
        });
        inspectListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                inspectList.removeAll(inspectList);
                inspect=1;
                getInspectData(inspect);
                inspectListView.stopRefresh();


            }

            @Override
            public void onLoadMore() {
                inspect++;
                getInspectData(inspect);
                inspectListView.stopLoadMore();

            }
        });

        pop_my_inspect_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(mInspectPopWindow);
            }
        });



    }

    //查看我的巡查详情
    private void initInspectDetailPop(int inspectId){
        mDatas.removeAll(mDatas);

        String images=inspectList.get(inspectId).getImages();

        if(!"".equals(images)){
            String[] img=images.split(",");
            for(int i=0;i<img.length;i++){
                mDatas.add(img[i]);
            }
        }

        final View view= LayoutInflater.from(this).inflate(R.layout.pop_my_inspect_detail,null,false);

        detailPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindowUtils.showfullPopupWindow(detailPopWindow);

        GridView inspect_detail_gridview=view.findViewById(R.id.inspect_detail_gridview);



        Button inspect_detail_btn_back=view.findViewById(R.id.inspect_detail_btn_back);
        TextView inspect_detail_tv_address=view.findViewById(R.id.inspect_detail_tv_address);
        TextView inspect_detail_tv_content=view.findViewById(R.id.inspect_detail_tv_content);
        TextView inspect_detail_tv_type=view.findViewById(R.id.inspect_detail_tv_type);
        TextView inspect_detail_tv_result=view.findViewById(R.id.inspect_detail_tv_result);
        TextView inspect_detail_tv_time=view.findViewById(R.id.inspect_detail_tv_time);
        TextView inspect_detail_tv_remark=view.findViewById(R.id.inspect_detail_tv_remark);


        inspect_detail_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(detailPopWindow);
            }
        });

        inspect_detail_tv_address.setSingleLine(true);//设置单行显示
        inspect_detail_tv_address.setHorizontallyScrolling(true);//设置水平滚动效

        Log.i("mDatas.size()",mDatas.size()+"");

        inspect_detail_tv_address.setText(inspectList.get(inspectId).getPosition());
        inspect_detail_tv_content.setText(inspectList.get(inspectId).getContent());
        inspect_detail_tv_type.setText(inspectList.get(inspectId).getType());
        inspect_detail_tv_result.setText(inspectList.get(inspectId).getResult());
        inspect_detail_tv_time.setText(inspectList.get(inspectId).getDate());
        inspect_detail_tv_remark.setText(inspectList.get(inspectId).getRemark());



        adapter=new MyRepGridAdapter(this,mDatas);
        inspect_detail_gridview.setAdapter(adapter);
        inspect_detail_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position", position + "");
                Log.i("mDatas.size()", mDatas.size() + "");
                imageId = position;

                startEditImage(mDatas.get(position));

            }

        });



    }

    /**
     *
     * 初始化设置界面
     * */
    private void initSettingPop() {
        View view=LayoutInflater.from(this).inflate(R.layout.pop_setting, null, false);

        mSettingPopWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Button pop_my_setting_ll_back=view.findViewById(R.id.pop_my_setting_btn_back);
        LinearLayout pop_my_setting_ll_about=view.findViewById(R.id.pop_my_setting_ll_about);
        LinearLayout pop_my_setting_ll_help=view.findViewById(R.id.pop_my_setting_ll_help);
        TextView pop_my_setting_version=view.findViewById(R.id.pop_my_setting_version);
        LinearLayout pop_my_setting_ll_exit=view.findViewById(R.id.pop_my_setting_ll_exit);

        pop_my_setting_version.setText("v"+versionName);
        pop_my_setting_ll_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check(versionName);
//
//                if(!isUpdate){
//                    Toast.makeText(MainActivity.this,"已经是最新版本",Toast.LENGTH_SHORT).show();
//                }
            }
        });
        pop_my_setting_ll_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(MainActivity.this,"请联系管理员",Toast.LENGTH_SHORT).show();

            }
        });



        pop_my_setting_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindowUtils.dissPopupWindow(mSettingPopWindow);
            }
        });

        pop_my_setting_ll_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(MainActivity.this);
                normalDialog.setTitle("温馨提示");
                normalDialog.setMessage("确定退出当前账号？");
                normalDialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        popWindowUtils.dissPopupWindow(mSettingPopWindow);
                        popWindowUtils.dissPopupWindow(minePopWindow);
                        isLogin=false;
                        sp=getSharedPreferences("date",MODE_PRIVATE);
                        sp.edit().remove("login").commit();
                    }
                });
                normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
                // 显示
                normalDialog.show();
            }
        });

    }


    /**
     *
     * 初始化维修报修弹出窗口
     *
     * */
  /*  private void initObjPop() {
        View objView=LayoutInflater.from(this).inflate(R.layout.pop_main_repair_report, null, false);

        objPopWindow=new PopupWindow(objView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        ImageView pop_btn_house=objView.findViewById(R.id.pop_btn_house);
        ImageView pop_btn_equiment=objView.findViewById(R.id.pop_btn_equipment);
        pop_btn_house.setOnClickListener(this);
        pop_btn_equiment.setOnClickListener(this);
    }*/
    /**
     *
     * 初始化巡查弹出窗口
     *
     * */

    private void initInspectPop() {
        View inspectView=LayoutInflater.from(this).inflate(R.layout.pop_main_inspect, null, false);

        inspectPopWindow=new PopupWindow(inspectView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        ImageView pop_main_inspect_house=inspectView.findViewById(R.id.pop_main_inspect_house);
        ImageView pop_main_inspect_equipment=inspectView.findViewById(R.id.pop_main_inspect_equipment);
        ImageView pop_main_inspect_property=inspectView.findViewById(R.id.pop_main_inspect_property);



        pop_main_inspect_house.setOnClickListener(this);
        pop_main_inspect_equipment.setOnClickListener(this);
        pop_main_inspect_property.setOnClickListener(this);
    }

    /**
     *
     * 判断跳转到哪一个界面
     * */
    private void start() {
        switch (state){
            case REPAIR:
                Intent repair=new Intent();
                repair.setClass(this,RepairActivity.class);
                startActivity(repair);
                break;

            case REPORT:
                Intent report=new Intent();
                report.setClass(this,ReportActivity.class);
                startActivity(report);
                break;

            case INSPECT:
                if(obj==HOUSE) {
                    Intent inspect = new Intent(MainActivity.this, InspectActivity.class);
                    inspect.putExtra("inspectType","房屋");
                    startActivity(inspect);
                }
                else if(obj==EQUIPMENT) {
                    Intent inspect = new Intent(MainActivity.this, InspectActivity.class);
                    inspect.putExtra("inspectType","设备");
                    startActivity(inspect);
                }

                break;

                default:
        }
    }

    //跳转到固定资产提交界面
    private void startAssetsActivity(){
        Intent intent=new Intent();
        intent.setClass(this,AssetsActivity.class);
        startActivity(intent);
    }

//    /**
//     * 动态申请权限
//     *
//     * */
//    public void applyPermission(int requestCode) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Log.i("权限","需要申请权限");
//            if(this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED &&
//                    this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED&&
//                    this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED&&
//                    this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED&&
//                    this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
//                Log.i("权限","权限已经获得");
//                startScan(requestCode);
//
//            }
//            else {
//                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
//                Log.i("权限","开始申请权限");
//                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
//            }
//        }
//        else{
//            Log.i("权限","不需要申请权限");
//            startScan(requestCode);
//        }
//
//    }

    /***
     * 开始扫码
     *
     */
    private void startScan(int requestCode) {
        Intent intent = new Intent(MainActivity.this,
                CaptureActivity.class);
        startActivityForResult(intent, requestCode);


    }

    /**
     *
     * 保存头像信息
     *
     * */
    private void saveIcon() {


        sp.edit().putString("MyIcon",iconUri.toString()).commit();

    }


    //删除巡查记录
    public void deleteInspect(final String id, final int position,RequestParams params) {
        myLoadDialog.showLoading("删除", "正在删除中，请稍等...");


        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("result",result);
                if(!tb_name.equals("mps_attach_del")) {
                    tb_name = "mps_attach_del";
                    RequestParams params = new RequestParams(Contans.uri);
                    params.addBodyParameter("sqlcmd", "mps_attach_del");

                    params.addBodyParameter("ma_billid", id);
                    params.addBodyParameter("ma_tbname", "mps_patrol");
                    deleteInspect(id, position, params);


                }
                else{
                    myLoadDialog.hideLoading();
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();


//                myInspectAdapter=new MyInspectAdapter(MainActivity.this, inspectList);
//                inspectListView.setAdapter(myInspectAdapter);

                    inspectList.remove(position);
                    myInspectAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"删除失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                }
                myLoadDialog.hideLoading();
                if(myInspectAdapter!=null) {
                    myInspectAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                myLoadDialog.hideLoading();
            }

            @Override
            public void onFinished() {

            }
        });

    }
    //删除报修记录
 /*   public void deleteReport(final String id, final int position,RequestParams params) {

        myLoadDialog.showLoading("删除", "正在删除中，请稍等...");


        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("成功",id);
                if(!tb_name.equals("mps_attach_del")) {

                    tb_name = "mps_attach_del";
                    RequestParams params = new RequestParams(Contans.uri);
                    params.addBodyParameter("sqlcmd", "mps_attach_del");

                    params.addBodyParameter("ma_billid", id);
                    params.addBodyParameter("ma_tbname", "mps_repair");
                    deleteReport(id, position, params);


                }
                else{
                    myLoadDialog.hideLoading();
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();


//                myInspectAdapter=new MyInspectAdapter(MainActivity.this, inspectList);
//                inspectListView.setAdapter(myInspectAdapter);

                    reportList.remove(position);
                    myReportAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"删除失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                }
                myLoadDialog.hideLoading();
                if(myInspectAdapter!=null) {
                    myReportAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                myLoadDialog.hideLoading();
            }

            @Override
            public void onFinished() {

            }
        });

    }
*/
    //删除固定资产记录
    public void deleteAssets(final String id, final int position,RequestParams params) {
        myLoadDialog.showLoading("删除", "正在删除中，请稍等...");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("result",result);
                if(!tb_name.equals("mps_attach_del")) {
                    tb_name = "mps_attach_del";
                    RequestParams params = new RequestParams(Contans.uri);
                    params.addBodyParameter("sqlcmd", "mps_attach_del");

                    params.addBodyParameter("ma_billid", id);
                    params.addBodyParameter("ma_tbname", "mps_assets");
                    deleteAssets(id, position, params);


                }
                else{
                    myLoadDialog.hideLoading();
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                    assetsList.remove(position);
                    myAssetsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"删除失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Log.i("error","删除出错");
                    Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                }
                myLoadDialog.hideLoading();
             //   Log.i("error","删除出错");
                if(myInspectAdapter!=null) {
                    myAssetsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                myLoadDialog.hideLoading();
            }

            @Override
            public void onFinished() {

            }
        });

    }


    //获取维修数据
    public void getRepairDate(int index) {

        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_repair_list");
        params.addBodyParameter("datatype","json");

        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("id",sp.getString("cu_compid",""));
        params.addBodyParameter("pageindex",index+"");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");
        params.addBodyParameter("state","维修");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int count =1;
                myLoadDialog.hideLoading();
                Log.i("维修数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
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
                                    if(dsMap.get(key).equals("201808070001")) {
                                        repairBean.setReport_firm("中锦公司");
                                    }
                                    else if(dsMap.get(key).equals("201808070002")){
                                        repairBean.setReport_firm("睿华公司");
                                    }
                                    else if(dsMap.get(key).equals("201808070003")){
                                        repairBean.setReport_firm("青羊公司");
                                    }
                                    else if(dsMap.get(key).equals("201808070004")){
                                        repairBean.setReport_firm("金信源公司");
                                    }
                                    else{
                                        repairBean.setReport_firm("土地公司");
                                    }

                                }
                                if(key.equals("r_type")) {
                                    repairBean.setReport_type(dsMap.get(key));
                                }
                                if(key.equals("r_position")) {

                                    repairBean.setReport_address(dsMap.get(key));
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
                            if(!repairBean.getRepair_state().equals("报修")){
                                repairList.add(repairBean);
                            }
                            count++;

                        }

                    }
                }


                if(count<20){
                    repairListView.setPullLoadEnable(false);
                }
                else{
                    repairListView.setPullLoadEnable(true);
                }

                if(myRepairAdapter!=null){
                    myRepairAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(MainActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }
                if(myRepairAdapter!=null) {
                    myRepairAdapter.notifyDataSetChanged();
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
    //获取报修数据
    public void getReportDate(int index) {
        Log.i("id",sp.getString("cu_userid",""));
        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_repair_list");
        params.addBodyParameter("datatype","json");

        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("id",sp.getString("cu_userid",""));
        params.addBodyParameter("pageindex",index+"");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");
        params.addBodyParameter("state","");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int count=1;
                myLoadDialog.hideLoading();
                Log.i("报修数据",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
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
                                    if(dsMap.get(key).equals("201808070001")) {
                                        repairBean.setReport_firm("中锦公司");
                                    }
                                    else if(dsMap.get(key).equals("201808070002")){
                                        repairBean.setReport_firm("睿华公司");
                                    }
                                    else if(dsMap.get(key).equals("201808070003")){
                                        repairBean.setReport_firm("青羊公司");
                                    }
                                    else if(dsMap.get(key).equals("201808070004")){
                                        repairBean.setReport_firm("金信源公司");
                                    }
                                    else{
                                        repairBean.setReport_firm("土地公司");
                                    }
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
                                repairBean.setRepair_remark("");

                            }
                            reportList.add(repairBean);
                            count++;
                        }

                    }
                }

                if(count<20){
                    reportListView.setPullLoadEnable(false);
                }
                else{
                    reportListView.setPullLoadEnable(true);
                }

                if(myReportAdapter!=null) {
                    myReportAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(MainActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }
                if(myReportAdapter!=null) {

                    myReportAdapter.notifyDataSetChanged();
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
    //获取巡查数据
    public void getInspectData(int index) {
        Log.i("id",sp.getString("cu_userid",""));
        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_patrol_list");
        params.addBodyParameter("datatype","json");

        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("pageindex",index+"");

        params.addBodyParameter("id",sp.getString("cu_userid",""));
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","1");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int count=1;
                myLoadDialog.hideLoading();
                jsonMap = JsonUtils.stringToJson(result);


                Log.i("巡查数据",result);
                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        for(HashMap<String, String> dsMap:list){
                            InspectBean inspectBean=new InspectBean();

                            //取出想要的数据
                            for(String key:dsMap.keySet()) {

                                if(key.equals("p_id")) {

                                    inspectBean.setId(dsMap.get(key));
                                }
                                if(key.equals("images")) {

                                    inspectBean.setImages(dsMap.get(key));
                                }
                                if(key.equals("p_tbname")) {

                                    inspectBean.setTbname(dsMap.get(key));
                                }
                                if(key.equals("p_date")) {

                                    inspectBean.setDate(dsMap.get(key));
                                }
                                if(key.equals("p_type")) {

                                    inspectBean.setType(dsMap.get(key));
                                }
                                if(key.equals("p_result")) {

                                    inspectBean.setResult(dsMap.get(key));
                                }
                                if(key.equals("p_content")) {

                                    inspectBean.setContent(dsMap.get(key));
                                }
                                if(key.equals("p_remark")) {

                                    inspectBean.setRemark(dsMap.get(key));
                                }
                                if(key.equals("p_position")) {

                                    inspectBean.setPosition(dsMap.get(key));
                                }


                            }
                            inspectList.add(inspectBean);

                            count++;
                        }

                    }
                }

                if(count<20){
              //      Toast.makeText(MainActivity.this,"已经没有更多数据了",Toast.LENGTH_SHORT).show();
                    inspectListView.setPullLoadEnable(false);
                }
                else{
                    inspectListView.setPullLoadEnable(true);
                }
                if(myInspectAdapter!=null) {
                    myInspectAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(MainActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }
                if(myInspectAdapter!=null) {

                    myInspectAdapter.notifyDataSetChanged();
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
    //获取固定资产数据
    public void getAssetsData(int index) {

        myLoadDialog.showLoading("获取数据","正在加载数据中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_assets_list");
        params.addBodyParameter("datatype","json");

        params.addBodyParameter("pagesize","20");
        params.addBodyParameter("id",sp.getString("cu_userid",""));
        params.addBodyParameter("pageindex",index+"");
        params.addBodyParameter("qry","1");
        params.addBodyParameter("rtnds","2");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int count=1;
                myLoadDialog.hideLoading();
                Log.i("固定资产",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()){
                    Log.i("数组",ds);
                    ArrayList<HashMap<String, String>> list=jsonMap.get(ds);
                    if(list.size()>0){

                        for(HashMap<String, String> dsMap:list){
                            AssetsBean assetsBean=new AssetsBean();

                            //取出想要的数据
                            for(String key:dsMap.keySet()) {

                                if(key.equals("a_id")) {

                                    assetsBean.setId(dsMap.get(key));
                                }
                                if(key.equals("images")) {

                                    assetsBean.setImages(dsMap.get(key));
                                }
                                if(key.equals("a_card")) {

                                    assetsBean.setCard(dsMap.get(key));
                                }
                                if(key.equals("a_value")) {

                                    assetsBean.setValue(dsMap.get(key));
                                }
                                if(key.equals("a_deprecia")) {

                                    assetsBean.setDeprecia(dsMap.get(key));
                                }
                                if(key.equals("a_tbname")) {

                                    assetsBean.setTbname(dsMap.get(key));
                                }
                                if(key.equals("a_number")) {

                                    assetsBean.setNumber(dsMap.get(key));
                                }
                                if(key.equals("a_name")) {

                                    assetsBean.setName(dsMap.get(key));
                                }
                                if(key.equals("a_startdate")) {

                                    assetsBean.setStartdate(dsMap.get(key));
                                } if(key.equals("a_usemonth")) {

                                    assetsBean.setUsemonth(dsMap.get(key));
                                }
                                if(key.equals("a_way")) {

                                    assetsBean.setWay(dsMap.get(key));
                                }
                                if(key.equals("a_usedep")) {

                                    assetsBean.setUsedep(dsMap.get(key));
                                }
                                if(key.equals("a_residue")) {

                                    assetsBean.setResidue(dsMap.get(key));
                                }
                                if(key.equals("a_entrydate")) {

                                    assetsBean.setEntrydate(dsMap.get(key));
                                }
                                if(key.equals("a_address")) {

                                    assetsBean.setAddress(dsMap.get(key));
                                }
                                if(key.equals("a_model")) {

                                    assetsBean.setModel(dsMap.get(key));
                                }
                                if(key.equals("a_status")) {

                                    assetsBean.setStatus(dsMap.get(key));
                                }
                                if(key.equals("a_position")) {

                                   assetsBean.setPosition(dsMap.get(key));
                                }
                                if(key.equals("a_remark")) {

                                    assetsBean.setRemark(dsMap.get(key));
                                }


                            }
                            assetsList.add(assetsBean);
                            count++;
                        }

                    }
                }

                if(count<20){
                    assetsListView.setPullLoadEnable(false);
                }
                else {
                    assetsListView.setPullLoadEnable(true);
                }
                if(myAssetsAdapter!=null) {
                    myAssetsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("获取数据失败",ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(MainActivity.this,"获取数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                } else { // 其他错误
                    Toast.makeText(MainActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                }
                if(myAssetsAdapter!=null) {

                    myAssetsAdapter.notifyDataSetChanged();
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

    public void applyPermission(int requestCode) {
        if (EasyPermissions.hasPermissions(this, mPermissions)){
            myBaiduMap.reStart();
        }else{
           //第二个参数是提示信息
            EasyPermissions.requestPermissions(this,"请打开定位权限",requestCode,mPermissions);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);

    }

    @AfterPermissionGranted(LOCATION)//请求码
    private void after() {
        Log.i("after","调用after()方法");
        myBaiduMap.reStart();
//        if (EasyPermissions.hasPermissions(this, mPermissions)){
//            myBaiduMap.reStart();
//        }else{
//            EasyPermissions.requestPermissions(this,"请打开定位权限",LOCATION,mPermissions);
//        }
    }
    /**
     *
     * 跳转返回值
     *
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//           if(requestCode==SCAN){
//                if(resultCode == RESULT_OK) {
//                    if (data != null) {
//                        String content = data.getStringExtra(Constant.CODED_CONTENT);
//                        Log.d("scan", "扫一扫返回成功！扫码结果为：" + content);
//                    }
//                }
//           }
        Log.i("result",resultCode+"");
        if(requestCode==LOGIN){
            if(resultCode==1) {

                Log.i("result", "调用setIsLogin方法");
                setIsLogin();
            }
        }
        if(requestCode==REQUEST_CODE_CHOOSE){
            if(resultCode==RESULT_OK) {
                iconUri= Matisse.obtainResult(data).get(0);
                myGlideUtils.loadImage(MainActivity.this, 60, 60, pop_msg_img_photo,iconUri);
                myGlideUtils.loadImage(MainActivity.this, 60, 60, img_mine, iconUri);

                saveIcon();

            }
        }


    }


    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
//弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack(); //goBack()表示返回WebView的上一页面
            return true;          }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理

        Log.i("MainActivity","onResume");

        rl_top.setFocusable(true);

        rl_top.setFocusableInTouchMode(true);
    //    regeKeyListener(main_searchView);
//        main_searchView.setFocusable(false);
//        main_searchView.setFocusableInTouchMode(true);
        main_searchView.clearFocus();

        myBaiduMap.reStart();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity","onPause");
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
