package ca.ubc.cs304.database;

import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.RentModel;
import ca.ubc.cs304.model.ReservationModel;
import ca.ubc.cs304.model.ReturnModel;
import oracle.jdbc.driver.OracleDriver;

import java.sql.*;

public class ConnectionHandler {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";
    private Connection connection = null;

    public ConnectionHandler() {
        try {
            DriverManager.registerDriver(new OracleDriver());
        } catch (SQLException var2) {
            System.out.println("[EXCEPTION] " + var2.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public void delete(String tableName, int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM ? WHERE branch_id = ?");
            ps.setInt(1, Integer.parseInt(tableName));
            ps.setInt(2, id);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + tableName + " " + id + " does not exist!");
            }
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void insert(String tableName, Object o) {

        try {
            PreparedStatement ps;
            switch (tableName) {
                case "Customer":
                    CustomerModel customer = (CustomerModel) Class.forName("CustomerModel").cast(o);
                    ps = connection.prepareStatement("INSERT INTO customer VALUES (?,?,?,?)");
                    ps.setString(1, customer.getdLicense());
                    ps.setString(2, customer.getName());
                    ps.setString(3, customer.getAddress());
                    ps.setString(4, customer.getCellPhone());

                    ps.executeUpdate();
                    connection.commit();

                    ps.close();
                    break;
                case "Return":
                    ReturnModel returnModel = (ReturnModel) Class.forName("ReturnModel").cast(o);
                    ps = connection.prepareStatement("INSERT INTO return VALUES (?,?,?,?,?,?)");
                    ps.setInt(1, returnModel.getRid());
                    ps.setDate(2, returnModel.getReturnDate());
                    ps.setTime(3, returnModel.getReturnTime());
                    ps.setInt(4, returnModel.getOdometer());
                    ps.setBoolean(5, returnModel.isFullTank());
                    ps.setDouble(6, returnModel.getValue());

                    ps.executeUpdate();
                    connection.commit();

                    ps.close();
                    break;
                case "Rent":
                    RentModel rent = (RentModel) Class.forName("RentModel").cast(o);
                    ps = connection.prepareStatement("INSERT INTO rent VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
                    ps.setInt(1, rent.getRid());
                    ps.setInt(2, rent.getVid());
                    ps.setString(3, rent.getdLicense());
                    ps.setDate(4, rent.getFromDate());
                    ps.setTime(5, rent.getFromTime());
                    ps.setDate(6, rent.getToDate());
                    ps.setTime(7, rent.getToTime());
                    ps.setInt(8, rent.getOdometer());
                    ps.setString(9, rent.getCardName());
                    ps.setInt(10, rent.getCardNo());
                    ps.setDate(11, rent.getExpDate());
                    ps.setInt(12, rent.getConfNo());

                    ps.executeUpdate();
                    connection.commit();

                    ps.close();
                    break;
                case "Reservation":
                    ReservationModel reservation = (ReservationModel) Class.forName("ReservationModel").cast(o);
                    ps = connection.prepareStatement("INSERT INTO reservation VALUES (?,?,?,?,?,?,?,?,?)");
                    ps.setInt(1, reservation.getConfNo());
                    ps.setString(2, reservation.getVtname());
                    ps.setString(3, reservation.getdLicense());
                    ps.setDate(4, reservation.getFromDate());
                    ps.setTime(5, reservation.getFromTime());
                    ps.setDate(6, reservation.getToDate());
                    ps.setTime(7, reservation.getToTime());
                    ps.setString(8, reservation.getLocn());
                    ps.setString(9, reservation.getCity());

                    ps.executeUpdate();
                    connection.commit();

                    ps.close();
                    break;
                default:
                    System.out.println("No available model type.");
            }

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
        catch (ClassNotFoundException e) {
            System.out.println("The object type you gave me wasn't found :(");
        }
    }


    public boolean login(String username, String password) {
        try {
            if (connection != null) {
                connection.close();
            }

            connection = DriverManager.getConnection(ORACLE_URL, username, password);
            connection.setAutoCommit(false);

            System.out.println("\nConnected to Oracle!");
            return true;
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return false;
        }
    }

    private void rollbackConnection() {
        try  {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }


}

