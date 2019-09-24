package com.doughnut.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.doughnut.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义节点Dialog
 */
public class NodeCustomDialog extends BaseDialog implements View.OnClickListener {

    private EditText mEdtNode;
    private TextView mTvErr;
    private TextView mTvCancel;
    private TextView mTvConfirm;

    public interface onConfirmOrderListener {
        void onConfirmOrder();
    }

    private onConfirmOrderListener mOnConfirmOrderListener;

    public NodeCustomDialog(@NonNull Context context, onConfirmOrderListener onConfirmOrderListener) {
        super(context, R.style.DialogStyle);
        mOnConfirmOrderListener = onConfirmOrderListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_custom_node);
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

    private void initView() {
        mEdtNode = findViewById(R.id.edt_node);
        mEdtNode.requestFocus();
        mTvErr = findViewById(R.id.tv_err);
        mEdtNode.addTextChangedListener(new TextWatcher() {
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
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mTvCancel) {
            dismiss();
        } else if (v == mTvConfirm) {
            String node = mEdtNode.getText().toString();
            if (!TextUtils.isEmpty(node) && (node.startsWith("ws://") || node.startsWith("wss://"))) {
                String[] ws = node.replace("ws://", "").replace("wss://", "").split(":");
                if (ws.length == 2) {
                    String fileName = getContext().getPackageName() + "_customNode";
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String nodes = sharedPreferences.getString("nodes", "");
                    List<String> nodeList;
                    if (TextUtils.isEmpty(nodes)) {
                        nodeList = new ArrayList();
                    } else {
                        if (nodes.contains(",")) {
                            List<String> arrList = Arrays.asList(nodes.split(","));
                            nodeList = new ArrayList(arrList);
                        } else {
                            nodeList = new ArrayList();
                            nodeList.add(nodes);
                        }
                    }
                    if (!nodeList.contains(node)) {
                        nodeList.add(node);
                        editor.putString("nodes", nodeList.toString().replace("[", "").replace("]", "").replace(" ", ""));
                        editor.apply();
                    }
                    mOnConfirmOrderListener.onConfirmOrder();
                    dismiss();
                    return;
                }
            }
            mEdtNode.setText("");
            mTvErr.setVisibility(View.VISIBLE);
        }
    }
}
