package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.doughnut.R;
import com.doughnut.utils.LanguageUtil;
import com.doughnut.view.TitleBar;

import java.util.Locale;

public class LanguageActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;

    private RelativeLayout mLayoutLanguageZh;
    private RelativeLayout mLayoutLanguageEn;
    private ImageView mImageChinese;
    private ImageView mImageEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.title_languages));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setTitleBarClickListener(this);

        mLayoutLanguageZh = (RelativeLayout) findViewById(R.id.layout_chinese);
        mImageChinese = (ImageView) mLayoutLanguageZh.findViewById(R.id.img_chinese);
        mLayoutLanguageZh.setOnClickListener(this);
        mLayoutLanguageEn = (RelativeLayout) findViewById(R.id.layout_english);
        mImageEnglish = (ImageView) mLayoutLanguageEn.findViewById(R.id.img_english);
        mLayoutLanguageEn.setOnClickListener(this);

        String select = LanguageUtil.getUserSelect(this);
        imageShow(select);
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutLanguageZh) {
            LanguageUtil.saveUserSelect(this, Locale.CHINESE.getLanguage());
            imageShow(Locale.CHINESE.getLanguage());
            LanguageUtil.saveUserLocale(this, Locale.CHINESE);
        } else if (view == mLayoutLanguageEn) {
            LanguageUtil.saveUserSelect(this, Locale.ENGLISH.getLanguage());
            imageShow(Locale.ENGLISH.getLanguage());
            LanguageUtil.saveUserLocale(this, Locale.ENGLISH);
        }
    }

    @Override
    public void onLeftClick(View v) {
        MainActivity.startMainActivityForIndex(this, 2);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainActivity.startMainActivityForIndex(this, 2);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void onMiddleClick(View v) {

    }

    public static void startLanguageActivity(Context from) {
        Intent intent = new Intent(from, LanguageActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    private void imageShow(String select) {
        switch (select) {
            case "en":
                mImageEnglish.setVisibility(View.VISIBLE);
                mImageChinese.setVisibility(View.GONE);
                break;
            default:
                mImageChinese.setVisibility(View.VISIBLE);
                mImageEnglish.setVisibility(View.GONE);
                break;
        }
    }
}
