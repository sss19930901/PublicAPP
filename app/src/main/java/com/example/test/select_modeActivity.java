package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class select_modeActivity extends AppCompatActivity {
    Button selectfile,confirm,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        selectfile = findViewById(R.id.selectfile);
        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                intent.setType( "image/*" );
                Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
                startActivityForResult( destIntent, 0);
            }
        });

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(select_modeActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(select_modeActivity.this, main_mapActivity.class);
                startActivity(intent);
            }
        });
    }
}
