package com.warrior.developer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class KeyActivity extends AppCompatActivity {

    public static final String VPS_BASE = "http://80.241.210.193:5001";
    public static final String PREFS = "warrior_prefs";

    private EditText etKey;
    private Button btnValidate;
    private ProgressBar progress;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (sp.getBoolean("authorized", false)) {
            String email = sp.getString("email", "");
            String password = sp.getString("password", "");
            String url = sp.getString("target_url",
                    "https://schedule.cf-grcon-isl-pakistan.com/schedule/login/grcon-isl-pakistan/WORK_National_VISA?view=free");
            openBrowser(email, password, url);
            return;
        }

        setContentView(R.layout.activity_key);
        etKey = findViewById(R.id.etKey);
        btnValidate = findViewById(R.id.btnValidate);
        progress = findViewById(R.id.progress);
        tvStatus = findViewById(R.id.tvStatus);

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = etKey.getText().toString().trim();
                if (key.isEmpty()) {
                    Toast.makeText(KeyActivity.this, "Enter a key", Toast.LENGTH_SHORT).show();
                    return;
                }
                validateKey(key);
            }
        });
    }

    private String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void validateKey(final String key) {
        progress.setVisibility(View.VISIBLE);
        btnValidate.setEnabled(false);
        tvStatus.setText("Verifying with VPS...");
        tvStatus.setTextColor(0xFF8B949E);

        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    URL url = new URL(VPS_BASE + "/api/validate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);

                    JSONObject body = new JSONObject();
                    body.put("key", key);
                    body.put("device_id", getDeviceId());

                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream(),
                            "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();
                    return new JSONObject(sb.toString());
                } catch (Exception e) {
                    try {
                        JSONObject err = new JSONObject();
                        err.put("status", "error");
                        err.put("message", "Connection failed: " + e.getMessage());
                        return err;
                    } catch (Exception ignored) {
                        return null;
                    }
                }
            }

            @Override
            protected void onPostExecute(JSONObject res) {
                progress.setVisibility(View.GONE);
                btnValidate.setEnabled(true);
                if (res == null) {
                    tvStatus.setText("Unknown error");
                    tvStatus.setTextColor(0xFFFF7B72);
                    return;
                }
                String status = res.optString("status", "error");
                if ("success".equals(status)) {
                    String email = res.optString("email", "");
                    String password = res.optString("password", "");
                    String url = res.optString("target_url",
                            "https://schedule.cf-grcon-isl-pakistan.com/schedule/login/grcon-isl-pakistan/WORK_National_VISA?view=free");

                    SharedPreferences.Editor e = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    e.putBoolean("authorized", true);
                    e.putString("key", key);
                    e.putString("email", email);
                    e.putString("password", password);
                    e.putString("target_url", url);
                    e.apply();

                    tvStatus.setText("Authorized \u2713 Opening...");
                    tvStatus.setTextColor(0xFF56D364);
                    openBrowser(email, password, url);
                } else {
                    tvStatus.setText(res.optString("message", "Invalid key"));
                    tvStatus.setTextColor(0xFFFF7B72);
                }
            }
        }.execute();
    }

    private void openBrowser(String email, String password, String url) {
        Intent i = new Intent(this, BrowserActivity.class);
        i.putExtra("email", email);
        i.putExtra("password", password);
        i.putExtra("url", url);
        startActivity(i);
        finish();
    }
}
