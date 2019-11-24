package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.MainTerminalTransactionsDelegate;
import ca.ubc.cs304.model.*;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.SuperRentTerminalTransactions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * This is the main controller class that will orchestrate everything.
 */
public class SuperRent implements LoginWindowDelegate, MainTerminalTransactionsDelegate {
    private DatabaseConnectionHandler dbHandler = null;
    private LoginWindow loginWindow = null;
    private static SuperRentTerminalTransactions transaction = null;

    public SuperRent() {
        dbHandler = new DatabaseConnectionHandler();
        transaction = new SuperRentTerminalTransactions();
    }

    private void start() {
        loginWindow = new LoginWindow();
        loginWindow.showFrame(this);
    }

    /**
     * LoginWindowDelegate Implementation
     *
     * connects to Oracle database with supplied username and password
     */
    public void login(String username, String password) {
        boolean didConnect = dbHandler.login(username, password);

        if (!didConnect) {
            // Once connected, remove login window and start text transaction flow
            loginWindow.dispose();

            transaction.showMainMenu(this);
        } else {
            loginWindow.handleLoginFailed();

            if (loginWindow.hasReachedMaxLoginAttempts()) {
                loginWindow.dispose();
                System.out.println("You have exceeded your number of allowed attempts");
                System.exit(-1);
            }
        }
    }

    public void showAvailableVehicles(String location, Date startDate, Date endDate, String carType) {
        VehicleModel[] result = dbHandler.getAvailableCarInfo(location, startDate, endDate, carType);
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

    public void rentVehicle(int confNo, String vtname, String dLicense, Date fromDate, Time fromTime, Date toDate, Time toTime, String name, String cardName, int cardNo, Date expDate, String location)
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

    public void reserveVehicle(String carType, String dLicense, Date startDate, Time startTime, Date endDate, Time endTime, String location) {
        ReservationModel model;
        try {
            if (!dbHandler.checkCustomer(dLicense)){
                System.out.println("For first time renters, please enter your information.");
                String license = transaction.enterAny("driver's license");
                String name = transaction.enterAny("name");
                String address = transaction.enterAny("address");
                String phone = transaction.enterAny("phone");
                dbHandler.createCustomer(license, name, address, phone);
            }

            if(dbHandler.getAvailableCarInfo(location,startDate,endDate,carType).length == 0){
                System.out.print("No cars available for the selected inputs! Please try again");
                //use system to tell user to try again
            }
            model = dbHandler.makeReservation(carType, dLicense, startDate, startTime, endDate, endTime, location);
            System.out.print("Reservation completed!");
            System.out.println("ConfirmationNo: " +  model.getConfNo());

        }catch(SQLException e){
            System.out.println("Reservation failed:" + e.getMessage());
        }
    }

    public void generateRentalsReport(Date date) {
        DailyRentalReportModel[] dailyRentalReportModels = dbHandler.generateRentalsReport(date);
        DailyRentalReportPerBranchModel[] dailyRentalReportPerBranchModels = dbHandler.generateRentalsReportPerBranch(date);
        int newRentals = dbHandler.generateTotalNewRental(date);
        for (int i = 0 ; i < dailyRentalReportModels.length ; i++) {
            System.out.println("Branch: " + dailyRentalReportModels[i].branch + ", Vehicle Yype: " + dailyRentalReportModels[i].vehicleType + ", Total of such Type: " + dailyRentalReportModels[i].typeCount);
        }
        for (int i = 0 ; i < dailyRentalReportPerBranchModels.length ; i++) {
            System.out.println("Branch: " + dailyRentalReportPerBranchModels[i].branch + ", Total Rental Count of Branch: " + dailyRentalReportPerBranchModels[i].totalRentalCount);
        }
        System.out.println("Total New Rentals on " + date + " : " + newRentals);
    }

    public void generateRentalsBranchReport(Date date, String branch) {
        DailyRentalReportModel[] dailyRentalReportModels = dbHandler.generateRentalsReportByBranch(date, branch);
        int newRentals = dbHandler.generateTotalNewRentalbyBranch(date, branch);
        int totalOfBranch = dbHandler.generateTotalRentalbyBranch(date, branch);
        for (int i = 0; i < dailyRentalReportModels.length; i++) {
            System.out.println("Branch: " + branch + ", Vehicle Type: " + dailyRentalReportModels[i].vehicleType + ", Total of such Type: " + dailyRentalReportModels[i].typeCount);
        }
        System.out.println("Total New Rentals on " + date + " at branch " + branch + " : " + newRentals);
        System.out.println("Total Rentals of Branch: " + totalOfBranch);
    }

    public void generateReturnsReport(Date date) {
        DailyReturnReportModel[] dailyReturnReportModels = dbHandler.generateReturnsReport(date);
        DailyReturnReportPerBranchModel[] dailyReturnReportPerBranchModels = dbHandler.generateReturnsReportPerBranch(date);
        int totalEarning = dbHandler.generateTotalDailyEarning(date);
        int totalReturns = dbHandler.generateTotalNewReturn(date);
        for (int i = 0; i < dailyReturnReportModels.length; i++) {
            System.out.println("Branch: " + dailyReturnReportModels[i].branch + ", Vehicle Type: " + dailyReturnReportModels[i].vehicleType + ", Total of such Type: " + dailyReturnReportModels[i].typeCount + ", Total Earning from such Type: " + dailyReturnReportModels[i].value);
        }
        for (int i = 0; i< dailyReturnReportPerBranchModels.length; i++) {
            System.out.println("Branch: " + dailyReturnReportPerBranchModels[i].branch + ", Total Return Count of Branch: " + dailyReturnReportPerBranchModels[i].totalReturnCount + ", Total Earning of Branch: " + dailyReturnReportPerBranchModels[i].value);
        }
        System.out.println("Total Returns on " + date + " : " + totalReturns);
        System.out.println("Total Earning of Company on " + date + " : " + totalEarning);
    }

    public void generateReturnsBranchReport(Date date, String branch) {
        DailyReturnReportModel[] dailyReturnReportModels = dbHandler.generateReturnsReportByBranch(date, branch);
        int totalReturnsOfBranch = dbHandler.generateTotalReturnByBranch(date, branch);
        int totalEarningOfBranch = dbHandler.generateTotalDailyEarningByBranch(date, branch);
        for (int i = 0; i< dailyReturnReportModels.length; i++) {
            System.out.println("Branch: " + branch + ", Vehicle Type: " + dailyReturnReportModels[i].vehicleType + ", Total of such Type: " + dailyReturnReportModels[i].typeCount + ", Total Earning from such Type: " + dailyReturnReportModels[i].value);
        }
        System.out.println("Total Returns on " + date + " of branch " + branch + " : " + totalReturnsOfBranch);
        System.out.println("Total Earning of branch " + branch + " on " + date + " : " + totalEarningOfBranch);
    }

    public void returnVehicle(int rid, Date returnDate, Time returnTime, int odometerReading, boolean isTankFull){
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

    /**
     * TerminalTransactionsDelegate Implementation
     *
     * The TerminalTransaction instance tells us that it is done with what it's
     * doing so we are cleaning up the connection since it's no longer needed.
     */
    public void terminalTransactionsFinished() {
        dbHandler.close();
        dbHandler = null;

        System.exit(0);
    }

    /**
     * Main method called at launch time
     */
    public static void main(String args[]) {
        SuperRent superRent = new SuperRent();
        superRent.start();
    }
}
