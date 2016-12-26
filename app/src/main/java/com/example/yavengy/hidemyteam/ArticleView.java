package com.example.yavengy.hidemyteam;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);

        Intent intent = getIntent();

        String url = "http://bleacherreport.com/articles/" + intent.getStringExtra("permalink");

        WebView webView = (WebView)findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setJavaScriptEnabled(true);

        //webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 );

        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );

        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );

        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        if ( !isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        webView.loadUrl(url);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
