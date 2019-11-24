package ca.ubc.cs304.controller;
import ca.ubc.cs304.database.DatabaseConnectionHandler2;
import ca.ubc.cs304.model.RentModel;
import ca.ubc.cs304.model.ReservationModel;
import ca.ubc.cs304.model.ReturnModel;
import ca.ubc.cs304.model.VehicleModel;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			System.out.println("Location: " + model.getLocation());

    } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void ReserveVehicle(String vtname, String dLicense, Date fromDate, Time fromTime, Date toDate, Time toTime, String location){
        ReservationModel model;
        try {
            if (!dbHandler.checkCustomer(dLicense)){
                //use system to get customer info
                //call create customer on this
            }

            if(dbHandler.getAvailableCarInfo(location,fromDate,toDate,vtname).length == 0){
                System.out.print("No cars available for the selected inputs! Please try again");
                //use system to tell user to try again
            }
            model = dbHandler.makeReservation(vtname, dLicense, fromDate, fromTime, toDate, toTime, location);
            System.out.print("Reservation completed!");
            System.out.println("ConfirmationNo: " +  model.getConfNo());

        }catch(SQLException e){
            System.out.println("Reservation failed:" + e.getMessage());
        }
    }

    public void ReturnVehicle(int rid, Date returnDate, Time returnTime, int odometerReading, boolean isTankFull){
        ReturnModel model;
        try{

            ResultSet rs = dbHandler.checkValidRentalId(rid);
            //while loop?
            if (!rs.next()){
                System.out.println("Error - Invalid confirmation number, please retry");
                //retry, break out of thing
            }

            model = dbHandler.returnVehicle(rid, returnDate,returnTime,odometerReading,isTankFull,rs);
            //print out receipt
            System.out.println("Return completed!");
            System.out.println("Rid: " +  model.getRid());
            System.out.println("Rental Returned: " +  model.getReturnTime() + model.getReturnDate());
            System.out.println("Rental Days: " +  model.valueDetails.numDays);
            System.out.println("Total Cost: $" + model.getValue());
            System.out.println("Cost breakdown: ");
            System.out.println("Rental Rate - " + model.valueDetails.rateValue);
            System.out.println("Insurance Rate - " + model.valueDetails.insuranceValue);
            System.out.println("Mileage Rate - " + model.valueDetails.kmValue);
            System.out.println("Gas Rate -" + model.valueDetails.tankRate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void ViewAvailableVehicles(String location, Date fromDate, Date toDate, String vtName){
        VehicleModel[] result = dbHandler.getAvailableCarInfo(location, fromDate, toDate, vtName);
        if (result.length == 0){
            System.out.println("No available vehicles for selected dates!");
        }else{
            System.out.println("Number of available vehicles: " + result.length);

            for (int i = 0; i < result.length; i++) {
                VehicleModel model = result[i];

                // simplified output formatting; truncation may occur
                System.out.printf("%-10.10s", model.getVid());
                System.out.printf("%-20.20s", model.getVLicense());
                System.out.printf("%-15.15s", model.getMake());
                System.out.printf("%-15.15s", model.getModel());
                System.out.printf("%-15.15s", model.getYear());
                System.out.printf("%-15.15s", model.getColor());
                System.out.printf("%-15.15s", model.getOdometer());
                System.out.printf("%-15.15s", model.getStatus());
                System.out.printf("%-15.15s", model.getVtname());
                System.out.printf("%-15.15s", model.getLocation());
                System.out.printf("%-15.15s", model.getCity());
                System.out.printf("%-15.15s", model.getFuelType());


                System.out.println();
            }
        }
    }
}
