package com.doughnut.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.activity.AboutActivity;
import com.doughnut.activity.JtNodeRecordActivity;
import com.doughnut.activity.LanguageActivity;
import com.doughnut.activity.ModifyWalletActivity;
import com.doughnut.activity.ShareActivity;
import com.doughnut.activity.TokenReceiveActivity;
import com.doughnut.activity.TokenTransferActivity;
import com.doughnut.activity.TransactionRecordActivity;
import com.doughnut.activity.WalletManageActivity;
import com.doughnut.activity.WalletQRActivity;
import com.doughnut.activity.WebBrowserActivity;
import com.doughnut.config.Constant;
import com.doughnut.utils.DeviceUtil;
import com.doughnut.utils.ViewUtil;
import com.doughnut.wallet.WalletSp;


public class MainUserFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout mLayoutManageWallet;
    private RelativeLayout mLayoutRecordTransaction;
    private RelativeLayout mLayoutNode;
    private RelativeLayout mLayoutHelp;
    private RelativeLayout mLayoutAbout;
    private RelativeLayout mLayoutShare;
    private RelativeLayout mLayoutLanguage;
    private LinearLayout mLayoutRight;
    private LinearLayout mLayoutReceive;
    private LinearLayout mLayoutSend;
    private TextView mTvAddress;
    private TextView mTvName;
    private TextView mTvLab;
    private TextView mTvVersion;
    private LinearLayout mLayoutQR;
    private String currentWallet;

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
        setWalletInfo();
        mLayoutManageWallet.setClickable(true);
        mLayoutRecordTransaction.setClickable(true);
        mLayoutHelp.setClickable(true);
        mLayoutAbout.setClickable(true);
        mLayoutShare.setClickable(true);
        mLayoutLanguage.setClickable(true);
        mLayoutNode.setClickable(true);
        mLayoutRight.setClickable(true);
        mLayoutReceive.setClickable(true);
        mLayoutSend.setClickable(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        setWalletInfo();
        mLayoutManageWallet.setClickable(true);
        mLayoutRecordTransaction.setClickable(true);
        mLayoutHelp.setClickable(true);
        mLayoutAbout.setClickable(true);
        mLayoutShare.setClickable(true);
        mLayoutLanguage.setClickable(true);
        mLayoutNode.setClickable(true);
        mLayoutRight.setClickable(true);
        mLayoutReceive.setClickable(true);
        mLayoutSend.setClickable(true);
        mLayoutQR.setClickable(true);
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutManageWallet) {
            mLayoutManageWallet.setClickable(false);
            WalletManageActivity.startModifyWalletActivity(getActivity(), false);
        } else if (view == mLayoutRecordTransaction) {
            mLayoutRecordTransaction.setClickable(false);
            TransactionRecordActivity.startTransactionRecordActivity(getActivity());
        } else if (view == mLayoutNode) {
            mLayoutNode.setClickable(false);
            JtNodeRecordActivity.startJtNodeRecordActivity(getActivity());
        } else if (view == mLayoutHelp) {
            mLayoutHelp.setClickable(false);
            WebBrowserActivity.startWebBrowserActivity(getActivity(), getString(R.string.titleBar_help_center), Constant.help_url);
        } else if (view == mLayoutAbout) {
            mLayoutAbout.setClickable(false);
            AboutActivity.startAboutActivity(getActivity());
        } else if (view == mLayoutShare) {
            mLayoutAbout.setClickable(false);
            ShareActivity.startAboutActivity(getActivity());
        } else if (view == mLayoutLanguage) {
            mLayoutLanguage.setClickable(false);
            LanguageActivity.startLanguageActivity(getActivity());
        } else if (view == mLayoutRight) {
            mLayoutRight.setClickable(false);
            ModifyWalletActivity.startModifyWalletActivity(getActivity(),
                    currentWallet);
        } else if (view == mLayoutReceive) {
            mLayoutReceive.setClickable(false);
            TokenReceiveActivity.startTokenReceiveActivity(getActivity(), "");
        } else if (view == mLayoutSend) {
            mLayoutSend.setClickable(false);
            TokenTransferActivity.startTokenTransferActivity(getActivity());
        } else if (view == mLayoutQR) {
            mLayoutQR.setClickable(false);
            WalletQRActivity.startTokenReceiveActivity(getActivity(), currentWallet);
        }
    }

    private void initView(View view) {

        mLayoutManageWallet = view.findViewById(R.id.layout_manage_wallet);
        mLayoutRecordTransaction = view.findViewById(R.id.layout_transaction_record);
        mLayoutNode = view.findViewById(R.id.layout_node);
        mLayoutHelp = view.findViewById(R.id.layout_help);
        mLayoutAbout = view.findViewById(R.id.layout_about);
        mLayoutShare = view.findViewById(R.id.layout_share);
        mLayoutLanguage = view.findViewById(R.id.layout_language);

        mLayoutRight = view.findViewById(R.id.layout_right);
        mLayoutReceive = view.findViewById(R.id.layout_receiver);
        mLayoutSend = view.findViewById(R.id.layout_send);
        mTvAddress = view.findViewById(R.id.tv_wallet_address);
        mTvName = view.findViewById(R.id.tv_wallet_name);
        mTvLab = view.findViewById(R.id.tv_label);
        mLayoutQR = view.findViewById(R.id.layout_qr);
        mTvVersion = view.findViewById(R.id.tv_version);
        mTvVersion.setText(DeviceUtil.getVersionName());

        mLayoutManageWallet.setOnClickListener(this);
        mLayoutRecordTransaction.setOnClickListener(this);
        mLayoutNode.setOnClickListener(this);
        mLayoutHelp.setOnClickListener(this);
        mLayoutAbout.setOnClickListener(this);
        mLayoutShare.setOnClickListener(this);
        mLayoutLanguage.setOnClickListener(this);
        mLayoutRight.setOnClickListener(this);
        mLayoutReceive.setOnClickListener(this);
        mLayoutSend.setOnClickListener(this);
        mLayoutQR.setOnClickListener(this);

        setWalletInfo();
    }

    private void setWalletInfo() {
        currentWallet = WalletSp.getInstance(getContext(), "").getCurrentWallet();
        if (TextUtils.isEmpty(currentWallet)) {
            mTvLab.setVisibility(View.GONE);
            mTvName.setText(getString(R.string.tv_has_no_wallet));
            mLayoutQR.setEnabled(false);
            mLayoutRight.setEnabled(false);
        } else {
            mTvAddress.setText(currentWallet);
            ViewUtil.EllipsisTextView(mTvAddress);
            mTvName.setText(WalletSp.getInstance(getContext(), currentWallet).getName());
            ViewUtil.EllipsisTextView(mTvName);
            mTvLab.setVisibility(View.VISIBLE);
            mLayoutQR.setEnabled(true);
            mLayoutRight.setEnabled(true);
        }
    }

}
