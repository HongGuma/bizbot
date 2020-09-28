package com.bizbot.bizbot.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bizbot.bizbot.Room.Entity.PermitModel;

@Dao
public interface PermitDAO {
    @Insert
    void insert(PermitModel permit);

    @Update
    void update(PermitModel permit);

    @Delete
    void delete(PermitModel permit);

    @Query("UPDATE PERMIT SET Background = :check ")
    void checkBackGround(boolean check);

    @Query("UPDATE Permit SET FirstAccess = :check")
    void checkFirstAccess(boolean check);

}
