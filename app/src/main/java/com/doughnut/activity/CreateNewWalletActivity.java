
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;


public class CreateNewWalletActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "CreateNewWalletActivity";

    private TitleBar mTitleBar;

    private EditText mEdtWalletName, mEdtWalletPwd, mEdtWalletPwdConfirm;
    private ImageView mImgServiceTerms, imgShowRepPsd, imgShowPsd;
    private TextView mTvServiceTerms, mTVAlertWalletName, mTVAlertPsd, mTVAlertPsdRep, mTVAlertServiceTerms, mTvErrWalletName, mTvErrPassword, mTvPsdRep, mTvAlertServiceTerms;

    private LinearLayout mTvShowPsd, mTvShowPsdRep;

    private Button mBtnConfirm;
    private boolean isShowPsd, isShowPsdRep;


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

        mTvErrWalletName = findViewById(R.id.err_wallet_name);
        mTvErrPassword = findViewById(R.id.err_password);
        mTvPsdRep = findViewById(R.id.err_psd_rep);
        mTvAlertServiceTerms = findViewById(R.id.alert_service_terms);

        mEdtWalletName = findViewById(R.id.edt_wallet_name);
        mEdtWalletName.requestFocus();
        mEdtWalletName.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isCreate();
                        if (mTvErrWalletName.isShown()) {
                            mTvErrWalletName.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String address = mEdtWalletName.getText().toString();
                    if (!TextUtils.isEmpty(address)) {
//                        String currentAddr = WalletSp.getInstance(CreateNewWalletActivity.this, "").getCurrentWallet();
//                        if (TextUtils.equals(currentAddr, address)) {
//                            mTvErrWalletName.setText(getString(R.string.tv_err_address1));
//                            mTvErrWalletName.setVisibility(View.VISIBLE);
//                            AppConfig.postOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mEdtWalletName.requestFocus();
//                                }
//                            });
//                            return;
//                        }
//                        boolean isValidAddress = Wallet.isValidAddress(address);
//                        if (!isValidAddress) {
//                            mTvErrWalletName.setVisibility(View.VISIBLE);
//                            mTvErrWalletName.setText(getString(R.string.tv_err_address));
//                            AppConfig.postOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mEdtWalletName.requestFocus();
//                                }
//                            });
//                        }
                    }
                }
            }
        });



        mEdtWalletPwd = findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwd.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isCreate();
                        if (mTvErrPassword.isShown()) {
                            mTvErrPassword.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWalletPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mEdtWalletPwd.getText().toString();
                    if (!TextUtils.isEmpty(passWord)) {
                        if (!passWord.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{8,64}$")) {
                            mTvErrPassword.setText("8-64位大小写字母、数字组合密码");
                            mTvErrPassword.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletPwd.requestFocus();
                                }
                            });
                            return;
                        }
                    }
                }
            }
        });
        mEdtWalletPwdConfirm = findViewById(R.id.edt_wallet_pwd_confirm);
        mEdtWalletPwdConfirm.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isCreate();
                        if (mTvPsdRep.isShown()) {
                            mTvPsdRep.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mEdtWalletPwdConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mEdtWalletPwd.getText().toString();
                    String passwordConfim = mEdtWalletPwdConfirm.getText().toString();
                    if (!TextUtils.isEmpty(passwordConfim)) {
                        if (!passwordConfim.equals(passWord)) {
                            mTvPsdRep.setText(getString(R.string.dialog_content_passwords_unmatch));
                            mTvPsdRep.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletPwdConfirm.requestFocus();
                                }
                            });
                            return;
                        }
                    }
                }
            }
        });



        mImgServiceTerms = findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);
        mBtnConfirm = findViewById(R.id.btn_confirm);

//        mTVAlertWalletName = findViewById(R.id.alert_wallet_name);
        mTVAlertPsd = findViewById(R.id.alert_psd);
