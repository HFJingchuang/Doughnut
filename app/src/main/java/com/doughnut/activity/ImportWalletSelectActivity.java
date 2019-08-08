package com.doughnut.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.doughnut.R;
import com.doughnut.view.TitleBar;


public class ImportWalletSelectActivity extends BaseActivity implements View.OnClickListener{


    private RelativeLayout privateKeyBtn, keyStoreBtn;

    private TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet_select);
        initView();
    }

    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.titleBar_import_wallet);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });


        privateKeyBtn = findViewById(R.id.select_export_privateKey);
        privateKeyBtn.setOnClickListener(this);

        keyStoreBtn = findViewById(R.id.select_export_keyStore);
        keyStoreBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        if (view == privateKeyBtn) {
            Intent intent = new Intent(this, PrivateKeyImportActivity.class);
            startActivity(intent);
        } else if (view == keyStoreBtn) {
            Intent intent = new Intent(this, KeyStoreImportActivity.class);
            startActivity(intent);
        }
    }

}
