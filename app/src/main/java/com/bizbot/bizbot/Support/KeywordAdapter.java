package com.bizbot.bizbot.Support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.ViewHolder> {
    private static final String TAG = "KeywordAdapter";

    String wordArr[];

    public KeywordAdapter(String[] arr){
        this.wordArr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword,parent,false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return wordArr.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView word;

        ViewHolder(View view){
            super(view);
            word = (TextView)view.findViewById(R.id.item_name);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.word.setText(wordArr[position]);
    }


}
