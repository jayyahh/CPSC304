package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.MainTerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.DailyRentalReportModel;
import ca.ubc.cs304.model.DailyReturnReportModel;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.SuperRentTerminalTransactions;

import java.sql.Date;
import java.sql.Time;

/**
 * This is the main controller class that will orchestrate everything.
 */
public class SuperRent implements LoginWindowDelegate, MainTerminalTransactionsDelegate {
    private DatabaseConnectionHandler dbHandler = null;
    private LoginWindow loginWindow = null;

    public SuperRent() {
        dbHandler = new DatabaseConnectionHandler();
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

            SuperRentTerminalTransactions transaction = new SuperRentTerminalTransactions();
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

    public void showAvailableVehicles(String carType, String location, Date startDate, Time startTime, Date endDate, Time endTime) {

    }

    public void rentVehicle() {

    }

    public void returnVehicle() {

    }

    public void generateRentalsReport(Date date) {
        DailyRentalReportModel[] dailyRentalReportModels = dbHandler.generateRentalsReport(date);
        int newRentals = dbHandler.generateTotalNewRental(date);
        // need to compute total count of vehicles per branch
        // what do we do with the data?
    }

    public void generateRentalsBranchReport(Date date, String branch) {
        DailyRentalReportModel[] dailyRentalReportModels = dbHandler.generateRentalsReportByBranch(date, branch);
        int newRentals = dbHandler.generateTotalNewRentalbyBranch(date, branch);
        // need to compute total count of vehicles
        // what do we do with the data?
    }

    public void generateReturnsReport(Date date) {
        DailyReturnReportModel[] dailyReturnReportModels = dbHandler.generateReturnsReport(date);
        int totalEarning = dbHandler.generateTotalDailyEarning(date);
        int totalReturns = dbHandler.generateTotalNewReturn(date);
        // need to compute subtotals
        // what do we do with the data?
    }

    public void generateReturnsBranchReport(Date date, String branch) {
        DailyReturnReportModel[] dailyReturnReportModels = dbHandler.generateReturnsReportByBranch(date, branch);
        // need to compute subtotal
        // what do we do with the data?
    }


    /**
     * TermainalTransactionsDelegate Implementation
     *
     * Displays information about varies bank branches.
     */
    public void showBranch() {
        BranchModel[] models = dbHandler.getBranchInfo();

        for (int i = 0; i < models.length; i++) {
            BranchModel model = models[i];

            // simplified output formatting; truncation may occur
            System.out.printf("%-10.10s", model.getId());
            System.out.printf("%-20.20s", model.getName());
            if (model.getAddress() == null) {
                System.out.printf("%-20.20s", " ");
            } else {
                System.out.printf("%-20.20s", model.getAddress());
            }
            System.out.printf("%-15.15s", model.getCity());
            if (model.getPhoneNumber() == 0) {
                System.out.printf("%-15.15s", " ");
            } else {
                System.out.printf("%-15.15s", model.getPhoneNumber());
            }

            System.out.println();
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
