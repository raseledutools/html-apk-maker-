package com.rasel.rasfocus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ১. স্ট্যাটাস বার হাইড করে ফুল স্ক্রিন করা (অ্যান্ড্রয়েড ডিসপ্লে স্টাইল)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                           WindowManager.LayoutParams.FLAG_FULLSCREEN);

        webView = new WebView(this);
        setContentView(webView);

        // ২. WebView কনফিগারেশন (HTML/JS চালানোর জন্য)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // জাভাস্ক্রিপ্ট অন করা
        webSettings.setDomStorageEnabled(true); // লোকাল স্টোরেজ সাপোর্ট
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        // ৩. জাভাস্ক্রিপ্ট এবং অ্যান্ড্রয়েড জাভার মধ্যে ব্রিজ (AndroidBridge)
        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // ৪. assets ফোল্ডার থেকে index.html লোড করা
        webView.loadUrl("file:///android_asset/index.html");
    }

    // ৫. এই ইন্টারফেসটি HTML এর বাটন থেকে কল করা যাবে
    public class WebAppInterface {
        Activity mContext;

        WebAppInterface(Activity c) {
            mContext = c;
        }

        // এটি কল করলে ফোনের অ্যাক্সেসিবিলিটি সেটিংস ওপেন হবে
        @JavascriptInterface
        public void openAccessibilitySettings() {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            mContext.startActivity(intent);
            Toast.makeText(mContext, "অনুগ্রহ করে RasFocus+ অন করুন", Toast.LENGTH_LONG).show();
        }

        // সেশন স্টার্ট হলে জাভা থেকে মেসেজ দেখাবে
        @JavascriptInterface
        public void startFocusSession(int seconds) {
            Toast.makeText(mContext, "ফোকাস মোড চালু হয়েছে: " + (seconds/60) + " মিনিটের জন্য", Toast.LENGTH_SHORT).show();
            // এখানে তুমি চাইলে ব্যাকগ্রাউন্ড সার্ভারকে সিগন্যাল পাঠাতে পারো
        }

        // সেশন শেষ হলে
        @JavascriptInterface
        public void endFocusSession() {
            Toast.makeText(mContext, "সেশন শেষ! আপনি এখন স্বাধীন।", Toast.LENGTH_SHORT).show();
        }
    }

    // ব্যাক বাটন টিপলে যেন অ্যাপ হুট করে বন্ধ না হয় (ব্রাউজারের মতো কাজ করবে)
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
