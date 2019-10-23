package com.doughnut.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.doughnut.R;
import com.doughnut.view.WebViewWrapper;

public class TestyWebViewActivity extends AppCompatActivity {

    private WebViewWrapper mWebViewWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testy_web_view);
        mWebViewWrapper = findViewById(R.id.webViewWrapper);
        mWebViewWrapper.loadUrl("https://www.baidu.com");
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebViewWrapper.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mWebViewWrapper.onPause();
    }

    @Override
    public void onDestroy() {
        mWebViewWrapper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        boolean isBack = mWebViewWrapper.goBack();
        if (!isBack) {
           finish();
        }
    }
}
