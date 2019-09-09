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

public class KeyStoreExpFragment extends BaseFragment implements View.OnClickListener {


    private TextView mTvKeyStore, mTvDownKeyStore;

    private Context mContext;

    private ImageView mTvKeyStoreImg, mTvQRImg;

    private static String password;


    public static PrivateKeyExpFragment newInstance(String password) {
        Bundle args = new Bundle();
        PrivateKeyExpFragment fragment = new PrivateKeyExpFragment();
        fragment.setArguments(args);
//        fragment.password = password;
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
        String keyStore = WalletManager.getInstance(mContext).getPrivateKey(password, currentWallet);

        mTvKeyStore = view.findViewById(R.id.tv_keystore);

        mTvDownKeyStore = view.findViewById(R.id.tv_down_keyStore);
        mTvDownKeyStore.setOnClickListener(this);

        mTvKeyStore.setText(keyStore);

        mTvKeyStoreImg = view.findViewById(R.id.keyStore_qr);
        mTvQRImg = view.findViewById(R.id.img_qrcode_shadow);

        createQRCode(keyStore);
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
            case R.id.tv_down_keyStore:
                Util.clipboard(getContext(), "", mTvKeyStore.getText().toString());
                ToastUtil.toast(getContext(), getContext().getString(R.string.toast_private_key_copied));
                break;
        }
    }

//    private void setPassword(String password){
//        this.password = password;
//    }

}