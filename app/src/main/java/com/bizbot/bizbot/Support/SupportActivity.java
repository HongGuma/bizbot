package com.bizbot.bizbot.Support;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.Home.MainActivity;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.AppViewModel;
import com.bizbot.bizbot.Search.SearchActivity;
import com.bizbot.bizbot.Search.SearchAdapter;

import java.util.List;

public class SupportActivity extends AppCompatActivity {
    private static final String TAG = "SupportActivity";

    private List<SupportModel> supportList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_activity);

        //레이아웃 선언
        Button closeBtn = (Button)findViewById(R.id.close); //닫기 버튼
        ImageButton categoryBtn = (ImageButton)findViewById(R.id.category_menu_btn); //카테고리 메뉴 버튼
        RecyclerView sRecyclerView = (RecyclerView)findViewById(R.id.support_rv); //지원사업 리스트 리사이클러뷰
        Spinner sortSpinner = (Spinner)findViewById(R.id.support_spinner); //정렬 선택 버튼
        TextView areaState = (TextView)findViewById(R.id.area_state);//선택한 지역 표시
        TextView SportCunt = (TextView)findViewById(R.id.support_list_count); //지원사업 건수
        TextView pop_up = (TextView)findViewById(R.id.new_pop_up); //새 게시물 팝업
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar); //로딩 아이콘
        LinearLayout searchBar = (LinearLayout)findViewById(R.id.search_bar); //검색 창

        //카테고리에서 받아온 지역과 분야 키워드
        String areaWord = getIntent().getStringExtra("areaItem");
        String fieldWord = getIntent().getStringExtra("fieldItem");
        if(areaWord == null)
            areaState.setText("전체");
        else
            areaState.setText(areaWord);

        //지원 사업 리사이클러뷰
        sRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =new LinearLayoutManager(SupportActivity.this);
        sRecyclerView.setLayoutManager(layoutManager);
        SupportListAdapter listAdapter = new SupportListAdapter(getBaseContext(),this, areaWord,fieldWord); //어뎁터 생성

        //변화 감지해서 리스트 갱신
        AppViewModel appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        appViewModel.getAllSupportItem().observe(SupportActivity.this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                //Log.d(TAG, "onChanged: supportModels.size()="+supportModels.size());
                if(supportModels != null){
                    progressBar.setVisibility(View.GONE); //로딩바 지우기
                    supportList = supportModels;
                    listAdapter.setList(supportList);
                    sRecyclerView.setAdapter(listAdapter); //어뎁터 데이터 갱신

                    SportCunt.setText("총 "+ listAdapter.getItemCount() +" 건");

                }



            }
        });

        //리스트 정렬
        ArrayAdapter spinAdapter = ArrayAdapter.createFromResource(getBaseContext(),R.array.sort_mode,R.layout.spinner_dropdown);
        sortSpinner.setAdapter(spinAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(supportList != null) {
                    listAdapter.ListSort(i);
                    sRecyclerView.scrollToPosition(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        //닫기 버튼 클릭시
        closeBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        //카테고리 메뉴 버튼 클릭시
        categoryBtn.setOnClickListener(view -> {
            startActivity(new Intent(SupportActivity.this, CategoryActivity.class));
            finish();
        });

        //검색바 클릭시
        searchBar.setOnClickListener(view ->{
            startActivity(new Intent(SupportActivity.this, SearchActivity.class));
            finish();
        });

    }


    /**
     * 뒤로가기
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
