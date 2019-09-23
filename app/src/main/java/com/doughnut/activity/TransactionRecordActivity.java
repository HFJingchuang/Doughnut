package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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

import com.alibaba.fastjson.JSONObject;
import com.android.jtblk.client.Wallet;
import com.android.jtblk.client.bean.AccountTx;
import com.android.jtblk.client.bean.Marker;
import com.android.jtblk.client.bean.Transactions;
import com.doughnut.R;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.jessyan.autosize.internal.CustomAdapt;


public class TransactionRecordActivity extends BaseActivity implements
        TitleBar.TitleBarClickListener, CustomAdapt {

    private static final int SCALE = 2;
    private static final int LIMIT = 10;

    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;

    private RecyclerView mRecyclerView;
    private LinearLayout mLayoutEmpty;

    private TransactionRecordAdapter mAdapter;
    private Marker marker;
    private List<Transactions> transactions;
    private String currentAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onLeftClick(View view) {
        this.finish();
    }

    @Override
    public void onRightClick(View view) {
    }

    @Override
    public void onMiddleClick(View view) {
    }

    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setLeftTextColor(R.color.white);
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setTitle(getString(R.string.tv_transferdetail));
        mTitleBar.setTitleBarClickListener(this);

        mLayoutEmpty = findViewById(R.id.layout_no_transfer);

        mAdapter = new TransactionRecordAdapter();
        mRecyclerView = findViewById(R.id.view_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.layout_refresh);
        mSmartRefreshLayout.autoRefresh();
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getHistory();
            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (marker == null) {
                    refreshlayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                getHistoryMore();
            }
        });
        currentAddr = WalletSp.getInstance(this, "").getCurrentWallet();
    }

    public static void startTransactionRecordActivity(Context context) {
        Intent intent = new Intent(context, TransactionRecordActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 730;
    }

    class TransactionRecordAdapter extends RecyclerView.Adapter<TransactionRecordAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            RelativeLayout mLayoutItem;
            ImageView mImgIcon;
            TextView mTvTransactionAddress;
            TextView mTvTransactionTime;
            TextView mTvTransactionCount;

            public VH(View v) {
                super(v);
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Transactions tr = transactions.get(getAdapterPosition());
                        TransactionDetailsActivity.startTransactionDetailActivity(TransactionRecordActivity.this, tr);
                    }
                });
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTransactionAddress = itemView.findViewById(R.id.tv_transaction_address);
                mTvTransactionCount = itemView.findViewById(R.id.tv_transaction_count);
                mTvTransactionTime = itemView.findViewById(R.id.tv_transaction_time);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_transaction, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (transactions == null) {
                return;
            }
            Transactions tr = transactions.get(position);
            JSONObject gets;
            JSONObject pays;
            switch (tr.getType()) {
                case "sent":
                    holder.mImgIcon.setImageResource(R.drawable.ic_transfer_send);
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());

                    String paysH = "<font color=\"#F55758\">" + "-" + CaclUtil.formatAmount(tr.getAmount().getValue(), SCALE) + " </font>";
                    String tokenName = tr.getAmount().getCurrency();
                    if (TextUtils.equals(WConstant.CURRENCY_SWT, tokenName)) {
                        tokenName = WConstant.CURRENCY_SWTC;
                    }
                    String paysCurH = "<font color=\"#021E38\">" + tokenName + "</font>";
                    holder.mTvTransactionCount.setText(Html.fromHtml(paysH.concat(paysCurH)));
                    break;
                case "received":
                    holder.mImgIcon.setImageResource(R.drawable.ic_transfer_receive);
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());

                    String paysH1 = "<font color=\"#27B498\">" + "+" + CaclUtil.formatAmount(tr.getAmount().getValue(), SCALE) + " </font>";
                    String tokenName1 = tr.getAmount().getCurrency();
                    if (TextUtils.equals(WConstant.CURRENCY_SWT, tokenName1)) {
                        tokenName1 = WConstant.CURRENCY_SWTC;
                    }
                    String paysCurH1 = "<font color=\"#021E38\">" + tokenName1 + "</font>";
                    holder.mTvTransactionCount.setText(Html.fromHtml(paysH1.concat(paysCurH1)));
                    break;
                case "offernew":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_new);
                    holder.mTvTransactionAddress.setText(getResources().getString(R.string.tv_offernew));

                    String getsCur = tr.getGets().getCurrency();
                    String paysCur = tr.getPays().getCurrency();
                    String getsAmount = "0";
                    String paysAmount = "0";
                    if (tr.getEffects() != null) {
                        for (int i = 0; i < tr.getEffects().size(); i++) {
                            JSONObject effect = tr.getEffects().getJSONObject(i);
                            pays = effect.getJSONObject("paid");
                            gets = effect.getJSONObject("got");
                            if (pays != null && gets != null) {
                                String currency = pays.getString("currency");
                                if (TextUtils.equals(currency, paysCur)) {
                                    paysAmount = CaclUtil.add(paysAmount, pays.getString("value"));
                                }
                                currency = gets.getString("currency");
                                if (TextUtils.equals(currency, getsCur)) {
                                    getsAmount = CaclUtil.add(getsAmount, gets.getString("value"));
                                }
                            }
                        }
                        if (TextUtils.isEmpty(getsAmount) || CaclUtil.compare(getsAmount, "0") == 0 ||
                                TextUtils.isEmpty(paysAmount) || CaclUtil.compare(paysAmount, "0") == 0) {
                            holder.mTvTransactionCount.setText(formatHtml(CaclUtil.formatAmount(tr.getPays().getValue(), SCALE), paysCur, CaclUtil.formatAmount(tr.getGets().getValue(), SCALE), getsCur));
                        } else {
                            String entrustAmount = CaclUtil.formatAmount(getsAmount, SCALE);
                            String payAmount = CaclUtil.formatAmount(paysAmount, SCALE);
                            holder.mTvTransactionCount.setText(formatHtml(payAmount, paysCur, entrustAmount, getsCur));

                        }
                    }
                    break;
                case "offercancel":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_cancel);
                    holder.mTvTransactionAddress.setText(getResources().getString(R.string.tv_offercancel));
                    if (tr.getGets() != null && tr.getPays() != null) {
                        String entrustAmount = CaclUtil.formatAmount(tr.getGets().getValue(), SCALE);
                        String entrustToken = tr.getGets().getCurrency();
                        String payAmount = CaclUtil.formatAmount(tr.getPays().getValue(), SCALE);
                        String payToken = tr.getPays().getCurrency();
                        holder.mTvTransactionCount.setText(formatHtml(payAmount, payToken, entrustAmount, entrustToken));
                    } else {
                        holder.mTvTransactionCount.setText("---");
                    }
                    break;
                case "offereffect":
                    holder.mTvTransactionAddress.setText(getResources().getString(R.string.tv_offereffect));
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_effect);
                    if (tr.getEffects() != null) {
                        String getsAmount1 = "0";
                        String getsCur1 = "";
                        String paysAmount1 = "0";
                        String paysCur1 = "";
                        for (int i = 0; i < tr.getEffects().size(); i++) {
                            JSONObject effect = tr.getEffects().getJSONObject(i);
                            JSONObject counterparty = effect.getJSONObject("counterparty");
                            if (counterparty != null) {
                                String addr = effect.getJSONObject("counterparty").getString("account");
                                if (TextUtils.equals(addr, currentAddr)) {
                                    pays = effect.getJSONObject("paid");
                                    gets = effect.getJSONObject("got");
                                    if (pays != null && gets != null) {
                                        if (TextUtils.isEmpty(getsCur1) && TextUtils.isEmpty(paysCur1)) {
                                            getsAmount1 = CaclUtil.add(getsAmount1, gets.getString("value"));
                                            getsCur1 = gets.getString("currency");
                                            paysAmount1 = CaclUtil.add(paysAmount1, pays.getString("value"));
                                            paysCur1 = pays.getString("currency");
                                        } else {
                                            String currency = pays.getString("currency");
                                            if (TextUtils.equals(currency, paysCur1)) {
                                                paysAmount1 = CaclUtil.add(paysAmount1, pays.getString("value"));
                                            }
                                            currency = gets.getString("currency");
                                            if (TextUtils.equals(currency, getsCur1)) {
                                                getsAmount1 = CaclUtil.add(getsAmount1, gets.getString("value"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(getsAmount1) && CaclUtil.compare(getsAmount1, "0") != 0 && !TextUtils.isEmpty(paysAmount1) && CaclUtil.compare(paysAmount1, "0") != 0) {
                            holder.mTvTransactionCount.setText(formatHtml(CaclUtil.formatAmount(getsAmount1, SCALE), getsCur1, CaclUtil.formatAmount(paysAmount1, SCALE), paysCur1));
                        }
                    }
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());
                    break;
                default:
                    holder.mTvTransactionAddress.setText(getString(R.string.tv_unkown));
                    holder.mTvTransactionCount.setText("---");
                    break;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
            Date date = new Date(tr.getDate().longValue() * 1000);
            String sim = formatter.format(date);
            holder.mTvTransactionTime.setText(sim);
            String address = holder.mTvTransactionAddress.getText().toString();
            holder.mTvTransactionAddress.setText(EllipsizeAddress(address));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            if (transactions != null) {
                return transactions.size();
            }
            return 0;
        }
    }

    private void getHistory() {
        if (transactions != null) {
            transactions.clear();
        }

        WalletManager.getInstance(this).getTransferHistory(WalletSp.getInstance(this, "").getCurrentWallet(), LIMIT, null, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    AccountTx accountTx = (AccountTx) response;
                    if (accountTx != null) {
                        transactions = accountTx.getTransactions();
                        marker = accountTx.getMarker();
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (transactions == null || transactions.isEmpty()) {
                    if (!mLayoutEmpty.isShown()) {
                        mLayoutEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mLayoutEmpty.isShown()) {
                        mLayoutEmpty.setVisibility(View.GONE);
                    }
                }
                mSmartRefreshLayout.finishRefresh();
            }
        });

    }

    private void getHistoryMore() {
        WalletManager.getInstance(this).getTransferHistory(WalletSp.getInstance(this, "").getCurrentWallet(), LIMIT, marker, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    AccountTx accountTx = (AccountTx) response;
                    if (accountTx != null) {
                        List<Transactions> transaction = accountTx.getTransactions();
                        if (transactions != null) {
                            transactions.addAll(transaction);
                            marker = accountTx.getMarker();
                            if (mAdapter != null) {
                                mAdapter.notifyDataSetChanged();
                                mSmartRefreshLayout.finishLoadMore();
                                return;
                            }
                        }
                    }
                }
                marker = null;
                mSmartRefreshLayout.finishLoadMore();
            }
        });
    }

    private Spanned formatHtml(String paysValue, String paysCur, String getsValue, String getsCur) {
        if (TextUtils.equals(WConstant.CURRENCY_SWT, paysCur)) {
            paysCur = WConstant.CURRENCY_SWTC;
        }
        if (TextUtils.equals(WConstant.CURRENCY_SWT, getsCur)) {
            getsCur = WConstant.CURRENCY_SWTC;
        }
        String paysH = "<font color=\"#3B6CA6\">" + paysValue + " </font>";
        String paysCurH = "<font color=\"#021E38\">" + paysCur + " </font>";
        String right = "<font color=\"#A6A9AD\">" + "\u2192" + " </font>";
        String getsH = "<font color=\"#3B6CA6\">" + getsValue + " </font>";
        String getsCurH = "<font color=\"#021E38\">" + getsCur + "</font>";
        return Html.fromHtml(paysH.concat(paysCurH).concat(right).concat(getsH).concat(getsCurH));
    }

    private String EllipsizeAddress(String address) {
        if (Wallet.isValidAddress(address)) {
            String startStr = address.substring(0, 4);
            String endStr = address.substring(address.length() - 4);
            return startStr + "***" + endStr;
        }
        return address;
    }
}
