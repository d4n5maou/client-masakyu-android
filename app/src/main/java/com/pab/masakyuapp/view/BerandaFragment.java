package com.pab.masakyuapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class BerandaFragment extends Fragment {

    private TextView tvSearch, tvHello, tvPencarian;
    private Config config;
    private String BASE_URL, URL, QUERY_CARI, token, userName;
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private RequestQueue requestQueue;
    private List<DataModel> dataList; // List untuk menyimpan data resep

    public BerandaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        //ambil data di sharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", null);
        userName = sharedPreferences.getString("USERNAME", null);

        // Inisialisasi config
        config = new Config(getContext());
        BASE_URL = config.getBaseUrl();
        URL = BASE_URL + "/resep/get";

        // Inisialisasi TextView Search
        tvSearch = view.findViewById(R.id.textViewSearch);
        tvPencarian = view.findViewById(R.id.tvPencarian);
        tvHello = view.findViewById(R.id.tvHello);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_beranda, searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dataList = new ArrayList<>();
        dataAdapter = new DataAdapter(dataList, getContext(), new DataAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(DataModel data) {
                // Handle item click here
                navigateToDetail(data);
            }
        });
        recyclerView.setAdapter(dataAdapter);
        if(getArguments() != null){
            QUERY_CARI = getArguments().getString("CARI");
            tvSearch.setText(QUERY_CARI);
            performSearch(QUERY_CARI);
        }else {
            fetchData(); // Memuat data resep dari server
        }



        return view;
    }

    private void fetchData() {


        if (token == null) {
            // Token tidak ditemukan, tampilkan pesan atau lakukan tindakan yang sesuai
            return;
        }

        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
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

                                // Ambil username dari data resep
                                JSONObject userObject = jsonObject.getJSONObject("User");
                                String username = userObject.getString("username");

                                tvHello.setText("Hello, " + userName);
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

        requestQueue.add(jsonObjectRequest);
    }
    private void performSearch(String query) {

        // Implementasikan permintaan ke server
        String URL = BASE_URL + "/resep/cari?q="+query;
        requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        tvPencarian.setVisibility(View.VISIBLE);
                        try {
                            //ubah response json menjadi string
                            String message = response.getString("message");
                            JSONArray data = response.getJSONArray("data");
                            if (data.length() == 0) {
                                Toast.makeText(getContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                                tvPencarian.setText("Tidak ada data yang ditemukan");
                            } else {
                                // Handle the data
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject resep = data.getJSONObject(i);
                                    int id = resep.getInt("id");
                                    String namaResep = resep.getString("nama_resep");
                                    int porsi = resep.getInt("porsi");
                                    String waktuMasak = resep.getString("waktu_masak");

                                    // Ambil username dari data resep
                                    JSONObject userObject = resep.getJSONObject("User");
                                    Log.d("User", userObject.toString());
                                    String username = userObject.getString("username");

                                    dataList.add(new DataModel(id, namaResep, porsi, waktuMasak, username));
                                }
                                dataAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
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
        requestQueue.add(jsonObjectRequest);
    }

    private void navigateToDetail(DataModel data) {
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


    private void clearBackStack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
