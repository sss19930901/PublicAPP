package com.example.test;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class select_timeActivity extends AppCompatActivity {
    String dateTime,board,occupied_slot,chosen_slot,Shared;
    Date date;
    Button confirm,cancel,smaller,bigger;
    int picknum = 15;
    final int slot_length = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        //textView1 = findViewById(R.id.test);
        Intent intent = getIntent();
        board = intent.getStringExtra("chosen_board");
        Shared = intent.getStringExtra("Shared");
        Log.i("chosen_board",board);
        chosen_slot =",";
        smaller = findViewById(R.id.smaller);
        smaller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(picknum == 1)
                    Toast.makeText(select_timeActivity.this, "不能再小了R", Toast.LENGTH_LONG).show();
                else {
                    if (picknum == 15)
                        picknum = 1;
                    else if (picknum == 30)
                        picknum = 15;
                    try {
                        Thread thread = new Thread(CheckSchedule);
                        thread.start();
                        thread.join();
                        initViews();
                        //board = ",";
                        occupied_slot = "";
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
           }
        });

        bigger = findViewById(R.id.bigger);
        bigger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(picknum == 30)
                    Toast.makeText(select_timeActivity.this, "不能再大了R", Toast.LENGTH_LONG).show();
                else {
                    if (picknum == 15)
                        picknum = 30;
                    else if (picknum == 1)
                        picknum = 15;
                    try {
                        Thread thread = new Thread(CheckSchedule);
                        thread.start();
                        thread.join();
                        initViews();
                        //board = ",";
                        occupied_slot = "";
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
           }
        });

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(chosen_slot.length() > 1) {
                    Intent intent = new Intent(select_timeActivity.this, selectfileActivity.class);
                    intent.putExtra("chosen_slot", chosen_slot);
                    intent.putExtra("chosen_board", board);
                    intent.putExtra("Shared", Shared);
                    startActivity(intent);
                    /*getIntent().putExtra("upload_time",upload_time);
                    setResult(RESULT_OK,getIntent());
                    finish();*/
                }
                else{
                    Toast.makeText(select_timeActivity.this, "請先選擇時間", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(select_timeActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this , R.style.MyDialogTheme,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String monthstr = String.format("%02d",month+1);
                String daystr = String.format("%02d",day);
                dateTime = String.valueOf(year)+"-"+monthstr+"-"+daystr;
                Log.i("dateTime tag",dateTime);
                //textView1.setText(dateTime);
                try {
                    Thread thread = new Thread(CheckSchedule);
                    thread.start();
                    thread.join();
                    initViews();
                    //board = ",";
                    occupied_slot = "";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day).show();
    }

    private List<String[]> buildData(int length) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");

        try {
            date = sdf.parse(dateTime);
        }
        catch (ParseException e) { e.printStackTrace(); }
        Calendar calendar = Calendar.getInstance();

        occupied_slot = occupied_slot.substring(1,occupied_slot.length()-1); //去掉 "[" "]"
        String[] occupied_strs = occupied_slot.split(",");
        //occupied_slot = "";
        Set<String> words = new HashSet<>();            //=======================
        for(String occupied_str : occupied_strs) {      //使用Set刪掉重複項目
            words.add(occupied_str);                    //=======================
        }

        Integer[] temp = new Integer[words.size()];
        Object[] tempArray = words.toArray();                   //=================
        for (int i = 0; i < tempArray.length; i++) {            //轉回int array
            temp[i] = Integer.parseInt(tempArray[i].toString());  //=================
        }
        Arrays.sort(temp);
         /*List<Integer> templist = Arrays.asList(temp);
        for (int i = 0; i < templist.size(); i++)
            if (templist.get(i) % picknum != 0)
                templist.remove(i);
        temp = templist.toArray(new Integer[templist.size()]);*/
        ArrayList<String> freeslot =  new ArrayList<>();
        for (int i = 0; i < length; i++)
            freeslot.add(String.valueOf(i));

        for (int i = 0; i < temp.length; i++) {
            //Log.i("occupied_slot[" + i + "]",String.valueOf(temp[i]));
            freeslot.remove(temp[i]-i);
        }
        Log.i("delete ocupied",String.valueOf(freeslot));
        for (int i = 0; i < freeslot.size(); i++) {
            //Log.i("freeslot[" + i + "]",freeslot.get(i));
            //Log.i("freeslot[" + i + "] % picknum = ",String.valueOf(Integer.valueOf(freeslot.get(i)) % picknum));
            if ((Integer.valueOf(freeslot.get(i)) % picknum != 0)) {
                freeslot.remove(i);
                i--;
            }
            else if(i + (picknum - 1) < freeslot.size()){
                if(Integer.valueOf(freeslot.get(i + picknum - 1)) - Integer.valueOf(freeslot.get(i)) != picknum - 1) {
                    freeslot.remove(i);
                    i--;
                }
            }
            else if(i + (picknum - 1) >= freeslot.size()){
                freeslot.remove(i);
                i--;
            }
        }
        Log.i("delete picknum",String.valueOf(freeslot));
        String[] timestr = new String[freeslot.size()];
        for (int i = 0; i < freeslot.size(); i++){
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, Integer.valueOf(freeslot.get(i)) * slot_length);
            String reStr = sdf1.format(calendar.getTime());
            calendar.add(Calendar.MINUTE, slot_length * picknum);
            String reStr1 = sdf1.format(calendar.getTime());
            timestr[i] = reStr + "-" + reStr1;
            /*Log.i("freeslot[" + i + "]",freeslot.get(i));
            Log.i("timestr[" + i +"]",timestr[i]);*/
        }
        String[] freeslot_arr = new String[freeslot.size()];
        for (int i = 0; i < freeslot.size(); i++)
            freeslot_arr[i] = freeslot.get(i);
        //String[] freeslot = new String[length-tempArray.length];
        /*for (int i = 0; i < temp.length;i++)
            Log.i("temp[" + i + "]",Integer.toString(temp[i])); //Log.i用*/
        /*int j = 0,k = 0;                                    //=======================================
        for (int i = 0; i < length; i++) {                  //留下0~length沒被occupied的slot number，
            if (j < temp.length && temp[j]==i){             //i代表slot number，temp紀錄被占用的slot
                j = j + 1;                                  //且已經sort，所以可以用index j從0逐步+1
            }                                               //比對是否temp[j]等於i，成立的話那個i就不
            else {                                          //紀錄進array[k]。(i會從nmu轉成時間格式)
                if(i % picknum == 0) {                      //=======================================
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE, i * slot_length);
                    String reStr = sdf1.format(calendar.getTime());
                    calendar.add(Calendar.MINUTE, slot_length * picknum);
                    String reStr1 = sdf1.format(calendar.getTime());
                    array[k] = reStr + "-" + reStr1;
                    freeslot[k] = String.valueOf(i);
                    Log.i("free slot[" + k + "]", freeslot[k]);
                    Log.i("array[" + k + "]", array[k]);
                    k = k + 1;
                }
            }
        }*/
        List<String[]> data = new ArrayList<>();
        data.add(timestr);
        data.add(freeslot_arr);

        return data;
    }

    private List<String[]> buildEZData(int length){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        String[] array = new String[length];
        String[] freeslot = new String[length];
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
            freeslot[i] = String.valueOf(i);
        }
        List<String[]> data = new ArrayList<>();
        data.add(array);
        data.add(freeslot);
        return data;
    }

    private void initViews(){
        ListView mList =  findViewById(R.id.listView);
        List<String[]> data = new ArrayList<>();
        if(occupied_slot.equals("[-1]"))
            data = buildEZData(1440 / slot_length);
        else
            data = buildData(1440 / slot_length);

        final String[] strs = data.get(0);
        final String[] free_slot = data.get(1);
        Log.i("strslength:",String.valueOf(strs.length));
        for(int i=0;i<strs.length;i++)
            Log.i("strs["+i+"]",strs[i]);
        //Log.i("strs",strs[0]);
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
                chosen_slot =",";
                AbsListView list = (AbsListView)adapterView;
                Adapter adapter = list.getAdapter();
                int idx = list.getCheckedItemPosition();
                SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
                List checked = new ArrayList<>(list.getCheckedItemCount());
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    int key = checkedItemPositions.keyAt(i);
                    if (checkedItemPositions.get(key)) {
                        for (int j =0; j < picknum; j++)
                            chosen_slot = chosen_slot + String.valueOf(Integer.valueOf(free_slot[key]) + j) + ",";
                        checked.add(adapter.getItem(key));
                        Log.i("選擇的項目",(String)adapter.getItem(key));
                    }
                }
                Log.i("index",chosen_slot);
                //Log.i("str",checked);
                //textView1.setText(dateTime + " " + checked.substring(0,5));
                //upload_time = dateTime + " " + checked.substring(0,5) + ":00";
            }
        });
    }

    private Runnable CheckSchedule = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("datetime",dateTime)
                    .addFormDataPart("board",board)
                    .addFormDataPart("Shared",Shared)
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/CheckToday.php")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    occupied_slot = response.body().string();
                    Log.i("occupied_slot",occupied_slot);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };
}

