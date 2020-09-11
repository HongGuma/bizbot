package com.bizbot.bizbot.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Support.SupportActivity;
import com.bizbot.bizbot.SupportDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    private ArrayList<SupportDAO> adList = new ArrayList<SupportDAO>(); //광고 데이터 리스트
    AreaCategoryAdapter areaAdapter;
    Handler mHandler;
    Thread thread;
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

        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json"; //데이터 가져올 url
        LoadData(baseURL); //데이터 가져오는 함수
        thread.start();

        //하단 버튼
        BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:

                    case R.id.support:
                        Intent intent = new Intent(MainActivity.this, SupportActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.favourite:

                    case R.id.partner:
                }
                return true;
            }
        };
        bottomBtn.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        adRecyclerView.setHasFixedSize(true); //리사이클러뷰 크기 고정
        LinearLayoutManager layoutManager =new LinearLayoutManager(MainActivity.this);
        adRecyclerView.setLayoutManager(layoutManager);

        cRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,5); //지역, 분야 카테고리 리사이클러뷰
        cRecyclerView.setLayoutManager(gridLayoutManager);

        //지역 버튼 클릭시
        areaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(areaBtnChk == false){
                    fieldBtnChk = false;
                    categoryLayout.setVisibility(View.VISIBLE);
                    areaAdapter = new AreaCategoryAdapter(1);
                    cRecyclerView.setAdapter(areaAdapter);
                    areaBtnChk = true;
                }else{
                    categoryLayout.setVisibility(View.GONE);
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
                    categoryLayout.setVisibility(View.VISIBLE);
                    areaAdapter = new AreaCategoryAdapter(2);
                    cRecyclerView.setAdapter(areaAdapter);
                    fieldBtnChk = true;
                }else{
                    categoryLayout.setVisibility(View.GONE);
                    fieldBtnChk = false;
                }

            }
        });


        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case THREAD_END: //스레드 정상 종료시
                        AdListAdapter adListAdapter = new AdListAdapter(adList); //어뎁터 생성
                        adRecyclerView.setAdapter(adListAdapter);
                        break;

                    case THREAD_ERROR: //스레드에서 에러 발생시
                    default:
                        Toast.makeText(MainActivity.this,"에러 발생",Toast.LENGTH_SHORT).show(); //디버깅용
                        //나중에 코드 채워 넣기
                        break;
                }

            }
        };

    }

    //서버에서 데이터 받아오는 함수
    private void LoadData(String url){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message message = new Message();
                try{
                    //RequestURLConnection requestURLConnection = new RequestURLConnection(url);
                    String line = "";

                    InputStream is = getAssets().open("data.json");
                    InputStreamReader ir = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(ir);

                    line = br.readLine();

                    //line = requestURLConnection.DataLoad();
                    JSONObject json = new JSONObject(line);
                    JSONArray jsonArray = json.getJSONArray("jsonArray");
                    for(int i=0; i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        SupportDAO s_list = new SupportDAO();

                        String industNm = jsonObject.optString("industNm");
                        s_list.setIndustNm(industNm);
                        s_list.setRceptInsttEmailAdres(jsonObject.optString("rceptInsttEmailAdres"));
                        s_list.setInqireCo(jsonObject.optInt("inqireCo"));
                        s_list.setRceptEngnHmpgUrl(jsonObject.optString("rceptEngnHmpgUrl"));
                        s_list.setPblancUrl(jsonObject.optString("pblancUrl"));
                        s_list.setJrsdInsttNm(jsonObject.optString("jrsdInsttNm"));
                        s_list.setRceptEngnNm(jsonObject.optString("rceptEngnNm"));
                        s_list.setEntrprsStle(jsonObject.optString("entrprsStle"));
                        s_list.setPldirSportRealmLclasCodeNm(jsonObject.optString("pldirSportRealmLclasCodeNm"));
                        s_list.setTrgetNm(jsonObject.optString("trgetNm"));
                        s_list.setRceptInsttTelno(jsonObject.optString("rceptInsttTelno"));
                        s_list.setBsnsSumryCn(jsonObject.optString("bsnsSumryCn0px"));
                        s_list.setReqstBeginEndDe(jsonObject.optString("reqstBeginEndDe"));
                        s_list.setAreaNm(jsonObject.optString("areaNm"));
                        s_list.setPldirSportRealmMlsfcCodeNm(jsonObject.optString("pldirSportRealmMlsfcCodeNm"));
                        s_list.setRceptInsttChargerDeptNm(jsonObject.optString("rceptInsttChargerDeptNm"));
                        s_list.setRceptInsttChargerNm(jsonObject.optString("rceptInsttChargerNm"));
                        s_list.setPblancNm(jsonObject.optString("pblancNm"));
                        s_list.setCreatPnttm(jsonObject.optString("creatPnttm"));
                        s_list.setPblancId(jsonObject.optString("pblancId"));

                        adList.add(s_list);
                    }
                    message.what = THREAD_END;
                    //Log.d(TAG, "run: supportList="+supportList.get(0).getPblancNm());

                }catch (IOException e) {
                    e.printStackTrace();
                    message.what = THREAD_ERROR;
                }catch (JSONException e){
                    e.printStackTrace();
                    message.what = THREAD_ERROR;
                }finally {
                    message.what = THREAD_END;
                    mHandler.sendMessage(message);
                }
            }
        });

    }



}