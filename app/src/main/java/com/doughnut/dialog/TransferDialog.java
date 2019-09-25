package com.doughnut.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.utils.AESUtil;
import com.doughnut.view.SubCharSequence;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

public class TransferDialog extends Dialog implements View.OnClickListener {

    private EditText mEdtPw;
    private TextView mTvCancel;
    private TextView mTvOk;
    private TextView mTvErr;
    private LinearLayout mLayoutNoPwd;
    private RadioButton mRadNoPwd;
    private String mAddress;
    private Context mContext;

    private PwdResultListener mPwdResultListener;
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


    public interface PwdResultListener {
        void authPwd(boolean result, String key);
    }

    public TransferDialog(@NonNull Context context, String address) {
        super(context, R.style.DialogStyle);
        this.mContext = context;
        this.mAddress = address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_transfer);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvCancel) {
            dismiss();
            mPwdResultListener.authPwd(false, "");
        } else if (view == mTvOk) {
            if (mPwdResultListener == null) {
                dismiss();
                return;
            }

            String input = mEdtPw.getText().toString();
            if (TextUtils.isEmpty(input)) {
                return;
            }
            String keyStore = WalletSp.getInstance(mContext, mAddress).getKeyStore();
            WalletManager.getInstance(getContext()).getPrivateKey(input, keyStore, new ICallBack() {
                @Override
                public void onResponse(Object response) {
                    String oldKey = (String) response;
                    if (TextUtils.isEmpty(oldKey)) {
                        mPwdResultListener.authPwd(false, "");
                        mEdtPw.setText("");
                        mTvErr.setVisibility(View.VISIBLE);
                    } else {
                        savePwd();
                        dismiss();
                        mPwdResultListener.authPwd(true, oldKey);
                    }
                }
            });
        } else if (view == mLayoutNoPwd) {
            mRadNoPwd.setChecked(!mRadNoPwd.isChecked());
        }
    }

    private void initView() {
        mLayoutNoPwd = findViewById(R.id.layout_no_pwd);
        mLayoutNoPwd.setOnClickListener(this);
        mRadNoPwd = findViewById(R.id.radio_no_pwd);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvOk = (TextView) findViewById(R.id.tv_confirm);
        mTvOk.setOnClickListener(this);
        mEdtPw = (EditText) findViewById(R.id.edt_pwd);
        mEdtPw.setTransformationMethod(transformationMethod);
        mEdtPw.requestFocus();
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
    }

    /**
     * 结果回调
     *
     * @param pwdResultListener
     * @return
     */
    public TransferDialog setResultListener(PwdResultListener pwdResultListener) {
        this.mPwdResultListener = pwdResultListener;
        return this;
    }

    /**
     * 密码再加密后本地保存
     */
    private void savePwd() {
        String pwd = mEdtPw.getText().toString();
        if (!TextUtils.isEmpty(pwd) && mRadNoPwd.isChecked()) {
            String fileName = mContext.getPackageName() + "_pwd_" + mAddress;
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            long now = System.currentTimeMillis();
            String key = AESUtil.generateKey();
            AESUtil.encrypt(key, pwd, new ICallBack() {
                @Override
                public void onResponse(Object response) {
                    String encrypt = (String) response;
                    editor.putString("key", key);
                    editor.putString("encrypt", encrypt);
                    editor.putLong("time", now);
                    editor.apply();
                }
            });
        }
    }
}
