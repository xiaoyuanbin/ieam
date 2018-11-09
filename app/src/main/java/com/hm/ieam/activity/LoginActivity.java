package com.hm.ieam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hm.ieam.R;
import com.hm.ieam.bean.InspectBean;
import com.hm.ieam.utils.Contans;
import com.hm.ieam.utils.JsonUtils;
import com.hm.ieam.utils.MD5Tools;
import com.hm.ieam.utils.MyLoadDialog;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    HashMap<String, ArrayList<HashMap<String, String>>> jsonMap; //存放解析json数据的结果
    Button login_btn_back;
    EditText login_edit_username;
    EditText login_edit_password;
    Button login_img_see_password;
    Button login_btn_login;
    boolean isVisible = false;
    boolean isRight=false;
    SharedPreferences sp;
    MyLoadDialog myLoadDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        myLoadDialog=new MyLoadDialog(this);

    }

    private void initView() {
        login_btn_back = findViewById(R.id.login_btn_back);
        login_edit_username = findViewById(R.id.login_edit_username);
        login_edit_password = findViewById(R.id.login_edit_password);
        login_img_see_password = findViewById(R.id.login_img_see_password);
        login_btn_login = findViewById(R.id.login_btn_login);

        login_btn_back.setOnClickListener(this);
        login_img_see_password.setOnClickListener(this);
        login_btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_back:
                finish();
                break;
            case R.id.login_img_see_password:
                setPasswordVisibility();
                break;
            case R.id.login_btn_login:
                login();
                break;
        }

    }

    private void setPasswordVisibility() {
        if (isVisible) {
            login_img_see_password.setBackgroundResource(R.drawable.invisible);
            //密码不可见
            login_edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isVisible = false;

        } else {
            login_img_see_password.setBackgroundResource(R.drawable.visible);
            isVisible = true;
            //密码可见
            login_edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }


    private void login() {
        String username = login_edit_username.getText().toString();
        String password = login_edit_password.getText().toString();
        if ("".equals(username) || "".equals(password)) {
            Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();

        } else {

            String md5Password=MD5Tools.md5Password(password);

       //     Log.i("md5Password",md5Password);

            getUserData(username,md5Password);

        }

    }

    public void getUserData(String username, final String password) {

        myLoadDialog.showLoading("正在登录","登录中，请稍等...");
        RequestParams params = new RequestParams(Contans.uri);
        params.addBodyParameter("sqlcmd","moblie_user_list");

        params.addBodyParameter("id",username);
        params.addBodyParameter("password",password);
        params.addBodyParameter("datatype","json");
        params.addBodyParameter("rtnds","1");


        Log.i("登录",params+"");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
                sp= getSharedPreferences("date",MODE_PRIVATE);
                Log.i("账号信息",result);
                jsonMap = JsonUtils.stringToJson(result);

                for(String ds:jsonMap.keySet()) {

                    ArrayList<HashMap<String, String>> list = jsonMap.get(ds);
                    Log.i("数组", list.size()+"");
                    if (list.size() > 0) {

                        for (HashMap<String, String> dsMap : list) {


                            //取出想要的数据
                            for (String key : dsMap.keySet()) {

                                if (key.equals("cu_userid")) {
                                    sp.edit().putString("cu_userid", dsMap.get(key)).commit();
                                    isRight=true;
                                }
                                if (key.equals("cu_usercode")) {
                                    sp.edit().putString("cu_usercode", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_username")) {
                                    sp.edit().putString("cu_username", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_userid1")) {
                                    sp.edit().putString("cu_userid1", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_depid")) {
                                    sp.edit().putString("cu_depid", dsMap.get(key)).commit();

                                }
                                if (key.equals("dep_name")) {
                                    sp.edit().putString("dep_name", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_cmpid")) {
                                    sp.edit().putString("cu_cmpid", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_mobile")) {
                                    sp.edit().putString("cu_mobile", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_contact")) {
                                    sp.edit().putString("cu_contact", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_contact1")) {
                                    sp.edit().putString("cu_contact1", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_islogin")) {
                                    sp.edit().putString("cu_islogin", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_photo")) {
                                    sp.edit().putString("cu_photo", dsMap.get(key)).commit();

                                }

                                if (key.equals("cu_type")) {
                                    sp.edit().putString("cu_type", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_compid")) {
                                    sp.edit().putString("cu_compid", dsMap.get(key)).commit();

                                }

                                if (key.equals("cu_ispatrol")) {
                                    sp.edit().putString("cu_ispatrol", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_isrepair")) {
                                    sp.edit().putString("cu_isrepair", dsMap.get(key)).commit();

                                }
                                if (key.equals("cu_ismaintain")) {
                                    sp.edit().putString("cu_ismaintain", dsMap.get(key)).commit();

                                }
                                if (key.equals("c_name")) {
                                    sp.edit().putString("c_name", dsMap.get(key)).commit();

                                }

                            }
                        }
                    }
                }
                if(isRight) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    sp.edit().putString("cu_password", password).commit();
                    setResult(1);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "账号或密码不正确", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                if (ex instanceof HttpException) { // 网络错误
                    Log.i("登录失败，网络错误",ex.getMessage());
                } else { // 其他错误
                    Log.i("登录失败",ex.getMessage());
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



}
