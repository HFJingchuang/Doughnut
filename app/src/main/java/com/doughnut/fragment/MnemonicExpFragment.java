package com.doughnut.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.dialog.MsgDialog;

public class MnemonicExpFragment extends BaseFragment implements View.OnClickListener {

    private TextView mTvWord1, mTvWord2, mTvWord3, mTvWord4, mTvWord5, mTvWord6, mTvWord7, mTvWord8, mTvWord9, mTvWord10, mTvWord11, mTvWord12;
    private Button mBtnVerify;
    private OnButtonClick onButtonClick;

    public static MnemonicExpFragment newInstance(String privateKey) {
        Bundle args = new Bundle();
        args.putString(Constant.MNEMONICS, privateKey);
        MnemonicExpFragment fragment = new MnemonicExpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mnemonic_exp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    /**
     * 画面初期化
     *
     * @param view
     */
    private void initView(View view) {

        mTvWord1 = view.findViewById(R.id.tv_word1);
        mTvWord2 = view.findViewById(R.id.tv_word2);
        mTvWord3 = view.findViewById(R.id.tv_word3);
        mTvWord4 = view.findViewById(R.id.tv_word4);
        mTvWord5 = view.findViewById(R.id.tv_word5);
        mTvWord6 = view.findViewById(R.id.tv_word6);
        mTvWord7 = view.findViewById(R.id.tv_word7);
        mTvWord8 = view.findViewById(R.id.tv_word8);
        mTvWord9 = view.findViewById(R.id.tv_word9);
        mTvWord10 = view.findViewById(R.id.tv_word10);
        mTvWord11 = view.findViewById(R.id.tv_word11);
        mTvWord12 = view.findViewById(R.id.tv_word12);

        if (getArguments() != null) {
            String mnemonic = getArguments().getString(Constant.MNEMONICS);
            if (!TextUtils.isEmpty(mnemonic)) {
                String[] mnemonics = mnemonic.split(" ");
                if (mnemonics.length == 12) {
                    mTvWord1.setText(mnemonics[0]);
                    mTvWord2.setText(mnemonics[1]);
                    mTvWord3.setText(mnemonics[2]);
                    mTvWord4.setText(mnemonics[3]);
                    mTvWord5.setText(mnemonics[4]);
                    mTvWord6.setText(mnemonics[5]);
                    mTvWord7.setText(mnemonics[6]);
                    mTvWord8.setText(mnemonics[7]);
                    mTvWord9.setText(mnemonics[8]);
                    mTvWord10.setText(mnemonics[9]);
                    mTvWord11.setText(mnemonics[10]);
                    mTvWord12.setText(mnemonics[11]);
                }
            }
        }

        mBtnVerify = view.findViewById(R.id.btn_confirm);
        mBtnVerify.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
//                new MsgDialog(getContext(), getString(R.string.toast_qr_create_fail)).setIsHook(false).show();
                onButtonClick.onClick(mBtnVerify);
        }
    }

    // 定义接口变量的get方法
    public OnButtonClick getOnButtonClick() {
        return onButtonClick;
    }

    // 定义接口变量的set方法
    public void setOnButtonClick(OnButtonClick onButtonClick) {
        this.onButtonClick = onButtonClick;
    }

    public interface OnButtonClick {
        public void onClick(View view);
    }

}




