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


public class UpgradeDialog extends BaseDialog {

    private TextView mTvUpgrade;
    private String mUpgradeUrl;

    public UpgradeDialog(@NonNull Context context, String upgradeUrl) {
        super(context, R.style.DialogStyle);
        this.mUpgradeUrl = upgradeUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.layout_dialog_upgrade);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setDimAmount(0f);
        initView();
    }

    private void initView() {
        mTvUpgrade = (TextView) findViewById(R.id.tv_upgrade);
        mTvUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUpgradeUrl)) {
                    dismiss();
                    return;
                }
                APKDownLoad.downLoad(getContext(), mUpgradeUrl);
                dismiss();
            }
        });
    }
}
