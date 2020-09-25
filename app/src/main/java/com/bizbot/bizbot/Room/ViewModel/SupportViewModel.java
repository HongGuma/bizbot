package com.bizbot.bizbot.Room.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.AppRepository;

import java.util.List;

/**
 * Model 과 UI 사이 통신 역할
 */
public class SupportViewModel extends AndroidViewModel {
    private AppRepository mAppRepository;
    private LiveData<List<SupportModel>> mAllItem;
    private LiveData<List<SupportModel>> mAllLiked;


    public SupportViewModel(@NonNull Application application) {
        super(application);
        mAppRepository = new AppRepository(application);
        mAllItem = mAppRepository.getAllSupportItem();
        mAllLiked = mAppRepository.getAllLikedItem();
    }

    public LiveData<List<SupportModel>> getAllList(){
        return mAllItem;
    }

    public LiveData<List<SupportModel>> getAllLiked() {
        return mAllLiked;
    }

    public void insert(SupportModel support){
        mAppRepository.insertSupportItem(support);
    }
}
