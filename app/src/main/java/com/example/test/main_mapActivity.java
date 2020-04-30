package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class main_mapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button upload,login;
    String account_str,password_str;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        sharedPreferences = getApplication().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferences.getBoolean("login_state",false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        upload = findViewById(R.id.upload);
        login = findViewById(R.id.login);

        if(sharedPreferences.getBoolean("login_state",false)) {
            login.setText("帳號資訊");
            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(main_mapActivity.this, userinformationActivity.class);
                    startActivityForResult(intent,2);
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //textView.setText(dateTime);
        if(sharedPreferences.getBoolean("login_state",false)) {
            login.setText("帳號資訊");
            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(main_mapActivity.this, userinformationActivity.class);
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
                //account_str = data.getStringExtra("login_account");
                //password_str = data.getStringExtra("login_password");

            }
        }
    }

    //給layout裡的上傳按鈕使用
    public void upload_page(View view) {
        if(sharedPreferences.getBoolean("login_state",false)) {
            Intent intent = new Intent(this, select_signagetypeActivity.class);
            intent.putExtra("account", account_str);
            startActivityForResult(intent, 0);
        }
        else
            Toast.makeText(this, "請先登入帳號", Toast.LENGTH_LONG).show();
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
