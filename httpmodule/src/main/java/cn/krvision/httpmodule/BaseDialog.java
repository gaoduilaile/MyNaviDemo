package cn.krvision.mynavidemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.KeyEvent;

/**
 * Created by gaoqiong on 2018/3/14
 */
public abstract class BaseDialog extends Dialog {
    public Context context;
    public DialogInterface dialogInterface;
    public String dialogTitle;
    public String dialogMessage;
    public String okMessage;
    public String cancelMessage;
    public boolean canceledOnKeyBack = true;

    public BaseDialog(Context context) {
        super(context);
        this.context = context;
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState, @LayoutRes int layout) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout);
        this.initBoots();
        this.initViews();
        this.initData();
        this.initEvents();
    }

    public abstract void initBoots();

    public abstract void initViews();

    public abstract void initData();

    public abstract void initEvents();

    public BaseDialog setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    public BaseDialog setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
        return this;
    }

    public BaseDialog setDialogOkMessage(String okMessage) {
        this.okMessage = okMessage;
        return this;
    }

    public BaseDialog setDialogCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
        return this;
    }

    public BaseDialog setDialogAllInfo(String dialogTitle, String dialogMessage, String okMessage, String cancelMessage) {
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.okMessage = okMessage;
        this.cancelMessage = cancelMessage;
        return this;
    }

    public BaseDialog setDialogCallBack(DialogInterface dialogInterface) {
        this.dialogInterface = dialogInterface;
        return this;
    }

    public BaseDialog setCanceledOnKeyBack(boolean canceledOnKeyBack) {
        this.canceledOnKeyBack = canceledOnKeyBack;
        return this;
    }

    public Resources getRes() {
        return this.context.getResources();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.canceledOnKeyBack && keyCode == 4?true:super.onKeyDown(keyCode, event);
    }
}
