package ca.ubc.cs304.delegates;

import java.sql.Timestamp;
import java.util.ArrayList;

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

    public void showAvailableVehicles(String location, Timestamp fromDateTime, Timestamp toDateTimee, String carType);

    public void rentVehicle(int confNo, String vtname, String dLicense, Timestamp fromDateTime, Timestamp toDateTime, String name, String cardName, int cardNo, Timestamp expDate, String location);

    public void reserveVehicle(String carType, String dLicense, Timestamp fromDateTime, Timestamp toDateTime, String location);

    public void returnVehicle(int rid, Timestamp returnTime, int odometerReading, boolean isTankFull);

    public void generateRentalsReport(Timestamp date);

    public void generateRentalsBranchReport(Timestamp date, String branch);

    public void generateReturnsReport(Timestamp date);

    public void generateReturnsBranchReport(Timestamp date, String branch);

    public void terminalTransactionsFinished();

    public void viewAllTables();

    public void viewTable(String tableName);

    public void updateTable(String tableName, String primaryKeyColName, String primaryKey, String colName, String condition, boolean updateIntValue);

    public void deleteFromTable(String tableName, String ColName, String condition);

    public void insertIntoTable(String tableName, Object model);
}
