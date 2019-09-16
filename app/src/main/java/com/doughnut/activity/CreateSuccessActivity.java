package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;


public class CreateSuccessActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnBackup;
    private TextView mTvWarning, mTvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_success);
        initView();
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initView() {
        mBtnBackup = findViewById(R.id.btn_backup);
        mBtnBackup.setOnClickListener(this);
        mTvWarning = findViewById(R.id.tv_warning);
        mTvWarning.setText(Html.fromHtml(getString(R.string.tv_warning)));
        mTvSkip = findViewById(R.id.tv_skip);
        mTvSkip.setOnClickListener(this);
    }

    /**
     * 画面按钮事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backup:
                if (getIntent() != null) {
                    String address = getIntent().getStringExtra(Constant.WALLET_ADDRESS);
                    String privateKey = getIntent().getStringExtra(Constant.PRIVATE_KEY);
                    WalletExportActivity.startExportWalletActivity(this, address, privateKey);
                    finish();
                }
                break;
            case R.id.tv_skip:
                MainActivity.startMainActivity(this);
                finish();
                break;
        }
    }

    public static void startCreateSuccessActivity(Context context, String address, String privateKey) {
        Intent intent = new Intent(context, CreateSuccessActivity.class);
        intent.putExtra(Constant.WALLET_ADDRESS, address);
        intent.putExtra(Constant.PRIVATE_KEY, privateKey);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
