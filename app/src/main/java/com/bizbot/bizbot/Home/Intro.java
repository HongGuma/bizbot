package com.bizbot.bizbot.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.bizbot.bizbot.Background.DataService;
import com.bizbot.bizbot.Background.LoadSupportData;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.ViewModel.SupportViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Intro extends AppCompatActivity {
    private static final String TAG = "Intro";
    private static final int REPEAT_DELAY = 15000;

    private SupportViewModel supportViewModel;
    Handler introHandler;
    Handler mHandler; //메인 핸들러
    Handler timerHandler; //타이머 핸들러
    ArrayList<String> list2 = new ArrayList<String>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        LinearLayout logo = (LinearLayout)findViewById(R.id.intro_logo);
        LinearLayout loading = (LinearLayout)findViewById(R.id.intro_loading);

        //디버깅용========
        //ClearDB();
        //Downloading();
        //================

        File dbPath = getBaseContext().getDatabasePath("app_db");
        //Log.d(TAG, "onCreate: dbPath="+dbPath+" : "+dbPath.exists());

        if(dbPath.exists()){ //db 존재
            loading.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
            startActivity(new Intent(Intro.this,MainActivity.class));
            finish();
        }else{ //없음
            Downloading();
        }


        //데이터 받는 스레드 핸들러
        introHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        loading.setVisibility(View.GONE);
                        logo.setVisibility(View.VISIBLE);
                        Toast.makeText(getBaseContext(),"반갑습니다!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intro.this,MainActivity.class));
                        finish();
                        break;
                    case 1:
                        Toast.makeText(getBaseContext(),"에러 발생!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intro.this,MainActivity.class));
                        finish();
                        break;

                }
            }
        };

        /*
        timerHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage: 백그라운드 시작");
                Intent backIntent = new Intent(Intro.this, DataService.class);
                startService(backIntent);
                stopService(backIntent);
                this.sendEmptyMessageDelayed(0,REPEAT_DELAY);
            }
        };
        timerHandler.sendEmptyMessage(0);


         */
    }


    //서버에서 데이터 다운
    private void Downloading(){
        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                LoadSupportData load = new LoadSupportData(baseURL);
                message.what = load.LoadData(getBaseContext());
                introHandler.sendMessage(message);
            }
        });
        thread.start();

    }

    //테이블 레코드 삭제 디버깅용
    private void ClearDB(){
        Thread thread = new Thread(()->{
            AppDatabase db = Room.databaseBuilder(getBaseContext(),AppDatabase.class,"app_db").build();
            db.supportDAO().deleteAll();
            db.close();
        });
        thread.start();
    }



}
