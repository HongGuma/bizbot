package com.bizbot.bizbot.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bizbot.bizbot.Room.DAO.SupportDAO;
import com.bizbot.bizbot.Room.Entity.SupportModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * local에서 가져올지 network에서 가져올지 정함
 */
public class SupportRepository  {
    private SupportDAO mSupportDAO;
    private LiveData<List<SupportModel>> mAllItem;

    public SupportRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        mSupportDAO = db.supportDAO(); //db에 있는 supportDAO 가져오기
        mAllItem = mSupportDAO.getAll(); //DAO 쿼리문 이용
    }


    //모든 데이터 출력
    public LiveData<List<SupportModel>> getAll(){
        return mAllItem;
    }

    //타이틀 출력

    //삽입
    public void insert(SupportModel support){
        Runnable addRun = () -> mSupportDAO.insert(support);

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(addRun);

    }




}
