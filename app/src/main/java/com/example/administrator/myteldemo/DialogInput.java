package com.example.administrator.myteldemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Administrator on 2019/7/11.
 */

public class DialogInput {
    private LayoutInflater inflater;
    private Context mContext;
    private int width;
    private int height;
    public Dialog dialog;
    private View popContentView;
    private EditText edtTitle;
    private EditText edtTel;

    public DialogInput(Context mContext, int width, int height) {
        this.mContext = mContext;
        this.width = width;
        this.height = height;
        inflater = LayoutInflater.from(mContext);
        initDialog();
    }

    private void initDialog() {
        popContentView = inflater.inflate(R.layout.pop_input, null);
        initView();
        dialog = new Dialog(mContext, R.style.Dialog);
        dialog.setContentView(popContentView);
        dialog.setCancelable(false);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = width;
        params.height = height;
        dialog.getWindow().setAttributes(params);
        dialog.setOnDismissListener(new onDismissListener());
    }

    private void initView() {
        Button btnFinish = (Button) popContentView.findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialogWindow();
            }
        });
        ContentInfoBean infoBean = (ContentInfoBean) Utils.getObjectData(mContext, Utils.ContentInfo);
        edtTitle = (EditText) popContentView.findViewById(R.id.edt_title);
        edtTitle.setText(infoBean.getTitle());
        edtTel = (EditText) popContentView.findViewById(R.id.edt_tel);
        edtTel.setText(infoBean.getTel());
    }

    public String getTitle() {
        return edtTitle.getText().toString().trim();
    }

    public String getTel() {
        return edtTel.getText().toString().trim();
    }

    class onDismissListener implements Dialog.OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {
            onClose();
        }
    }

    public void onClose() {

    }

    public void setCursor() {
        edtTitle.setSelection(getTitle().length());
        edtTel.setSelection(getTel().length());
    }

    public void showDialogCenterWindowBlack() {
        closeDialogWindow();
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }

    public void closeDialogWindow() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
