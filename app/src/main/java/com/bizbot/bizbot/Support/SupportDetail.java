package com.bizbot.bizbot.Support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.Entity.SupportModel;

public class SupportDetail extends AppCompatActivity {
    private static final String TAG = "SupportDetail";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_details);

        ImageView closeBtn = (ImageView)findViewById(R.id.detail_close_btn);
        TextView title = (TextView)findViewById(R.id.detail_title);
        TextView contents = (TextView)findViewById(R.id.detail_contents);
        TextView area = (TextView)findViewById(R.id.detail_area);
        TextView field = (TextView)findViewById(R.id.detail_field);
        TextView term = (TextView)findViewById(R.id.detail_term);
        TextView agency = (TextView)findViewById(R.id.detail_agency);
        TextView department = (TextView)findViewById(R.id.detail_department);
        TextView telNum = (TextView)findViewById(R.id.detail_tel);
        TextView manager = (TextView)findViewById(R.id.detail_manager);
        LinearLayout homePageBtn = (LinearLayout)findViewById(R.id.detail_homepage);
        RecyclerView keywordRecyclerView = (RecyclerView)findViewById(R.id.detail_keyword_rv);

        SupportModel supportModel = (SupportModel) getIntent().getSerializableExtra("detail");
        String[] areaKeyword = getIntent().getStringArrayExtra("areaWord");
        String fieldKeyword = getIntent().getStringExtra("fieldWord");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.HORIZONTAL,false);
        keywordRecyclerView.setLayoutManager(layoutManager);
        KeywordAdapter kwAdapter = new KeywordAdapter(getBaseContext(),areaKeyword,fieldKeyword);
        keywordRecyclerView.setAdapter(kwAdapter);

        title.setText(supportModel.getPblancNm());
        SliceContents(supportModel.getBsnsSumryCn(),contents);
        SliceWord(supportModel.getAreaNm(),area);
        SliceWord(supportModel.getPldirSportRealmMlsfcCodeNm(),field);
        term.setText(supportModel.getReqstBeginEndDe());
        agency.setText(supportModel.getRceptEngnNm());
        department.setText(supportModel.getJrsdInsttNm());
        telNum.setText(supportModel.getRceptInsttTelno());
        manager.setText(supportModel.getRceptInsttChargerNm());

        //홈페이지 버튼 클릭시
        homePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(supportModel.getRceptEngnHmpgUrl()));
                startActivity(intent);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //@으로 연결된 단어 자르기
    public void SliceWord(String str, TextView textView){
        String[] arr1 = str.split("@"); //분야

        String line = "";
        for(String word : arr1)
            line += word + " ";

        textView.setText(line);

    }

    /**
     * HTML 태그 제거
     * @param str : 화면에 띄울 본문 데이터
     * @param textView : 본문 띄우는 textview
     */
    public void SliceContents(String str,TextView textView){
        String[] arr1 = str.split("<br />");
        String line1 = "";
        for(String word : arr1)
            line1 += word + "\n";

        String[] arr2 = line1.split("&nbsp;");
        String line2 = "";
        for(String word : arr2)
            line2 += word + " ";

        String[] arr3 = line2.split("<p style=\\\"margin: 0px\\\">");
        String line3 = "";
        for(String word : arr3)
            line3 += word + " ";

        String[] arr4 = line3.split("</p>");
        String line4 = "";
        for(String word: arr4)
            line4 += word + " ";

        line4 = line4.replace("R&amp;D","R&D");

        textView.setText(line4);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
