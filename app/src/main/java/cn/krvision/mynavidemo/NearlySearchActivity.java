package cn.krvision.mynavidemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


public class NearlySearchActivity extends AppCompatActivity implements NearbySearch.NearbyListener {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearly_search);


        //获取附近实例（单例模式）
        NearbySearch mNearbySearch = NearbySearch.getInstance(getApplicationContext());
//设置附近监听
        NearbySearch.getInstance(getApplicationContext()).addNearbyListener(this);



        initView();


    }

    @Override
    public void onUserInfoCleared(int i) {

    }

    @Override
    public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int i) {

    }

    @Override
    public void onNearbyInfoUploaded(int i) {

    }


    private void initView() {


        String url ="http://navigation.krvision.cn/newsshare/?id=1&user=15669933226";
//        webView = (WebView) findViewById(R.id.tbsContent);
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.tbsContent);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);//启用html5数据库功能
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 设置加载进来的页面自适应手机屏幕
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

}
