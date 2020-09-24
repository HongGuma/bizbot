package com.bizbot.bizbot.Background;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataService extends Service {
    private static final String TAG = "DataService";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //파일 다운로드 같은 작업 수행
            try {
                Thread.sleep(5000);
                Log.d(TAG, "handleMessage: thread 실행중");
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            Log.d(TAG, "handleMessage: Thread 종료");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //서비스를 실행하는 스레드
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //각 시작 요청에 대해 작업을 시작하라는 메시지를 보내고 시작 ID를 전달하여 작업 완료시 중지되는 요청을 알 수 있다.
        Log.d(TAG, "onStartCommand: service starting");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: service done");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
