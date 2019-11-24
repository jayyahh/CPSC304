package ca.ubc.cs304.controller;
import ca.ubc.cs304.database.DatabaseConnectionHandler2;
import ca.ubc.cs304.model.ReturnModel;

import java.sql.Date;
import java.sql.Time;

public class Rentals {
    private DatabaseConnectionHandler2 dbHandler = null;

    public Rentals(DatabaseConnectionHandler2 dbHandler){
        this.dbHandler = dbHandler;
    }

    // manage the customers thing..
    public void RentVehicle(int confNo, String vtname, String dLicense, Date fromDate, Time fromTime, Date toDate, Time toTime, String name, String cardName, int cardNo, Date expDate, String location)
    {
        RentModel model;
        try{
        if (confNo == 0) {
            model = dbHandler.rentAVehicleWithoutReservation(vtname, dLicense, fromDate, fromTime, toDate, toTime, name, cardName, cardNo, expDate, location);
        }
        else {
            model = dbHandler.rentAVehicleWithReservation(confNo, dLicense, cardName, cardNo, expDate);
        }
            //print out receipt
			System.out.print("Rental completed!");
			System.out.println("ConfirmationNo: " +  model.getConfNo());
			System.out.println("Rental Period: " +  model.getFromDate() + "to" + model.getToDate());
			System.out.println("Vehicle Type: " +  model.getVtName());
			System.out.println("Location: " + model.getLocation());

    } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void ReturnVehicle(int rid, Date returnDate, Time returnTime, int odometerReading, boolean isTankFull){
        ReturnModel model;
        try{
            model = dbHandler.returnVehicle(rid, returnDate,returnTime,odometerReading,isTankFull);
            //print out receipt
            System.out.print("Return completed!");
            System.out.println("Rid: " +  model.getRid());
            System.out.println("Rental Returned: " +  model.getReturnDate() + "to" + model.getReturnTime());
            System.out.println("Rental Days: " +  model.valueDetails.numDays);
            System.out.println("Total Cost: $" + model.getValue());
            System.out.println("Cost breakdown: Rate - " + )

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
