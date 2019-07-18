package com.example.administrator.myteldemo;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FitHeightTextView tvTitle;
    FitTextView tvDate1, tvDate2, tvTel;
    LinearLayout llPrent;
    private ObjectAnimator firstAnimator;
    private ObjectAnimator infiniteAnimator;
    public int screenWidth;
    public int screenHeight;
    DialogInput dialogInput;
    PopTelCome popTelCome;
    int slideTime = 40000;
    double averageTime;
    HorizontalScrollView hSv;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (popTelCome != null) {
                        popTelCome.setTelComeContent((String) msg.obj);//展示
                        popTelCome.showCenterWindow();
                    }
                    break;
                case 2:
                    if (popTelCome != null) {
                        popTelCome.closeDialogWindow();
                    }
                    break;
            }
        }
    };
    private static final String tag = "PhoneListenService";
    // 电话管理者对象
    private TelephonyManager mTelephonyManager;
    // 电话状态监听者
    private MyPhoneStateListener myPhoneStateListener;
    // 动态监听去电的广播接收器
    private InnerOutCallReceiver mInnerOutCallReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;         // 屏幕宽度（像素）
        screenHeight = dm.heightPixels;       // 屏幕高度（像素）
        llPrent = (LinearLayout) findViewById(R.id.ll_parent);
        tvTitle = (FitHeightTextView) findViewById(R.id.tv_title);
        tvDate1 = (FitTextView) findViewById(R.id.tv_date1);
        tvDate2 = (FitTextView) findViewById(R.id.tv_date2);
        tvTel = (FitTextView) findViewById(R.id.tv_tel);
        hSv = (HorizontalScrollView) findViewById(R.id.h_sv);
        hSv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ContentInfoBean infoBean = (ContentInfoBean) Utils.getObjectData(this, Utils.ContentInfo);
        if (infoBean == null) {
            infoBean = new ContentInfoBean(getString(R.string.input_title), getString(R.string.input_tel));
            Utils.saveObjectData(MainActivity.this, Utils.ContentInfo, infoBean);
        }
        String title = infoBean.getTitle();
        String tel = infoBean.getTel();
        tvTitle.setText(title);
        tvTitle.measure(0, 0);
        if (tvTitle.getMeasuredWidth() > screenWidth) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnimation();
                }
            }, 1000);
        }
        tvTel.setText(tel);
        tvTitle.setOnClickListener(this);
        tvTel.setOnClickListener(this);
        dialogInput = new DialogInput(this, screenHeight / 5 * 4, ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            public void onClose() {
                tvTitle.setText(getTitle());
                tvTel.setText(getTel());
                ContentInfoBean infoBean = new ContentInfoBean(getTitle(), getTel());
                Utils.saveObjectData(MainActivity.this, Utils.ContentInfo, infoBean);
                startAnimation();
            }
        };
        popTelCome = new PopTelCome(this, tvDate1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initDate();
        initTelManager();
    }


    private void initTelManager() {
        requestPermission();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        // 动态注册广播接收器监听去电信息
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        // 手机拨打电话时会发送：android.intent.action.NEW_OUTGOING_CALL的广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mInnerOutCallReceiver, intentFilter);
    }

    private void initDate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (tvDate1 != null) {
                    tvDate1.setText(getDate("HH:mm aa (zz)"));
                }
                if (tvDate2 != null) {
                    tvDate2.setText(getDate("dd/MM/yyyy"));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void startAnimation() {
        tvTitle.measure(0, 0);
        averageTime = slideTime / tvTitle.getMeasuredWidth();
        if (tvTitle.getMeasuredWidth() <= screenWidth) {
            return;
        }
        if (firstAnimator != null) {
            firstAnimator.cancel();
            firstAnimator = null;
        }
        firstAnimator = ObjectAnimator.ofFloat(tvTitle, "translationX", 0, -tvTitle.getMeasuredWidth());
        firstAnimator.setDuration(slideTime);
        firstAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (infiniteAnimator != null) {
                    infiniteAnimator.cancel();
                    infiniteAnimator = null;
                }
                tvTitle.measure(0, 0);
                infiniteAnimator = ObjectAnimator.ofFloat(tvTitle, "translationX", screenWidth - (llPrent.getPaddingLeft() + llPrent.getPaddingRight() * 5), -tvTitle.getMeasuredWidth());
                infiniteAnimator.setDuration((long) (averageTime * (tvTitle.getMeasuredWidth() + screenWidth)));
                infiniteAnimator.setRepeatCount(ValueAnimator.INFINITE);
                infiniteAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        firstAnimator.start();
    }

    private void cancelAnimation() {
        if (firstAnimator != null) {
            firstAnimator.cancel();
        }
        firstAnimator = null;
        if (infiniteAnimator != null) {
            infiniteAnimator.cancel();
        }
        infiniteAnimator = null;
        tvTitle.setX(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title:
            case R.id.tv_tel:
                cancelAnimation();
                dialogInput.showDialogCenterWindowBlack();
                dialogInput.setCursor();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        cancelAnimation();
        // 取消来电的电话状态监听服务
        if (mTelephonyManager != null && myPhoneStateListener != null) {
            mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        // 取消去电的广播监听
        if (mInnerOutCallReceiver != null) {
            unregisterReceiver(mInnerOutCallReceiver);
        }
    }

    private String getDate(String format) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        return df.format(date);
    }

    /**
     * 动态注册广播接收器监听去电信息
     */
    class InnerOutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取播出的去电号码
            String outPhone = getResultData();
            Log.i(tag, "outPhone:" + outPhone);
        }
    }

    /**
     * 自定义内部类对来电的电话状态进行监听
     */
    class MyPhoneStateListener extends PhoneStateListener {
        // 重写电话状态改变时触发的方法
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(tag, "响铃:" + incomingNumber);
                    handler.sendMessageDelayed(handler.obtainMessage(1, incomingNumber), 2000);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(tag, "接听");
                    handler.sendEmptyMessage(2);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(tag, "挂断");
                    handler.sendEmptyMessage(2);
                    break;
            }
        }
    }

    public void requestPermission() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
