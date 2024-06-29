package com.pab.masakyuapp.view;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends BottomSheetDialogFragment {
    public static final String TAG = "BottomSheetFragment";
    private static final String URL = "http://192.168.0.7:3000/auth/register";
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_sign_up, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText textBuatUsername = view.findViewById(R.id.textBuatUsername);
        EditText textBuatEmail = view.findViewById(R.id.textBuatEmail);
        EditText textBuatPassword = view.findViewById(R.id.textBuatPassword);
        EditText textCPassword = view.findViewById(R.id.textCPassword);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        TextView tv_yukMasuk = view.findViewById(R.id.tv_yukMasuk);
        tv_yukMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInBottomSheet();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textBuatUsername.getText().toString();
                String email = textBuatEmail.getText().toString();
                String password = textBuatPassword.getText().toString();
                String cPassword = textCPassword.getText().toString();

                if(!password.equals(cPassword)){
                    Toast.makeText(getContext(), "Password tidak sama", Toast.LENGTH_SHORT).show();
                    return;
                }

                postData(username, email, password);
            }
        });

    }

    private void postData(String username,String email, String password){
        requestQueue = Volley.newRequestQueue(getContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
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
                        Log.d("resister Response", response.toString());
                        try {
                            //ubah response json menjadi string
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            showSignInBottomSheet();
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

    private void showSignInBottomSheet() {
        dismiss();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.executePendingTransactions();
        LoginFragment signInBottomSheet = new LoginFragment();
        signInBottomSheet.show(fragmentManager, TAG);
    }
}
