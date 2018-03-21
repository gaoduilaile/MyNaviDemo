package cn.krvision.mynavidemo;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import cn.krvision.httpmodule.R;

/**
 * Created by gaoqiong on 2018/3/14
 */

public class HttpLoadingDialog extends cn.krvision.mynavidemo.BaseDialog {
    private TextView tvMessage;
    public static final int DIALOG_HTTP_LOADING_UPLOAD = 1;
    public static final int DIALOG_HTTP_LOADING_WORKING = 2;

    public HttpLoadingDialog(Context context) {
        super(context,  R.style.Dialog_Style);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.dialog_http_loading);
    }

    public void initBoots() {
        this.setCanceledOnTouchOutside(false);
        this.setCanceledOnKeyBack(true);
    }

    public void initViews() {
        this.tvMessage = (TextView)this.findViewById(R.id.tv_dialog_http_loading);
    }

    public void initData() {
        this.tvMessage.setText(this.dialogMessage);
    }

    public void initEvents() {
    }

    public void visible() {
        this.setDialogMessage(this.getRes().getString(R.string.dialog_http_loading_message_default));
        this.setViewMessage();
        this.show();
    }

    public void visible(String message) {
        this.setDialogMessage(message);
        this.setViewMessage();
        this.show();
    }

    public void visible(int type) {
        int res = R.string.dialog_http_loading_message_default;
        switch(type) {
            case 1:
                res = R.string.dialog_http_loading_message_upload;
                break;
            case 2:
                res = R.string.dialog_http_loading_message_working;
        }

        this.setDialogMessage(this.getRes().getString(res));
        this.setViewMessage();
        this.show();
    }

    private void setViewMessage() {
        if(this.tvMessage != null) {
            this.tvMessage.setText(this.dialogMessage);
        }

    }
}
