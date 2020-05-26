package com.doughnut.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.activity.TokenTransferActivity;
import com.doughnut.config.AppConfig;
import com.doughnut.utils.AESUtil;
import com.doughnut.utils.TLog;
import com.doughnut.view.SubCharSequence;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

public class EditDialog extends Dialog implements View.OnClickListener {

    private final static String TAG = "EditDialog";
    private EditText mEdtPw;
    private TextView mTvTitle;
    private TextView mTvCancel;
    private TextView mTvOk;
    private TextView mTvErr;
    private TextView mTvTips;
    private String mAddress;
    private int mDialogTitle;
    private int mDialogHint;
    private int mDialogCancelText;
    private int mDialogCancelColor;
    private int mDialogConfirmText;
    private int mDialogConfirmColor;
    private boolean mIsDeleteWallet = false;
    private boolean mIsVerifyPwd = true;
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
        void authPwd(boolean result, String key, String mnemonics);
    }

    public EditDialog(@NonNull Context context, String address) {
        super(context, R.style.DialogStyle);
        this.mAddress = address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_pwd);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvCancel) {
            dismiss();
            mPwdResultListener.authPwd(false, "", "");
        } else if (view == mTvOk) {
            mTvOk.setClickable(false);
            if (mPwdResultListener == null) {
                TLog.e(TAG, "回掉接口空");
                dismiss();
                return;
            }

            String input = mEdtPw.getText().toString();
            if (TextUtils.isEmpty(input)) {
                return;
            }

            if (!mIsVerifyPwd) {
                mPwdResultListener.authPwd(true, input, "");
                dismiss();
                return;
            }
            String keyStore = WalletSp.getInstance(AppConfig.getContext(), mAddress).getKeyStore();
            String enMnemonics = WalletSp.getInstance(AppConfig.getContext(), mAddress).getMnemonics();
            WalletManager.getInstance(getContext()).getPrivateKey(input, keyStore, new ICallBack() {
                @Override
                public void onResponse(Object response) {
                    String oldKey = (String) response;
                    if (TextUtils.isEmpty(oldKey)) {
                        mPwdResultListener.authPwd(false, "", "");
                        mEdtPw.setText("");
                        mTvErr.setVisibility(View.VISIBLE);
                    } else {
                        String mnemonics = AESUtil.decrypt(input, enMnemonics);
                        mPwdResultListener.authPwd(true, oldKey, mnemonics);
                        dismiss();
                    }
                }
            });
        }
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvOk = (TextView) findViewById(R.id.tv_confirm);
        mTvOk.setOnClickListener(this);
        mEdtPw = (EditText) findViewById(R.id.edt_pwd);
        mEdtPw.requestFocus();
        if (mIsVerifyPwd) {
            mEdtPw.setTransformationMethod(transformationMethod);
        } else {
            mEdtPw.setInputType(InputType.TYPE_CLASS_TEXT);
        }
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
        if (mIsDeleteWallet) {
            mTvTips.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        if (mDialogTitle != 0) {
            this.mTvTitle.setText(mDialogTitle);
        }
        if (mDialogHint != 0) {
            this.mEdtPw.setHint(mDialogHint);
        }
        if (mDialogCancelText != 0) {
            this.mTvCancel.setText(mDialogCancelText);
        }
        if (mDialogCancelColor != 0) {
            this.mTvCancel.setTextColor(mDialogCancelColor);
        }
        if (mDialogConfirmText != 0) {
            this.mTvOk.setText(mDialogConfirmText);
        }
        if (mDialogConfirmColor != 0) {
            this.mTvOk.setTextColor(mDialogConfirmColor);
        }
    }

    /**
     * Dialog的标题
     *
     * @param id
     * @return
     */
    public EditDialog setDialogTitle(int id) {
        this.mDialogTitle = id;
        return this;
    }

    /**
     * 输入框的提示文字
     *
     * @param id
     * @return
     */
    public EditDialog setDialogHint(int id) {
        this.mDialogHint = id;
        return this;
    }

    /**
     * 取消按钮的文字
     *
     * @param id
     * @return
     */
    public EditDialog setDialogCancelText(int id) {
        this.mDialogCancelText = id;
        return this;
    }

    /**
     * 取下按钮的颜色
     *
     * @param id
     * @return
     */
    public EditDialog setDialogCancelColor(int id) {
        this.mDialogCancelColor = id;
        return this;
    }

    /**
     * 确认按钮的文字
     *
     * @param id
     * @return
     */
    public EditDialog setDialogConfirmText(int id) {
        this.mDialogConfirmText = id;
        return this;
    }

    /**
     * 确认按钮的颜色
     *
     * @param id
     * @return
     */
    public EditDialog setDialogConfirmColor(int id) {
        this.mDialogConfirmColor = id;
        return this;
    }

    /**
     * 是否删除钱包操作，默认false；true的情况下Dialog会显示确认备份警示
     *
     * @param isDeleteWallet
     * @return
     */
    public EditDialog setIsDeleteWallet(boolean isDeleteWallet) {
        this.mIsDeleteWallet = isDeleteWallet;
        return this;
    }

    /**
     * 是否需要验证密码，默认true
     *
     * @param isVerifyPwd
     * @return
     */
    public EditDialog setIsVerifyPwd(boolean isVerifyPwd) {
        this.mIsVerifyPwd = isVerifyPwd;
        return this;
    }

    /**
     * 结果回调
     *
     * @param pwdResultListener
     * @return
     */
    public EditDialog setResultListener(PwdResultListener pwdResultListener) {
        this.mPwdResultListener = pwdResultListener;
        return this;
    }

}
