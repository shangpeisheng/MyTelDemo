package com.example.administrator.myteldemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/* renamed from: dg.ca.com.DGPhone.phone.Overlay */
public abstract class Overlay {
    public static ViewGroup mOverlay;
    protected static final Object monitor = new Object();

    protected static ViewGroup init(Context context, int layout, LayoutParams params) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (mOverlay != null) {
            try {
                wm.removeView(mOverlay);
                mOverlay = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ViewGroup overlay = (ViewGroup) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, null);
        mOverlay = overlay;
        wm.addView(overlay, params);
        return overlay;
    }

    public static void show(Context context, String number) {
    }

    public static void hide(Context context) {
    }
}
