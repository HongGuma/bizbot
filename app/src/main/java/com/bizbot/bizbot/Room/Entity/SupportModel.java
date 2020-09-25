package com.bizbot.bizbot.Room.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Supports")
public class SupportModel {

    @NonNull
    @PrimaryKey
    String pblancId;
    String industNm;
    String rceptInsttEmailAdres;
    int inqireCo;   //조회수
    String rceptEngnHmpgUrl;
    String pblancUrl;
    String jrsdInsttNm;
    String rceptEngnNm;
    String entrprsStle;
    String pldirSportRealmLclasCodeNm;
    String trgetNm;
    String rceptInsttTelno;
    String bsnsSumryCn;
    String reqstBeginEndDe;
    String areaNm;
    String pldirSportRealmMlsfcCodeNm;
    String rceptInsttChargerDeptNm;
    String rceptInsttChargerNm;
    String pblancNm;
    String creatPnttm;
    boolean checkLike;

    public String getPblancId() {
        return pblancId;
    }

    public void setPblancId(String pblancId) {
        this.pblancId = pblancId;
    }

    public String getIndustNm() {
        return industNm;
    }

    public void setIndustNm(String industNm) {
        this.industNm = industNm;
    }

    public String getRceptInsttEmailAdres() {
        return rceptInsttEmailAdres;
    }

    public void setRceptInsttEmailAdres(String rceptInsttEmailAdres) {
        this.rceptInsttEmailAdres = rceptInsttEmailAdres;
    }

    public int getInqireCo() {
        return inqireCo;
    }

    public void setInqireCo(int inqireCo) {
        this.inqireCo = inqireCo;
    }

    public String getRceptEngnHmpgUrl() {
        return rceptEngnHmpgUrl;
    }

    public void setRceptEngnHmpgUrl(String rceptEngnHmpgUrl) {
        this.rceptEngnHmpgUrl = rceptEngnHmpgUrl;
    }

    public String getPblancUrl() {
        return pblancUrl;
    }

    public void setPblancUrl(String pblancUrl) {
        this.pblancUrl = pblancUrl;
    }

    public String getJrsdInsttNm() {
        return jrsdInsttNm;
    }

    public void setJrsdInsttNm(String jrsdInsttNm) {
        this.jrsdInsttNm = jrsdInsttNm;
    }

    public String getRceptEngnNm() {
        return rceptEngnNm;
    }

    public void setRceptEngnNm(String rceptEngnNm) {
        this.rceptEngnNm = rceptEngnNm;
    }

    public String getEntrprsStle() {
        return entrprsStle;
    }

    public void setEntrprsStle(String entrprsStle) {
        this.entrprsStle = entrprsStle;
    }

    public String getPldirSportRealmLclasCodeNm() {
        return pldirSportRealmLclasCodeNm;
    }

    public void setPldirSportRealmLclasCodeNm(String pldirSportRealmLclasCodeNm) {
        this.pldirSportRealmLclasCodeNm = pldirSportRealmLclasCodeNm;
    }

    public String getTrgetNm() {
        return trgetNm;
    }

    public void setTrgetNm(String trgetNm) {
        this.trgetNm = trgetNm;
    }

    public String getRceptInsttTelno() {
        return rceptInsttTelno;
    }

    public void setRceptInsttTelno(String rceptInsttTelno) {
        this.rceptInsttTelno = rceptInsttTelno;
    }

    public String getBsnsSumryCn() {
        return bsnsSumryCn;
    }

    public void setBsnsSumryCn(String bsnsSumryCn) {
        this.bsnsSumryCn = bsnsSumryCn;
    }

    public String getReqstBeginEndDe() {
        return reqstBeginEndDe;
    }

    public void setReqstBeginEndDe(String reqstBeginEndDe) {
        this.reqstBeginEndDe = reqstBeginEndDe;
    }

    public String getAreaNm() {
        return areaNm;
    }

    public void setAreaNm(String areaNm) {
        this.areaNm = areaNm;
    }

    public String getPldirSportRealmMlsfcCodeNm() {
        return pldirSportRealmMlsfcCodeNm;
    }

    public void setPldirSportRealmMlsfcCodeNm(String pldirSportRealmMlsfcCodeNm) {
        this.pldirSportRealmMlsfcCodeNm = pldirSportRealmMlsfcCodeNm;
    }

    public String getRceptInsttChargerDeptNm() {
        return rceptInsttChargerDeptNm;
    }

    public void setRceptInsttChargerDeptNm(String rceptInsttChargerDeptNm) {
        this.rceptInsttChargerDeptNm = rceptInsttChargerDeptNm;
    }

    public String getRceptInsttChargerNm() {
        return rceptInsttChargerNm;
    }

    public void setRceptInsttChargerNm(String rceptInsttChargerNm) {
        this.rceptInsttChargerNm = rceptInsttChargerNm;
    }

    public String getPblancNm() {
        return pblancNm;
    }

    public void setPblancNm(String pblancNm) {
        this.pblancNm = pblancNm;
    }

    public String getCreatPnttm() {
        return creatPnttm;
    }

    public void setCreatPnttm(String creatPnttm) {
        this.creatPnttm = creatPnttm;
    }

    public boolean isCheckLike() {
        return checkLike;
    }

    public void setCheckLike(boolean checkLike) {
        this.checkLike = checkLike;
    }
}
