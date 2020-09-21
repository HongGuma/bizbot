package com.bizbot.bizbot.Room.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.SupportRepository;

import java.util.List;

/**
 * Model 과 UI 사이 통신 역할
 */
public class SupportViewModel extends AndroidViewModel {
    private SupportRepository mSupportRepository;
    private LiveData<List<SupportModel>> mAllItem;


    public SupportViewModel(@NonNull Application application) {
        super(application);
        mSupportRepository = new SupportRepository(application);
        mAllItem = mSupportRepository.getAll();
    }

    public LiveData<List<SupportModel>> getAllList(){
        return mAllItem;
    }


    public void insert(SupportModel support){
        mSupportRepository.insert(support);
    }
}
