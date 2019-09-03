package com.doughnut.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;


import com.doughnut.R;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.view.TitleBar;


public class BackupInfoActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;
    private TextView mTvbtn_next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_info);
        initView();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.titleBar_backup_wallet));

        // 下一步按钮
        mTvbtn_next = findViewById(R.id.tv_next);
        mTvbtn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mTvbtn_next) {
            Intent intent = new Intent(this, BackupInfoActivity.class);
            startActivity(intent);
        }
    }

}
