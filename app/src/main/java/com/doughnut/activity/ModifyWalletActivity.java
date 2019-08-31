package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.dialog.PKDialog;
import com.doughnut.dialog.PwdDialog;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import java.math.BigDecimal;

public class ModifyWalletActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener,
        PwdDialog.PwdResult {
    private final static String TAG = "ModifyWalletActivity";

    private TitleBar mTitleBar;

    private TextView mTvWalletAddress;
    private TextView mTvWalletBalance;
    private TextView mTvWalletBalanceCNY;

    private EditText mEdtWalletName;
    private RelativeLayout mLayoutModifyPwd;

    private TextView mTvDeleteWallet;
    private TextView mTvExportWallet;

    private ImageView ImgCopy;
    private ImageView ImgEdt;

    private String mWalletAddress;
    private String mWalletName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_wallet);
        if (getIntent() != null) {
            mWalletAddress = getIntent().getStringExtra("Wallet_Address");
            mWalletName = WalletSp.getInstance(this, mWalletAddress).getName();
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutModifyPwd) {
            gotoModifyPwd();
        } else if (view == mTvDeleteWallet) {
            verifyPwd(Constant.WALLET_DEL);
        } else if (view == mTvWalletAddress) {
            Util.clipboard(ModifyWalletActivity.this, "", mTvWalletAddress.getText().toString());
            ToastUtil.toast(ModifyWalletActivity.this, getString(R.string.toast_wallet_address_copied));
        } else if (view == mTvExportWallet) {
            verifyPwd(Constant.WALLET_IMP);
        } else if (view == ImgCopy) {
            Util.clipboard(ModifyWalletActivity.this, "", mTvWalletAddress.getText().toString());
            ToastUtil.toast(ModifyWalletActivity.this, getString(R.string.toast_wallet_address_copied));
        } else if (view == ImgEdt) {
//todo
        }
    }

    @Override
    public void onLeftClick(View view) {
        finish();
    }

    @Override
    public void onRightClick(View view) {
        saveWalletInfo();
    }

    @Override
    public void onMiddleClick(View view) {

    }

    @Override
    public void authPwd(String tag, boolean result, String key) {
        if (TextUtils.equals(tag, Constant.WALLET_IMP)) {
            if (result) {
                gotoExportWallet();
            }
        } else if (TextUtils.equals(tag, Constant.WALLET_DEL)) {
            if (result) {
                deleteWallet();
            }
        }
    }

    public static void startModifyWalletActivity(Context context, String walletAddress) {
        Intent intent = new Intent(context, ModifyWalletActivity.class);
        intent.putExtra("Wallet_Address", walletAddress);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back_white);
        mTitleBar.setTitle(mWalletName);
        mTitleBar.setRightText(getString(R.string.titleBar_save));
        mTitleBar.setTitleBarBackColor(R.color.color_dialog_confirm);
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mTvWalletBalance = findViewById(R.id.tv_wallet_balance);
        mTvWalletBalanceCNY = findViewById(R.id.tv_balance_CNY);
        mTvWalletAddress = (TextView) findViewById(R.id.tv_wallet_address);
        mTvWalletAddress.setOnClickListener(this);

        mEdtWalletName = (EditText) findViewById(R.id.edt_wallet_name);

        mLayoutModifyPwd = (RelativeLayout) findViewById(R.id.layout_modify_pwd);
        mLayoutModifyPwd.setOnClickListener(this);

        mTvDeleteWallet = (TextView) findViewById(R.id.tv_delete_wallet);
        mTvDeleteWallet.setOnClickListener(this);

        mTvExportWallet = (TextView) findViewById(R.id.tv_export_wallet);
        mTvExportWallet.setOnClickListener(this);

        ImgCopy = findViewById(R.id.img_copy);
        ImgCopy.setOnClickListener(this);

        setWalletInfo();
    }

    private void gotoModifyPwd() {
        ModifyPwdActivity.startModifyPwdActivity(ModifyWalletActivity.this, mWalletAddress);
    }

    private void gotoExportWallet() {
        WalletExportActivity.startExportWalletActivity(this);
    }

    private void verifyPwd(String tag) {
        PwdDialog pwdDialog = new PwdDialog(this, this, mWalletAddress, tag);
        pwdDialog.show();
    }

    private void realExportPrivateKey(String key) {
        String privateKey = WalletManager.getInstance(this).getPrivateKey(key, mWalletAddress);
        PKDialog PKDialog = new PKDialog(this,
                privateKey);
        PKDialog.show();
    }

    private void saveWalletInfo() {
        String newWalletName = mEdtWalletName.getText().toString();
        if (TextUtils.isEmpty(newWalletName)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_name), "Ok");
            return;
        }
        WalletSp.getInstance(this, mWalletAddress).setName(newWalletName);
        finish();
    }

    private void setWalletInfo() {
        mTvWalletAddress.setText(mWalletAddress);
        mEdtWalletName.setText(mWalletName);
        mTvWalletAddress.setText(mWalletAddress);
        String balance = WalletManager.getInstance(this).getSWTBalance(mWalletAddress);
        mTvWalletBalance.setText(balance);
        WalletManager.getInstance(this).getTokenPrice(WConstant.CURRENCY_SWT, new BigDecimal(balance), mTvWalletBalanceCNY, null);
    }

    private void deleteWallet() {
//        if (!mWalletData.isBaked) {
//            if (mWalletData.type == 1) {
//                ViewUtil.showSysAlertDialog(ModifyWalletActivity.this, getString(R.string.dialog_title_warning), getString(R.string.dialog_content_no_wallet_backup), getString(R.string.dialog_btn_backup), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        gotoBak();
//                        dialog.dismiss();
//                    }
//                }, getString(R.string.dialog_btn_delete), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        WalletInfoManager.getInstance().deleteWallet(ModifyWalletActivity.this, mWalletData);
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
//            } else if (mWalletData.type == 2) {
//                ViewUtil.showSysAlertDialog(ModifyWalletActivity.this, getString(R.string.dialog_title_warning), getString(R.string.dialog_content_no_key_backup), getString(R.string.dialog_btn_backup), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        verifyPwd("exportprivatekey");
//                        dialog.dismiss();
//                    }
//                }, getString(R.string.dialog_btn_delete), ønew DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        WalletInfoManager.getInstance().deleteWallet(ModifyWalletActivity.this, mWalletData);
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
//            }
//        } else {
//            WalletInfoManager.getInstance().deleteWallet(ModifyWalletActivity.this, mWalletData);
//            finish();
//        }

        boolean isBack = true;
        if (isBack) {
            WalletManager.getInstance(this).deleteWallet(mWalletAddress);
            ToastUtil.toast(this, "钱包「" + mWalletName + "」已成功删除!");
            finish();
        } else {
            // todo 检测要删除钱包是否已备份过
        }
    }

//    private void gotoBak() {
//        String[] words = null;
//        words = mWalletData.words.split(" ");
//        if (words == null || words.length < 12) {
//            ToastUtil.toast(ModifyWalletActivity.this, getString(R.string.toast_cant_backup));
//            return;
//        }
//        StartBakupActivity.startBakupWalletStartActivity(ModifyWalletActivity.this, mWalletData.waddress, 2);
//    }

}
