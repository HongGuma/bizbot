package com.bizbot.bizbot.Support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;

public class CategoryActivity extends Activity {
    private static final String TAG = "CategoryActivity";

    private String areaItem = "";
    private String fieldItem = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);

        ImageView closeBtn = (ImageView) findViewById(R.id.close_btn);
        TextView okBtn = (TextView)findViewById(R.id.ok_btn);
        RecyclerView areaRV = (RecyclerView)findViewById(R.id.area_category_rv); //지역 리사이클러뷰
        RecyclerView fieldRV = (RecyclerView)findViewById(R.id.field_category_rv); //분야 리사이클러뷰

        //지역 레이아웃
        GridLayoutManager areaLayoutM = new GridLayoutManager(CategoryActivity.this,5);
        CategoryAdapter areaAdapter = new CategoryAdapter(1);
        areaRV.setLayoutManager(areaLayoutM);
        areaRV.setAdapter(areaAdapter);

        //분야 레이아웃
        GridLayoutManager fieldLayouyM = new GridLayoutManager(CategoryActivity.this,2);
        CategoryAdapter fieldAdapter = new CategoryAdapter(2);
        fieldRV.setLayoutManager(fieldLayouyM);
        fieldRV.setAdapter(fieldAdapter);

        //area 어뎁터에서 handler 로 변수 받아오기
        areaAdapter.adapterHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                areaItem = msg.obj.toString();
                if(areaItem.equals("전체"))
                    areaItem = null;
            }
        };

        //분야 어뎁터에서 변수 받아오기
        fieldAdapter.adapterHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                fieldItem = msg.obj.toString();
                if(fieldItem.equals("전체"))
                    fieldItem = null;
            }
        };

        //닫기 버튼 클릭시
        closeBtn.setOnClickListener(view -> {
            startActivity(new Intent(CategoryActivity.this,SupportActivity.class));
            finish();
        });

        //ok 버튼 클릭시
        okBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryActivity.this, SupportActivity.class);
            intent.putExtra("areaItem",areaItem);
            intent.putExtra("fieldItem",fieldItem);
            startActivity(intent);
            finish();
        });

    }

    //뒤로가기 버튼 클릭시
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
