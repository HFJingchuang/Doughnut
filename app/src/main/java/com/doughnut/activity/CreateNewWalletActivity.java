
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.jtblk.client.Wallet;
import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.dialog.LoadDialog;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.utils.PWDUtils;
import com.doughnut.view.SubCharSequence;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import java.util.List;


public class CreateNewWalletActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "CreateNewWalletActivity";

    private TitleBar mTitleBar;
    private RadioButton mRadioRead;
    private EditText mEdtWalletName, mEdtWalletPwd, mEdtWalletPwdConfirm;
    private TextView mTvPolicy, mTvErrPassword, mTvErrPasswordRep;
    private ImageView mImgShowRepPwd, mImgShowPwd;
    private LinearLayout mTvShowPwd, mTvShowPwdRep, mLayoutRead;
    private Button mBtnConfirm;
    private Switch mSwhED25519;
    private boolean isED25519 = false;

    private boolean isErr;
    private TransformationMethod transformationMethod = new TransformationMethod() {
        @Override
        public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
            // TODO Auto-generated method stub

        }

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            // TODO Auto-generated method stub
            return new SubCharSequence(source);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_wallet);
        isErr = false;
        initView();
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
                mBtnConfirm.setClickable(false);
                String passWord = mEdtWalletPwd.getText().toString();
                String passwordConfirm = mEdtWalletPwdConfirm.getText().toString();
                boolean isValid = PWDUtils.verifyPasswordFormat(passWord);
                if (!isValid) {
                    isErr = true;
                    mEdtWalletPwd.setText("");
                    mTvErrPassword.setText(getResources().getString(R.string.tv_pwd_err));
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEdtWalletPwd.requestFocus();
                        }
                    });
                } else if (TextUtils.equals(passWord, passwordConfirm)) {
                    mBtnConfirm.setClickable(false);
                    LoadDialog loadDialog = new LoadDialog(this, getString(R.string.dialog_loading));
                    loadDialog.show();
                    String walletName = mEdtWalletName.getText().toString();
                    String walletPwd = mEdtWalletPwd.getText().toString();
                    // 创建钱包
                    WalletManager.getInstance(CreateNewWalletActivity.this).createWallet(walletPwd, walletName, isED25519, new ICallBack() {
                        @Override
                        public void onResponse(Object response) {
                            List<String> list = (List) response;
                            String address = list.get(0);
                            String mnemonics = list.get(1);
                            loadDialog.dismiss();
                            if (Wallet.isValidAddress(address)) {
                                String keyStore = WalletSp.getInstance(CreateNewWalletActivity.this, address).getKeyStore();
                                WalletManager.getInstance(CreateNewWalletActivity.this).getPrivateKey(walletPwd, keyStore, new ICallBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        String privateKey = (String) response;
                                        CreateSuccessActivity.startCreateSuccessActivity(CreateNewWalletActivity.this, address, privateKey, mnemonics);
                                        finish();
                                    }
                                });
                            } else {
                                new MsgDialog(CreateNewWalletActivity.this, getString(R.string.tv_create_fail)).setIsHook(false).show();
                            }
                        }
                    });
                } else {
                    mEdtWalletPwdConfirm.setText("");
                    mTvErrPasswordRep.setText(getString(R.string.dialog_content_passwords_unmatch));
                    mTvErrPasswordRep.setVisibility(View.VISIBLE);
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEdtWalletPwdConfirm.requestFocus();
                        }
                    });
                }
                break;
            // 勾选框
            case R.id.layout_read:
                mRadioRead.setChecked(!mRadioRead.isChecked());
                isCreateWallet();
                break;
            // 跳转服务条款页面
            case R.id.tv_policy:
                gotoServiceTermPage();
                break;
            case R.id.show_pwd:
                mImgShowPwd.setSelected(!mImgShowPwd.isSelected());
                if (mImgShowPwd.isSelected()) {
                    mEdtWalletPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdtWalletPwd.setTransformationMethod(transformationMethod);
                }
                mEdtWalletPwd.setSelection(mEdtWalletPwd.getText().length());
                break;
            case R.id.show_pwd_rep:
                mImgShowRepPwd.setSelected(!mImgShowRepPwd.isSelected());
                if (mImgShowRepPwd.isSelected()) {
                    mEdtWalletPwdConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdtWalletPwdConfirm.setTransformationMethod(transformationMethod);
                }
                mEdtWalletPwdConfirm.setSelection(mEdtWalletPwdConfirm.getText().length());
                break;
            case R.id.swh_ed25519:
                isED25519 = mSwhED25519.isChecked();
        }
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
                finish();
            }
        });

        mEdtWalletName = findViewById(R.id.edt_wallet_name);
        mEdtWalletName.requestFocus();
        mEdtWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isCreateWallet();
                }
            }
        });

        mEdtWalletPwd = findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwd.setTransformationMethod(transformationMethod);
        mTvErrPassword = findViewById(R.id.tv_pwd_tips);
        mEdtWalletPwd.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isErr = false;
                        mTvErrPassword.setText(getResources().getString(R.string.tv_pwd_tips));
                        isCreateWallet();
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
                        boolean isValid = PWDUtils.verifyPasswordFormat(passWord);
                        if (!isValid) {
                            isErr = true;
                            mEdtWalletPwd.setText("");
                            mTvErrPassword.setText(getResources().getString(R.string.tv_pwd_err));
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletPwd.requestFocus();
                                }
                            });
                        } else {
                            isErr = false;
                        }
                    }
                    isCreateWallet();
                }
            }
        });

        mEdtWalletPwdConfirm = findViewById(R.id.edt_wallet_pwd_confirm);
        mEdtWalletPwdConfirm.setTransformationMethod(transformationMethod);
        mTvErrPasswordRep = findViewById(R.id.tv_pwd_rep_tips);
        mEdtWalletPwdConfirm.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (mTvErrPasswordRep.isShown()) {
                            mTvErrPasswordRep.setVisibility(View.GONE);
                        }
                        isCreateWallet();
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
                        if (!TextUtils.isEmpty(passWord) && !TextUtils.equals(passwordConfim, passWord)) {
                            mEdtWalletPwdConfirm.setText("");
                            mTvErrPasswordRep.setText(getString(R.string.dialog_content_passwords_unmatch));
                            mTvErrPasswordRep.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletPwdConfirm.requestFocus();
                                }
                            });
                        } else {
                            boolean isValid = PWDUtils.verifyPasswordFormat(passwordConfim);
                            if (!isValid) {
                                mEdtWalletPwdConfirm.setText("");
                                mTvErrPasswordRep.setText(getResources().getString(R.string.tv_pwd_err));
                                mTvErrPasswordRep.setVisibility(View.VISIBLE);
                                AppConfig.postOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEdtWalletPwdConfirm.requestFocus();
                                    }
                                });
                            }

                        }
                    }
                    isCreateWallet();
                }
            }
        });

        mRadioRead = findViewById(R.id.radio_read);
        mLayoutRead = findViewById(R.id.layout_read);
        mLayoutRead.setOnClickListener(this);
        mTvPolicy = findViewById(R.id.tv_policy);
        mTvPolicy.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvPolicy.setOnClickListener(this);
        mBtnConfirm = findViewById(R.id.btn_confirm);

        mTvShowPwd = findViewById(R.id.show_pwd);
        mTvShowPwd.setOnClickListener(this);
        mTvShowPwdRep = findViewById(R.id.show_pwd_rep);
        mTvShowPwdRep.setOnClickListener(this);

        mImgShowPwd = findViewById(R.id.img_show_pwd);
        mImgShowRepPwd = findViewById(R.id.img_show_pwd_rep);

        mSwhED25519 = findViewById(R.id.swh_ed25519);
        mSwhED25519.setOnClickListener(this);

        mBtnConfirm.setOnClickListener(this);
    }

    /**
     * 创建钱包选项check
     */
    private void isCreateWallet() {
        String walletName = mEdtWalletName.getText().toString();
        String passWord = mEdtWalletPwd.getText().toString();
        String passwordConfirm = mEdtWalletPwdConfirm.getText().toString();
        boolean isRead = mRadioRead.isChecked();

        if (!TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(passWord) && !isErr
                && !TextUtils.isEmpty(passwordConfirm) && !mTvErrPasswordRep.isShown() && isRead) {
            mBtnConfirm.setEnabled(true);
            mBtnConfirm.setClickable(true);
        } else {
            mBtnConfirm.setEnabled(false);
        }
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
