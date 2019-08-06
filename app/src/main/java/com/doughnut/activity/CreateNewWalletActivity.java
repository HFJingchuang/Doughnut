
package com.doughnut.activity;

import android.app.Activity;
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
import com.doughnut.base.BaseWalletUtil;
import com.doughnut.base.BlockChainData;
import com.doughnut.base.TBController;
import com.doughnut.config.Constant;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;


public class CreateNewWalletActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "CreateNewWalletActivity";
    public static final String BLOCK = "BLOCK";
    private static final int REQUEST_CODE = 1005; //选择底层请求码

    private TitleBar mTitleBar;
    //    private TextView mTvWalletType;

    private EditText mEdtWalletName, mEdtWalletPwd, mEdtWalletPwdConfirm, mEdtWalletTips;
    private ImageView mImgServiceTerms;
    private TextView mTvServiceTerms;

    private Button mBtnConfirm;

    private BlockChainData.Block mBlock;
    private BaseWalletUtil mWalletUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_wallet);
        if (getIntent().hasExtra(BLOCK)) {
            mBlock = getIntent().getParcelableExtra(BLOCK);
        }
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
//        mTvWalletType = findViewById(R.id.tv_wallet_type);
        mEdtWalletName = findViewById(R.id.edt_wallet_name);
        mEdtWalletPwd = findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwdConfirm = findViewById(R.id.edt_wallet_pwd_confirm);
        mEdtWalletTips = findViewById(R.id.edt_wallet_tips);

        mImgServiceTerms = findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);
        mBtnConfirm = findViewById(R.id.btn_confirm);

//        mTvWalletType.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);

        setWalletTypeInfo();
    }

    /**
     *  画面按钮事件
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
                    Intent intent = new Intent(this, WalletManageActivity.class);
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
     * 创建钱包
     * @param walletName
     * @param walletPwd
     */
    private void createWallet(final String walletName, final String walletPwd) {

         WalletManager walletManager =  WalletManager.getInstance(this);
         // 创建钱包
         walletManager.createWallet(walletPwd,walletName);
         // 获取钱包私钥
        walletManager.getPrivateKey(walletPwd,walletName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                mBlock = data.getParcelableExtra(BLOCK);
                setWalletTypeInfo();
            }
        }
    }

    private void setWalletTypeInfo() {
//        if (mBlock != null) {
//            mTvWalletType.setText(mBlock.desc);
//        } else {
//            mTvWalletType.setText("");
//        }

        if (mBlock != null) {
            mWalletUtil = TBController.getInstance().getWalletUtil((int) mBlock.hid);
        }
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void navToActivity(Context context, int request) {
        navToActivity(context, null, request);
    }

    /**
     * 启动Activity
     *
     * @param context
     * @param block
     */
    public static void navToActivity(Context context, BlockChainData.Block block, int request) {
        if (!(context instanceof BaseActivity)) {
            return;
        }
        Intent intent = new Intent(context, CreateWalletActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        if (block != null) {
            intent.putExtra(BLOCK, block);
        }
        ((Activity) context).startActivityForResult(intent, request);
    }

    /**
     * 前端页面校验
     * @return
     */
    private boolean paramCheck() {

        String walletName = mEdtWalletName.getText().toString();
        String walletPwd = mEdtWalletPwd.getText().toString();
        String walletPwdRepeat = mEdtWalletPwdConfirm.getText().toString();
        boolean readedTerms = mImgServiceTerms.isSelected();

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
