package instasaver.coolapp_engine.it.instasaver.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import instasaver.coolapp_engine.it.instasaver.R;

public class WebViewPers {

    private WebView webView;

    public WebViewPers(WebView webView){
        this.webView = webView;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSavePassword(false);

        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        webView.getSettings().setBlockNetworkImage(true);

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public WebView getWebView(){
        return webView;
    }

    public void setOnFinish(String urlOrJavascript, Context context){
        webView.post(() -> {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    view.post(() -> {
                        webView.loadUrl(urlOrJavascript);
                    });
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if(Internet.isConnected(context))
                        Toast.makeText(context, context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                    else
                        Toast.makeText(context, context.getString(R.string.error_load_failed), Toast.LENGTH_LONG);
                }
            });
        });
    }

    public void removeOnFinish(){
        webView.post(() -> {
            webView.setWebViewClient(null);
        });
    }

    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object object, String name){
        webView.post(() -> {
            webView.addJavascriptInterface(object, name);
        });
    }

    public void loadUrl(String url){
        webView.post(() -> {
            webView.loadUrl(url);
        });
    }

    public void stopLoading(){
        webView.post(() -> {
            webView.stopLoading();
        });
    }

    public void scrollTo(int x, int y){
        webView.scrollTo(x, y);
    }

    public void scrollBy(int x, int y){
        webView.scrollBy(x, y);
    }

}
