package com.pab.masakyuapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment {

    private LinearLayout layam, lsapi;
    private ImageView clearSearch;
    private EditText editText;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layam = view.findViewById(R.id.layam);
        lsapi = view.findViewById(R.id.lsapi);
        clearSearch = view.findViewById(R.id.clear_search);

        //auto focus pada editText dan langsung menampilkan keyboard
        editText = view.findViewById(R.id.editTextSearch);
        editText.requestFocus();

        // Untuk menampilkan keyboard secara langsung setelah meminta fokus
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        //tekan clear search
        clearSearch.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               editText.setText("");
                                           }
                                       });
        //tekan layam
        layam.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         editText.setText("ayam");
                                     }
                                     });
        //tekan lsapi
        lsapi.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         editText.setText("sapi");
                                     }
                                     });

        // Set listener untuk menangani tombol "Enter"
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        navigateToBeranda(editText.getText().toString());

                        return true;
                    }
                }
                return false;
            }
        });
    }
    private void navigateToBeranda(String cari) {
        clearBackStack();
        BerandaFragment berandaFragment = new BerandaFragment();

        Bundle bundle = new Bundle();
        bundle.putString("CARI", cari);
        berandaFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.framelayout_beranda, berandaFragment)
                .addToBackStack(null)
                .commit();
    }
    private void clearBackStack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}
