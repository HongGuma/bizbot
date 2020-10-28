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
import com.bizbot.bizbot.SEARCH_MODE;
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

    SupportListAdapter resultAdapter;
    SearchAdapter searchAdapter;
    ArrayList<String> words;
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
        TextView analysisWord = (TextView)findViewById(R.id.search_analysis_word);//분석한 단어 띄우는 텍스트뷰
        TextView titleBtn = (TextView)findViewById(R.id.search_title_btn); //제목 검색 버튼
        TextView contentBtn = (TextView)findViewById(R.id.search_content_btn); //내용 검색 버튼
        TextView agencyBtn = (TextView)findViewById(R.id.search_agency_btn); //기관 검색 버튼

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
        //검색 결과 리스트
        viewModel.getAllSupportItem().observe(this, supportModels -> resultAdapter.setList(supportModels));
        //최근 검색어 리스트
        viewModel.getAllSearchItem().observe(this, searchWordModels -> searchAdapter.setList((ArrayList<SearchWordModel>)searchWordModels));

        //모두 지우기 버튼 클릭시
        searchWordClear.setOnClickListener(view -> {
            viewModel.deleteSearchAll();
            searchAdapter.notifyDataSetChanged();
            lastSearchWord.setAdapter(searchAdapter);

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
        clearBtn.setOnClickListener(view -> {
            searchEditText.setText(null);
            searchItemLayout.setVisibility(View.GONE);
        });

        //검색 버튼
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //형태소 분석기 사용한 검색 기능
                String inputText = searchEditText.getText().toString();
                SearchWordModel searchWordModel = new SearchWordModel();
                searchWordModel.setWord(inputText);
                viewModel.insertSearchItem(searchWordModel); //입력한 검색어 저장

                words = Komoran(inputText); //형태소 분석기 사용

                //분석기로 분해한 명사 출력
                String line = "";
                for(String word : words)
                    line += word + ", ";
                if(!line.equals(""))
                    line = line.substring(0,line.length()-2); //마지막 단어에 ', ' 제거
                analysisWord.setText(line);

                PrintSearchResult(SEARCH_MODE.TITLE,searchResultRecyclerView,notSearch);

                searchItemLayout.setVisibility(View.VISIBLE);

            }
        });

        //제목 검색 출력 버튼
        titleBtn.setOnClickListener(view -> {
            PrintSearchResult(SEARCH_MODE.TITLE,searchResultRecyclerView,notSearch);
        });
        //내용 검색 출력 버튼
        contentBtn.setOnClickListener(view -> {
            PrintSearchResult(SEARCH_MODE.CONTENT,searchResultRecyclerView,notSearch);
        });
        //기관 검색 출력 버튼
        agencyBtn.setOnClickListener(view -> {
            PrintSearchResult(SEARCH_MODE.AGENCY,searchResultRecyclerView,notSearch);
        });

        //뒤로가기 버튼
        closeBtn.setOnClickListener(view -> finish());

    }

    public void PrintSearchResult(SEARCH_MODE search_mode,RecyclerView searchResultRV,TextView notSearchTextView){

        boolean resultCheck = resultAdapter.PosTaggingFilter(words,search_mode);
        if(resultCheck){//일치하는 내용이 있으면
            notSearchTextView.setVisibility(View.GONE);
            searchResultRV.setVisibility(View.VISIBLE);
        }
        else{//일치하는 내용이 없으면
            notSearchTextView.setVisibility(View.VISIBLE);
            searchResultRV.setVisibility(View.GONE);
        }
        searchResultRV.scrollToPosition(0);

    }

    public ArrayList<String> Komoran(String inputStr){
        ArrayList<String> result = new ArrayList<>();

        String path = getFilesDir()+"/user.txt";
        File file = new File(path);
        //if(!file.exists())
            FileInit(file);

        try{
            long start = System.currentTimeMillis();
            Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
            komoran.setUserDic(path);
            List<Token> tokens = komoran.analyze(inputStr).getTokenList();
            for(Token token : tokens){
                if(token.getPos().equals("NNG") || token.getPos().equals("NNP") || token.getPos().equals("SL")){
                    System.out.println(token.getMorph());
                    result.add(token.getMorph());
                }
            }
            long end = System.currentTimeMillis();
            Log.d(TAG, "Komoran: time = "+(end-start)/1000.0+" s");
        }catch (Exception e){
            e.printStackTrace();
        }

        return WordProcessing(result);
    }

    //assets 있는 '사용자 사전' 기기 내부 file에 저장
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

    public ArrayList<String> WordProcessing(ArrayList<String> wordList){
        ArrayList<String> processedStr = new ArrayList<>();
        for(String word:wordList){
            if(word.equals("지원 사업") || word.equals("지원") || word.equals("사업"))
                continue;
            //단어 사이 공백 없애기 (ex : 중소 기업 -> 중소기업)
            word = word.replace(" ","");

            processedStr.add(word);

            if(word.equals("코트라")){
                processedStr.add("kotra");
                processedStr.add("KOTRA");
                processedStr.add("Kotra");
            }
            if(word.equals("알앤디")){
                processedStr.add("R&D");
                processedStr.add("r&d");
                processedStr.add("R&amp;D");
            }
            if(word.equals("R&D")){
                processedStr.add("r&d");
                processedStr.add("R&amp;D");
            }
            if ( word.equals("r&d")){
                processedStr.add("R&D");
                processedStr.add("R&amp;D");
            }
        }
        return processedStr;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
