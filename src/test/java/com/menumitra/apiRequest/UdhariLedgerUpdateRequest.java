package com.menumitra.apiRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UdhariLedgerUpdateRequest {
    @JsonProperty("user_id")
    private int userid;
    
    @JsonProperty("ledger_id")
    private int ledgerid;
    
    @JsonProperty("settle_amount")
    private double settleamount;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getLedgerid() {
        return ledgerid;
    }

    public void setLedgerid(int ledgerid) {
        this.ledgerid = ledgerid;
    }

    public double getSettleamount() {
        return settleamount;
    }

    public void setSettleamount(double settleamount) {
        this.settleamount = settleamount;
    }
}
