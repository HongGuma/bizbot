package com.bizbot.bizbot.Room.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bizbot.bizbot.Room.Entity.SearchWordModel;

import java.util.List;

@Dao
public interface SearchWordDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchWordModel searchWordModel);

    @Query("DELETE FROM Search")
    void deleteAll();

    @Query("DELETE FROM Search Where id = :id")
    void deleteItem(int id);

    @Query("SELECT * FROM Search")
    LiveData<List<SearchWordModel>> getAll();


}
