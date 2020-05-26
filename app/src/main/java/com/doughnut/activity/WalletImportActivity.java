package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.fragment.KeyStoreImpFragment;
import com.doughnut.fragment.MnemonicImpFragment;
import com.doughnut.fragment.PrivateKeyImpFragment;
import com.doughnut.view.TitleBar;
import com.zxing.activity.CaptureActivity;


public class WalletImportActivity extends BaseActivity implements View.OnClickListener {

    private final static int PRIVATEKEY_INDEX = 0;
    private final static int KEYSTORE_INDEX = 1;
    private final static int MNEMONIC_INDEX = 2;
    private ViewPager mMainViewPager;
    private LinearLayout mLayoutTabPrivateKey;
    private LinearLayout mLayoutTabKeyStore;
    private LinearLayout mLayoutTabMnemonic;
    private TextView mTvPrivateKey;
    private TextView mTvKeyStore;
    private TextView mTvMnemonic;
    private TitleBar mTitleBar;

    private Fragment[] mFragments = new Fragment[]{
            PrivateKeyImpFragment.newInstance(""),
            KeyStoreImpFragment.newInstance(""),
            MnemonicImpFragment.newInstance()
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_import);
        initView();
        if (getIntent() != null) {
            int index = getIntent().getIntExtra(Constant.PAGE_INDEX, 0);
            String importKey = getIntent().getStringExtra(Constant.IMPORT_KEY);
            if (index == 0) {
                mFragments = new Fragment[]{
                        PrivateKeyImpFragment.newInstance(importKey),
                        KeyStoreImpFragment.newInstance(""),
                        MnemonicImpFragment.newInstance()

                };
            } else {
                mFragments = new Fragment[]{
                        PrivateKeyImpFragment.newInstance(""),
                        KeyStoreImpFragment.newInstance(importKey),
                        MnemonicImpFragment.newInstance()

                };
            }
            mMainViewPager.getAdapter().notifyDataSetChanged();
            mMainViewPager.setCurrentItem(index);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainActivity.startMainActivity(WalletImportActivity.this);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutTabPrivateKey) {
            mMainViewPager.setCurrentItem(PRIVATEKEY_INDEX);
        } else if (view == mLayoutTabKeyStore) {
            mMainViewPager.setCurrentItem(KEYSTORE_INDEX);
        } else if (view == mLayoutTabMnemonic) {
            mMainViewPager.setCurrentItem(MNEMONIC_INDEX);
        }

    }

    public static void startWalletImportActivity(Context from) {
        Intent intent = new Intent(from, WalletImportActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    public static void startWalletImportActivity(Context context, int pageIndex, String importKey) {
        Intent intent = new Intent(context, WalletImportActivity.class);
        intent.putExtra(Constant.PAGE_INDEX, pageIndex);
        intent.putExtra(Constant.IMPORT_KEY, importKey);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.titleBar_import_wallet);
        mTitleBar.setTitleTextColor(R.color.color_currency_name);
        mTitleBar.setRightDrawable(R.drawable.ic_scan);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onRightClick(View view) {
                CaptureActivity.startCaptureActivity(WalletImportActivity.this, null);
            }

            @Override
            public void onLeftClick(View view) {
                MainActivity.startMainActivity(WalletImportActivity.this);
                finish();
            }
        });

        mLayoutTabPrivateKey = (LinearLayout) findViewById(R.id.layout_tab_privatekey);
        mLayoutTabKeyStore = (LinearLayout) findViewById(R.id.layout_tab_keystore);
        mLayoutTabMnemonic = (LinearLayout) findViewById(R.id.layout_tab_mnemonic);
        mLayoutTabPrivateKey.setOnClickListener(this);
        mLayoutTabKeyStore.setOnClickListener(this);
        mLayoutTabMnemonic.setOnClickListener(this);

        mTvPrivateKey = (TextView) findViewById(R.id.tv_tab_privatekey);
        mTvKeyStore = (TextView) findViewById(R.id.tv_tab_keystore);
        mTvMnemonic = (TextView) findViewById(R.id.tv_tab_mnemonic);

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
            case MNEMONIC_INDEX:
                mTvMnemonic.setSelected(true);
                break;
        }
    }

    private void resetTab() {
        mTvPrivateKey.setSelected(false);
        mTvKeyStore.setSelected(false);
        mTvMnemonic.setSelected(false);
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
