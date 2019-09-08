package com.doughnut.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class DoughnutRefreshLayout extends SmartRefreshLayout {

    DoughnutHeaderView mHeaderView;

    public DoughnutRefreshLayout(Context context) {
        this(context, null);
    }

    public DoughnutRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoughnutRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeaderView = new DoughnutHeaderView(context, attrs, defStyleAttr);
        mHeaderView.setLayoutParams(layoutParams);
        addView(mHeaderView, 0);
    }
}