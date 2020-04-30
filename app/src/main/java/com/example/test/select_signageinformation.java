package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class select_signageinformation extends AppCompatActivity {

    String result,account;
    String[] RequestID,start,end,alone,resultarr;
    private ViewPager viewPager;

    //三個view
    private View[] view;

    //用來存放view並傳遞給viewPager的介面卡。
    private ArrayList<View> pageview;


    //用來存放圓點，沒有寫第四步的話，就不要定義一下三個變量了。
    private ImageView[] tips = new ImageView[3];

    private ImageView imageView;
    private SharedPreferences sharedPreferences;
    //圓點組的物件
    private ViewGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_signageinformation);

        sharedPreferences = getApplication().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        account = sharedPreferences.getString("account",null);
        try{
            Thread thread = new Thread(GetUserRequest);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(result.equals("0")) {
            LinearLayout templayout = findViewById(R.id.layout1);
            templayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0, 0));
            TextView textView = findViewById(R.id.text);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0, 2));
            textView.setText("您目前沒有租用記錄");
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

            //resultarr[4] = resultarr[4].substring(1);
            //other_users = resultarr[4].split(",");

            for(int i = 0; i < RequestID.length; i++) {
                RequestID[i] = RequestID[i].substring(1, RequestID[i].length() - 1);
                alone[i] = alone[i].substring(1, alone[i].length() - 1);
                start[i] = start[i].substring(1,start[i].length()-8);
                end[i] = end[i].substring(1,end[i].length()-8);
            }

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
                imageView = new ImageView(select_signageinformation.this);
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

            TextView temp = page.findViewById(R.id.RequestID);
            temp.setText("RequestID: " + RequestID[pos]);

            String[] tempstr;

            temp = page.findViewById(R.id.start);
            tempstr = start[pos].split(" ");
            temp.setText(tempstr[0] + "\n" + tempstr[1]);

            temp = page.findViewById(R.id.end);
            tempstr = end[pos].split(" ");
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
                    Intent intent = new Intent(select_signageinformation.this, signage_informationActivity.class);
                    intent.putExtra("RequestID", RequestID[pos]);
                    intent.putExtra("input", "history");
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
                    .addFormDataPart("account",account)
                    .addFormDataPart("input","history")
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
