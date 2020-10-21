package com.bizbot.bizbot.Background;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.AppViewModel;
import com.bizbot.bizbot.Room.Entity.PermitModel;
import com.bizbot.bizbot.Room.Entity.SupportModel;
import com.bizbot.bizbot.Support.SupportActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SynchronizationData{
    private static final String TAG = "SynchronizationData";
    public static final String CHANNEL_ID = "107";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    Context context;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

    public SynchronizationData(Context context){
        this.context = context;
    }

    /**
     * 서버에서 데이터 받는 함수
     */
    public int SyncData() {
        long start=0,end=0;
        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json";
        try{
            start = System.currentTimeMillis();
            RequestURLConnection requestURLConnection = new RequestURLConnection(baseURL); //서버에 연결
            String line = "";

            line = requestURLConnection.DataLoad();

            JSONObject json = new JSONObject(line);
            JSONArray jsonArray = json.getJSONArray("jsonArray");

            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class,"app_db").build(); //db
            PermitModel permit = db.permitDAO().getAll();
            Date sync = simpleDateFormat.parse(permit.getSyncTime());

            for(int i=0; i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                SupportModel s_list = new SupportModel();

                s_list.setPblancId(jsonObject.optString("pblancId"));
                s_list.setIndustNm(jsonObject.optString("industNm"));
                s_list.setRceptInsttEmailAdres(jsonObject.optString("rceptInsttEmailAdres"));
                s_list.setInqireCo(jsonObject.optInt("inqireCo"));
                s_list.setRceptEngnHmpgUrl(jsonObject.optString("rceptEngnHmpgUrl"));
                s_list.setPblancUrl(jsonObject.optString("pblancUrl"));
                s_list.setJrsdInsttNm(jsonObject.optString("jrsdInsttNm"));
                s_list.setRceptEngnNm(jsonObject.optString("rceptEngnNm"));
                s_list.setEntrprsStle(jsonObject.optString("entrprsStle"));
                s_list.setPldirSportRealmLclasCodeNm(jsonObject.optString("pldirSportRealmLclasCodeNm"));
                s_list.setTrgetNm(jsonObject.optString("trgetNm"));
                s_list.setRceptInsttTelno(jsonObject.optString("rceptInsttTelno"));
                s_list.setBsnsSumryCn(jsonObject.optString("bsnsSumryCn"));
                s_list.setReqstBeginEndDe(jsonObject.optString("reqstBeginEndDe"));
                s_list.setAreaNm(jsonObject.optString("areaNm"));
                s_list.setPldirSportRealmMlsfcCodeNm(jsonObject.optString("pldirSportRealmMlsfcCodeNm"));
                s_list.setRceptInsttChargerDeptNm(jsonObject.optString("rceptInsttChargerDeptNm"));
                s_list.setRceptInsttChargerNm(jsonObject.optString("rceptInsttChargerNm"));
                s_list.setPblancNm(jsonObject.optString("pblancNm"));
                s_list.setCreatPnttm(jsonObject.optString("creatPnttm"));
                s_list.setCheckLike(false);

                //새로 생성된지 2일 이하면 새글
                Date create = simpleDateFormat.parse(s_list.getCreatPnttm());
                long differentTime = sync.getTime() - create.getTime();
                long differentDay = differentTime/(24*60*60*1000);
                //Log.d(TAG, "SyncData: differntDay="+differentDay);
                if(differentDay <= 2)
                    s_list.setCheckNew(true);
                else
                    s_list.setCheckNew(false);

                db.supportDAO().insert(s_list); //데이터 추가

                if(db.permitDAO().isAlertCheck() && sync.compareTo(create) < 0)
                    NotificationNewSupport(i,s_list.getCreatPnttm(),s_list.getPblancNm()); //새글 알림


            }
            end = System.currentTimeMillis();
            //Log.d(TAG, "run: supportList="+supportList.get(0).getPblancNm());

            Log.d(TAG, "data loading : "+(end-start)/1000.0+" s");

            //동기화 시간 업데이트
            Date syncDate = new Date(System.currentTimeMillis());
            String syncTime = simpleDateFormat.format(syncDate);
            db.permitDAO().setSyncTime(syncTime);

            db.close();
            return THREAD_END; //정상 종료
        }catch (JSONException | ParseException e){
            e.printStackTrace();
            return THREAD_ERROR; //에러
        }

    }

    public void NotificationNewSupport(int NOTIFICATION_ID,String id, String title){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, SupportActivity.class);
        notificationIntent.putExtra("newPostID",id); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) //상단에 작은 아이콘
                .setContentTitle("새로운 지원사업이 등록 되었습니다.") //상태바 드래그시 보이는 타이틀
                .setContentText(title) //상태바 드래그시 보이는 서브 타이틀
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)//사용자가 알림 클릭시 해당 activity 로 이동
                .setAutoCancel(true); //알림 클릭시 자동 삭제

        // OREO (API 26 이상) notification 채널
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //Oreo 이상에서 사용할 icon
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            notificationManager.createNotificationChannel(channel);
        }else
            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 사용할 icon

        notificationManager.notify(NOTIFICATION_ID, builder.build()); //알림 표시

    }

}


