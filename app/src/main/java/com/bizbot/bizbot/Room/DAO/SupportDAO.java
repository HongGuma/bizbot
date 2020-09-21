package com.bizbot.bizbot.Room.DAO;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bizbot.bizbot.Room.Entity.SupportModel;

import java.util.List;

@Dao
public interface  SupportDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //중복 id일 경우 덮어쓴다.
    void insert(SupportModel supports);

    @Delete
    void delete(SupportModel supports);

    @Update
    void update(SupportModel supports);

    //모든 데이터 출력
    @Query("SELECT * FROM Supports")
    LiveData<List<SupportModel>> getAll();

    //모든 데이터 삭제
    @Query("DELETE FROM SUPPORTS")
    void deleteAll();

    //타이틀
    @Query("SELECT pblancNm FROM supports")
    LiveData<List<String>> getPblancNm();

    //접수 기관
    @Query("SELECT jrsdInsttNm FROM supports")
    LiveData<List<String>> getJrsdInsttNm();

    //접수 기간
    @Query("SELECT reqstBeginEndDe FROM supports")
    LiveData<List<String>> getReqstBeginEndDe();

    //디버깅용
    @Query("SELECT pblancNm FROM Supports WHERE id = :id")
    String getTest(int id);

    //갯수
    @Query("SELECT count(id) FROM SUPPORTS")
    String getCnt();


}



