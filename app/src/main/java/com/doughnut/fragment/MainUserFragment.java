package com.doughnut.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.activity.AboutActivity;
import com.doughnut.activity.LanguageActivity;
import com.doughnut.activity.ManageWalletActivity;
import com.doughnut.activity.TokenTransferActivity;
import com.doughnut.activity.TransactionRecordActivity;
import com.doughnut.activity.WebBrowserActivity;
import com.doughnut.config.Constant;
import com.doughnut.utils.ToastUtil;
import com.doughnut.wallet.WalletSp;


public class MainUserFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout mLayoutManageWallet;
    private RelativeLayout mLayoutRecordTransaction;
    private RelativeLayout mLayoutHelp;
    private RelativeLayout mLayoutAbout;
    private RelativeLayout mLayoutLanguage;
    private LinearLayout mLayoutPay;
    private LinearLayout mLayoutRecieve;
    private LinearLayout mLayoutRight;
    private TextView mAddrressTv;
    private TextView mNameTv;

    public static MainUserFragment newInstance() {
        Bundle args = new Bundle();
        MainUserFragment fragment = new MainUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mainuser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManageWallet.setClickable(true);
        mLayoutRecordTransaction.setClickable(true);
        mLayoutHelp.setClickable(true);
        mLayoutAbout.setClickable(true);
        mLayoutLanguage.setClickable(true);
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutManageWallet) {
            mLayoutManageWallet.setClickable(false);
            ManageWalletActivity.startModifyWalletActivity(getActivity());
        } else if (view == mLayoutRecordTransaction) {
            mLayoutRecordTransaction.setClickable(false);
            TransactionRecordActivity.startTransactionRecordActivity(getActivity());
        } else if (view == mLayoutHelp) {
            mLayoutHelp.setClickable(false);
            WebBrowserActivity.startWebBrowserActivity(getActivity(), getString(R.string.titleBar_help_center), Constant.help_url);
        } else if (view == mLayoutAbout) {
            mLayoutAbout.setClickable(false);
            AboutActivity.startAboutActivity(getActivity());
        } else if (view == mLayoutLanguage) {
            mLayoutLanguage.setClickable(false);
            LanguageActivity.startLanguageActivity(getActivity());
        } else if (view == mLayoutPay) {
            TokenTransferActivity.startTokenTransferActivity(getActivity());
        } else if (view == mLayoutRecieve) {
            ToastUtil.toast(getContext(), "还没实现。。。");
        } else if (view == mLayoutRight) {
            ToastUtil.toast(getContext(), "还没实现。。。");
        }
    }

    private void initView(View view) {

        mLayoutManageWallet = view.findViewById(R.id.layout_manage_wallet);
        mLayoutRecordTransaction = view.findViewById(R.id.layout_transaction_record);
        mLayoutHelp = view.findViewById(R.id.layout_help);
        mLayoutAbout = view.findViewById(R.id.layout_about);
        mLayoutLanguage = view.findViewById(R.id.layout_language);

        mLayoutRight = view.findViewById(R.id.layout_right);
        mLayoutPay = view.findViewById(R.id.layout_pay);
        mLayoutRecieve = view.findViewById(R.id.layout_recieve);
        mAddrressTv = view.findViewById(R.id.tv_wallet_address);
        mNameTv = view.findViewById(R.id.tv_wallet_name);

        mLayoutManageWallet.setOnClickListener(this);
        mLayoutRecordTransaction.setOnClickListener(this);
        mLayoutHelp.setOnClickListener(this);
        mLayoutAbout.setOnClickListener(this);
        mLayoutLanguage.setOnClickListener(this);
        mLayoutRight.setOnClickListener(this);
        mLayoutPay.setOnClickListener(this);
        mLayoutRecieve.setOnClickListener(this);

        setWalletInfo();
    }

    private void setWalletInfo() {
        String currentWallet = WalletSp.getInstance(getContext(), "").getCurrentWallet();
        mAddrressTv.setText(currentWallet);
        mNameTv.setText(WalletSp.getInstance(getContext(), currentWallet).getName());
    }

}
