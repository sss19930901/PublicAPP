package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class userinformationActivity extends AppCompatActivity {

    Button file,signage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinformation);
        file = findViewById(R.id.file);
        signage = findViewById(R.id.signage);

        file.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(userinformationActivity.this, file_informationActivity.class);
                startActivityForResult(intent,0);
            }
        });

        signage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(userinformationActivity.this, signage_informationActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }
}
