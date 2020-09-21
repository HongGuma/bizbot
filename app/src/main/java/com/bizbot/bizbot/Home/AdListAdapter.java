package com.bizbot.bizbot.Home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Support.KeywordAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdListAdapter extends RecyclerView.Adapter<AdListAdapter.ViewHolder> {
    private static final String TAG = "AdListAdapter";

    private List<SupportModel> sList;
    private Context context;

    public AdListAdapter(Context context){
        this.context = context;
        //this.sList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_support_list,parent,false);
        ViewHolder vh =new ViewHolder(view);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView agency;
        TextView term;
        ToggleButton likeBtn;
        RecyclerView keyWord;

        ViewHolder(View view){
            super(view);
            title = (TextView)view.findViewById(R.id.title);
            agency = (TextView)view.findViewById(R.id.agency);
            term = (TextView)view.findViewById(R.id.term);
            likeBtn = (ToggleButton)view.findViewById(R.id.like);
            keyWord = (RecyclerView)view.findViewById(R.id.keyword_rv);
        }
    }

    @Override
    public int getItemCount() {
        if(sList != null)
            return 2;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(sList == null)
            return;
        holder.title.setText(sList.get(position).getPblancNm());
        holder.agency.setText(sList.get(position).getJrsdInsttNm());
        holder.term.setText(sList.get(position).getReqstBeginEndDe());

        //키워드 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        holder.keyWord.setLayoutManager(layoutManager);
        KeywordAdapter kwAdapter = new KeywordAdapter(SlicingWord(sList,position));
        holder.keyWord.setAdapter(kwAdapter);
    }

    public String[] SlicingWord(List<SupportModel>list, int position){
        String[] arr1 = list.get(position).getPldirSportRealmLclasCodeNm().split("@");
        String[] arr2 = list.get(position).getPldirSportRealmMlsfcCodeNm().split("@");
        String[] wordList = new String[arr1.length+arr2.length];
        System.arraycopy(arr1,0,wordList,0,arr1.length);
        System.arraycopy(arr2,0,wordList,arr1.length,arr2.length);

        return wordList;
    }

    public void setList(List<SupportModel> list){
        this.sList = list;
        notifyDataSetChanged();
    }
}
