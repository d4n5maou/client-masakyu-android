package com.pab.masakyuapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pab.masakyuapp.R;
import com.pab.masakyuapp.model.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailResepFragment extends Fragment {

    private TextView tvDetailNama, tvDetailBahan, tvDetailPorsi, tvDetailLangkah, tvDetailWaktu;
    private RequestQueue queue;
    private int idResep;
    private String BASE_URL;
    private Config config;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_resep, container, false);

        config = new Config(getContext());
        BASE_URL = config.getBaseUrl();

        tvDetailNama = view.findViewById(R.id.tvDetailNama);
        tvDetailBahan = view.findViewById(R.id.tvDetailBahan);
        tvDetailPorsi = view.findViewById(R.id.tvDetailPorsi);
        tvDetailLangkah = view.findViewById(R.id.tvDetailLangkah);
        tvDetailWaktu = view.findViewById(R.id.tvDetailWaktu);

        // Mendapatkan ID resep dari arguments
        if (getArguments() != null) {
            idResep = getArguments().getInt("RESEP_ID");
        }

        fetchDetailResep();

        return view;
    }

    public void fetchDetailResep() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", null);

        if (token == null) {
            // Token tidak ditemukan, tampilkan pesan atau lakukan tindakan yang sesuai
            return;
        }

        String URL = BASE_URL + "/resep/get/" + idResep;
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
                    Log.d("TAG", "onResponse: "+namaResep);

                    tvDetailNama.setText(namaResep);
                    tvDetailBahan.setText(bahan);
                    tvDetailPorsi.setText(String.valueOf(porsi));
                    tvDetailLangkah.setText(langkah);
                    tvDetailWaktu.setText(waktuMasak);
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
}
