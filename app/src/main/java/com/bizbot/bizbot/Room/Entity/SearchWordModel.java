package com.bizbot.bizbot.Room.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search")
public class SearchWordModel {

    @PrimaryKey
    @NonNull
    String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
