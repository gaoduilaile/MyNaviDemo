package cn.krvision.mynavidemo;

import android.content.Context;

import cn.krvision.httpmodule.BaseModel;

/**
 * Created by gaoqiong on 2018/3/14
 */

public class MapModel  extends BaseModel {
    public MapModel(Context context) {
        super(context);
    }

    public void method1(){

//        httpLoadingDialog.show();
    }

    public void method2(){

        httpLoadingDialog.dismiss();
    }
}
