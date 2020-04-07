package com.example.mvp_demo.module.webview;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.mvp_demo.R;
import com.example.mvp_demo.base.BaseActivity;
import com.example.mvp_demo.view.ProgressWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity<WebViewPrensenter> implements WebViewView {

    @BindView(R.id.pwv)
    ProgressWebView pwv;

    @Override
    protected WebViewPrensenter createPresenter() {
        return new WebViewPrensenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        pwv.getSettings().setJavaScriptEnabled(true);
        pwv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        String url =getIntent().getStringExtra("link");
        pwv.loadUrl(url);
    }
}
