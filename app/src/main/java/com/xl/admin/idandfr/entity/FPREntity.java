package com.xl.admin.idandfr.entity;

/**
 * Created by Administrator on 2018/9/9.
 */

public class FPREntity {

    private byte[] fprInfo;
    private boolean isOK;


    public byte[] getFprInfo() {
        return fprInfo;
    }

    public void setFprInfo(byte[] fprInfo) {
        this.fprInfo = fprInfo;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }
}
