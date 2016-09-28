package com.fxly.creatsms.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fxly.creatsms.R;
import com.fxly.creatsms.net.ConnectionUtil;

public class MyWebView extends AppCompatActivity {
    private WebView mainWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        check_net();

        Intent intent1 = getIntent();
        String one= intent1.getStringExtra("web");


        mainWebView=(WebView)findViewById(R.id.mainWebView);

        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mainWebView.setWebViewClient(new MyCustomWebViewClient());
        mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        if(intent1.getStringExtra("web").equals("Mygithub")){
            mainWebView.loadUrl("https://github.com/fxlysm/");
            this.setTitle(intent1.getStringExtra("web")+" Code View");
        }

    }
    private class MyCustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /***
     * Chek network is connect
     */
    public void check_net(){
        if(!ConnectionUtil.isConn(getApplicationContext())){
            ConnectionUtil.setNetworkMethod(MyWebView.this);

        }else {

        }
    }

}
