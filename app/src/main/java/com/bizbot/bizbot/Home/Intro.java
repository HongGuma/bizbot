package com.bizbot.bizbot.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
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

        TextView hi = (TextView)findViewById(R.id.intro_hi);
        ProgressBar loading = (ProgressBar)findViewById(R.id.progressBar2);

        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json";

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
                    hi.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Intro.this,MainActivity.class));
                    finish();
                    //Log.d(TAG, "startActivity");
                }
            }
        });


        introHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        hi.setVisibility(View.VISIBLE);
                        Toast.makeText(getBaseContext(),"데이터 불러오기 성공!",Toast.LENGTH_SHORT).show();
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
                Message message = new Message();

                LoadSupportData load = new LoadSupportData(url);
                message.what = load.LoadData(getBaseContext());
                introHandler.sendMessage(message);

            }
        });

        thread.start();

    }



}
