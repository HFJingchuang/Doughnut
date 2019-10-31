package com.doughnut.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.doughnut.R;
import com.doughnut.activity.AddCurrencyActivity;
import com.doughnut.activity.BaseActivity;
import com.doughnut.activity.LanguageActivity;
import com.doughnut.config.Constant;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.LanguageUtil;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

public class WebActivity extends BaseActivity {

    protected AgentWeb mAgentWeb;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(getUrl());
        mAgentWeb.getJsInterfaceHolder().addJavaObject("JsNativeBridge", new JsNativeBridge(mAgentWeb, this));
    }

    private com.just.agentweb.WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }
    };

    private com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    };

    //用户需要在打开WebActivity时传递对应的url
    public String getUrl() {
        Intent intent = getIntent();
        String load_url = "";
        if (intent != null) {
            load_url = intent.getStringExtra(Constant.LOAD_URL);
        }
        return load_url;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "onResult:" + requestCode + " onResult:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(Constant.LOAD_URL, url);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(JsEvent jsEvent) {
        String msg = jsEvent.getMsg();
        Log.v("onEvent", msg);
        String callbackId = JsNativeBridge.getCallBackId();
        Log.v("onEvent", callbackId);
        GsonUtil msgJson = new GsonUtil(msg);
        if (msgJson.isValid()) {
            mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + msg + "')");
        } else {
            mAgentWeb.getJsAccessEntrace().quickCallJs(callbackId, msg);
        }
    }

}
