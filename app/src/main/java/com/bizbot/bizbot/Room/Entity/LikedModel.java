package com.bizbot.bizbot.Room.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "liked")
public class LikedModel {

    @NonNull
    @PrimaryKey
    String pblancId;

    @NonNull
    public String getPblancId() {
        return pblancId;
    }

    public void setPblancId(@NonNull String pblancId) {
        this.pblancId = pblancId;
    }
}
