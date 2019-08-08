package com.doughnut.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;
import com.zxing.activity.CaptureActivity;

public class PrivateKeyImportActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;

    private ImageView mImgServiceTerms,mimg_scan;
    private TextView mTvServiceTerms;

    private EditText mEPrivateKey, mEWalletName, mEWalletPwd, mEWalletPwdConfirm;

    private Button mBtnConfirm;
    private Integer SCAN_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_key_import);
        initView();
    }

    /**
     * 画面初期话
     */
    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.select_privateKey_import);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });
        mimg_scan = findViewById(R.id.img_scan);
        mimg_scan.setOnClickListener(this);
        mImgServiceTerms = findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);

        mEPrivateKey = findViewById(R.id.edt_private_key);
        mEWalletName = findViewById(R.id.edt_wallet_name);
        mEWalletPwd = findViewById(R.id.edt_wallet_pwd);
        mEWalletPwdConfirm = findViewById(R.id.edt_wallet_pwd_confirm);

        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String result = data.getStringExtra("scan_result");
                 if (!result.isEmpty()) {
                     mEPrivateKey.setText(result);
                 }
        } else {
            this.finish();
        }


    }



    /**
     * 页面点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 导入按钮
            case R.id.btn_confirm:
                if (paramCheck()) {
                    String privateKey = mEPrivateKey.getText().toString();
                    String walletName = mEWalletName.getText().toString();
                    String walletPwd = mEWalletPwd.getText().toString();
                    // 导入钱包
                    importWallet(privateKey, walletName, walletPwd);
                }
                break;
            // 勾选框
            case R.id.img_service_terms:
                mImgServiceTerms.setSelected(!mImgServiceTerms.isSelected());
                break;
            // 跳转服务条款页面
            case R.id.tv_service_terms:
                gotoServiceTermPage();
                break;
            case R.id.img_scan:
                Intent intent=new Intent(PrivateKeyImportActivity.this, CaptureActivity.class);
                startActivityForResult(intent,SCAN_CODE);
                break;
        }
    }

    /**
     * 导入钱包
     * @param walletName
     * @param walletPwd
     */
    private void importWallet(final String  privateKey, final String walletName, final String walletPwd) {

        WalletManager walletManager =  WalletManager.getInstance(this);
        // 创建钱包
        walletManager.importWalletWithKey(walletPwd, privateKey, walletName);
//         获取钱包私钥
//        walletManager.getPrivateKey(walletPwd,walletName);
    }

    /**
     * 前端页面校验
     * @return
     */
    private boolean paramCheck() {

        String privateKey = mEPrivateKey.getText().toString();
        String walletName = mEWalletName.getText().toString();
        String walletPwd = mEWalletPwd.getText().toString();
        String walletPwdRepeat = mEWalletPwdConfirm.getText().toString();
        boolean readedTerms = mImgServiceTerms.isSelected();

        if (TextUtils.isEmpty(privateKey)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_private_key), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletName)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_name), "OK");
            return false;
        }
        if (TextUtils.isEmpty(walletPwd)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_password), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletPwdRepeat)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_verify_password), "OK");
            return false;
        }

        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_passwords_unmatch), "OK");
            return false;
        }

        if (walletPwd.length() < 8) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_short_password), "OK");
            return false;
        }

        if (!readedTerms) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_read_service), "OK");
            return false;
        }

        return true;
    }


    /**
     * 跳转服务条款页面
     */
    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(this, getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }


}
