package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.dialog.EditDialog;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import java.math.BigDecimal;

public class ModifyWalletActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {
    private final static String TAG = "ModifyWalletActivity";

    private TitleBar mTitleBar;

    private TextView mTvWalletAddress;
    private TextView mTvWalletBalance;
    private TextView mTvWalletBalanceCNY;

    private TextView mTvWalletName;
    private RelativeLayout mLayoutModifyPwd;

    private TextView mTvDeleteWallet;
    private TextView mTvExportWallet;

    private LinearLayout mLayoutEdt;
    private LinearLayout mLayoutCopy;

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
            deleteWallet();
        } else if (view == mTvExportWallet) {
            importWallet();
        } else if (view == mLayoutCopy) {
            Util.clipboard(ModifyWalletActivity.this, "", mWalletAddress);
            ToastUtil.toast(ModifyWalletActivity.this, getString(R.string.toast_wallet_address_copied));
        } else if (view == mLayoutEdt) {
            changeWalletName();
        }
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
        mTitleBar.setTitleBarBackColor(R.color.color_dialog_confirm);
        mTitleBar.setTitleBarClickListener(this);

        mTvWalletBalance = findViewById(R.id.tv_wallet_balance);
        mTvWalletBalanceCNY = findViewById(R.id.tv_balance_CNY);
        mTvWalletAddress = (TextView) findViewById(R.id.tv_wallet_address);
        ViewUtil.EllipsisTextView(mTvWalletAddress);

        mTvWalletName = (TextView) findViewById(R.id.tv_wallet_name);
        ViewUtil.EllipsisTextView(mTvWalletName);

        mLayoutModifyPwd = (RelativeLayout) findViewById(R.id.layout_modify_pwd);
        mLayoutModifyPwd.setOnClickListener(this);

        mTvDeleteWallet = (TextView) findViewById(R.id.tv_delete_wallet);
        mTvDeleteWallet.setOnClickListener(this);

        mTvExportWallet = (TextView) findViewById(R.id.tv_export_wallet);
        mTvExportWallet.setOnClickListener(this);

        mLayoutEdt = findViewById(R.id.layout_edt);
        mLayoutEdt.setOnClickListener(this);

        mLayoutCopy = findViewById(R.id.layout_copy);
        mLayoutCopy.setOnClickListener(this);

        setWalletInfo();
    }

    private void gotoModifyPwd() {
        ModifyPwdActivity.startModifyPwdActivity(ModifyWalletActivity.this, mWalletAddress);
    }

    private void deleteWallet() {
        new EditDialog(this, mWalletAddress)
                .setIsDeleteWallet(true)
                .setResultListener(new EditDialog.PwdResultListener() {
                    @Override
                    public void authPwd(boolean result, String key) {
                        if (result) {
                            WalletManager.getInstance(ModifyWalletActivity.this).deleteWallet(mWalletAddress);
                            ToastUtil.toast(ModifyWalletActivity.this, "钱包「" + mWalletName + "」已成功删除!");
                            finish();
                        }
                    }
                }).show();
    }

    private void importWallet() {
        new EditDialog(this, mWalletAddress)
                .setDialogConfirmText(R.string.dialog_btn_confirm)
                .setDialogConfirmColor(R.color.color_dialog_confirm)
                .setResultListener(new EditDialog.PwdResultListener() {
                    @Override
                    public void authPwd(boolean result, String key) {
                        if (result) {
                            WalletExportActivity.startExportWalletActivity(ModifyWalletActivity.this, key);
                        }
                    }
                }).show();
    }

    private void changeWalletName() {
        new EditDialog(this, mWalletAddress)
                .setIsVerifyPwd(false)
                .setDialogTitle(R.string.title_change_name)
                .setDialogHint(R.string.title_change_name_hint)
                .setDialogConfirmText(R.string.dialog_btn_confirm)
                .setDialogConfirmColor(R.color.color_dialog_confirm)
                .setResultListener(new EditDialog.PwdResultListener() {
                    @Override
                    public void authPwd(boolean result, String key) {
                        if (result) {
                            WalletSp.getInstance(ModifyWalletActivity.this, mWalletAddress).setName(key);
                            mTvWalletName.setText(key);
                            ViewUtil.EllipsisTextView(mTvWalletName);
                            mTitleBar.setTitle(key);
                        }
                    }
                }).show();
    }

    private void setWalletInfo() {
        mTvWalletName.setText(mWalletName);
        mTvWalletAddress.setText(mWalletAddress);
        String balance = WalletManager.getInstance(this).getSWTBalance(mWalletAddress);
        mTvWalletBalance.setText(balance);
        WalletManager.getInstance(this).getTokenPrice(WConstant.CURRENCY_SWT, new BigDecimal(balance), mTvWalletBalanceCNY, null);
    }
}
