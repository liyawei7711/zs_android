package com.zs.ui.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.rx.RxUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.models.auth.bean.AnJianBean;

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
    LinearLayout fl_root;
    @BindView(R.id.tv_anjian)
    TextView tv_anjian;

    @BindView(R.id.webview)
    WebView webview;
    Gson gson;

    @BindExtra
    boolean fromCapture;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        mZeusLoadView.setLoadingText("正在加载").setLoading();
        gson = new Gson();
        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final String str = AppAuth.get().getToken();
                webview.loadUrl("javascript:setToken('" + str + "')");
                return false;
            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.addJavascriptInterface(new JavaScriptInterface(this), "wv");
        webview.setWebViewClient(new InnerWebViewClient());
        //添加客户端支持
        webview.setWebChromeClient(new WebChromeClient());
        System.out.println("ccccccccccccccccccccc h5 " +AppAuth.get().getH5Web());
        webview.loadUrl(AppAuth.get().getH5Web());
//        webview.loadUrl("http://www.baidu.com");

        if (!TextUtils.isEmpty(AppAuth.get().getAnJian())) {
            AnJianBean bean = new Gson().fromJson(AppAuth.get().getAnJian(), AnJianBean.class);
            tv_anjian.setText(bean.getName());
            tv_anjian.setVisibility(View.VISIBLE);
        } else {
            tv_anjian.setVisibility(View.GONE);
        }

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
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mZeusLoadView.dismiss();
            System.out.println("cccccccccccccccccccccccccc onPageFinished");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
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

    class JavaScriptInterface {
        Context context;

        public JavaScriptInterface(Context context) {
            this.context = context;
        }

        public void testJS() {
            final String str = AppAuth.get().getToken();
            new RxUtils().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                @Override
                public Object doOnThread() {
                    return "";
                }

                @Override
                public void doOnMain(Object data) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webview.loadUrl("javascript:setToken('" + str + "')");
                        webview.evaluateJavascript("javascript:setToken('" + str + "')", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                System.out.println("ccccccccccccccccccccc onReceiveValue "+value);
                                //获取返回值，如果存在
                            }

                        });
                    } else {
                        webview.loadUrl("javascript:setToken('" + str + "')");
                    }
                }
            });

        }

        //与js交互时用到的方法
        @JavascriptInterface
        public String getToken() {
            System.out.println("cccccccccccccccccccccccccc getToken:" + AppAuth.get().getToken());
            return AppAuth.get().getToken();
        }

        //与js交互时用到的方法
        @JavascriptInterface
        public void correlateInfo() {
            System.out.println("cccccccccccccccccccccccccc correlateInfo()");
            AppAuth.get().put("AnJianBean", "");
            showToast("取消关联");
        }

        //与js交互时用到的方法
        @JavascriptInterface
        public void correlateInfo(String str) {
            showToast("关联案件");
            System.out.println("cccccccccccccccccccccccccc correlateInfo:" + str);
            try{
                if(fromCapture) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                AppAuth.get().put("AnJianBean", str);
                finish();
            }catch (Exception e){
                System.out.println("cccccccccccccccccccccccccc correlateInfo:Exception " + e);
            }

        }

    }

}
