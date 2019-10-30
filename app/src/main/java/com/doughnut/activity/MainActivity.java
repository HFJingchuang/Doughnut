package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.dialog.ImportSuccessDialog;
import com.doughnut.fragment.DappFragment;
import com.doughnut.fragment.MainUserFragment;
import com.doughnut.fragment.MainWalletFragment;
import com.doughnut.fragment.NoWalletFragment;
import com.doughnut.update.UpdateTask;
import com.doughnut.wallet.WalletManager;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final static int WALLET_INDEX = 0;
    private final static int DAPP_INDEX = 1;
    private final static int MINE_INDEX = 2;
    private ViewPager mMainViewPager;

    //tab
    private LinearLayout mLayoutTabWallet;
    private LinearLayout mLayoutTabMine;

    private ImageView mImgWallet;
    private TextView mTvWallet;

    private ImageView mImgMine;
    private TextView mTvMine;

    private ImageView mIvDapp;
    private TextView mTvDapp;

    private Fragment[] mFragments;
    private LinearLayout mLl_tab_dapp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UpdateTask().execute();
        hasWallet();
        initView();
        if (getIntent() != null) {
            boolean isImport = getIntent().getBooleanExtra(Constant.IMPORT_FLAG, false);
            if (isImport) {
                String walletName = getIntent().getStringExtra(Constant.WALLET_NAME);
                ImportSuccessDialog importSuccessDialog = new ImportSuccessDialog(this, walletName);
                importSuccessDialog.show();
                getIntent().removeExtra(Constant.IMPORT_FLAG);
            }
            if (getIntent().hasExtra(Constant.PAGE_INDEX)) {
                mMainViewPager.setCurrentItem(getIntent().getIntExtra(Constant.PAGE_INDEX, 0));
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        if (getIntent() != null) {
            boolean isImport = getIntent().getBooleanExtra(Constant.IMPORT_FLAG, false);
            if (isImport) {
                String walletName = getIntent().getStringExtra(Constant.WALLET_NAME);
                ImportSuccessDialog importSuccessDialog = new ImportSuccessDialog(this, walletName);
                importSuccessDialog.show();
                getIntent().removeExtra(Constant.IMPORT_FLAG);
            }
            if (getIntent().hasExtra(Constant.PAGE_INDEX)) {
                mMainViewPager.setCurrentItem(getIntent().getIntExtra(Constant.PAGE_INDEX, 0));
                getIntent().removeExtra(Constant.PAGE_INDEX);
            }
        }
        int index = mMainViewPager.getCurrentItem();
        judgeState();
        mMainViewPager.setCurrentItem(index);
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.layout_tab_wallet) {
            mMainViewPager.setCurrentItem(WALLET_INDEX);
        } else if (id == R.id.layout_tab_mine) {
            mMainViewPager.setCurrentItem(MINE_INDEX);
        } else if (id == R.id.layout_tab_dapp) {
            mMainViewPager.setCurrentItem(DAPP_INDEX);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startMainActivityForIndex(Context context, int pageIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.PAGE_INDEX, pageIndex);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {
        //tab
        mLayoutTabWallet = (LinearLayout) findViewById(R.id.layout_tab_wallet);
        mLayoutTabMine = (LinearLayout) findViewById(R.id.layout_tab_mine);
        mLl_tab_dapp = findViewById(R.id.layout_tab_dapp);
        mLl_tab_dapp.setOnClickListener(this);
        mLayoutTabWallet.setOnClickListener(this);
        mLayoutTabMine.setOnClickListener(this);


        mImgWallet = (ImageView) findViewById(R.id.img_tab_wallet);
        mTvWallet = (TextView) findViewById(R.id.tv_tab_wallet);
        mImgMine = (ImageView) findViewById(R.id.img_tab_mine);
        mTvMine = (TextView) findViewById(R.id.tv_tab_mine);
        mIvDapp = (ImageView) findViewById(R.id.img_tab_dapp);
        mTvDapp = (TextView) findViewById(R.id.tv_tab_dapp);

        mMainViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mMainViewPager.setOffscreenPageLimit(2);
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

        mMainViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        pageSelected(WALLET_INDEX);
    }


    private void pageSelected(int position) {
        resetTab();
        switch (position) {
            case WALLET_INDEX:
                mImgWallet.setImageResource(R.drawable.ic_wallet_click);
                mTvWallet.setSelected(true);
                break;
            case MINE_INDEX:
                mImgMine.setImageResource(R.drawable.ic_myself_click);
                mTvMine.setSelected(true);
                break;
            case DAPP_INDEX:
                mIvDapp.setImageResource(R.drawable.ic_wallet_click);
                mTvDapp.setSelected(true);
                break;
        }
    }

    private void resetTab() {
        mImgWallet.setImageResource(R.drawable.ic_wallet_noclick);
        mTvWallet.setSelected(false);

        mImgMine.setImageResource(R.drawable.ic_myself_noclick);
        mTvMine.setSelected(false);

        mIvDapp.setImageResource(R.drawable.ic_wallet_noclick);
        mTvDapp.setSelected(false);
    }

    class MainViewPagerAdapter extends FragmentStatePagerAdapter {

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
            notifyDataSetChanged();
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

    private void hasWallet() {
        if (!WalletManager.getInstance(this).hasWallet()) {
            mFragments = new Fragment[]{
                    NoWalletFragment.newInstance(),
                    DappFragment.newInstance(),
                    MainUserFragment.newInstance()
            };
        } else {
            mFragments = new Fragment[]{
                    MainWalletFragment.newInstance(),
                    DappFragment.newInstance(),
                    MainUserFragment.newInstance()
            };
        }
    }

    /**
     * 保存App钱包有无状态
     *
     * @param state
     */
    private void saveWalletState(boolean state) {
        String fileName = getPackageName() + "_wallet_state";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", state);
        editor.apply();
    }

    /**
     * 判断是否要刷新ViewPager内容
     */
    private void judgeState() {
        String fileName = getPackageName() + "_wallet_state";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        boolean state = sharedPreferences.getBoolean("state", false);
        boolean isHasWallet = WalletManager.getInstance(this).hasWallet();
        if (!state && isHasWallet) {
            mFragments = new Fragment[]{
                    MainWalletFragment.newInstance(),
                    MainUserFragment.newInstance()
            };
            saveWalletState(isHasWallet);
            if (mMainViewPager != null) {
                mMainViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
            }
        } else if (state && !isHasWallet) {
            mFragments = new Fragment[]{
                    NoWalletFragment.newInstance(),
                    MainUserFragment.newInstance()
            };
            saveWalletState(isHasWallet);
            if (mMainViewPager != null) {
                mMainViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
            }
        }
    }
}
