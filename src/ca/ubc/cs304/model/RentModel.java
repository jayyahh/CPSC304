package ca.ubc.cs304.model;

import java.sql.Date;
import java.sql.Time;

public class RentModel {
    private final int rid;
    private final int vid;
    private final String vtName;
    private final String dlicense;
    private final Date fromDate;
    private final Time fromTime;
    private final Date toDate;
    private final Time toTime;
    private final String location;
    private final int odometer;
    private final String cardName;
    private final int cardNo;
    private final Date expDate;
    private final int confNo;

    public RentModel(int rid, int vid, String vtName, String dlicense, Date fromDate, Time fromTime, Date toDate, Time toTime, String location, int odometer, String cardName, int cardNo, Date expDate, int confNo){
        this.rid = rid;
        this.vid = vid;
        this.vtName = vtName;
        this.dlicense = dlicense;
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
        this.location = location;
        this.odometer = odometer;
        this.cardName = cardName;
        this.cardNo = cardNo;
        this.expDate = expDate;
        this.confNo = confNo;
    }

    public int getRid() {
        return rid;
    }

    public int getVid() {
        return vid;
    }

    public String getDlicense() {
        return dlicense;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Time getFromTime() {
        return fromTime;
    }

    public Date getToDate() {
        return toDate;
    }

    public Time getToTime() {
        return toTime;
    }

    public String getLocation() {
        return location;
    }

    public int getOdometer() {
        return odometer;
    }

    public String getCardName() {
        return cardName;
    }

    public int getCardNo() {
        return cardNo;
    }

    public Date getExpDate() {
        return expDate;
    }

    public int getConfNo() {
        return confNo;
    }

    public String getVtName() {
        return vtName;
    }
}
