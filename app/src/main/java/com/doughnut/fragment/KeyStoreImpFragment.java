package com.doughnut.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.activity.MainActivity;
import com.doughnut.activity.WebBrowserActivity;
import com.doughnut.config.Constant;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;


public class KeyStoreImpFragment extends BaseFragment implements View.OnClickListener {


    private TitleBar mTitleBar;

    private ImageView mImgServiceTerms;
    private TextView mTvServiceTerms;

    private EditText mEKeyStore, mEWalletName, mEWalletPwd;

    private Button mBtnConfirm;
    private Context mContext;

    public static KeyStoreImpFragment newInstance() {
        Bundle args = new Bundle();
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
        mContext = getActivity();
        initView(view);
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
                if (paramCheck()) {
                    String keyStore = mEKeyStore.getText().toString();
                    String walletName = mEWalletName.getText().toString();
                    String walletPwd = mEWalletPwd.getText().toString();
                    // 导入钱包
                    importWallet(keyStore, walletName, walletPwd);
                    // TODO 暂时跳转到钱包管理
//                    Intent intent = new Intent(this, WalletManageActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
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
     * 画面初期化
     * @param view
     */
    private void initView(View view) {

//        mTitleBar = view.findViewById(R.id.title_bar);
//        mTitleBar.setLeftDrawable(R.drawable.ic_back);
//        mTitleBar.setTitle(R.string.select_keyStore_import);
//        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
////            @Override
////            public void onLeftClick(View view) {
////                onBackPressed();
////
////            }
//        });

        mImgServiceTerms = view.findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = view.findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);

        mEKeyStore = view.findViewById(R.id.keyStore_text);
        mEWalletName = view.findViewById(R.id.edt_wallet_name);
        mEWalletPwd = view.findViewById(R.id.edt_wallet_pwd);

        mBtnConfirm = view.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
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
            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_keyStore), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletName)) {
            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_wallet_name), "OK");
            return false;
        }
        if (TextUtils.isEmpty(walletPwd)) {
            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_password), "OK");
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
            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_short_password), "OK");
            return false;
        }

        if (!readedTerms) {
            ViewUtil.showSysAlertDialog(mContext, getString(R.string.dialog_content_no_read_service), "OK");
            return false;
        }

        return true;
    }

    /**
     * 导入钱包
     * @param walletName
     * @param walletPwd
     */
    private void importWallet(final String  keyStore, final String walletName, final String walletPwd) {

        WalletManager walletManager =  WalletManager.getInstance(mContext);
        // 导入钱包
        walletManager.importKeysStore(keyStore, walletName);
//         获取钱包私钥
//        walletManager.getPrivateKey(walletPwd,walletName);
    }

    /**
     * 跳转服务条款页面
     */
    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(mContext, getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }
}
