import java.sql.Time;
import java.util.*;

public class ReservationModel {
    private final int confNo;
    private final String vtname;
    private final int dLicense;
    private final Date fromDate;
    private final Date toDate;
    private final Time toTime;

    public ReservationModel(int confNo, String vtname, int dLicense, Date fromDate, Date toDate, Time toTime) {
        this.confNo = confNo;
        this.vtname = vtname;
        this.dLicense = dLicense;
        this.fromDate = fromDate;
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

     public Date getToDate() {
        return this.toDate;
     }

     public Time getToTime() {
        return this.toTime;
     }
}
