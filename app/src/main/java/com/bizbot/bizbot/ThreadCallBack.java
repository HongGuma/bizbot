package com.bizbot.bizbot;

public class ThreadCallBack {
    public enum Type {
        THREAD_END, THREAD_ERROR
    }

   public int getType(Type type){
        int num = 1;
        switch (type){
            case THREAD_END:
                num = 0;
                break;
            case THREAD_ERROR:
                num = 1;
        }

        return num;
   }

}

