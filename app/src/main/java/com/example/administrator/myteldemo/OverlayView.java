package com.example.administrator.myteldemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/* renamed from: dg.ca.com.DGPhone.phone.OverlayView */
public class OverlayView extends Overlay {
    public static final String EXTRA_PHONE_NUM = "phoneNum";
    public static final int MSG_FAILED = 4097;
    public static final int MSG_OK = 4096;
    private static Activity mContext = null;

    public static void show(Activity context, String number, int percentScreen) {
        synchronized (monitor) {
            mContext = context;
            init(context, number, R.layout.pop_tel_come, percentScreen);
            if (mOverlay != null) {
                try {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(2, 1);
                    imm.toggleSoftInput(1, 0);
                    imm.hideSoftInputFromWindow(mOverlay.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void hide(Context context) {
        synchronized (monitor) {
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            if (mOverlay != null) {
                try {
                    wm.removeView(mOverlay);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(2, 1);
                    imm.toggleSoftInput(1, 0);
                    imm.hideSoftInputFromWindow(mOverlay.getWindowToken(), 0);
                    mOverlay = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ViewGroup init(Context context, String number, int layout, int percentScreen) {
        LayoutParams params = getShowingParams();
        ViewGroup overlay = init(context, layout, params);
        initView(overlay, number, percentScreen);
        return overlay;
    }

    private static void initView(View v, String phoneNum, int percentScreen) {
        FitTextView fitTextView = v.findViewById(R.id.tv_tel_come);
        fitTextView.setText(phoneNum);
    }

    private static LayoutParams getShowingParams() {
        LayoutParams params = new LayoutParams();
        params.type = LayoutParams.TYPE_SYSTEM_ERROR;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        params.x = 0;
        params.y = 0;
        params.format = 1;
        params.gravity = 5;
        params.flags = 7274787;
        params.screenOrientation = 0;
        return params;
    }

    private static int getHeight(Context context, int percentScreen) {
        return (getLarger(context) * percentScreen) / 100;
    }

    private static int getLarger(Context context) {
        int height;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (Utils.hasHoneycombMR2()) {
            height = getLarger(display);
        } else if (display.getHeight() > display.getWidth()) {
            height = display.getHeight();
        } else {
            height = display.getWidth();
        }
        System.out.println("getLarger: " + height);
        return height;
    }

    @TargetApi(13)
    private static int getLarger(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.y > size.x ? size.y : size.x;
    }
}
