package com.bizbot.bizbot.Support;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.Category.CategoryActivity;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.ViewModel.SupportViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

public class SupportActivity extends AppCompatActivity {
    private static final String TAG = "SupportActivity";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    private List<SupportModel> supportList;
    ProgressBar asyncDialog;
    SupportListAdapter listAdapter;
    Thread thread;
    Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_activity);

        Button closeBtn = (Button)findViewById(R.id.close); //닫기 버튼
        ImageButton categoryBtn = (ImageButton)findViewById(R.id.category_menu_btn); //카테고리 메뉴 버튼
        RecyclerView sRecyclerView = (RecyclerView)findViewById(R.id.support_rv); //지원사업 리스트 리사이클러뷰
        Spinner sortSpinner = (Spinner)findViewById(R.id.support_spinner); //정렬 선택 버튼
        TextView SportCunt = (TextView)findViewById(R.id.support_list_count); //지원사업 건수

        //카테고리에서 받아온 지역과 분야 키워드
        String areaWord = getIntent().getStringExtra("areaItem");
        String fieldWord = getIntent().getStringExtra("fieldItem");

        //지원 사업 리사이클러뷰
        sRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =new LinearLayoutManager(SupportActivity.this);
        sRecyclerView.setLayoutManager(layoutManager);


        //DB에서 데이터 가져오기
        SupportViewModel supportViewModel = ViewModelProviders.of(this).get(SupportViewModel.class);
        supportViewModel.getAllList().observe(SupportActivity.this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                //Log.d(TAG, "onChanged: supportModels.size()="+supportModels.size());
                supportList=supportModels;
                listAdapter = new SupportListAdapter(getBaseContext(),supportList,areaWord,fieldWord);
                sRecyclerView.setAdapter(listAdapter);
                //listAdapter.setList(supportModels);
            }
        });

        //listAdapter = new SupportListAdapter(getBaseContext(),supportList,areaWord,fieldWord);
        //sRecyclerView.setAdapter(listAdapter);


        /*
        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json"; //데이터 가져올 url
        LoadData(baseURL); //데이터 가져오는 함수
        if(supportList == null)
            thread.start();
         */



        //닫기 버튼 클릭시
        closeBtn.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,0); //에니메이션 제거
        });

        //카테고리 메뉴 버튼 클릭시
        categoryBtn.setOnClickListener(view -> {
            startActivity(new Intent(SupportActivity.this, CategoryActivity.class));
        });

        /*
        //핸들러
        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                //todo: thread callback 정의
                switch (msg.what){
                    case 0: //스레드 정상 종료시
                        listAdapter = new SupportLisAdapter(getBaseContext(),supportList,areaWord,fieldWord); //어뎁터 생성
                        sRecyclerView.setAdapter(listAdapter);
                        listAdapter.notifyDataSetChanged();

                        SportCunt.setText("총 "+ listAdapter.ItemCount() +" 건");
                        break;

                    case 1: //스레드에서 에러 발생시
                        TextView error = (TextView)findViewById(R.id.error_text);
                        error.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Toast.makeText(SupportActivity.this,"에러 발생",Toast.LENGTH_SHORT).show(); //디버깅용
                        //나중에 코드 채워 넣기
                        break;
                }

            }
        };
         */

        //리스트 정렬
        ArrayAdapter spinAdapter = ArrayAdapter.createFromResource(this,R.array.sort_mode,R.layout.spinner_dropdown);
        sortSpinner.setAdapter(spinAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(listAdapter != null)
                    listAdapter.ListSort(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    /**
     * 서버에서 데이터 받는 함수
     * @param url : 데이터 받아올 url
     */
    private void LoadData(String url) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message message = new Message();
                long start=0,end=0;
                try{
                    start = System.currentTimeMillis();
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

                        SupportModel s_list = new SupportModel();

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
                    end = System.currentTimeMillis();
                    //Log.d(TAG, "run: supportList="+supportList.get(0).getPblancNm());

                } catch (JSONException | IOException e){
                    e.printStackTrace();
                    message.what = THREAD_ERROR;
                }finally {
                    System.out.println("data loading : "+(end-start)/1000.0);
                    message.what = THREAD_END; //정상 종료
                    asyncDialog = new ProgressBar(SupportActivity.this);
                    asyncDialog.setProgress(R.id.progressBar);

                    mHandler.sendMessage(message);
                }
            }
        });

    }


    /**
     * 뒤로가기
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
