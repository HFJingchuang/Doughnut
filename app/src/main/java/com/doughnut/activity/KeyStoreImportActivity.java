package com.doughnut.activity;

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

public class KeyStoreImportActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;

    private ImageView mImgServiceTerms;
    private TextView mTvServiceTerms;

    private EditText mEKeyStore, mEWalletName, mEWalletPwd;

    private Button mBtnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_store_import);

        initView();
    }

    /**
     * 画面初期化
     */
    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.select_keyStore_import);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();

            }
        });

        mImgServiceTerms = findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);

        mEKeyStore = findViewById(R.id.keyStore_text);
        mEWalletName = findViewById(R.id.edt_wallet_name);
        mEWalletPwd = findViewById(R.id.edt_wallet_pwd);

        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 导入按钮
            case R.id.btn_confirm:
                if (paramCheck()) {
                    String keyStore = mEKeyStore.getText().toString();
                    String walletName = mEWalletName.getText().toString();
                    String walletPwd = mEWalletPwd.getText().toString();
                    // 导入钱包
                    importWallet(keyStore, walletName, walletPwd);
                    // TODO 暂时跳转到钱包管理
//                    Intent intent = new Intent(this, WalletManageActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    this.finish();
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
        }
    }

    /**
     * 导入钱包
     * @param walletName
     * @param walletPwd
     */
    private void importWallet(final String  keyStore, final String walletName, final String walletPwd) {

        WalletManager walletManager =  WalletManager.getInstance(this);
        // 导入钱包
        walletManager.importKeysStore(keyStore, walletName);
//         获取钱包私钥
//        walletManager.getPrivateKey(walletPwd,walletName);
    }

    /**
     * 前端页面校验
     * @return
     */
    private boolean paramCheck() {

        String keyStore = mEKeyStore.getText().toString();
        String walletName = mEWalletName.getText().toString();
        String walletPwd = mEWalletPwd.getText().toString();
//        String walletPwdRepeat = mEWalletPwdConfirm.getText().toString();
        boolean readedTerms = mImgServiceTerms.isSelected();

        if (TextUtils.isEmpty(keyStore)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_keyStore), "OK");
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

//        if (TextUtils.isEmpty(walletPwdRepeat)) {
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_verify_password), "OK");
//            return false;
//        }
//
//        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_passwords_unmatch), "OK");
//            return false;
//        }

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
