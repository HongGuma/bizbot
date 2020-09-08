package com.bizbot.bizbot;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestHttpConnection {
    public String request(String url, ContentValues params){

        HttpURLConnection urlConn = null;
        StringBuffer sbParams = new StringBuffer();

        /**
         * 데이터 가져오기
         */
        try{
            URL url1 = new URL(url); //url 가져오기
            urlConn = (HttpURLConnection) url1.openConnection(); //url 연결

            //urlConn 설정
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset","UTF-8");
            urlConn.setRequestProperty("Context_Type","application/x-www-form-urlencoded;cahrset=UTF-8");

            //파라미터 전달 및 데이터 읽어오기
            String strParams = sbParams.toString(); //
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();

            //url 연결 확인 실패시 null 리턴후 종료
            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));

            String line;
            String page = "";

            while ((line = reader.readLine()) != null){
                page += line;
            }

            JSONObject json = new JSONObject(page);
            JSONArray jarr = json.getJSONArray("support_list");

            for (int i=0; i<jarr.length(); i++){

                //i번째 배열 할당
                json = jarr.getJSONObject(i);

                //
                String industNm = json.getString("industNm");
                String jrsdInsttNm = json.getString("jrsdInsttNm");
                System.out.println("industNm:"+industNm+"/jrsdInsttNm:"+jrsdInsttNm);



            }

            return page;

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(urlConn != null){
                urlConn.disconnect(); //연결 종료
            }
        }

        return null;
    }
}
