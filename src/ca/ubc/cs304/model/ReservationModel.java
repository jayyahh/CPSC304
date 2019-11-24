package ca.ubc.cs304.model;

import java.sql.Time;
import java.sql.Date;
import java.util.*;

public class ReservationModel {
    private final int confNo;
    private final String vtname;
    private final String dLicense;
    private final Date fromDate;
    private final Date toDate;
    private final Time toTime;
    private final Time fromTime;

    private final String location;

    public ReservationModel(int confNo, String vtname, String dLicense, Date fromDate, Time fromTime, Date toDate,
                            Time toTime, String location) {
        this.confNo = confNo;
        this.vtname = vtname;
        this.dLicense = dLicense;
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
        this.location = location;
    }

    public int getConfNo() {
        return this.confNo;
    }

    public String getVtname() {
        return this.vtname;
    }

    public String getdLicense() {
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

    public String getLocation() {return this.location; }
}
