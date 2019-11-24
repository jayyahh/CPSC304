package ca.ubc.cs304.model;

import com.sun.jdi.Value;
import java.sql.Time;
import java.sql.Date;
import java.sql.Timestamp;

public class ReturnModel {
    private final int rid;
    private final Timestamp returnDate;
    private int odometer;
    private boolean fullTank;
    private double value;
    public RentalValue valueDetails;

    public ReturnModel(int rid, Timestamp returnDate, int odometer, boolean fullTank, double value){
        this.rid = rid;
        this.returnDate = returnDate;
        this.odometer = odometer;
        this.fullTank = fullTank;
        this.value = value;
    }

    public int getRid() {
        return rid;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public int getOdometer() {
        return odometer;
    }

    public boolean isFullTank() {
        return fullTank;
    }

    public double getValue() {
        return value;
    }
}
