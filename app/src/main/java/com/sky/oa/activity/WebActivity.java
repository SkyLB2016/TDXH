package com.sky.oa.activity;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sky.base.utils.LogUtils;
import com.sky.oa.databinding.ActivityWebBinding;
import com.sky.base.ui.BaseActivity;

public class WebActivity extends BaseActivity<ActivityWebBinding> {
    @Override
    protected ActivityWebBinding inflateBinding() {
        return ActivityWebBinding.inflate(getLayoutInflater());
    }

    //    private Button loadUrlBtn;
//    private Button evaluateBtn;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initEvents();
    }

    public void initViews() {
        binding.mWebView.loadUrl("file:///android_asset/index.html");
        binding.mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {//js调用原生的方式二
                LogUtils.i("webactivity==shouldOverrideUrlLoading: ");

                Uri url = request.getUrl();
                if (url != null) {
                    String schemenName = url.getScheme();//约定好的协议名称，这里我约定成了myscheme，经测试，协议名需要完全为小写字母
                    if ("myscheme".equals(schemenName)) {
                        String authority = url.getAuthority();//约定好的地址名
                        if ("myAddress".equals(authority)) {
                            String userName = url.getQueryParameter("userName");//获取用户名参数
                            String password = url.getQueryParameter("password");//获取密码参数
                            LogUtils.i("webactivity==shouldOverrideUrlLoading: 获取到js传过来的用户名userName为：" + userName + "，密码为：" + password);
                            Toast.makeText(WebActivity.this, "通过重写shouldOverrideUrlLoading的方式让js调用原生的方法", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        binding.mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                LogUtils.i("webactivity==onJsPrompt: ");
                Uri uri = Uri.parse(message);
                if (uri != null) {
                    String schemeName = uri.getScheme();//自定义的协议名称
                    if ("myscheme".equals(schemeName)) {
                        String authority = uri.getAuthority();
                        if ("myAddress".equals(authority)) {//自定义的地址名
                            String userName = uri.getQueryParameter("userName");//获取js传过来的参数userName
                            String password = uri.getQueryParameter("password"); //获取js传过来的password
                            Toast.makeText(WebActivity.this, "通过拦截prompt弹框的方式让js调用原生的方法，原生获取到的userName为：" + userName + "，password为：" + password, Toast.LENGTH_SHORT).show();
                        }
                        result.cancel();//这行要加上，不然onJsPrompt只会被调用一次，而且后续的js方法也会失效
                        return true;//返回true表示拦截弹框，只要符合约定的协议，就将js中的prompt弹框拦截，不让其弹出来
                    }
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
        WebSettings settings = binding.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
//        loadUrlBtn = findViewById(R.id.loadUrlBtn);
//        evaluateBtn = findViewById(R.id.evaluateBtn);
    }

    private void initEvents() {

        //原生调用js的第一种方式：调用WebView的loadUrl()方法，会使得页面重新刷新一次，不方便获取js函数的返回值
        binding.loadUrlBtn.setOnClickListener(v -> binding.mWebView.loadUrl("javascript:cat()"));

        //原生调用js的第二种方式，调用WebView的evaluateJavascript()方法
        binding.evaluateBtn.setOnClickListener(v -> {
            binding.mWebView.evaluateJavascript("javascript:sayHello()", value -> {//可以在这个回调方法里面拿到js方法的返回值
                LogUtils.i("webactivity==onReceiveValue: " + value);
            });
        });


        MyNative myNative = new MyNative();//不要使用匿名内部类放入addJavascriptInterface中
        //让js调用原生的方法
        binding.mWebView.addJavascriptInterface(myNative, "android");
    }


    class MyNative {
        @JavascriptInterface
        public void getNative1(String who) {
            LogUtils.i("webactivity==getNative1: " + who);
            Toast.makeText(WebActivity.this, "js通过对象映射addJavascriptInterface的方式调用了原生的方法", Toast.LENGTH_SHORT).show();
        }
    }
}