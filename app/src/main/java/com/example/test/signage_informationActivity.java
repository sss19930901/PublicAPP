package com.example.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URL;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class signage_informationActivity extends FragmentActivity implements OnMapReadyCallback {

    private SharedPreferences sharedPreferences;
    private GoogleMap mMap;
    Button changefile,extendtime;
    String RequestID,result,start,end,alone,slot_number,duration,temp,temp1,chosen_slot,board,Shared,account,input;
    ImageView BigImage;
    String[] resultarr,lat,lng,boardDescription,fileID,board_ID;
    ImageView[] imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signage_information);

        sharedPreferences = getApplication().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        account = sharedPreferences.getString("account",null);

        Intent intent = getIntent();
        RequestID = intent.getStringExtra("RequestID");
        input = intent.getStringExtra("input");
        Log.d("RequestID",RequestID);
        Log.d("input",input);
        try{
            Thread thread = new Thread(GetRequestInformatiom);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        resultarr = result.split("]");

        resultarr[0] = resultarr[0].substring(1);
        fileID = resultarr[0].split(",");
        temp = "";
        for(int i = 0; i < fileID.length; i++) {
            if(i > 0)
                temp = temp + ", ";
            temp = temp + String.format("%03d",Integer.valueOf(fileID[i].substring(1,fileID[i].length()-1)));
        }

        resultarr[1] = resultarr[1].substring(2,resultarr[1].length()-8);
        start = resultarr[1];

        resultarr[2] = resultarr[2].substring(2,resultarr[2].length()-8);
        end = resultarr[2];

        resultarr[3] = resultarr[3].substring(2,resultarr[3].length()-1);
        alone = resultarr[3];
        if(alone.equals("1"))
            alone = "否";
        else
            alone = "是";

        resultarr[4] = resultarr[4].substring(1);
        lat = resultarr[4].split(",");

        resultarr[5] = resultarr[5].substring(1);
        lng = resultarr[5].split(",");

        resultarr[6] = resultarr[6].substring(1);
        boardDescription = resultarr[6].split(",");
        temp1 = "";
        for(int i = 0; i < boardDescription.length; i++) {
            if(i > 0)
                temp1 = temp1 + ", ";
            temp1 = temp1 + boardDescription[i].substring(1,boardDescription[i].length()-1);
        }

        resultarr[7] = resultarr[7].substring(2,resultarr[7].length()-1);
        slot_number = resultarr[7];

        resultarr[8] = resultarr[8].substring(2,resultarr[8].length()-1);
        duration = resultarr[8];

        resultarr[9] = resultarr[9].substring(1);
        board_ID = resultarr[9].split(",");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BigImage = findViewById(R.id.imageView);
        LinearLayout imagelayer = findViewById(R.id.imagelayer);
        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT,1);
        LinearLayout.LayoutParams buttonparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0,0);

        textView = findViewById(R.id.requestInformation);
        textView.setText("租用時間: " + start + " - " + end + "\n"
        + "檔案數量: " + fileID.length + "\n"
        + "檔案ID: " + temp + "\n"
        + "是否共用: " + alone + "\n"
        + "看板: " + temp1
        );

        imageView = new ImageView[fileID.length];
        for (int i = 0;i < fileID.length;i++){
            final int temp = i;
            imageView[i] = new ImageView(this);
            imageView[i].setLayoutParams(imageparams);
            imageView[i].setBackgroundResource(R.drawable.button);
            imageView[i].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    if(!imageView[temp].isSelected()){
                        for(int j = 0;j < imageView.length;j++)
                            if(imageView[j].isSelected())
                                imageView[j].setSelected(false);
                        imageView[temp].setSelected(true);
                        BigImage.setImageDrawable(imageView[temp].getDrawable());
                    }
                    else {
                        imageView[temp].setSelected(false);
                    }
                    Log.i("選擇的檔案INDEX",String.valueOf(temp));
                }
            });
            imagelayer.addView(imageView[i]);
        }
        board = ",";
        for(int i = 0; i < board_ID.length; i++)
            board = board + board_ID[i].substring(1,board_ID[i].length()-1) + ",";
        if(alone.equals("否"))
            Shared = "0";
        else
            Shared = "1";
        changefile = findViewById(R.id.changefile);
        extendtime = findViewById(R.id.extendtime);
        if(input.equals("history")) {
            changefile.setLayoutParams(buttonparams);
            extendtime.setLayoutParams(buttonparams);
        }
        else if(input.equals("now")) {
            chosen_slot = ",";
            for (int i = 0; i < Integer.valueOf(duration); i++)
                chosen_slot = chosen_slot + (Integer.valueOf(slot_number) + i) + ",";
            changefile.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(signage_informationActivity.this, selectfileActivity.class);
                    intent.putExtra("chosen_slot", chosen_slot);
                    intent.putExtra("chosen_board", board);
                    intent.putExtra("Shared", Shared);
                    intent.putExtra("Source", "Update");
                    startActivity(intent);
                }
            });
            extendtime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(signage_informationActivity.this, select_timeActivity.class);
                    intent.putExtra("chosen_board", board);
                    intent.putExtra("Shared", Shared);
                    intent.putExtra("intent_slot", chosen_slot);
                    intent.putExtra("Source", "Update");
                    startActivity(intent);
                }
            });
        }

        signage_informationActivity.GetImage getImage = new GetImage();
        getImage.execute(fileID);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng locate;
        // Add a marker in Tainan and move the camera
        for (int i = 0;i < lat.length ;i++) {
            locate = new LatLng(Double.valueOf(lat[i].substring(1,lat[i].length()-1)), Double.valueOf(lng[i].substring(1,lng[i].length()-1)));
            mMap.addMarker(new MarkerOptions().
                    position(locate).
                    title(boardDescription[i]));
        }
        locate = new LatLng(Double.valueOf(lat[0].substring(1,lat[0].length()-1)), Double.valueOf(lng[0].substring(1,lng[0].length()-1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate, 17));
    }

    private Runnable GetRequestInformatiom = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("RequestID",RequestID)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetRequestInformatiom.php")
                    .post(requestBody)
                    .build();
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

    class GetImage extends AsyncTask<String , Integer , Bitmap[]> {

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
        }

        @Override
        protected Bitmap[] doInBackground(String... params) {
            //執行中 在背景做事情
            Bitmap[] userfile = new Bitmap[params.length];
            for (int i = 0;i < params.length;i++) {
                Log.i("params",params[i]);
                String urlStr = "http://140.116.72.152/img/board/"+ String.format("%03d", Integer.valueOf(params[i].substring(1,params[i].length()-1))) + ".png";
                Log.i("urlStr",urlStr);
                try {
                    URL url = new URL(urlStr);
                    userfile[i] =  BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return userfile;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Bitmap[] bitmap) {
            //執行後 完成背景任務
            super.onPostExecute(bitmap);
            for (int i = 0; i < bitmap.length; i++)
               imageView[i].setImageBitmap(bitmap[i]);
        }
    }
}
