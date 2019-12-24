package com.example.test;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class select_timeActivity extends AppCompatActivity {
    TextView textView1;
    String dateTime,board,occupied_slot,upload_time;
    Date date;
    Button confirm;
    final int slot_length = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        textView1 = findViewById(R.id.test);
        confirm = findViewById(R.id.confirm);
        Intent intent = getIntent();
        board = intent.getStringExtra("board");

        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(upload_time.length() > 1) {
                    getIntent().putExtra("upload_time",upload_time);
                    setResult(RESULT_OK,getIntent());
                    finish();
                }
                else{
                    textView1.setText("請先選擇時間");
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this , R.style.MyDialogTheme,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                dateTime = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day);
                textView1.setText(dateTime);
                try {
                    Thread thread = new Thread(CheckSchedule);
                    thread.start();
                    thread.join();
                    initViews();
                    //textView1.setText(occupied_slot);
                    board = ",";
                    occupied_slot = "";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day).show();
    }

    private String[] buildData(int length) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");

        try {
            date = sdf.parse(dateTime);
        }
        catch (ParseException e) { e.printStackTrace(); }
        Calendar calendar = Calendar.getInstance();

        occupied_slot = occupied_slot.substring(1,occupied_slot.length()-1); //去掉 "[" "]"
        String[] occupied_strs = occupied_slot.split(",");
        Set<String> words = new HashSet<>();            //=======================
        for(String occupied_str : occupied_strs) {      //使用Set刪掉重複項目
            words.add(occupied_str);                    //=======================
        }

        int[] temp = new int[words.size()];
        Object[] tempArray = words.toArray();                   //=================
        for (int i = 0; i < tempArray.length; i++) {            //轉回int array
            temp[i] = Integer.parseInt(tempArray[i].toString());  //=================
        }
        Arrays.sort(temp);
        String[] array = new String[length-tempArray.length];
        /*Log.i("array_length",Integer.toString(length-tempArray.length));
        for (int i = 0; i < temp.length;i++)
            Log.i("temp[" + i + "]",Integer.toString(temp[i]));*/ //Log.i用
        int j = 0,k = 0;                                    //=======================================
        for (int i = 0; i < length; i++) {                  //留下0~length沒被occupied的slot number，
            if (j < temp.length && temp[j]==i){             //i代表slot number，temp紀錄被占用的slot
                j = j + 1;                                  //且已經sort，所以可以用index j從0逐步+1
            }                                               //比對是否temp[j]等於i，成立的話那個i就不
            else {                                          //紀錄進array[k]。(i會從nmu轉成時間格式)
                calendar.setTime(date);                     //=======================================
                calendar.add(Calendar.MINUTE,i * slot_length);
                String reStr = sdf1.format(calendar.getTime());
                calendar.add(Calendar.MINUTE,slot_length);
                String reStr1 = sdf1.format(calendar.getTime());
                array[k] = reStr + "-" + reStr1;
                k = k + 1;
            }
        }
        return array;
    }

    private String[] buildEZData(int length){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        String[] array = new String[length];
        try {
            date = sdf.parse(dateTime);
        }
        catch (ParseException e) { e.printStackTrace(); }
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < length; i++){
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE,i * slot_length);
            String reStr = sdf1.format(calendar.getTime());
            calendar.add(Calendar.MINUTE,slot_length);
            String reStr1 = sdf1.format(calendar.getTime());
            array[i] = reStr + "-" + reStr1;
        }
        return array;
    }

    private void initViews(){
        ListView mList =  findViewById(R.id.listView);
        String[] strs;
        if(occupied_slot.equals("[-1]")) {
            strs = buildEZData(1440 / slot_length);
        }
        else{
            strs = buildData(1440 / slot_length);
        }
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
                textView1.setText(dateTime + " " + checked.substring(0,5));
                upload_time = dateTime + " " + checked.substring(0,5) + ":00";
            }
        });
    }

    private Runnable CheckSchedule = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("datetime",dateTime)
                    .addFormDataPart("board",board)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/CheckSchedule.php")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    occupied_slot = response.body().string();
                    Log.i("Success tag",occupied_slot);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };
}

