package ca.ubc.cs304.model;

import java.sql.Date;

public class RentModel {
    private final int rid;
    private final int vid;
    private final String dlicense;
    private final Date fromDate;
    private final Date fromTime;
    private final Date toDate;
    private final Date toTime;
    private final String location;
    private final int odometer;
    private final String cardName;
    private final int cardNo;
    private final Date expDate;
    private final int confNo;

    public RentModel(int rid, int vid, String dlicense, Date fromDate, Date fromTime, Date toDate, Date toTime, String location, int odometer, String cardName, int cardNo, Date expDate, int confNo){
        this.rid = rid;
        this.vid = vid;
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
}
