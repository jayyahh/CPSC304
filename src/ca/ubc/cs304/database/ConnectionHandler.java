package ca.ubc.cs304.database;

import ca.ubc.cs304.model.*;
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

    public void delete(String tableName, String id, String idColName) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM ? WHERE ? = ?");
            ps.setString(1, tableName);
            ps.setString(2, idColName);

            if (tableName.equals("Customer") || tableName.equals("VehicleType")) {
                ps.setString(3, id);
            }
            else {
                int idAsInt = Integer.parseInt(id);
                ps.setInt(3, idAsInt);
            }

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
    /** Dynamically constructs an insert statement based on given object*/
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
                case "Vehicle":
                    VehicleModel vehicle = (VehicleModel) Class.forName("VehicleModel").cast(o);
                    ps = connection.prepareStatement("INSERT INTO Vehicle VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
                    ps.setInt(1, vehicle.getVid());
                    ps.setString(2, vehicle.getvLicense());
                    ps.setString(3, vehicle.getMake());
                    ps.setString(4, vehicle.getModel());
                    ps.setString(5, vehicle.getYear());
                    ps.setString(6, vehicle.getColor());
                    ps.setInt(7, vehicle.getOdometer());
                    ps.setString(8,vehicle.getStatus());
                    ps.setString(9, vehicle.getVtname());
                    ps.setString(10, vehicle.getLocation());
                    ps.setString(11, vehicle.getCity());
                    ps.setString(12, vehicle.getFuelType());

                    ps.executeUpdate();
                    connection.commit();

                    ps.close();
                    break;
                case "VehicleType":
                    VehicleTypeModel vType = (VehicleTypeModel) Class.forName("VehicleTypeModel").cast(o);
                    ps = connection.prepareStatement("INSERT INTO VehicleType VALUES (?,?,?,?,?,?,?,?)");
                    ps.setString(1, vType.getVtname());
                    ps.setString(2, vType.getFeatures());
                    ps.setDouble(3, vType.getWRate());
                    ps.setDouble(4, vType.getDRate());
                    ps.setDouble(5, vType.getHRate());
                    ps.setDouble(6, vType.getDiRate());
                    ps.setDouble(7, vType.getHiRate());
                    ps.setDouble(8,vType.getKRate());

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
            System.out.println("The object type given was not found.");
        }
    }

    /** This implementation of the update method can only update one parameter at a time */

    public void update(String tableName, String pkColName, String pk, String colName, String var, boolean updateIntValue) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("UPDATE ? SET ? = ? WHERE ? = ?");
            ps.setString(1, tableName);
            ps.setString(2, colName);
            if (tableName.equals("Customer") || tableName.equals("VehicleType")) {
                ps.setString(3, var);
            }
            else {
                int varAsInt = Integer.parseInt(var);
                ps.setInt(3, varAsInt);
            }

            ps.setString(4, pkColName);

            if (updateIntValue) {
                int pkAsInt = Integer.parseInt(pk);
                ps.setInt(5, pkAsInt);
            }
            else {
                ps.setString(5, pk);
            }

            int rowCount = ps.executeUpdate();

            if (rowCount == 0) {
                System.out.println("[WARNING] " + tableName + " " + pk + " does not exist!");
            }

            this.connection.commit();
            ps.close();
        } catch (SQLException e) {
            System.out.println("[EXCEPTION] " + e.getMessage());
            this.rollbackConnection();
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

