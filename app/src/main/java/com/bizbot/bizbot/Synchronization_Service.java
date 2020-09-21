package com.bizbot.bizbot;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.bizbot.bizbot.Room.AppDatabase;

public class Synchronization_Service extends Service {
    private static final String TAG = "Data_Synchronization";

    Handler serviceHandler;

    public Synchronization_Service() { }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d(TAG, "onCreate()");
        serviceHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage: 서비스 종료");
                if(msg.what == 0)
                    onDestroy();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand()");

        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json"; //지원사업 링크

        //LoadSupportData load = new LoadSupportData(baseURL);
        Message message = new Message();
        //message.what = load.LoadData(getBaseContext());
        Thread thread = new Thread(()->{
            try {
                Thread.sleep(5000);
                message.what=0;
                serviceHandler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
          
        
        return START_REDELIVER_INTENT;//시스템에 의해 종료 되어도 데이터 유지
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Log.d(TAG, "onBind()");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Log.d(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

}
