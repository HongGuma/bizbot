package com.bizbot.bizbot.Support;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.SupportDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SupportActivity extends Activity {
    private static final String TAG = "SupportActivity";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    private ArrayList<SupportDAO> supportList = new ArrayList<SupportDAO>();
    SupportLisAdapter listAdapter;
    Thread thread;
    Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_activity);

        Button closeBtn = (Button)findViewById(R.id.close);
        RecyclerView sRecyclerView = (RecyclerView)findViewById(R.id.support_rv); //지원사업 리스트 리사이클러뷰
        Spinner sortSpinner = (Spinner)findViewById(R.id.support_spinner);
        TextView SportCunt = (TextView)findViewById(R.id.support_list_count);

        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json"; //데이터 가져올 url
        LoadData(baseURL); //데이터 가져오는 함수
        thread.start();

        //지원 사업 리사이클러뷰
        sRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =new LinearLayoutManager(SupportActivity.this);
        sRecyclerView.setLayoutManager(layoutManager);

        //닫기 버튼 클릭시
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        //리스트 정렬
        ArrayAdapter spinAdapter = ArrayAdapter.createFromResource(this,R.array.sort_mode,R.layout.spinner_dropdown);
        sortSpinner.setAdapter(spinAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //todo: switch내 코드 중복 개선, default 값 설정
                switch (i) {
                    case 1: //최신순 정렬
                        Comparator<SupportDAO> newest = (item1, item2) -> {
                            int result = item1.getCreatPnttm().compareTo(item2.getCreatPnttm());
                            return result;
                        };
                        Collections.sort(supportList,newest);
                        listAdapter.notifyDataSetChanged();
                        break;
                    case 2: //제목순 정렬
                        Comparator<SupportDAO> title = (item1, item2) -> {
                            int result = item1.getPblancNm().compareTo(item2.getPblancNm());
                            return result;
                        };
                        Collections.sort(supportList,title);
                        listAdapter.notifyDataSetChanged();
                        break;
                    case 3: //이름순 정렬
                        Comparator<SupportDAO> agency = (item1, item2) -> {
                            int result = item1.getJrsdInsttNm().compareTo(item2.getJrsdInsttNm());
                            return result;
                        };
                        Collections.sort(supportList,agency);
                        listAdapter.notifyDataSetChanged();
                        break;
                    case 4: //접수 기간 마감순 정렬
                        Comparator<SupportDAO> term = (item1, item2) -> {
                            int result = item1.getReqstBeginEndDe().compareTo(item2.getReqstBeginEndDe());
                            return result;
                        };
                        Collections.sort(supportList,term);
                        listAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //핸들러
        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                //todo: thread callback 정의
                switch (msg.what){
                    case 0: //스레드 정상 종료시
                        listAdapter = new SupportLisAdapter(supportList); //어뎁터 생성
                        sRecyclerView.setAdapter(listAdapter);
                        SportCunt.setText("총 "+ supportList.size() +" 건");
                        break;

                    case 1: //스레드에서 에러 발생시
                    default:
                        Toast.makeText(SupportActivity.this,"에러 발생",Toast.LENGTH_SHORT).show(); //디버깅용
                        //나중에 코드 채워 넣기
                        break;
                }

            }
        };
    }

    /**
     * 서버에서 데이터 받는 함수
     * @param url : 데이터 받아올 url
     */
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

                        supportList.add(s_list);
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

    /**
     * 데이터 정렬 함수
     */


    /**
     * 뒤로가기
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
