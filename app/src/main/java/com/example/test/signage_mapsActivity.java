package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class signage_mapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //Double lat = 22.996845,lng = 120.222487;
    ArrayList<String> lat = new ArrayList<String>();
    ArrayList<String> lng = new ArrayList<String>();
    ArrayList<String> locD = new ArrayList<String>();
    String result,board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signage_maps);
        try {
            Thread thread = new Thread(GetMapMarker);
            thread.start();
            thread.join();
            buildMarkerData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng locate;
        // Add a marker in Tainan and move the camera
        for (int i = 0;i < lat.size();i++) {
            locate = new LatLng(Double.valueOf(lat.get(i)), Double.valueOf(lng.get(i)));
            mMap.addMarker(new MarkerOptions().
                    position(locate).
                    title(locD.get(i)).
                    snippet("(點擊文字前往選擇看板)"));
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                GetBoardID getBoardID = new GetBoardID(String.valueOf(marker.getPosition()));
                try {
                    Thread thread = new Thread(getBoardID);
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(signage_mapsActivity.this, select_signagesActivity.class);
                intent.putExtra("board", board);
                startActivity(intent);
            }
        });
        locate = new LatLng(Double.valueOf(lat.get(0)), Double.valueOf(lng.get(0)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate, 17));
        //System.out.println(lat);
        lat.clear();
        lng.clear();
        locD.clear();
        //System.out.println(lat);
    }

    private Runnable GetMapMarker = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetMapMarker.php")
                    .get()
                    .build();
            /*RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetMapMarker.php")
                    .post(requestBody)
                    .build();*/
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    result = response.body().string();
                    Log.i("result",result);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };

    private class GetBoardID implements Runnable{

        private String location;

        public GetBoardID(String location) {
            this.location = location;
        }
        public void run() {
            //System.out.println(location);
            location = location.substring(10,location.length()-1);
            String[] temp = location.split(",");
            String lat = temp[0],lng = temp[1];
            System.out.println(temp[0]);
            System.out.println(temp[1]);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("lat",lat)
                    .addFormDataPart("lng",lng)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetMapBoard.php")
                    .post(requestBody)
                    .build();
            /*RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetMapMarker.php")
                    .post(requestBody)
                    .build();*/
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    board = response.body().string();
                    Log.i("board",board);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void buildMarkerData(){
        int i = 0;
        String[] tokens = result.split("]");
        for (String token:tokens) {
            token = token.substring(1);
            String[] subtokens = token.split(",");
            for (String subtoken:subtokens) {
                subtoken = subtoken.substring(1, subtoken.length() - 1);
                if (i==0)
                    lat.add(subtoken);
                else if(i==1)
                    lng.add(subtoken);
                else if(i==2)
                    locD.add(subtoken);
                //System.out.println(subtoken);
            }
            i++;
        }
    }


}
