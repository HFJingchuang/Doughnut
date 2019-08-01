package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.view.TitleBar;


public class BackupStartActivity extends BaseActivity implements View.OnClickListener {

    private static final String WALLET_ADDRESS = "Wallet_Address";
    private static final String BAKUP_TYPE = "Bakup_Type";


    private TitleBar mTitleBar;
    private TextView mTvBakupTitle;
    private TextView mTvBakupContent;
    private TextView mTvStartBakWallet;


    private WalletInfoManager.WData mWalletData;
    private String[] mWords;
    private int mType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bakup_wallet_start);
        if (getIntent() != null) {
            mType = getIntent().getIntExtra(BAKUP_TYPE, -1);
            String walletAddress = getIntent().getStringExtra(WALLET_ADDRESS);
            if (!TextUtils.isEmpty(walletAddress)) {
                mWalletData = WalletInfoManager.getInstance().getWData(walletAddress);
            }
        }
//        if (!verifyData()) {
//            this.finish();
//            return;
//        }
        initView();
    }

    // 备份私钥画面初始化
    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.titleBar_backup_wallet));

        mTvBakupTitle = findViewById(R.id.tv_bakup_title);
        mTvBakupContent = findViewById(R.id.tv_bakup_content);
        // 备份钱包按钮
        mTvStartBakWallet = findViewById(R.id.tv_start_bakwallet);
        mTvStartBakWallet.setOnClickListener(this);
        mTvBakupTitle.setText(getString(R.string.title_backup_private_key));
        mTvBakupContent.setText(getString(R.string.content_backup_private_kye));
    }

    @Override
    public void onClick(View v) {
        if (v == mTvStartBakWallet) {
//            BWDInfoActivity.startBakupWalletInfoActivity(BackupStartActivity.this,
//                    mWalletData.waddress, mType);
//            this.finish();
            Intent intent = new Intent(this, BackupInfoActivity.class);
            startActivity(intent);

        }
    }


//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
////            case R.id.tv_wallet_type:
////                ChooseWalletBlockActivity.navToActivity(CreateWalletActivity.this, REQUEST_CODE);
////                break;
//            case R.id.btn_confirm:
//
//                if (paramCheck()) {
//
////                    String walletName = mEdtWalletName.getText().toString();
////                    String walletPwd = mEdtWalletPwd.getText().toString();
////                    createWallet(walletName, walletPwd);
//                    Intent intent = new Intent(this, BackupStartActivity.class);
//                    startActivity(intent);
//
//                }
//                break;
//            case R.id.img_service_terms:
//                mImgServiceTerms.setSelected(!mImgServiceTerms.isSelected());
//                break;
//            case R.id.tv_service_terms:
//                gotoServiceTermPage();
//                break;
//        }
//    }

}
