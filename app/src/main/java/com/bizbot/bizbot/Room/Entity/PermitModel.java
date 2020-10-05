package com.bizbot.bizbot.Room.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Permit")
public class PermitModel {
    @PrimaryKey(autoGenerate = true)
    int id;
    boolean Alert;
    String syncTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAlert() {
        return Alert;
    }

    public void setAlert(boolean alert) {
        Alert = alert;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }
}
