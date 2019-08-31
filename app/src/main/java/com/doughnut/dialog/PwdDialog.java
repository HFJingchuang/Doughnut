package com.doughnut.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.TLog;
import com.doughnut.view.SubCharSequence;
import com.doughnut.wallet.WalletManager;


public class PwdDialog extends Dialog implements View.OnClickListener {

    private final static String TAG = "PwdDialog";
    private EditText mEdtPw;
    private TextView mTvCancel;
    private TextView mTvOk;
    private TextView mTvErr;
    private TextView mTvTips;
    private String mAddress;
    private String mTag;

    private PwdResult mPwdResult;
    private TransformationMethod transformationMethod = new TransformationMethod() {
        @Override
        public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
            // TODO Auto-generated method stub

        }

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            // TODO Auto-generated method stub
            return new SubCharSequence(source);
        }
    };


    public interface PwdResult {
        void authPwd(String tag, boolean result, String key);
    }

    public PwdDialog(@NonNull Context context, PwdResult authPwdListener, String address, String tag) {
        super(context, R.style.DialogStyle);
        this.mPwdResult = authPwdListener;
        this.mAddress = address;
        this.mTag = tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_pwd);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -2;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvCancel) {
            dismiss();
        } else if (view == mTvOk) {
            if (mPwdResult == null) {
                TLog.e(TAG, "回掉接口空");
                dismiss();
                return;
            }
            if (TextUtils.isEmpty(mEdtPw.getText().toString())) {
                mPwdResult.authPwd(mTag, false, "");
            } else {
                String oldKey = WalletManager.getInstance(getContext()).getPrivateKey(mEdtPw.getText().toString(), mAddress);
                if (TextUtils.isEmpty(oldKey)) {
                    mPwdResult.authPwd(mTag, false, "");
                    mEdtPw.setText("");
                    mTvErr.setVisibility(View.VISIBLE);
                } else {
                    mPwdResult.authPwd(mTag, true, mEdtPw.getText().toString());
                    dismiss();
                }
            }
        }
    }

    private void initView() {
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvOk = (TextView) findViewById(R.id.tv_confirm);
        mTvOk.setOnClickListener(this);
        mEdtPw = (EditText) findViewById(R.id.edt_pwd);
        mEdtPw.setTransformationMethod(transformationMethod);
        mEdtPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && mTvErr.isShown()) {
                    mTvErr.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTvErr = (TextView) findViewById(R.id.tv_err);
        mTvTips = (TextView) findViewById(R.id.tv_tips);
        if (TextUtils.equals(mTag, Constant.WALLET_DEL)) {
            mTvTips.setVisibility(View.VISIBLE);
        } else {
            mTvOk.setText(R.string.dialog_btn_confirm);
            mTvOk.setTextColor(getContext().getResources().getColor(R.color.color_dialog_confirm));
        }
    }

}
