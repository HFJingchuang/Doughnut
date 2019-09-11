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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.fragment.MainUserFragment;
import com.doughnut.fragment.MainWalletFragment;


public class MainActivity extends BaseActivity implements View.OnClickListener {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mMainViewPager.setCurrentItem(getIntent().getIntExtra(Constant.PAGE_INDEX, 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutTabWallet) {
            mMainViewPager.setCurrentItem(WALLET_INDEX);
        } else if (view == mLayoutTabMine) {
            mMainViewPager.setCurrentItem(MINE_INDEX);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
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
        initViewPager();
    }

    private void initViewPager() {
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
        }
    }

    private void resetTab() {
        mImgWallet.setImageResource(R.drawable.ic_wallet_noclick);
        mTvWallet.setSelected(false);

        mImgMine.setImageResource(R.drawable.ic_myself_noclick);
        mTvMine.setSelected(false);
    }

    class MainViewPagerAdapter extends FragmentPagerAdapter {

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private Fragment[] mFragments = new Fragment[]{
                MainWalletFragment.newInstance(),
                MainUserFragment.newInstance()
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
