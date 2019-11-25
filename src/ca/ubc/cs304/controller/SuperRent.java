package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.MainTerminalTransactionsDelegate;
import ca.ubc.cs304.model.*;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.SuperRentTerminalTransactions;

import java.sql.*;
import java.util.ArrayList;

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

        if (didConnect) {
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

    public void showAvailableVehicles(String location, Timestamp startDateTime, Timestamp endDateTime, String carType) {
        try{VehicleModel[] result = dbHandler.getAvailableCarInfo(location, startDateTime, endDateTime, carType);
        if (result.length == 0){
            System.out.println("No available vehicles for selected dates!");
            transaction.showMainMenu(this);
        }else{
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
                System.out.println("Number of available vehicles: " + result.length);

                transaction.showMainMenu(this);
            }
        }}catch (SQLException e){
            System.out.println(e.getMessage());
            transaction.showMainMenu(this);
        }
    }

    public void rentAVehicleWithoutReservation(String vtname, String dLicense, Timestamp fromDateTime, Timestamp toDateTime, String name, String cardName, int cardNo, Timestamp expDate, String location)
    {
        RentModel model;
        try{

            model = dbHandler.rentAVehicleWithoutReservation(vtname, dLicense, fromDateTime,toDateTime, name, cardName, cardNo, expDate, location);
            //print out receipt
if (model.getRid() == 0) {
transaction.showMainMenu(this);
}
            System.out.print("Rental completed!");
            System.out.println("ConfirmationNo: " +  model.getConfNo());
            System.out.println("Rental Period: " +  model.getFromDate() + "to" + model.getToDate());
            System.out.println("Location: " + model.getLocation());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            transaction.showMainMenu(this);
        }
    }
    public void rentVehicleWithReservation(int confNo, String cardName, int cardNo, Timestamp expDate)
    {
        RentModel model;
        try{
            model = dbHandler.rentAVehicleWithReservation(confNo, cardName, cardNo, expDate);
            if (model.getRid() == 0) {
                transaction.showMainMenu(this);
            }
            //print out receipt
            System.out.print("Rental completed!");
            System.out.println("ConfirmationNo: " +  model.getConfNo());
            System.out.println("Rental Period: " +  model.getFromDate() + "to" + model.getToDate());
            System.out.println("Location: " + model.getLocation());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            transaction.showMainMenu(this);
        }
    }

    public void reserveVehicle(String carType, String dLicense, Timestamp startDateTime, Timestamp endDateTime, String location) {
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

            if(dbHandler.getAvailableCarInfo(location,startDateTime,endDateTime,carType).length == 0){
                System.out.print("No cars available for the selected inputs! Please try again");
                transaction.showMainMenu(this);
            }
            model = dbHandler.makeReservation(carType, dLicense, startDateTime, endDateTime, location);
            System.out.print("Reservation completed!");
            System.out.println("ConfirmationNo: " +  model.getConfNo());

        }catch(SQLException e){
            System.out.println("Reservation failed:" + e.getMessage());
            transaction.showMainMenu(this);
        }
    }

    public void generateRentalsReport(Timestamp date) {
        DailyRentalReportModel[] dailyRentalReportModels = dbHandler.generateRentalsReport(date);
        DailyRentalReportPerBranchModel[] dailyRentalReportPerBranchModels = dbHandler.generateRentalsReportPerBranch(date);
        int newRentals = dbHandler.generateTotalNewRental(date);
        System.out.println();
        System.out.printf("%-15.15s", "Branch");
        System.out.printf("%-20.20s", "Vehicle Type");
        System.out.printf("%-25.25s", "Total Rental");
        System.out.println();
        for (int i = 0 ; i < dailyRentalReportModels.length ; i++) {
            System.out.printf("%-15.15s", dailyRentalReportModels[i].branch);
            System.out.printf("%-20.20s", dailyRentalReportModels[i].vehicleType);
            System.out.printf("%-10.10s", dailyRentalReportModels[i].typeCount);
            System.out.println();
        }
        System.out.println();
        System.out.printf("%-15.15s", "Branch");
        System.out.printf("%-25.25s", "Total Rental");
        System.out.println();
        for (int i = 0 ; i < dailyRentalReportPerBranchModels.length ; i++) {
            System.out.printf("%-15.15s", dailyRentalReportPerBranchModels[i].branch);
            System.out.printf("%-25.25s", dailyRentalReportPerBranchModels[i].totalRentalCount);
            System.out.println();
        }
        System.out.println();
        System.out.println("Total New Rentals on this day: " + newRentals);
    }

    public void generateRentalsBranchReport(Timestamp date, String branch) {
        DailyRentalReportModel[] dailyRentalReportModels = dbHandler.generateRentalsReportByBranch(date, branch);
        int newRentals = dbHandler.generateTotalNewRentalbyBranch(date, branch);
        int totalOfBranch = dbHandler.generateTotalRentalbyBranch(date, branch);
        System.out.println();
        System.out.printf("%-15.15s", "Branch");
        System.out.printf("%-20.20s", "Vehicle Type");
        System.out.printf("%-25.25s", "Total Rental");
        System.out.println();
        for (int i = 0; i < dailyRentalReportModels.length; i++) {
            System.out.printf("%-15.15s", branch);
            System.out.printf("%-20.20s", dailyRentalReportModels[i].vehicleType);
            System.out.printf("%-10.10s", dailyRentalReportModels[i].typeCount);
            System.out.println();
        }
        System.out.println("Total New Rentals on this day at branch " + branch + " : " + newRentals);
        System.out.println("Total Rentals of Branch: " + totalOfBranch);
    }

    public void generateReturnsReport(Timestamp date) {
        DailyReturnReportModel[] dailyReturnReportModels = dbHandler.generateReturnsReport(date);
        DailyReturnReportPerBranchModel[] dailyReturnReportPerBranchModels = dbHandler.generateReturnsReportPerBranch(date);
        int totalEarning = dbHandler.generateTotalDailyEarning(date);
        int totalReturns = dbHandler.generateTotalNewReturn(date);
        System.out.println();
        System.out.printf("%-15.15s", "Branch");
        System.out.printf("%-20.20s", "Vehicle Type");
        System.out.printf("%-25.25s", "Total Return");
        System.out.printf("%-25.25s", "Total Earnings");
        System.out.println();
        for (int i = 0; i < dailyReturnReportModels.length; i++) {
            System.out.printf("%-15.15s", dailyReturnReportModels[i].branch);
            System.out.printf("%-20.20s", dailyReturnReportModels[i].vehicleType);
            System.out.printf("%-25.25s", dailyReturnReportModels[i].typeCount);
            System.out.printf("%-25.25s", dailyReturnReportModels[i].value);
            System.out.println();
        }
        System.out.println();
        System.out.printf("%-15.15s", "Branch");
        System.out.printf("%-25.25s", "Total Return");
        System.out.printf("%-25.25s", "Total Earnings");
        System.out.println();
        for (int i = 0; i< dailyReturnReportPerBranchModels.length; i++) {
            System.out.printf("%-15.15s", dailyReturnReportPerBranchModels[i].branch);
            System.out.printf("%-25.25s", dailyReturnReportPerBranchModels[i].totalReturnCount);
            System.out.printf("%-25.25s", dailyReturnReportPerBranchModels[i].value);
            System.out.println();
        }
        System.out.println("Total Returns on this day: " + totalReturns);
        System.out.println("Total Earning of Company on this day: " + totalEarning);
    }

    public void generateReturnsBranchReport(Timestamp date, String branch) {
        DailyReturnReportModel[] dailyReturnReportModels = dbHandler.generateReturnsReportByBranch(date, branch);
        int totalReturnsOfBranch = dbHandler.generateTotalReturnByBranch(date, branch);
        int totalEarningOfBranch = dbHandler.generateTotalDailyEarningByBranch(date, branch);
        System.out.println();
        System.out.printf("%-15.15s", "Branch");
        System.out.printf("%-20.20s", "Vehicle Type");
        System.out.printf("%-25.25s", "Total Return");
        System.out.printf("%-25.25s", "Total Earnings");
        System.out.println();
        for (int i = 0; i< dailyReturnReportModels.length; i++) {
            System.out.printf("%-15.15s", branch);
            System.out.printf("%-20.20s", dailyReturnReportModels[i].vehicleType);
            System.out.printf("%-25.25s", dailyReturnReportModels[i].typeCount);
            System.out.printf("%-25.25s", dailyReturnReportModels[i].value);
            System.out.println();
        }
        System.out.println("Total Returns on this day of branch " + branch + " : " + totalReturnsOfBranch);
        System.out.println("Total Earning of branch " + branch + " on this day: " + totalEarningOfBranch);
    }

    public void returnVehicle(int rid, Timestamp returnDateTime, int odometerReading, boolean isTankFull){
        ReturnModel model;
        try{

            ResultSet rs = dbHandler.checkValidRentalId(rid);
            if (!rs.next()){
                System.out.println("Error - Invalid confirmation number, please retry");
                transaction.showMainMenu(this);
            }

            model = dbHandler.returnVehicle(rid, returnDateTime, odometerReading,isTankFull,rs);
            //print out receipt
            System.out.println("Return completed!");
            System.out.println("Rid: " +  model.getRid());
            System.out.println("Rental Returned: " +  model.getReturnDate());
            System.out.println("Rental Days: " +  model.valueDetails.numDays);
            System.out.println("Total Cost: $" + model.getValue());
            System.out.println("Cost breakdown: ");
            System.out.println("Rental Rate - $" + model.valueDetails.rateValue);
            System.out.println("Insurance Rate - $" + model.valueDetails.insuranceValue);
            System.out.println("Mileage Rate - $" + model.valueDetails.kmValue);
            System.out.println("Gas Rate - $" + model.valueDetails.tankRate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            transaction.showMainMenu(this);
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

    public void viewAllTables() {
        ArrayList<String> tables = dbHandler.viewAllTables();
        System.out.println("List of the tables: ");
        for (int i = 0; i<tables.size();i++){
            System.out.println(tables.get(i));
        }
    }

    public void viewTable(String tableName) {
        Object[] objects = dbHandler.viewTable(tableName);
        System.out.println("Information of table " + tableName);
        for (int i =0; i<objects.length;i++) {
            if (objects[i] instanceof CustomerModel){
                displayCustomer((CustomerModel) objects[i]);
            } else if (objects[i] instanceof RentModel) {
                displayRent((RentModel) objects[i]);
            } else if (objects[i] instanceof ReservationModel) {
                displayReservation((ReservationModel) objects[i]);
            } else if (objects[i] instanceof  ReturnModel) {
                displayReturn((ReturnModel) objects[i]);
            } else if (objects[i] instanceof VehicleModel) {
                displayVehicle((VehicleModel) objects[i]);
            } else if (objects[i] instanceof VehicleTypeModel) {
                displayVehicleType((VehicleTypeModel) objects[i]);
            }
        }
    }

    private void displayCustomer(CustomerModel vtm) {
        System.out.printf("%-20.20s", vtm.getCellPhone());
        System.out.printf("%-20.20s", vtm.getName());
        System.out.printf("%-20.20s", vtm.getAddress());
        System.out.printf("%-20.20s", vtm.getdLicense());
        System.out.println();
    }

    private void displayRent(RentModel vtm) {
        System.out.printf("%-20.20s", vtm.getRid());
        System.out.printf("%-20.20s", vtm.getVid());
        System.out.printf("%-20.20s", vtm.getdLicense());
        System.out.printf("%-40.40s", vtm.getFromDate());
        System.out.printf("%-40.40s", vtm.getToDate());
        System.out.printf("%-20.20s", vtm.getLocation());
        System.out.printf("%-20.20s", vtm.getOdometer());
        System.out.printf("%-20.20s", vtm.getCardName());
        System.out.printf("%-20.20s", vtm.getCardNo());
        System.out.printf("%-40.40s", vtm.getExpDate());
        System.out.printf("%-20.20s", vtm.getConfNo());
        System.out.println();
    }

    private void displayReservation(ReservationModel vtm) {
        System.out.printf("%-20.20s", vtm.getConfNo());
        System.out.printf("%-20.20s", vtm.getVtname());
        System.out.printf("%-20.20s", vtm.getdLicense());
        System.out.printf("%-40.40s", vtm.getFromDate());
        System.out.printf("%-40.40s", vtm.getToDate());
        System.out.printf("%-20.20s", vtm.getLocation());
        System.out.println();
    }

    private void displayReturn(ReturnModel vtm) {
        System.out.printf("%-20.20s", vtm.getRid());
        System.out.printf("%-40.40s", vtm.getReturnDate());
        System.out.printf("%-20.20s", vtm.getOdometer());
        System.out.printf("%-20.20s", vtm.isFullTank());
        System.out.printf("%-20.20s", vtm.getValue());
        System.out.println();
    }

    private void displayVehicle(VehicleModel vtm) {
        System.out.printf("%-20.20s", vtm.getVid());
        System.out.printf("%-20.20s", vtm.getVLicense());
        System.out.printf("%-20.20s", vtm.getMake());
        System.out.printf("%-20.20s", vtm.getModel());
        System.out.printf("%-20.20s", vtm.getYear());
        System.out.printf("%-20.20s", vtm.getColor());
        System.out.printf("%-20.20s", vtm.getOdometer());
        System.out.printf("%-20.20s", vtm.getStatus());
        System.out.printf("%-20.20s", vtm.getVtname());
        System.out.printf("%-20.20s", vtm.getLocation());
        System.out.printf("%-20.20s", vtm.getCity());
        System.out.printf("%-20.20s", vtm.getFuelType());
        System.out.println();
    }

    private void displayVehicleType(VehicleTypeModel vtm) {
        System.out.printf("%-20.20s", vtm.getVtname());
        System.out.printf("%-20.20s", vtm.getFeatures());
        System.out.printf("%-20.20s", vtm.getWRate());
        System.out.printf("%-20.20s", vtm.getDRate());
        System.out.printf("%-20.20s", vtm.getHRate());
        System.out.printf("%-20.20s", vtm.getWiRate());
        System.out.printf("%-20.20s", vtm.getDiRate());
        System.out.printf("%-20.20s", vtm.getHiRate());
        System.out.printf("%-20.20s", vtm.getKRate());
        System.out.printf("%-20.20s", vtm.getNumAvail());
        System.out.println();
    }

    public void updateTable(String tableName, String primaryKeyColName, String primaryKey, String colName, String condition, boolean updateIntValue) {
        dbHandler.update(tableName, primaryKeyColName, primaryKey, colName, condition, updateIntValue);
        System.out.println("Done");
    }

    public void deleteFromTable(String tableName, String ColName, String condition) {
        dbHandler.delete(tableName, condition, ColName);
        System.out.println("Done");
    }

    public void insertIntoTable(String tableName, Object model) {
        dbHandler.insert(tableName, model);
        System.out.println("Done");
    }

    /**
     * Main method called at launch time
     */
    public static void main(String args[]) {
        SuperRent superRent = new SuperRent();
        superRent.start();
    }
}
