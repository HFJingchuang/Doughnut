
package com.doughnut.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jtblk.client.bean.AccountRelations;
import com.doughnut.R;
import com.doughnut.activity.AddCurrencyActivity;
import com.doughnut.activity.CreateNewWalletActivity;
import com.doughnut.activity.TokenDetailsActivity;
import com.doughnut.activity.WalletImportActivity;
import com.doughnut.adapter.BaseRecycleAdapter;
import com.doughnut.adapter.BaseRecyclerViewHolder;
import com.doughnut.base.BaseWalletUtil;
import com.doughnut.base.BlockChainData;
import com.doughnut.base.TBController;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.dialog.DeleteDialog;
import com.doughnut.dialog.WalletActionPop;
import com.doughnut.dialog.WalletMenuPop;
import com.doughnut.utils.DefaultItemDecoration;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.NetUtil;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.TokenImageLoader;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.RecyclerViewSpacesItemDecoration;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;


public class MainWalletFragment extends BaseFragment implements View.OnClickListener {

    private final static int SCAN_REQUEST_CODE = 10001;

    private SwipeRefreshLayout mSwipteRefreshLayout;
    private CardView mAppbarLayout;
    private SwipeMenuRecyclerView mRecycleView;
    private MainTokenRecycleViewAdapter mAdapter;
    private View mEmptyView;
    private View mWalletAction, mMenuAction, mViewSee;
    private TextView mTvWalletName, mTvAddCurrency, mTvBalance, mTvBalanceDec, mTvBalanceCny, mTvBalanceCnyDec;
    private LinearLayout mTvCreateWallet, mTvImportWallet;
    private ImageView mTvOpenEyes, mTvCloseEyes;

    private WalletMenuPop walletMenuPop;
    private WalletActionPop walletActionPop;
//    private boolean isAssetVisible = false;
    private BaseWalletUtil mWalletUtil;

    private String unit = "¥";
    private boolean isViewCreated = false;

    private Context mContext;
    private TextView mNameTv;

    private Integer deletePosition;

    private List dataList;

    private boolean isHidden;

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
        mContext = getActivity();
        dataList = new ArrayList();
        deletePosition = null;
        isHidden = false;
        initView(view);
    }

    private void initView(View view) {

        mNameTv = view.findViewById(R.id.tv_wallet_name);

        //下拉刷新
        mSwipteRefreshLayout = view.findViewById(R.id.swiperefreshlayout);
        //下拉刷新的圆圈是否显示
        mSwipteRefreshLayout.setRefreshing(false);

        //设置下拉时圆圈的颜色（可以由多种颜色拼成）
        mSwipteRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);

        //设置下拉时圆圈的背景颜色（这里设置成白色）
        mSwipteRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        //设置下拉刷新时的操作
        mSwipteRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

//                //修改数据的代码，最后记得填上此行代码
                mSwipteRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshWallet();
                        mSwipteRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mAppbarLayout = view.findViewById(R.id.main_appbar);
//        mAppbarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);

        mWalletAction = view.findViewById(R.id.wallet_menu_action);
        mTvWalletName = view.findViewById(R.id.tv_wallet_name);
        setWalletName();

        mEmptyView = view.findViewById(R.id.empty_view);

        mTvBalance = view.findViewById(R.id.tv_balance);
        mTvBalanceDec = view.findViewById(R.id.tv_balance_decimal);
        mTvBalanceCny = view.findViewById(R.id.tv_balance_cny);
        mTvBalanceCnyDec = view.findViewById(R.id.tv_balance_cny_decimal);

        mViewSee = view.findViewById(R.id.view_see);
        mViewSee.setOnClickListener(this);
        mRecycleView = view.findViewById(R.id.mainwallet_recycleview);

        mRecycleView.addItemDecoration(new RecyclerViewSpacesItemDecoration(mContext, 10));
        mRecycleView.setSwipeMenuCreator(swipeMenuCreator);

        ///
        mRecycleView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                int position = menuBridge.getAdapterPosition();//当前item的position

                ToastUtil.toast(getActivity(), "已删除");
                deletePosition = position;
                mAdapter.refresh();
                menuBridge.closeMenu();
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
//        mAdapter.setDataLoadingListener(this);
        mRecycleView.addItemDecoration(
                new DefaultItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_line)));
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(mAdapter);


        mTvWalletName.setOnClickListener(this);
        mMenuAction = view.findViewById(R.id.wallet_menu);
        mMenuAction.setOnClickListener(this);

        mTvAddCurrency = view.findViewById(R.id.add_asset);
        mTvAddCurrency.setOnClickListener(this);

        mTvCreateWallet = view.findViewById(R.id.create_wallet);
        mTvCreateWallet.setOnClickListener(this);

        mTvImportWallet = view.findViewById(R.id.import_wallet);
        mTvImportWallet.setOnClickListener(this);

        mTvOpenEyes = view.findViewById(R.id.openEyes);
        mTvOpenEyes.setOnClickListener(this);

