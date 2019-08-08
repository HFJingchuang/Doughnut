package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.jtblk.client.bean.AccountTx;
import com.android.jtblk.client.bean.Marker;
import com.android.jtblk.client.bean.Transactions;
import com.doughnut.R;
import com.doughnut.adapter.BaseRecycleAdapter;
import com.doughnut.adapter.BaseRecyclerViewHolder;
import com.doughnut.base.BaseWalletUtil;
import com.doughnut.base.TBController;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.TLog;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.JtServer;
import com.doughnut.wallet.WalletManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TransactionRecordActivity extends BaseActivity implements
        TitleBar.TitleBarClickListener {

    private final static String TAG = "TransactionRecordActivity";

    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;

    private RecyclerView mRecyclerView;
    private TransactionRecordAdapter mAdapter;
    private BaseWalletUtil mWalletUtil;
    private Marker marker;
    private List<Transactions> transactions;

    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int blockId = WalletInfoManager.getInstance().getWalletType();
        mWalletUtil = TBController.getInstance().getWalletUtil(blockId);
        if (mWalletUtil == null) {
            this.finish();
            return;
        }
        if (mAdapter != null) {
            mSmartRefreshLayout.setEnableRefresh(true);
        }
        mTitleBar.setTitle(WalletInfoManager.getInstance().getWname());


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

//        mSmartRefreshLayout = findViewById(R.id.root_view);
//        mSmartRefreshLayout.setOnRefreshListener(this);

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setLeftTextColor(R.color.white);
        mTitleBar.setTitleTextColor(R.color.white);
        mTitleBar.setRightDrawable(R.drawable.ic_changewallet);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitleBarClickListener(this);

//        mEmptyView = findViewById(R.id.empty_view);
//        mEmptyView.setVisibility(View.GONE);

        mAdapter = new TransactionRecordAdapter();
        mRecyclerView = findViewById(R.id.view_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.layout_refresh);
        mSmartRefreshLayout.autoRefresh();
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mAdapter.notifyDataSetChanged();
                refreshlayout.finishRefresh();

            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
            }
        });

        JtServer.getInstance().changeServer("wss://s.jingtum.com:5020");
        AccountTx accountTx = WalletManager.getInstance(TransactionRecordActivity.this).getTansferHishory("jBvrdYc6G437hipoCiEpTwrWSRBS2ahXN6", 10, null);
        transactions = accountTx.getTransactions();
        marker = accountTx.getMarker();
        mSmartRefreshLayout.finishRefresh();
    }

    public static void startTransactionRecordActivity(Context context, int from) {
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
            ImageView mImgIcon;
            TextView mTvTransactionAddress;
            TextView mTvTransactionTime;
            TextView mTvTransactionCount;

            public VH(View v) {
                super(v);
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
                    holder.mImgIcon.setImageResource(R.drawable.ic_send);
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());
                    holder.mTvTransactionCount.setText("-" + tr.getAmount().getValue() + " " + tr.getAmount().getCurrency());
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_red));
                    break;
                case "received":
                    holder.mImgIcon.setImageResource(R.drawable.ic_reciver);
                    holder.mTvTransactionAddress.setText(tr.getCounterparty());
                    holder.mTvTransactionCount.setText("+" + tr.getAmount().getValue() + " " + tr.getAmount().getCurrency());
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_blue));
                    break;
                case "trusted":
                    break;
                case "trusting":
                    break;
                case "convert":
                    break;
                case "offernew":
                    //Transaction.flags.OfferCreate.Sell 是一个常量
                    holder.mImgIcon.setImageResource(R.drawable.ic_transaction_out);
                    pays = tr.getEffects().getJSONObject(0).getJSONObject("paid");
                    gets = tr.getEffects().getJSONObject(tr.getEffects().size() - 1).getJSONObject("got");
                    if (pays != null && gets != null) {
                        holder.mTvTransactionCount.setText(pays.getString("value") + pays.getString("currency") + " -> " + gets.getString("value") + gets.getString("currency"));
                    } else {
                        holder.mTvTransactionCount.setText(tr.getGets().getValue() + tr.getGets().getCurrency() + " -> " + tr.getPays().getValue() + tr.getPays().getCurrency());
                    }
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_green));
                    break;
                case "offercancel":
                    holder.mImgIcon.setImageResource(R.drawable.ic_transfer);
                    holder.mTvTransactionCount.setText(tr.getGets().getValue() + tr.getGets().getCurrency() + " -> " + tr.getPays().getValue() + tr.getPays().getCurrency());
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_green));
                    break;
                case "offereffect":
                    holder.mImgIcon.setImageResource(R.drawable.ic_token_normal);
                    holder.mTvTransactionAddress.setText(tr.getEffects().getJSONObject(0).getJSONObject("counterparty").getString("account"));
                    pays = tr.getEffects().getJSONObject(0).getJSONObject("paid");
                    gets = tr.getEffects().getJSONObject(0).getJSONObject("got");
                    holder.mTvTransactionCount.setText(pays.getString("value") + pays.getString("currency") + " -> " + gets.getString("value") + gets.getString("currency"));
                    holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_green));
                    break;
                case "relationset":
                    break;
                case "relationdel":
                    break;
                case "configcontract":
                    break;
                default:
                    // TODO parse other type
                    break;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(tr.getDate().longValue() * 1000);
            String sim = formatter.format(date);
            holder.mTvTransactionTime.setText(sim);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }
    }

}
