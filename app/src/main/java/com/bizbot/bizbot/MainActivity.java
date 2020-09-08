package com.bizbot.bizbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    private ArrayList<SupportDAO> supportList = new ArrayList<SupportDAO>();
    Handler mHandler;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json";

        DataLoad(baseURL);
        thread.start();
        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == THREAD_END){
                    Log.d(TAG, "true: supportList.size()="+supportList.size());
                    RecyclerView adRecyclerView = (RecyclerView)findViewById(R.id.ad_list);
                    adRecyclerView.setHasFixedSize(true); //리사이클러뷰 크기 고정

                    LinearLayoutManager layoutManager =new LinearLayoutManager(MainActivity.this);
                    adRecyclerView.setLayoutManager(layoutManager);

                    ListAdapter listAdapter = new ListAdapter(supportList);
                    adRecyclerView.setAdapter(listAdapter);

                }else{
                    Log.d(TAG, "else: supportList.size()="+supportList.size());
                }
            }
        };




    }

    private void DataLoad(String url){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message message = new Message();
                try{
                    URL url1 = new URL(url); //url 가져오기
                    conn = (HttpURLConnection) url1.openConnection(); //url 연결
                    String page = "";

                    /*
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    Log.d(TAG, "onCreate: reader = "+reader.toString());

                    String line = reader.readLine();
                    */

                    InputStream is = getAssets().open("data.json");
                    InputStreamReader ir = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(ir);

                    String line = br.readLine();

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

                        supportList.add(s_list);
                    }
                    message.what = THREAD_END;
                    //Log.d(TAG, "run: supportList="+supportList.get(0).getPblancNm());

                }catch (MalformedURLException e){
                    e.printStackTrace();
                    message.what = THREAD_ERROR;
                }catch (IOException e){
                    e.printStackTrace();
                    message.what = THREAD_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = THREAD_ERROR;
                } finally {
                    if(conn != null){
                        conn.disconnect(); //연결 종료
                        message.what = THREAD_END;
                    }
                    mHandler.sendMessage(message);
                }
            }
        });

    }


}