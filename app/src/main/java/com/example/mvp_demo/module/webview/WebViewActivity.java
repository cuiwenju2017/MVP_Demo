package com.example.mvp_demo.module.webview;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.mvp_demo.R;
import com.example.mvp_demo.base.BaseActivity;
import com.example.mvp_demo.base.BasePresenter;
import com.example.mvp_demo.base.BaseView;
import com.example.mvp_demo.databinding.ActivityWebViewBinding;

public class WebViewActivity extends BaseActivity<ActivityWebViewBinding,BasePresenter> {

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        String url = getIntent().getStringExtra("link");
        viewBinding.pwv.getSettings().setJavaScriptEnabled(true);
        viewBinding.pwv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebView.HitTestResult hit = view.getHitTestResult();
                //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
                if (TextUtils.isEmpty(hit.getExtra()) || hit.getType() == 0) {
                    //通过判断开头协议就可解决大部分重定向问题了，有另外的需求可以在此判断下操作
                    Log.e("重定向", "重定向: " + hit.getType() + " && EXTRA（）" + hit.getExtra() + "------");
                    Log.e("重定向", "GetURL: " + view.getUrl() + "\n" + "getOriginalUrl()" + view.getOriginalUrl());
                    Log.d("重定向", "URL: " + url);
                }
                if (url.startsWith("http://") || url.startsWith("https://")) { //加载的url是http/https协议地址
                    view.loadUrl(url);
                    return false; //返回false表示此url默认由系统处理,url未加载完成，会继续往下走
                } else { //加载的url是自定义协议地址
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        });
        viewBinding.pwv.loadUrl(url);
    }

    /**
     * 使点击回退按钮不会直接退出整个应用程序而是返回上一个页面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && viewBinding.pwv.canGoBack()) {
            viewBinding.pwv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewBinding.pwv != null) {
            viewBinding.pwv.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewBinding.pwv != null) {
            viewBinding.pwv.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewBinding.pwv != null) {
            viewBinding.pwv.destroy();
        }
    }
}
