package com.doughnut.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.utils.ToastUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;


public class ModifyPwdActivity extends BaseActivity implements TitleBar.TitleBarClickListener, View.OnClickListener {


    private TitleBar mTitleBar;

    private EditText mEdtOldPwd;
    private EditText mEdtNewPwd;
    private EditText mEdtReaptNewPwd;

    private TextView mTvForgetPwdTips;
    private String mWalletAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        if (getIntent() != null) {
            mWalletAddress = getIntent().getStringExtra("Wallet_Address");
        }
        initView();
    }

    @Override
    public void onLeftClick(View view) {
        finish();
    }

    @Override
    public void onRightClick(View view) {
        if (TextUtils.isEmpty(mEdtOldPwd.getText().toString())) {
            showTipAlertDialog(getString(R.string.dialog_content_no_old_password));
            return;
        }
        if (TextUtils.isEmpty(mEdtNewPwd.getText().toString())) {
            showTipAlertDialog(getString(R.string.dialog_content_no_new_password));
            return;
        }

        if (TextUtils.isEmpty(mEdtReaptNewPwd.getText().toString())) {
            showTipAlertDialog(getString(R.string.dialog_content_no_verify_new_password));
            return;
        }
        if (!TextUtils.equals(mEdtReaptNewPwd.getText().toString(), mEdtNewPwd.getText().toString())) {
            showTipAlertDialog(getString(R.string.dialog_new_passwords_unmatch));
            return;
        }
        if (mEdtNewPwd.getText().toString().length() < 8) {
            showTipAlertDialog(getString(R.string.dialog_content_short_password));
            return;
        }
        if (TextUtils.equals(mEdtNewPwd.getText().toString(), mEdtOldPwd.getText().toString())) {
            showTipAlertDialog(getString(R.string.dialog_content_old_new_same));
            return;
        }

        String oldKey = WalletManager.getInstance(this).getPrivateKey(mEdtOldPwd.getText().toString(), mWalletAddress);
        if (TextUtils.isEmpty(oldKey)) {
            showTipAlertDialog(getString(R.string.dialog_content_old_password_incorrect));
            mEdtOldPwd.setText("");
            return;
        }

        // 修改KeyStore密码，name参数可不传
        String res = WalletManager.getInstance(this).importWalletWithKey(mEdtNewPwd.getText().toString(), oldKey, "");
        if (TextUtils.equals(res, mWalletAddress)) {
            ToastUtil.toast(ModifyPwdActivity.this, getString(R.string.toast_password_changed));
            this.finish();
        } else {
            showTipAlertDialog("修改失败，请重新尝试。");
        }
    }

    @Override
    public void onMiddleClick(View view) {

    }

    @Override
    public void onClick(View view) {
        if (view == mTvForgetPwdTips) {
            gotoImportPrivateKeyFragment();
        }
    }


    public static void startModifyPwdActivity(Context context, String walletAddress) {
        Intent intent = new Intent(context, ModifyPwdActivity.class);
        intent.putExtra("Wallet_Address", walletAddress);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);

        mTitleBar.setTitle(getString(R.string.titleBar_change_pwd));

        mTitleBar.setRightText(getString(R.string.titleBar_completed));
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mEdtOldPwd = findViewById(R.id.edt_old_pwd);
        mEdtNewPwd = findViewById(R.id.edt_new_pwd);
        mEdtReaptNewPwd = findViewById(R.id.edt_new_repeat_pwd);
        mTvForgetPwdTips = findViewById(R.id.tv_forgetpwd_tip);
        mTvForgetPwdTips.setText(Html.fromHtml(getString(R.string.content_forgot_password)));
        mTvForgetPwdTips.setOnClickListener(this);
    }

    private void showTipAlertDialog(String tips) {
        new AlertDialog.Builder(ModifyPwdActivity.this).setTitle(tips).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void gotoImportPrivateKeyFragment() {
        ImportWalletActivity.startImportWalletActivity(ModifyPwdActivity.this);
    }
}
