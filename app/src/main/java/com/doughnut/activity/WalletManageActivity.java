package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.utils.MethodCompat;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import java.util.List;


public class WalletManageActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;

    private TextView mTvCreateWallet;
    private TextView mTvImPortWallet;

    private RecyclerView mLsWallet;
    private WalletRecordAdapter mAdapter;

    private List<String> walletList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerwallet);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public static void startModifyWalletActivity(Context context) {
        Intent intent = new Intent(context, WalletManageActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_manage_wallet));
        mTitleBar.setTitleTextColor(R.color.white);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.common_blue));
        mTitleBar.setTitleBarClickListener(this);

        mTvCreateWallet = findViewById(R.id.tv_create_wallet);
        mTvCreateWallet.setOnClickListener(this);
        MethodCompat.setLeftDrawableWithBounds(WalletManageActivity.this, mTvCreateWallet, R.drawable.ic_manager_createwallet,
                ViewUtil.dip2px(WalletManageActivity.this, 6),
                ViewUtil.dip2px(WalletManageActivity.this, 6));
        mTvImPortWallet = findViewById(R.id.tv_import_wallet);
        mTvImPortWallet.setOnClickListener(this);
        MethodCompat.setLeftDrawableWithBounds(WalletManageActivity.this, mTvImPortWallet, R.drawable.ic_manager_importwallet,
                ViewUtil.dip2px(WalletManageActivity.this, 6),
                ViewUtil.dip2px(WalletManageActivity.this, 6));

        mLsWallet = (RecyclerView) findViewById(R.id.ls_manager_wallet);
        mLsWallet.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WalletRecordAdapter();
        mLsWallet.setAdapter(mAdapter);

        getWallets();
    }


    @Override
    public void onClick(View v) {
        if (v == mTvCreateWallet) {
            gotoCreateWallet();
        } else if (v == mTvImPortWallet) {
            gotoImportWallet();
        }

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

    private void gotoCreateWallet() {
        CreateNewWalletActivity.startCreateNewWalletActivity(this);
    }

    private void gotoImportWallet() {
        ImportWalletActivity.startImportWalletActivity(this);
    }

    class WalletRecordAdapter extends RecyclerView.Adapter<WalletRecordAdapter.VH> {

        class VH extends RecyclerView.ViewHolder {
            LinearLayout mLayoutItem;
            TextView mTvBalance;
            TextView mTvBalanceCNY;
            TextView mTvAddress;
            TextView mTvName;

            public VH(View v) {
                super(v);
                mTvBalance = itemView.findViewById(R.id.tv_balance);
                mTvBalanceCNY = itemView.findViewById(R.id.tv_balance_cny);
                mTvAddress = itemView.findViewById(R.id.tv_wallet_address);
                mTvName = itemView.findViewById(R.id.tv_wallet_name);
                mLayoutItem = itemView.findViewById(R.id.layout_wallet);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ModifyWalletActivity.startModifyWalletActivity(WalletManageActivity.this,
                                "");
                    }
                });
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_wallet_manager, false);
            return new VH(v);

        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (walletList == null) {
                return;
            }
            String address = walletList.get(position);
            String balance = WalletManager.getInstance(WalletManageActivity.this).getSWTBalance(address);
            holder.mTvBalance.setText(balance);
            holder.mTvBalanceCNY.setText("没数据。。");
            holder.mTvAddress.setText(address);
            holder.mTvName.setText(WalletSp.getInstance(WalletManageActivity.this, address).getName());

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return walletList.size();
        }
    }

    private void getWallets() {
        walletList = WalletSp.getInstance(this, "").getAllWallet();
        mAdapter.notifyDataSetChanged();
    }
}
