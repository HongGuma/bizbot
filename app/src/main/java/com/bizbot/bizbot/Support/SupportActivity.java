package com.bizbot.bizbot.Support;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Room.ViewModel.SupportViewModel;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity {
    private static final String TAG = "SupportActivity";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    private List<SupportModel> supportList;
    ProgressBar asyncDialog;
    Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_activity);

        //레이아웃 선언
        Button closeBtn = (Button)findViewById(R.id.close); //닫기 버튼
        ImageButton categoryBtn = (ImageButton)findViewById(R.id.category_menu_btn); //카테고리 메뉴 버튼
        RecyclerView sRecyclerView = (RecyclerView)findViewById(R.id.support_rv); //지원사업 리스트 리사이클러뷰
        Spinner sortSpinner = (Spinner)findViewById(R.id.support_spinner); //정렬 선택 버튼
        TextView SportCunt = (TextView)findViewById(R.id.support_list_count); //지원사업 건수
        TextView pop_up = (TextView)findViewById(R.id.new_pop_up); //새 게시물 팝업
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar); //로딩 아이콘

        //카테고리에서 받아온 지역과 분야 키워드
        String areaWord = getIntent().getStringExtra("areaItem");
        String fieldWord = getIntent().getStringExtra("fieldItem");

        //지원 사업 리사이클러뷰
        sRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =new LinearLayoutManager(SupportActivity.this);
        sRecyclerView.setLayoutManager(layoutManager);
        SupportListAdapter listAdapter = new SupportListAdapter(getBaseContext(),areaWord,fieldWord); //어뎁터 생성

        /*
        getDBData();//
        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){ //데이터 로드 성공
                    progressBar.setVisibility(View.GONE); //로딩바 지우기
                    listAdapter = new SupportListAdapter(getBaseContext(),supportList,areaWord,fieldWord); //어뎁터 생성
                    SportCunt.setText("총 "+ listAdapter.ItemCount() +" 건");
                }else //데이터 로드 실패
                    Toast.makeText(SupportActivity.this,"데이터를 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        };

         */

        //변화 감지해서 리스트 갱신
        SupportViewModel supportViewModel = ViewModelProviders.of(this).get(SupportViewModel.class);
        supportViewModel.getAllList().observe(SupportActivity.this, new Observer<List<SupportModel>>() {
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
                /*
                if(supportList.size() != supportModels.size()){
                    pop_up.setVisibility(View.VISIBLE);
                    pop_up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sRecyclerView.scrollToPosition(0);
                            pop_up.setVisibility(View.GONE);
                        }
                    });
                }

                 */


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
            finish();
            overridePendingTransition(0,0); //에니메이션 제거
        });

        //카테고리 메뉴 버튼 클릭시
        categoryBtn.setOnClickListener(view -> {
            startActivity(new Intent(SupportActivity.this, CategoryActivity.class));
        });

    }


    /**
     * 뒤로가기
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
