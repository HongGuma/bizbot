package com.bizbot.bizbot.Home;

import android.Manifest;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.bizbot.bizbot.Background.DataJobService;
import com.bizbot.bizbot.Background.InitData;
import com.bizbot.bizbot.Background.SynchronizationData;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.AppViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Intro extends AppCompatActivity {
    private static final String TAG = "Intro";
    private static final int MULTIPLE_PERMISSION = 10235;//권한 변수
    public static final int JOB_ID_UPDATE = 0x1000;

    private AppDatabase db;

    boolean check =false;
    Handler introHandler;
    String[] PERMISSION = { //권한 종류
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        File dbPath = getBaseContext().getDatabasePath("app_db");
        LinearLayout logo = (LinearLayout)findViewById(R.id.intro_logo);
        LinearLayout loading = (LinearLayout)findViewById(R.id.intro_loading);

        if(!dbPath.exists()){//db가 없는 사용자 == 첫 접속 사용자'
            CustomDialog();
            Downloading();
            /*
            todo: 지금은 파일과 위치 권한을 사용하는 일이 없어서 권한 얻는 함수는 사용 x
            alertDialog.setTitle("비즈봇 접근 권한 안내");
            alertDialog.setMessage("저장(선택): 지원 사업  ");
            alertDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(Intro.this,PERMISSION,MULTIPLE_PERMISSION); //권한 요청

                }
            });

             */
        }else if(dbPath.exists() || checkData()){
            /*//db도 있고 데이터도 있는 사용자
            if(!hasPermission(this,PERMISSION)){ //권한 없는 경우
                ActivityCompat.requestPermissions(Intro.this,PERMISSION,MULTIPLE_PERMISSION); //권한 요청
            }
             */
            //백그라운드 작업이 실행될 조건

            BackgroundLoadData();

            loading.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
            startActivity(new Intent(Intro.this, MainActivity.class));
            finish();

        }else if(dbPath.exists() && !checkData()){
            //db는 있는데 데이터가 없는 사용자
            Downloading();
        }

        //Downloading thread 끝난 후 값 받아와서 실행
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
                        //todo: 데이터 로드 에러 발생시 작업, 수정
                        Toast.makeText(getBaseContext(),"에러 발생!",Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };

    }

    //db 데이터 있는지 없는지
    public boolean checkData(){
        AppViewModel appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        appViewModel.getAllSupportItem().observe(this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                if(supportModels == null)
                    check = false;
                else
                    check = true;
            }
        });

        return check;
    }

    //권한 요청에 대한 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MULTIPLE_PERMISSION:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Message message = new Message();
                    message.what = 0;
                    introHandler.sendMessage(message);
                }else{
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:"+getApplicationContext().getPackageName()));
                    startActivity(intent);
                }
                return;
        }

    }

    //권한 여부 확인
    //PERMISSION_GRANTED : 권한 보유
    //PERMISSION_DENIED : 권한 미보유
    public boolean hasPermission(Context context, String... permissions){
        if(context != null && permissions != null){
            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    //서버에서 데이터 다운
    private void Downloading(){
        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                InitData load = new InitData(getBaseContext());
                message.what = load.LoadData();
                introHandler.sendMessage(message);
            }
        });
        thread.start();

    }

    //알림 여부 db저장
    public void DB_IO(PermitModel permit){
        Thread thread = new Thread(()->{
            db = Room.databaseBuilder(Intro.this,AppDatabase.class,"app_db").build();
            db.permitDAO().insert(permit);
            db.close();
        });
        thread.start();
    }

    //커스텀 다이얼로그
    public void CustomDialog(){
        Dialog dialog = new Dialog(Intro.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.intro_dialog_view);
        dialog.show();

        Button yesBtn = (Button)dialog.findViewById(R.id.yes_btn);
        Button noBtn = (Button)dialog.findViewById(R.id.no_btn);

        PermitModel permitModel = new PermitModel();

        //첫 동기화 시간
        Date syncDate = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String syncTime = simpleDateFormat.format(syncDate);

        permitModel.setSyncTime(syncTime);

        yesBtn.setOnClickListener(view -> {
            permitModel.setAlert(true);
            DB_IO(permitModel);
            dialog.dismiss();
        });

        noBtn.setOnClickListener(view ->{
            permitModel.setAlert(false);
            DB_IO(permitModel);
            dialog.dismiss();
        });

    }

    //백그라운드 동기화
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void BackgroundLoadData(){
        Thread thread = new Thread(()->{
            db = Room.databaseBuilder(Intro.this,AppDatabase.class,"app_db").build();
            boolean check = db.permitDAO().isAlertCheck();
            JobScheduler jobScheduler = (JobScheduler) getBaseContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID_UPDATE,new ComponentName(this, DataJobService.class))
                    .setRequiresStorageNotLow(true) //충분한 저장공간
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) //네트워크 타입
                    .setPeriodic(TimeUnit.MINUTES.toMillis(20))//20분마다 실행
                    //.setTriggerContentMaxDelay(TimeUnit.MINUTES.toMillis(3))//3분후 실행
                    .build();

            if(!check){//1회만 실행
                jobScheduler.cancel(jobInfo.getId()); //기존의 반복 백그라운드 중지
                Log.d(TAG, "run: 데이터 다운 완료");
                SynchronizationData sync = new SynchronizationData(this);
                int result = sync.SyncData();
                if(result == 0)
                    Log.d(TAG, "run: 데이터 다운 완료");
                else
                    Log.d(TAG, "run: 데이터 다운 실패");
            }else{
                if(jobInfo != null)
                    jobScheduler.schedule(jobInfo);
            }
            db.close();
        });
        thread.start();
    }
}
