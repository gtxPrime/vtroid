package com.gtxprime.vtroid.Activities;

import static android.content.ContentValues.TAG;
import static com.gtxprime.vtroid.Utils.Utils.loadShow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.greenfrvr.rubberloader.RubberLoaderView;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Search.HistorySQLite;
import com.gtxprime.vtroid.Search.VisitedPages;
import com.gtxprime.vtroid.Utils.Unity;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;
import com.gtxprime.vtroid.WatchHistory.WListSQLite;
import com.gtxprime.vtroid.WatchHistory.Watched;

import org.apache.commons.io.FilenameUtils;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    Dialog LoadDialog, keyCheckDialog;
    Window window;
    WebSettings webSettings;
    static boolean isRunning = false, watchedAd = false;
    String publicUrl;
    View decorView;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        isRunning = true;

        decorView = getWindow().getDecorView();

        window = WebActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(WebActivity.this, R.color.black));


        webView = findViewById(R.id.myWeb);
        Intent intent = getIntent();
        String link = intent.getStringExtra("links");
        LoadDialog = new Dialog(WebActivity.this, R.style.MyDialogStyle);
        LoadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LoadDialog.setCancelable(true);
        LoadDialog.setContentView(R.layout.dialog_load);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMixedContentMode(0);
        webSettings.setMixedContentMode(0);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(link);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setPadding(0, 0, 0, 0);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            if (webView.getUrl().contains("vtroid") || webView.getUrl().contains("adminstreamx")) {
                Dialog movieDialog;
                movieDialog = new Dialog(WebActivity.this, R.style.MyDialogStyle);
                movieDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                movieDialog.setCancelable(true);
                movieDialog.setContentView(R.layout.dialog_play);

                TextView play = movieDialog.findViewById(R.id.dialogPlay);
                TextView download = movieDialog.findViewById(R.id.dialogDownload);

                download.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(Intent.createChooser(browserIntent, "Open with"));
                    movieDialog.dismiss();
                });

                play.setOnClickListener(v -> {
                    publicUrl = url;
                    movieDialog.dismiss();
                    Watched vp = new Watched();
                    String name = FilenameUtils.getName(url);
                    name = name.replace("%20", " ").replace("%5B", "[").replace("%28", "(").replace("%29", ")").replace("%2D", "-").replace("_", " ").replace("%5D", "]").replace(".mkv?dl=0", "");
                    vp.title = name;
                    vp.link = url;
                    new WListSQLite(WebActivity.this).addPageToHistory(vp);
                    startActivity(new Intent(WebActivity.this, PlayerActivity.class).putExtra("playLink", url));
                });

                movieDialog.show();
            }

        });
        webView.setWebViewClient(new Callback());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                VisitedPages vp = new VisitedPages();
                vp.title = title;
                vp.link = webView.getUrl();
                if (!link.contains("vtroid")) {
                    new HistorySQLite(WebActivity.this).addPageToHistory(vp);
                }
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                webView.loadUrl("javascript:window.confirm = function() { return false; };");
                result.cancel();
                return true;
            }
        });

    }

    private void showLayouts() {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void hideLayouts() {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideLayouts();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showLayouts();
        }
    }



    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public class Callback extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            LoadDialog.dismiss();
            view.setInitialScale(1);
            loadShow = true;



            if (url != null && url.startsWith("https://drive.google.com")) {
                // Execute your JavaScript code here
                webView.loadUrl("javascript:(function() { " +
                        "    var button = document.getElementById('uc-download-link');" +
                        "    if (button) {" +
                        "        button.click();" +
                        "    }" +
                        "})()");
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!loadShow && isRunning) {
                new Handler().postDelayed(() -> LoadDialog.show(), 100);

            }

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            if (url.contains("vtroid-live-match")) {
                // Your custom handling when the condition is true
                webView.stopLoading();
                webView.goBack();
                String newLink = url.replace("vtroid-live-match", "");
                startActivity(new Intent(WebActivity.this, PlayerActivity.class).putExtra("playLink", newLink));
                Toast.makeText(WebActivity.this, newLink, Toast.LENGTH_SHORT).show();

                view.stopLoading();
                onBackPressed();
                return true; // Return true to indicate that the WebView has handled the URL
            } else {
                // Allow WebView to handle the URL normally when the condition is false
                return false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadShow = false;
        isRunning = false;
        watchedAd = false;
    }


}