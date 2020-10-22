package com.bizbot.bizbot.Room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bizbot.bizbot.Room.DAO.PermitDAO;
import com.bizbot.bizbot.Room.DAO.SearchWordDAO;
import com.bizbot.bizbot.Room.DAO.SupportDAO;
import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Room.Entity.SearchWordModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Model 과 UI 사이 통신 역할
 */
public class AppViewModel extends AndroidViewModel {
    /*
    private AppRepository mAppRepository;
    private LiveData<List<SupportModel>> mAllItem; //지원 사업 아이템
    private LiveData<List<SupportModel>> mAllLiked; //관심 사업 아이템
    private LiveData<PermitModel> alert; //알림 설정
    private LiveData<List<SearchWordModel>> mAllSearch;//최근 검색어 아이템

     */
    private PermitDAO mPermitDAO; //권한 DAO
    private SupportDAO mSupportDAO; //지원 사업 DAO
    private SearchWordDAO mSearchWordDAO; //최근 검색어 DAO
    private LiveData<List<SupportModel>> allSupportItem; //지원 사업 리스트
    private LiveData<List<SupportModel>> allLikedItem; //관심 사업 리스트
    private LiveData<PermitModel> allPermit; //permit 모두 출력
    private LiveData<List<SearchWordModel>> allSearchItem;//검색어 리스트
    private LiveData<SearchWordModel> searchItem;//검색어 아이템


    public AppViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        mSupportDAO = db.supportDAO(); //db에 있는 supportDAO 가져오기
        allSupportItem = mSupportDAO.getAll(); //지원 사업 아이템 전부 가져오기
        allLikedItem = mSupportDAO.getLikedList(); //관심 사업 아이템 전부 가져오기

        mPermitDAO = db.permitDAO();//db에 있는 permitDAO 가져오기
        allPermit = mPermitDAO.getAll(); //알림 설정 상태 가져오기

        mSearchWordDAO = db.searchWordDAO();
        allSearchItem = mSearchWordDAO.getAll(); //검색어 리스트 가져오기
    }

    //관심사업 목록 출력
    public LiveData<List<SupportModel>> getAllLikedItem() {
        return allLikedItem;
    }
    //관심사업 설정
    public void setLikedItem(boolean check,String id){
        Runnable run = () -> mSupportDAO.updateLike(check,id);

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(run);
    }

    //지원사업 리스트 모든 리스트 출력
    public LiveData<List<SupportModel>> getAllSupportItem(){
        return allSupportItem;
    }
    //지원사업 아이템 추가
    public void insertSupportItem(SupportModel support){
        Runnable run = () -> mSupportDAO.insert(support);

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(run);
    }

    //permit 모두 출력
    public LiveData<PermitModel> getAllPermit() {return allPermit;}
    //동기화 시간 설정
    public void setSyncTime(String time){
        Runnable run = () -> mPermitDAO.setSyncTime(time);

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(run);
    }

    //모든 검색어 리스트 출력
    public LiveData<List<SearchWordModel>> getAllSearchItem() { return allSearchItem;}
    //검색어 아이템 하나만 출력
    public LiveData<SearchWordModel> getSearchItem(int id){
        return searchItem = mSearchWordDAO.getItem(id);
    }
    //검색어 리스트 입력
    public void insertSearchItem(SearchWordModel word){
        Runnable run = () -> mSearchWordDAO.insert(word);

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(run);

    }
    //검색어 리스트 모두 삭제
    public void deleteSearchAll(){
        Runnable run = () -> mSearchWordDAO.deleteAll();

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(run);
    }
    //검색어 아이템 삭제
    public void deleteSearchItem(int id){
        Runnable run = () -> mSearchWordDAO.deleteItem(id);

        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(run);
    }

}
