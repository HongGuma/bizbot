package com.bizbot.bizbot.Setting;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bizbot.bizbot.Background.DataJobService;
import com.bizbot.bizbot.Home.Intro;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.AppViewModel;
import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Search.SearchActivity;

import java.util.concurrent.TimeUnit;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        ImageView closeBtn = (ImageView)findViewById(R.id.close_btn);
        SwitchCompat alertSwitch = (SwitchCompat)findViewById(R.id.alert_check);

        AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        viewModel.getAlertState().observe(this, new Observer<PermitModel>() {
            @Override
            public void onChanged(PermitModel permitModel) {
                alertSwitch.setChecked(permitModel.isAlert());
            }
        });


        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DB_UPDATE(b);

            }
        });

        
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void DB_UPDATE(boolean check){
        Thread thread = new Thread(()->{
            AppDatabase db = Room.databaseBuilder(SettingActivity.this,AppDatabase.class,"app_db").build();
            db.permitDAO().setAlert(check);
            db.close();
        });
        thread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
