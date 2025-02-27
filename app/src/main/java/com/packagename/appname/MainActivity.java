package com.packagename.appname;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.annotation.NonNull;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    private AdvancedWebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean preventCaching = false;

        hideSystemUI();

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setMixedContentAllowed(false);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface");
        mWebView.loadUrl("https://github.com/ErikliPizza", preventCaching);
        mWebView.addPermittedHostname("github.com");
        // Add this WebViewClient to intercept image clicks and force downloads
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Check if the URL ends with an image extension
                if (url.matches(".*\\.(png|jpe?g|gif)$")) {
                    // Force download of the image
                    String fileName = url.substring(url.lastIndexOf('/') + 1);
                    if (AdvancedWebView.handleDownload(MainActivity.this, url, fileName)) {
                        Toast.makeText(MainActivity.this, "Downloading " + fileName, Toast.LENGTH_SHORT).show();
                    }
                    return true; // Prevent the webview from loading the image
                }
                return false; // For other URLs, let the webview handle them as usual
            }
        });
        // ...
    }

    // Hide navigation and status bar
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    // Show navigation when user swipes
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) { return; }
        // ...
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(
            final String url,
            final String suggestedFilename,
            final String mimeType,
            long contentLength, String contentDisposition, String userAgent
    ) {
        try {
            // For non-blob URLs, use AdvancedWebView's default download handler.
            if (AdvancedWebView.handleDownload(this, url, suggestedFilename)) {
                Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onExternalPageRequest(String url) {
        if (url.startsWith("https://api.whatsapp.com/")) {
            openWhatsApp(url);
        } else if (url.startsWith("tel:")) {
            openDial(url);
        } else if (url.startsWith("mailto")) {
            openMail(url);
        } else {
            openInChrome(url);
        }
    }
    private void openInChrome(@NonNull String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome"); // Force open in Chrome

            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // If Chrome is not installed, open with default browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    private void openWhatsApp(@NonNull String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't find WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }
    private void openDial(@NonNull String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No cellphone", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMail(@NonNull String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't find an email app", Toast.LENGTH_SHORT).show();
        }
    }
}