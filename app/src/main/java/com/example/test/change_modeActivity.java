package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class change_modeActivity extends AppCompatActivity {

    Button changefile,extendtime,confirm,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mode);

        changefile = findViewById(R.id.changefile);
        changefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                intent.setType( "image/*" );
                Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
                startActivityForResult( destIntent, 0);
                }
        });

        extendtime = findViewById(R.id.extendtime);
        extendtime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(change_modeActivity.this, select_timeActivity.class);
                intent.putExtra("board", ",1,");
                startActivityForResult(intent,1);
            }
        });

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(change_modeActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(change_modeActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });

        initViews();
    }

    private void initViews(){
        ListView mList =  findViewById(R.id.listView);
        String[] strs = new String[3];
        strs[0] = "File1";
        strs[1] = "File2";
        strs[2] = "File3";

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
                //textView1.setText(dateTime + " " + checked.substring(0,5));
            }
        });
    }
}
