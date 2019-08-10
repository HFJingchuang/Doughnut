package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.fragment.KeyStoreFragment;
import com.doughnut.fragment.PrivateKeyFragment;
import com.doughnut.view.TitleBar;


public class WalletImportActivity extends BaseActivity implements View.OnClickListener {

    private final static int WALLET_INDEX = 0;
    private final static int MINE_INDEX = 1;
    private ViewPager mMainViewPager;

    //tab
    private LinearLayout mLayoutTabWallet;
    private LinearLayout mLayoutTabMine;

    private ImageView mImgWallet;
    private TextView mTvWallet;

    private ImageView mImgMine;
    private TextView mTvMine;
    private TitleBar mTitleBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_import);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!WalletInfoManager.getInstance().getCurrentWallet().isBaked) {
//            ViewUtil.showBakupDialog(MainActivity.this, WalletInfoManager.getInstance().getCurrentWallet(), false,
//                    true, WalletInfoManager.getInstance().getCurrentWallet().whash);
//        }
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutTabWallet) {
            mMainViewPager.setCurrentItem(WALLET_INDEX);
        } else if (view == mLayoutTabMine) {
            mMainViewPager.setCurrentItem(MINE_INDEX);
        }

    }

    /**
     * 前画面跳转用
     * @param context
     */
    public static void startWalletImportActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {
        initViewPager();
    }

    private void initViewPager() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.titleBar_import_wallet);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });
        //tab
        mLayoutTabWallet = (LinearLayout) findViewById(R.id.layout_tab_wallet);
        mLayoutTabMine = (LinearLayout) findViewById(R.id.layout_tab_mine);
        mLayoutTabWallet.setOnClickListener(this);
        mLayoutTabMine.setOnClickListener(this);


        mImgWallet = (ImageView) findViewById(R.id.img_tab_wallet);
        mTvWallet = (TextView) findViewById(R.id.tv_tab_wallet);
        mImgMine = (ImageView) findViewById(R.id.img_tab_mine);
        mTvMine = (TextView) findViewById(R.id.tv_tab_mine);

        mMainViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mMainViewPager.setOffscreenPageLimit(3);
        mMainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mMainViewPager.setAdapter(new WalletImportActivity.MainViewPagerAdapter(getSupportFragmentManager()));
        pageSelected(WALLET_INDEX);
    }



    private void pageSelected(int position) {
        resetTab();
        switch (position) {
            case WALLET_INDEX:
                mImgWallet.setImageResource(R.drawable.ic_tab_asset_selected);
                mTvWallet.setSelected(true);
                break;
            case MINE_INDEX:
                mImgMine.setImageResource(R.drawable.ic_tab_mine_selected);
                mTvMine.setSelected(true);
                break;
        }
    }

    private void resetTab() {
        mImgWallet.setImageResource(R.drawable.ic_tab_asset_unselected);
        mTvWallet.setSelected(false);

        mImgMine.setImageResource(R.drawable.ic_tab_mine_unselected);
        mTvMine.setSelected(false);
    }

    class MainViewPagerAdapter extends FragmentPagerAdapter {

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private Fragment[] mFragments = new Fragment[]{
                KeyStoreFragment.newInstance(),
                PrivateKeyFragment.newInstance()
        };

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }


}
