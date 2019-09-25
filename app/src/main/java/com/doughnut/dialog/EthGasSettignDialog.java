package com.doughnut.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.doughnut.R;

import java.math.BigDecimal;


public class EthGasSettignDialog extends BaseDialog implements View.OnClickListener {

    private final static String GAS1 = "0.00001";
    private final static String GAS2 = "0.001";
    private final static String GAS3 = "0.01";

    public interface OnSettingGasListener {
        void onSettingGas(String gasPrice);
    }

    private ImageView mImgClose;
    private SeekBar mSeekBarGas;
    private TextView mTvGas, mTvGas1, mTvGas2, mTvGas3;
    private TextView mTvOk;
    private OnSettingGasListener mOnsettingGasListener;
    private String mGasPrice;

    public EthGasSettignDialog(@NonNull Context context, OnSettingGasListener onSettingGasListener, String gasPrice) {
        super(context, R.style.DialogStyle);
        mOnsettingGasListener = onSettingGasListener;
        mGasPrice = gasPrice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_gas);
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
            if (mOnsettingGasListener != null) {
                mOnsettingGasListener.onSettingGas(mTvGas.getText().toString());
                dismiss();
            }
        } else if (view == mImgClose) {
            dismiss();
        } else if (view == mTvGas1) {
            mTvGas1.setActivated(true);
            mTvGas1.setTextColor(Color.WHITE);
            mTvGas2.setActivated(false);
            mTvGas2.setTextColor(Color.BLACK);
            mTvGas3.setActivated(false);
            mTvGas3.setTextColor(Color.BLACK);
            mTvGas.setText(GAS1);
            mSeekBarGas.setProgress(0);
        } else if (view == mTvGas2) {
            mTvGas1.setActivated(false);
            mTvGas1.setTextColor(Color.BLACK);
            mTvGas2.setActivated(true);
            mTvGas2.setTextColor(Color.WHITE);
            mTvGas3.setActivated(false);
            mTvGas3.setTextColor(Color.BLACK);
            mTvGas.setText(GAS2);
            mSeekBarGas.setProgress(0);
        } else if (view == mTvGas3) {
            mTvGas1.setActivated(false);
            mTvGas1.setTextColor(Color.BLACK);
            mTvGas2.setActivated(false);
            mTvGas2.setTextColor(Color.BLACK);
            mTvGas3.setActivated(true);
            mTvGas3.setTextColor(Color.WHITE);
            mTvGas.setText(GAS3);
            mSeekBarGas.setProgress(1);
        }
    }

    private void initView() {
        mImgClose = (ImageView) findViewById(R.id.img_close);
        mImgClose.setOnClickListener(this);
        mTvGas1 = findViewById(R.id.tv_gas1);
        mTvGas1.setOnClickListener(this);
        mTvGas2 = findViewById(R.id.tv_gas2);
        mTvGas2.setOnClickListener(this);
        mTvGas3 = findViewById(R.id.tv_gas3);
        mTvGas3.setOnClickListener(this);
        mTvGas = (TextView) findViewById(R.id.tv_gascount_intoken);
        mTvGas.setText(mGasPrice);
        mSeekBarGas = (SeekBar) findViewById(R.id.seekbar_gas);
        mSeekBarGas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("seekBar", progress + "");
                if (progress > 1) {
                    mTvGas1.setActivated(false);
                    mTvGas1.setTextColor(Color.BLACK);
                    mTvGas2.setActivated(false);
                    mTvGas2.setTextColor(Color.BLACK);
                    mTvGas3.setActivated(false);
                    mTvGas3.setTextColor(Color.BLACK);
                    String fee = new BigDecimal(progress).divide(new BigDecimal(100)).stripTrailingZeros().toPlainString();
                    mTvGas.setText(fee);
                } else if (progress == 1) {
                    mTvGas3.setActivated(true);
                    mTvGas3.setTextColor(Color.WHITE);
                    mTvGas.setText(GAS3);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBarGas.setMax(100);
        mTvOk = (TextView) findViewById(R.id.tv_ok);
        mTvOk.setOnClickListener(this);
        initGas();
    }

    private void initGas() {
        if (TextUtils.equals(GAS1, mGasPrice)) {
            mTvGas1.setActivated(true);
            mTvGas1.setTextColor(Color.WHITE);
            mTvGas.setText(GAS1);
        } else if (TextUtils.equals(GAS2, mGasPrice)) {
            mTvGas2.setActivated(true);
            mTvGas2.setTextColor(Color.WHITE);
            mTvGas.setText(GAS2);
        } else if (TextUtils.equals(GAS3, mGasPrice)) {
            mTvGas3.setActivated(true);
            mTvGas3.setTextColor(Color.WHITE);
            mTvGas.setText(GAS3);
        }
        int fee = new BigDecimal(mGasPrice).multiply(new BigDecimal(100)).intValue();
        mSeekBarGas.setProgress(fee);
    }
}
