package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;

import butterknife.Bind;

/**
 * Created by yt on 2017/2/14.
 */
public class WebActivityTest extends BaseActivity {
    private static final String HTTP_URL = "https://www.baidu.com/";
    private static Activity parentActivity;
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    public static void openActivity(Activity activity) {
        parentActivity = activity;
        Intent intent = new Intent();
        intent.setClass(activity, WebActivityTest.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(false);// 是否使用缓存
        webSettings.setDomStorageEnabled(true);// DOM Storage

        webView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                webView.requestFocus();
                return false;
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//载入网页
                return true;
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //接受证书
                handler.proceed();
                Logger.e("---------------error=" + error);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Logger.i("*****************errorCode=" + errorCode + "  description=" + description + "  failingUrl=" + failingUrl);
            }


        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                super.onProgressChanged(view, newProgress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(newProgress * 100);
                    }
                });
                Logger.i("newProgress " + newProgress);
            }
        });
        //屏蔽掉长按事件 因为webview长按时将会调用系统的复制控件:
        webView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.loadUrl(HTTP_URL);

    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings_web));
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();//返回上一页面
                }
                onBackPressed();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUseActivity.openActivity(WebActivityTest.this);
                parentActivity.finish();
                finish();
            }
        });
    }

    /**
     * 初始化布局文件
     *
     * @return
     */
    @Override
    public int initLayout() {
        return R.layout.activity_wifi_web;
    }

    /**
     * 初始化butter
     *
     * @return
     */
    @Override
    public Activity butterKnife() {
        return this;
    }

}
