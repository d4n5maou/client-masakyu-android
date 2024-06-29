package com.pab.masakyuapp.view;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pab.masakyuapp.R;
import com.pab.masakyuapp.model.Config;
import com.pab.masakyuapp.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditResepFragment extends Fragment {

    private EditText etEditNamaResep, etEditPorsi, etEditWaktu,etEditBahan,etEditLangkah;
    private RequestQueue queue;
    private Button btnEditResep;
    private int idResep;
    private String BASE_URL;
    private Config config;

    public EditResepFragment(){

    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_resep, container, false);

        config = new Config(getContext());
        BASE_URL = config.getBaseUrl();

        etEditNamaResep = view.findViewById(R.id.etEditNamaResep);
        etEditPorsi = view.findViewById(R.id.etEditPorsi);
        etEditWaktu = view.findViewById(R.id.etEditWaktu);
        etEditBahan = view.findViewById(R.id.etEditBahan);
        etEditLangkah = view.findViewById(R.id.etEditLangkah);
        btnEditResep = view.findViewById(R.id.btnEditResep);

        // Mendapatkan ID resep dari arguments
        if (getArguments() != null) {
            idResep = getArguments().getInt("RESEP_ID");
        }

        setDataToField();

        btnEditResep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        return view;
    }

    public void setDataToField() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", null);

        if (token == null) {
            // Token tidak ditemukan, tampilkan pesan atau lakukan tindakan yang sesuai
            return;
        }

        String URL = BASE_URL+"/resep/get/" + idResep;
        queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", response);
                // Parsing response dan menampilkan data ke TextView
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String namaResep = jsonObject.getString("nama_resep");
                    String bahan = jsonObject.getString("bahan");
                    int porsi = jsonObject.getInt("porsi");
                    String langkah = jsonObject.getString("langkah");
                    String waktuMasak = jsonObject.getString("waktu_masak");
                    Log.d("TAG", "onResponse: " + namaResep);

                    etEditNamaResep.setText(namaResep);
                    etEditBahan.setText(bahan);
                    etEditPorsi.setText(String.valueOf(porsi));
                    etEditLangkah.setText(langkah);
                    etEditWaktu.setText(waktuMasak);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }

    public void updateData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        int uidInt = sharedPreferences.getInt("UID", -1);
        String token = sharedPreferences.getString("TOKEN", null);

        if (token == null || uidInt == -1) {
            // Token atau UID tidak ditemukan, tampilkan pesan atau lakukan tindakan yang sesuai
            return;
        }

        String namaResep = etEditNamaResep.getText().toString();
        String porsiResep = etEditPorsi.getText().toString();
        String waktuMasakResep = etEditWaktu.getText().toString();
        String bahanResep = etEditBahan.getText().toString();
        String langkahResep = etEditLangkah.getText().toString();

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

        String uid = String.valueOf(uidInt);
        String url = BASE_URL+"/resep/edit/"+idResep+"/"+uid;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ubah response json menjadi string
                            Log.d("TAG", "onResponse: "+response);
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            clearBackStack();
                            AkunFragment akunFragment = new AkunFragment();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.framelayout_beranda, akunFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
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

        requestQueue.add(jsonObjectRequest);
    }

    private void clearBackStack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
