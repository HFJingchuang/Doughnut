package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.jtblk.client.bean.Memo;
import com.android.jtblk.client.bean.Transactions;
import com.doughnut.R;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.view.indicator.LinePagerIndicatorDecoration;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletSp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TransactionDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;
    private TextView mTvHash;
    private TextView mTvType;
    private TextView mTvFrom;
    private ImageView mImgFrom;
    private TextView mTvTo;
    private ImageView mImgTo;
    private TextView mTvAmount;
    private TextView mTvToken;
    private TextView mTvEntrustContent, mTvPayAmountNone;
    private TextView mTvTurnoverContent;
    private TextView mTvValue, mTvValueToken;
    private TextView mTvTurnoverValue, mTvTurnoverValueToken;
    private TextView mTvTransferType;
    private TextView mTvGas;
    private TextView mTvTime;
    private TextView mTvResult;
    private TextView mTvMemo;
    private LinearLayout mLayoutHash;
    private RelativeLayout mLayoutAmount;
    private RelativeLayout mLayoutEntrustAmount;
    private RelativeLayout mLayoutTurnoverAmount;
    private RelativeLayout mLayoutTurnoverValue;
    private RelativeLayout mLayoutValue;
    private RelativeLayout mLayoutFrom;
    private RelativeLayout mLayoutTo;
    private RelativeLayout mLayoutToV;
    private RelativeLayout mLayoutType;
    private RecyclerView mRecyclerView;
    private TransactionInfoAdapter mTransactionInfoAdapter;
    private JSONArray mEffects = new JSONArray();

    private static Transactions mTransactions = new Transactions();
    private String currentAddr;
    private String mFrom;
    private String mTo;
    private static final int SCALE = 2;
    private static final String KEY_CNY = WConstant.CURRENCY_CNY + "_" + WConstant.CURRENCY_ISSUE;
    private static final String KEY_SWT = WConstant.CURRENCY_SWT + "_" + "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        if (getIntent() != null) {
            mTransactions = getIntent().getParcelableExtra("data");
        }
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_hash:
                Util.clipboard(this, "", mTvHash.getText().toString());
                new MsgDialog(this, getString(R.string.toast_hash_cp)).show();
                break;
            case R.id.img_copy_from:
            case R.id.layout_from:
                Util.clipboard(this, "", mFrom);
                new MsgDialog(this, getString(R.string.toast_from_cp)).show();
                break;
            case R.id.img_copy_to:
            case R.id.layout_to:
                Util.clipboard(this, "", mTo);
                new MsgDialog(this, getString(R.string.toast_to_cp)).show();
                break;
        }
    }

    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_transaction_details));
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });

        mLayoutHash = findViewById(R.id.layout_hash);
        mLayoutHash.setOnClickListener(this);
        mTvHash = findViewById(R.id.tv_hash);

        mTvType = findViewById(R.id.tv_type);

        mLayoutFrom = findViewById(R.id.layout_from);
        mLayoutFrom.setOnClickListener(this);
        mTvFrom = findViewById(R.id.tv_from);
        mImgFrom = findViewById(R.id.img_copy_from);
        mImgFrom.setOnClickListener(this);

        mLayoutToV = findViewById(R.id.layout_to_v);
        mLayoutTo = findViewById(R.id.layout_to);
        mLayoutTo.setOnClickListener(this);
        mTvTo = findViewById(R.id.tv_to);
        mImgTo = findViewById(R.id.img_copy_to);
        mImgTo.setOnClickListener(this);

        mTvAmount = findViewById(R.id.tv_amount);
        mTvToken = findViewById(R.id.tv_token);
        mLayoutAmount = findViewById(R.id.layout_amount);

        mLayoutEntrustAmount = findViewById(R.id.layout_entrust_amount);
        mTvEntrustContent = findViewById(R.id.tv_entrust_content);
        mTvPayAmountNone = findViewById(R.id.tv_entrust_amount_none);

        mTvValue = findViewById(R.id.tv_value);
        mTvValueToken = findViewById(R.id.tv_value_token);
        mLayoutValue = findViewById(R.id.layout_value);

        mLayoutTurnoverAmount = findViewById(R.id.layout_turnover_amount);
        mTvTurnoverContent = findViewById(R.id.tv_turnover_content);

        mLayoutTurnoverValue = findViewById(R.id.layout_turnover_value);
        mTvTurnoverValue = findViewById(R.id.tv_turnover_value);
        mTvTurnoverValueToken = findViewById(R.id.tv_turnover_value_token);

        mLayoutType = findViewById(R.id.layout_transfer_type);
        mTvTransferType = findViewById(R.id.tv_transfer_type);

        mTvGas = findViewById(R.id.tv_gas);
        mTvTime = findViewById(R.id.tv_time);
        mTvResult = findViewById(R.id.tv_result);
        mTvMemo = findViewById(R.id.tv_memo);

        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LinePagerIndicatorDecoration(this));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mTransactionInfoAdapter = new TransactionInfoAdapter();
        mRecyclerView.setAdapter(mTransactionInfoAdapter);

        updateData();
    }

    class TransactionInfoAdapter extends RecyclerView.Adapter<TransactionInfoAdapter.VH> {


        class VH extends RecyclerView.ViewHolder {
            private TextView mTvTitleIndex;
            private TextView mTvIndex;
            private TextView mTvContent;
            private TextView mTvValue, mTvValueToken;
            private RelativeLayout mLayoutType;
            private TextView mTvType;
            private ImageView mImgType;
            private ImageView mImgCopy;
            private RelativeLayout mLayoutTo;
            private TextView mTvTo;
            private String mTo;

            public VH(View v) {
                super(v);
                mTvTitleIndex = itemView.findViewById(R.id.tv_title_index);
                mTvIndex = itemView.findViewById(R.id.tv_index);
                mTvContent = itemView.findViewById(R.id.tv_content);
                mTvValue = itemView.findViewById(R.id.tv_value);
                mTvValueToken = itemView.findViewById(R.id.tv_value_token);
                mLayoutTo = itemView.findViewById(R.id.layout_to_v);
                mTvTo = itemView.findViewById(R.id.tv_to);
                mTvTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.clipboard(TransactionDetailsActivity.this, "", mTo);
                        new MsgDialog(TransactionDetailsActivity.this, getString(R.string.toast_to_cp)).show();
                    }
                });
                ViewUtil.EllipsisTextView(mTvTo);
                mLayoutType = itemView.findViewById(R.id.layout_type);
                mTvType = itemView.findViewById(R.id.tv_type);
                mImgType = itemView.findViewById(R.id.img_type);
                mImgCopy = itemView.findViewById(R.id.img_copy1);
                mImgCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.clipboard(TransactionDetailsActivity.this, "", mTo);
                        new MsgDialog(TransactionDetailsActivity.this, getString(R.string.toast_to_cp)).show();
                    }
                });
            }
        }

        @Override
        public TransactionInfoAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_transaction_info, false);
            return new TransactionInfoAdapter.VH(v);
        }

        @Override
        public void onBindViewHolder(TransactionInfoAdapter.VH holder, int position) {
            if (mEffects == null) {
                return;
            }
            JSONObject item = mEffects.getJSONObject(position);
            JSONObject pays;
            JSONObject gets;
            // 首位补零
            String index;
            if (position < 9) {
                index = "0" + (position + 1);
            } else {
                index = String.valueOf(position + 1);
            }
            holder.mTvTitleIndex.setText(index);
            holder.mTvIndex.setText(index);
            String effectType = item.getString("effect");
            if (TextUtils.equals(effectType, "offer_cancelled")) {
                pays = item.getJSONObject("pays");
                gets = item.getJSONObject("gets");
                holder.mLayoutType.setVisibility(View.VISIBLE);
                holder.mLayoutTo.setVisibility(View.GONE);
                holder.mTvType.setText(getResources().getString(R.string.tv_offercancel));
                holder.mImgType.setImageResource(R.drawable.ic_offer_cancel);
            } else if (TextUtils.equals(effectType, "offer_created")) {
                pays = item.getJSONObject("pays");
                gets = item.getJSONObject("gets");
                holder.mLayoutType.setVisibility(View.VISIBLE);
                holder.mLayoutTo.setVisibility(View.GONE);
                holder.mTvType.setText(getResources().getString(R.string.tv_offernew));
                holder.mImgType.setImageResource(R.drawable.ic_offer_new);
            } else {
                pays = item.getJSONObject("paid");
                gets = item.getJSONObject("got");
                holder.mLayoutType.setVisibility(View.GONE);
                holder.mLayoutTo.setVisibility(View.VISIBLE);
                if (pays != null && gets != null) {
                    JSONObject counterparty = item.getJSONObject("counterparty");
                    if (counterparty != null) {
                        String addr = counterparty.getString("account");
                        holder.mTo = addr;
                        holder.mTvTo.setText(addr);
                    }
                }
                if (TextUtils.equals(currentAddr, holder.mTo)) {
                    holder.mTvTo.setTextColor(getResources().getColor(R.color.color_ping_normal));
                } else {
                    holder.mTvTo.setTextColor(getResources().getColor(R.color.color_detail_address));
                }
            }

            if (pays != null && gets != null) {
                String paysCur = pays.getString("currency") + "_" + pays.getString("issuer");
                String paysCount = pays.getString("value");

                String getsCur = gets.getString("currency") + "_" + gets.getString("issuer");
                String getsCount = gets.getString("value");
                holder.mTvContent.setText(formatHtml(CaclUtil.formatAmount(paysCount, SCALE), paysCur, CaclUtil.formatAmount(getsCount, SCALE), getsCur));

                String price = item.getString("price");
                if (!TextUtils.isEmpty(price) && price.contains(" ")) {
                    String[] priceArr = price.split(" ");
                    if (priceArr.length == 2) {
                        holder.mTvValue.setText(priceArr[0]);
                        holder.mTvValueToken.setText(priceArr[1]);
                    }
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            if (mEffects != null) {
                return mEffects.size();
            }
            return 0;
        }
    }

    private void updateData() {
        JSONObject gets;
        JSONObject pays;
        String currentAddr = WalletSp.getInstance(this, "").getCurrentWallet();
        switch (mTransactions.getType()) {
            case "sent":
                mLayoutAmount.setVisibility(View.VISIBLE);
                mTvType.setText(getResources().getString(R.string.tv_transfer));
                mTvAmount.setText(CaclUtil.formatAmount(mTransactions.getAmount().getValue(), SCALE));
                String tokenName = mTransactions.getAmount().getCurrency();
                String tokenIssue = mTransactions.getAmount().getIssuer();
                if (TextUtils.equals(WConstant.CURRENCY_SWT, tokenName)) {
                    tokenName = WConstant.CURRENCY_SWTC;
                } else if (TextUtils.equals(WConstant.CURRENCY_CNY, tokenName) && TextUtils.equals(WConstant.CURRENCY_ISSUE, tokenIssue)) {
                    tokenName = WConstant.CURRENCY_CNT;
                }
                mTvToken.setText(tokenName);
                mTo = mTransactions.getCounterparty();
                mTvTo.setText(mTo);
                mLayoutToV.setVisibility(View.VISIBLE);
                mFrom = currentAddr;
                mTvFrom.setText(mFrom);
                break;
            case "received":
                mLayoutAmount.setVisibility(View.VISIBLE);
                mTvType.setText(getResources().getString(R.string.tv_transfer));
                mTvAmount.setText(CaclUtil.formatAmount(mTransactions.getAmount().getValue(), SCALE));
                String tokenName1 = mTransactions.getAmount().getCurrency();
                String tokenIssue1 = mTransactions.getAmount().getIssuer();
                if (TextUtils.equals(WConstant.CURRENCY_SWT, tokenName1)) {
                    tokenName1 = WConstant.CURRENCY_SWTC;
                } else if (TextUtils.equals(WConstant.CURRENCY_CNY, tokenName1) && TextUtils.equals(WConstant.CURRENCY_ISSUE, tokenIssue1)) {
                    tokenName1 = WConstant.CURRENCY_CNT;
                }
                mTvToken.setText(tokenName1);
                mFrom = mTransactions.getCounterparty();
                mTvFrom.setText(mFrom);
                mTo = currentAddr;
                mLayoutToV.setVisibility(View.VISIBLE);
                mTvTo.setText(mTo);
                break;
            case "offernew":
                mTvType.setText(getResources().getString(R.string.tv_offernew));
                String getsCur = mTransactions.getGets().getCurrency() + "_" + mTransactions.getGets().getIssuer();
                String paysCur = mTransactions.getPays().getCurrency() + "_" + mTransactions.getPays().getIssuer();
                String getsAmount = "0";
                String paysAmount = "0";
                if (mTransactions.getEffects() != null) {
                    mEffects = mTransactions.getEffects();
                    for (int i = mEffects.size() - 1; i >= 0; i--) {
                        JSONObject effect = mEffects.getJSONObject(i);
                        pays = effect.getJSONObject("paid");
                        gets = effect.getJSONObject("got");
                        String effectType = effect.getString("effect");
                        if (!TextUtils.equals(effectType, "offer_cancelled") && !TextUtils.equals(effectType, "offer_created")) {
                            if (pays != null && gets != null) {
                                String currency = pays.getString("currency") + "_" + pays.getString("issuer");
                                String paysCount = pays.getString("value");
                                if (TextUtils.equals(currency, paysCur)) {
                                    paysAmount = CaclUtil.add(paysAmount, paysCount);
                                }
                                currency = gets.getString("currency") + "_" + gets.getString("issuer");
                                String getsCount = gets.getString("value");
                                if (TextUtils.equals(currency, getsCur)) {
                                    getsAmount = CaclUtil.add(getsAmount, getsCount);
                                }
                            } else {
                                mEffects.remove(i);
                            }
                        }
                    }

                    if (mEffects != null && mEffects.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    // 成交
                    if (!TextUtils.isEmpty(getsAmount) && CaclUtil.compare(getsAmount, "0") != 0 && !TextUtils.isEmpty(paysAmount) && CaclUtil.compare(paysAmount, "0") != 0) {
                        mLayoutTurnoverAmount.setVisibility(View.VISIBLE);
                        mTvTurnoverContent.setText(formatHtml(CaclUtil.formatAmount(paysAmount, SCALE), paysCur, CaclUtil.formatAmount(getsAmount, SCALE), getsCur));

                        // 成交价格
                        String price;
                        String token;
                        if (TextUtils.equals(KEY_CNY, getsCur)) {
                            price = amountRatio(getsAmount, paysAmount);
                            token = WConstant.CURRENCY_CNT;
                        } else if (TextUtils.equals(KEY_CNY, paysCur)) {
                            price = amountRatio(paysAmount, getsAmount);
                            token = WConstant.CURRENCY_CNT;
                        } else if (TextUtils.equals(KEY_SWT, getsCur)) {
                            price = amountRatio(getsAmount, paysAmount);
                            token = WConstant.CURRENCY_SWTC;
                        } else {
                            price = amountRatio(paysAmount, getsAmount);
                            token = paysCur.split("_")[0];
                        }
                        if (!TextUtils.equals("0", price)) {
                            mLayoutTurnoverValue.setVisibility(View.VISIBLE);
                            mTvTurnoverValue.setText(CaclUtil.formatAmount(price, 6));
                            mTvTurnoverValueToken.setText(token);
                        }
                    }

                    // 委托
                    String getsValue = mTransactions.getGets().getValue();
                    String paysValue = mTransactions.getPays().getValue();
                    mLayoutEntrustAmount.setVisibility(View.VISIBLE);
                    mTvEntrustContent.setText(formatHtml(CaclUtil.formatAmount(paysValue, SCALE), paysCur, CaclUtil.formatAmount(getsValue, SCALE), getsCur));

                    // 委托价格
                    String price;
                    String token;
                    if (TextUtils.equals(KEY_CNY, getsCur)) {
                        price = amountRatio(getsValue, paysValue);
                        token = WConstant.CURRENCY_CNT;
                    } else if (TextUtils.equals(KEY_CNY, paysCur)) {
                        price = amountRatio(paysValue, getsValue);
                        token = WConstant.CURRENCY_CNT;
                    } else if (TextUtils.equals(KEY_SWT, getsCur)) {
                        price = amountRatio(getsValue, paysValue);
                        token = WConstant.CURRENCY_SWTC;
                    } else {
                        price = amountRatio(paysValue, getsValue);
                        token = paysCur.split("_")[0];
                    }
                    if (!TextUtils.equals("0", price)) {
                        mLayoutValue.setVisibility(View.VISIBLE);
                        mTvValue.setText(CaclUtil.formatAmount(price, 6));
                        mTvValueToken.setText(token);
                    }

                    mFrom = currentAddr;
                    mTvFrom.setText(mFrom);
                }
                break;
            case "offercancel":
                mTvType.setText(getResources().getString(R.string.tv_offercancel));
                mLayoutEntrustAmount.setVisibility(View.VISIBLE);
                if (mTransactions.getGets() != null && mTransactions.getPays() != null) {
                    String getsValue = mTransactions.getGets().getValue();
                    String paysValue = mTransactions.getPays().getValue();
                    String getsToken = mTransactions.getGets().getCurrency() + "_" + mTransactions.getGets().getIssuer();
                    String paysToken = mTransactions.getPays().getCurrency() + "_" + mTransactions.getPays().getIssuer();
                    mTvEntrustContent.setText(formatHtml(CaclUtil.formatAmount(paysValue, SCALE), paysToken, CaclUtil.formatAmount(getsValue, SCALE), getsToken));

                    // 委托价格
                    if (mTransactions.getEffects() != null && mTransactions.getEffects().getJSONObject(0) != null) {
                        String price = mTransactions.getEffects().getJSONObject(0).getString("price");
                        if (!TextUtils.isEmpty(price) && price.contains(" ")) {
                            String[] priceArr = price.split(" ");
                            if (priceArr.length == 2) {
                                mLayoutValue.setVisibility(View.VISIBLE);
                                mTvValue.setText(priceArr[0]);
                                mTvValueToken.setText(priceArr[1]);
                            }
                        }
                    }
                } else {
                    mTvPayAmountNone.setText("---");
                    mTvPayAmountNone.setVisibility(View.VISIBLE);
                }
                mTvAmount.setTextColor(getResources().getColor(R.color.common_green));
                mFrom = currentAddr;
                mTvFrom.setText(mFrom);
                break;
            case "offereffect":
                mTvType.setText(getResources().getString(R.string.tv_offereffect));
                mFrom = mTransactions.getCounterparty();
                mTvFrom.setText(mFrom);
                getsCur = mTransactions.getGets().getCurrency() + "_" + mTransactions.getGets().getIssuer();
                paysCur = mTransactions.getPays().getCurrency() + "_" + mTransactions.getPays().getIssuer();
                String getsAmount1 = "0";
                String paysAmount1 = "0";
                if (mTransactions.getEffects() != null) {
                    mEffects = mTransactions.getEffects();
                    int index = 0;
                    for (int i = mEffects.size() - 1; i >= 0; i--) {
                        JSONObject effect = mEffects.getJSONObject(i);
                        String effectType = effect.getString("effect");
                        if (!TextUtils.equals(effectType, "offer_cancelled") && !TextUtils.equals(effectType, "offer_created")) {
                            // 本机帐号记录前置
                            JSONObject counterparty = effect.getJSONObject("counterparty");
                            if (counterparty != null) {
                                String addr = counterparty.getString("account");
                                if (TextUtils.equals(addr, currentAddr)) {
                                    while (index < mEffects.size() && i > index) {
                                        JSONObject jsonObject = mEffects.getJSONObject(index);
                                        mEffects.set(index, effect);
                                        mEffects.set(i, jsonObject);
                                        effect = mEffects.getJSONObject(i);
                                        index++;
                                        break;
                                    }
                                }
                            }

                            pays = effect.getJSONObject("paid");
                            gets = effect.getJSONObject("got");
                            if (pays != null && gets != null) {
                                String currency = pays.getString("currency") + "_" + pays.getString("issuer");
                                String paysCount = pays.getString("value");
                                if (TextUtils.equals(currency, paysCur)) {
                                    paysAmount1 = CaclUtil.add(paysAmount1, paysCount);
                                }
                                currency = gets.getString("currency") + "_" + gets.getString("issuer");
                                String getsCount = gets.getString("value");
                                if (TextUtils.equals(currency, getsCur)) {
                                    getsAmount1 = CaclUtil.add(getsAmount1, getsCount);
                                }
                            } else {
                                mEffects.remove(i);
                            }
                        }
                    }

                    if (mEffects != null && mEffects.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    // 成交
                    if (!TextUtils.isEmpty(getsAmount1) && CaclUtil.compare(getsAmount1, "0") != 0 && !TextUtils.isEmpty(paysAmount1) && CaclUtil.compare(paysAmount1, "0") != 0) {
                        mLayoutTurnoverAmount.setVisibility(View.VISIBLE);
                        mTvTurnoverContent.setText(formatHtml(CaclUtil.formatAmount(paysAmount1, SCALE), paysCur, CaclUtil.formatAmount(getsAmount1, SCALE), getsCur));

                        // 成交价格
                        String price;
                        String token;
                        if (TextUtils.equals(KEY_CNY, getsCur)) {
                            price = amountRatio(getsAmount1, paysAmount1);
                            token = WConstant.CURRENCY_CNT;
                        } else if (TextUtils.equals(KEY_CNY, paysCur)) {
                            price = amountRatio(paysAmount1, getsAmount1);
                            token = WConstant.CURRENCY_CNT;
                        } else if (TextUtils.equals(KEY_SWT, getsCur)) {
                            price = amountRatio(getsAmount1, paysAmount1);
                            token = WConstant.CURRENCY_SWTC;
                        } else {
                            price = amountRatio(paysAmount1, getsAmount1);
                            token = paysCur.split("_")[0];
                        }
                        if (!TextUtils.equals("0", price)) {
                            mLayoutTurnoverValue.setVisibility(View.VISIBLE);
                            mTvTurnoverValue.setText(CaclUtil.formatAmount(price, 6));
                            mTvTurnoverValueToken.setText(token);
                        }
                    }

                    // 委托
                    String getsValue = mTransactions.getGets().getValue();
                    String paysValue = mTransactions.getPays().getValue();
                    mLayoutEntrustAmount.setVisibility(View.VISIBLE);
                    mTvEntrustContent.setText(formatHtml(CaclUtil.formatAmount(paysValue, SCALE), paysCur, CaclUtil.formatAmount(getsValue, SCALE), getsCur));

                    // 委托价格
                    String price;
                    String token;
                    if (TextUtils.equals(KEY_CNY, getsCur)) {
                        price = amountRatio(getsValue, paysValue);
                        token = WConstant.CURRENCY_CNT;
                    } else if (TextUtils.equals(KEY_CNY, paysCur)) {
                        price = amountRatio(paysValue, getsValue);
                        token = WConstant.CURRENCY_CNT;
                    } else if (TextUtils.equals(KEY_SWT, getsCur)) {
                        price = amountRatio(getsValue, paysValue);
                        token = WConstant.CURRENCY_SWTC;
                    } else {
                        price = amountRatio(paysValue, getsValue);
                        token = paysCur.split("_")[0];
                    }
                    if (!TextUtils.equals("0", price)) {
                        mLayoutValue.setVisibility(View.VISIBLE);
                        mTvValue.setText(CaclUtil.formatAmount(price, 6));
                        mTvValueToken.setText(token);
                    }
                }
                break;
            default:
                mTvType.setText(getString(R.string.tv_unkown));
                mFrom = currentAddr;
                mTvFrom.setText(mFrom);
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
        mTvGas.setText(CaclUtil.formatAmount(mTransactions.getFee(), 10));
        mTvHash.setText(mTransactions.getHash());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(mTransactions.getDate().longValue() * 1000);
        String time = formatter.format(date);
        mTvTime.setText(time);

        if ("tesSUCCESS".equals(mTransactions.getResult())) {
            mTvResult.setText(getResources().getString(R.string.tv_transfer_success));
            mTvResult.setTextColor(getResources().getColor(R.color.color_detail_receive));
        } else {
            mTvResult.setText(getResources().getString(R.string.tv_transfer_failed));
            mTvResult.setTextColor(getResources().getColor(R.color.color_detail_send));
        }

        if (TextUtils.equals(currentAddr, mTo)) {
            mTvTo.setTextColor(getResources().getColor(R.color.color_ping_normal));
        } else if (TextUtils.equals(currentAddr, mFrom)) {
            mTvFrom.setTextColor(getResources().getColor(R.color.color_ping_normal));
        }
        ViewUtil.EllipsisTextView(mTvFrom);
        ViewUtil.EllipsisTextView(mTvTo);
    }

    public static void startTransactionDetailActivity(Context context, Transactions data) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        intent.putExtra("data", data);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String amountRatio(String pays, String gets) {
        if (!TextUtils.isEmpty(pays) && !TextUtils.isEmpty(gets) && !TextUtils.equals("0", pays) && !TextUtils.equals("0", gets)) {
            BigDecimal bi1 = new BigDecimal(pays);
            BigDecimal bi2 = new BigDecimal(gets);
            BigDecimal bi3 = bi1.divide(bi2, 6, BigDecimal.ROUND_HALF_UP);
            return bi3.stripTrailingZeros().toPlainString();
        } else {
            return "0";
        }
    }

    private Spanned formatHtml(String paysValue, String paysCur, String getsValue, String getsCur) {
        String[] paysArr = paysCur.split("_");
        String paysToken = "";
        String paysIssue = "";
        if (paysArr.length == 2) {
            paysToken = paysArr[0];
            paysIssue = paysArr[1];
        } else if (paysArr.length == 1) {
            paysToken = paysArr[0];
        }

        if (TextUtils.equals(WConstant.CURRENCY_SWT, paysToken)) {
            paysCur = WConstant.CURRENCY_SWTC;
        } else if (TextUtils.equals(WConstant.CURRENCY_CNY, paysToken) && TextUtils.equals(WConstant.CURRENCY_ISSUE, paysIssue)) {
            paysCur = WConstant.CURRENCY_CNT;
        } else {
            paysCur = paysToken;
        }

        String[] getsArr = getsCur.split("_");
        String getsToken = "";
        String getsIssue = "";
        if (getsArr.length == 2) {
            getsToken = getsArr[0];
            getsIssue = getsArr[1];
        } else if (getsArr.length == 1) {
            getsToken = getsArr[0];
        }
        if (TextUtils.equals(WConstant.CURRENCY_SWT, getsToken)) {
            getsCur = WConstant.CURRENCY_SWTC;
        } else if (TextUtils.equals(WConstant.CURRENCY_CNY, getsToken) && TextUtils.equals(WConstant.CURRENCY_ISSUE, getsIssue)) {
            getsCur = WConstant.CURRENCY_CNT;
        } else {
            getsCur = getsToken;
        }

        String paysH = "<font color=\"#3B6CA6\">" + paysValue + " </font>";
        String paysCurH = "<font color=\"#021E38\">" + paysCur + " </font>";
        String right = "<font color=\"#A6A9AD\">" + "\u2192" + " </font>";
        String getsH = "<font color=\"#3B6CA6\">" + getsValue + " </font>";
        String getsCurH = "<font color=\"#021E38\">" + getsCur + "</font>";
        return Html.fromHtml(paysH.concat(paysCurH).concat(right).concat(getsH).concat(getsCurH));
    }
}
