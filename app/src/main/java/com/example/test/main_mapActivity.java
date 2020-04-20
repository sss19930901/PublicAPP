package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class main_mapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button upload,login;
    String account_str = "user001",password_str;
    boolean login_state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        upload = findViewById(R.id.upload);
        login = findViewById(R.id.login);

        if(login_state) {
            login.setText("帳號資訊");
            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(main_mapActivity.this, userinformationActivity.class);
                    intent.putExtra("account", account_str);
                    startActivityForResult(intent,2);
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //textView.setText(dateTime);
        if(login_state) {
            login.setText("帳號資訊");
            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(main_mapActivity.this, userinformationActivity.class);
                    intent.putExtra("account", account_str);
                    startActivityForResult(intent,2);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                account_str = data.getStringExtra("login_account");
                password_str = data.getStringExtra("login_password");
                login_state = true;
            }
        }
    }

    //給layout裡的上傳按鈕使用
    public void upload_page(View view) {
        Intent intent = new Intent(this, select_signagetypeActivity.class);
        intent.putExtra("account", account_str);
        startActivityForResult(intent,0);
    }
    //給layout裡的登入按鈕使用
    public void login_page(View view) {
        Intent intent = new Intent(this, loginActivity.class);
        startActivityForResult(intent,1);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Tainan and move the camera
        LatLng tainan = new LatLng(22.996845, 120.222487);
        mMap.addMarker(new MarkerOptions().position(tainan).title("Marker in Tainan"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tainan, 17));
    }
}
