package com.bizbot.bizbot.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bizbot.bizbot.Room.DAO.SupportDAO;
import com.bizbot.bizbot.Room.Entity.SupportModel;



@Database(entities = {SupportModel.class},version=1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SupportDAO supportDAO();
    public static AppDatabase mAppDatabase;

    public static AppDatabase getInstance(Context context){
        if(mAppDatabase==null){
            mAppDatabase = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"app_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mAppDatabase;
    }

    public static void RemoveDatabase(){
        mAppDatabase = null;
    }

}
