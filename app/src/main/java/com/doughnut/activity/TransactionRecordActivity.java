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
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TransactionRecordActivity extends BaseActivity implements
        TitleBar.TitleBarClickListener {

    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;

    private RecyclerView mRecyclerView;
    private LinearLayout mLayoutEmpty;

    private TransactionRecordAdapter mAdapter;
    private Marker marker;
    private List<Transactions> transactions;
    private String currentAddr;
    private final int SCALE = 2;

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
        ChangeWalletActivity.startChangeWalletActivity(this);
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
                if (transactions == null || transactions.isEmpty()) {
                    mLayoutEmpty.setVisibility(View.VISIBLE);
                } else {
                    mLayoutEmpty.setVisibility(View.GONE);
                }
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

                    String paysH = "<font color=\"#F55758\">" + "-" + Util.formatAmount(tr.getAmount().getValue(), SCALE) + " </font>";
                    String paysCurH = "<font color=\"#021E38\">" + tr.getAmount().getCurrency() + "</font>";
                    holder.mTvTransactionCount.setText(Html.fromHtml(paysH.concat(paysCurH)));
                    break;
                case "received":
                    holder.mImgIcon.setImageResource(R.drawable.ic_transfer_receive);
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());

                    String paysH1 = "<font color=\"#27B498\">" + "+" + Util.formatAmount(tr.getAmount().getValue(), SCALE) + " </font>";
                    String paysCurH1 = "<font color=\"#021E38\">" + tr.getAmount().getCurrency() + "</font>";
                    holder.mTvTransactionCount.setText(Html.fromHtml(paysH1.concat(paysCurH1)));
                    break;
                case "offernew":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_new);
                    holder.mTvTransactionAddress.setText(getResources().getString(R.string.tv_offernew));

                    String getsCur = tr.getGets().getCurrency();
                    String paysCur = tr.getPays().getCurrency();
                    BigDecimal getsAmount = new BigDecimal("0");
                    BigDecimal paysAmount = new BigDecimal("0");
                    if (tr.getEffects() != null) {
                        for (int i = 0; i < tr.getEffects().size(); i++) {
                            JSONObject effect = tr.getEffects().getJSONObject(i);
                            pays = effect.getJSONObject("paid");
                            gets = effect.getJSONObject("got");
                            if (pays != null && gets != null) {
                                String currency = pays.getString("currency");
                                if (TextUtils.equals(currency, paysCur)) {
                                    paysAmount = paysAmount.add(new BigDecimal(pays.getString("value")));
                                }
                                currency = gets.getString("currency");
                                if (TextUtils.equals(currency, getsCur)) {
                                    getsAmount = getsAmount.add(new BigDecimal(gets.getString("value")));
                                }
                            }
                        }
                        if (getsAmount.equals(new BigDecimal("0")) || paysAmount.equals(new BigDecimal("0"))) {
                            holder.mTvTransactionCount.setText(formatHtml(Util.formatAmount(tr.getPays().getValue(), SCALE), paysCur, Util.formatAmount(tr.getGets().getValue(), SCALE), getsCur));
                        } else {
                            String entrustAmount = Util.formatAmount(getsAmount.stripTrailingZeros().toPlainString(), SCALE);
                            String payAmount = Util.formatAmount(paysAmount.stripTrailingZeros().toPlainString(), SCALE);
                            holder.mTvTransactionCount.setText(formatHtml(payAmount, paysCur, entrustAmount, getsCur));

                        }
                    }
                    break;
                case "offercancel":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_cancel);
                    holder.mTvTransactionAddress.setText(getResources().getString(R.string.tv_offercancel));
                    if (tr.getGets() != null && tr.getPays() != null) {
                        String entrustAmount = Util.formatAmount(tr.getGets().getValue(), SCALE);
                        String entrustToken = tr.getGets().getCurrency();
                        String payAmount = Util.formatAmount(tr.getPays().getValue(), SCALE);
                        String payToken = tr.getPays().getCurrency();
                        holder.mTvTransactionCount.setText(formatHtml(payAmount, payToken, entrustAmount, entrustToken));
                    } else {
                        holder.mTvTransactionCount.setText("---");
                    }
                    break;
                case "offereffect":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_cancel);
                    holder.mTvTransactionAddress.setText(getResources().getString(R.string.tv_offercancel));

                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_effect);
                    if (tr.getEffects() != null) {
                        for (int i = 0; i < tr.getEffects().size(); i++) {
                            JSONObject effect = tr.getEffects().getJSONObject(i);
                            String addr = effect.getJSONObject("counterparty").getString("account");
                            if (TextUtils.equals(addr, currentAddr)) {
                                pays = effect.getJSONObject("paid");
                                gets = effect.getJSONObject("got");
                                if (pays != null && gets != null) {
                                    holder.mTvTransactionCount.setText(formatHtml(Util.formatAmount(gets.getString("value"), SCALE), gets.getString("currency"), Util.formatAmount(pays.getString("value"), SCALE), pays.getString("currency")));
                                }
                            }
                        }
                    }
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());
                    break;
                default:
                    // TODO parse other type
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

        AccountTx accountTx = WalletManager.getInstance(TransactionRecordActivity.this).getTansferHishory(WalletSp.getInstance(this, "").getCurrentWallet(), 10, null);
        if (accountTx != null) {
            transactions = accountTx.getTransactions();
            marker = accountTx.getMarker();
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mSmartRefreshLayout.finishRefresh();
    }

    private void getHistoryMore() {
        AccountTx accountTx = WalletManager.getInstance(TransactionRecordActivity.this).getTansferHishory(WalletSp.getInstance(this, "").getCurrentWallet(), 10, marker);
        if (accountTx != null) {
            transactions.addAll(accountTx.getTransactions());
            marker = accountTx.getMarker();
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mSmartRefreshLayout.finishLoadMore();
    }

    private Spanned formatHtml(String paysValue, String paysCur, String getsValue, String getsCur) {
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
