
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.jtblk.client.Wallet;
import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.Line;
import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.dialog.EthGasSettignDialog;
import com.doughnut.dialog.LoadDialog;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.dialog.TransferDialog;
import com.doughnut.utils.AESUtil;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.CashierInputFilter;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.zxing.activity.CaptureActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TokenTransferActivity extends BaseActivity implements View.OnClickListener {

    private final static String FEE = "10";
    private final static long FIFTEEN = 15 * 60 * 1000L;

    private TitleBar mTitleBar;
    private TextView mTvTokenName, mTvToken, mTvBalance, mTvErrAddr, mTvErrAmount, mTvGas;
    private EditText mEdtWalletAddress, mEdtTransferNum, mEdtMemo;
    private Button mBtnConfirm;
    private LinearLayout mLayoutToken, mLayoutLatest, mLayoutGas;
    private String mBalance;
    private String mIssue;
    private String mCurrentWallet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_token);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLayoutToken.setEnabled(true);
        getTransferToken();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
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
                finish();
            }

            @Override
            public void onRightClick(View view) {
                startActivity(new Intent(TokenTransferActivity.this, CaptureActivity.class));
            }
        });

        mLayoutLatest = findViewById(R.id.layout_latest);
        mLayoutLatest.setOnClickListener(this);

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
                    isTransfer();
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
                        if (CaclUtil.compare(input, "0") == 0) {
                            mTvErrAmount.setText(getString(R.string.tv_transfer_err_aomunt));
                            mTvErrAmount.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtTransferNum.requestFocus();
                                }
                            });
                        } else if (CaclUtil.compare(mBalance, input) < 0 && !mTvErrAddr.isShown()) {
                            mTvErrAmount.setText(getString(R.string.tv_err_amount));
                            mTvErrAmount.setVisibility(View.VISIBLE);
                            AppConfig.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtTransferNum.requestFocus();
                                }
                            });
                        }

                    }
                    isTransfer();
                }
            }
        });
        mTvTokenName = findViewById(R.id.tv_token_name);
        mTvToken = findViewById(R.id.tv_token);
        mTvBalance = findViewById(R.id.tv_balance);
        mTvBalance.setText("---");
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

        mLayoutGas = findViewById(R.id.layout_gas);
        mLayoutGas.setOnClickListener(this);
        mTvGas = findViewById(R.id.tv_gas);

        mBtnConfirm = findViewById(R.id.btn_send);
        mBtnConfirm.setOnClickListener(this);
    }

    private void initData() {
        mCurrentWallet = WalletSp.getInstance(this, "").getCurrentWallet();
        if (getIntent() != null) {
            String address = getIntent().getStringExtra(Constant.RECEIVE_ADDRESS_KEY);
            if (!TextUtils.isEmpty(address)) {
                mEdtWalletAddress.setText(address);
                mEdtWalletAddress.setSelection(address.length());
            }
            String amount = getIntent().getStringExtra(Constant.TOEKN_AMOUNT);
            if (!TextUtils.isEmpty(amount)) {
                mEdtTransferNum.setText(amount);
                if (!mTvErrAddr.isShown()) {
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEdtTransferNum.requestFocus();
                            mEdtTransferNum.setSelection(amount.length());
                        }
                    });
                }
            }
//            token = getIntent().getStringExtra(Constant.TOEKN_NAME);
//            if (!TextUtils.isEmpty(token)) {
//                mTvTokenName.setText(token);
//                setBalance(token);
//                return;
//            }

