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
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

public class DoughnutHeaderView extends InternalAbstract {

    public static String REFRESH_HEADER_PULLING = "下拉可以刷新";//"下拉可以刷新";
    public static String REFRESH_HEADER_LOADING = "正在加载...";//"正在加载...";
    public static String REFRESH_HEADER_RELEASE = "释放立即刷新";
    public static String REFRESH_HEADER_FINISH = "刷新成功";//"刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";//"刷新失败";
    
    private TextView mTitleText;
    private ImageView mAnimationImg;
    private AnimationDrawable mAnimationDrawable;

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
        mAnimationDrawable.setOneShot(false);
        mAnimationImg.setImageDrawable(mAnimationDrawable);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (success) {
            mTitleText.setText(REFRESH_HEADER_FINISH);
        } else {
            mTitleText.setText(REFRESH_HEADER_FAILED);
        }
        super.onFinish(layout, success);
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
            case PullDownToRefresh: //下拉过程
                mAnimationDrawable.start();
                mTitleText.setText(REFRESH_HEADER_PULLING);
                break;
            case ReleaseToRefresh: //松开刷新
                mTitleText.setText(REFRESH_HEADER_RELEASE);
                break;
            case Refreshing: //loading中
                mTitleText.setText(REFRESH_HEADER_LOADING);
                break;
        }
    }

    /**
     * 开始
     */
    protected void start() {
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
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
