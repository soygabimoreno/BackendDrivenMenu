package com.gabrielmorenoibarra.android.backenddrivenmenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This class manages way of show a <code>{@link WebView}</code>
 * Created by Gabriel Moreno on 2017-13-02.
 */
public class CustomWebViewClient extends WebViewClient {

    public static final String TAG = CustomWebViewClient.class.getSimpleName();

    private Activity activity;
    private WebView wv;
    private String url;
    private String urlBase;
    private int initialScale;
    private UrlCache urlCache;

    /**
     * Constructor.
     * @param activity Activity to manage intents.
     * @param wv <code>WebView</code> where web site will be shown.
     * @param url URL address where it is hosted.
     * @param urlBase URL main address.
     * @param initialScale Initial zoom (0 is default).
     */
    @SuppressLint("SetJavaScriptEnabled")
    public CustomWebViewClient(Activity activity, WebView wv, String url, String urlBase, UrlCache urlCache, int initialScale) {
        this.activity = activity;
        this.wv = wv;
        this.url = url;
        this.urlBase = urlBase;
        this.initialScale = initialScale;
        this.urlCache = urlCache;

        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true); // We enable Javascript by default, due to it is needed by lots of websites

        wv.loadUrl(url);
        wv.setInitialScale(initialScale);
    }

    /**
     * Show the web.
     */
    public void show() {
        wv.setWebViewClient(new CustomWebViewClient(activity, wv, url, urlBase, urlCache, initialScale) {

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
                wv.loadUrl("javascript:(function() { " +
                        "document.getElementById('advanced_menu_toggle').style.display='none'; " +
                        "})()");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
                ((MainActivity) activity).showProgressBar();
                urlCache.register(urlBase, url, UrlCache.ONE_HOUR);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
                ((MainActivity) activity).hideProgressBar();
            }
        });
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        if (url.contains(urlBase)) {
            // This is my web site, so do not override; let my WebView load the page
            return false;
        }

        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return urlCache.load(url);
    }
}