//            String memo = getIntent().getStringExtra(Constant.MEMO);
//            if (!TextUtils.isEmpty(memo)) {
//                mEdtMemo.setText(memo);
//            }
        }
        mTvGas.setText(getFee());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                mBtnConfirm.setEnabled(false);
                saveContact();
                if (!NoPwdTransfer()) {
                    sendTranscation();
                }
                break;
            case R.id.layout_token:
                mLayoutToken.setEnabled(false);
                TransferTokenActivity.startActivity(this);
                break;
            case R.id.layout_latest:
                ContactsActivity.startContactsActivity(this);
                break;
            case R.id.layout_gas:
                ViewUtil.hideKeyboard(mEdtMemo);
                new EthGasSettignDialog(this, new EthGasSettignDialog.OnSettingGasListener() {
                    @Override
                    public void onSettingGas(String gasPrice) {
                        setFee(gasPrice);
                        mTvGas.setText(gasPrice);
                    }
                }, mTvGas.getText().toString()).show();
                break;
        }
    }

    private void sendTranscation() {
        String currentAddr = WalletSp.getInstance(TokenTransferActivity.this, "").getCurrentWallet();
        new TransferDialog(this, currentAddr)
                .setResultListener(new TransferDialog.PwdResultListener() {
                    @Override
                    public void authPwd(boolean result, String key) {
                        if (result) {
                            String to = mEdtWalletAddress.getText().toString();
                            String token = mTvTokenName.getText().toString();
                            String value = mEdtTransferNum.getText().toString();
                            String memo = mEdtMemo.getText().toString();
                            String gas = mTvGas.getText().toString();
                            BigDecimal fee = new BigDecimal(gas).multiply(new BigDecimal(1000000));
                            WalletManager.getInstance(TokenTransferActivity.this).transfer(key, currentAddr, to, token, mIssue, value, fee.stripTrailingZeros().toPlainString(), memo, new ICallBack() {
                                @Override
                                public void onResponse(Object result) {
                                    boolean isSuccess = (boolean) result;
                                    String msg = getString(R.string.dailog_msg_success);
                                    if (!isSuccess) {
                                        msg = getString(R.string.dialog_msg_fail);
                                    } else {
                                        mEdtMemo.setText("");
                                        mEdtTransferNum.setText("");
                                    }
                                    new MsgDialog(TokenTransferActivity.this, msg).setIsHook(isSuccess).show();
                                }
                            });
                        }
                        ViewUtil.hideKeyboard(getWindow().getCurrentFocus());
                        mBtnConfirm.setEnabled(true);
                    }
                }).show();
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenTransferActivity(Context context, String receiveAddress,
                                                  String amount) {
        Intent intent = new Intent(context, TokenTransferActivity.class);
        intent.putExtra(Constant.RECEIVE_ADDRESS_KEY, receiveAddress);
        intent.putExtra(Constant.TOEKN_AMOUNT, amount);
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

    /**
     * 获取选择的币种
     */
    private void getTransferToken() {
        String fileName = getPackageName() + "_transfer_token";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        if (TextUtils.isEmpty(token)) {
            token = WConstant.CURRENCY_SWTC;
            mIssue = "";
        }
        setBalance(token);
    }

    /**
     * @param token
     */
    private void setBalance(String token) {
        // 取得钱包资产
        mBalance = "0.00";
        WalletManager.getInstance(this).getBalance(mCurrentWallet, true, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    AccountRelations accountRelations = (AccountRelations) response;
                    if (accountRelations != null && accountRelations.getLines() != null) {
                        List<Line> lines = accountRelations.getLines();
                        // 排除余额为零的token
                        try {
                            for (int i = 0; i < lines.size(); i++) {
                                Line line = lines.get(i);
                                String currency = line.getCurrency();
                                if (TextUtils.equals(WConstant.CURRENCY_SWT, currency)) {
                                    currency = WConstant.CURRENCY_SWTC;
                                }
                                if (TextUtils.equals(token, currency)) {
                                    mBalance = CaclUtil.formatAmount(line.getBalance(), 4);
                                }
                            }

                            mTvBalance.setText(mBalance);
                            mTvTokenName.setText(token);
                            mTvToken.setText(token);
                            if (CaclUtil.compare(mBalance, "0") == 0) {
                                new MsgDialog(TokenTransferActivity.this, String.format(getString(R.string.tv_no_token), token)).setIsHook(false).show();
                                String fileName = getPackageName() + "_transfer_token";
                                SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token", WConstant.CURRENCY_SWTC);
                                editor.apply();
                                return;
                            }

                            // 获取issue
                            String fileName = getPackageName() + "_tokens";
                            SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                            String tokens = sharedPreferences.getString("tokens", "");
                            if (!TextUtils.isEmpty(tokens)) {
                                Map<String, String> tokenMap = JSON.parseObject(tokens, Map.class);
                                mIssue = tokenMap.get(token);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 保存转账联系地址
     */
    private void saveContact() {
        // 本地保存tokens
        String fileName = getPackageName() + "_contacts";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String contacts = sharedPreferences.getString("contacts", "");
        Map<String, String> contactMap;
        String address = mEdtWalletAddress.getText().toString();
        if (!TextUtils.isEmpty(contacts)) {
            contactMap = JSON.parseObject(contacts, Map.class);
        } else {
            contactMap = new HashMap<>();
        }
        GsonUtil newContact = new GsonUtil("{}");
        newContact.putString("address", address);
        newContact.putString("amount", mEdtTransferNum.getText().toString());
        newContact.putString("token", mTvTokenName.getText().toString());
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        String now = formatter.format(System.currentTimeMillis());
        newContact.putString("time", now);
        contactMap.put(address, newContact.toString());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contacts", JSON.toJSONString(contactMap));
        editor.apply();
    }

    /**
     * 免密支付
     */
    private boolean NoPwdTransfer() {
        String fileName = getPackageName() + "_pwd_" + mCurrentWallet;
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong("time", 0);
        long now = System.currentTimeMillis();
        long diff = now - time;
        if (diff < FIFTEEN) {
            LoadDialog loadDialog = new LoadDialog(TokenTransferActivity.this, getString(R.string.dialog_tranfer));
            loadDialog.show();
            String key = sharedPreferences.getString("key", "");
            String encrypt = sharedPreferences.getString("encrypt", "");
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(encrypt)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                mBtnConfirm.setEnabled(true);
                return false;
            }
            AESUtil.decrypt(key, encrypt, new ICallBack() {
                @Override
                public void onResponse(Object response) {
                    String pwd = (String) response;
                    String keyStore = WalletSp.getInstance(TokenTransferActivity.this, mCurrentWallet).getKeyStore();
                    WalletManager.getInstance(TokenTransferActivity.this).getPrivateKey(pwd, keyStore, new ICallBack() {
                        @Override
                        public void onResponse(Object response) {
                            String privateKey = (String) response;
                            String to = mEdtWalletAddress.getText().toString();
                            String token = mTvTokenName.getText().toString();
                            String value = mEdtTransferNum.getText().toString();
                            String memo = mEdtMemo.getText().toString();
                            String gas = mTvGas.getText().toString();
                            BigDecimal fee = new BigDecimal(gas).multiply(new BigDecimal(1000000));
                            WalletManager.getInstance(TokenTransferActivity.this).transfer(privateKey, mCurrentWallet, to, token, mIssue, value, fee.stripTrailingZeros().toPlainString(), memo, new ICallBack() {
                                @Override
                                public void onResponse(Object result) {
                                    boolean isSuccess = (boolean) result;
                                    String msg = getString(R.string.dailog_msg_success);
                                    if (!isSuccess) {
                                        mBtnConfirm.setEnabled(true);
                                        msg = getString(R.string.dialog_msg_fail);
                                    } else {
                                        mEdtMemo.setText("");
                                        mEdtTransferNum.setText("");
                                    }
                                    new MsgDialog(TokenTransferActivity.this, msg).setIsHook(isSuccess).show();
                                }
                            });
                            loadDialog.dismiss();
                        }
                    });
                }
            });
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            mBtnConfirm.setEnabled(true);
            return false;
        }
        return true;
    }

    /**
     * 保存燃料费用
     *
     * @param fee
     */
    private void setFee(String fee) {
        String fileName = getPackageName() + "_fee_" + mCurrentWallet;
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fee", fee);
        editor.apply();
    }

    /**
     * 获取燃料费用
     */
    private String getFee() {
        String fileName = getPackageName() + "_fee_" + mCurrentWallet;
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("fee", "0.00001");
    }
}
