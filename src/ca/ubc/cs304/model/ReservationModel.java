package ca.ubc.cs304.model;

import java.sql.Time;
import java.util.*;

public class ReservationModel {
    private final int confNo;
    private final String vtname;
    private final int dLicense;
    private final Date fromDate;
    private final Date toDate;
    private final Time toTime;
    private final Time fromTime;

    public ReservationModel(int confNo, String vtname, int dLicense, Date fromDate, Time fromTime, Date toDate, Time toTime) {
        this.confNo = confNo;
        this.vtname = vtname;
        this.dLicense = dLicense;
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
    }

     public int getConfNo() {
        return this.confNo;
    }
     public String getVtname() {
        return this.vtname;
     }

     public int getdLicense() {
        return this.dLicense;
     }

     public Date getFromDate() {
        return this.fromDate;
     }
     public Time getFromTime() {
        return this.fromTime;
    }

     public Date getToDate() {
        return this.toDate;
     }

     public Time getToTime() {
        return this.toTime;
     }
}
