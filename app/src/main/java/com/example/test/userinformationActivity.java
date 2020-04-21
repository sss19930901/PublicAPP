package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class userinformationActivity extends AppCompatActivity {

    Button file,signage;
    TextView userinformation;
    String account,email,sex,phonenumber,registerday,result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinformation);

        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        if(account != null)
            Log.i("intent account", account);

        try{
            Thread thread = new Thread(GetUserInformation);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = result.substring(1,result.length()-1);
        String[] resultarr = result.split(",");
        email = resultarr[0].substring(1,resultarr[0].length()-1);
        if(Integer.valueOf(resultarr[1].substring(1,resultarr[1].length()-1)) == 0)
            sex = "女";
        else if(Integer.valueOf(resultarr[1].substring(1,resultarr[1].length()-1)) == 1)
            sex = "男";
        else
            sex = "不明";
        phonenumber = resultarr[2].substring(1,resultarr[2].length()-1);
        registerday = resultarr[3].substring(1,resultarr[3].length()-1);

        userinformation = findViewById(R.id.userinformation);
        file = findViewById(R.id.file);
        signage = findViewById(R.id.signage);

        userinformation.setText(
                "使用者帳號: " + account + "\n\n" +
                "性別: " + sex + "\n\n" +
                "電子郵件: " + email + "\n\n" +
                "連絡電話: " + phonenumber + "\n\n" +
                "註冊日期: " + registerday
        );

        file.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(userinformationActivity.this, file_informationActivity.class);
                intent.putExtra("account", account);
                startActivityForResult(intent,0);
            }
        });

        signage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(userinformationActivity.this, select_signageinformation.class);
                startActivityForResult(intent,1);
            }
        });
    }

    private Runnable GetUserInformation = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("account",account)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetUserInformation.php")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    result = response.body().string();
                    Log.i("Success tag",result);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };
}
