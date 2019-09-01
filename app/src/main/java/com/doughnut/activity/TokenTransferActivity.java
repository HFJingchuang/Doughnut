
package com.doughnut.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jtblk.utils.CheckUtils;
import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.dialog.EditDialog;
import com.doughnut.dialog.OrderDetailDialog;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TokenTransferActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "TokenTransferActivity";
    private TitleBar mTitleBar;
    private WheelView mWhTokenName;
    private TextView mTvGas;
    private EditText mEdtWalletAddress, mEdtTransferNum, mEdtTransferRemark;
    private Button mBtnNext;
    private double mGasPrice = 0.0f;
    private List<String> tokenEntries;

    private final static int SCAN_REQUEST_CODE = 10001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_token);
        initView();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.titleBar_transfer));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightDrawable(R.drawable.ic_pop_item_scan);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onRightClick(View view) {
                startActivityForResult(new Intent(TokenTransferActivity.this, CaptureActivity.class), SCAN_REQUEST_CODE);
            }
        });

        mEdtWalletAddress = findViewById(R.id.edt_wallet_address);

        mEdtTransferNum = findViewById(R.id.edt_transfer_num);
        mWhTokenName = findViewById(R.id.wh_token_name);

        //准备数据
        tokenEntries = new ArrayList<>();
        tokenEntries.clear();
        tokenEntries.add("SWT");
        tokenEntries.add("CNT");
        tokenEntries.add("MOAC");
        tokenEntries.add("JCC");
        tokenEntries.add("CSP");
        mWhTokenName.setAdapter(new ArrayWheelAdapter(tokenEntries));
        mWhTokenName.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Toast.makeText(TokenTransferActivity.this, "" + tokenEntries.get(index), Toast.LENGTH_SHORT).show();
            }
        });

//        mGas = mWalletUtil.getRecommendGas(mGas, defaultToken);

        mTvGas = findViewById(R.id.tv_transfer_gas);
        mTvGas.setOnClickListener(this);
//        mWalletUtil.gasPrice(new WCallback() {
//            @Override
//            public void onGetWResult(int ret, GsonUtil extra) {
//                if (ret == 0) {
//                    mGasPrice = extra.getDouble("gasPrice", 0.0);
//                    mWalletUtil.calculateGasInToken(mGas, mGasPrice, defaultToken, new WCallback() {
//                        @Override
//                        public void onGetWResult(int ret, GsonUtil extra) {
//                            mTvGas.setText(extra.getString("gas", ""));
//                        }
//                    });
//                }
//            }
//        });
//        DecimalFormat df = new DecimalFormat("#.00000000");
//        mEdtTransferNum.setText(mAmount > 0.0f ? df.format(mAmount).toString() : "");

        mEdtTransferRemark = findViewById(R.id.edt_transfer_remark);

        mBtnNext = findViewById(R.id.btn_next);

        mBtnNext.setOnClickListener(this);

        if (getIntent() != null) {
            mEdtWalletAddress.setText(getIntent().getStringExtra(Constant.RECEIVE_ADDRESS_KEY));
            mEdtTransferNum.setText(getIntent().getStringExtra(Constant.TOEKN_AMOUNT));
            mWhTokenName.setCurrentItem(getIntent().getIntExtra(Constant.TOEKN_NAME, 0));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SCAN_REQUEST_CODE) {
                //扫描到地址
                String scanResult = data.getStringExtra("result");
                if (TextUtils.isEmpty(scanResult)) {
                    ToastUtil.toast(this, getString(R.string.toast_scan_failure));
                } else {
                    //swt
//                    handleSwtScanResult(scanResult);
                    GsonUtil res = new GsonUtil(scanResult);
                    startTokenTransferActivity(this, res.getString(Constant.RECEIVE_ADDRESS_KEY, ""), res.getString(Constant.TOEKN_AMOUNT, ""), res.getInt(Constant.TOEKN_NAME, 0));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (paramCheck()) {
                    OrderDetailDialog orderDetailDialog = new OrderDetailDialog(TokenTransferActivity.this,
                            new OrderDetailDialog.onConfirmOrderListener() {
                                @Override
                                public void onConfirmOrder() {
                                    verifyPwd();
                                }
                            }, mEdtWalletAddress.getText().toString(),
                            mGasPrice, 0, Util.parseDouble(mEdtTransferNum.getText().toString()), tokenEntries.get(mWhTokenName.getCurrentItem()), mEdtTransferRemark.getText().toString());
                    orderDetailDialog.show();
                }
                break;
            case R.id.tv_transfer_gas:
                break;
        }
    }

    private void verifyPwd() {
//        EditDialog editDialog = new EditDialog(TokenTransferActivity.this, new EditDialog.PwdResultListener() {
//            @Override
//            public void authPwd(String tag, boolean result, String key) {
//                if (TextUtils.equals(tag, "transaction")) {
//                    if (result) {
////                        pwdRight();
//                        sendTranscation();
//                    } else {
//                        ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_order_password_incorrect));
//                    }
//                }
//            }
//        }, "", "transaction");
//        editDialog.show();
    }

    public void sendTranscation() {
        String hash = WalletManager.getInstance(this).transfer("", WalletSp.getInstance(this, "").getCurrentWallet(), mEdtWalletAddress.getText().toString(),
                "", "", mEdtTransferNum.getText().toString(), mEdtTransferRemark.getText().toString());
        if (!isValidHash(hash)) {
            ToastUtil.toast(this, "交易已发送，请等待链上确认，仅需几秒时间。。");
        } else {
            ToastUtil.toast(this, "交易发送失败" + "\n" + hash);
        }
    }

    private boolean isValidHash(String hash) {
        final String HASH_RE = "^[A-F0-9]{64}$";
        Pattern pattern = Pattern.compile(HASH_RE);
        Matcher matcher = pattern.matcher(hash);
        return matcher.matches();
    }

    private boolean paramCheck() {

        String address = mEdtWalletAddress.getText().toString();
        String num = mEdtTransferNum.getText().toString();

//        if (TextUtils.isEmpty(mTvToken.getText().toString())) {
//            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_choose_token), "OK");
//            return false;
//        }
        if (TextUtils.isEmpty(address)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_address), "OK");
            return false;
        }

        if (TextUtils.equals(address, WalletSp.getInstance(this, "").getCurrentWallet())) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_receive_address_incorrect), "OK");
            return false;
        }

        if (!CheckUtils.isValidAddress(address)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_address_format_incorrect), "OK");
            return false;
        }


        if ((TextUtils.isEmpty(num) || Util.parseDouble(num) <= 0.0f)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_amount_incorrect), "OK");
            return false;
        }
        return true;
    }

    private void updateBtnToTranferingState() {
        mBtnNext.setEnabled(false);
        mBtnNext.setText(getString(R.string.btn_transferring));
    }

    private void resetTranferBtn() {
        mBtnNext.setEnabled(true);
        mBtnNext.setText(getString(R.string.btn_next));
    }


    class ArrayWheelAdapter implements WheelAdapter {
        private List<String> mList;

        public ArrayWheelAdapter(List<String> pList) {
            this.mList = pList;
        }

        @Override
        public int getItemsCount() {
            return this.mList.size();
        }

        @Override
        public Object getItem(int index) {
            return this.mList.get(index);
        }

        @Override
        public int indexOf(Object o) {
            return mList.indexOf(o);

        }
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenTransferActivity(Context context, String receiveAddress,
                                                  String amount, int token) {
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

}
