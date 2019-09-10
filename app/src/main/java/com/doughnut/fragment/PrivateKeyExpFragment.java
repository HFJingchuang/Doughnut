package com.doughnut.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.utils.QRUtils;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;


public class PrivateKeyExpFragment extends BaseFragment implements View.OnClickListener {


    private TextView mTvPrivatekey, mTvCopyPrivatekey;

    private Context mContext;

    private ImageView mTvPrivatekeyImg, mTvQRImg;

    private static String password;


    public static PrivateKeyExpFragment newInstance(String password) {
        Bundle args = new Bundle();
        PrivateKeyExpFragment fragment = new PrivateKeyExpFragment();
        fragment.setArguments(args);
        fragment.password = password;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_key_exp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        initView(view);
    }

    /**
     * 画面初期化
     *
     * @param view
     */
    private void initView(View view) {

        String currentWallet = WalletSp.getInstance(getContext(), "").getCurrentWallet();
        String privateKey = WalletManager.getInstance(mContext).getPrivateKey(password, currentWallet);

        mTvPrivatekey = view.findViewById(R.id.tv_privatekey);

        mTvCopyPrivatekey = view.findViewById(R.id.tv_copy_privatekey);
        mTvCopyPrivatekey.setOnClickListener(this);

        mTvPrivatekey.setText(privateKey);

        mTvPrivatekeyImg = view.findViewById(R.id.privateKey_qr);
        mTvQRImg = view.findViewById(R.id.img_qrcode_shadow);

        createQRCode(privateKey);
    }

    private void createQRCode(String privateKey) {
        try {
            Bitmap bitmap = QRUtils.createQRCode(privateKey, getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mTvQRImg.setImageBitmap(bitmap);
        } catch (Exception e) {
            ToastUtil.toast(mContext, "格式不正确");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
//        mLayoutManageWallet.setClickable(true);
//        mLayoutRecordTransaction.setClickable(true);
//        mLayoutNotification.setClickable(true);
//        mLayoutHelp.setClickable(true);
//        mLayoutAbout.setClickable(true);
//        mLayoutLanguage.setClickable(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy_privatekey:
                Util.clipboard(getContext(), "", mTvPrivatekey.getText().toString());
                ToastUtil.toast(getContext(), getContext().getString(R.string.toast_private_key_copied));
                break;
        }
    }

}




