package com.bizbot.bizbot.Support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.AppViewModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.SEARCH_MODE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class SupportListAdapter extends RecyclerView.Adapter<SupportListAdapter.ViewHolder> {
    private static final String TAG = "SupportLisAdapter";

    private Context context;
    private List<SupportModel> sList;
    private List<SupportModel> filterList;
    private KeywordAdapter kwAdapter;
    private String area = null;
    private String field = null;
    private FragmentActivity activity;

    public SupportListAdapter(Context context,FragmentActivity activity, String area, String field){
        this.context = context;
        this.activity = activity;
        this.area = area;
        this.field = field;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView agency;
        TextView term;
        TextView newIcon;
        ToggleButton likeBtn;
        RecyclerView keywordRV;
        ConstraintLayout layout;

        ViewHolder(View view){
            super(view);
            title = (TextView)view.findViewById(R.id.title);
            agency = (TextView)view.findViewById(R.id.agency);
            term = (TextView)view.findViewById(R.id.term);
            newIcon = (TextView)view.findViewById(R.id.new_icon);
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

        //새글 알림
        if(filterList.get(position).isCheckNew())
            holder.newIcon.setVisibility(View.VISIBLE);
        else
            holder.newIcon.setVisibility(View.GONE);

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
                AppViewModel viewModel = ViewModelProviders.of(activity).get(AppViewModel.class);
                if(holder.likeBtn.isChecked()){
                    holder.likeBtn.setBackgroundResource(R.drawable.heart);
                    Toast.makeText(context,"관심사업으로 등록되었습니다.",Toast.LENGTH_SHORT).show();
                    viewModel.setLikedItem(true,filterList.get(position).getPblancId());
                }else{
                    holder.likeBtn.setBackgroundResource(R.drawable.heart_empty);
                    Toast.makeText(context,"관심사업이 해제되었습니다.",Toast.LENGTH_SHORT).show();
                    viewModel.setLikedItem(false,filterList.get(position).getPblancId());
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
     * 카테고리 필터
     * @param area : 카테고리 엑티비티에서 선택한 지역 영역
     * @param field : 카테고리 엑티비티에서 선택한 분야 영역
     */
    public void CategoryFilter(String area, String field){
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
                    int result = item2.getCreatPnttm().compareTo(item1.getCreatPnttm());
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

        CategoryFilter(area,field);
    }

    /**
     * 검색 필터
     * @return : 검색된 데이터랑 일치하는 아이템 출력
     */
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(!charString.isEmpty()){ //입력받은게 있다면
                    ArrayList<SupportModel> filtering = new ArrayList<SupportModel>();
                    for(SupportModel item: sList){
                        //세부 내용으로 검색
                        if(item.getBsnsSumryCn().toLowerCase().contains(charString.toLowerCase()))
                            filtering.add(item); //전체 데이터 중에서 입력받은 데이터만 추가
                    }
                    filterList = filtering; //검색창에서 입력받은 아이템만 출력한다.
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            //필터링된걸로 리사이클러뷰 업데이트
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (ArrayList<SupportModel>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * 형태소 분석기 검색 필터
     * @param wordList : 형태소 분석기로 분석한 단어 리스트
     * @return 필터링된 지원사업 리스트
     */
    public boolean PosTaggingFilter(ArrayList<String> wordList, SEARCH_MODE search_type){
        boolean result = false;
        ArrayList<SupportModel> filtering = new ArrayList<SupportModel>();
        for (SupportModel item : sList) {
            for(String word: wordList) {
                //단어 사이 공백 없애기 (ex : 중소 기업 -> 중소기업)
                word = word.replace(" ","");
                //세부 내용으로 검색, 문자열에 해당 단어만 들어가도 출력
                switch (search_type){
                    case TITLE:
                        if (item.getPblancNm().contains(word)){
                            filtering.add(item); //전체 데이터 중에서 입력받은 데이터만 추가
                        }
                        break;
                    case CONTENT:
                        if (item.getBsnsSumryCn().contains(word)){
                            filtering.add(item); //전체 데이터 중에서 입력받은 데이터만 추가
                        }
                        break;
                    case AGENCY:
                        if (item.getJrsdInsttNm().contains(word)){
                            filtering.add(item); //전체 데이터 중에서 입력받은 데이터만 추가
                        }
                        break;
                }
            }
        }

        filterList = filtering; //검색창에서 입력받은 아이템만 출력한다.
        notifyDataSetChanged();
        if(filterList.size() == 0)
            result = false;
        else
            result = true;

        return result;
    }

}
