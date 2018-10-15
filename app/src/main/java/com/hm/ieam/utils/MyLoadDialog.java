package com.hm.ieam.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class MyLoadDialog {
    Context context;
    private ProgressDialog mLoadingDialog; //显示正在加载的对话框
    public MyLoadDialog(Context context){
        this.context=context;
    }


    //展示加载对话框
    public void showLoading(String title,String message) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(context);
        }
        mLoadingDialog.setTitle(title);
        mLoadingDialog.setMessage(message);
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
    }
    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();


        }
    }
}
