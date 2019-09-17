package com.doughnut.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.jtblk.client.Wallet;
import com.doughnut.R;
import com.doughnut.activity.MainActivity;
import com.doughnut.activity.WebBrowserActivity;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.utils.PWDUtils;
import com.doughnut.view.SubCharSequence;
import com.doughnut.wallet.WalletManager;


public class PrivateKeyImpFragment extends BaseFragment implements View.OnClickListener {

    private RadioButton mRadioRead;
    private EditText mEdtPrivateKey, mEdtWalletName, mEdtWalletPwd, mEdtWalletPwdConfirm;
    private TextView mTvPolicy, mTvErrKey, mTvErrPassword, mTvErrPasswordRep;
    private ImageView mImgShowRepPwd, mImgShowPwd;
    private LinearLayout mTvShowPwd, mTvShowPwdRep, mLayoutRead;
    private Button mBtnConfirm;

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

    public static PrivateKeyImpFragment newInstance(String importKey) {
        Bundle args = new Bundle();
        args.putString(Constant.IMPORT_KEY, importKey);
        PrivateKeyImpFragment fragment = new PrivateKeyImpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_key_imp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isErr = false;
        if (getArguments() != null) {
            initView(view, getArguments().getString(Constant.IMPORT_KEY));
        } else {
            initView(view, "");
        }
    }

    /**
     * 画面初期化
     *
     * @param view
     */
    private void initView(View view, String importKey) {

        mEdtPrivateKey = view.findViewById(R.id.edt_private_key);
        mTvErrKey = view.findViewById(R.id.tv_err_key);
        mEdtPrivateKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTvErrKey.isShown()) {
                    mTvErrKey.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtPrivateKey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String privateKey = mEdtPrivateKey.getText().toString();
                    if (TextUtils.isEmpty(privateKey)) {
                        return;
                    }
                    boolean isVaild = Wallet.isValidSecret(privateKey);
                    if (!isVaild) {
                        AppConfig.postOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvErrKey.setVisibility(View.VISIBLE);
                                mTvErrKey.setText(getString(R.string.tv_err_key));
                                mEdtPrivateKey.requestFocus();
                                mEdtPrivateKey.setSelection(privateKey.length());
                            }
                        });
                    }
                }
            }
        });
        mEdtWalletName = view.findViewById(R.id.edt_wallet_name);
        if (!TextUtils.isEmpty(importKey)) {
            mEdtPrivateKey.setText(importKey);
            mEdtWalletName.requestFocus();
        } else {
            mEdtPrivateKey.requestFocus();
        }

        mEdtWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isImportWallet();
                }
            }
        });

        mEdtWalletPwd = view.findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwd.setTransformationMethod(transformationMethod);
        mTvErrPassword = view.findViewById(R.id.tv_pwd_tips);
        mEdtWalletPwd.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (isErr) {
                            mTvErrPassword.setText(getResources().getString(R.string.tv_pwd_tips));
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
                    isImportWallet();
                }
            }
        });

        mEdtWalletPwdConfirm = view.findViewById(R.id.edt_wallet_pwd_confirm);
        mEdtWalletPwdConfirm.setTransformationMethod(transformationMethod);
        mTvErrPasswordRep = view.findViewById(R.id.tv_pwd_rep_tips);
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
                        isImportWallet();
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
                    isImportWallet();
                }
            }
        });

        mRadioRead = view.findViewById(R.id.radio_read);
        mLayoutRead = view.findViewById(R.id.layout_read);
        mLayoutRead.setOnClickListener(this);
        mTvPolicy = view.findViewById(R.id.tv_policy);
        mTvPolicy.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvPolicy.setOnClickListener(this);
        mBtnConfirm = view.findViewById(R.id.btn_confirm);

        mTvShowPwd = view.findViewById(R.id.show_pwd);
        mTvShowPwd.setOnClickListener(this);
        mTvShowPwdRep = view.findViewById(R.id.show_pwd_rep);
        mTvShowPwdRep.setOnClickListener(this);

        mImgShowPwd = view.findViewById(R.id.img_show_pwd);
        mImgShowRepPwd = view.findViewById(R.id.img_show_pwd_rep);

        mBtnConfirm.setOnClickListener(this);
    }

    /**
     * 判断导入按钮是受可点击
     */
    private void isImportWallet() {
        String privateKey = mEdtPrivateKey.getText().toString();
        String walletName = mEdtWalletName.getText().toString();
        String passWord = mEdtWalletPwd.getText().toString();
        String passwordConfim = mEdtWalletPwdConfirm.getText().toString();
        boolean isRead = mRadioRead.isChecked();

        if (!TextUtils.isEmpty(privateKey) && !TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(passWord) && !isErr
                && !TextUtils.isEmpty(passwordConfim) && !mTvErrPasswordRep.isShown() && isRead) {
            mBtnConfirm.setEnabled(true);
            mBtnConfirm.setClickable(true);
        } else {
            mBtnConfirm.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mBtnConfirm.setClickable(true);
        mEdtPrivateKey.requestFocus();
        mEdtPrivateKey.setSelection(mEdtPrivateKey.getText().toString().length());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 导入按钮
            case R.id.btn_confirm:
                String passWord = mEdtWalletPwd.getText().toString();
                String passwordConfim = mEdtWalletPwdConfirm.getText().toString();
                if (!TextUtils.equals(passWord, passwordConfim)) {
                    mEdtWalletPwdConfirm.setText("");
                    mTvErrPasswordRep.setText(getString(R.string.dialog_content_passwords_unmatch));
                    mTvErrPasswordRep.setVisibility(View.VISIBLE);
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEdtWalletPwdConfirm.requestFocus();
                        }
                    });
                    return;
                }
                mBtnConfirm.setClickable(false);
                String privateKey = mEdtPrivateKey.getText().toString();
                String walletName = mEdtWalletName.getText().toString();
                String walletPwd = mEdtWalletPwd.getText().toString();

                boolean isSuccess = WalletManager.getInstance(getContext()).importWalletWithKey(walletPwd, privateKey, walletName);
                if (isSuccess) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra(Constant.IMPORT_FLAG, true);
                    intent.putExtra(Constant.WALLET_NAME, walletName);
                    startActivity(intent);
                } else {
                    new MsgDialog(getContext(), getString(R.string.dialog_import_fail)).setIsHook(false).show();
                }
                break;
            // 勾选框
            case R.id.layout_read:
                mRadioRead.setChecked(!mRadioRead.isChecked());
                isImportWallet();
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
        }
    }

    /**
     * 跳转服务条款页面
     */
    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(getContext(), getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }
}
