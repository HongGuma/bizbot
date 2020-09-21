package com.bizbot.bizbot.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;

public class AreaCategoryAdapter extends RecyclerView.Adapter<AreaCategoryAdapter.ViewHolder> {
    private String[] area = {"서울","부산","대구","인천","광주","대전","울산","세종","경기","강원","충북","충남","전북","전남","경북","경남","제주"};
    private String[] field = {"금융","기술","인력","수출","내수","창업","경영","제도","동반성장"};
    private String[] arr;
    private String item = "";
    private boolean clickChk = false;

    public AreaCategoryAdapter(int type){
        if(type == 1){
            arr = area;
        }else if(type == 2){
            arr = field;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;

        ViewHolder(View view){
            super(view);
            itemName = (TextView)view.findViewById(R.id.item_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemName.setText(arr[position]);

    }

    @Override
    public int getItemCount() {
        return arr.length;
    }

}