//        mTvCloseEyes = view.findViewById(R.id.closeEyes);
//        mTvCloseEyes.setOnClickListener(this);

        isViewCreated = true;

        setWalletInfo();
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dimen_mnue_width);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的菜单，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackground(R.drawable.shape_delete_bg)
                        .setText(R.string.tv_delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);
            }
        }
    };

    private void setWalletInfo() {
        String currentWallet = WalletSp.getInstance(getContext(), "").getCurrentWallet();

        mNameTv.setText(WalletSp.getInstance(getContext(), currentWallet).getName());
    }

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
            case R.id.tv_wallet_name:
                showWalletMenuPop();
                break;
            case R.id.wallet_menu:
                showActionMenuPop();
                break;
            case R.id.mainwallet_recycleview:
//                CreateNewWalletActivity.startCreateNewWalletActivity(mContext);
                break;
            case R.id.add_asset:
                AddCurrencyActivity.startLanguageActivity(mContext);
                break;
            case R.id.view_see:
                this.isHidden = !isHidden;
                if (isHidden) {
                    mTvOpenEyes.setImageResource(R.drawable.ic_close_eyes);
                } else {
                    mTvOpenEyes.setImageResource(R.drawable.ic_see);
                }
                mAdapter.refresh();
                break;
            case R.id.create_wallet:
                CreateNewWalletActivity.startCreateNewWalletActivity(mContext);
                break;
            case R.id.import_wallet:
                WalletImportActivity.startImportWalletActivity(mContext);
                break;
        }
    }


    /**
     * 显示钱包菜单pop
     */
    private void showWalletMenuPop() {
//        if (walletMenuPop == null) {q
//            walletMenuPop = new WalletMenuPop(getActivity());
//            walletMenuPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    mTvWalletName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0,
//                            R.drawable.ic_arrow_down, 0);
//                    refreshWallet();
//                }
//            });
//        }
//        walletMenuPop.setData();
//        walletMenuPop.showAsDropDown(mTvWalletName);
//        mTvWalletName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, R.drawable.ic_arrow_up, 0);
    }

    /**
     * 界面刷新功能
     */
    private void refreshWallet() {
        setWalletName();
        deletePosition = null;
        mAdapter.refresh();
        mWalletUtil = TBController.getInstance().getWalletUtil(WalletInfoManager.getInstance().getWalletType());
    }


    /**
     * 显示功能菜单pop
     */
    private void showActionMenuPop() {
        Intent intent = new Intent(mContext, CaptureActivity.class);
        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }


    private void update() {
//        mTvWalletUnit.setText(String.format(getString(R.string.content_my_asset)));
        setWalletName();
    }

    private void setWalletName() {
        BlockChainData.Block block = BlockChainData.getInstance().getBolckByHid(WalletInfoManager.getInstance().getWalletType());
        if (block != null) {
            mTvWalletName.setText(WalletInfoManager.getInstance().getWname() +
                    "(" + block.desc + ")");
        }
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

    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            float fraction = Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange();
//            mToolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.colorPrimary), fraction));
            if (fraction < 0.5f) {
                if (mTvWalletName.getVisibility() != View.VISIBLE) {
                    mTvWalletName.setVisibility(View.VISIBLE);
                    mWalletAction.setVisibility(View.GONE);
                }
                if (fraction < 0.3) {
                    mTvWalletName.setAlpha(1);
                } else {
                    mTvWalletName.setAlpha(1 - (float) ((fraction - 0.3) * 5));
                }
            } else {
                if (mWalletAction.getVisibility() != View.VISIBLE) {
                    mTvWalletName.setVisibility(View.GONE);
                    mWalletAction.setVisibility(View.VISIBLE);
                }
                if (fraction > 0.7) {
                    mWalletAction.setAlpha(1);
                } else {
                    mWalletAction.setAlpha((float) ((fraction - 0.5) * 5));
                }
            }
            if (verticalOffset >= 0) {
                if (!mSwipteRefreshLayout.isEnabled()) {
                    mSwipteRefreshLayout.setEnabled(true);
                }
            } else {
                if (mSwipteRefreshLayout.isEnabled()) {
                    mSwipteRefreshLayout.setEnabled(false);
                }
            }
        }
    };

    class MainTokenRecycleViewAdapter extends BaseRecycleAdapter<String, RecyclerView.ViewHolder> {

        private boolean mHasMore = true;
        private int mPageIndex = 0;
        private final static int PAGE_SIZE = 10;

        private BaseRecyclerViewHolder.ItemClickListener mItemClickListener =
                new BaseRecyclerViewHolder.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        gotoTokenDetail(MainTokenRecycleViewAdapter.this.getItem(position));
                    }
                };
        /***
         * 长安按钮点击事件
         */
        private BaseRecyclerViewHolder.ItemLongClickListener mItemLongClickListener =
                new BaseRecyclerViewHolder.ItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {

                        DeleteDialog pwdDialog = new DeleteDialog(mContext);
                        pwdDialog.show();

//                        ToastUtil.toast(getActivity(),"长按按钮点击删除方法"+position);
                    }
                };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = ViewUtil.inflatView(getContext(), parent, R.layout.wallet_token_item_view, false);

            //　由原本的点击事件，更改为长安点击事件　
            // return new TokenViewHolder(view, mItemClickListener);
            return new TokenViewHolder(view, mItemLongClickListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GsonUtil itemData = getItem(position);
            fillTokenData((TokenViewHolder) holder, itemData);
        }

        @Override
        public void loadData(final String params, final boolean loadmore) {
            if (!loadmore) {
                mPageIndex = 0;
            } else {
                mPageIndex++;
            }
            if (loadmore && !mHasMore) {
                return;
            }
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, false, loadmore);
            }

            WalletManager walletManager = WalletManager.getInstance(mContext);
            String currentWallet = WalletSp.getInstance(getContext(), "").getCurrentWallet();
            // 取得钱包资产
            AccountRelations accountRelations = walletManager.getBalance(currentWallet);
            List list = new ArrayList();
            if (accountRelations != null) {
                list = accountRelations.getLines();
                if (dataList != null && dataList.size() != 0) {
                    list = dataList;
                }
                List copyList = new ArrayList<>();
                if (deletePosition != null) {
                    copyList.add(list.get(deletePosition));
                }
                list.removeAll(copyList);
                dataList = list;
            }

            GsonUtil extra = new GsonUtil(list);

            // SWTC余额
