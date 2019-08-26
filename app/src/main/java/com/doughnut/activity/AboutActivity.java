package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.update.UpdateTask;
import com.doughnut.utils.DeviceUtil;
import com.doughnut.utils.ToastUtil;
import com.doughnut.view.TitleBar;

public class AboutActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;
    private TextView mTvVersion;

    private RelativeLayout mLayoutUserTerms;
    private RelativeLayout mLayoutPrivliTerms;
    private RelativeLayout mLayoutCheckUpdate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_about));
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mTvVersion.setText(getString(R.string.content_version) + DeviceUtil.getVersionName());

        mLayoutUserTerms = (RelativeLayout) findViewById(R.id.layout_use_terms);
        mLayoutUserTerms.setOnClickListener(this);
        mLayoutPrivliTerms = (RelativeLayout) findViewById(R.id.layout_privil_terms);
        mLayoutPrivliTerms.setOnClickListener(this);
        mLayoutCheckUpdate = (RelativeLayout) findViewById(R.id.layout_check_update);
        mLayoutCheckUpdate.setVisibility(View.GONE);
        mLayoutCheckUpdate.setOnClickListener(this);
        new UpdateTask().execute(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutUserTerms) {
            WebBrowserActivity.startWebBrowserActivity(AboutActivity.this, getString(R.string.titleBar_agreement), Constant.service_term_url);

        } else if (view == mLayoutPrivliTerms) {
            WebBrowserActivity.startWebBrowserActivity(AboutActivity.this, getString(R.string.titleBar_privacy), Constant.privilege_url);

        } else if (view == mLayoutCheckUpdate) {
            ToastUtil.toast(AboutActivity.this, getString(R.string.toast_latest_version));
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

    public static void startAboutActivity(Context from) {
        Intent intent = new Intent(from, AboutActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }
}
