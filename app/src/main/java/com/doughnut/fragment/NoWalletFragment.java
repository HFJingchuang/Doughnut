package com.doughnut.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.doughnut.R;
import com.doughnut.activity.CreateNewWalletActivity;
import com.doughnut.activity.WalletImportActivity;
import com.zxing.activity.CaptureActivity;


public class NoWalletFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout mLayoutCreate, mLayoutImport;
    private ImageView mImgScan;

    public static NoWalletFragment newInstance() {
        Bundle args = new Bundle();
        NoWalletFragment fragment = new NoWalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutCreate.setClickable(true);
        mLayoutImport.setClickable(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLayoutCreate.setClickable(true);
        mLayoutImport.setClickable(true);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_scan:
                CaptureActivity.startCaptureActivity(getContext(), false);
                break;
            case R.id.layout_create_wallet:
                mLayoutCreate.setClickable(false);
                CreateNewWalletActivity.startCreateNewWalletActivity(getContext());
                break;
            case R.id.layout_import_wallet:
                mLayoutImport.setClickable(false);
                WalletImportActivity.startWalletImportActivity(getContext());
                break;
        }
    }

    private void initView(View view) {

        mImgScan = view.findViewById(R.id.img_scan);
        mImgScan.setOnClickListener(this);
        mLayoutCreate = view.findViewById(R.id.layout_create_wallet);
        mLayoutCreate.setOnClickListener(this);
        mLayoutImport = view.findViewById(R.id.layout_import_wallet);
        mLayoutImport.setOnClickListener(this);
    }
}
