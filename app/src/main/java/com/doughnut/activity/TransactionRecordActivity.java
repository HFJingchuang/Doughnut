package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.adapter.BaseRecycleAdapter;
import com.doughnut.adapter.BaseRecyclerViewHolder;
import com.doughnut.base.BaseWalletUtil;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.base.WCallback;
import com.doughnut.base.TBController;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.TLog;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;


public class TransactionRecordActivity extends BaseActivity implements BaseRecycleAdapter.OnDataLodingFinish,
        TitleBar.TitleBarClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "TransactionRecordActivity";

    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;

    private RecyclerView mRecyclerView;
    private TransactionRecordAdapter mAdapter;
    private BaseWalletUtil mWalletUtil;

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
            mAdapter.refresh();
            mSmartRefreshLayout.setEnableRefresh(true);
        }
        mTitleBar.setTitle(WalletInfoManager.getInstance().getWname());


    }

    @Override
    public <K> void onDataLoadingFinish(K params, boolean end, boolean loadmore) {
        if (!loadmore) {
            if (end) {
                if (mAdapter.getLength() <= 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
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

//        mSmartRefreshLayout = findViewById(R.id.root_view);
//        mSmartRefreshLayout.setOnRefreshListener(this);

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setLeftTextColor(R.color.white);
        mTitleBar.setTitleTextColor(R.color.white);
        mTitleBar.setRightDrawable(R.drawable.ic_changewallet);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitleBarClickListener(this);

        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);

//        mRecyclerView = findViewById(R.id.recyclerview_transaction_record);
        mAdapter = new TransactionRecordAdapter();
        mAdapter.setDataLoadingListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(TransactionRecordActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isReadyForPullEnd()) {
                    //最后一个可见
                    mAdapter.loadmore(null);
                }
            }
        });
    }

    public static void startTransactionRecordActivity(Context context, int from) {
        Intent intent = new Intent(context, TransactionRecordActivity.class);
        intent.putExtra("From", from);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mAdapter.refresh();
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

    class TransactionRecordAdapter extends BaseRecycleAdapter<String, TransactionRecordAdapter.TransactionRecordViewHolder> {

        private boolean mHasMore = true;
        private int mPageIndex = 0;
        private final static int PAGE_SIZE = 10;
        private String mMarker; //jt 需要

        private BaseRecyclerViewHolder.ItemClickListener mItemClickListener = new BaseRecyclerViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GsonUtil item = getItem(position);

                gotoTransactionDetail(item);
            }
        };

        @Override
        public void loadData(final String params, final boolean loadmore) {
            if (!loadmore) {
                mPageIndex = 0;
                mMarker = "";
            } else {
                mPageIndex++;
            }
            if (loadmore && !mHasMore) {
                return;
            }

            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, false, loadmore);
            }

            GsonUtil requestParams = new GsonUtil("{}");
            requestParams.putInt("start", mPageIndex);
            requestParams.putInt("pagesize", PAGE_SIZE);
            requestParams.putString("marker", mMarker);

            mWalletUtil.queryTransactionList(requestParams, new WCallback() {
                @Override
                public void onGetWResult(int ret, GsonUtil extra) {
                    if (ret == 0) {
                        handleTransactioRecordResult(params, loadmore, extra);
                        mMarker = extra.getString("marker", "");
                    }
                    mSmartRefreshLayout.setEnableRefresh(false);
                }
            });
        }

        @Override
        public TransactionRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TransactionRecordViewHolder holder = new TransactionRecordViewHolder(ViewUtil.inflatView(parent.getContext(),
                    parent, R.layout.layout_item_transaction, false), mItemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(TransactionRecordViewHolder transactionRecordViewHolder, int position) {
            TLog.e(TAG, "itemcount:" + getItemCount());
            fillData(transactionRecordViewHolder, getItem(position));
        }

        private void handleTransactioRecordResult(final String params, final boolean loadmore, GsonUtil json) {
            TLog.d(TAG, "transaction list:" + json);
            GsonUtil transactionRecord = json.getArray("data", "[]");
            if (!loadmore) {
                //第一页
                setData(transactionRecord);
            } else {
                if (transactionRecord.getLength() > 0) {
                    addData(transactionRecord);
                }
            }

            if (transactionRecord.getLength() < PAGE_SIZE) {
                //最后一页了
                mHasMore = false;
            } else {
                mHasMore = true;
            }

            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, true, loadmore);
            }
        }

        private void fillData(final TransactionRecordViewHolder holder, final GsonUtil item) {
            if (item == null || TextUtils.equals(item.toString(), "{}")) {
                return;
            }

            double value = item.getDouble("real_value", 0.0f);
            String toAddress = item.getString("to", "");

            String fromAddress = item.getString("from", "");
            String currentAddress = WalletInfoManager.getInstance().getWAddress().toLowerCase();
            boolean in = false;
            holder.mTvTransactionTime.setText(Util.formatTime(item.getLong("timeStamp", 0l)));
            String label = "";
            if (TextUtils.equals(currentAddress, fromAddress)) {
                label = "-";
                in = false;
            }
            if (TextUtils.equals(currentAddress, toAddress)) {
                label = "+";
                in = true;
            }
            if (in) {
                holder.mImgIcon.setImageResource(R.drawable.ic_transaction_in);
                holder.mTvTransactionAddress.setText(fromAddress);
                holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_blue));
            } else {
                holder.mTvTransactionAddress.setText(toAddress);
                holder.mImgIcon.setImageResource(R.drawable.ic_transaction_out);
                holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_red));
            }

            holder.mTvTransactionCount.setText(label + value + item.getString("tokenSymbol", ""));
        }

        private void gotoTransactionDetail(GsonUtil json) {
            TransactionDetailsActivity.startTransactionDetailActivity(TransactionRecordActivity.this, json);
        }

        class TransactionRecordViewHolder extends BaseRecyclerViewHolder {
            ImageView mImgIcon;
            TextView mTvTransactionAddress;
            TextView mTvTransactionTime;
            TextView mTvTransactionCount;

            public TransactionRecordViewHolder(View itemView, ItemClickListener itemClickListener) {
                super(itemView, itemClickListener);
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTransactionAddress = itemView.findViewById(R.id.tv_transaction_address);
                mTvTransactionTime = itemView.findViewById(R.id.tv_transaction_time);
                mTvTransactionCount = itemView.findViewById(R.id.tv_transaction_count);
            }
        }
    }

}
