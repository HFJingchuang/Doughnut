package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.LanguageUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletManager;

import java.util.Locale;

public class AddCurrencyActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.content_add_currency_type));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);



    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }


    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void onMiddleClick(View v) {

    }

    public static void startLanguageActivity(Context from) {
        Intent intent = new Intent(from, AddCurrencyActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }


}
