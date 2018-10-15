package com.hm.ieam.utils;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MyOkhttpClient {

    public MyLoadDialog myLoadDialog;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    Context context;

    public MyOkhttpClient( Context context) {
       this.context=context;
        myLoadDialog=new MyLoadDialog(context);
    }
    public void upload(RequestParams params) {
        myLoadDialog.showLoading("提交中","正在提交，请稍等...");
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                myLoadDialog.hideLoading();
     //           Looper.prepare();

                Toast.makeText(context,"提交成功",Toast.LENGTH_SHORT).show();
             //   Looper.loop();


                Log.i("data",result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                myLoadDialog.hideLoading();
                Log.i("提交失败","提交失败");
         //       Looper.prepare();

                Toast.makeText(context,"提交失败",Toast.LENGTH_SHORT).show();
           //     Looper.loop();
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



    public  void uploadImage(final Map<String,String> params, final List<String> mImages){

        myLoadDialog.showLoading("提交中","正在提交，请稍等...");
        OkHttpClient client = new OkHttpClient();
        final Message mMessage = new Message();
        final MultipartBody.Builder mbody=new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String,String> param:params.entrySet()){
            mbody.addFormDataPart(param.getKey(),param.getValue());
        }

        for (int i =0; i< mImages.size();i++){
            Log.i("img",mImages.get(i));
            Log.i("i",i+"");
            mbody.addFormDataPart("img_"+i, mImages.get(i).toString().substring(mImages.get(i).toString().lastIndexOf("/")+1),
                    RequestBody.create(MEDIA_TYPE_PNG,new File(mImages.get(i).toString())));
        }
        mbody.build();
        RequestBody body = mbody.build();
        Request request = new Request.Builder().url(Contans.uri).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                myLoadDialog.hideLoading();
                Log.i("提交失败","提交失败");
                toast("提交失败");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {


                myLoadDialog.hideLoading();
                final  String data= response.body().string();
                Log.i("data",data);
                toast("提交成功");

            }
        });
    }
    private void toast(String content) {

        Looper.prepare();
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }


}
