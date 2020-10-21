package com.bizbot.bizbot.Support;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.AppViewModel;

import java.util.List;

public class FavouriteActivity extends AppCompatActivity {
    private static final String TAG = "FavouriteActivity";

    List<String> favoriteID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_activity);

        Button close = (Button)findViewById(R.id.close);
        RecyclerView fRecyclerView = (RecyclerView)findViewById(R.id.favourite_rv);

        fRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavouriteActivity.this);
        fRecyclerView.setLayoutManager(layoutManager);

        SupportListAdapter likeAdapter = new SupportListAdapter(getBaseContext(),this,null,null);

        AppViewModel appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        appViewModel.getAllLikedItem().observe(FavouriteActivity.this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                likeAdapter.setList(supportModels);
                fRecyclerView.setAdapter(likeAdapter);
            }
        });



        //닫기 버튼 클릭시
        close.setOnClickListener(view -> finish());

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
