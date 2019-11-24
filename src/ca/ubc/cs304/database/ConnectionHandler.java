package ca.ubc.cs304.database;

import ca.ubc.cs304.model.*;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.util.ArrayList;

public class ConnectionHandler {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
    private static final String EXCEPTION_TAG = "[EXCEPTION] ";
    private static final String WARNING_TAG = "[WARNING] ";
    private Connection connection = null;

    public ConnectionHandler() {
        try {
            DriverManager.registerDriver(new OracleDriver());
        } catch (SQLException var2) {
            System.out.println(EXCEPTION_TAG + var2.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + e.getMessage());
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
                    ps = connection.prepareStatement("INSERT INTO reservation VALUES (?,?,?,?,?,?,?,?)");
                    ps.setInt(1, reservation.getConfNo());
                    ps.setString(2, reservation.getVtname());
                    ps.setString(3, reservation.getdLicense());
                    ps.setDate(4, reservation.getFromDate());
                    ps.setTime(5, reservation.getFromTime());
                    ps.setDate(6, reservation.getToDate());
                    ps.setTime(7, reservation.getToTime());
                    ps.setString(8, reservation.getLocation());


                    ps.executeUpdate();
                    connection.commit();

                    ps.close();
                    break;
                case "Vehicle":
                    VehicleModel vehicle = (VehicleModel) Class.forName("VehicleModel").cast(o);
                    ps = connection.prepareStatement("INSERT INTO Vehicle VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
                    ps.setInt(1, vehicle.getVid());
                    ps.setString(2, vehicle.getVLicense());
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

    public Object[] viewTable(String tableName) {
        ArrayList<Object> result = new ArrayList<Object>();
        String query = "SELECT * FROM " + tableName;

        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                switch (tableName) {
                    case "Customer":
                        CustomerModel c = new CustomerModel(
                                rs.getString("cellphone"),
                                rs.getString("name"),
                                rs.getString("address"),
                                rs.getString("dLicense"));
                        result.add(c);
                        break;
                    case "Vehicle":
                        VehicleModel v = new VehicleModel(
                                rs.getInt("vid"), rs.getString("vLicense"),
                                rs.getString("make"),
                                rs.getString("model"),
                                rs.getString("year"),
                                rs.getString("color"),
                                rs.getInt("odometer"),
                                rs.getString("status"),
                                rs.getString("vt"),
                                rs.getString("locn"),
                                rs.getString("city"),
                                rs.getString("fuelType"));
                        result.add(v);
                        break;
                    case "VehicleType":
                        VehicleTypeModel vt = new VehicleTypeModel(
                                rs.getString("vtname"),
                                rs.getString("features"),
                                rs.getDouble("wrate"),
                                rs.getDouble("drate"),
                                rs.getDouble("hrate"),
                                rs.getDouble("wirate"),
                                rs.getDouble("dirate"),
                                rs.getDouble("hirate"),
                                rs.getDouble("krate"));
                        result.add(vt);
                        break;
                    case "Reservation":
                        ReservationModel res = new ReservationModel(
                                rs.getInt("confNo"),
                                rs.getString("vtname"),
                                rs.getString("dLicense"),
                                rs.getDate("fromDate"),
                                rs.getTime("fromTime"),
                                rs.getDate("toDate"),
                                rs.getTime("toTime"),
                                rs.getString("location"));
                        result.add(res);
                        break;
                    case "Rent":
                        RentModel r = new RentModel(
                                rs.getInt("rid"),
                                rs.getInt("vid"),
                                rs.getString("dLicense"),
                                rs.getDate("fromDate"),
                                rs.getTime("fromTime"),
                                rs.getDate("toDate"),
                                rs.getTime("toTime"),
                                rs.getString("location"),
                                rs.getInt("odometer"),
                                rs.getString("cardname"), rs.getInt("cardNo"),
                                rs.getDate("expDate"),
                                rs.getInt("confNo"));
                        result.add(r);
                        break;
                    case "Return":
                        ReturnModel ret = new ReturnModel(
                                rs.getInt("rid"),
                                rs.getDate("returnDate"),
                                rs.getTime("returnTime"),
                                rs.getInt("odometer"),
                                rs.getBoolean("fullTank"),
                                rs.getDouble("value"));
                        result.add(ret);
                        break;
                }
            }
            rs.close();
            stmt.close();

        } catch (SQLException var5) {
            System.out.println("[EXCEPTION] " + var5.getMessage());
        }

        return (Object[]) result.toArray(new Object[result.size()]);
    }


    public ArrayList<String> viewAllTables() {
        ArrayList<String> result = new ArrayList<String>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT table_name FROM all_tables");
            while (rs.next()) {
                result.add(rs.getString("table_name"));
            }

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + e.getMessage());
        }

        return result;
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

