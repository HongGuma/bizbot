package com.bizbot.bizbot.Support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.Entity.SupportModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SupportListAdapter extends RecyclerView.Adapter<SupportListAdapter.ViewHolder> {
    private static final String TAG = "SupportLisAdapter";

    private Context context;
    private List<SupportModel> sList;
    private List<SupportModel> filterList;
    private KeywordAdapter kwAdapter;
    private String area = null;
    private String field = null;
    private boolean checkMark;

    public SupportListAdapter(Context context, String area, String field){
        this.context = context;
        this.area = area;
        this.field = field;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView agency;
        TextView term;
        ToggleButton likeBtn;
        RecyclerView keywordRV;
        ConstraintLayout layout;

        ViewHolder(View view){
            super(view);
            title = (TextView)view.findViewById(R.id.title);
            agency = (TextView)view.findViewById(R.id.agency);
            term = (TextView)view.findViewById(R.id.term);
            likeBtn = (ToggleButton)view.findViewById(R.id.like_btn);
            keywordRV = (RecyclerView)view.findViewById(R.id.keyword_rv);
            layout = (ConstraintLayout)view.findViewById(R.id.support_item_layout);
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

    //리사이클러뷰 아이템 갯수
    @Override
    public int getItemCount() {
        if(filterList == null)
            return 0;
        return filterList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(filterList.get(position).getPblancNm()); //지원 사업 제목
        holder.agency.setText(filterList.get(position).getJrsdInsttNm()); //접수기관명
        holder.term.setText(filterList.get(position).getReqstBeginEndDe()); //접수기간

        if(filterList.get(position).isCheckLike()) {
            holder.likeBtn.setChecked(true);
            holder.likeBtn.setBackgroundResource(R.drawable.heart);
        }else {
            holder.likeBtn.setChecked(false);
            holder.likeBtn.setBackgroundResource(R.drawable.heart_empty);
        }

        //좋아요 버튼 클릭시
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.likeBtn.isChecked()){
                    holder.likeBtn.setBackgroundResource(R.drawable.heart);
                    Toast.makeText(context,"관심사업으로 등록되었습니다.",Toast.LENGTH_SHORT).show();
                    DB_IO(true,filterList.get(position).getPblancId());
                }else{
                    holder.likeBtn.setBackgroundResource(R.drawable.heart_empty);
                    Toast.makeText(context,"관심사업이 해제되었습니다.",Toast.LENGTH_SHORT).show();
                    DB_IO(false,filterList.get(position).getPblancId());
                }
            }
        });

        //키워드 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        holder.keywordRV.setLayoutManager(layoutManager);
        kwAdapter = new KeywordAdapter(context,SlicingWord(filterList,position),field);
        holder.keywordRV.setAdapter(kwAdapter);

        //레이아웃 클릭시
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SupportModel supportModel = filterList.get(position);
                Intent intent = new Intent(view.getContext(),SupportDetail.class);
                intent.putExtra("detail",supportModel);
                intent.putExtra("areaWord",SlicingWord(filterList,position));
                intent.putExtra("fieldWord",field);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }


    /**
     * 상단에 쓰이는 단어만 추출
     * @param list : 전체 데이터
     * @param position : 아이템 위치
     * @return 키워드
     */
    public String[] SlicingWord(List<SupportModel>list, int position){
        String[] arr1 = list.get(position).getPldirSportRealmLclasCodeNm().split("@"); //분야
        String[] arr2 = list.get(position).getPldirSportRealmMlsfcCodeNm().split("@"); //내수@수출 같은거..
        String[] wordList = new String[arr1.length+arr2.length];
        System.arraycopy(arr1,0,wordList,0,arr1.length);
        System.arraycopy(arr2,0,wordList,arr1.length,arr2.length);

        return wordList;
    }

    /**
     * 아이템 필터
     * @param area : 카테고리 엑티비티에서 선택한 지역 영역
     * @param field : 카테고리 엑티비티에서 선택한 분야 영역
     */
    public void FilterItem(String area, String field){
        List<SupportModel> filtering = new ArrayList<SupportModel>();

        if(area != null && field == null){ //지역 o, 분야 x
            for(SupportModel item:sList){
                if(item.getAreaNm().contains(area))
                    filtering.add(item);
            }
        }else if(area == null && field != null){ //지역 x, 분야 o
            for(SupportModel item:sList){
                if(item.getPldirSportRealmLclasCodeNm().contains(field))
                    filtering.add(item);
            }
        }else if(area != null && field != null){ //지역 o, 분야 o
            for(SupportModel item:sList){
                if(item.getAreaNm().contains(area) && item.getPldirSportRealmLclasCodeNm().contains(field))
                    filtering.add(item);
            }
        }else{
            filtering = sList;
        }

        filterList = (List<SupportModel>) filtering;
        notifyDataSetChanged();
    }

    /**
     * 리스트에 출력하는 아이템 개수 출력
     * @return 아이템 개수
     */
    public int ItemCount(){
        return filterList.size();
    }

    /**
     * 아이템 정렬
     * @param i : spinner 에서 선택한 아이템 번호
     */
    public void ListSort(int i){
        //todo: default 값 설정, 코드 중복 개선
        switch (i) {
            case 1: //최신순 정렬
                Comparator<SupportModel> newest = (item1, item2) -> {
                    int result = item1.getCreatPnttm().compareTo(item2.getCreatPnttm());
                    return result;
                };
                Collections.sort(filterList,newest);
                notifyDataSetChanged();
                break;
            case 2: //제목순 정렬
                Comparator<SupportModel> title = (item1, item2) -> {
                    int result = item1.getPblancNm().compareTo(item2.getPblancNm());
                    return result;
                };
                Collections.sort(filterList,title);
                notifyDataSetChanged();
                break;
            case 3: //이름순 정렬
                Comparator<SupportModel> agency = (item1, item2) -> {
                    int result = item1.getJrsdInsttNm().compareTo(item2.getJrsdInsttNm());
                    return result;
                };
                Collections.sort(filterList,agency);
                notifyDataSetChanged();
                break;
            case 4: //접수 기간 마감순 정렬
                Comparator<SupportModel> term = (item1, item2) -> {
                    int result = item1.getReqstBeginEndDe().compareTo(item2.getReqstBeginEndDe());
                    return result;
                };
                Collections.sort(filterList,term);
                notifyDataSetChanged();
                break;
            default: //아무것도 선택 안했을 때
                Comparator<SupportModel> reset = (item1, item2) -> {
                    int result = item1.getPblancId().compareTo(item2.getPblancId());
                    return result;
                };
                Collections.sort(filterList,reset);
                notifyDataSetChanged();
        }
    }

    /**
     * 리스트 갱신
     * @param list : 새로운 아이템이 추가된 리스트
     */
    public void setList(List<SupportModel> list){
        this.sList = list;
        this.filterList = list;
        notifyDataSetChanged();

        FilterItem(area,field);
    }

    public void DB_IO(boolean check,String id){
        Thread thread = new Thread(()->{
            AppDatabase db = Room.databaseBuilder(context,AppDatabase.class,"app_db").build();
            db.supportDAO().updateLike(check,id);
            db.close();
        });
        thread.start();
    }
}
