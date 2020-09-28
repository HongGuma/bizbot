package com.bizbot.bizbot.Setting;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bizbot.bizbot.Background.DataJobService;
import com.bizbot.bizbot.R;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";

    private static final int JOB_ID_UPDATE = 0x1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        ImageView closeBtn = (ImageView)findViewById(R.id.close_btn);

        //백그라운드 동기화
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID_UPDATE,new ComponentName(this, DataJobService.class))
                .setRequiresStorageNotLow(true) //충분한 저장공간
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) //네트워크 타입
                .build();

        JobScheduler jobScheduler = (JobScheduler) getBaseContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
        
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fileList();
    }
}
