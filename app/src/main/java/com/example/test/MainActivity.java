package com.example.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    ImageView testImg;
    Button button,loginbutton,choosebutton,dcl_01_01,dcl_01_02,dcl_02_01,timebutton;
    String result,account_str,password_str,upload_time,board =",";
    int choice_count = 0;
    boolean login_state = false,time_state = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 找到視圖的元件並連接
        //testImg = findViewById(R.id.imageViewObj);
        dcl_01_01 = findViewById(R.id.dcl_01_01);
        dcl_01_02 = findViewById(R.id.dcl_01_02);
        dcl_02_01 = findViewById(R.id.dcl_02_01);
        button = findViewById(R.id.button_id);
        choosebutton = findViewById(R.id.choose_files);
        textView = findViewById(R.id.textView_id);
        loginbutton = findViewById(R.id.button_login);
        testImg = findViewById(R.id.ImgView_id);
        timebutton  = findViewById(R.id.timepicker);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼
                // 宣告執行緒
                Thread thread = new Thread(mutiThread);
                thread.start(); // 開始執行
            }
        });
        choosebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(login_state && time_state && choice_count>0){
                        Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                        intent.setType( "image/*" );
                        Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
                        startActivityForResult( destIntent, 1 );
                    }
                    else if(!login_state)
                        textView.setText("尚未登入");
                    else if(choice_count==0){
                        textView.setText("尚未選擇看板");
                    }
                    else if(!time_state)
                        textView.setText("尚未選擇時間");
                }
        });
        dcl_01_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dcl_01_01.isSelected()) {
                    dcl_01_01.setSelected(true);
                    choice_count++;
                } else {
                    dcl_01_01.setSelected(false);
                    choice_count--;
                }
            }
        });
        dcl_01_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dcl_01_02.isSelected()) {
                    dcl_01_02.setSelected(true);
                    choice_count++;
                } else {
                    dcl_01_02.setSelected(false);
                    choice_count--;
                }
            }
        });
        dcl_02_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dcl_02_01.isSelected()) {
                    dcl_02_01.setSelected(true);
                    choice_count++;
                } else {
                    dcl_02_01.setSelected(false);
                    choice_count--;
                }
            }
        });
        timebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dcl_01_01.isSelected())
                {
                    board = board.concat("1,");
                }
                if (dcl_01_02.isSelected())
                {
                    board = board.concat("2,");
                }
                if (dcl_02_01.isSelected())
                {
                    board = board.concat("3,");
                }
                if(board.length() > 1) {
                    Intent intent = new Intent(MainActivity.this, select_timeActivity.class);
                    intent.putExtra("board", board);
                    startActivityForResult(intent, 2);
                }
                else{
                    textView.setText("請先選擇看板");
                }
            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        //textView.setText(dateTime);
        if(login_state) {
            loginbutton.setText("帳號資訊");
            loginbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Thread thread = new Thread(getInformation);
                    thread.start();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (resultCode == RESULT_OK) {
                account_str = data.getStringExtra("login_account");
                password_str = data.getStringExtra("login_password");
                login_state = true;
            }
        }
        else if(requestCode == 1){
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
        else if(requestCode == 2){
            if ( resultCode == RESULT_OK ){
                upload_time = data.getStringExtra("upload_time");
                time_state = true;
                textView.setText(upload_time);
            }
        }
    }

    public void login_page(View view) {
        Intent intent = new Intent(this, loginActivity.class);
        startActivityForResult(intent,0);
    }



    private Runnable getInformation = new Runnable(){
        public void run()
        {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost method = new HttpPost("http://140.116.72.152/GetInformation.php");
                //傳值給PHP
                List<NameValuePair> vars = new ArrayList<NameValuePair>();
                vars.add(new BasicNameValuePair("account", account_str));
                method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));
                vars.add(new BasicNameValuePair("password", password_str));
                method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));
                //接收PHP回傳的資料
                HttpResponse response = httpclient.execute(method);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    result = EntityUtils.toString(entity);
                } else {
                    textView.setText("error");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    textView.setText(result);                }
            });
        }
    };
    // Android 有規定，連線網際網路的動作都不能再主線程做執行
    // 畢竟如果使用者連上網路結果等太久整個系統流程就卡死了
    private Runnable mutiThread = new Runnable(){
        public void run()
        {
            try {
                URL url = new URL("http://140.116.72.152/GetBoard.php");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.connect(); // 開始連線

                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件
                result = Integer.toString(responseCode);
                if(responseCode ==
                        HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream =
                            connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        box += line + "\n";

                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    result = box; // 把存放用字串放到全域變數

                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }
             // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    textView.setText(result); // 更改顯示文字
                    dcl_01_01.setVisibility(View.VISIBLE);
                    dcl_01_02.setVisibility(View.VISIBLE);
                    dcl_02_01.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    public void UploadFiles (final Bitmap image) {
        testImg.setImageBitmap(image);
        /*if (dcl_01_01.isSelected())
        {
            board = board.concat("1,");
        }
        if (dcl_01_02.isSelected())
        {
            board = board.concat("2,");
        }
        if (dcl_02_01.isSelected())
        {
            board = board.concat("3,");
        }*/
        new Thread(new Runnable() {

            public void run() {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] byteArray = bos.toByteArray();

                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", "test.png", RequestBody.create(MediaType.parse("image/*"),byteArray))
                        .addFormDataPart("account",account_str)
                        .addFormDataPart("board",board)
                        .addFormDataPart("upload_time",upload_time)
                        .build();

                Request request = new Request.Builder()
                        .url("http://140.116.72.152/upload.php")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        Log.i("Success tag",response.body().string());
                        board = "上傳成功" + board;
                        //textView.setText(board);
                        board = ",";
                    }
                }
                catch (IOException e) { e.printStackTrace(); }
            }

        }).start();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}