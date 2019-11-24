package ca.ubc.cs304.model;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class RentModel {
    private final int rid;
    private final int vid;
    private final String dLicense;
    private final Timestamp fromDate;
    private final Timestamp toDate;
    private final String location;
    private final int odometer;
    private final String cardName;
    private final int cardNo;
    private final Timestamp expDate;
    private final int confNo;

    public RentModel(int rid, int vid, String dLicense, Timestamp fromDate, Timestamp toDate, String location, int odometer, String cardName, int cardNo, Timestamp expDate, int confNo){
        this.rid = rid;
        this.vid = vid;
        this.dLicense = dLicense;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

    public String getdLicense() {
        return dLicense;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }


    public Timestamp getToDate() {
        return toDate;
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

    public Timestamp getExpDate() {
        return expDate;
    }

    public int getConfNo() {
        return confNo;
    }
}
