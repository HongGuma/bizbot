package com.bizbot.bizbot.Support;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;

import android.os.Handler;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String TAG = "CategoryAdapter";
    private String[] area = {"전체","서울","부산","대구","인천","광주","대전","울산","세종","경기","강원","충북","충남","전북","전남","경북","경남","제주"};
    private String[] field = {"전체","금융","기술","인력","수출","내수","창업","경영","제도","동반성장"};
    private String[] arr;
    private String item = "";
    private int index = -1; //버튼 위치
    public Handler adapterHandler;

    public CategoryAdapter(int type){
        if(type == 1){
            arr = area;
        }else if(type == 2){
            arr = field;
        }
    }

    @Override
    public int getItemCount() {
        return arr.length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button itemName;

        ViewHolder(View view){
            super(view);
            itemName = (Button) view.findViewById(R.id.item_btn);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.itemName.setText(arr[position]);
        holder.itemName.setOnClickListener(view -> {
            index = position;
            setItem(position);
            notifyDataSetChanged();
        });

        if(index == position){
            holder.itemName.setSelected(true);
        }else{
            holder.itemName.setSelected(false);
        }
    }

    //클릭한 아이템 handler로 값 전달
    public void setItem(int position){
        Message message = new Message();
        item = arr[position];
        message.obj = item;
        adapterHandler.sendMessage(message);
    }

    public String getItem(){
        return item;
    }


}



