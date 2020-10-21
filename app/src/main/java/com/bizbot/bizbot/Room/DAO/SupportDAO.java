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
public interface SupportDAO {
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

    //관심사업 체크
    @Query("UPDATE Supports SET checkLike = :check WHERE pblancId = :id")
    void updateLike(boolean check,String id);

    @Query("SELECT * FROM SUPPORTS WHERE checkLike = 1")
    LiveData<List<SupportModel>> getLikedList();

    //id 출력
    @Query("SELECT pblancId FROM SupportS")
    List<String> getID();

    @Query("UPDATE Supports SET checkNew = :check WHERE pblancId = :id")
    void updateNew(boolean check,String id);

}



