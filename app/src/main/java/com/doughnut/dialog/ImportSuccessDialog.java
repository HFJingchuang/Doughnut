package com.doughnut.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.update.APKDownLoad;


public class ImportSuccessDialog extends BaseDialog {

    private final static String TAG = "PKDialog";

    private TextView mTvUpgrade;
    private String mUpgradeUrl;

    public ImportSuccessDialog(@NonNull Context context) {
        super(context, R.style.DialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_import_success);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -2;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        initView();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    private void initView() {
//        mTvUpgrade = (TextView) findViewById(R.id.tv_upgrade);
//        mTvUpgrade.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(mUpgradeUrl)) {
//                    dismiss();
//                    return;
//                }
//                APKDownLoad.downLoad(getContext(), mUpgradeUrl);
//                dismiss();
////                AppConfig.getContext().clearActivity();
////                android.os.Process.killProcess(android.os.Process.myPid());
////                System.exit(0);
//
//            }
//        });
    }
}
