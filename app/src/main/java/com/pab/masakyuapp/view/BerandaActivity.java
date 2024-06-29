package com.pab.masakyuapp.view;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.pab.masakyuapp.R;

public class BerandaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        //ubah warna status bar dan iconnya
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));


        // Inisialisasi tombol-tombol dan set onClickListener
        ImageButton btnBeranda = findViewById(R.id.btnBeranda);
        ImageButton btnBuatResep = findViewById(R.id.btnBuatresep);
        ImageButton btnAkun = findViewById(R.id.btnAkun);

        btnBeranda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new BerandaFragment());
            }
        });

        btnBuatResep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menghapus semua fragment dari back stack sebelum mengganti ke BuatResepFragment
                clearBackStack();
                replaceFragment(new BuatResepFragment());
            }
        });

        btnAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menghapus semua fragment dari back stack sebelum mengganti ke AkunFragment
                clearBackStack();
                replaceFragment(new AkunFragment());
            }
        });

        // Tampilkan fragment BerandaFragment saat pertama kali
        if (savedInstanceState == null) {
            replaceFragment(new BerandaFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_beranda, fragment);
        if (!(fragment instanceof BerandaFragment)) {
            fragmentTransaction.addToBackStack(null);  // Hanya tambahkan ke back stack jika bukan BerandaFragment
        }
        fragmentTransaction.commit();
    }

    private void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}
