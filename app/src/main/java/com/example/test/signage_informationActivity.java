package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class signage_informationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button manage,home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signage_information);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manage = findViewById(R.id.manage);
        manage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(signage_informationActivity.this, change_modeActivity.class);
                startActivityForResult(intent,0);
            }
        });

        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(signage_informationActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });

        //initViews();
    }

    private void initViews(){
        ListView mList =  findViewById(R.id.listView);
        String[] strs = new String[3];
        strs[0] = "Signage1";
        strs[1] = "Signage2";
        strs[2] = "Signage3";

        ListAdapter mAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_single_choice,
                        android.R.id.text1,
                        strs);
            mList.setAdapter(mAdapter);

            mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                AbsListView list = (AbsListView)adapterView;
                int idx = list.getCheckedItemPosition();
                String checked = (String)adapterView.getAdapter().getItem(idx);
                //textView1.setText(dateTime + " " + checked.substring(0,5));
            }
        });
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
