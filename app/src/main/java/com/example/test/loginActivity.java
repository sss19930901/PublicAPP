package com.example.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class loginActivity extends AppCompatActivity {
    EditText account_input,password_input;
    Button loginbutton;
    public static String result,account_str,password_str;
    TextView textView;
    Boolean login;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getApplication().
                getSharedPreferences("userdata", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        textView = findViewById(R.id.textView10);
        loginbutton = findViewById(R.id.button_login2);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(multiThread);
                thread.start();
            }
        });
    }
    private Runnable multiThread = new Runnable() {
        public void run() {
            account_input = findViewById(R.id.account_input);
            password_input = findViewById(R.id.password_input);
            account_str = account_input.getText().toString();
            password_str = password_input.getText().toString();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost method = new HttpPost("http://140.116.72.152/login.php");
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

            if(result.equals("[\"1\"]")){
                result = "登入成功";
                editor.putBoolean("login_state", true);
                editor.putString("account", account_str);
                editor.putString("password", password_str);
                editor.commit();
                }
            else
                result = "帳號或密碼錯誤";

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    Log.i("TEST",String.valueOf(login));
                    textView.setText(result); // 更改顯示文字
                    if (sharedPreferences.getBoolean("login_state", false)) {
                        setResult(RESULT_OK,getIntent());
                        finish();
                    }
                }
            });
        }
    };
}