package com.warrior.developer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class BrowserActivity extends AppCompatActivity {

    private WebView web;
    private ProgressBar loading;
    private String email, password, url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        url = getIntent().getStringExtra("url");
        if (url == null || url.isEmpty()) {
            url = "https://schedule.cf-grcon-isl-pakistan.com/schedule/login/grcon-isl-pakistan/WORK_National_VISA?view=free";
        }

        web = findViewById(R.id.webview);
        loading = findViewById(R.id.loading);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadsImagesAutomatically(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setUserAgentString(
            "Mozilla/5.0 (Linux; Android 13; SM-S918B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        );
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setSupportMultipleWindows(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(web, true);

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                loading.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String pageUrl) {
                super.onPageFinished(view, pageUrl);
                view.evaluateJavascript(buildHelperJs(), null);
            }
        });

        web.loadUrl(url);
    }

    private String buildHelperJs() {
        String safeEmail = email == null ? "" : email.replace("\\", "\\\\").replace("'", "\\'");
        String safePass = password == null ? "" : password.replace("\\", "\\\\").replace("'", "\\'");

        return "(function(){\n" +
               "  if (window.__warriorInjected) return; window.__warriorInjected = true;\n" +
               "  var EMAIL = '" + safeEmail + "';\n" +
               "  var PASS  = '" + safePass + "';\n" +
               "  var FREE_URL = 'https://schedule.cf-grcon-isl-pakistan.com/schedule/login/grcon-isl-pakistan/WORK_National_VISA?view=free';\n" +
               "  function log(m){try{console.log('[Warrior] '+m);}catch(e){}}\n" +
               "  function tryTurnstile(){\n" +
               "    try{\n" +
               "      var ifr = document.querySelectorAll('iframe[src*=\"challenges.cloudflare.com\"], iframe[src*=\"turnstile\"]');\n" +
               "      ifr.forEach(function(f){\n" +
               "        try{\n" +
               "          var d = f.contentDocument || (f.contentWindow && f.contentWindow.document);\n" +
               "          if(!d) return;\n" +
               "          var cb = d.querySelector('input[type=\"checkbox\"]');\n" +
               "          if(cb && !cb.checked){ cb.click(); log('clicked turnstile checkbox'); }\n" +
               "        }catch(e){}\n" +
               "      });\n" +
               "      var local = document.querySelector('input[name=\"cf-turnstile-response\"]');\n" +
               "      if(local){\n" +
               "        var lbl = document.querySelector('label.cb-lb input, .cf-turnstile input[type=\"checkbox\"]');\n" +
               "        if(lbl && !lbl.checked) lbl.click();\n" +
               "      }\n" +
               "    }catch(e){ log('cf err '+e); }\n" +
               "  }\n" +
               "  var cfTimer = setInterval(tryTurnstile, 800);\n" +
               "  setTimeout(function(){ clearInterval(cfTimer); }, 60000);\n" +
               "  function tryFill(){\n" +
               "    var name = document.getElementById('name');\n" +
               "    var pwd  = document.getElementById('password');\n" +
               "    var form = name && name.form;\n" +
               "    if(!name || !pwd || !form) return false;\n" +
               "    if(window.__warriorFilled) return true;\n" +
               "    window.__warriorFilled = true;\n" +
               "    name.value = EMAIL;\n" +
               "    pwd.value  = PASS;\n" +
               "    name.dispatchEvent(new Event('input',{bubbles:true}));\n" +
               "    pwd.dispatchEvent(new Event('input',{bubbles:true}));\n" +
               "    log('filled credentials');\n" +
               "    try{\n" +
               "      var act = form.getAttribute('action') || '';\n" +
               "      if(act.indexOf('view=free') === -1){\n" +
               "        form.setAttribute('action', FREE_URL);\n" +
               "      }\n" +
               "    }catch(e){}\n" +
               "    setTimeout(function(){\n" +
               "      try{ if(typeof request3CA==='function') request3CA(); }catch(e){}\n" +
               "      try{ form.submit(); log('form submitted'); }catch(e){\n" +
               "        var btn = form.querySelector('button[type=\"submit\"]'); if(btn) btn.click();\n" +
               "      }\n" +
               "    }, 600);\n" +
               "    return true;\n" +
               "  }\n" +
               "  var fillTimer = setInterval(function(){ if(tryFill()) clearInterval(fillTimer); }, 700);\n" +
               "  setTimeout(function(){ clearInterval(fillTimer); }, 60000);\n" +
               "})();";
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
