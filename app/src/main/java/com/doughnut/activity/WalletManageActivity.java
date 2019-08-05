package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.utils.MethodCompat;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;

import java.util.ArrayList;
import java.util.List;


public class WalletManageActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;

    private TextView mTvCreateWallet;
    private TextView mTvImPortWallet;

    private ListView mLsWallet;
    private WalletAdapter mAdapter;

    private List< WalletInfoManager.WData>  walletList;


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
        Intent intent = new Intent(context, ManageWalletActivity.class);
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


        ///初始化
        walletList=new ArrayList<>();
        WalletInfoManager.WData wallet = new WalletInfoManager.WData();
        wallet.waddress="agdjshsudfhfijshdjdjsiksididi";
        wallet.type=1;
        wallet.wname="钱包１";
        wallet.words="wewwre";

        walletList.add(wallet);

        WalletInfoManager.WData wallet1 = new WalletInfoManager.WData();
        wallet1.waddress="agdjshsudfhfijshdjdjsiksididi";
        wallet1.type=1;
        wallet1.wname="钱包2";
        walletList.add(wallet1);

        ///////////////////////////

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

        mLsWallet = (ListView) findViewById(R.id.ls_manager_wallet);
        mAdapter = new WalletAdapter();
        mLsWallet.setAdapter(mAdapter);

        mLsWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModifyWalletActivity.startModifyWalletActivity(WalletManageActivity.this,
                        WalletInfoManager.getInstance().getAllWallet().get(position).waddress);
            }
        });

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
//        CreateWalletActivity.navToActivity(WalletManageActivity.this, -1);
        Intent intent = new Intent(this, CreateNewWalletActivity.class);
        startActivity(intent);
    }

    private void gotoImportWallet() {
//        ImportWalletActivity.startImportWalletActivity(WalletManageActivity.this);
        Intent intent = new Intent(this, ImportWalletSelectActivity.class);
        startActivity(intent);
    }


    class WalletAdapter extends BaseAdapter {

        @Override
        public int getCount() {

//            return WalletInfoManager.getInstance().getAllWallet().size();
            return walletList.size();
        }

        @Override
        public Object getItem(int position) {
//            return WalletInfoManager.getInstance().getAllWallet() == null ? 0 : WalletInfoManager.getInstance().getAllWallet().size();
            return walletList == null ? 0 : walletList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_wallet_manager, false);
                holder = new ViewHolder();
                holder.mTvWalletName = (TextView) convertView.findViewById(R.id.tv_wallet_name);
//                holder.mTvBakTips = (TextView) convertView.findViewById(R.id.tv_bak_tips);
                holder.mTvWalletAddress = (TextView) convertView.findViewById(R.id.tv_wallet_address);
//                holder.mTvTotalAsset = (TextView) convertView.findViewById(R.id.tv_total_asset);
//                holder.mTvTotalAsset.setVisibility(View.INVISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            WalletInfoManager.WData wallet = WalletInfoManager.getInstance().getAllWallet().get(position);
            WalletInfoManager.WData wallet = walletList.get(position);
            holder.mTvWalletName.setText(wallet.wname);
//            holder.mTvBakTips.setVisibility(wallet.isBaked ? View.GONE : View.VISIBLE);
            holder.mTvWalletAddress.setText(wallet.waddress);
//            holder.mTvTotalAsset.setText(Html.fromHtml("<font><big>0</big>ether</font>"));
            return convertView;
        }

        class ViewHolder {
            TextView mTvWalletName;
//            TextView mTvBakTips;
            TextView mTvWalletAddress;
//            TextView mTvTotalAsset;
        }
    }
}
