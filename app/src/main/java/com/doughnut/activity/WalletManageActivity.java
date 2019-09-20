package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.Line;
import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.jccdex.rpc.base.JCallback;

import java.util.ArrayList;
import java.util.List;

import static com.doughnut.config.AppConfig.getContext;


public class WalletManageActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;

    private LinearLayout mLayoutCreateWallet;
    private LinearLayout mLayoutImPortWallet;
    private LinearLayout mLayoutNoWallet;
    private RecyclerView mLsWallet;
    private WalletRecordAdapter mAdapter;

    private List<String> walletList = new ArrayList<>();
    private boolean isChange;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerwallet);
        isChange = false;
        if (getIntent() != null) {
            isChange = getIntent().getBooleanExtra("isChange", false);
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWallets();
    }

    @Override
    public void onClick(View v) {
        if (v == mLayoutCreateWallet) {
            gotoCreateWallet();
        } else if (v == mLayoutImPortWallet) {
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

    public static void startModifyWalletActivity(Context context, boolean isChange) {
        Intent intent = new Intent(context, WalletManageActivity.class);
        intent.putExtra("isChange", isChange);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        if (isChange) {
            mTitleBar.setTitle(getResources().getString(R.string.title_change_wallet));
        } else {
            mTitleBar.setTitle(getString(R.string.titleBar_manage_wallet));
        }

        mTitleBar.setTitleTextColor(R.color.black);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.setTitleBarClickListener(this);

        mLayoutNoWallet = findViewById(R.id.layout_no_wallet);
        mLayoutCreateWallet = findViewById(R.id.layout_create_wallet);
        mLayoutCreateWallet.setOnClickListener(this);
        mLayoutImPortWallet = findViewById(R.id.layout_import_wallet);
        mLayoutImPortWallet.setOnClickListener(this);
        mLsWallet = (RecyclerView) findViewById(R.id.ls_manager_wallet);
        mLsWallet.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WalletRecordAdapter();
        mLsWallet.setAdapter(mAdapter);
        getWallets();
    }

    private void gotoCreateWallet() {
        CreateNewWalletActivity.startCreateNewWalletActivity(this);
    }

    private void gotoImportWallet() {
        WalletImportActivity.startWalletImportActivity(this);
    }

    class WalletRecordAdapter extends RecyclerView.Adapter<WalletRecordAdapter.VH> {

        String currentWallet = WalletSp.getInstance(WalletManageActivity.this, "").getCurrentWallet();

        class VH extends RecyclerView.ViewHolder {
            LinearLayout mLayoutItem;
            TextView mTvBalance;
            TextView mTvBalanceCNY;
            TextView mTvAddress;
            TextView mTvName;
            TextView mTvTime;
            TextView mTvLabel;
            ImageView mImgQR;

            public VH(View v) {
                super(v);
                mTvBalance = itemView.findViewById(R.id.tv_balance);
                mTvBalanceCNY = itemView.findViewById(R.id.tv_balance_cny);
                mTvAddress = itemView.findViewById(R.id.tv_wallet_address);
                mImgQR = itemView.findViewById(R.id.img_qr);
                mImgQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WalletQRActivity.startTokenReceiveActivity(WalletManageActivity.this, walletList.get(getAdapterPosition()));
                    }
                });

                mTvName = itemView.findViewById(R.id.tv_wallet_name);
                ViewUtil.EllipsisTextView(mTvName);
                mTvTime = itemView.findViewById(R.id.tv_wallet_time);
                mTvLabel = itemView.findViewById(R.id.tv_label);
                mLayoutItem = itemView.findViewById(R.id.layout_wallet);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isChange) {
                            WalletSp.getInstance(WalletManageActivity.this, "").setCurrentWallet(walletList.get(getAdapterPosition()));
                            WalletManageActivity.this.finish();
                        } else {
                            ModifyWalletActivity.startModifyWalletActivity(WalletManageActivity.this,
                                    walletList.get(getAdapterPosition()));
                        }
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
            if (TextUtils.equals(currentWallet, address)) {
                holder.mTvLabel.setVisibility(View.VISIBLE);
            }
            holder.mTvAddress.setText(address);
            ViewUtil.EllipsisTextView(holder.mTvAddress);
            holder.mTvName.setText(WalletSp.getInstance(WalletManageActivity.this, address).getName());
            ViewUtil.EllipsisTextView(holder.mTvName);
            holder.mTvTime.setText(WalletSp.getInstance(WalletManageActivity.this, address).getCreateTime());
            // 取得钱包资产
            WalletManager.getInstance(getContext()).getBalance(address, new ICallBack() {
                @Override
                public void onResponse(Object response) {
                    if (response != null) {
                        AccountRelations accountRelations = (AccountRelations) response;
                        if (accountRelations != null) {
                            List<Line> dataList = accountRelations.getLines();
                            if (dataList != null) {
                                WalletManager.getInstance(getContext()).getAllTokenPrice(new JCallback() {
                                    // 钱包总价值
                                    String values = "0.00";
                                    // 钱包折换总SWT
                                    String number = "0.00";
                                    String swtPrice = "0.00";

                                    @Override
                                    public void onResponse(String code, String response) {
                                        if (TextUtils.equals(code, WConstant.SUCCESS_CODE)) {
                                            GsonUtil res = new GsonUtil(response);
                                            GsonUtil data = res.getObject("data");
                                            GsonUtil gsonUtil = data.getArray("SWT-CNY");
                                            swtPrice = gsonUtil.getString(1, "0");

                                            for (int i = 0; i < dataList.size(); i++) {
                                                Line line = (Line) dataList.get(i);
                                                // 数量
                                                String balance = line.getBalance();
                                                if (TextUtils.isEmpty(balance)) {
                                                    balance = "0";
                                                }
                                                // 币种
                                                String currency = line.getCurrency();
                                                // 冻结
                                                String freeze = line.getLimit();
                                                if (TextUtils.isEmpty(freeze)) {
                                                    freeze = "0";
                                                }

                                                String price = "0";
                                                if (TextUtils.equals(currency, WConstant.CURRENCY_CNY)) {
                                                    price = "1";
                                                } else {
                                                    String currency_cny = currency + "-CNY";
                                                    GsonUtil currencyLst = data.getArray(currency_cny);
                                                    if (currencyLst != null) {
                                                        price = currencyLst.getString(1, "0");
                                                    }
                                                }
                                                // 当前币种总价值
                                                String sum = CaclUtil.add(balance, freeze);
                                                String value = CaclUtil.mul(sum, price);
                                                values = CaclUtil.add(values, value);
                                            }
                                            number = CaclUtil.div(values, swtPrice, 2);
                                            AppConfig.postOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.mTvBalance.setText(Util.formatWithComma(Double.parseDouble(number), 2));
                                                    holder.mTvBalanceCNY.setText(Util.formatWithComma(Double.parseDouble(values), 2));

                                                }
                                            });
                                        } else {
                                            holder.mTvBalanceCNY.setText("0.00");
                                            holder.mTvBalance.setText("0.00");
                                        }
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                holder.mTvBalanceCNY.setText("0.00");
                                holder.mTvBalance.setText("0.00");
                            }
                        }
                    }
                }
            });
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
        boolean isNoWallet = WalletManager.getInstance(this).hasWallet();
        if (isNoWallet) {
            mLayoutNoWallet.setVisibility(View.GONE);
            mLsWallet.setVisibility(View.VISIBLE);
        } else {
            mLayoutNoWallet.setVisibility(View.VISIBLE);
            mLsWallet.setVisibility(View.GONE);
            return;
        }
        walletList.clear();
        String currentWallet = WalletSp.getInstance(WalletManageActivity.this, "").getCurrentWallet();
        walletList.add(currentWallet);
        List<String> wallets = WalletSp.getInstance(this, "").getAllWallet();
        wallets.remove(currentWallet);
        walletList.addAll(wallets);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
