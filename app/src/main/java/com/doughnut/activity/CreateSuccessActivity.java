package com.doughnut.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.view.TitleBar;


public class CreateSuccessActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;

    private ImageView mTvGoback;

    private Button mTvBackup;

    private TextView mTvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_success);
        initView();
    }

    private void initView(){

        mTvGoback = findViewById(R.id.btn_go_back);
        mTvGoback.setOnClickListener(this);
        mTvBackup = findViewById(R.id.btn_backup);
        mTvBackup.setOnClickListener(this);
        mTvSkip = findViewById(R.id.text_skip);
        mTvSkip.setOnClickListener(this);
//        mTitleBar = findViewById(R.id.title_bar);
//        mTitleBar.setLeftDrawable(R.drawable.ic_back_white);
//        mTitleBar.setTitle(R.string.btn_create_wallet);
//        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
//            @Override
//            public void onLeftClick(View view) {
//                onBackPressed();
//            }
//        });
    }

    /**
     * 画面按钮事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_go_back:
                onBackPressed();
                break;
            case R.id.btn_backup:
                WalletExportActivity.startExportWalletActivity(this, "");
                break;
            case R.id.text_skip:
                MainActivity.startMainActivity(this);
                break;
        }
    }

    public static void startCreateSuccessActivity(Context from) {
        Intent intent = new Intent(from, CreateSuccessActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }


}
