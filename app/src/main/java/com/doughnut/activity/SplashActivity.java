package com.doughnut.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.utils.NetUtil;
import com.doughnut.utils.PermissionUtil;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.wallet.WalletManager;
import com.yasic.library.particletextview.MovingStrategy.BidiHorizontalStrategy;
import com.yasic.library.particletextview.Object.ParticleTextViewConfig;
import com.yasic.library.particletextview.View.ParticleTextView;

public class SplashActivity extends BaseActivity {

    private final static String TAG = "SplashActivity";
    private ParticleTextView mTvSpalsh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        if (!NetUtil.isNetworkAvailable(this)) {
            ToastUtil.toast(this, getString(R.string.toast_no_network));
        }
        mTvSpalsh = (ParticleTextView) findViewById(R.id.tv_splash);
        BidiHorizontalStrategy movingStrategy5 = new BidiHorizontalStrategy();
        ParticleTextViewConfig config1 = new ParticleTextViewConfig.Builder()
                .setTargetText(getString(R.string.content_splash))
                .setReleasing(0.03)
                .setParticleRadius(ViewUtil.dip2px(this, 1))
                .setTextSize(ViewUtil.dip2px(this, 25))
                .setMiniDistance(ViewUtil.dip2px(this, 0.02f))
                .setColumnStep(ViewUtil.dip2px(this, 1))
                .setRowStep(ViewUtil.dip2px(this, 1))
                .setIsLoop(false)
                .setParticleColorArray(new String[]{"#25A886", "#F55758", "#3B6CA6"})
                .instance();
        mTvSpalsh.setConfig(config1);
        mTvSpalsh.startAnimation();
        checkPermission();
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
            mTvSpalsh.setAnimationFrozen();
            MainActivity.startMainActivity(SplashActivity.this);
            SplashActivity.this.finish();
        }, 3000);
    }

}