//        mTVAlertPsdRep = findViewById(R.id.alert_psd_rep);
        mTVAlertServiceTerms = findViewById(R.id.alert_service_terms);

        mTvShowPsd = findViewById(R.id.show_psd);
        mTvShowPsd.setOnClickListener(this);
        mTvShowPsdRep = findViewById(R.id.show_psd_rep);
        mTvShowPsdRep.setOnClickListener(this);

        imgShowPsd = findViewById(R.id.img_show_psd);
        imgShowRepPsd = findViewById(R.id.img_show_rep_psd);

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
                if (true) {
                    String walletName = mEdtWalletName.getText().toString();
                    String walletPwd = mEdtWalletPwd.getText().toString();
                    // 创建钱包
                    createWallet(walletName, walletPwd);
                    //
//                    Intent intent = new Intent(this, BackupStartActivity.class);
//                    startActivity(intent);
//                    Intent intent = new Intent(this, WalletManageActivity.class);
//                    startActivity(intent);

//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
//                    this.finish();

                    CreateSuccessActivity.startCreateSuccessActivity(this);
                }
                break;
            // 勾选框
            case R.id.img_service_terms:
                mImgServiceTerms.setSelected(!mImgServiceTerms.isSelected());
                isCreate();
                break;
            // 跳转服务条款页面
            case R.id.tv_service_terms:
                gotoServiceTermPage();
                break;
            case R.id.show_psd:
                showPassWord("password");
                break;
            case R.id.show_psd_rep:
                showPassWord("passwordConfim");
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
     * 判断密码显示隐藏
     *
     * @param type
     */
    private void showPassWord(String type) {
        if ("password".equals(type)) {
            // 密码是否显示(是)
            if (isShowPsd) {
                imgShowPsd.setImageResource(R.drawable.ic_close_eyes);
                mEdtWalletPwd.setInputType(129);
                isShowPsd = false;

            } else {
                imgShowPsd.setImageResource(R.drawable.ic_see1);
                mEdtWalletPwd.setInputType(128);
                isShowPsd = true;

            }
        } else if ("passwordConfim".equals(type)) {
            // 密码确认是否显示(是)
            if (isShowPsdRep) {
                imgShowRepPsd.setImageResource(R.drawable.ic_close_eyes);
                mEdtWalletPwdConfirm.setInputType(129);
                isShowPsdRep = false;

            } else {
                imgShowRepPsd.setImageResource(R.drawable.ic_see1);
                mEdtWalletPwdConfirm.setInputType(128);
                isShowPsdRep = true;
            }
        }
    }

    private void isCreate() {
        String walletName = mEdtWalletName.getText().toString();
        String passWord  = mEdtWalletPwd.getText().toString();
        String passwordConfim  = mEdtWalletPwdConfirm.getText().toString();
        boolean readedTerms = mImgServiceTerms.isSelected();

        if (!TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(passWord) && !TextUtils.isEmpty(passwordConfim)
                && passWord.equals(passwordConfim) && readedTerms) {
            mBtnConfirm.setEnabled(true);
        } else {
            mBtnConfirm.setEnabled(false);
        }
    }

    /**
     * 前端页面校验
     *
     * @return
     */
//    private boolean paramCheck() {
//
//        String walletName = mEdtWalletName.getText().toString();
//        String walletPwd = mEdtWalletPwd.getText().toString();
//        String walletPwdRepeat = mEdtWalletPwdConfirm.getText().toString();
//        boolean readedTerms = mImgServiceTerms.isSelected();
//
//        if (TextUtils.isEmpty(walletName)) {
//            // TODO
////            mTVAlertWalletName.setText(R.string.dialog_content_no_wallet_name);
////            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_name), "OK");
//            return false;
//        } else {
//            mTVAlertWalletName.setText("");
//        }
//        if (TextUtils.isEmpty(walletPwd)) {
////            mTVAlertPsd.setText(R.string.dialog_content_no_password);
////            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_password), "OK");
//            return false;
//        } else {
//            mTVAlertPsd.setText("8-64位大小写字母、数字组合密码");
//        }
//
//        if (TextUtils.isEmpty(walletPwdRepeat)) {
//            mTVAlertPsdRep.setText(R.string.dialog_content_no_verify_password);
////            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_verify_password), "OK");
//            return false;
//        } else {
//            mTVAlertPsdRep.setText("");
//        }
//
//        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
////            mTVAlertPsdRep.setText(R.string.dialog_content_passwords_unmatch);
////            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_passwords_unmatch), "OK");
//            return false;
//        } else {
//            mTVAlertPsdRep.setText("");
//        }
//
//        if (!walletPwd.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{8,64}$")) {
//
//            mTVAlertPsd.setText("8-64位大小写字母、数字组合密码");
////            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_password), "OK");
//            return false;
//        } else {
//            mTVAlertPsd.setText("8-64位大小写字母、数字组合密码");
//        }
//
//        if (!readedTerms) {
//            mTVAlertServiceTerms.setText(R.string.dialog_content_no_read_service);
////            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_read_service), "OK");
//            return false;
//        } else {
//            mTVAlertServiceTerms.setText("");
//        }
//
//        return true;
//    }

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
