package com.bizbot.bizbot.Room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bizbot.bizbot.Room.DAO.PermitDAO;
import com.bizbot.bizbot.Room.DAO.SearchWordDAO;
import com.bizbot.bizbot.Room.DAO.SupportDAO;
import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Room.Entity.SearchWordModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;

@Database(entities = {SupportModel.class, PermitModel.class,SearchWordModel.class},version=1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SupportDAO supportDAO();
    public abstract PermitDAO permitDAO();
    public abstract SearchWordDAO searchWordDAO();
    public static AppDatabase mAppDatabase;

    public static AppDatabase getInstance(Context context){
        if(mAppDatabase==null){
            mAppDatabase = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"app_db")
                    .build();
        }
        return mAppDatabase;
    }

    public static void RemoveDatabase(){
        mAppDatabase = null;
    }

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'Search'('id' INTEGER PRIMARY KEY,'word' TEXT)");
            //database.execSQL("ALTER TABLE Search ADD COLUMN FirstAccess INTEGER NOT NULL ");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'new_Search'('Background' INTEGER PRIMARY KEY NOT NULL,'FirstAccess' INTEGER NOT NULL)");
            database.execSQL("INSERT INTO new_Search(Background,FirstAccess) SELECT Background, FirstAccess FROM Permit");
            database.execSQL("DROP TABLE Search");
            database.execSQL("ALTER TABLE new_Search RENAME TO Search");

        }
    };
}


