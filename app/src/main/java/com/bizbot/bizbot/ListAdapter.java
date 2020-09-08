package com.bizbot.bizbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<SupportDAO> sList;

    public ListAdapter(ArrayList<SupportDAO> list){
        this.sList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView agency;
        TextView term;
        ToggleButton likeBtn;

        ViewHolder(View view){
            super(view);
            title = (TextView)view.findViewById(R.id.title);
            agency = (TextView)view.findViewById(R.id.agency);
            term = (TextView)view.findViewById(R.id.term);
            likeBtn = (ToggleButton)view.findViewById(R.id.like);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_support_list,parent,false);
        ViewHolder vh =new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(sList.get(position).getPblancNm());
        holder.agency.setText(sList.get(position).getJrsdInsttNm());
        holder.term.setText(sList.get(position).getReqstBeginEndDe());
    }


    @Override
    public int getItemCount() {
        return sList.size();
    }
}