//            String balance = walletManager.getSWTBalance(currentWallet);
//            String[] balanceArr = balance.split("\\.");
//            mTvBalance.setText(Util.formatWithComma(Long.parseLong(balanceArr[0])));
//            mTvBalanceDec.setText(balanceArr[1]);
            // SWTC实时总价值
            walletManager.getAllTokenPrice(dataList, mTvBalanceCny, mTvBalanceCnyDec, mTvBalance, mTvBalanceDec, isHidden);
            handleTokenRequestResult(params, loadmore, extra);
        }



        private void handleTokenRequestResult(final String params, final boolean loadmore, GsonUtil json) {
//            TLog.d(TAG, "token list:" + json);
            GsonUtil data = json.getObject("data", "{}");
            unit = data.getString("unit", "¥");
            GsonUtil tokens = json;
            if (!loadmore) {
                //第一页
                setData(tokens);
            } else {
                if (tokens.getLength() > 0) {
                    addData(tokens);
                }
            }
            if (!loadmore) {
                update();
            }
            mHasMore = false;
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, true, loadmore);
            }
        }

        private void fillTokenData(TokenViewHolder holder, GsonUtil data) {
            TokenImageLoader.displayImage(data.getString("icon_url", ""), holder.mImgTokenIcon,
                    TokenImageLoader.imageOption(R.drawable.ic_images_common_loading, R.drawable.ic_images_asset_eth,
                            R.drawable.ic_images_asset_eth));
            holder.mTvTokenName.setText(data.getString("currency", "ETH"));
            if (!isHidden) {
//                holder.mTvTokenCount.setText("" + mWalletUtil.getValue(data.getInt("decimal", 0), Util.parseDouble(data.getString("balance", "0"))));
                holder.mTvTokenCount.setText("" + Util.parseDouble(data.getString("balance", "0")));
            } else {
                holder.mTvTokenCount.setText("***");
            }
        }

        private void gotoTokenDetail(GsonUtil data) {
            TokenDetailsActivity.NavToActivity(getActivity(), data.toString(), unit);
        }

        class TokenViewHolder extends BaseRecyclerViewHolder {
            ImageView mImgTokenIcon;
            TextView mTvTokenName;
            TextView mTvTokenCount;
            TextView mTvTokenAsset;

            public TokenViewHolder(View itemView, ItemLongClickListener onItemLongClickListener) {
                super(itemView, onItemLongClickListener);
                mImgTokenIcon = itemView.findViewById(R.id.token_icon);
                mTvTokenName = itemView.findViewById(R.id.token_name);
                mTvTokenCount = itemView.findViewById(R.id.token_count);
                mTvTokenAsset = itemView.findViewById(R.id.token_asset);
            }
        }
    }
}