package com.doughnut.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.activity.MainActivity;
import com.doughnut.activity.WalletImportActivity;
import com.doughnut.activity.WebBrowserActivity;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.dialog.DeleteDialog;
import com.doughnut.dialog.ImportSuccessDialog;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;
import com.zxing.activity.CaptureActivity;


public class PrivateKeyImpFragment extends BaseFragment implements View.OnClickListener{

    private ImageView mImgServiceTerms, mImgShowPsd, mImgShowRepPsd;
    private TextView mTvServiceTerms, mTvErrPrivateKey, mTvErrWalletName, mTVErrPassword, mTvErrPsdRep, mTvAlertServiceTerms;

    private LinearLayout mTvShowPsd, mTvShowPsdRep;

    private EditText mTvPrivateKey, mTvWalletName, mTvWalletPwd, mTvWalletPwdConfirm;

    private Button mBtnConfirm;
    private Integer SCAN_CODE=101;

    private Context mContext;

//    private TitleBar mTitleBar;

    private boolean isShowPsd, isShowPsdRep;

    public static PrivateKeyImpFragment newInstance() {
        Bundle args = new Bundle();
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
        mContext = getActivity();
        initView(view);
    }

    /**
     * 画面初期化
     * @param view
     */
    private void initView(View view) {

//        mimg_scan = view.findViewById(R.id.img_scan);
//        mimg_scan.setOnClickListener(this);
//        mTitleBar = view.findViewById(R.id.title_bar);
//        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
//            @Override
//            public void onRightClick(View view) {
//                Intent intent = new Intent(mContext, CaptureActivity.class);
//                startActivityForResult(intent, SCAN_CODE);
//
//            }
//        });
        mImgServiceTerms = view.findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = view.findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);

