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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.bizbot.bizbot.LoadSupportData;
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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        LinearLayout logo = (LinearLayout)findViewById(R.id.intro_logo);
        LinearLayout loading = (LinearLayout)findViewById(R.id.intro_loading);

        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json";
        ClearDB();
        Downloading(baseURL);

        /*
        supportViewModel = ViewModelProviders.of(this).get(SupportViewModel.class);
        supportViewModel.getAllList().observe(this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                if(supportModels == null){
                    Downloading(baseURL);
                    //Log.d(TAG, "Downloading");
                }
                else{
                    loading.setVisibility(View.GONE);
                    logo.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Intro.this,MainActivity.class));
                    finish();
                    //Log.d(TAG, "startActivity");
                }
            }
        });

         */


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
    }

    private void Downloading(String url){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //AppDatabase db = Room.databaseBuilder(getBaseContext(),AppDatabase.class,"app_db").build();
                Message message = new Message();

                LoadSupportData load = new LoadSupportData(url);
                message.what = load.LoadData(getBaseContext());
                introHandler.sendMessage(message);

            }
        });

        thread.start();

    }

    private void ClearDB(){
        Thread thread = new Thread(()->{
            AppDatabase db = Room.databaseBuilder(getBaseContext(),AppDatabase.class,"app_db").build();
            db.supportDAO().deleteAll();
        });
        thread.start();
    }



}
