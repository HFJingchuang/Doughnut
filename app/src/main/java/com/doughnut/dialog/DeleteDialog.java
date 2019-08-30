package com.doughnut.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.utils.TLog;


public class DeleteDialog extends Dialog implements View.OnClickListener {

    private final static String TAG = "DeleteDialog";
    private TextView mTvCancel;
    private TextView mTvOk;
    private DeleteDialog mPwdResult;


    public interface DeleteResult {
        void authPwd(String tag, boolean result, String key);
    }

    public DeleteDialog(@NonNull Context context) {
        super(context, R.style.DialogStyle);
//        this.mPwdResult = authPwdListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_delete);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -2;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
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
            dismiss();
        }
    }

    private void initView() {
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvOk = (TextView) findViewById(R.id.tv_ok);
        mTvOk.setOnClickListener(this);
    }

}
