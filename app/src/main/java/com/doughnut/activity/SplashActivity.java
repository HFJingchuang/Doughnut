package com.doughnut.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.utils.NetUtil;
import com.doughnut.utils.PermissionUtil;
import com.doughnut.utils.ToastUtil;
import com.doughnut.wallet.WalletManager;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "SplashActivity";

    private LinearLayout mLayoutSplashBtn;
    private TextView mTvCreateWallet;
    private TextView mTvImportWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        // 启动页背景图片
        mLayoutSplashBtn = (LinearLayout) findViewById(R.id.layout_splash_btn);
        // 启动页创建钱包按钮
        mTvCreateWallet = (TextView) findViewById(R.id.tv_create_wallet);
        mTvCreateWallet.setOnClickListener(this);
        // 启动页导入钱包按钮
        mTvImportWallet = (TextView) findViewById(R.id.tv_import_wallet);
        mTvImportWallet.setOnClickListener(this);
        if (!NetUtil.isNetworkAvailable(this)) {
            ToastUtil.toast(this, getString(R.string.toast_no_network));
        }
        checkPermission();
    }

    @Override
    public void onClick(View v) {
        // 创建钱包按钮点击事件
        if (v == mTvCreateWallet) {
            CreateNewWalletActivity.startCreateNewWalletActivity(this);
        }
        // 导入钱包按钮点击事件
        else if (v == mTvImportWallet) {
            WalletImportActivity.startImportWalletActivity(this);
        }
    }

    public static void startSplashActivity(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    // 权限检查
    private void checkPermission() {
        final String permissions[] = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        boolean permission = false;
        if (!PermissionUtil.needCheckPermission()) {
            permission = true;
        } else {
            String[] var1 = PermissionUtil.lackPermissions(SplashActivity.this, permissions);
            if (var1 == null || var1.length == 0) {
                permission = true;
            }
        }

        if (!permission) {
            PermissionUtil.doWithPermissionChecked(SplashActivity.this, permissions, new PermissionUtil.CheckTipCallback2() {
                @Override
                public void onPermissionGranted() {
                    permissonSuccess();
                }

                @Override
                public void onPermissionDenied(final String[] lackedPermissions) {
                    PermissionUtil.getInstance().attachPermissionCheckCallback(new PermissionUtil.CheckCallback2() {
                        @Override
                        public void onPermissionGranted() {
                            PermissionUtil.getInstance().detachPermissionCheckCallback(this);
                            permissonSuccess();
                        }

                        @Override
                        public void onPermissionDenied(String[] var1) {
                            PermissionUtil.getInstance().detachPermissionCheckCallback(this);
                            PermissionUtil.showPermSetDialog(SplashActivity.this, false, lackedPermissions);
                        }
                    });
                    PermissionUtil.getInstance().requestPermissions(SplashActivity.this, lackedPermissions);
                }

                @Override
                public void onUserOnceDenied(String[] lackedPermissions) {
                    PermissionUtil.showPermSetDialog(SplashActivity.this, false, lackedPermissions);
                }
            });
        } else {
            permissonSuccess();
        }
    }

    private void permissonSuccess() {
        WalletManager.getInstance(this).getAllTokens();
        AppConfig.postDelayOnUiThread(() -> {
            if (WalletManager.getInstance(SplashActivity.this).hasWallet()) {
                MainActivity.startMainActivity(SplashActivity.this);
                SplashActivity.this.finish();
            } else {
                playAlphaAnim();
            }

        }, 1000);
    }

    private void playAlphaAnim() {
        mLayoutSplashBtn.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            mLayoutSplashBtn.setAlpha(alpha);
        });
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }
}
