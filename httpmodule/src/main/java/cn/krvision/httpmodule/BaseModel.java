package cn.krvision.httpmodule;

import android.content.Context;

import cn.krvision.mynavidemo.HttpLoadingDialog;

/**
 * Created by gaoqiong on 2018/3/14
 */

public class BaseModel {

    public Context context;
    public HttpLoadingDialog httpLoadingDialog;

    public BaseModel(Context context) {
        this.context = context;
        httpLoadingDialog = new HttpLoadingDialog(context);
    }
}
