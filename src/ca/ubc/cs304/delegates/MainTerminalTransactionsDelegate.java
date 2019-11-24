package ca.ubc.cs304.delegates;

import ca.ubc.cs304.model.VehicleModel;

import java.sql.Date;
import java.sql.Time;

/**
 * This interface uses the delegation design pattern where instead of having
 * the TerminalTransactions class try to do everything, it will only
 * focus on handling the UI. The actual logic/operation will be delegated to the
 * controller class (in this case Bank).
 *
 * TerminalTransactions calls the methods that we have listed below but
 * Bank is the actual class that will implement the methods.
 */
public interface MainTerminalTransactionsDelegate {
    public void showAvailableVehicles(String location, Date startDate, Date endDate, String carType);
    public void rentVehicle(int confNo, String vtname, String dLicense, Date fromDate, Time fromTime, Date toDate, Time toTime, String name, String cardName, int cardNo, Date expDate, String location);
    public void reserveVehicle(String carType, String dLicense, Date startDate, Time startTime, Date endDate, Time endTime, String location);
    public void returnVehicle(int rid, Date returnDate, Time returnTime, int odometerReading, boolean isTankFull);
    public void generateRentalsReport(Date date);
    public void generateRentalsBranchReport(Date date, String branch);
    public void generateReturnsReport(Date date);
    public void generateReturnsBranchReport(Date date, String branch);

    public void terminalTransactionsFinished();
}
