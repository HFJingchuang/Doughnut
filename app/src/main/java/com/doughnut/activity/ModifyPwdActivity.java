package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jtblk.client.Wallet;
import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.dialog.LoadDialog;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.utils.PWDUtils;
import com.doughnut.view.SubCharSequence;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;


public class ModifyPwdActivity extends BaseActivity implements TitleBar.TitleBarClickListener, View.OnClickListener {

    private TitleBar mTitleBar;
    private EditText mEdtOldPwd, mEdtNewPwd, mEdtRepNewPwd;
    private TextView mTvOldTips, mTvNewTips, mTvRepTips;
    private ImageView mImgShow, mImgShowNew, mImgShowRep;
    private LinearLayout mLayoutShow, mLayoutShowNew, mLayoutShowRep;
    private Button mBtnDone;

    private String mWalletAddress;
    private String mPrivateKey;
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
        setContentView(R.layout.activity_modify_pwd);
        isErr = false;
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
    }

    @Override
    public void onMiddleClick(View view) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 创建钱包按钮
            case R.id.btn_done:
                String newPwd = mEdtNewPwd.getText().toString();
                String repPwd = mEdtRepNewPwd.getText().toString();
                boolean isValid = PWDUtils.verifyPasswordFormat(newPwd);
                if (!isValid) {
                    isErr = true;
                    mEdtNewPwd.setText("");
                    mTvNewTips.setText(getResources().getString(R.string.tv_pwd_err));
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEdtNewPwd.requestFocus();
                        }
                    });
                } else if (TextUtils.equals(newPwd, mEdtOldPwd.getText().toString())) {
                    isErr = true;
                    mEdtNewPwd.setText("");
                    mTvNewTips.setText(getResources().getString(R.string.tv_err_same));
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEdtNewPwd.requestFocus();
                        }
                    });
                } else if (TextUtils.equals(newPwd, repPwd)) {
                    LoadDialog loadDialog = new LoadDialog(this, getString(R.string.dialog_modify));
                    loadDialog.show();
                    // 修改KeyStore密码，name参数可不传
                    WalletManager.getInstance(this).importWalletWithKey(mEdtNewPwd.getText().toString(), mPrivateKey, "", false, new ICallBack() {
                        @Override
                        public void onResponse(Object response) {
                            boolean isSuccess = (boolean) response;
                            loadDialog.dismiss();
                            if (isSuccess) {
                                finish();
                            } else {
                                new MsgDialog(ModifyPwdActivity.this, getString(R.string.dailog_modify_fail)).setIsHook(false).show();
                            }
                        }
                    });

                } else {
                    mEdtRepNewPwd.setText("");
                    mTvRepTips.setText(getString(R.string.dialog_content_passwords_unmatch));
                    mTvRepTips.setVisibility(View.VISIBLE);
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEdtRepNewPwd.requestFocus();
                        }
                    });
                }
                break;
            case R.id.layout_show:
                mImgShow.setSelected(!mImgShow.isSelected());
                if (mImgShow.isSelected()) {
                    mEdtOldPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdtOldPwd.setTransformationMethod(transformationMethod);
                }
                mEdtOldPwd.setSelection(mEdtOldPwd.getText().length());
                break;
            case R.id.layout_show_pwd:
                mImgShowNew.setSelected(!mImgShowNew.isSelected());
                if (mImgShowNew.isSelected()) {
                    mEdtNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdtNewPwd.setTransformationMethod(transformationMethod);
                }
                mEdtNewPwd.setSelection(mEdtNewPwd.getText().length());
                break;
            case R.id.layout_show_pwd_rep:
                mImgShowRep.setSelected(!mImgShowRep.isSelected());
                if (mImgShowRep.isSelected()) {
                    mEdtRepNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdtRepNewPwd.setTransformationMethod(transformationMethod);
                }
                mEdtRepNewPwd.setSelection(mEdtRepNewPwd.getText().length());
                break;
        }
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);

        mTitleBar.setTitle(getString(R.string.titleBar_change_pwd));

        mTitleBar.setRightText(getString(R.string.titleBar_completed));
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mEdtOldPwd = findViewById(R.id.edt_current_pwd);
        mEdtOldPwd.requestFocus();
        mEdtOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTvOldTips.isShown()) {
                    mTvOldTips.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtOldPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pwd = mEdtOldPwd.getText().toString();
                    if (!TextUtils.isEmpty(pwd)) {
                        String keyStore = WalletSp.getInstance(ModifyPwdActivity.this, mWalletAddress).getKeyStore();
                        WalletManager.getInstance(ModifyPwdActivity.this).getPrivateKey(pwd, keyStore, new ICallBack() {
                            @Override
                            public void onResponse(Object response) {
                                mPrivateKey = (String) response;
                                if (!Wallet.isValidSecret(mPrivateKey)) {
                                    mEdtOldPwd.setText("");
                                    mTvOldTips.setText(getResources().getString(R.string.tv_err_old_pwd));
                                    mTvOldTips.setVisibility(View.VISIBLE);
                                    AppConfig.postOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mEdtOldPwd.requestFocus();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
        mTvOldTips = findViewById(R.id.tv_current_pwd_tips);
        mImgShow = findViewById(R.id.img_show);
        mLayoutShow = findViewById(R.id.layout_show);
        mLayoutShow.setOnClickListener(this);

        mEdtNewPwd = findViewById(R.id.edt_new_pwd);
        mEdtNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isErr = false;
                mTvNewTips.setText(getResources().getString(R.string.tv_pwd_tips));
                isDone();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtNewPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mEdtNewPwd.getText().toString();
                    if (!TextUtils.isEmpty(passWord)) {
                        boolean isValid = PWDUtils.verifyPasswordFormat(passWord);
                        if (!isValid) {
                            isErr = true;
                            mEdtNewPwd.setText("");
                            mTvNewTips.setText(getResources().getString(R.string.tv_pwd_err));
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtNewPwd.requestFocus();
                                }
                            });
                        } else if (TextUtils.equals(passWord, mEdtOldPwd.getText().toString())) {
                            isErr = true;
                            mEdtNewPwd.setText("");
                            mTvNewTips.setText(getResources().getString(R.string.tv_err_same));
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtNewPwd.requestFocus();
                                }
                            });
                        } else {
                            isErr = false;
                        }
                    }
                    isDone();
                }
            }
        });
        mTvNewTips = findViewById(R.id.tv_pwd_tips);
        mImgShowNew = findViewById(R.id.img_show_pwd);
        mLayoutShowNew = findViewById(R.id.layout_show_pwd);
        mLayoutShowNew.setOnClickListener(this);

        mEdtRepNewPwd = findViewById(R.id.edt_rep_new_pwd);
        mEdtRepNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTvRepTips.isShown()) {
                    mTvRepTips.setVisibility(View.GONE);
                }
                isDone();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtRepNewPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mEdtNewPwd.getText().toString();
                    String passwordConfim = mEdtRepNewPwd.getText().toString();
                    if (!TextUtils.isEmpty(passwordConfim)) {
                        if (!TextUtils.isEmpty(passWord) && !TextUtils.equals(passwordConfim, passWord)) {
                            mEdtRepNewPwd.setText("");
                            mTvRepTips.setText(getString(R.string.dialog_content_passwords_unmatch));
                            mTvRepTips.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtRepNewPwd.requestFocus();
                                }
                            });
                        } else {
                            boolean isValid = PWDUtils.verifyPasswordFormat(passwordConfim);
                            if (!isValid) {
                                mEdtRepNewPwd.setText("");
                                mTvRepTips.setText(getResources().getString(R.string.tv_pwd_err));
                                mTvRepTips.setVisibility(View.VISIBLE);
                                AppConfig.postOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEdtRepNewPwd.requestFocus();
                                    }
                                });
                            }

                        }
                    }
                    isDone();
                }
            }
        });
        mTvRepTips = findViewById(R.id.tv_rep_pwd_tips);
        mImgShowRep = findViewById(R.id.img_show_pwd_rep);
        mLayoutShowRep = findViewById(R.id.layout_show_pwd_rep);
        mLayoutShowRep.setOnClickListener(this);

        mBtnDone = findViewById(R.id.btn_done);
        mBtnDone.setOnClickListener(this);
    }

    /**
     * 选项check
     */
    private void isDone() {
        String oldPwd = mEdtOldPwd.getText().toString();
        String newPwd = mEdtNewPwd.getText().toString();
        String repPwd = mEdtRepNewPwd.getText().toString();

        if (!TextUtils.isEmpty(oldPwd) && !mTvOldTips.isShown() && !TextUtils.isEmpty(newPwd) && !isErr
                && !TextUtils.isEmpty(repPwd) && !mTvRepTips.isShown()) {
            mBtnDone.setEnabled(true);
            mBtnDone.setClickable(true);
        } else {
            mBtnDone.setEnabled(false);
        }
    }

    public static void startModifyPwdActivity(Context context, String walletAddress) {
        Intent intent = new Intent(context, ModifyPwdActivity.class);
        intent.putExtra("Wallet_Address", walletAddress);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
