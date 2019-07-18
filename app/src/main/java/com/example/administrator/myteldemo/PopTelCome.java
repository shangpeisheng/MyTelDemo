package com.example.administrator.myteldemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Administrator on 2019/7/11.
 */

public class PopTelCome {
    private LayoutInflater inflater;
    private Context mContext;
    private int width;
    private int height;
    private View indexView;
    public PopupWindow popupWindow;
    private View popContentView;
    private TextView tvTelCome;

    public PopTelCome(Context mContext, View indexView, int width, int height) {
        this.mContext = mContext;
        this.width = width;
        this.height = height;
        this.indexView = indexView;
        inflater = LayoutInflater.from(mContext);
        initPop();
    }

    private void initPop() {
        popContentView = inflater.inflate(R.layout.pop_tel_come, null);
        initView();
        popupWindow = new PopupWindow(popContentView, width, height, true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initView() {
        tvTelCome = (TextView) popContentView.findViewById(R.id.tv_tel_come);
    }

    public void setTelComeContent(String tel) {
        tvTelCome.setText(tel);
    }

    public void showCenterWindow() {
        closeDialogWindow();
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            popupWindow.showAtLocation(indexView, Gravity.CENTER, 0, 0);
        }
    }

    public void closeDialogWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
