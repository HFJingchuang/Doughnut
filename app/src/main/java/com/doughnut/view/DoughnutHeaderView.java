package com.doughnut.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

public class DoughnutHeaderView extends InternalAbstract implements RefreshHeader {

    private TextView mTitleText;
    private ImageView mAnimationImg;
    private AnimationDrawable mAnimationDrawable;
    private AnimationDrawable mAnimationDrawableLoad;

    protected DoughnutHeaderView(@NonNull View wrapped) {
        super(wrapped);
    }

    public DoughnutHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected DoughnutHeaderView(@NonNull View wrappedView, @Nullable RefreshInternal wrappedInternal) {
        super(wrappedView, wrappedInternal);
    }

    protected DoughnutHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.from(context).inflate(R.layout.activity_header, this);
        mTitleText = view.findViewById(R.id.tv_status);
        mAnimationImg = (ImageView) view.findViewById(R.id.img_header);
        mAnimationDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.anim_loading);
        mAnimationDrawableLoad = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.anim_refresh);
        mAnimationImg.setImageDrawable(mAnimationDrawable);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (success) {
            mTitleText.setText(getResources().getString(R.string.srl_header_finish));
        } else {
            mTitleText.setText(getResources().getString(R.string.srl_header_failed));
        }
        super.onFinish(layout, success);
        mAnimationDrawableLoad.stop();
        return 500; //延迟500毫秒之后再弹回
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight);
        //超时关闭
        AppConfig.postDelayOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (refreshLayout.getState()) {
                    case Refreshing:
                        onFinish(refreshLayout, false);
                        break;
                }
            }
        }, 15000);
        start();
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh: //下拉过程
                start();
                mTitleText.setText(getResources().getString(R.string.srl_header_pulling));
                break;
            case ReleaseToRefresh: //松开刷新
                mTitleText.setText(getResources().getString(R.string.srl_header_release));
                break;
            case Refreshing: //loading中
                stop();
                mAnimationImg.setImageDrawable(mAnimationDrawableLoad);
                mAnimationDrawableLoad.start();
                mTitleText.setText(getResources().getString(R.string.srl_header_refreshing));
                break;
        }
    }

    /**
     * 开始
     */
    protected void start() {
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationImg.setImageDrawable(mAnimationDrawable);
            mAnimationDrawable.start();
        }
    }

    /**
     * 结束
     */
    protected void stop() {
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }
}
