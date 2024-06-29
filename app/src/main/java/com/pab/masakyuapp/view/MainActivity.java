package com.pab.masakyuapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pab.masakyuapp.R;
import com.pab.masakyuapp.model.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String TAG = "MainActivity";
    private Config config;
    private String BASE_URL, URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = new Config(this);
        BASE_URL = config.getBaseUrl();
        Log.e(TAG, "onCreate: BASE_URL = " + BASE_URL );

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", null);

        getWindow().setStatusBarColor(getResources().getColor(R.color.merahjambu));
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        if (token != null) {
            isLogin(token);
        } else {
            findViewById(R.id.buttonShowBottomSheet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginFragment();
                }
            });
        }
    }

    private void isLogin(String token) {
        requestQueue = Volley.newRequestQueue(this);
        URL = BASE_URL + "/auth/validation";
        Log.e(TAG, "isLogin: URL = " + URL);
        String fullUrl = URL + "?token=" + token;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null,
                this::handleLoginResponse,
                this::handleLoginError);

        requestQueue.add(jsonObjectRequest);
    }

    private void handleLoginResponse(JSONObject response) {
        try {
            boolean valid = response.getBoolean("valid");
            if (valid) {
                Intent intent = new Intent(MainActivity.this, BerandaActivity.class);
                startActivity(intent);
                finish();
            } else {
                showLoginFragment();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLoginError(VolleyError error) {
        error.printStackTrace();
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null && networkResponse.data != null) {
            String errorMessage = new String(networkResponse.data);
            try {
                JSONObject errorObject = new JSONObject(errorMessage);
                String message = errorObject.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                findViewById(R.id.buttonShowBottomSheet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoginFragment();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "handleLoginError: "+e.getMessage() );
                Toast.makeText(this, "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("Login Error Response", "Network response is null");
            Toast.makeText(this, "Login gagal, cek koneksi Anda", Toast.LENGTH_SHORT).show();
            findViewById(R.id.buttonShowBottomSheet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginFragment();
                }
            });
        }
    }

    private void showLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.show(getSupportFragmentManager(), LoginFragment.TAG);
    }
}
