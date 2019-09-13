package com.doughnut.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.AppConfig;


public class ImportSuccessDialog extends BaseDialog {

    private TextView mTvUpgrade;
    private String mWalletName;

    public ImportSuccessDialog(@NonNull Context context, String walletName) {
        super(context, R.style.DialogStyle);
        mWalletName = walletName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.layout_dialog_import_success);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        initView();
    }

    @Override
    public void onBackPressed() {
    }


    private void initView() {
        mTvUpgrade = (TextView) findViewById(R.id.tv_upgrade);
        mTvUpgrade.setText(String.format(getContext().getString(R.string.tv_dialog_name), mWalletName));

        // 5秒后关闭
        AppConfig.postDelayOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 5000);
    }
}
