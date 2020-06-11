package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.update.UpdateTask;
import com.doughnut.utils.DeviceUtil;
import com.doughnut.view.TitleBar;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AboutActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;
    private TextView mTvVersion;
    private ImageView mImgLoad;
    private RelativeLayout mLayoutCheckUpdate;
    private ProgressDrawable mProgressDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutCheckUpdate) {
//            String appId = "wxe56d3277ffedce24"; // 填应用AppId
//            IWXAPI api = WXAPIFactory.createWXAPI(this, appId);
//
//            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//            req.userName = "gh_0a250eabeb78"; // 填小程序原始id
//            req.path = "pages/home/home?coinName=SWTC";                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
//            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;// 可选打开 开发版，体验版和正式版
//            api.sendReq(req);
            mProgressDrawable.start();
            mImgLoad.setVisibility(View.VISIBLE);
            new UpdateTask().execute();
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

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_about));
        mTitleBar.setTitleTextColor(R.color.color_detail_address);
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mTvVersion.setText(getString(R.string.content_version) + DeviceUtil.getVersionName());

        mLayoutCheckUpdate = (RelativeLayout) findViewById(R.id.layout_check_update);
        mLayoutCheckUpdate.setOnClickListener(this);
        mImgLoad = findViewById(R.id.img_loading);
        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(0xff666666);
        mImgLoad.setImageDrawable(mProgressDrawable);
        mImgLoad.setVisibility(View.GONE);

    }

    public static void startAboutActivity(Context from) {
        Intent intent = new Intent(from, AboutActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String str) {
        switch (str) {
            case "LATEST":
                new MsgDialog(this, getResources().getString(R.string.toast_latest_version)).show();
                break;
            case "UPDATE_FAIL":
                new MsgDialog(this, getResources().getString(R.string.dialog_update_fail)).setIsHook(false).show();
            case "CHECK_FINISH":
                mProgressDrawable.stop();
                mImgLoad.setVisibility(View.GONE);
                break;
        }
    }
}
