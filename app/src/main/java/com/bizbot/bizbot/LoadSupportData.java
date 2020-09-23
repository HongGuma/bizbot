package com.bizbot.bizbot;


import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.bizbot.bizbot.Room.AppDatabase;
import com.bizbot.bizbot.Room.Entity.SupportModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LoadSupportData{
    private static final String TAG = "LoadSupportData";
    public static final int THREAD_END = 0;
    public static final int THREAD_ERROR = 1;

    String url;

    public LoadSupportData(String url){
        this.url = url;
    }

    /**
     * 서버에서 데이터 받는 함수
     */
    public int LoadData(Context context) {
        long start=0,end=0;
        try{
            start = System.currentTimeMillis();
            RequestURLConnection requestURLConnection = new RequestURLConnection(url); //서버에 연결
            String line = "";

            /*
            InputStream is = getAssets().open("data.json");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);

            line = br.readLine();
            */

            line = requestURLConnection.DataLoad();

            JSONObject json = new JSONObject(line);
            JSONArray jsonArray = json.getJSONArray("jsonArray");

            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class,"app_db").build(); //db
           // db.supportDAO().deleteAll();

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
                s_list.setBsnsSumryCn(jsonObject.optString("bsnsSumryCn0px"));
                s_list.setReqstBeginEndDe(jsonObject.optString("reqstBeginEndDe"));
                s_list.setAreaNm(jsonObject.optString("areaNm"));
                s_list.setPldirSportRealmMlsfcCodeNm(jsonObject.optString("pldirSportRealmMlsfcCodeNm"));
                s_list.setRceptInsttChargerDeptNm(jsonObject.optString("rceptInsttChargerDeptNm"));
                s_list.setRceptInsttChargerNm(jsonObject.optString("rceptInsttChargerNm"));
                s_list.setPblancNm(jsonObject.optString("pblancNm"));
                s_list.setCreatPnttm(jsonObject.optString("creatPnttm"));

                db.supportDAO().insert(s_list); //데이터 추가
            }
            end = System.currentTimeMillis();
            //Log.d(TAG, "run: supportList="+supportList.get(0).getPblancNm());

            Log.d(TAG, "data loading : "+(end-start)/1000.0+" s");

            return THREAD_END; //정상 종료
        }catch (JSONException e){
            e.printStackTrace();
            return THREAD_ERROR; //에러
        }

    }

}
