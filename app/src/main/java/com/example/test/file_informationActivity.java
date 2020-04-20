package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class file_informationActivity extends AppCompatActivity {

    TextView description;
    Button addusual,upload;
    String file,account;
    Boolean addu,chosen = false;
    String[] IDandState,file_ID,State,size,times,upload_date,usually;
    ImageView[] imageView;
    Boolean[] chosen_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_information);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");

        description = findViewById(R.id.description);

        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                intent.setType( "image/*" );
                Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
                startActivityForResult( destIntent, 0 );
            }
        });

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

        IDandState[2] = IDandState[2].substring(1);
        size = IDandState[2].split(",");

        IDandState[3] = IDandState[3].substring(1);
        times = IDandState[3].split(",");

        IDandState[4] = IDandState[4].substring(1);
        upload_date = IDandState[4].split(",");

        IDandState[5] = IDandState[5].substring(1);
        usually = IDandState[5].split(",");

        imageView = new ImageView[file_ID.length];
        chosen_file = new Boolean[file_ID.length];
        for(int i = 0; i < file_ID.length; i++)
            chosen_file[i] = false;

        addusual = findViewById(R.id.addusual);
        addusual.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for(int i = 0; i < chosen_file.length; i++)
                    if(chosen_file[i])
                        chosen = true;
                if(chosen)
                    try{
                        Thread thread = new Thread(AddUsualFile);
                        thread.start();
                        thread.join();
                        if(addu)
                            Toast.makeText(file_informationActivity.this, "修改成功，請重新進入此頁來更新資料", Toast.LENGTH_LONG).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(file_informationActivity.this, "請先選擇檔案", Toast.LENGTH_LONG).show();
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
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        if(!imageView[temp].isSelected()){
                            for(int j = 0;j < chosen_file.length;j++)
                                if(chosen_file[j]){
                                    imageView[j].setSelected(false);
                                    chosen_file[j] = false;
                                }
                            imageView[temp].setSelected(true);
                            chosen_file[temp] = true;
                            String fileid = String.format("%03d", Integer.valueOf(file_ID[temp].substring(1,file_ID[temp].length()-1)));
                            String filesize = size[temp].substring(1,size[temp].length()-1);
                            String fileState = State[temp].substring(1,State[temp].length()-1);
                            String filedate = upload_date[temp].substring(1,upload_date[temp].length()-1);
                            String filetimes = times[temp].substring(1,times[temp].length()-1);
                            String fileusually = usually[temp].substring(1,usually[temp].length()-1);
                            if(fileState.equals("1"))
                                fileState = "已審核";
                            else
                                fileState = "未審核";
                            if(fileusually.equals("1"))
                                fileusually = "是";
                            else
                                fileusually = "否";
                            description.setText("檔案 ID: " + fileid + "\n" +
                                    "檔案大小: " + filesize + "\n" +
                                    "審查狀態: " + fileState + "\n" +
                                    "上傳日期: " + filedate + "\n" +
                                    "刊登次數: " + filetimes + "\n" +
                                    "已加入常用檔案: " + fileusually);
                        }
                        else {
                            imageView[temp].setSelected(false);
                            chosen_file[temp] = false;
                            description.setText("檔案 ID: " + "\n" +
                                    "檔案大小: " + "\n" +
                                    "審查狀態: " +  "\n" +
                                    "上傳日期: " + "\n" +
                                    "刊登次數: " + "\n" +
                                    "已加入常用檔案: ");
                        }
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
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(View v) {
                            if(!imageView[temp+1].isSelected()){
                                for(int j = 0;j < chosen_file.length;j++)
                                    if(chosen_file[j]) {
                                        imageView[j].setSelected(false);
                                        chosen_file[j] = false;
                                    }
                                imageView[temp+1].setSelected(true);
                                chosen_file[temp+1] = true;
                                String fileid = String.format("%03d", Integer.valueOf(file_ID[temp+1].substring(1,file_ID[temp+1].length()-1)));
                                String filesize = size[temp+1].substring(1,size[temp+1].length()-1);
                                String fileState = State[temp+1].substring(1,State[temp+1].length()-1);
                                String filedate = upload_date[temp+1].substring(1,upload_date[temp+1].length()-1);
                                String filetimes = times[temp+1].substring(1,times[temp+1].length()-1);
                                String fileusually = usually[temp+1].substring(1,usually[temp+1].length()-1);
                                if(fileState.equals("1"))
                                    fileState = "已審核";
                                else
                                    fileState = "未審核";
                                if(fileusually.equals("1"))
                                    fileusually = "是";
                                else
                                    fileusually = "否";
                                description.setText("檔案 ID: " + fileid + "\n" +
                                        "檔案大小: " + filesize + "\n" +
                                        "審查狀態: " + fileState + "\n" +
                                        "上傳日期: " + filedate + "\n" +
                                        "刊登次數: " + filetimes + "\n" +
                                        "已加入常用檔案: " + fileusually);
                            }
                            else {
                                imageView[temp+1].setSelected(false);
                                chosen_file[temp+1] = false;
                                description.setText("檔案 ID: " + "\n" +
                                        "檔案大小: " + "\n" +
                                        "審查狀態: " +  "\n" +
                                        "上傳日期: " + "\n" +
                                        "刊登次數: " + "\n" +
                                        "已加入常用檔案: ");
                            }
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
        file_informationActivity.GetImage getImage = new GetImage();
        getImage.execute(file_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if ( resultCode == RESULT_OK ){
                Uri uri = data.getData();
                if( uri != null ){
                    // 利用 Uri 顯示 ImageView 圖片
                    try {
                        Bitmap image = getBitmapFromUri(uri);
                        UploadFiles(image);
                    }
                    catch(Exception e) {
                        // 如果出事，回傳錯誤訊息
                    }
                }
                else{
                    setTitle("無效的檔案路徑 !!");
                }
            }
        }
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

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

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

    public void UploadFiles (final Bitmap image) {
        new Thread(new Runnable() {
            public void run() {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] byteArray = bos.toByteArray();

                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", "test.png", RequestBody.create(MediaType.parse("image/*"),byteArray))
                        .addFormDataPart("account",account)
                        .build();

                Request request = new Request.Builder()
                        .url("http://140.116.72.152/upload.php")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        Log.i("Success tag",response.body().string());
                        Looper.prepare();
                        Toast.makeText(file_informationActivity.this, "上傳成功，請重新進入此頁來更新資料", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }
                catch (IOException e) { e.printStackTrace(); }
            }

        }).start();
    }

    private Runnable AddUsualFile = new Runnable(){
        public void run() {
            int i;
            for(i = 0; i < chosen_file.length;i++)
                if(chosen_file[i])
                    break;
            Log.i("選擇的ID",file_ID[i].substring(1,file_ID[i].length()-1));
            Log.i("選擇的usual",usually[i].substring(1,usually[i].length()-1));
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file_id",file_ID[i].substring(1,file_ID[i].length()-1))
                    .addFormDataPart("usually",usually[i].substring(1,usually[i].length()-1))
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/AddUsualFile.php")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    addu = true;
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };
}
