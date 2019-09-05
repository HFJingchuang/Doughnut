package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.jtblk.client.bean.AccountTx;
import com.android.jtblk.client.bean.Marker;
import com.android.jtblk.client.bean.Transactions;
import com.doughnut.R;
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
    private TransactionRecordAdapter mAdapter;
    private Marker marker;
    private List<Transactions> transactions;

    private LinearLayout mLayoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentWallet = WalletSp.getInstance(this, "").getCurrentWallet();
        mTitleBar.setTitle(WalletSp.getInstance(this, currentWallet).getName());
        if (mAdapter != null) {
            mSmartRefreshLayout.autoRefresh();
        }
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
        mTitleBar.setRightDrawable(R.drawable.ic_changewallet);
        mTitleBar.setTitle(getString(R.string.tv_transferdetail));
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
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
    }

    public static void startTransactionRecordActivity(Context context) {
        Intent intent = new Intent(context, TransactionRecordActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecyclerView.getChildAdapterPosition(
                    mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
            if (lastVisiblePosition >= mRecyclerView.getAdapter().getItemCount() - 1) {
                return mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1)
                        .getBottom() <= mRecyclerView.getBottom();
            }
        } catch (Throwable e) {
        }

        return false;
    }

    class TransactionRecordAdapter extends RecyclerView.Adapter<TransactionRecordAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            LinearLayout mLayoutItem;
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
                mTvTransactionTime = itemView.findViewById(R.id.tv_transaction_time);
                mTvTransactionCount = itemView.findViewById(R.id.tv_transaction_count);
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
                    holder.mTvTransactionCount.setText("-" + tr.getAmount().getValue() + " " + tr.getAmount().getCurrency());
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.color_detail_send));
                    break;
                case "received":
                    holder.mImgIcon.setImageResource(R.drawable.ic_transfer_receive);
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());
                    holder.mTvTransactionCount.setText("+" + tr.getAmount().getValue() + " " + tr.getAmount().getCurrency());
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.color_detail_receive));
                    break;
                case "offernew":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_new);
                    holder.mTvTransactionAddress.setText("挂单创建");
                    String getsCur = tr.getGets().getCurrency();
                    String paysCur = tr.getPays().getCurrency();
                    BigDecimal getsAmount = new BigDecimal("0");
                    BigDecimal paysAmount = new BigDecimal("0");
                    if (tr.getEffects() != null) {
                        for (int i = 0; i < tr.getEffects().size(); i++) {
                            pays = tr.getEffects().getJSONObject(i).getJSONObject("paid");
                            gets = tr.getEffects().getJSONObject(i).getJSONObject("got");
                            if (pays != null && gets != null) {
                                String currency = pays.getString("currency");
                                if (TextUtils.equals(currency, getsCur)) {
                                    paysAmount = paysAmount.add(new BigDecimal(pays.getString("value")));
                                }
                                currency = gets.getString("currency");
                                if (TextUtils.equals(currency, paysCur)) {
                                    getsAmount = getsAmount.add(new BigDecimal(gets.getString("value")));
                                }
                            }
                        }
                        if (getsAmount.equals(new BigDecimal("0")) || paysAmount.equals(new BigDecimal("0"))) {
                            holder.mTvTransactionCount.setText(tr.getGets().getValue() + " " + getsCur + " -> " + tr.getPays().getValue() + " " + paysCur);
                        } else {
                            holder.mTvTransactionCount.setText(paysAmount.stripTrailingZeros().toPlainString() + " " + getsCur + " -> " + getsAmount.stripTrailingZeros().toPlainString() + " " + paysCur);
                        }
                    }
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_green));
                    break;
                case "offercancel":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_cancel);
                    holder.mTvTransactionAddress.setText("挂单取消");
                    if (tr.getGets() != null && tr.getPays() != null) {
                        holder.mTvTransactionCount.setText(tr.getGets().getValue() + " " + tr.getGets().getCurrency() + " -> " + tr.getPays().getValue() + " " + tr.getPays().getCurrency());
                    } else {
                        holder.mTvTransactionCount.setText("---");
                    }
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_green));
                    break;
                case "offereffect":
                    holder.mImgIcon.setImageResource(R.drawable.ic_offer_effect);
                    BigDecimal getsAmount1 = new BigDecimal("0");
                    BigDecimal paysAmount1 = new BigDecimal("0");
                    if (tr.getEffects() != null) {
                        for (int i = 0; i < tr.getEffects().size(); i++) {
                            pays = tr.getEffects().getJSONObject(i).getJSONObject("paid");
                            gets = tr.getEffects().getJSONObject(i).getJSONObject("got");
                            if (pays != null && gets != null) {
                                paysAmount1 = paysAmount1.add(new BigDecimal(pays.getString("value")));
                                getsAmount1 = getsAmount1.add(new BigDecimal(gets.getString("value")));
                            }
                        }
                        String payCurrency = tr.getEffects().getJSONObject(0).getJSONObject("paid").getString("currency");
                        String getCurrency = tr.getEffects().getJSONObject(0).getJSONObject("got").getString("currency");
                        holder.mTvTransactionCount.setText(paysAmount1.stripTrailingZeros().toPlainString() + " " + payCurrency + " -> " + getsAmount1.stripTrailingZeros().toPlainString() + " " + getCurrency);
                    }
                    holder.mTvTransactionAddress.setText(tr.getEffects().getJSONObject(0).getJSONObject("counterparty").getString("account"));
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_green));
                    break;
                default:
                    // TODO parse other type
                    break;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(tr.getDate().longValue() * 1000);
            String sim = formatter.format(date);
            holder.mTvTransactionTime.setText(sim);
            ViewUtil.EllipsisTextView(holder.mTvTransactionAddress);
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

}
