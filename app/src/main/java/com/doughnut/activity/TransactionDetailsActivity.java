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
    private TextView mTvHash;
    private TextView mTvType;
    private TextView mTvFrom;
    private TextView mTvTo;
    private TextView mTvAmount;
    private TextView mTvTransferAmount;
    private TextView mTvValue;
    private TextView mTvTransferValue;
    private TextView mTvTransferType;
    private TextView mTvGas;
    private TextView mTvTime;
    private TextView mTvResult;
    private TextView mTvMemo;

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
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mTvHash = findViewById(R.id.tv_hash);
        mTvType = findViewById(R.id.tv_type);
        mTvFrom = findViewById(R.id.tv_from);
        mTvTo = findViewById(R.id.tv_to);
        mTvAmount = findViewById(R.id.tv_amount);
        mTvTransferAmount = findViewById(R.id.tv_transfer_amount);
        mTvValue = findViewById(R.id.tv_value);
        mTvTransferValue = findViewById(R.id.tv_transfer_value);
        mTvTransferType = findViewById(R.id.tv_transfer_type);
        mTvGas = findViewById(R.id.tv_gas);
        mTvTime = findViewById(R.id.tv_time);
        mTvResult = findViewById(R.id.tv_result);
        mTvMemo = findViewById(R.id.tv_memo);

        updateData();
    }

    private void updateData() {
        JSONObject gets;
        JSONObject pays;
        String address = WalletSp.getInstance(this, "").getCurrentWallet();
        switch (mTransactions.getType()) {
            case "sent":
                mTvAmount.setText("-" + mTransactions.getAmount().getValue() + " " + mTransactions.getAmount().getCurrency());
                mTvAmount.setTextColor(getResources().getColor(R.color.common_red));
                mTvTo.setText(mTransactions.getCounterparty());
                mTvFrom.setText(address);
                break;
            case "received":
                mTvAmount.setText("+" + mTransactions.getAmount().getValue() + " " + mTransactions.getAmount().getCurrency());
                mTvAmount.setTextColor(getResources().getColor(R.color.common_blue));
                mTvFrom.setText(mTransactions.getCounterparty());
                mTvTo.setText(address);
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
                        mTvAmount.setText(mTransactions.getGets().getValue() + " " + getsCur + " -> " + mTransactions.getPays().getValue() + " " + paysCur);
                        mTvTo.setText("---");
                    } else {
                        mTvTo.setText(receiver);
                        mTvAmount.setText(paysAmount.stripTrailingZeros().toPlainString() + " " + getsCur + " -> " + getsAmount.stripTrailingZeros().toPlainString() + " " + paysCur);
                    }
                    mTvFrom.setText(address);
                }
                mTvAmount.setTextColor(getResources().getColor(R.color.common_green));
                break;
            case "offercancel":
                if (mTransactions.getGets() != null && mTransactions.getPays() != null) {
                    mTvAmount.setText(mTransactions.getGets().getValue() + " " + mTransactions.getGets().getCurrency() + " -> " + mTransactions.getPays().getValue() + " " + mTransactions.getPays().getCurrency());
                } else {
                    mTvAmount.setText("---");
                }
                mTvAmount.setTextColor(getResources().getColor(R.color.common_green));
                mTvFrom.setText(address);
                mTvTo.setText("---");
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
                    mTvFrom.setText(receiver);
                    mTvAmount.setText(paysAmount1.stripTrailingZeros().toPlainString() + " " + payCurrency + " -> " + getsAmount1.stripTrailingZeros().toPlainString() + " " + getCurrency);
                }
                mTvFrom.setText(address);
                mTvAmount.setTextColor(getResources().getColor(R.color.common_green));
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
            mTvMemo.setText(info);
        }
        mTvGas.setText(new BigDecimal(mTransactions.getFee()).stripTrailingZeros().toPlainString() + " SWT");
        mTvHash.setText(mTransactions.getHash());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(mTransactions.getDate().longValue() * 1000);
        String sim = formatter.format(date);
        mTvTime.setText(sim);
        if ("tesSUCCESS".equals(mTransactions.getResult())) {
            mTvResult.setText("成功");
            mTvResult.setTextColor(getResources().getColor(R.color.common_green));
        } else {
            mTvResult.setText("失败");
            mTvResult.setTextColor(getResources().getColor(R.color.common_red));
        }
    }

    public static void startTransactionDetailActivity(Context context, Transactions data) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        mTransactions = data;
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}
