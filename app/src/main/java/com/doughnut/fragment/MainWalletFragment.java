
package com.doughnut.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.Line;
import com.doughnut.R;
import com.doughnut.activity.AddCurrencyActivity;
import com.doughnut.activity.CreateNewWalletActivity;
import com.doughnut.activity.WalletImportActivity;
import com.doughnut.activity.WalletManageActivity;
import com.doughnut.adapter.BaseRecycleAdapter;
import com.doughnut.adapter.BaseRecyclerViewHolder;
import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.DefaultItemDecoration;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.NetUtil;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.RecyclerViewSpacesItemDecoration;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.jccdex.rpc.base.JCallback;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.zxing.activity.CaptureActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainWalletFragment extends BaseFragment implements View.OnClickListener {

    private static final int SCALE = 4;

    private SmartRefreshLayout mSmartRefreshLayout;
    private SwipeRecyclerView mRecycleView;
    private MainTokenRecycleViewAdapter mAdapter;
    private View mViewSee;
    private TextView mTvAddCurrency, mTvBalance, mTvBalanceDec, mTvBalanceCny, mTvBalanceCnyDec, mTvPrice;
    private LinearLayout mTvCreateWallet, mTvImportWallet, mLayoutScan, mLayoutName;
    private ImageView mTvOpenEyes;
    private TextView mTvWalletName;
    private ImageView mImgLoad, mImgLoadCNY;

    private List<Line> dataList;
    private String mCurrentWallet;
    private boolean isHidden;
    private boolean isTokenHidden;
    private ProgressDrawable mProgressDrawable;
    private ProgressDrawable mProgressDrawableCNY;

    public static MainWalletFragment newInstance() {
        Bundle args = new Bundle();
        MainWalletFragment fragment = new MainWalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return ViewUtil.inflatView(inflater, container, R.layout.fragment_main_wallet_new, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dataList = new ArrayList();
        isHidden = false;
        isTokenHidden = false;
        initView(view);
    }

    private void initView(View view) {
        mTvWalletName = view.findViewById(R.id.tv_wallet_name);
        mLayoutName = view.findViewById(R.id.layout_name);
        mLayoutName.setOnClickListener(this);

        mTvPrice = view.findViewById(R.id.tv_price);

        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(Color.WHITE);
        mImgLoad = view.findViewById(R.id.img_load);
        mImgLoad.setImageDrawable(mProgressDrawable);
        mProgressDrawable.start();

        mProgressDrawableCNY = new ProgressDrawable();
        mProgressDrawableCNY.setColor(Color.WHITE);
        mImgLoadCNY = view.findViewById(R.id.img_load_cny);
        mImgLoadCNY.setImageDrawable(mProgressDrawableCNY);
        mProgressDrawableCNY.start();

        //下拉刷新
        mSmartRefreshLayout = view.findViewById(R.id.layout_smart_refresh);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mAdapter.refresh();
            }
        });

        mTvBalance = view.findViewById(R.id.tv_balance);
        mTvBalanceDec = view.findViewById(R.id.tv_balance_decimal);
        mTvBalanceCny = view.findViewById(R.id.tv_balance_cny);
        mTvBalanceCnyDec = view.findViewById(R.id.tv_balance_cny_decimal);

        mTvOpenEyes = view.findViewById(R.id.openEyes);
        mViewSee = view.findViewById(R.id.view_see);
        mViewSee.setOnClickListener(this);
        mRecycleView = view.findViewById(R.id.mainwallet_recycleview);
        mRecycleView.addItemDecoration(new RecyclerViewSpacesItemDecoration(getContext(), 10));
        mRecycleView.setSwipeMenuCreator(swipeMenuCreator);
        mRecycleView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();
                int direction = menuBridge.getPosition();
                switch (direction) {
                    case 0:
                        hideToken(adapterPosition);
                        break;
                    case 1:
                        clearHideToken();
                        break;
                }
                mAdapter.refresh();
            }
        });
        mRecycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isReadyForPullEnd()) {
                    //最后一个可见
                    mAdapter.loadmore(null);
                }
            }
        });
        mAdapter = new MainTokenRecycleViewAdapter();
        mRecycleView.addItemDecoration(
                new DefaultItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_line)));
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(mAdapter);

        mLayoutScan = view.findViewById(R.id.layout_scan);
        mLayoutScan.setOnClickListener(this);

        mTvAddCurrency = view.findViewById(R.id.add_asset);
        mTvAddCurrency.setOnClickListener(this);

        mTvCreateWallet = view.findViewById(R.id.create_wallet);
        mTvCreateWallet.setOnClickListener(this);

        mTvImportWallet = view.findViewById(R.id.import_wallet);
        mTvImportWallet.setOnClickListener(this);
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dimen_mnue_width);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            if (isTokenHidden) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity()).setBackground(R.drawable.shape_delete_bg)
                        .setText(getResources().getString(R.string.tv_hide))
                        .setTextColor(Color.WHITE)
                        .setTextSize(16)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(deleteItem);

                SwipeMenuItem showItem = new SwipeMenuItem(getActivity()).setBackground(R.drawable.shape_show_bg)
                        .setText(getResources().getString(R.string.tv_show))
                        .setTextColor(Color.WHITE)
                        .setTextSize(16)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(showItem);
            } else {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity()).setBackground(R.drawable.shape_delete_node_bg)
                        .setText(getResources().getString(R.string.tv_hide))
                        .setTextColor(Color.WHITE)
                        .setTextSize(16)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(deleteItem);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        refreshWallet();
    }

    @Override
    public void onClick(View view) {
        if (!NetUtil.isNetworkAvailable(getActivity())) {
            ToastUtil.toast(getContext(), getString(R.string.toast_no_network));
            return;
        }
        switch (view.getId()) {
            case R.id.layout_name:
                WalletManageActivity.startModifyWalletActivity(getContext(), true);
                break;
            case R.id.layout_scan:
                CaptureActivity.startCaptureActivity(getContext());
                break;
            case R.id.add_asset:
                AddCurrencyActivity.startActivity(getContext(), false);
                break;
            case R.id.create_wallet:
                CreateNewWalletActivity.startCreateNewWalletActivity(getContext());
                break;
            case R.id.import_wallet:
                WalletImportActivity.startWalletImportActivity(getContext());
                break;
            case R.id.view_see:
                this.isHidden = !isHidden;
                if (isHidden) {
                    mTvOpenEyes.setImageResource(R.drawable.ic_close_eyes);
                } else {
                    mTvOpenEyes.setImageResource(R.drawable.ic_see);
                }
                mAdapter.refresh();
        }
    }

    /**
     * 界面刷新功能
     */
    private void refreshWallet() {
        setWalletInfo();
        dataList.clear();
        mAdapter.refresh();
    }

    /**
     * 获取当前钱包
     */
    private void setWalletInfo() {
        mCurrentWallet = WalletSp.getInstance(getContext(), "").getCurrentWallet();
        mTvWalletName.setText(WalletSp.getInstance(getContext(), mCurrentWallet).getName());
        ViewUtil.EllipsisTextView(mTvWalletName);
    }

    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecycleView.getChildAdapterPosition(
                    mRecycleView.getChildAt(mRecycleView.getChildCount() - 1));
            if (lastVisiblePosition >= mRecycleView.getAdapter().getItemCount() - 1) {
                return mRecycleView.getChildAt(mRecycleView.getChildCount() - 1).getBottom()
                        <= mRecycleView.getBottom();
            }
        } catch (Throwable e) {
        }

        return false;
    }

    class MainTokenRecycleViewAdapter extends BaseRecycleAdapter<String, RecyclerView.ViewHolder> {

        private boolean mHasMore = true;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = ViewUtil.inflatView(getContext(), parent, R.layout.wallet_token_item_view, false);

            return new TokenViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GsonUtil itemData = getItem(position);
            fillTokenData((TokenViewHolder) holder, itemData);
        }

        @Override
        public void loadData(final String params, final boolean loadmore) {
            if (loadmore && !mHasMore) {
                return;
            }
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, false, loadmore);
            }
            if (isHidden) {
                if (mTvBalanceCnyDec != null) {
                    mTvBalanceCny.setText("***");
                }
                if (mTvBalanceCny != null) {
                    mTvBalanceCnyDec.setText("");
                }
                if (mTvBalance != null) {
                    mTvBalance.setText("***");
                }
                if (mTvBalanceDec != null) {
                    mTvBalanceDec.setText("");
                }

                handleTokenRequestResult(params, loadmore, null);
                if (mSmartRefreshLayout != null) {
                    mSmartRefreshLayout.finishRefresh();
                }
            } else {
                // 取得钱包资产
                WalletManager.getInstance(getContext()).getBalance(mCurrentWallet, new ICallBack() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            AccountRelations accountRelations = (AccountRelations) response;

                            if (accountRelations != null) {
                                List<Line> lines = accountRelations.getLines();
                                if (lines != null) {
                                    dataList.clear();
                                    dataList.addAll(lines);
                                }

                            }
                            // 获取将显示的币种
                            List<String> currencies = new ArrayList<>();
                            for (int i = 0; i < dataList.size(); i++) {
                                currencies.add(dataList.get(i).getCurrency());
                            }

                            List<String> selects = getSelectToken();
                            for (int i = 0; i < selects.size(); i++) {
                                String token = selects.get(i);
                                if (!currencies.contains(token)) {
                                    Line line = new Line();
                                    line.setCurrency(token);
                                    dataList.add(line);
                                }
                            }

                            //去除选择隐藏的币种
                            deleteHideToken();

                            //排序,余额 > 冻结
                            Collections.sort(dataList, new Comparator<Line>() {
                                @Override
                                public int compare(Line o1, Line o2) {
                                    //币种排序
//                                String cur1 = o1.getCurrency();
//                                String cur2 = o2.getCurrency();
//                                boolean b1 = Util.isStartWithNumber(cur1);
//                                boolean b2 = Util.isStartWithNumber(cur2);
//                                if (b1 && !b2) {
//                                    return 1;
//                                } else if (!b1 && b2) {
//                                    return -1;
//                                } else {
//                                    return cur1.compareTo(cur2);
//                                }

                                    String b1 = o1.getBalance();
                                    String b2 = o2.getBalance();
                                    if (CaclUtil.compare(b1, "0") == 0 && CaclUtil.compare(b2, "0") == 0) {
                                        String l1 = o1.getLimit();
                                        String l2 = o2.getLimit();
                                        return CaclUtil.compare(l2, l1);
                                    } else {
                                        return CaclUtil.compare(b2, b1);
                                    }
                                }
                            });

                            if (dataList == null || dataList.size() == 0) {
                                stopLoadAnima();
                                if (mTvBalanceCnyDec != null) {
                                    mTvBalanceCny.setText("0.00");
                                }
                                if (mTvBalanceCny != null) {
                                    mTvBalanceCnyDec.setText("");
                                }
                                if (mTvBalance != null) {
                                    mTvBalance.setText("0.00");
                                }
                                if (mTvBalanceDec != null) {
                                    mTvBalanceDec.setText("");
                                }
                            } else {
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
                                                values = CaclUtil.add(values, value, 2);
                                            }
                                            number = CaclUtil.div(values, swtPrice, 2);
                                            AppConfig.postOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTvPrice.setText(String.format("≈￥%.5f", new BigDecimal(swtPrice)));
                                                    mTvPrice.setVisibility(View.VISIBLE);

                                                    if (values.contains(".")) {
                                                        String[] balanceArr = values.split("\\.");
                                                        mTvBalanceCny.setText(Util.formatWithComma(Double.parseDouble(balanceArr[0]), 0));
                                                        if (!TextUtils.isEmpty(balanceArr[1])) {
                                                            mTvBalanceCnyDec.setText("." + balanceArr[1]);
                                                        }
                                                    } else {
                                                        mTvBalanceCny.setText(Util.formatWithComma(Double.parseDouble(values), 0));
                                                    }

                                                    if (number.contains(".")) {
                                                        String[] balanceArr = number.split("\\.");
                                                        mTvBalance.setText(Util.formatWithComma(Double.parseDouble(balanceArr[0]), 0));
                                                        if (!TextUtils.isEmpty(balanceArr[1])) {
                                                            mTvBalanceDec.setText("." + balanceArr[1]);
                                                        }
                                                    } else {
                                                        mTvBalance.setText(Util.formatWithComma(Double.parseDouble(number), 0));
                                                    }
                                                }
                                            });
                                        } else {
                                            AppConfig.postOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (mTvBalanceCnyDec != null) {
                                                        mTvBalanceCny.setText("---");
                                                    }
                                                    if (mTvBalanceCny != null) {
                                                        mTvBalanceCnyDec.setText("");
                                                    }
                                                    if (mTvBalance != null) {
                                                        mTvBalance.setText("---");
                                                    }
                                                    if (mTvBalanceDec != null) {
                                                        mTvBalanceDec.setText("");
                                                    }
                                                }
                                            });
                                        }
                                        stopLoadAnima();
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                            GsonUtil extra = new GsonUtil(dataList);
                            handleTokenRequestResult(params, loadmore, extra);
                            if (mSmartRefreshLayout != null) {
                                mSmartRefreshLayout.finishRefresh();
                            }
                        }
                    }
                });
            }
        }


        private void handleTokenRequestResult(final String params, final boolean loadmore, GsonUtil json) {
            GsonUtil tokens = json;
            if (!loadmore) {
                //第一页
                setData(tokens);
            } else {
                if (tokens.getLength() > 0) {
                    addData(tokens);
                }
            }
            mHasMore = false;
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, true, loadmore);
            }
        }

        private void fillTokenData(TokenViewHolder holder, GsonUtil data) {
            String currency = data.getString("currency", "");
            holder.mTvTokenName.setText(currency);
            ViewUtil.EllipsisTextView(holder.mTvTokenName);
            holder.mImgTokenIcon.setImageResource(Util.getTokenIcon(currency));
            if (!isHidden) {
                try {
                    String balance = CaclUtil.formatAmount(data.getString("balance", "0"), SCALE);
                    String balanceFreeze = CaclUtil.formatAmount(data.getString("limit", "0"), SCALE);
                    String sum = CaclUtil.add(balance, balanceFreeze, SCALE);
                    if (TextUtils.equals(WConstant.CURRENCY_CNY, currency)) {
                        holder.mTvCNY.setText(sum);
                    } else {
                        WalletManager.getInstance(getContext()).getTokenPrice(currency, new JCallback() {
                            @Override
                            public void onResponse(String code, String response) {
                                if (TextUtils.equals(code, WConstant.SUCCESS_CODE)) {
                                    GsonUtil res = new GsonUtil(response);
                                    GsonUtil data = res.getArray("data");
                                    if (data.isValid()) {
                                        // SWT当前价
                                        String cur = data.getString(1, "0");
                                        // 计算SWT总价值
                                        String value = CaclUtil.mul(sum, cur, SCALE);
                                        AppConfig.postOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                holder.mTvCNY.setText(String.format("%.2f", new BigDecimal(value)));
                                            }
                                        });
                                    }
                                } else {
                                    AppConfig.postOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.mTvCNY.setText("0.00");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    holder.mTvTokenCount.setText(balance);
                    holder.mTvTokenFreeze.setText(balanceFreeze);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                holder.mTvTokenCount.setText("***");
                holder.mTvCNY.setText("***");
                holder.mTvTokenFreeze.setText("***");
            }
        }

        class TokenViewHolder extends BaseRecyclerViewHolder {
            ImageView mImgTokenIcon;
            TextView mTvTokenName;
            TextView mTvCNY, mTvTokenCount, mTvTokenFreeze;

            public TokenViewHolder(View itemView) {
                super(itemView);
                mImgTokenIcon = itemView.findViewById(R.id.token_icon);
                mTvTokenName = itemView.findViewById(R.id.token_name);
                mTvCNY = itemView.findViewById(R.id.tv_balance_cny);
                mTvTokenCount = itemView.findViewById(R.id.token_count);
                mTvTokenFreeze = itemView.findViewById(R.id.token_freeze);
            }
        }
    }

    /**
     * 保存被隐藏的token
     *
     * @param index
     */
    private void hideToken(int index) {
        String fileName = getContext().getPackageName() + Constant.SELECT_TOKEN + mCurrentWallet;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String tokens = sharedPreferences.getString("hide", "");
        List<String> tokenList;
        if (TextUtils.isEmpty(tokens)) {
            tokenList = new ArrayList();
        } else {
            if (tokens.contains(",")) {
                List<String> arrList = Arrays.asList(tokens.split(","));
                tokenList = new ArrayList(arrList);
            } else {
                tokenList = new ArrayList();
                tokenList.add(tokens);
            }
        }
        tokenList.add(dataList.get(index).getCurrency());
        editor.putString("hide", tokenList.toString().replace("[", "").replace("]", "").replace(" ", ""));
        editor.apply();
    }

    /**
     * 清除被隐藏的token
     */
    private void clearHideToken() {
        String fileName = getContext().getPackageName() + Constant.SELECT_TOKEN + mCurrentWallet;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("hide");
        editor.apply();
        isTokenHidden = false;
    }

    /**
     * 显示时，去除被隐藏的token
     */
    private void deleteHideToken() {
        String fileName = getContext().getPackageName() + Constant.SELECT_TOKEN + mCurrentWallet;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString("hide", "");
        List<String> tokenList;
        if (!TextUtils.isEmpty(tokens)) {
            if (tokens.contains(",")) {
                List<String> arrList = Arrays.asList(tokens.split(","));
                tokenList = new ArrayList(arrList);
            } else {
                tokenList = new ArrayList();
                tokenList.add(tokens);
            }
            if (tokenList.size() > 0) {
                isTokenHidden = true;
                for (int i = dataList.size() - 1; i >= 0; i--) {
                    String currency = dataList.get(i).getCurrency();
                    if (tokenList.contains(currency)) {
                        dataList.remove(i);
                    }
                }
            } else {
                isTokenHidden = false;
            }
        }
    }

    /**
     * 显示时，获取用户添加的币种
     */
    private List<String> getSelectToken() {
        String fileName = getContext().getPackageName() + Constant.SELECT_TOKEN + mCurrentWallet;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString("select", "");
        List<String> tokenList = new ArrayList<>();
        if (!TextUtils.isEmpty(tokens)) {
            if (tokens.contains(",")) {
                List<String> arrList = Arrays.asList(tokens.split(","));
                tokenList = new ArrayList(arrList);
            } else {
                tokenList = new ArrayList();
                tokenList.add(tokens);
            }
        }
        return tokenList;
    }

    private void stopLoadAnima() {
        if (mImgLoad.isShown() && mImgLoad.isShown()) {
            AppConfig.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDrawable.stop();
                    mProgressDrawableCNY.stop();
                    mImgLoad.setVisibility(View.GONE);
                    mImgLoadCNY.setVisibility(View.GONE);
                }
            });
        }
    }
}