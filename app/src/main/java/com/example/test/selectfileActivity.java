package com.example.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class selectfileActivity extends AppCompatActivity {
    String chosen_board,chosen_slot,chosen_ID = ",",file,account = "user001",playtime = ",",Shared;
    Button confirm,cancel;
    String[] IDandState,file_ID,State;
    ImageView[] imageView;
    Boolean[] chosen_file;
    int count = 0,filecount = 0,usertime=120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectfile);

        Intent intent = getIntent();
        chosen_board = intent.getStringExtra("chosen_board");
        chosen_slot = intent.getStringExtra("chosen_slot");
        Shared = intent.getStringExtra("Shared");
        LinearLayout layer1 = findViewById(R.id.selectfile);

        try{
            Thread thread = new Thread(CheckFile);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IDandState = file.split("]");

        IDandState[0] = IDandState[0].substring(1);
        file_ID = IDandState[0].split(",");

        IDandState[1] = IDandState[1].substring(1);
        State = IDandState[1].split(",");

        imageView = new ImageView[file_ID.length];
        chosen_file = new Boolean[file_ID.length];
        for(int i = 0; i < file_ID.length; i++)
            chosen_file[i] = false;

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(selectfileActivity.this)
                        .setTitle("確認送出?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i = 0; i < chosen_file.length; i++)
                                    if(chosen_file[i]) {
                                        chosen_ID = chosen_ID + file_ID[i].substring(1, file_ID[i].length() - 1) + ",";
                                        filecount++;
                                    }
                                if(Shared.equals("1"))
                                    usertime = 40;
                                for(int i = 0; i < filecount; i++)
                                    playtime = playtime + String.valueOf(usertime / filecount) + ",";
                                if (chosen_ID.equals(","))
                                    Toast.makeText(selectfileActivity.this, "尚未選擇檔案", Toast.LENGTH_LONG).show();
                                else {
                                    try{
                                        Thread thread = new Thread(Publish);
                                        thread.start();
                                        thread.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Intent intent = new Intent(selectfileActivity.this, main_mapActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create()
                        .show();
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(selectfileActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout.LayoutParams layer2params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0,2.5f);
        LinearLayout.LayoutParams layerparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0,0.2f);
        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams(
                0,ViewGroup.LayoutParams.MATCH_PARENT,4.25f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,ViewGroup.LayoutParams.MATCH_PARENT,0.5f);

        for (int i = 0;i < file_ID.length;i++)
        {
            //每兩張圖加入一個新的Linearlayout
            if (i % 2 == 0) {
                final int temp = i;
                ImageView spaceView0 = new ImageView(this);
                spaceView0.setLayoutParams(params);
                ImageView spaceView1 = new ImageView(this);
                spaceView1.setLayoutParams(params);
                ImageView spaceView2 = new ImageView(this);
                spaceView2.setLayoutParams(params);
                ImageView spaceView3 = new ImageView(this);
                spaceView3.setLayoutParams(layerparams);

                LinearLayout layer2 = new LinearLayout(this);
                layer2.addView(spaceView0);
                layer2.setOrientation(LinearLayout.HORIZONTAL);
                //layer2.setGravity(0);
                layer2.setLayoutParams(layer2params);

                imageView[i] = new ImageView(this);
                imageView[i].setLayoutParams(imageparams);
                imageView[i].setBackgroundResource(R.drawable.button);
                imageView[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!imageView[temp].isSelected()){
                            if(count == 2)
                                Toast.makeText(selectfileActivity.this, "最多選擇兩個檔案", Toast.LENGTH_LONG).show();
                            else {
                                imageView[temp].setSelected(true);
                                chosen_file[temp] = true;
                                count++;
                            }
                        }
                        else {
                            imageView[temp].setSelected(false);
                            chosen_file[temp] = false;
                            count--;
                        }
                        Log.i("選擇的檔案數目",String.valueOf(count));
                        Log.i("選擇的檔案INDEX",String.valueOf(temp));
                    }
                });

                layer2.addView(imageView[i]);
                layer2.addView(spaceView1);

                //加入完第一張，如果還有下一張 把下一張圖也加入進來
                if(i < file_ID.length-1) {
                    imageView[i+1] = new ImageView(this);
                    imageView[i+1].setLayoutParams(imageparams);
                    imageView[i+1].setBackgroundResource(R.drawable.button);
                    imageView[i+1].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!imageView[temp+1].isSelected()){
                                if(count == 2)
                                    Toast.makeText(selectfileActivity.this, "最多選擇兩個檔案", Toast.LENGTH_LONG).show();
                                else {
                                    imageView[temp+1].setSelected(true);
                                    chosen_file[temp+1] = true;
                                    count++;
                                }
                            }
                            else {
                                imageView[temp+1].setSelected(false);
                                chosen_file[temp+1] = false;
                                count--;
                            }
                            Log.i("選擇的檔案數目",String.valueOf(count));
                            Log.i("選擇的檔案INDEX",String.valueOf(temp+1));
                        }
                    });

                    layer2.addView(imageView[i+1]);
                }
                //加入完第一張，如果沒下一張 加入一個空白的view
                else if(i == file_ID.length-1){
                    ImageView tempView = new ImageView(this);
                    tempView.setLayoutParams(imageparams);
                    layer2.addView(tempView);
                }
                layer2.addView(spaceView2);
                layer1.addView(layer2);
                layer1.addView(spaceView3);
            }
        }
        GetImage getImage = new GetImage();
        getImage.execute(file_ID);
    }

    private Runnable CheckFile = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("account",account)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/CheckFile.php")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                        file = response.body().string();
                    Log.i("Success tag",file);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };

    private Runnable Publish = new Runnable(){
        public void run() {
            Log.i("account",account);
            Log.i("chosen_board",chosen_board);
            Log.i("chosen_slot",chosen_slot);
            Log.i("chosen_ID",chosen_ID);
            Log.i("playtime",playtime);
            Log.i("Shared",Shared);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("account",account)
                    .addFormDataPart("board",chosen_board)
                    .addFormDataPart("publish_slot",chosen_slot)
                    .addFormDataPart("file_id",chosen_ID)
                    .addFormDataPart("playtime",playtime)
                    .addFormDataPart("Shared",Shared)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/publish.php")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    file = response.body().string();
                    Log.i("Success tag",file);
                    chosen_ID = ",";
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };

    private class GetImage extends AsyncTask<String , Integer , Bitmap[]> {

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


