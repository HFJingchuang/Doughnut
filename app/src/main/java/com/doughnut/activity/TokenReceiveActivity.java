
package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.QRUtils;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.Util;
import com.doughnut.view.TitleBar;
import com.doughnut.wallet.WalletSp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TokenReceiveActivity extends BaseActivity {

    public final static String TAG = "TokenTransferActivity";
    private TitleBar mTitleBar;

    private final static String TOKEN = "Token";
    private String mToken;

    private ImageView mImgQr;
    private ImageView mImgQrShadow;
    private EditText mEdtAmount;
    private TextView mTvAddress;
    private ImageView mImgCopyAddress;
    private WheelView mWhTokenName;
    private Map<String, String> tokenEntries;

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
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_collect));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mImgQr = findViewById(R.id.receive_qr);
        mImgQrShadow = findViewById(R.id.img_qrcode_shadow);
        mImgQrShadow.setVisibility(View.GONE);
        mEdtAmount = findViewById(R.id.receive_amount);
        mTvAddress = findViewById(R.id.receive_address);
        mImgCopyAddress = findViewById(R.id.img_copy);
        mImgCopyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.clipboard(TokenReceiveActivity.this, "", mTvAddress.getText().toString());
                ToastUtil.toast(TokenReceiveActivity.this, getString(R.string.toast_wallet_address_copied));
            }
        });
        //准备数据
        getAllTokens();
        mWhTokenName = findViewById(R.id.wh_token_name);
        mWhTokenName.setAdapter(new ArrayWheelAdapter(tokenEntries.keySet()));
        mWhTokenName.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                createQRCode();
            }
        });
        createQRCode();
    }

    private void initData() {
        final String address = WalletSp.getInstance(this, "").getCurrentWallet();
        mTvAddress.setText(address);
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
    }

    private void createQRCode() {
        String amountStr = mEdtAmount.getText().toString();
        try {
            GsonUtil gsonUtil = new GsonUtil("{}");
            gsonUtil.putString(Constant.RECEIVE_ADDRESS_KEY, mTvAddress.getText().toString());
            if (TextUtils.isEmpty(amountStr)) {
                gsonUtil.putString(Constant.TOEKN_AMOUNT, "");
            } else {
                BigDecimal amount = new BigDecimal(amountStr);
                gsonUtil.putString(Constant.TOEKN_AMOUNT, amount.stripTrailingZeros().toPlainString());
            }
            gsonUtil.putInt(Constant.TOEKN_NAME, mWhTokenName.getCurrentItem());
            Bitmap bitmap = QRUtils.createQRCode(gsonUtil.toString(), getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            ToastUtil.toast(TokenReceiveActivity.this, "数量格式不正确");
        }
    }

    class ArrayWheelAdapter implements WheelAdapter {
        private List<String> mList = new ArrayList<>();

        public ArrayWheelAdapter(Set<String> set) {
            this.mList.clear();
            this.mList.addAll(set);
        }

        @Override
        public int getItemsCount() {
            return this.mList.size();
        }

        @Override
        public Object getItem(int index) {
            return this.mList.get(index);
        }

        @Override
        public int indexOf(Object o) {
            return mList.indexOf(o);

        }
    }

    /**
     * 获取所有tokens
     */
    public void getAllTokens() {
        String fileName = getPackageName() + "_tokens";
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString("tokens", "");
        tokenEntries = JSONObject.parseObject(tokens, Map.class);
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
