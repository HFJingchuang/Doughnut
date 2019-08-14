package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.jtblk.client.bean.Memo;
import com.android.jtblk.client.bean.Transactions;
import com.doughnut.R;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.QRUtils;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletSp;
import com.google.zxing.WriterException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TransactionDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;
    private TextView mTvTransactionStatus;
    private TextView mTvCount;
    private TextView mTvSender;
    private TextView mTvReceiver;
    private TextView mTvGas;
    private TextView mTvInfo;
    private TextView mTvTransactionId;
    private TextView mTvTransactionTime;
    private TextView mTvCopyUrl;

    private ImageView mImgTransactionQrCode;
    private GsonUtil transactionData;
    private static Transactions mTransactions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        if (getIntent() != null) {
            String data = getIntent().getStringExtra("ITEM");
            transactionData = new GsonUtil(data);
        }
        initView();
    }

    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_transaction_details));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mTvTransactionStatus = findViewById(R.id.tv_transaction_status);
        mTvCount = findViewById(R.id.tv_transaction_count);
        mTvSender = findViewById(R.id.tv_send_address);
        mTvSender.setOnClickListener(this);

        mTvReceiver = findViewById(R.id.tv_receive_address);
        mTvReceiver.setOnClickListener(this);

        mTvGas = findViewById(R.id.tv_gas);
        mTvInfo = findViewById(R.id.tv_info);
        mTvTransactionId = findViewById(R.id.tv_transaction_id);
        mTvTransactionId.setOnClickListener(this);
        mTvTransactionTime = findViewById(R.id.tv_transaction_time);
        mTvCopyUrl = findViewById(R.id.tv_copy_transaction_url);
        mTvCopyUrl.setOnClickListener(this);
        mImgTransactionQrCode = findViewById(R.id.img_transaction_qrcode);

        updateData();
    }

    private void updateData() {
        JSONObject gets;
        JSONObject pays;
        String address = WalletSp.getInstance(this, "").getCurrentWallet();
        switch (mTransactions.getType()) {
            case "sent":
                mTvCount.setText("-" + mTransactions.getAmount().getValue() + " " + mTransactions.getAmount().getCurrency());
                mTvCount.setTextColor(getResources().getColor(R.color.common_red));
                mTvReceiver.setText(mTransactions.getCounterparty());
                mTvSender.setText(address);
                break;
            case "received":
                mTvCount.setText("+" + mTransactions.getAmount().getValue() + " " + mTransactions.getAmount().getCurrency());
                mTvCount.setTextColor(getResources().getColor(R.color.common_blue));
                mTvSender.setText(mTransactions.getCounterparty());
                mTvReceiver.setText(address);
                break;
            case "offernew":
                String getsCur = mTransactions.getGets().getCurrency();
                String paysCur = mTransactions.getPays().getCurrency();
                BigDecimal getsAmount = new BigDecimal("0");
                BigDecimal paysAmount = new BigDecimal("0");
                if (mTransactions.getEffects() != null) {
                    String receiver = "";
                    for (int i = 0; i < mTransactions.getEffects().size(); i++) {
                        pays = mTransactions.getEffects().getJSONObject(i).getJSONObject("paid");
                        gets = mTransactions.getEffects().getJSONObject(i).getJSONObject("got");
                        if (pays != null && gets != null) {
                            String currency = pays.getString("currency");
                            String paysCount = pays.getString("value");
                            String addr = mTransactions.getEffects().getJSONObject(i).getJSONObject("counterparty").getString("account");
                            String payStr = paysCount + " " + currency;
                            if (TextUtils.equals(currency, getsCur)) {
                                paysAmount = paysAmount.add(new BigDecimal(paysCount));
                            }
                            currency = gets.getString("currency");
                            String getsCount = gets.getString("value");
                            String getStr = getsCount + " " + currency;
                            if (TextUtils.equals(currency, paysCur)) {
                                getsAmount = getsAmount.add(new BigDecimal(getsCount));
                            }
                            receiver = receiver + addr + "\n" + payStr + " ->" + getStr + "\n \n";
                        }
                    }
                    if (getsAmount.equals(new BigDecimal("0")) || paysAmount.equals(new BigDecimal("0"))) {
                        mTvCount.setText(mTransactions.getGets().getValue() + " " + getsCur + " -> " + mTransactions.getPays().getValue() + " " + paysCur);
                        mTvReceiver.setText("---");
                    } else {
                        mTvReceiver.setText(receiver);
                        mTvCount.setText(paysAmount.stripTrailingZeros().toPlainString() + " " + getsCur + " -> " + getsAmount.stripTrailingZeros().toPlainString() + " " + paysCur);
                    }
                    mTvSender.setText(address);
                }
                mTvCount.setTextColor(getResources().getColor(R.color.common_green));
                break;
            case "offercancel":
                if (mTransactions.getGets() != null && mTransactions.getPays() != null) {
                    mTvCount.setText(mTransactions.getGets().getValue() + " " + mTransactions.getGets().getCurrency() + " -> " + mTransactions.getPays().getValue() + " " + mTransactions.getPays().getCurrency());
                } else {
                    mTvCount.setText("---");
                }
                mTvCount.setTextColor(getResources().getColor(R.color.common_green));
                mTvSender.setText(address);
                mTvReceiver.setText("---");
                break;
            case "offereffect":
                BigDecimal getsAmount1 = new BigDecimal("0");
                BigDecimal paysAmount1 = new BigDecimal("0");
                if (mTransactions.getEffects() != null) {
                    String receiver = "";
                    for (int i = 0; i < mTransactions.getEffects().size(); i++) {
                        pays = mTransactions.getEffects().getJSONObject(i).getJSONObject("paid");
                        gets = mTransactions.getEffects().getJSONObject(i).getJSONObject("got");
                        if (pays != null && gets != null) {
                            String addr = mTransactions.getEffects().getJSONObject(i).getJSONObject("counterparty").getString("account");
                            String currency = pays.getString("currency");
                            String paysCount = pays.getString("value");
                            String payStr = paysCount + " " + currency;
                            paysAmount1 = paysAmount1.add(new BigDecimal(paysCount));

                            currency = gets.getString("currency");
                            String getsCount = gets.getString("value");
                            String getStr = getsCount + " " + currency;
                            getsAmount1 = getsAmount1.add(new BigDecimal(getsCount));
                            receiver = receiver + addr + "\n" + getStr + " -> " + payStr + "\n \n";
                        }
                    }
                    String payCurrency = mTransactions.getEffects().getJSONObject(0).getJSONObject("paid").getString("currency");
                    String getCurrency = mTransactions.getEffects().getJSONObject(0).getJSONObject("got").getString("currency");
                    mTvSender.setText(receiver);
                    mTvCount.setText(paysAmount1.stripTrailingZeros().toPlainString() + " " + payCurrency + " -> " + getsAmount1.stripTrailingZeros().toPlainString() + " " + getCurrency);
                }
                mTvReceiver.setText(address);
                mTvCount.setTextColor(getResources().getColor(R.color.common_green));
                break;
            default:
                // TODO parse other type
                break;
        }
        List<Memo> memos = mTransactions.getMemos();
        if (memos != null && memos.size() > 0) {
            String info = "";
            for (int i = 0; i < memos.size(); i++) {
                info = info + memos.get(i).getMemoData() + "\n";
            }
            mTvInfo.setText(info);
        }
        mTvGas.setText(new BigDecimal(mTransactions.getFee()).stripTrailingZeros().toPlainString() + " SWT");
        mTvTransactionId.setText(mTransactions.getHash());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(mTransactions.getDate().longValue() * 1000);
        String sim = formatter.format(date);
        mTvTransactionTime.setText(sim);
        if ("tesSUCCESS".equals(mTransactions.getResult())) {
            mTvTransactionStatus.setText("成功");
            mTvTransactionStatus.setTextColor(getResources().getColor(R.color.common_green));
        } else {
            mTvTransactionStatus.setText("失败");
            mTvTransactionStatus.setTextColor(getResources().getColor(R.color.common_red));
        }
    }

    public static void startTransactionDetailActivity(Context context, Transactions data) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        mTransactions = data;
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void createQRCode(String transactionUrl) {
        try {
            Bitmap bitmap = QRUtils.createQRCode(transactionUrl, getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgTransactionQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mTvCopyUrl) {
        } else if (v == mTvSender) {
            Util.clipboard(TransactionDetailsActivity.this, "", mTvSender.getText().toString());
            ToastUtil.toast(TransactionDetailsActivity.this, getString(R.string.toast_send_address_copied))
            ;
        } else if (v == mTvReceiver) {
            Util.clipboard(TransactionDetailsActivity.this, "", mTvReceiver.getText().toString());
            ToastUtil.toast(TransactionDetailsActivity.this, getString(R.string.toast_receive_address_copied))
            ;
        } else if (v == mTvTransactionId) {
        }
    }
}
