package com.bizbot.bizbot.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Search.SearchActivity;
import com.bizbot.bizbot.Setting.SettingActivity;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Background.LoadSupportData;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.ViewModel.SupportViewModel;
import com.bizbot.bizbot.Support.SupportActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int REPEAT_DELAY = 1800000;//반복할 시간 (30분 = 1800000)
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    private List<SupportModel> adList; //광고 데이터 리스트
    AreaCategoryAdapter areaAdapter; //지역 어뎁터
    AreaCategoryAdapter fieldAdapter; //분야 어뎁터

    Handler DBHandler; //db 핸들러
    boolean areaBtnChk = false;
    boolean fieldBtnChk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //레이아웃 선언 부분
        RecyclerView adRecyclerView = (RecyclerView)findViewById(R.id.ad_list); //광고 리사이클러뷰
        RecyclerView cRecyclerView = (RecyclerView)findViewById(R.id.area_category_rv);//카테고리 리사이클러뷰
        LinearLayout search = (LinearLayout)findViewById(R.id.search_bar);
        LinearLayout areaBtn = (LinearLayout)findViewById(R.id.area_btn); //지역 버튼
        LinearLayout field = (LinearLayout)findViewById(R.id.field_btn); //분야 버튼
        LinearLayout categoryLayout = (LinearLayout)findViewById(R.id.area_category); //카테고리 레이아웃
        BottomNavigationView bottomBtn = (BottomNavigationView)findViewById(R.id.bottom_navigation); //하단 네비게이션 버튼
        Button settingBtn = (Button)findViewById(R.id.setting_btn);

        //하단 네비게이션 버튼
        BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = menuItem -> {
            Intent intent;
            switch (menuItem.getItemId()){
                //todo: home 버튼 누를때 기능 아직 미정
                case R.id.home:
                    break;
                case R.id.support: //지원 사업 페이지로 이동
                    intent = new Intent(MainActivity.this, SupportActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    break;
                case R.id.favourite: //관심 지원 사업 페이지로 이동
                case R.id.partner: //파트너 사업 페이지로 이동
            }
            return true;
        };
        bottomBtn.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //광고 리사이클러뷰
        adRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =new LinearLayoutManager(MainActivity.this);
        adRecyclerView.setLayoutManager(layoutManager);

        //광고 리사이클러뷰 어뎁터
        AdListAdapter adListAdapter = new AdListAdapter(getBaseContext());

        //변화 감지해서 리스트 갱신
        //todo: 지금은 지원 사업 리스트 가져오지만 나중에 광고 리스트로 수정
        SupportViewModel supportViewModel = ViewModelProviders.of(this).get(SupportViewModel.class);
        supportViewModel.getAllList().observe(this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                //Log.d(TAG, "onChanged: supportModels="+supportModels.size());
                adListAdapter.setList(supportModels); //DB에서 꺼내온 리스트 교체
                adRecyclerView.setAdapter(adListAdapter);
            }
        });


        //지역,분야 리사이클러뷰
        cRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,5); //지역, 분야 카테고리 리사이클러뷰
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(MainActivity.this,2);

        //지역 버튼 클릭시
        areaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(areaBtnChk == false){
                    fieldBtnChk = false; //분야 버튼 false
                    cRecyclerView.setLayoutManager(gridLayoutManager); //그리드 레이아웃 매니저 설정
                    categoryLayout.setVisibility(View.VISIBLE); //카테고리 보이기
                    //areaAdapter = new CategoryAdapter(1); // type: 1 = 지역, 2 = 분야
                    areaAdapter = new AreaCategoryAdapter(1);
                    cRecyclerView.setAdapter(areaAdapter); //어뎁터 설정
                    areaBtnChk = true;
                }else{
                    categoryLayout.setVisibility(View.GONE); //카테고리 없애기
                    areaBtnChk = false;
                }

            }
        });

        //분야 버튼 클릭시
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fieldBtnChk == false){
                    areaBtnChk = false;
                    cRecyclerView.setLayoutManager(gridLayoutManager2);
                    categoryLayout.setVisibility(View.VISIBLE);
                    //fieldAdapter = new CategoryAdapter(2);
                    fieldAdapter = new AreaCategoryAdapter(2);
                    cRecyclerView.setAdapter(fieldAdapter);
                    fieldBtnChk = true;
                }else{
                    categoryLayout.setVisibility(View.GONE);
                    fieldBtnChk = false;
                }

            }
        });

        //설정 버튼 클릭시
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        //검색바 클릭시
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}