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
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

public class DoughnutFooterView extends InternalAbstract implements RefreshFooter {

    private TextView mTitleText;
    private ImageView mAnimationImg;
    private AnimationDrawable mAnimationDrawable;
    private boolean mNoMoreData = false;

    protected DoughnutFooterView(@NonNull View wrapped) {
        super(wrapped);
    }

    public DoughnutFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected DoughnutFooterView(@NonNull View wrappedView, @Nullable RefreshInternal wrappedInternal) {
        super(wrappedView, wrappedInternal);
    }

    protected DoughnutFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.from(context).inflate(R.layout.activity_footer, this);
        mTitleText = view.findViewById(R.id.tv_status);
        mAnimationImg = (ImageView) view.findViewById(R.id.img_header);
        mAnimationDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.anim_refresh);
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
        stop();
        return 500; //延迟500毫秒之后再弹回
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight);
        start();
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case PullUpToLoad:
                start();
                mTitleText.setText(getResources().getString(R.string.srl_footer_pulling));
                break;
            case LoadReleased:
                mTitleText.setText(getResources().getString(R.string.srl_footer_loading));
                break;
            case Refreshing:
                mTitleText.setText(getResources().getString(R.string.srl_footer_finish));
                break;
        }
    }

    /**
     * 开始
     */
    protected void start() {
        if (!mNoMoreData && mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationImg.setImageDrawable(mAnimationDrawable);
            mAnimationDrawable.start();
        }
    }

    /**
     * 结束
     */
    protected void stop() {
        if (!mNoMoreData && mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            if (noMoreData) {
                mAnimationImg.setVisibility(GONE);
                mTitleText.setText(getResources().getString(R.string.srl_footer_nothing));
            } else {
                mAnimationImg.setVisibility(VISIBLE);
                mTitleText.setText(getResources().getString(R.string.srl_footer_pulling));
            }
        }
        return true;
    }
}
