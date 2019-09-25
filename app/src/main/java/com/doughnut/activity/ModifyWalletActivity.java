package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.Line;
import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.dialog.EditDialog;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.jccdex.rpc.base.JCallback;

import java.util.List;

import static com.doughnut.config.AppConfig.getContext;

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
            exportWallet();
        } else if (view == mLayoutCopy) {
            Util.clipboard(ModifyWalletActivity.this, "", mWalletAddress);
            new MsgDialog(ModifyWalletActivity.this, getString(R.string.toast_wallet_address_copied)).show();
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
                            finish();
                        }
                    }
                }).show();
    }

    private void exportWallet() {
        new EditDialog(this, mWalletAddress)
                .setDialogConfirmText(R.string.dialog_btn_confirm)
                .setDialogConfirmColor(R.color.color_dialog_confirm)
                .setResultListener(new EditDialog.PwdResultListener() {
                    @Override
                    public void authPwd(boolean result, String key) {
                        if (result) {
                            WalletExportActivity.startExportWalletActivity(ModifyWalletActivity.this, mWalletAddress, key);
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
        // 取得钱包资产
        WalletManager.getInstance(getContext()).getBalance(mWalletAddress, true, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    AccountRelations accountRelations = (AccountRelations) response;
                    if (accountRelations != null) {
                        List<Line> dataList = accountRelations.getLines();
                        if (dataList != null) {
                            WalletManager.getInstance(getContext()).getAllTokenPrice(new JCallback() {
                                // 钱包总价值
                                String values = "0.00";
                                // 钱包折换总SWT
                                String number = "0.00";
                                String swtPrice = "0.00";

                                @Override
                                public void onResponse(String code, String response) {
                                    if (TextUtils.equals(code, WConstant.SUCCESS_CODE)) {
                                        GsonUtil res = new GsonUtil(response);
                                        GsonUtil data = res.getObject("data");
                                        GsonUtil gsonUtil = data.getArray("SWT-CNY");
                                        swtPrice = gsonUtil.getString(1, "0");

                                        for (int i = 0; i < dataList.size(); i++) {
                                            Line line = (Line) dataList.get(i);
                                            // 数量
                                            String balance = line.getBalance();
                                            if (TextUtils.isEmpty(balance)) {
                                                balance = "0";
                                            }
                                            // 币种
                                            String currency = line.getCurrency();
                                            // 冻结
                                            String freeze = line.getLimit();
                                            if (TextUtils.isEmpty(freeze)) {
                                                freeze = "0";
                                            }

                                            String price = "0";
                                            if (TextUtils.equals(currency, WConstant.CURRENCY_CNY)) {
                                                price = "1";
                                            } else {
                                                String currency_cny = currency + "-CNY";
                                                GsonUtil currencyLst = data.getArray(currency_cny);
                                                if (currencyLst != null) {
                                                    price = currencyLst.getString(1, "0");
                                                }
                                            }
                                            // 当前币种总价值
                                            String sum = CaclUtil.add(balance, freeze);
                                            String value = CaclUtil.mul(sum, price);
                                            values = CaclUtil.add(values, value);
                                        }
                                        number = CaclUtil.div(values, swtPrice, 2);
                                        AppConfig.postOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mTvWalletBalance.setText(Util.formatWithComma(Double.parseDouble(number), 2));
                                                mTvWalletBalanceCNY.setText(Util.formatWithComma(Double.parseDouble(values), 2));

                                            }
                                        });
                                    } else {
                                        mTvWalletBalance.setText("0.00");
                                        mTvWalletBalanceCNY.setText("0.00");
                                    }
                                }

                                @Override
                                public void onFail(Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            mTvWalletBalance.setText("0.00");
                            mTvWalletBalanceCNY.setText("0.00");
                        }
                    }
                }
            }
        });
    }
}
