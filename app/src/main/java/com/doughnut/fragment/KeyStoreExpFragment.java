package com.doughnut.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.activity.TokenReceiveActivity;
import com.doughnut.config.Constant;
import com.doughnut.utils.ImageUtils;
import com.doughnut.utils.QRUtils;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.wallet.WalletSp;

public class KeyStoreExpFragment extends BaseFragment implements View.OnClickListener {

    private TextView mTvWalletName, mTvKeyStore;
    private ImageView mImgQR;
    private LinearLayout mLayoutExport, mLayoutCopy;

    private Context mContext;
    private String mKeyStore;


    public static KeyStoreExpFragment newInstance(String walletAddress) {
        Bundle args = new Bundle();
        args.putString(Constant.WALLET_ADDRESS, walletAddress);
        KeyStoreExpFragment fragment = new KeyStoreExpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_key_store_exp, container, false);
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

        mTvWalletName = view.findViewById(R.id.tv_wallet_name);
        mTvKeyStore = view.findViewById(R.id.tv_keystore);
        if (getArguments() != null) {
            String walletAddress = getArguments().getString(Constant.WALLET_ADDRESS);
            String walletName = WalletSp.getInstance(getContext(), walletAddress).getName();
            mKeyStore = WalletSp.getInstance(getContext(), walletAddress).getKeyStore();
            if (!TextUtils.isEmpty(walletName)) {
                mTvWalletName.setText(walletName);
                ViewUtil.EllipsisTextView(mTvWalletName);
            }
            if (!TextUtils.isEmpty(mKeyStore)) {
                mTvKeyStore.setText(mKeyStore);
            }
        }

        mImgQR = view.findViewById(R.id.img_qr);

        mLayoutCopy = view.findViewById(R.id.layout_copy);
        mLayoutCopy.setOnClickListener(this);
        mLayoutExport = view.findViewById(R.id.layout_export);
        mLayoutExport.setOnClickListener(this);

        createQRCode();
    }

    private void createQRCode() {
        try {
            Bitmap bitmap = QRUtils.createQRCode(mKeyStore, getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgQR.setImageBitmap(bitmap);
        } catch (Exception e) {
            ToastUtil.toast(mContext, getResources().getString(R.string.toast_qr_create_fail));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_copy:
                Util.clipboard(getContext(), "", mKeyStore);
                ToastUtil.toast(getContext(), getResources().getString(R.string.toast_private_key_copied));
                break;
            case R.id.layout_export:
                BitmapDrawable bmpDrawable = (BitmapDrawable) mImgQR.getDrawable();
                Bitmap bitmap = bmpDrawable.getBitmap();
                Boolean saved = ImageUtils.saveImageToGallery(getContext(), bitmap);
                if (saved) {
                    ToastUtil.toast(getContext(), getResources().getString(R.string.toast_save_success));
                } else {
                    ToastUtil.toast(getContext(), getResources().getString(R.string.toast_save_fail));
                }
        }
    }
}