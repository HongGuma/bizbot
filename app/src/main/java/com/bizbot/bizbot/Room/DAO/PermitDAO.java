package com.bizbot.bizbot.Room.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bizbot.bizbot.Room.Entity.PermitModel;

@Dao
public interface PermitDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PermitModel permit);

    @Update
    void update(PermitModel permit);

    @Delete
    void delete(PermitModel permit);

    @Query("UPDATE Permit SET Alert = :check")
    void setAlert(boolean check);

    @Query("UPDATE Permit SET SyncTime = :time")
    void setSyncTime(String time);

    @Query("SELECT * FROM Permit WHERE id=1")
    LiveData<PermitModel> getAlertState();

    @Query("SELECT * FROM Permit WHERE id=1")
    boolean isAlertCheck();

    @Query("SELECT * FROM Permit")
    PermitModel getAll();


}
