
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jtblk.client.Wallet;
import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.dialog.EditDialog;
import com.doughnut.view.CashierInputFilter;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.zxing.activity.CaptureActivity;

import java.math.BigDecimal;


public class TokenTransferActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "TokenTransferActivity";
    private TitleBar mTitleBar;
    private TextView mTvTokenName, mTvBalance, mTvErrAddr, mTvErrAmount;
    private EditText mEdtWalletAddress, mEdtTransferNum, mEdtMemo;
    private Button mBtnConfirm;
    private LinearLayout mLayoutToken;
    private String mBalance;

    private final static int SCAN_REQUEST_CODE = 10001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_token);
        initView();
        initData();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.titleBar_transfer));
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightDrawable(R.drawable.ic_scan);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                MainActivity.startMainActivityForIndex(TokenTransferActivity.this, 2);
                finish();
            }

            @Override
            public void onRightClick(View view) {
                startActivity(new Intent(TokenTransferActivity.this, CaptureActivity.class));
            }
        });
        mTvErrAddr = findViewById(R.id.tv_err_address);
        mTvErrAmount = findViewById(R.id.tv_err_amount);
        mEdtWalletAddress = findViewById(R.id.edt_address);
        mEdtWalletAddress.requestFocus();
        mEdtWalletAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTransfer();
                if (mTvErrAddr.isShown()) {
                    mTvErrAddr.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtWalletAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String address = mEdtWalletAddress.getText().toString();
                    if (!TextUtils.isEmpty(address)) {
                        String currentAddr = WalletSp.getInstance(TokenTransferActivity.this, "").getCurrentWallet();
                        if (TextUtils.equals(currentAddr, address)) {
                            mTvErrAddr.setText(getString(R.string.tv_err_address1));
                            mTvErrAddr.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletAddress.requestFocus();
                                }
                            });
                            return;
                        }
                        boolean isValidAddress = Wallet.isValidAddress(address);
                        if (!isValidAddress) {
                            mTvErrAddr.setVisibility(View.VISIBLE);
                            mTvErrAddr.setText(getString(R.string.tv_err_address));
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtWalletAddress.requestFocus();
                                }
                            });
                        }
                    }
                }
            }
        });

        mEdtTransferNum = findViewById(R.id.edt_amount);
        InputFilter[] filters = {new CashierInputFilter()};
        mEdtTransferNum.setFilters(filters);
        mEdtTransferNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTransfer();
                if (mTvErrAmount.isShown()) {
                    mTvErrAmount.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtTransferNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String input = mEdtTransferNum.getText().toString();
                    if (!TextUtils.isEmpty(input)) {
                        BigDecimal balance = new BigDecimal(mBalance);
                        BigDecimal amount = new BigDecimal(input);
                        if (balance.compareTo(amount) < 0) {
                            mTvErrAmount.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtTransferNum.requestFocus();
                                }
                            });
                        }

                    }
                }
            }
        });
        mTvTokenName = findViewById(R.id.tv_token_name);
        mTvBalance = findViewById(R.id.tv_balance);
        mLayoutToken = findViewById(R.id.layout_token);
        mLayoutToken.setOnClickListener(this);
        mEdtMemo = findViewById(R.id.edt_memo);
        mEdtMemo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTransfer();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBtnConfirm = findViewById(R.id.btn_send);
        mBtnConfirm.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null) {
            String address = getIntent().getStringExtra(Constant.RECEIVE_ADDRESS_KEY);
            if (!TextUtils.isEmpty(address)) {
                mEdtWalletAddress.setText(address);
                mEdtWalletAddress.setSelection(address.length());
            }
            String amount = getIntent().getStringExtra(Constant.TOEKN_AMOUNT);
            if (!TextUtils.isEmpty(amount)) {
                mEdtTransferNum.setText(amount);
                mEdtTransferNum.requestFocus();
                mEdtTransferNum.setSelection(amount.length());
            }
            String token = getIntent().getStringExtra(Constant.TOEKN_NAME);
            if (!TextUtils.isEmpty(token)) {
                mTvTokenName.setText(token);
            }

//            String memo = getIntent().getStringExtra(Constant.MEMO);
//            if (!TextUtils.isEmpty(memo)) {
//                mEdtMemo.setText(memo);
//            }
        }

        String currentAddr = WalletSp.getInstance(this, "").getCurrentWallet();
        mBalance = WalletManager.getInstance(this).getSWTBalance(currentAddr);
        mTvBalance.setText(String.format(getString(R.string.tv_balance), mBalance, "SWTC"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                sendTranscation();
                break;
            case R.id.layout_token:
                // todo 选择转账token
                break;
        }
    }

    private void sendTranscation() {
        String currentAddr = WalletSp.getInstance(TokenTransferActivity.this, "").getCurrentWallet();
        new EditDialog(this, currentAddr)
                .setDialogConfirmText(R.string.dialog_btn_confirm)
                .setDialogConfirmColor(R.color.color_dialog_confirm)
                .setResultListener(new EditDialog.PwdResultListener() {
                    @Override
                    public void authPwd(boolean result, String key) {
                        if (result) {
                            String to = mEdtWalletAddress.getText().toString();
                            String token = mTvTokenName.getText().toString();
                            String issue = "";// todo 获取issue
                            String value = mEdtTransferNum.getText().toString();
                            String memo = mEdtMemo.getText().toString();
                            WalletManager.getInstance(TokenTransferActivity.this).transfer(key, currentAddr, to, token, issue, value, memo);
                        }
                    }
                }).show();
    }

    private void updateBtnToTranferingState() {
        mBtnConfirm.setEnabled(false);
        mBtnConfirm.setText(getString(R.string.btn_transferring));
    }

    private void resetTranferBtn() {
        mBtnConfirm.setEnabled(true);
        mBtnConfirm.setText(getString(R.string.btn_next));
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenTransferActivity(Context context, String receiveAddress,
                                                  String amount, String token) {
        Intent intent = new Intent(context, TokenTransferActivity.class);
        intent.putExtra(Constant.RECEIVE_ADDRESS_KEY, receiveAddress);
        intent.putExtra(Constant.TOEKN_AMOUNT, amount);
        intent.putExtra(Constant.TOEKN_NAME, token);
        context.startActivity(intent);
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenTransferActivity(Context context) {
        Intent intent = new Intent(context, TokenTransferActivity.class);
        context.startActivity(intent);
    }

    private void isTransfer() {
        String receiveAddr = mEdtWalletAddress.getText().toString();
        String amount = mEdtTransferNum.getText().toString();
        if (!TextUtils.isEmpty(receiveAddr) && !TextUtils.isEmpty(amount) && !mTvErrAmount.isShown() && !mTvErrAddr.isShown()) {
            mBtnConfirm.setEnabled(true);
        } else {
            mBtnConfirm.setEnabled(false);
        }
    }

}
