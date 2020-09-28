package com.bizbot.bizbot.Room.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Permit")
public class PermitModel {

    @NonNull
    @PrimaryKey
    boolean Alert;

    public boolean isAlert() {
        return Alert;
    }

    public void setAlert(boolean alert) {
        Alert = alert;
    }
}
