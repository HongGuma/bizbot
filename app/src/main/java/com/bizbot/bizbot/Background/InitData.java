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
import java.util.Locale;


public class InitData {
    private static final String TAG = "LoadSupportData";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    Context context;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

    public InitData(Context context){
        this.context = context;
    }


    /**
     * 서버에서 데이터 받는 함수
     */
    public int LoadData() {
        long start=0,end=0;
        String baseURL = "http://www.bizinfo.go.kr/uss/rss/bizPersonaRss.do?dataType=json";
        try{
            start = System.currentTimeMillis();
            RequestURLConnection requestURLConnection = new RequestURLConnection(baseURL); //서버에 연결
            String line = "";

            /* 디버깅용 : json파일 읽어오기
            InputStream is = getAssets().open("data.json");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);

            line = br.readLine();
            */

            line = requestURLConnection.DataLoad();

            JSONObject json = new JSONObject(line);
            JSONArray jsonArray = json.getJSONArray("jsonArray");

            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class,"app_db").build(); //db
            PermitModel permit = db.permitDAO().getItem();
            Date sync = simpleDateFormat.parse(permit.getSyncTime());


            for(int i=0; i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                SupportModel supportList = JsonParsing_support(jsonObject);

                //새글 체크
                Date create = simpleDateFormat.parse(supportList.getCreatPnttm());
                long differentTime = sync.getTime() - create.getTime();
                long differentDay = differentTime/(24*60*60*1000);

                if(differentDay <= 2)
                    supportList.setCheckNew(true);
                else
                    supportList.setCheckNew(false);

                db.supportDAO().insert(supportList); //데이터 추가
            }
            end = System.currentTimeMillis();

            Log.d(TAG, "data loading : "+(end-start)/1000.0+" s");

            db.close();
            return THREAD_END; //정상 종료
        }catch (JSONException | ParseException e){
            e.printStackTrace();
            return THREAD_ERROR; //에러
        }



    }

    public SupportModel JsonParsing_support(JSONObject jsonObject) throws JSONException {
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

            return s_list;
    }

}


