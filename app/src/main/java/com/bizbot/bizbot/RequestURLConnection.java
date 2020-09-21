package com.bizbot.bizbot;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestURLConnection {
    private static final String TAG = "RequestURLConnection";
    private String url;
    private String line;

    public RequestURLConnection(String url){
        this.url = url;
    }

    public String DataLoad(){

        HttpURLConnection conn = null;

        try{
            URL url1 = new URL(url); //url 가져오기
            conn = (HttpURLConnection) url1.openConnection(); //url 연결

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            //Log.d(TAG, "onCreate: reader = "+reader.toString());

            String line = reader.readLine();
            //Log.d(TAG, "DataLoad: line="+line);

            return line;

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(conn != null){
                conn.disconnect(); //연결 종료
            }

        }

        return line;
    }
}
