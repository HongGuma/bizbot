package com.bizbot.bizbot.Room.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Supports")
public class SupportModel {

    @PrimaryKey(autoGenerate = true)
    int id;
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
    String pblancId;

    public void setId(int id) {
        this.id = id;
    }

    public void setPblancId(String pblancId) {
        this.pblancId = pblancId;
    }

    public void setIndustNm(String industNm) {
        this.industNm = industNm;
    }

    public void setRceptInsttEmailAdres(String rceptInsttEmailAdres) {
        this.rceptInsttEmailAdres = rceptInsttEmailAdres;
    }

    public void setInqireCo(int inqireCo) {
        this.inqireCo = inqireCo;
    }

    public void setRceptEngnHmpgUrl(String rceptEngnHmpgUrl) {
        this.rceptEngnHmpgUrl = rceptEngnHmpgUrl;
    }

    public void setPblancUrl(String pblancUrl) {
        this.pblancUrl = pblancUrl;
    }

    public void setJrsdInsttNm(String jrsdInsttNm) {
        this.jrsdInsttNm = jrsdInsttNm;
    }

    public void setRceptEngnNm(String rceptEngnNm) {
        this.rceptEngnNm = rceptEngnNm;
    }

    public void setEntrprsStle(String entrprsStle) {
        this.entrprsStle = entrprsStle;
    }

    public void setPldirSportRealmLclasCodeNm(String pldirSportRealmLclasCodeNm) {
        this.pldirSportRealmLclasCodeNm = pldirSportRealmLclasCodeNm;
    }

    public void setTrgetNm(String trgetNm) {
        this.trgetNm = trgetNm;
    }

    public void setRceptInsttTelno(String rceptInsttTelno) {
        this.rceptInsttTelno = rceptInsttTelno;
    }

    public void setBsnsSumryCn(String bsnsSumryCn) {
        this.bsnsSumryCn = bsnsSumryCn;
    }

    public void setReqstBeginEndDe(String reqstBeginEndDe) {
        this.reqstBeginEndDe = reqstBeginEndDe;
    }

    public void setAreaNm(String areaNm) {
        this.areaNm = areaNm;
    }

    public void setPldirSportRealmMlsfcCodeNm(String pldirSportRealmMlsfcCodeNm) {
        this.pldirSportRealmMlsfcCodeNm = pldirSportRealmMlsfcCodeNm;
    }

    public void setRceptInsttChargerDeptNm(String rceptInsttChargerDeptNm) {
        this.rceptInsttChargerDeptNm = rceptInsttChargerDeptNm;
    }

    public void setRceptInsttChargerNm(String rceptInsttChargerNm) {
        this.rceptInsttChargerNm = rceptInsttChargerNm;
    }

    public void setPblancNm(String pblancNm) {
        this.pblancNm = pblancNm;
    }

    public void setCreatPnttm(String creatPnttm) {
        this.creatPnttm = creatPnttm;
    }

    public int getId() {
        return id;
    }

    public String getIndustNm() {
        return industNm;
    }

    public String getRceptInsttEmailAdres() {
        return rceptInsttEmailAdres;
    }

    public int getInqireCo() {
        return inqireCo;
    }

    public String getRceptEngnHmpgUrl() {
        return rceptEngnHmpgUrl;
    }

    public String getPblancUrl() {
        return pblancUrl;
    }

    public String getJrsdInsttNm() {
        return jrsdInsttNm;
    }

    public String getRceptEngnNm() {
        return rceptEngnNm;
    }

    public String getEntrprsStle() {
        return entrprsStle;
    }

    public String getPldirSportRealmLclasCodeNm() {
        return pldirSportRealmLclasCodeNm;
    }

    public String getTrgetNm() {
        return trgetNm;
    }

    public String getRceptInsttTelno() {
        return rceptInsttTelno;
    }

    public String getBsnsSumryCn() {
        return bsnsSumryCn;
    }

    public String getReqstBeginEndDe() {
        return reqstBeginEndDe;
    }

    public String getAreaNm() {
        return areaNm;
    }

    public String getPldirSportRealmMlsfcCodeNm() {
        return pldirSportRealmMlsfcCodeNm;
    }

    public String getRceptInsttChargerDeptNm() {
        return rceptInsttChargerDeptNm;
    }

    public String getRceptInsttChargerNm() {
        return rceptInsttChargerNm;
    }

    public String getPblancNm() {
        return pblancNm;
    }

    public String getCreatPnttm() {
        return creatPnttm;
    }

    public String getPblancId() {
        return pblancId;
    }
}
