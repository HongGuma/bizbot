package com.bizbot.bizbot.Room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.AppRepository;

import java.util.List;

/**
 * Model 과 UI 사이 통신 역할
 */
public class AppViewModel extends AndroidViewModel {
    private AppRepository mAppRepository;
    private LiveData<List<SupportModel>> mAllItem; //지원 사업 아이템
    private LiveData<List<SupportModel>> mAllLiked; //관심 사업 아이템
    private LiveData<PermitModel> alert; //알림 설정


    public AppViewModel(@NonNull Application application) {
        super(application);
        mAppRepository = new AppRepository(application);
        mAllItem = mAppRepository.getAllSupportItem();
        mAllLiked = mAppRepository.getAllLikedItem();
        alert = mAppRepository.getAlertState();
    }

    //지원 사업 아이템 출력
    public LiveData<List<SupportModel>> getAllList(){
        return mAllItem;
    }

    //관심 사업 아이템 출력
    public LiveData<List<SupportModel>> getAllLiked() {
        return mAllLiked;
    }

    //지원 사업 아이템 추가
    public void insert(SupportModel support){
        mAppRepository.insertSupportItem(support);
    }

    public LiveData<PermitModel> getAlert(){
        return alert;
    }

    public void setAlert(PermitModel check){
        mAppRepository.updateAlertState(check);
    }

}
