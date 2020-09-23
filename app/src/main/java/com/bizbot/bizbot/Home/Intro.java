package com.bizbot.bizbot.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.bizbot.bizbot.Background.LoadSupportData;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.ViewModel.SupportViewModel;

import java.util.ArrayList;
import java.util.List;

public class Intro extends AppCompatActivity {
    private static final String TAG = "Intro";

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

        //디버깅용
        //ClearDB();
        //Downloading(baseURL);

        //백그라운드
        //Intent intent = new Intent(this, DataService.class);
        //startService(intent);


        supportViewModel = ViewModelProviders.of(this).get(SupportViewModel.class);
        supportViewModel.getAllList().observe(this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                //데이터가 없다면 서버에서 데이터 받아온다.
                if(supportModels == null){
                    Downloading();
                    //Log.d(TAG, "Downloading");
                }
                //데이터가 있다면 intro activity 종료후 main activity 실행
                else{
                    loading.setVisibility(View.GONE);
                    logo.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Intro.this,MainActivity.class));
                    finish();
                    //Log.d(TAG, "startActivity");
                }
            }
        });


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

        //데이터 동기화 타이머 핸들러 REPEAT_DELAY 마다 해당 코드를 실행한다.
        /*
        timerHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage: 백그라운드 시작");
                LoadDataAsync();
                this.sendEmptyMessageDelayed(0,REPEAT_DELAY);
            }
        };
        timerHandler.sendEmptyMessage(0);

        //데이터 동기화 스레드 핸들러
        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case THREAD_END: //스레드 정상 종료시
                        Log.d(TAG, "handleMessage: 데이터 갱신 성공");
                        break;
                    case THREAD_ERROR: //스레드에서 에러 발생시
                        Toast.makeText(MainActivity.this,"데이터 업로드에 실패하였습니다.",Toast.LENGTH_SHORT).show(); //디버깅용
                        break;
                    default:
                        Toast.makeText(MainActivity.this,"오류가 발생했습니다.",Toast.LENGTH_SHORT).show(); //디버깅용
                        //나중에 코드 채워 넣기
                }

            }
        };


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

    //디비 삭제 디버깅용
    private void ClearDB(){
        Thread thread = new Thread(()->{
            AppDatabase db = Room.databaseBuilder(getBaseContext(),AppDatabase.class,"app_db").build();
            db.supportDAO().deleteAll();
            db.close();
        });
        thread.start();
    }



}
