package com.bizbot.bizbot.Search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppViewModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Support.SupportListAdapter;

import java.util.ArrayList;
import java.util.List;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private List<SupportModel> supportList; //원본 데이터
    private List<SupportModel> filterList; //검색 필터링된 데이터
    SupportListAdapter resultAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        ImageView closeBtn = (ImageView)findViewById(R.id.close_btn);
        ImageView clearBtn = (ImageView)findViewById(R.id.search_clear);
        EditText editText = (EditText)findViewById(R.id.search_edit_bar);
        LinearLayout searchItemLayout = (LinearLayout)findViewById(R.id.search_result_layout);
        TextView notSearch = (TextView)findViewById(R.id.search_result_null);
        RecyclerView searchResultRecyclerView = (RecyclerView)findViewById(R.id.search_result_rv);


        searchResultRecyclerView.setHasFixedSize(true);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new SupportListAdapter(this,null,null);
        searchResultRecyclerView.setAdapter(resultAdapter);

        AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        viewModel.getAllList().observe(this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                supportList = supportModels;
                resultAdapter.setList(supportList);
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText(null);
                searchItemLayout.setVisibility(View.GONE);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputText = charSequence.toString();
                if(inputText.length()<1){
                    searchItemLayout.setVisibility(View.GONE);
                }else{
                    resultAdapter.getFilter().filter(charSequence);
                    searchItemLayout.setVisibility(View.VISIBLE);
                    notSearch.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    public void Komoran(){
        String str1= "코모란형태소분석기테스트";
        String str2= "아버지가방에들어가신다.";

        try{
            Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
            //komoran.setUserDic("");
            List<Token> tokens = komoran.analyze(str2).getTokenList();
            for(Token token : tokens)
                System.out.println(token);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
