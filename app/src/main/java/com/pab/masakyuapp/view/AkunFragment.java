package com.pab.masakyuapp.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pab.masakyuapp.R;
import com.pab.masakyuapp.model.Config;
import com.pab.masakyuapp.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AkunFragment extends Fragment {
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private List<DataModel> dataList; // List untuk menyimpan data resep
    private Button btnKeluar, btnHapus, btnEdit;
    private ImageView imgClose;
    private LinearLayout layoutEditDelete;
    private TextView tvAkunUsername, tvAkunEmail, tvPopupNamaResep, tvLihat, tvResepKosong;
    private RequestQueue queue;
    private int idResep;
    private SharedPreferences sharedPreferences;
    private String token, username, email;
    private int uidInt;
    private String BASE_URL;
    private Config config;

    public AkunFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        config = new Config(getContext());
        BASE_URL = config.getBaseUrl();

        // Inisialisasi UI
        btnKeluar = view.findViewById(R.id.btnKeluar);
        tvAkunUsername = view.findViewById(R.id.tvAkunUsername);
        tvAkunEmail = view.findViewById(R.id.tvAkunEmail);
        recyclerView = view.findViewById(R.id.rvAkun);
        tvResepKosong = view.findViewById(R.id.tvResepkuKosong);

        // Set listener untuk button
        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                requireActivity().finish();

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        dataAdapter = new DataAdapter(dataList, getContext(), new DataAdapter.OnItemClickCallback() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onItemClicked(DataModel data) {
                // Handle item click here
//                Toast.makeText(getContext(), "Item clicked: " + data.getNamaResep(), Toast.LENGTH_SHORT).show();
                layoutEditDelete = view.findViewById(R.id.layoutEditDelete);
                layoutEditDelete.setVisibility(View.VISIBLE);

                if(layoutEditDelete.getVisibility() == View.VISIBLE){
                    tvPopupNamaResep = view.findViewById(R.id.tvPopupNamaResep);
                    btnHapus = view.findViewById(R.id.btnHapus);
                    btnEdit = view.findViewById(R.id.btnEdit);
                    tvPopupNamaResep.setText(data.getNamaResep());
                    imgClose = view.findViewById(R.id.imgClose);
                    tvLihat = view.findViewById(R.id.tvLihat);

                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutEditDelete.setVisibility(View.GONE);
                        }
                    });

                    btnHapus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteResep(data);
                        }
                    });

                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navigateToEditResep(data);
                        }
                    });

                    tvLihat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navigateToDetailResep(data);
                        }
                    });
                }
            }
        });

        recyclerView.setAdapter(dataAdapter);

        // Fetch data resep
        fetchMyResep();

        return view;
    }

    public void fetchMyResep() {
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", null);
        uidInt = sharedPreferences.getInt("UID", -1); // Ambil UID sebagai int
        username = sharedPreferences.getString("USERNAME", null);
        email = sharedPreferences.getString("EMAIL", null);

        tvAkunUsername.setText("Hello, " + username);
        tvAkunEmail.setText(email);

        if (token == null || uidInt == -1) {
            // Token atau UID tidak ditemukan, tampilkan pesan atau lakukan tindakan yang sesuai
            return;
        }

        String uid = String.valueOf(uidInt); // Konversi UID ke String
        String url = BASE_URL + "/resep/get/uid/" + uid;

        queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject jsonObject = dataArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String namaResep = jsonObject.getString("nama_resep");
                                int porsi = jsonObject.getInt("porsi");
                                String waktuMasak = jsonObject.getString("waktu_masak");

                                dataList.add(new DataModel(id, namaResep, porsi, waktuMasak, username));
                            }
                            dataAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        tvResepKosong.setVisibility(View.VISIBLE);
//                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Login Error Response", "Network response is null");
                    Toast.makeText(getContext(), "Login gagal, cek koneksi Anda", Toast.LENGTH_SHORT).show();
                }
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

    public void deleteResep(DataModel data) {
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", null);
        uidInt = sharedPreferences.getInt("UID", -1); // Ambil UID sebagai int
        idResep = data.getId();

        if (token == null || uidInt == -1) {
            // Token atau UID tidak ditemukan, tampilkan pesan atau lakukan tindakan yang sesuai
            return;
        }

        String uid = String.valueOf(uidInt); // Konversi UID ke String
        String url = BASE_URL + "/resep/delete/" + idResep + "/" + uid;

        queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            // Refresh data setelah menghapus resep
                            refreshData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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

    private void navigateToEditResep(DataModel data) {
        clearBackStack();
        EditResepFragment editResepFragment = new EditResepFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("RESEP_ID", data.getId());
        editResepFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.framelayout_beranda, editResepFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToDetailResep(DataModel data) {
        clearBackStack();
        DetailResepFragment detailResepFragment = new DetailResepFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("RESEP_ID", data.getId());
        detailResepFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.framelayout_beranda, detailResepFragment)
                .addToBackStack(null)
                .commit();
    }

    private void refreshData() {
        // Bersihkan dataList
        layoutEditDelete.setVisibility(View.GONE);
        dataList.clear();

        // Panggil kembali method fetchMyResep untuk memuat ulang data
        fetchMyResep();

        // Beri tahu adapter bahwa dataset telah berubah
        dataAdapter.notifyDataSetChanged();
    }

    private void clearBackStack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}
