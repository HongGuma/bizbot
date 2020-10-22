package com.bizbot.bizbot.Search;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.AppViewModel;
import com.bizbot.bizbot.Room.Entity.SearchWordModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    ArrayList<SearchWordModel> wordList = new ArrayList<>();
    Context context;
    FragmentActivity activity;
    String searchWord;
    EditText input;

    public SearchAdapter(Context context, FragmentActivity activity, EditText inputTEXT){//, ArrayList<SearchWordModel> list){
        this.context = context;
        this.activity = activity;
        this.input = inputTEXT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView lastSearchWord;
        ImageView clearBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lastSearchWord = (TextView)itemView.findViewById(R.id.search_last_word);
            clearBtn = (ImageView)itemView.findViewById(R.id.search_last_word_clear);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_last_keyword,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppViewModel viewModel = ViewModelProviders.of(activity).get(AppViewModel.class);
        holder.lastSearchWord.setText(wordList.get(position).getWord());
        holder.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.deleteSearchItem(wordList.get(position).getId());
            }
        });

        holder.lastSearchWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getSearchItem(wordList.get(position).getId()).observe(activity, new Observer<SearchWordModel>() {
                    @Override
                    public void onChanged(SearchWordModel searchWordModel) {
                        if(searchWordModel != null){
                            searchWord = searchWordModel.getWord();
                            input.setText(searchWord);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if(wordList.size()>8)
            return 8;
        else if(wordList == null)
            return 0;
        else
            return wordList.size();
    }

    public void setList(ArrayList<SearchWordModel> list){
        this.wordList = list;
        notifyDataSetChanged();
    }

}
