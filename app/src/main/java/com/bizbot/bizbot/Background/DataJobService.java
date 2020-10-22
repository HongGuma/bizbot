package com.bizbot.bizbot.Background;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.bizbot.bizbot.Home.Intro;

public class DataJobService extends JobService {
    private static final String TAG = "DataJobService";

    //작업 시작시
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 데이터 다운 시작");
                
                SynchronizationData syncData = new SynchronizationData(getBaseContext());
                int result = syncData.SyncData();
                if(result == 0)
                    Log.d(TAG, "run: 데이터 다운 완료");
                else
                    Log.d(TAG, "run: 데이터 다운 실패");

                jobFinished(jobParameters,false);
            }
        });
        thread.start();

        return false; //true = 실행중, false = 완료
    }

    //작업 종료시
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false; //true = 다시 실행, false = 다시 실행 안함
    }

}
