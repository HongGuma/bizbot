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
import android.widget.LinearLayout;

import com.bizbot.bizbot.Category.CategoryAdapter;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.LoadSupportData;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.ViewModel.SupportViewModel;
import com.bizbot.bizbot.Support.SupportActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    private List<SupportModel> adList; //광고 데이터 리스트
    CategoryAdapter areaAdapter; //지역 어뎁터
    CategoryAdapter fieldAdapter; //분야 어뎁터
    Handler mHandler;
    boolean areaBtnChk = false;
    boolean fieldBtnChk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView adRecyclerView = (RecyclerView)findViewById(R.id.ad_list); //광고 리사이클러뷰
        RecyclerView cRecyclerView = (RecyclerView)findViewById(R.id.area_category_rv);//카테고리 리사이클러뷰
        LinearLayout areaBtn = (LinearLayout)findViewById(R.id.area_btn); //지역 버튼
        LinearLayout field = (LinearLayout)findViewById(R.id.field_btn); //분야 버튼
        LinearLayout categoryLayout = (LinearLayout)findViewById(R.id.area_category); //카테고리 레이아웃
        BottomNavigationView bottomBtn = (BottomNavigationView)findViewById(R.id.bottom_navigation); //하단 네비게이션 버튼

        //String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json"; //데이터 가져올 url

        //하단 네비게이션 버튼
        BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = menuItem -> {
            Intent intent;
            switch (menuItem.getItemId()){
                case R.id.home:
                    break;
                case R.id.support:
                    intent = new Intent(MainActivity.this, SupportActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    break;
                case R.id.favourite:
                case R.id.partner:
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
                    areaAdapter = new CategoryAdapter(1); // type: 1 = 지역, 2 = 분야
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
                    fieldAdapter = new CategoryAdapter(2);
                    cRecyclerView.setAdapter(fieldAdapter);
                    fieldBtnChk = true;
                }else{
                    categoryLayout.setVisibility(View.GONE);
                    fieldBtnChk = false;
                }

            }
        });


        /*
        //데이터 동기화
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadDataAsync(baseURL);
            }
        },36000);

        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case THREAD_END: //스레드 정상 종료시
                        adListAdapter.setList(adList); //데이터 갱신
                        break;
                    case THREAD_ERROR: //스레드에서 에러 발생시
                    default:
                        Toast.makeText(MainActivity.this,"데이터 업로드에 실패하였습니다.",Toast.LENGTH_SHORT).show(); //디버깅용
                        //나중에 코드 채워 넣기
                        break;
                }

            }
        };

         */


    }


    public void LoadDataAsync(String url){

        Thread thread = new Thread(()->{
            AppDatabase db = Room.databaseBuilder(getBaseContext(),AppDatabase.class,"app_db").build();

            LoadSupportData load = new LoadSupportData(url);
            Message message = new Message();
            message.what = load.LoadData(getBaseContext());
            mHandler.sendMessage(message);
        });

        thread.start();

    }


}