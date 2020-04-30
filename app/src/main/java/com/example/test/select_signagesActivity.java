package com.example.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class select_signagesActivity extends AppCompatActivity {
    int Shared;
    Button confirm,cancel;
    String board,other,chosen_board;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_signages);

        Intent intent = getIntent();
        board = intent.getStringExtra("board");
        Log.i("intent board",board);

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chosen_board.equals(","))
                    Toast.makeText(select_signagesActivity.this, "尚未選擇看板", Toast.LENGTH_LONG).show();
                else {
                    String[] temp = {"No","Yes"};
                    new AlertDialog.Builder(select_signagesActivity.this)
                            .setTitle("是否要與其他人共同使用廣告看板?")
                            .setSingleChoiceItems(temp, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Shared = which;
                                    Log.i("是否分享:",String.valueOf(Shared));
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(select_signagesActivity.this, select_timeActivity.class);
                                    intent.putExtra("chosen_board", chosen_board);
                                    intent.putExtra("Shared", String.valueOf(Shared));
                                    intent.putExtra("Source", "Publish");
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .create()
                            .show();
                }
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(select_signagesActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });

        GetBoardDes getBoardDes = new GetBoardDes(board);
        try{
            Thread thread = new Thread(getBoardDes);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initViews();
    }

    private void initViews(){
        ListView mList =  findViewById(R.id.listView);
        other = other.substring(1,other.length()-1);
        chosen_board = board.substring(1,board.length()-1);
        String[] strs = other.split(",");
        final String[] cboard_arr = chosen_board.split(",");
        /*String[] strs = new String[3];
        strs[0] = "Signage1";
        strs[1] = "Signage2";
        strs[2] = "Signage3";*/

        ListAdapter mAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_multiple_choice,
                        android.R.id.text1,
                        strs);
        mList.setAdapter(mAdapter);

        mList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                chosen_board = ",";
                AbsListView list = (AbsListView)adapterView;
                //Adapter adapter = list.getAdapter();
                SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
                //List checked = new ArrayList<>(list.getCheckedItemCount());
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    int key = checkedItemPositions.keyAt(i);
                    if (checkedItemPositions.get(key)) {
                        chosen_board = chosen_board + cboard_arr[key] + ",";
                        //checked.add(adapter.getItem(key));
                        //Log.i("選擇的項目",(String)adapter.getItem(key));
                    }
                }
                Log.i("選擇的看板",chosen_board);
            }
        });
    }

    public class GetBoardDes implements Runnable{

        private String board;

        public GetBoardDes(String board) {
            this.board = board;
        }
        public void run() {
            /*System.out.println(location);
            location = location.substring(10,location.length()-1);
            String[] temp = location.split(",");
            String lat = temp[0],lng = temp[1];
            System.out.println(temp[0]);
            System.out.println(temp[1]);*/

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("board",board)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetBoardDescription.php")
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
                    other = response.body().string();
                    Log.i("board",other);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }
}
