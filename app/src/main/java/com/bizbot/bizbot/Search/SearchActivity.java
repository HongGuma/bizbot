package com.bizbot.bizbot.Search;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.AppViewModel;
import com.bizbot.bizbot.Room.Entity.SearchWordModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Support.SupportListAdapter;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.snu.ids.kkma.ma.MExpression;
import org.snu.ids.kkma.ma.MorphemeAnalyzer;
import org.snu.ids.kkma.ma.Sentence;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    public static int DB_IO = 1;

    SupportListAdapter resultAdapter;
    SearchAdapter searchAdapter;
    AppDatabase db;
    Handler adapterHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        ImageView closeBtn = (ImageView)findViewById(R.id.close_btn); //닫기 버튼
        ImageView clearBtn = (ImageView)findViewById(R.id.search_clear); //입력한 검색어 삭제 버튼
        ImageView searchBtn = (ImageView)findViewById(R.id.search_button); //검색 버튼
        EditText searchEditText = (EditText)findViewById(R.id.search_edit_bar); //검색어 입력창
        LinearLayout searchItemLayout = (LinearLayout)findViewById(R.id.search_result_layout); // 찾은 아이템 레이아웃
        RecyclerView searchResultRecyclerView = (RecyclerView)findViewById(R.id.search_result_rv); // 찾은 아이템 리사이클러뷰
        RecyclerView lastSearchWord = (RecyclerView)findViewById(R.id.last_search_word);//최근 검색어 리사이클러뷰
        TextView notSearch = (TextView)findViewById(R.id.search_result_null); //일치하는 아이템이 없을때 안내 텍스트
        TextView searchWordClear = (TextView)findViewById(R.id.all_clear);//최근 검색어 모두 지우기 버튼
        TextView analysisWord = (TextView)findViewById(R.id.search_analysis_word);

        lastSearchWord.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        lastSearchWord.setLayoutManager(layoutManager);

        searchResultRecyclerView.setHasFixedSize(true);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new SupportListAdapter(getBaseContext(),this,null,null);
        searchResultRecyclerView.setAdapter(resultAdapter);
        searchAdapter = new SearchAdapter(getBaseContext(),this,searchEditText);
        lastSearchWord.setAdapter(searchAdapter);

        AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        viewModel.getAllSupportItem().observe(this, new Observer<List<SupportModel>>() {
            @Override
            public void onChanged(List<SupportModel> supportModels) {
                resultAdapter.setList(supportModels);
            }
        });
        viewModel.getAllSearchItem().observe(this, new Observer<List<SearchWordModel>>() {
            @Override
            public void onChanged(List<SearchWordModel> searchWordModels) {
                searchAdapter.setList((ArrayList<SearchWordModel>)searchWordModels);
            }
        });

        //모두 지우기 버튼 클릭시
        searchWordClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.deleteSearchAll();
                searchAdapter.notifyDataSetChanged();
                lastSearchWord.setAdapter(searchAdapter);

            }
        });

        //입력 텍스트 감지
        searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String inputText = charSequence.toString();
                    if(inputText.length()<1){
                        searchItemLayout.setVisibility(View.GONE);
                        clearBtn.setVisibility(View.INVISIBLE);
                    }else{
                        clearBtn.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        //입력 텍스트 지우기 버튼
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText(null);
                searchItemLayout.setVisibility(View.GONE);
            }
        });

        //검색 버튼
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 기본 검색 기능
                String inputText = searchEditText.getText().toString();
                if(inputText.length()<1){
                    searchItemLayout.setVisibility(View.GONE);
                }else{
                    resultAdapter.getFilter().filter(inputText);

                    searchResultRecyclerView.setAdapter(resultAdapter);
                    searchItemLayout.setVisibility(View.VISIBLE);
                    notSearch.setVisibility(View.GONE);
                    SearchWordModel searchWordModel = new SearchWordModel();
                    searchWordModel.setWord(inputText);
                    viewModel.insertSearchItem(searchWordModel);
                    searchAdapter.notifyDataSetChanged();

                }

                 */

                //형태소 분석기 사용한 검색 기능
                String inputText = searchEditText.getText().toString();
                SearchWordModel searchWordModel = new SearchWordModel();
                searchWordModel.setWord(inputText);
                viewModel.insertSearchItem(searchWordModel); //입력한 검색어 저장

                ArrayList<String> str = Komoran(inputText); //형태소 분석기 사용
                String line = "";
                for(String word : str)
                    line += word + ", ";
                analysisWord.setText(line);

                if(resultAdapter.PosTaggingFilter(str)){//일치하는 내용이 있으면
                    notSearch.setVisibility(View.GONE);
                }
                else{//일치하는 내용이 없으면
                    notSearch.setVisibility(View.VISIBLE);
                    searchResultRecyclerView.setVisibility(View.GONE);
                }

                searchItemLayout.setVisibility(View.VISIBLE);

            }
        });

        //뒤로가기 버튼
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*
        TextView komoranBtn = (TextView)findViewById(R.id.komoran);
        komoranBtn.setOnClickListener(view -> {
            String inputText = searchEditText.getText().toString();
            SearchWordModel searchWordModel = new SearchWordModel();
            searchWordModel.setWord(inputText);
            viewModel.insertSearchItem(searchWordModel);

            ArrayList<String> str = Komoran(inputText);
            if(resultAdapter.PosTaggingFilter(str)){
                notSearch.setVisibility(View.GONE);
            }
            else{
                notSearch.setVisibility(View.VISIBLE);
                searchResultRecyclerView.setVisibility(View.GONE);
            }

            searchItemLayout.setVisibility(View.VISIBLE);


        });

        //디버깅용 나중에 삭제
        TextView kkmaBtn = (TextView)findViewById(R.id.kkma);
        kkmaBtn.setOnClickListener(view -> {
            String path = getFilesDir()+"/user.txt";
            File file = new File(path);
            FileInit(file);
            //if(!file.exists()){ }
        });

         */

    }

    public ArrayList<String> Komoran(String inputStr){
        ArrayList<String> result = new ArrayList<>();

        String path = getFilesDir()+"/user.txt";
        File file = new File(path);
        FileInit(file);
        //if(!file.exists())

        try{
            long start = System.currentTimeMillis();
            Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
            komoran.setUserDic(path);
            List<Token> tokens = komoran.analyze(inputStr).getTokenList();
            for(Token token : tokens){
                //System.out.println(token);
                if(token.getPos().equals("NNG") || token.getPos().equals("NNP")){
                    System.out.println(token.getMorph());
                    result.add(token.getMorph());
                }
            }
            long end = System.currentTimeMillis();
            Log.d(TAG, "Komoran: time = "+(end-start)/1000.0+" s");
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    //assets 있는 사용자 사전 기기 내부 file에 저장
    public void FileInit(File file){
        try{
            String line ="";
            String str = null;
            InputStream is = getAssets().open("user.txt");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            FileWriter fw = new FileWriter(file);
            while((str  = br.readLine())!=null){
                line += str + '\n';
            }

            fw.write(line);

            if(fw != null)
                fw.close();

            Log.d(TAG, "FileInit complete");
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "FileInit error");
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
