package ca.ubc.cs304.model;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

public class ReservationModel {
    private final int confNo;
    private final String vtname;
    private final String dLicense;
    private final Timestamp fromDate;
    private final Timestamp toDate;
    private final String location;

    public ReservationModel(int confNo, String vtname, String dLicense, Timestamp fromDate, Timestamp toDate, String location) {
        this.confNo = confNo;
        this.vtname = vtname;
        this.dLicense = dLicense;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

    public Timestamp getFromDate() {
        return this.fromDate;
    }

    public Timestamp getToDate() {
        return this.toDate;
    }

    public String getLocation() {return this.location; }

    public String getCityFromLocation(String locn) {
        switch(locn) {
            case "SunnyCoast":
                return "Whistler";
            case "Bridgeport":
                return "Richmond";
            case "Bay":
                return "Seattle";
            default:
                return "Vancouver";
        }
    }
}