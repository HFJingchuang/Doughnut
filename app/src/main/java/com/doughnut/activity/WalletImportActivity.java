package com.doughnut.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.fragment.KeyStoreImpFragment;
import com.doughnut.fragment.PrivateKeyImpFragment;
import com.doughnut.view.TitleBar;
import com.zxing.activity.CaptureActivity;


public class WalletImportActivity extends BaseActivity implements View.OnClickListener {

    private final static int PRIVATEKEY_INDEX = 0;
    private final static int KEYSTORE_INDEX = 1;
    private ViewPager mMainViewPager;

    //tab
    private LinearLayout mLayoutTabPrivateKey;
    private LinearLayout mLayoutTabKeyStore;

    private TextView mTvPrivateKey;

    private EditText mEditPrivateKey;

    private TextView mTvKeyStore;
    private TitleBar mTitleBar;

    private String selectedTab;

    private Context mContext;

    private Integer SCAN_CODE=101;

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
        if (view == mLayoutTabPrivateKey) {
            mMainViewPager.setCurrentItem(PRIVATEKEY_INDEX);
        } else if (view == mLayoutTabKeyStore) {
            mMainViewPager.setCurrentItem(KEYSTORE_INDEX);
        }

    }

    public static void startImportWalletActivity(Context from) {
        Intent intent = new Intent(from, WalletImportActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
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
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });
        mTitleBar.setTitle(R.string.titleBar_import_wallet);
        mTitleBar.setTitleTextColor(R.color.color_currency_name);
        mTitleBar.setRightDrawable(R.drawable.ic_scan);
//        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
//            @Override
//            public void onRightClick(View view) {
//                Intent intent = new Intent(WalletImportActivity.this, CaptureActivity.class);
//                startActivityForResult(intent, SCAN_CODE);
//
//            }
//        });
        //tab
        mLayoutTabPrivateKey = (LinearLayout) findViewById(R.id.layout_tab_privatekey);
        mLayoutTabKeyStore = (LinearLayout) findViewById(R.id.layout_tab_keystore);
        mLayoutTabPrivateKey.setOnClickListener(this);
        mLayoutTabKeyStore.setOnClickListener(this);


        mEditPrivateKey = findViewById(R.id.edt_private_key);
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

        mMainViewPager.setAdapter(new WalletImportActivity.MainViewPagerAdapter(getSupportFragmentManager()));
        pageSelected(PRIVATEKEY_INDEX);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            String result = data.getStringExtra("scan_result");
//            if (!result.isEmpty()) {
//                mEditPrivateKey.setText(result);
//            }
//        }
//    }

    private void pageSelected(int position) {
        resetTab();
        switch (position) {
            case PRIVATEKEY_INDEX:
                mTvPrivateKey.setSelected(true);
                selectedTab = "private";
                break;
            case KEYSTORE_INDEX:
                mTvKeyStore.setSelected(true);
                selectedTab = "keystore";
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

        private Fragment[] mFragments = new Fragment[]{
                PrivateKeyImpFragment.newInstance(),
                KeyStoreImpFragment.newInstance()

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
