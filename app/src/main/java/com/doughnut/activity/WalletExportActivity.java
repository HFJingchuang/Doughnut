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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.fragment.KeyStoreExpFragment;
import com.doughnut.fragment.PrivateKeyExpFragment;
import com.doughnut.view.TitleBar;

public class WalletExportActivity extends BaseActivity implements View.OnClickListener {

    private final static int PRIVATEKEY_INDEX = 0;
    private final static int KEYSTORE_INDEX = 1;
    private ViewPager mMainViewPager;

    //tab
    private LinearLayout mLayoutTabPrivate;
    private LinearLayout mLayoutTabKeyStore;


    private TextView mTvPrivateKey;

    private TextView mTvKeyStore;
    private TitleBar mTitleBar;

    private Fragment[] mFragments = new Fragment[]{
            PrivateKeyExpFragment.newInstance("", ""),
            KeyStoreExpFragment.newInstance("")
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_export);
        if (getIntent() != null) {
            String walletAddress = getIntent().getStringExtra(Constant.WALLET_ADDRESS);
            String privateKey = getIntent().getStringExtra(Constant.PRIVATE_KEY);
            mFragments = new Fragment[]{
                    PrivateKeyExpFragment.newInstance(walletAddress, privateKey),
                    KeyStoreExpFragment.newInstance(walletAddress)

            };
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutTabPrivate) {
            mMainViewPager.setCurrentItem(PRIVATEKEY_INDEX);
        } else if (view == mLayoutTabKeyStore) {
            mMainViewPager.setCurrentItem(KEYSTORE_INDEX);
        }

    }

    public static void startExportWalletActivity(Context from, String address, String key) {
        Intent intent = new Intent(from, WalletExportActivity.class);
        intent.putExtra(Constant.WALLET_ADDRESS, address);
        intent.putExtra(Constant.PRIVATE_KEY, key);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back_white);
        mTitleBar.setTitle(R.string.titleBar_export_wallet);
        mTitleBar.setTitleTextColor(R.color.color_white);
        mTitleBar.setTitleBarBackColor(R.color.color_dialog_confirm);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });
        //tab
        mLayoutTabPrivate = (LinearLayout) findViewById(R.id.layout_tab_privatekey);
        mLayoutTabKeyStore = (LinearLayout) findViewById(R.id.layout_tab_keystore);
        mLayoutTabPrivate.setOnClickListener(this);
        mLayoutTabKeyStore.setOnClickListener(this);


        mTvPrivateKey = (TextView) findViewById(R.id.tv_tab_privatekey);
        mTvKeyStore = (TextView) findViewById(R.id.tv_tab_keystore);

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

        mMainViewPager.setAdapter(new WalletExportActivity.MainViewPagerAdapter(getSupportFragmentManager()));
        pageSelected(PRIVATEKEY_INDEX);
    }

    private void pageSelected(int position) {
        resetTab();
        switch (position) {
            case PRIVATEKEY_INDEX:
                mTvPrivateKey.setSelected(true);
                break;
            case KEYSTORE_INDEX:
                mTvKeyStore.setSelected(true);
                break;
        }
    }

    private void resetTab() {
        mTvPrivateKey.setSelected(false);

        mTvKeyStore.setSelected(false);
    }

    class MainViewPagerAdapter extends FragmentPagerAdapter {

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

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
