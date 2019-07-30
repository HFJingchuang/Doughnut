package com.doughnut.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.doughnut.R;
import com.doughnut.view.TitleBar;

public class ReceivablesActivity  extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivables);
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.titleBar_transfer);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();

            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
