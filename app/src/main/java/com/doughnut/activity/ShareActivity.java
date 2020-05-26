package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.doughnut.utils.GsonUtil;
import com.doughnut.view.TitleBar;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener {

    private TitleBar mTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
    }

    @Override
    public void onClick(View view) {
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
        share();
    }

    public static void startAboutActivity(Context from) {
        Intent intent = new Intent(from, ShareActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    private void share() {
        OnekeyShare oks = new OnekeyShare();
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("甜甜圈数字钱包");
        // titleUrl QQ和QQ空间跳转链接
//        oks.setTitleUrl(mUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("甜甜圈，圈住你的SWTC");
        // imagePath是图片的本地路径，确保SDcard下面存在此张图片
//                oks.setImagePath(mImgUrl);
        Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_qr);
        oks.setImageData(thumbBmp);
        // url在微信、Facebook等平台中使用
//        oks.setUrl(mUrl);
        // 启动分享GUI
        oks.show(getApplicationContext());
    }
}
