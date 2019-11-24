package ca.ubc.cs304.model;

import com.sun.jdi.Value;

import java.sql.Time;
import java.util.Date;

public class ReturnModel {
    private final int rid;
    private final Date returnDate;
    private final Time returnTime;
    private int odometer;
    private boolean fullTank;
    private double value;
    public RentalValue valueDetails;

    public ReturnModel(int rid, Date returnDate, Time returnTime, int odometer, boolean fullTank, double value){
        this.rid = rid;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
        this.odometer = odometer;
        this.fullTank = fullTank;
        this.value = value;
    }

    public int getRid() {return this.rid;}

    public Date getReturnDate(){return this.returnDate;}
    public Time getReturnTime(){return this.returnTime;}
    public int getOdometer(){return this.odometer;}

    public boolean getFullTank() {return this.fullTank;}

    public double getValue(){return this.value;}
}
