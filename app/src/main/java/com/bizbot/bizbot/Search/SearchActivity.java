package com.bizbot.bizbot.Search;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bizbot.bizbot.R;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
