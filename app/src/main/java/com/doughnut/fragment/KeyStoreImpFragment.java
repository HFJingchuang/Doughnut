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
import android.text.method.ScrollingMovementMethod;
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

import com.doughnut.R;
import com.doughnut.activity.MainActivity;
import com.doughnut.activity.WebBrowserActivity;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.dialog.LoadDialog;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.view.SubCharSequence;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;


public class KeyStoreImpFragment extends BaseFragment implements View.OnClickListener {

    private RadioButton mRadioRead;
    private EditText mEKeyStore, mEdtWalletName, mEdtWalletPwd;
    private TextView mTvPolicy, mTvErrPassword;
    private ImageView mImgShowPwd;
    private LinearLayout mTvShowPwd, mLayoutRead;
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

    public static KeyStoreImpFragment newInstance(String importKey) {
        Bundle args = new Bundle();
        args.putString(Constant.IMPORT_KEY, importKey);
        KeyStoreImpFragment fragment = new KeyStoreImpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_key_store_imp, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
        mBtnConfirm.setClickable(true);
        mEKeyStore.requestFocus();
        mEKeyStore.setSelection(mEKeyStore.getText().toString().length());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 导入按钮
            case R.id.btn_confirm:
                String keyStore = mEKeyStore.getText().toString();
                String walletName = mEdtWalletName.getText().toString();
                String walletPwd = mEdtWalletPwd.getText().toString();
                LoadDialog loadDialog = new LoadDialog(getContext(), getString(R.string.dialog_import));
                loadDialog.show();
                // 导入钱包
                WalletManager.getInstance(getContext()).importKeysStore(keyStore, walletPwd, walletName, new ICallBack() {
                    @Override
                    public void onResponse(Object response) {
                        boolean isSuccess = (boolean) response;
                        loadDialog.dismiss();
                        if (isSuccess) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra(Constant.IMPORT_FLAG, true);
                            intent.putExtra(Constant.WALLET_NAME, walletName);
                            startActivity(intent);
                        } else {
                            new MsgDialog(getContext(), getString(R.string.dialog_import_fail)).setIsHook(false).show();
                        }
                    }
                });

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
        }
    }


    /**
     * 画面初期化
     *
     * @param view
     */
    private void initView(View view, String importKey) {
        mEKeyStore = view.findViewById(R.id.edt_keystore);
        mEKeyStore.setMovementMethod(new ScrollingMovementMethod());
        mEdtWalletName = view.findViewById(R.id.edt_wallet_name);
        if (!TextUtils.isEmpty(importKey)) {
            mEKeyStore.setText(importKey);
            mEdtWalletName.requestFocus();
        } else {
            mEKeyStore.requestFocus();
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
                            mTvErrPassword.setText(AppConfig.getCurActivity().getResources().getString(R.string.tv_pwd_tips));
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

        mTvShowPwd = view.findViewById(R.id.show_pwd);
        mTvShowPwd.setOnClickListener(this);
        mImgShowPwd = view.findViewById(R.id.img_show_pwd);

        mBtnConfirm = view.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }

    /**
     * 判断导入按钮是受可点击
     */
    private void isImportWallet() {
        String keyStore = mEKeyStore.getText().toString();
        String walletName = mEdtWalletName.getText().toString();
        String passWord = mEdtWalletPwd.getText().toString();
        boolean isRead = mRadioRead.isChecked();

        if (!TextUtils.isEmpty(keyStore) && !TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(passWord) && isRead) {
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
        WebBrowserActivity.startWebBrowserActivity(getContext(), getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }
}
