package com.pab.masakyuapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pab.masakyuapp.R;
import com.pab.masakyuapp.model.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuatResepFragment extends Fragment {
    private Button btn_buat;
    private Config config;
    private String BASE_URL;
    private EditText nama_resep, bahan, langkah, porsi, waktu_masak;
    private RequestQueue queue;

    public BuatResepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buat_resep, container, false);

        config = new Config(getContext());
        BASE_URL = config.getBaseUrl();

        nama_resep = view.findViewById(R.id.editTextJudul_resep);
        bahan = view.findViewById(R.id.etBahan);
        langkah = view.findViewById(R.id.etLangkah);
        porsi = view.findViewById(R.id.editTextPorsi);
        waktu_masak = view.findViewById(R.id.editTextLamaMemasak);

        btn_buat = view.findViewById(R.id.btn_buat);
        btn_buat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buatResep();
            }
        });
        return view;
    }

    public void buatResep() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", null);

        if (token == null) {
            // Handle the case when there is no token
            Toast.makeText(getContext(), "Token tidak ditemukan, silakan login kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String namaResep = nama_resep.getText().toString();
        String porsiResep = porsi.getText().toString();
        String waktuMasakResep = waktu_masak.getText().toString();
        String bahanResep = bahan.getText().toString();
        String langkahResep = langkah.getText().toString();

        // Validasi input
        if (namaResep.isEmpty() || bahanResep.isEmpty() || langkahResep.isEmpty() || porsiResep.isEmpty() || waktuMasakResep.isEmpty()) {
            Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Konversi porsi ke integer
        int porsiInt;
        try {
            porsiInt = Integer.parseInt(porsiResep);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Porsi harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        queue = Volley.newRequestQueue(getContext());
        String URL = BASE_URL + "/resep/create";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nama_resep", namaResep);
            jsonObject.put("porsi", porsiInt); // Kirim sebagai integer
            jsonObject.put("waktu_masak", waktuMasakResep);
            jsonObject.put("bahan", bahanResep);
            jsonObject.put("langkah", langkahResep);
            Log.d("Request JSON", jsonObject.toString());  // Log JSON yang dikirim
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Login Response", response.toString());
                        try {
                            // Ubah response json menjadi string
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            clearBackStack();
                            BerandaFragment berandaFragment = new BerandaFragment();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.framelayout_beranda, berandaFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
    private void clearBackStack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}
