package com.pab.masakyuapp.view;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pab.masakyuapp.R;
import com.pab.masakyuapp.model.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends BottomSheetDialogFragment {

    private RequestQueue requestQueue;
    public static final String TAG = "BottomSheetFragment";
    private String BASE_URL;
    private Config config;

    //buat shared preferences untuk simpan token login
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        config = new Config(getContext());
        BASE_URL = config.getBaseUrl();
        Log.e(TAG, "onViewCreated: "+BASE_URL );

        sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        EditText textEmailMasuk = view.findViewById(R.id.textEmailMasuk);
        EditText textPasswordMasuk = view.findViewById(R.id.textPasswordMasuk);
        Button btnYukMasuk = view.findViewById(R.id.btnYukMasuk);
        TextView tv_buatDisini = view.findViewById(R.id.tv_buatDisini);

        tv_buatDisini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpBottomSheet();
            }
        });

        btnYukMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textEmailMasuk.getText().toString().trim().toLowerCase();
                String password = textPasswordMasuk.getText().toString().trim().toLowerCase();

                login(email, password);
            }
        });
    }

    // Method login
    private void login(String email, String password) {
        requestQueue = Volley.newRequestQueue(getContext());
        String URL = BASE_URL + "/auth/login";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            Log.d("Login Request JSON", jsonObject.toString());  // Log JSON yang dikirim
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Login Response", response.toString());
                        try {
                            //ubah response json menjadi string
                            String message = response.getString("message");
                            String token = response.getString("token");
                            Log.d("Login Token", token);
                            JSONObject user = response.getJSONObject("data");
                            int uid = user.getInt("id");
                            String username = user.getString("username");
                            String email = user.getString("email");

                            //simpan token ke shared preferences
                            editor.putString("TOKEN", token);
                            editor.putInt("UID", uid);
                            editor.putString("USERNAME", username);
                            editor.putString("EMAIL", email);
                            editor.apply();

                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), BerandaActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String errorMessage = new String(networkResponse.data);
                    Log.e("Login Error Response", errorMessage);
                    // Menampilkan pesan kesalahan dari response json ke string
                    try {
                        JSONObject errorObject = new JSONObject(errorMessage);
                        String message = errorObject.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Login Error Response", "Network response is null");
                    Toast.makeText(getContext(), "Login gagal, cek koneksi Anda", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    private void showSignUpBottomSheet() {
        dismiss();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.executePendingTransactions();
        RegisterFragment signUpBottomSheet = new RegisterFragment();
        signUpBottomSheet.show(fragmentManager, TAG);
    }
}
