package com.xl.admin.idandfr.entity;

import java.util.Map;

/**
 * Created by Administrator on 2018/9/8.
 */

public class JpushEntity {
    private String messge;
    private Map<String , String> mMap;

    public JpushEntity(String messge,Map<String , String> mMap) {
        this.messge = messge;
        this.mMap = mMap;
    }

    public JpushEntity() {
    }

    public String getMessge() {
        return messge;
    }

    public void setMessge(String messge) {
        this.messge = messge;
    }

    public Map<String, String> getmMap() {
        return mMap;
    }

    public void setmMap(Map<String, String> mMap) {
        this.mMap = mMap;
    }
}
