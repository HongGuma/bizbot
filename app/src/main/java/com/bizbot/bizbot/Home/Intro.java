package com.bizbot.bizbot.Home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bizbot.bizbot.Background.DataJobService;
import com.bizbot.bizbot.Background.LoadSupportData;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.ViewModel.SupportViewModel;

import java.io.File;
import java.util.List;

public class Intro extends AppCompatActivity {
    private static final String TAG = "Intro";
    private static final int MULTIPLE_PERMISSION = 10235;//권한 변수

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
            Downloading();
            PermitModel permitModel = new PermitModel();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("알림 동의");
            alertDialog.setMessage("BizBot만의 분석기술로\n 우리회사에 꼭 맞는\n 지원사업 알림을 받아보세요");
            alertDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    permitModel.setAlert(true);
                    DB_IO(permitModel);
                }
            });
            alertDialog.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    permitModel.setAlert(false);
                    DB_IO(permitModel);
                }
            });
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
            alertDialog.create().show();
        }else if(dbPath.exists() || checkData()){
            //db도 있고 데이터도 있는 사용자
            /*
            if(!hasPermission(this,PERMISSION)){ //권한 없는 경우
                ActivityCompat.requestPermissions(Intro.this,PERMISSION,MULTIPLE_PERMISSION); //권한 요청
            }
             */
            loading.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
            startActivity(new Intent(Intro.this, MainActivity.class));
            finish();
        }else if(dbPath.exists() && !checkData()){
            Downloading();
        }


        //디버깅용========
        //ClearDB();
        //Downloading();
        //================




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
                        //todo: 데이터 로드 에러 발생시 처리 수정
                        Toast.makeText(getBaseContext(),"에러 발생!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intro.this,MainActivity.class));
                        finish();
                        break;

                }
            }
        };

    }

    //db 데이터 있는지 없는지
    public boolean checkData(){
        SupportViewModel supportViewModel = ViewModelProviders.of(this).get(SupportViewModel.class);
        supportViewModel.getAllList().observe(this, new Observer<List<SupportModel>>() {
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
                LoadSupportData load = new LoadSupportData();
                message.what = load.LoadData(getBaseContext());
                introHandler.sendMessage(message);
            }
        });
        thread.start();

    }

    //테이블 칼럼 삭제 (디버깅용)
    private void ClearDB(){
        Thread thread = new Thread(()->{
            AppDatabase db = Room.databaseBuilder(getBaseContext(),AppDatabase.class,"app_db").build();
            db.supportDAO().deleteAll();
            db.close();
        });
        thread.start();
    }

    public void DB_IO(PermitModel permit){
        AppDatabase db = Room.databaseBuilder(Intro.this,AppDatabase.class,"app_db").build();
        db.permitDAO().insert(permit);
        db.close();
    }

}
