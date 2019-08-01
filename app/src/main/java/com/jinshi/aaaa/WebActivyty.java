package com.jinshi.aaaa;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jinshi.yifuguojiwapapp.mt4webh5.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebActivyty extends Activity implements View.OnClickListener {
    private WebView webGame;
    //    private ProgressBar pbWebLoad;
    //    private String url = "http://typhoon.zjwater.gov.cn/default.aspx";
    private String url;
    private String urls;
    private String emptyUrl = "";
    private Boolean isOpen;
    private Handler handler;
    private TextView tvTishi;


    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        init();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    private void init() {

        webGame = findViewById(R.id.wv_webGame);
        webGame.clearCache(true);
        webGame.clearHistory();
        webGame.requestFocus();

//        pbWebLoad = findViewById(R.id.pb_webloading);
        tvTishi = findViewById(R.id.tv_tishi);
//        pbWebLoad.setDrawingCacheBackgroundColor(getResources().getColor(R.color.black));
//        pbWebLoad.setDrawingCacheBackgroundColor(getResources().getColor(R.color.red));
        WebSettings webSettings = webGame.getSettings();//JavaScript支持
        webSettings.setAppCacheEnabled(false);//
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        String appCachePath = getApplicationContext().getCacheDir()
                .getAbsolutePath() + "/webcache";
        // 设置 Application Caches 缓存目录
        webSettings.setAppCachePath(appCachePath);
        webSettings.setDatabasePath(appCachePath);

        webGame.getSettings().setDomStorageEnabled(true);
        //WebView加载页面优先使用缓存加载
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


//        webGame.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.startsWith("http:") || url.startsWith("https:")) {
//                    return false;
//                }
////                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////                startActivity(intent);
//                view.loadUrl(url);
//                return true;
//            }
//        });
        webGame.setWebViewClient(new WebViewClient() {

            @Override//拦截下载链接
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return false;
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        getApplicationContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Toast.makeText(WebActivyty.this, "暂无应用打开此链接", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        webGame.setWebChromeClient(new WebChromeClient() {
            //文件上传适配
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    findViewById(R.id.iv_lead).setVisibility(View.GONE);
//                    pbWebLoad.setVisibility(View.GONE);
                } else {
//                    pbWebLoad.setVisibility(View.VISIBLE);
//                    pbWebLoad.setProgress(newProgress);

                }
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        webGame.setVisibility(View.VISIBLE);
                        tvTishi.setVisibility(View.GONE);
                        webGame.loadUrl(urls);
                        break;
                    case 2:
                        webGame.setVisibility(View.GONE);
                        tvTishi.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        getUrl();
                        break;
                    case 4:
//                        Toast.makeText(WebActivyty.this, "44444", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ReadyActivity.class));
                        finish();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        getIsOpen();
//        webGame.loadUrl(url);
//        pbWebLoad.setVisibility(View.GONE);
        tvTishi.setOnClickListener(this);
    }


    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");//图片上传
//        i.setType("file/*");//文件上传
        i.setType("*/*");//文件上传
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    // 3.选择图片后处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            // Uri result = (((data == null) || (resultCode != RESULT_OK)) ? null : data.getData());
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else {
            //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
            //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
            //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
        }
    }

    // 4. 选择内容回调到Html页面
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO 自动生成的方法存根
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webGame.canGoBack()) {//当webview不是处于第一页面时，返回上一个页面
                webGame.goBack();
                return true;
            } else {//当webview处于第一页面时,直接退出程序
                System.exit(0);
            }


        }
        return super.onKeyDown(keyCode, event);
    }

    private void getIsOpen() {
        final String url = "https://leancloud.cn:443/1.1/classes/open/5d40fd1fc8959c008a514a98";
        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder().add("", "").build();
        final Message msg = Message.obtain();

        Request request = new Request.Builder().url(url).addHeader("X-LC-Id", "TXgSJqxr5KJiEu4n1WtBetgl-gzGzoHsz").addHeader("X-LC-Key", "XnEQDD0BVyMCU8m2AdfWhUYC").get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
//                urls = "";
                msg.what = 4;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    Boolean isOpen = jsonObject.getBoolean("switch");
//                    urls = url1;
//                    urls = url1.replaceAll("\\\\", "");
                    if (isOpen) {
                        msg.what = 3;
                    } else {
                        msg.what = 4;
                    }
//                    msg.what = 1;
                    handler.sendMessage(msg);
//                    webGame.loadUrl(url);
//                    if (isOpen) {
//                        webGame.loadUrl(url);
//                    } else {
//                        webGame.loadUrl(emptyUrl);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.e("ttt", data);

            }
        });
    }

    private void getUrl() {
        //给予访问文件sd卡权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                     != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            }
        } else {
        }
        final String url = "https://leancloud.cn:443/1.1/classes/url/5d40fd70c8959c008a514bd8";
        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder().add("", "").build();
        final Message msg = Message.obtain();

        Request request = new Request.Builder().url(url).addHeader("X-LC-Id", "TXgSJqxr5KJiEu4n1WtBetgl-gzGzoHsz").addHeader("X-LC-Key", "XnEQDD0BVyMCU8m2AdfWhUYC").get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                urls = "";
                msg.what = 2;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String url1 = jsonObject.getString("url");
                    urls = url1;
//                    urls = url1.replaceAll("\\\\", "");

                    msg.what = 1;
                    handler.sendMessage(msg);
//                    webGame.loadUrl(url);
//                    if (isOpen) {
//                        webGame.loadUrl(url);
//                    } else {
//                        webGame.loadUrl(emptyUrl);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("ttt", data);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIsOpen();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tishi:
                getUrl();
        }
    }


}