        mTvPrivateKey = view.findViewById(R.id.edt_private_key);
        mTvPrivateKey.requestFocus();
        mTvPrivateKey.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isImportWallet();
                        if (mTvErrPrivateKey.isShown()) {
                            mTvErrPrivateKey.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mTvPrivateKey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String address = mTvPrivateKey.getText().toString();
                    if (!TextUtils.isEmpty(address)) {
                    }
                }
            }
        });

        mTvWalletName = view.findViewById(R.id.edt_wallet_name);
        mTvWalletName.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isImportWallet();
                        if (mTvErrWalletName.isShown()) {
                            mTvErrWalletName.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mTvWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String address = mTvWalletName.getText().toString();
                    if (!TextUtils.isEmpty(address)) {
                    }
                }
            }
        });

        mTvWalletPwd = view.findViewById(R.id.edt_wallet_pwd);
        mTvWalletPwd.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isImportWallet();
                        if (mTVErrPassword.isShown()) {
                            mTVErrPassword.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mTvWalletPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mTvWalletPwd.getText().toString();
                    if (!TextUtils.isEmpty(passWord)) {
                        if (!passWord.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{8,64}$")) {
//                            mTVErrPassword.setText("8-64位大小写字母、数字组合密码");
                            mTVErrPassword.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvWalletPwd.requestFocus();
                                }
                            });
                            return;
                        }
                    }
                }
            }
        });

        mTvWalletPwdConfirm = view.findViewById(R.id.edt_wallet_pwd_confirm);
        mTvWalletPwdConfirm.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isImportWallet();
                        if (mTvErrPsdRep.isShown()) {
                            mTvErrPsdRep.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        mTvWalletPwdConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passWord = mTvWalletPwd.getText().toString();
                    String passwordConfim = mTvWalletPwdConfirm.getText().toString();
                    if (!TextUtils.isEmpty(passwordConfim)) {
                        if (!passwordConfim.equals(passWord)) {
//                            mTvPsdRep.setText(getString(R.string.dialog_content_passwords_unmatch));
                            mTvErrPsdRep.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvWalletPwdConfirm.requestFocus();
                                }
                            });
                            return;
                        }
                    }
                }
            }
        });


        mBtnConfirm = view.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);

        mImgShowPsd = view.findViewById(R.id.img_show_psd);
        mImgShowPsd.setOnClickListener(this);

        mImgShowRepPsd = view.findViewById(R.id.img_show_rep_psd);
        mImgShowRepPsd.setOnClickListener(this);

        mTvErrPrivateKey = view.findViewById(R.id.err_private_key);
        mTvErrPrivateKey.setOnClickListener(this);

        mTvErrWalletName = view.findViewById(R.id.err_wallet_name);
        mTvErrWalletName.setOnClickListener(this);

        mTVErrPassword = view.findViewById(R.id.err_password);
        mTVErrPassword.setOnClickListener(this);

        mTvErrPsdRep = view.findViewById(R.id.err_psd_rep);
        mTvErrPsdRep.setOnClickListener(this);

        mTvAlertServiceTerms = view.findViewById(R.id.alert_service_terms);
        mTvAlertServiceTerms.setOnClickListener(this);

        mTvShowPsd = view.findViewById(R.id.show_psd);
        mTvShowPsd.setOnClickListener(this);

        mTvShowPsdRep = view.findViewById(R.id.show_psd_rep);
        mTvShowPsdRep.setOnClickListener(this);

    }

    /**
     * 判断导入按钮是受可点击
     */
    private void isImportWallet() {
        String privateKey = mTvPrivateKey.getText().toString();
        String walletName = mTvWalletName.getText().toString();
        String passWord  = mTvWalletPwd.getText().toString();
        String passwordConfim  = mTvWalletPwdConfirm.getText().toString();
        boolean readedTerms = mImgServiceTerms.isSelected();

        if (!TextUtils.isEmpty(privateKey) && !TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(passWord) && !TextUtils.isEmpty(passwordConfim)
                && passWord.equals(passwordConfim) && readedTerms) {
            mBtnConfirm.setEnabled(true);
        } else {
            mBtnConfirm.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String result = data.getStringExtra("scan_result");
            if (!result.isEmpty()) {
                mTvPrivateKey.setText(result);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        mLayoutManageWallet.setClickable(true);
//        mLayoutRecordTransaction.setClickable(true);
//        mLayoutNotification.setClickable(true);
//        mLayoutHelp.setClickable(true);
//        mLayoutAbout.setClickable(true);
//        mLayoutLanguage.setClickable(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 导入按钮
            case R.id.btn_confirm:
//                if (paramCheck()) {
                    String privateKey = mTvPrivateKey.getText().toString();
                    String walletName = mTvWalletName.getText().toString();
                    String walletPwd = mTvWalletPwd.getText().toString();
                    // 导入钱包
                    importWallet(privateKey, walletName, walletPwd);
                    // TODO 暂时跳转到钱包管理
//                    Intent intent = new Intent(this, WalletManageActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
//                ImportSuccessDialog pwdDialog = new ImportSuccessDialog(mContext);
//                pwdDialog.show();
//                    this.finish();
//                }
                break;
            // 勾选框
            case R.id.img_service_terms:
                mImgServiceTerms.setSelected(!mImgServiceTerms.isSelected());
                isImportWallet();
                break;
            // 跳转服务条款页面
            case R.id.tv_service_terms:
                gotoServiceTermPage();
                break;
            case R.id.img_scan:
//                Intent intent=new Intent(mContext, CaptureActivity.class);
//                startActivityForResult(intent,SCAN_CODE);
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
     * 判断密码显示隐藏
     *
     * @param type
     */
    private void showPassWord(String type) {
        if ("password".equals(type)) {
            // 密码是否显示(是)
            if (isShowPsd) {
                mImgShowPsd.setImageResource(R.drawable.ic_close_eyes);
                mTvWalletPwd.setInputType(129);
                isShowPsd = false;

            } else {
                mImgShowPsd.setImageResource(R.drawable.ic_see1);
                mTvWalletPwd.setInputType(128);
                isShowPsd = true;

            }
        } else if ("passwordConfim".equals(type)) {
            // 密码确认是否显示(是)
            if (isShowPsdRep) {
                mImgShowRepPsd.setImageResource(R.drawable.ic_close_eyes);
                mTvWalletPwdConfirm.setInputType(129);
                isShowPsdRep = false;

            } else {
                mImgShowRepPsd.setImageResource(R.drawable.ic_see1);
                mTvWalletPwdConfirm.setInputType(128);
                isShowPsdRep = true;
            }
        }
    }

    /**
     * 导入钱包
     * @param walletName
     * @param walletPwd
     */
    private void importWallet(final String  privateKey, final String walletName, final String walletPwd) {

        WalletManager walletManager =  WalletManager.getInstance(mContext);
        // 创建钱包
        walletManager.importWalletWithKey(walletPwd, privateKey, walletName);
    }

//    /**
//     * 前端页面校验
//     * @return
//     */
//    private boolean paramCheck() {
//
//        String privateKey = mTvPrivateKey.getText().toString();
//        String walletName = mTvWalletName.getText().toString();
//        String walletPwd = mTvWalletPwd.getText().toString();
//        String walletPwdRepeat = mTvWalletPwdConfirm.getText().toString();
//        boolean readedTerms = mImgServiceTerms.isSelected();
//
//        if (TextUtils.isEmpty(privateKey)) {
//            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_private_key), "OK");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(walletName)) {
//            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_wallet_name), "OK");
//            return false;
//        }
//        if (TextUtils.isEmpty(walletPwd)) {
//            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_password), "OK");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(walletPwdRepeat)) {
//            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_verify_password), "OK");
//            return false;
//        }
//
//        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
//            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_passwords_unmatch), "OK");
//            return false;
//        }
//
//        if (walletPwd.length() < 8) {
//            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_short_password), "OK");
//            return false;
//        }
//
//        if (!readedTerms) {
//            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_read_service), "OK");
//            return false;
//        }
//
//        return true;
//    }

    /**
     * 跳转服务条款页面
     */
    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(mContext, getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }
}
