package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.SupportMapFragment;

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
    Button upload,login;
    String result;
    String[] RequestID,start,end,alone,other_users,resultarr;
    private View[] view;
    private ViewPager viewPager;
    private ViewGroup group;
    private ArrayList<View> pageview;
    private ImageView[] tips = new ImageView[3];
    private ImageView imageView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplication().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferences.getBoolean("login_state",false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        upload = findViewById(R.id.upload);
        login = findViewById(R.id.login);

        if(sharedPreferences.getBoolean("login_state",false)) {
            login.setText("帳號資訊");
            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, userinformationActivity.class);
                    startActivityForResult(intent,2);
                }
            });

            try {
                Thread thread = new Thread(GetUserRequest);
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (result.equals("0")) {
                LinearLayout templayout = findViewById(R.id.layout1);
                templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 0));
                TextView textView = findViewById(R.id.text);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 2.5f));
                textView.setText("您目前沒有租用看板");
            }
            else {
                resultarr = result.split("]");

                resultarr[0] = resultarr[0].substring(1);
                RequestID = resultarr[0].split(",");

                resultarr[1] = resultarr[1].substring(1);
                start = resultarr[1].split(",");

                resultarr[2] = resultarr[2].substring(1);
                end = resultarr[2].split(",");

                resultarr[3] = resultarr[3].substring(1);
                alone = resultarr[3].split(",");

                for(int i = 0; i < RequestID.length; i++) {
                    RequestID[i] = RequestID[i].substring(1, RequestID[i].length() - 1);
                    alone[i] = alone[i].substring(1, alone[i].length() - 1);
                    start[i] = start[i].substring(1,start[i].length()-8);
                    end[i] = end[i].substring(1,end[i].length()-8);
                }

                //resultarr[4] = resultarr[4].substring(1);
                //other_users = resultarr[4].split(",");

                //將view加進pageview中
                viewPager = findViewById(R.id.viewPager);
                viewPager.setClipToPadding(false);
                viewPager.setPadding(40, 0, 40, 0);
                viewPager.setPageMargin(20);
                view = new View[RequestID.length];
                pageview = new ArrayList<View>();
                for (int i = 0; i < RequestID.length; i++) {
                    view[i] = getLayoutInflater().inflate(R.layout.viewpager, null);
                    pageview.add(view[i]);
                }

                //viewPager下面的圓點，ViewGroup
                group = findViewById(R.id.viewGroup);
                tips = new ImageView[pageview.size()];
                for (int i = 0; i < pageview.size(); i++) {
                    imageView = new ImageView(MainActivity.this);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(30, 30));
                    imageView.setPadding(20, 0, 20, 0);
                    tips[i] = imageView;

                    //預設第一張圖顯示為選中狀態
                    if (i == 0) {
                        tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
                    } else {
                        tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                    }
                    group.addView(tips[i]);
                }
                //這裡的mypagerAdapter是第三步定義好的。
                viewPager.setAdapter(new mypagerAdapter(pageview));
                //這裡的GuiPageChangeListener是第四步定義好的。
                viewPager.addOnPageChangeListener(new GuidePageChangeListener());
            }
        }
        else {
            LinearLayout templayout = findViewById(R.id.layout1);
            templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0, 0));
            TextView textView = findViewById(R.id.text);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0, 2.5f));
            textView.setText("請先登入以獲取資訊");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //textView.setText(dateTime);
        /*if(sharedPreferences.getBoolean("login_state",false)) {
            login.setText("帳號資訊");
            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, userinformationActivity.class);
                    startActivityForResult(intent, 2);
                }
            });

            try {
                Thread thread = new Thread(GetUserRequest);
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (result.equals("0")) {
                LinearLayout templayout = findViewById(R.id.layout1);
                templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 0));
                TextView textView = findViewById(R.id.text);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 2.5f));
                textView.setText("您目前沒有租用看板");
            } else {
                LinearLayout templayout = findViewById(R.id.layout1);
                templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 2.5f));
                TextView textView = findViewById(R.id.text);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 0));
                resultarr = result.split("]");

                resultarr[0] = resultarr[0].substring(1);
                RequestID = resultarr[0].split(",");

                resultarr[1] = resultarr[1].substring(1);
                start = resultarr[1].split(",");

                resultarr[2] = resultarr[2].substring(1);
                end = resultarr[2].split(",");

                resultarr[3] = resultarr[3].substring(1);
                alone = resultarr[3].split(",");

                resultarr[4] = resultarr[4].substring(1);
                other_users = resultarr[4].split(",");

                //將view加進pageview中
                viewPager = findViewById(R.id.viewPager);
                viewPager.setClipToPadding(false);
                viewPager.setPadding(40, 0, 40, 0);
                viewPager.setPageMargin(20);
                view = new View[RequestID.length];
                pageview = new ArrayList<View>();
                for (int i = 0; i < RequestID.length; i++) {
                    view[i] = getLayoutInflater().inflate(R.layout.viewpager, null);
                    pageview.add(view[i]);
                }

                //viewPager下面的圓點，ViewGroup
                group = findViewById(R.id.viewGroup);
                tips = new ImageView[pageview.size()];
                for (int i = 0; i < pageview.size(); i++) {
                    imageView = new ImageView(MainActivity.this);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(30, 30));
                    imageView.setPadding(20, 0, 20, 0);
                    tips[i] = imageView;

                    //預設第一張圖顯示為選中狀態
                    if (i == 0) {
                        tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
                    } else {
                        tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                    }
                    group.addView(tips[i]);
                }
                //這裡的mypagerAdapter是第三步定義好的。
                viewPager.setAdapter(new mypagerAdapter(pageview));
                //這裡的GuiPageChangeListener是第四步定義好的。
                viewPager.addOnPageChangeListener(new GuidePageChangeListener());
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if(sharedPreferences.getBoolean("login_state",false)) {
                    group = findViewById(R.id.viewGroup);
                    login.setText("帳號資訊");
                    login.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, userinformationActivity.class);
                            startActivityForResult(intent, 2);
                        }
                    });

                    try {
                        Thread thread = new Thread(GetUserRequest);
                        thread.start();
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    TextView textView = findViewById(R.id.text);
                    if (result.equals("0")) {
                        LinearLayout templayout = findViewById(R.id.layout1);
                        templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                0, 0));

                        group.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                0, 0));
                        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                0, 3));
                        textView.setText("您目前沒有租用看板");
                    }
                    else {
                        LinearLayout templayout = findViewById(R.id.layout1);
                        templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                0, 2));
                        group.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                0, 0.5f));
                        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                0, 0.5f));
                        textView.setText("請點擊任務以獲取詳細訊息");
                        resultarr = result.split("]");

                        resultarr[0] = resultarr[0].substring(1);
                        RequestID = resultarr[0].split(",");

                        resultarr[1] = resultarr[1].substring(1);
                        start = resultarr[1].split(",");

                        resultarr[2] = resultarr[2].substring(1);
                        end = resultarr[2].split(",");

                        resultarr[3] = resultarr[3].substring(1);
                        alone = resultarr[3].split(",");
                        for(int i = 0; i < RequestID.length; i++) {
                            RequestID[i] = RequestID[i].substring(1, RequestID[i].length() - 1);
                            alone[i] = alone[i].substring(1, alone[i].length() - 1);
                            start[i] = start[i].substring(1,start[i].length()-8);
                            end[i] = end[i].substring(1,end[i].length()-8);
                        }
                        //resultarr[4] = resultarr[4].substring(1);
                        //other_users = resultarr[4].split(",");

                        //將view加進pageview中
                        viewPager = findViewById(R.id.viewPager);
                        viewPager.setClipToPadding(false);
                        viewPager.setPadding(40, 0, 40, 0);
                        viewPager.setPageMargin(20);
                        view = new View[RequestID.length];
                        pageview = new ArrayList<View>();
                        for (int i = 0; i < RequestID.length; i++) {
                            view[i] = getLayoutInflater().inflate(R.layout.viewpager, null);
                            pageview.add(view[i]);
                        }

                        //viewPager下面的圓點，ViewGroup
                        tips = new ImageView[pageview.size()];
                        for (int i = 0; i < pageview.size(); i++) {
                            imageView = new ImageView(MainActivity.this);
                            imageView.setLayoutParams(new ViewGroup.LayoutParams(30, 30));
                            imageView.setPadding(20, 0, 20, 0);
                            tips[i] = imageView;

                            //預設第一張圖顯示為選中狀態
                            if (i == 0) {
                                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
                            } else {
                                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                            }
                            group.addView(tips[i]);
                        }
                        //這裡的mypagerAdapter是第三步定義好的。
                        viewPager.setAdapter(new mypagerAdapter(pageview));
                        //這裡的GuiPageChangeListener是第四步定義好的。
                        viewPager.addOnPageChangeListener(new GuidePageChangeListener());
                    }
                }
            }
        }
        else if(requestCode == 2)
            if (resultCode == RESULT_OK)
            {
                if(group.getChildCount() > 0)
                    group.removeAllViews();
                LinearLayout templayout = findViewById(R.id.layout1);
                templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 0));
                group = findViewById(R.id.viewGroup);
                group.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 0));
                TextView textView = findViewById(R.id.text);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 3));
                textView.setText("請先登入以獲取資訊");
                login.setText("登入");
                login.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, loginActivity.class);
                        startActivityForResult(intent,1);
                    }
                });
            }
    }

    //給layout裡的上傳按鈕使用
    public void upload_page(View view) {
        if(sharedPreferences.getBoolean("login_state",false)) {
            Intent intent = new Intent(this, select_signagetypeActivity.class);
            startActivityForResult(intent, 0);
        }
        else
            Toast.makeText(this, "請先登入帳號", Toast.LENGTH_LONG).show();
    }
    //給layout裡的登入按鈕使用
    public void login_page(View view) {
        Intent intent = new Intent(this, loginActivity.class);
        startActivityForResult(intent,1);
    }

    class mypagerAdapter extends PagerAdapter {
        private ArrayList<View> pageview1;
        public mypagerAdapter(ArrayList<View> pageview1){
            this.pageview1 = pageview1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d("MainActivityDestroy",position+"");
            if (pageview1.get(position)!=null) {
                container.removeView(pageview1.get(position));
            }
        }

        @Override
        public Object instantiateItem(View collection, final int pos) { //have to make final so we can see it inside of onClick()
            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View page = inflater.inflate(R.layout.viewpager, null);
            Log.i("pos", String.valueOf(RequestID[pos].length()));

            //other_users[pos] = other_users[pos].substring(1,other_users[pos].length()-1);

            TextView temp = page.findViewById(R.id.RequestID);
            temp.setText("RequestID: " + RequestID[pos]);

            String[] tempstr;

            temp = page.findViewById(R.id.start);
            tempstr = start[pos].split(" ");
            temp.setText(tempstr[0] + "\n" + tempstr[1]);

            temp = page.findViewById(R.id.end);
            tempstr = end[pos].split(" ");
            Log.i("tempstr",String.valueOf(tempstr.length));
            temp.setText(tempstr[0] + "\n" + tempstr[1]);

            if(alone[pos].equals("1"))
                alone[pos] = "否";
            else
                alone[pos] = "是";
            temp = page.findViewById(R.id.alone);
            temp.setText("是否共享: " + alone[pos]);

            //temp = page.findViewById(R.id.other_users);
            //temp.setText("共用人數: " + other_users[pos] + "人");

            page.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Log.i("TAG", "This page was clicked: " + pos);
                    Intent intent = new Intent(MainActivity.this, signage_informationActivity.class);
                    intent.putExtra("RequestID", RequestID[pos]);
                    intent.putExtra("input", "now");
                    startActivity(intent);
                }
            });
            ((ViewPager) collection).addView(page, 0);
            return page;
        }
        /*@Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageview1.get(position));
            Log.d("MainActivityInstanti",position+"");
            return pageview1.get(position);
        }*/

        @Override
        public int getCount() {
            return pageview1.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object==view;
        }
    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }


        @Override
        //切換view時，下方圓點的變化。
        public void onPageSelected(int position) {
            tips[position].setBackgroundResource(R.mipmap.page_indicator_focused);
            //這個圖片就是選中的view的圓點
            for(int i=0;i<pageview.size();i++){
                if (position != i) {
                    tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                    //這個圖片是未選中view的圓點
                }
            }
        }
    }

    private Runnable GetUserRequest = new Runnable(){
        public void run() {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("account",sharedPreferences.getString("account",null))
                    .addFormDataPart("input","now")
                    .build();
            Request request = new Request.Builder()
                    .url("http://140.116.72.152/GetUserRequest.php")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Log.i("Success tag","check success");
                    result = response.body().string();
                    Log.i("Result",result);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    };
}