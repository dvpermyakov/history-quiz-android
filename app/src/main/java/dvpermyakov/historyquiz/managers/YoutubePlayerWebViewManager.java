package dvpermyakov.historyquiz.managers;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import dvpermyakov.historyquiz.interfaces.OnYoutubePlayerListener;

/**
 * Created by dvpermyakov on 26.03.17.
 */

public class YoutubePlayerWebViewManager {
    private Activity activity;
    private WebView webPlayer;
    private boolean wasInFullscreen;

    public YoutubePlayerWebViewManager(Activity activity, final WebView webPlayer, String url, final OnYoutubePlayerListener listener) {
        this.activity = activity;
        this.webPlayer = webPlayer;

        webPlayer.getSettings().setJavaScriptEnabled(true);
        webPlayer.setWebViewClient(new WebViewClient() {
            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String code = "(function() { " +
                            "document.getElementById('koya_elem_0_17').style.display = 'none'; " +
                            "document.getElementsByClassName('_mevb')[0].style.display = 'none'; " +
                            "})();";
                    view.evaluateJavascript(code, null);
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webPlayer.setVisibility(View.VISIBLE);
                        listener.onUIComplete();
                    }
                }, 2000);
            }
        });
        webPlayer.setWebChromeClient(new MyWebClient());
        webPlayer.loadUrl(url);
    }

    public void setVisibility(int visibility) {
        webPlayer.setVisibility(visibility);
    }

    public boolean isAdviceStartTest() {
        return wasInFullscreen;
    }

    public void pause() {
        webPlayer.onPause();
        webPlayer.pauseTimers();
    }

    public void start() {
        webPlayer.resumeTimers();
        webPlayer.onResume();
    }

    private class MyWebClient extends WebChromeClient {
        private View customView;
        private WebChromeClient.CustomViewCallback customViewCallback;
        private int originalOrientation;
        private int originalSystemUiVisibility;

        @Override
        public Bitmap getDefaultVideoPoster() {
            return BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), 2130837573);
        }

        @Override
        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            wasInFullscreen = true;

            if (this.customView != null) {
                onHideCustomView();
                return;
            }

            customView = paramView;
            customViewCallback = paramCustomViewCallback;

            originalSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            if (originalSystemUiVisibility == 0) {
                originalSystemUiVisibility = 1024;
            }
            originalOrientation = activity.getRequestedOrientation();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            ((FrameLayout) activity.getWindow().getDecorView()).addView(customView, new FrameLayout.LayoutParams(-1, -1));
            activity.getWindow().getDecorView().setSystemUiVisibility(5894);
        }

        @Override
        public void onHideCustomView() {
            ((FrameLayout) activity.getWindow().getDecorView()).removeView(customView);
            customView = null;

            activity.getWindow().getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
            activity.setRequestedOrientation(originalOrientation);

            customViewCallback.onCustomViewHidden();
            customViewCallback = null;
        }
    }
}
