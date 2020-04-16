package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class select_signagetypeActivity extends AppCompatActivity {

    Button usually,history,map;
    String account,board,inputdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_signagetype);

        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        Log.i("account",account);

        usually = findViewById(R.id.usually);
        usually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdata = "usually";
                try {
                    Thread thread = new Thread(GetUserBoard);
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(board.equals("0"))
                    Toast.makeText(select_signagetypeActivity.this, "沒有設定常用看板", Toast.LENGTH_LONG).show();
                else {
                    board = board.substring(2,board.length()-2);
                    board = "," + board + ",";
                    System.out.print(board);
                    Intent intent = new Intent(select_signagetypeActivity.this, select_signagesActivity.class);
                    intent.putExtra("board", board);
                    startActivity(intent);
                }
            }
        });

        history = findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdata = "history";
                try {
                    Thread thread = new Thread(GetUserBoard);
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(board.equals("0"))
                    Toast.makeText(select_signagetypeActivity.this, "沒有刊登紀錄", Toast.LENGTH_LONG).show();
                else {
                    boolean repeat = false;
                    board = board.substring(1,board.length()-1);
                    String[] boardarr = board.split(",");
                    board = ",";
                    for(int i = 0; i < boardarr.length; i++) {
                        boardarr[i] = boardarr[i].substring(1, boardarr[i].length() - 1);
                        for(int j = 0; j < i; j++)
                            if(boardarr[i].equals(boardarr[j]))
                                repeat = true;
                        if(!repeat)
                            board = board + boardarr[i] + ",";
                    }
                    System.out.print(board);
                    Intent intent = new Intent(select_signagetypeActivity.this, select_signagesActivity.class);
                    intent.putExtra("board", board);
                    startActivity(intent);
                }
            }
        });

        map = findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(select_signagetypeActivity.this, signage_mapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private Runnable GetUserBoard = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("account",account)
                    .addFormDataPart("inputdata",inputdata)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetUserBoard.php")
                    .post(requestBody)
                    .build();
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
    };
}
