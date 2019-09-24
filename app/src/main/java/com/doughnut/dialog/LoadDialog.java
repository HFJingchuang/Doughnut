package com.doughnut.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.update.APKDownLoad;


public class LoadDialog extends BaseDialog {

    private ImageView mImgLoad;
    private TextView mTvTilte;
    private AnimationDrawable mAnimationDrawable;
    private Context mContext;
    private String mTitle;

    public LoadDialog(@NonNull Context context, String title) {
        super(context, R.style.DialogStyle);
        mContext = context;
        mTitle = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_load);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setDimAmount(0f);
        initView();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mAnimationDrawable.stop();
    }

    @Override
    public void show() {
        super.show();
        mAnimationDrawable.start();
    }

    private void initView() {
        mImgLoad = (ImageView) findViewById(R.id.img_loading);
        mAnimationDrawable = (AnimationDrawable) ContextCompat.getDrawable(mContext, R.drawable.anim_refresh);
        mImgLoad.setImageDrawable(mAnimationDrawable);
        mTvTilte = findViewById(R.id.tv_title);
        mTvTilte.setText(mTitle);
    }
}
