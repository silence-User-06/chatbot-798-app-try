package com.chatbot798.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView webView;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "chatbot798_prefs";
    private static final String KEY_API = "gemini_api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new Bridge(), "Android");
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    class Bridge {
        @JavascriptInterface
        public String getApiKey() {
            return prefs.getString(KEY_API, "");
        }

        @JavascriptInterface
        public void saveApiKey(String key) {
            prefs.edit().putString(KEY_API, key).apply();
        }

        @JavascriptInterface
        public void onBackPressed() {
            runOnUiThread(() -> MainActivity.this.onBackPressed());
        }

        @JavascriptInterface
        public void showMenu() {
            runOnUiThread(() -> {
                String[] opts = {"API Anahtarını Değiştir", "Sohbeti Temizle"};
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Ayarlar")
                    .setItems(opts, (d, i) -> {
                        if (i == 0) {
                            prefs.edit().remove(KEY_API).apply();
                            webView.loadUrl("javascript:showOverlay()");
                            Toast.makeText(MainActivity.this, "API anahtarı silindi", Toast.LENGTH_SHORT).show();
                        } else {
                            webView.loadUrl("javascript:location.reload()");
                        }
                    }).show();
            });
        }

        @JavascriptInterface
        public void openUrl(String url) {
            runOnUiThread(() -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
        }
    }
}
