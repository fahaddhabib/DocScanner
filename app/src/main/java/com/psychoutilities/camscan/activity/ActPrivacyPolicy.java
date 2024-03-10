package com.psychoutilities.camscan.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdView;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.utils.AdsUtils;


public class ActPrivacyPolicy extends BaseAct {
    private AdView adView;
    protected WebView web;

    private ProgressDialog progDailog;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.privacy_policy_act);
        adView = findViewById(R.id.adView);
        AdsUtils.showGoogleBannerAd(this, adView);
        progDailog = ProgressDialog.show(this, "Loading","Please wait...", true);
        progDailog.setCancelable(false);

        this.web = (WebView) findViewById(R.id.webView);


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        web.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });

        this.web.loadUrl("https://docscanner.psychocreatives.com/privacy-policy.html");
    }
}
