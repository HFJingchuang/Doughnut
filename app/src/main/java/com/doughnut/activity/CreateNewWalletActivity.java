
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;


public class CreateNewWalletActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "CreateNewWalletActivity";

    private TitleBar mTitleBar;

    private EditText mEdtWalletName, mEdtWalletPwd, mEdtWalletPwdConfirm;
    private ImageView mImgServiceTerms;
    private TextView mTvServiceTerms, mTVAlertWalletName, mTVAlertPsd, mTVAlertPsdRep, mTVAlertServiceTerms;

    private Button mBtnConfirm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_wallet);
        initView();
    }


    /**
     * 页面初始化
     */
    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.btn_create_wallet);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });
        mEdtWalletName = findViewById(R.id.edt_wallet_name);
        mEdtWalletPwd = findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwdConfirm = findViewById(R.id.edt_wallet_pwd_confirm);

        mImgServiceTerms = findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);
        mBtnConfirm = findViewById(R.id.btn_confirm);

        mTVAlertWalletName = findViewById(R.id.alert_wallet_name);
        mTVAlertPsd = findViewById(R.id.alert_psd);
        mTVAlertPsdRep = findViewById(R.id.alert_psd_rep);
        mTVAlertServiceTerms = findViewById(R.id.alert_service_terms);

        mBtnConfirm.setOnClickListener(this);
    }

    /**
     * 画面按钮事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 创建钱包按钮
            case R.id.btn_confirm:
                if (paramCheck()) {
                    String walletName = mEdtWalletName.getText().toString();
                    String walletPwd = mEdtWalletPwd.getText().toString();
                    // 创建钱包
                    createWallet(walletName, walletPwd);
                    // TODO 后面改成跳转到备份页面
//                    Intent intent = new Intent(this, BackupStartActivity.class);
//                    startActivity(intent);
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
     * 创建钱包
     *
     * @param walletName
     * @param walletPwd
     */
    private void createWallet(final String walletName, final String walletPwd) {

        WalletManager walletManager = WalletManager.getInstance(this);
        // 创建钱包
        walletManager.createWallet(walletPwd, walletName);
        // 获取钱包私钥
        walletManager.getPrivateKey(walletPwd, walletName);
    }

    /**
     * 前端页面校验
     *
     * @return
     */
    private boolean paramCheck() {

        String walletName = mEdtWalletName.getText().toString();
        String walletPwd = mEdtWalletPwd.getText().toString();
        String walletPwdRepeat = mEdtWalletPwdConfirm.getText().toString();
        boolean readedTerms = mImgServiceTerms.isSelected();

        if (TextUtils.isEmpty(walletName)) {
            // TODO
            mTVAlertWalletName.setText(R.string.dialog_content_no_wallet_name);
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_name), "OK");
            return false;
        } else {
            mTVAlertWalletName.setText("");
        }
        if (TextUtils.isEmpty(walletPwd)) {
            mTVAlertPsd.setText(R.string.dialog_content_no_password);
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_password), "OK");
            return false;
        } else {
            mTVAlertPsd.setText("8-64位大小写字母、数字组合密码");
        }

        if (TextUtils.isEmpty(walletPwdRepeat)) {
            mTVAlertPsdRep.setText(R.string.dialog_content_no_verify_password);
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_verify_password), "OK");
            return false;
        } else {
            mTVAlertPsdRep.setText("");
        }

        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
            mTVAlertPsdRep.setText(R.string.dialog_content_passwords_unmatch);
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_passwords_unmatch), "OK");
            return false;
        } else {
            mTVAlertPsdRep.setText("");
        }

        if (!walletPwd.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{8,64}$")) {

            mTVAlertPsd.setText("8-64位大小写字母、数字组合密码");
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_password), "OK");
            return false;
        } else {
            mTVAlertPsd.setText("8-64位大小写字母、数字组合密码");
        }

        if (!readedTerms) {
            mTVAlertServiceTerms.setText(R.string.dialog_content_no_read_service);
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_read_service), "OK");
            return false;
        } else {
            mTVAlertServiceTerms.setText("");
        }

        return true;
    }

    /**
     * 跳转服务条款页面
     */
    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(this, getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }

    public static void startCreateNewWalletActivity(Context from) {
        Intent intent = new Intent(from, CreateNewWalletActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }
}
