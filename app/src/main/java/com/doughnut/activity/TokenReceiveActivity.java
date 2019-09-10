
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.ImageUtils;
import com.doughnut.utils.QRUtils;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.utils.ViewUtil;
import com.doughnut.view.CashierInputFilter;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletSp;

import java.math.BigDecimal;


public class TokenReceiveActivity extends BaseActivity {

    public final static String TAG = "TokenTransferActivity";
    private TitleBar mTitleBar;

    private final static String TOKEN = "Token";
    private String mToken;

    private ImageView mImgQr;
    private ImageView mImgQrShadow;
    private EditText mEdtAmount;
    private TextView mTvAddress;
    private TextView mTvWalletName;
    private TextView mTvTokenName;
    private RelativeLayout mLayoutCopy;
    private LinearLayout mLayoutSave;
    private LinearLayout mLayoutToken;
    private LinearLayout mLayoutRoot;
    private ScrollView mViewScroll;
    private String mAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_receive_activity);
        if (getIntent() != null) {
            mToken = getIntent().getStringExtra(TOKEN);
        }
        initView();
        initData();
    }

    private void initView() {
        mLayoutRoot = findViewById(R.id.root_view);
        mViewScroll = findViewById(R.id.view_scroll);
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back_white);
        mTitleBar.setTitleBarBackColor(R.color.color_detail_receive);
        mTitleBar.setTitle(getString(R.string.titleBar_collect));
        mTitleBar.setTitleTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                MainActivity.startMainActivityForIndex(TokenReceiveActivity.this, 2);
                finish();
            }
        });

        mImgQr = findViewById(R.id.receive_qr);
        mImgQrShadow = findViewById(R.id.img_qrcode_shadow);
        mImgQrShadow.setVisibility(View.GONE);
        mEdtAmount = findViewById(R.id.receive_amount);
        InputFilter[] filters = {new CashierInputFilter()};
        mEdtAmount.requestFocus();
        mEdtAmount.setFilters(filters);
        mEdtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                createQRCode();
            }
        });
        mTvAddress = findViewById(R.id.receive_address);
        mTvWalletName = findViewById(R.id.tv_wallet_name);
        mTvTokenName = findViewById(R.id.tv_token_name);
        mLayoutCopy = findViewById(R.id.layout_copy);
        mLayoutCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.clipboard(TokenReceiveActivity.this, "", mAddress);
                ToastUtil.toast(TokenReceiveActivity.this, getString(R.string.toast_wallet_address_copied));
            }
        });
        mLayoutSave = findViewById(R.id.layout_save);
        mLayoutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bmpDrawable = (BitmapDrawable) mImgQr.getDrawable();
                Bitmap bitmap = bmpDrawable.getBitmap();
                Boolean saved = ImageUtils.saveImageToGallery(TokenReceiveActivity.this, bitmap);
                if (saved) {
                    ToastUtil.toast(TokenReceiveActivity.this, getResources().getString(R.string.toast_save_success));
                } else {
                    ToastUtil.toast(TokenReceiveActivity.this, getResources().getString(R.string.toast_save_fail));
                }
            }
        });
        mLayoutToken = findViewById(R.id.layout_token);
        mLayoutToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCurrencyActivity.startLanguageActivity(TokenReceiveActivity.this);
            }
        });
        ViewUtil.controlKeyboardLayout(mLayoutRoot, mViewScroll, this);
    }

    private void initData() {
        mAddress = WalletSp.getInstance(this, "").getCurrentWallet();
        mTvAddress.setText(mAddress);
        if (!TextUtils.isEmpty(mToken)) {
            mTvTokenName.setText(mToken);
        }
        ViewUtil.EllipsisTextView(mTvAddress);
        mTvWalletName.setText(WalletSp.getInstance(this, mAddress).getName());
        ViewUtil.EllipsisTextView(mTvWalletName);
        createQRCode();
    }

    private void createQRCode() {
        String amountStr = mEdtAmount.getText().toString();
        try {
            GsonUtil gsonUtil = new GsonUtil("{}");
            gsonUtil.putString(Constant.RECEIVE_ADDRESS_KEY, mAddress);
            if (TextUtils.isEmpty(amountStr)) {
                gsonUtil.putString(Constant.TOEKN_AMOUNT, "");
            } else {
                BigDecimal amount = new BigDecimal(amountStr);
                gsonUtil.putString(Constant.TOEKN_AMOUNT, amount.stripTrailingZeros().toPlainString());
            }
            gsonUtil.putString(Constant.TOEKN_NAME, mTvTokenName.getText().toString());
            Bitmap bitmap = QRUtils.createQRCode(gsonUtil.toString(), getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            ToastUtil.toast(TokenReceiveActivity.this, getResources().getString(R.string.toast_amount_format_err));
        }
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenReceiveActivity(Context context, String token) {
        Intent intent = new Intent(context, TokenReceiveActivity.class);
        intent.putExtra(TOKEN, token);
        context.startActivity(intent);
    }
}
