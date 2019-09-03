
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.utils.QRUtils;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletSp;


public class WalletQRActivity extends BaseActivity {

    private final static String ADDR = "address";

    private TitleBar mTitleBar;
    private ImageView mImgQr;
    private ImageView mImgQrShadow;
    private TextView mTvAddress;
    private RelativeLayout mLayoutCopy;
    private String mAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_qr_activity);
        if (getIntent() != null) {
            mAddress = getIntent().getStringExtra(ADDR);
        }
        initView();
        initData();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back_white);
        mTitleBar.setTitleBarBackColor(R.color.color_dialog_confirm);
        mTitleBar.setTitleTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mImgQr = findViewById(R.id.receive_qr);
        mImgQrShadow = findViewById(R.id.img_qrcode_shadow);
        mImgQrShadow.setVisibility(View.GONE);
        mTvAddress = findViewById(R.id.receive_address);
        mLayoutCopy = findViewById(R.id.layout_copy);
        mLayoutCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.clipboard(WalletQRActivity.this, "", mAddress);
                ToastUtil.toast(WalletQRActivity.this, getString(R.string.toast_wallet_address_copied));
            }
        });
        createQRCode();
    }

    private void initData() {
        mTvAddress.setText(mAddress);
        ViewUtil.EllipsisTextView(mTvAddress);
        mTitleBar.setTitle(WalletSp.getInstance(this, mAddress).getName());
    }

    private void createQRCode() {
        try {
            Bitmap bitmap = QRUtils.createQRCode(mAddress, getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenReceiveActivity(Context context, String address) {
        Intent intent = new Intent(context, WalletQRActivity.class);
        intent.putExtra(ADDR, address);
        context.startActivity(intent);
    }
}
