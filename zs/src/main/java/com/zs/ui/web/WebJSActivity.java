package com.zs.ui.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.dao.auth.AppAuth;

/**
 * author: admin
 * date: 2018/06/12
 * version: 0
 * mail: secret
 * desc: MeetActivity
 */

@BindLayout(R.layout.activity_web_view)
public class WebJSActivity extends AppBaseActivity {
    @BindView(R.id.fl_root)
    FrameLayout fl_root;

    @BindView(R.id.webview)
    WebView webview;


    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);

    }

    @Override
    public void doInitDelay() {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.addJavascriptInterface(new JavaScriptInterface(this), "wv");
        webview.setWebViewClient(new InnerWebViewClient());
        webview.loadUrl(AppAuth.get().getH5Web());
//        webview.loadUrl("http://36.152.32.85:8082/ajapp/#/login");
//        webview.loadUrl("http://www.baidu.com");
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class InnerWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            System.out.println("cccccccccccccccccccccccccc onReceivedSslError");
//            if (Macro.isDebug) {
            handler.proceed();
//            } else {
            super.onReceivedSslError(view, handler, error);
//            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url) && url.startsWith("MOVE")) {//迁移
                Uri uri = Uri.parse(url);
                String token = uri.getQueryParameter("_tk");
                if (TextUtils.isEmpty(token)) {
                    token = uri.getQueryParameter("portal_token");
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            } else if (!TextUtils.isEmpty(url) && url.startsWith("FORGET_PWD")) {//忘记密码
                Uri uri = Uri.parse(url);
                String token = uri.getQueryParameter("_tk");
                if (TextUtils.isEmpty(token)) {
                    token = uri.getQueryParameter("portal_token");
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            } else if (!TextUtils.isEmpty(url) && url.startsWith("PWD_SET")) {//设置支密
                Uri uri = Uri.parse(url);
                String token = uri.getQueryParameter("_tk");
                if (TextUtils.isEmpty(token)) {
                    token = uri.getQueryParameter("portal_token");
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            super.onConsoleMessage(consoleMessage);
            String msg = consoleMessage.message();//log内容
            return true;
        }
    }

    class JavaScriptInterface {
        Context context;

        public JavaScriptInterface(Context context) {
            this.context = context;
        }

        //与js交互时用到的方法
        @JavascriptInterface
        public String getToken() {
            System.out.println("cccccccccccccccccccccccccc getToken:" + AppAuth.get().getToken());
            return AppAuth.get().getToken();
        }

        //与js交互时用到的方法
        @JavascriptInterface
        public void correlateInfo(String str) {
            System.out.println("cccccccccccccccccccccccccc correlateInfo:" + str);
        }
    }

}
