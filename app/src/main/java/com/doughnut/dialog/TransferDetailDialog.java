package com.doughnut.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;


public class TransferDetailDialog extends BaseDialog implements View.OnClickListener {

    public interface OnListener {
        void onBack();
    }

    private ImageView mImgClose;
    private TextView mTvFrom, mTvTo, mTvValue, mTvGas, mTvMemo;
    private String mFrom, mTo, mMemo;
    private CharSequence mValue, mGas;
    private TextView mTvOk;
    private OnListener mOnListener;

    public TransferDetailDialog(@NonNull Context context, OnListener onListener) {
        super(context, R.style.DialogStyle);
        mOnListener = onListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_transfer_detail);
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvOk) {
            if (mOnListener != null) {
                mOnListener.onBack();
                dismiss();
            }
        } else if (view == mImgClose) {
            dismiss();
        }
    }

    private void initView() {
        mImgClose = (ImageView) findViewById(R.id.img_close);
        mImgClose.setOnClickListener(this);
        mTvFrom = findViewById(R.id.tv_from);
        mTvTo = findViewById(R.id.tv_to);
        mTvValue = findViewById(R.id.tv_value);
        mTvGas = findViewById(R.id.tv_gas);
        mTvMemo = findViewById(R.id.tv_memo);
        mTvOk = (TextView) findViewById(R.id.tv_ok);
        mTvOk.setOnClickListener(this);
        initData();
    }

    private void initData() {
        mTvFrom.setText(mFrom);
        mTvTo.setText(mTo);
        mTvValue.setText(mValue);
        mTvGas.setText(mGas);
        mTvMemo.setText(mMemo);
    }


    public TransferDetailDialog setFrom(String from) {
        this.mFrom = from;
        return this;
    }

    public TransferDetailDialog setTo(String to) {
        this.mTo = to;
        return this;
    }

    public TransferDetailDialog setValue(CharSequence value) {
        this.mValue = value;
        return this;
    }

    public TransferDetailDialog setGas(CharSequence gas) {
        this.mGas = gas + " SWTC";
        return this;
    }

    public TransferDetailDialog setMemo(String memo) {
        this.mMemo = memo;
        return this;
    }
}
