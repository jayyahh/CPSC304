package ca.ubc.cs304.database;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import ca.ubc.cs304.model.*;

/**
 * This class handles all database related transactions
 */
public class DatabaseConnectionHandler {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";


	private Connection connection = null;

	public DatabaseConnectionHandler(){
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
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

	// Generate overall rentals report, group by branch and vehicle type
	public DailyRentalReportModel[] generateRentalsReport(Timestamp date) {
		ArrayList<DailyRentalReportModel> result = new ArrayList<DailyRentalReportModel>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT V.location, V.vtname, COUNT(R.rid) AS typeCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= ? AND R.toDateTime >= ? GROUP BY V.location, V.vtname");
			ps.setTimestamp(1, date);
			ps.setTimestamp(2, date);
			String sql = "SELECT V.location, V.vtname, COUNT(R.rid) AS typeCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= " + date +  "AND R.toDateTime >= " + date + "GROUP BY V.location, V.vtname";
			System.out.println("Generate rentals report query: " + sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DailyRentalReportModel report = new DailyRentalReportModel();
				report.branch = rs.getString("location");
				report.vehicleType = rs.getString("vtname");
				report.typeCount = rs.getInt("typeCount");
				result.add(report);
			}
			ps.close();
		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return result.toArray(new DailyRentalReportModel[result.size()]);
	}

	// Generate overall rentals report, group by branch
	public DailyRentalReportPerBranchModel[] generateRentalsReportPerBranch(Timestamp date) {
		ArrayList<DailyRentalReportPerBranchModel> result = new ArrayList<DailyRentalReportPerBranchModel>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT V.location, COUNT(R.rid) AS typeCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= ? AND R.toDateTime >= ? GROUP BY V.location");
			ps.setTimestamp(1, date);
			ps.setTimestamp(2, date);
			String sql = "SELECT V.location, COUNT(R.rid) AS typeCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= " + date + "AND R.toDateTime >=  " + date + "GROUP BY V.location";
			System.out.println("Generate rental report per branch ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DailyRentalReportPerBranchModel report = new DailyRentalReportPerBranchModel();
				report.branch = rs.getString("location");
				report.totalRentalCount = rs.getInt("typeCount");
				result.add(report);
			}
			ps.close();
		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return result.toArray(new DailyRentalReportPerBranchModel[result.size()]);
	}

	// Generate new rental across all branches of the day
	public int generateTotalNewRental(Timestamp date) {
		int totalNew = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(R.rid) AS totalNew FROM Rent R WHERE R.fromDateTime = ?");
			ps.setTimestamp(1, date);
			String sql = "SELECT COUNT(R.rid) AS totalNew FROM Rent R WHERE R.fromDateTime = " + date;
			System.out.println("Generate total new rentals ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				totalNew = rs.getInt("totalNew");
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return totalNew;
	}

	// Generate overall rentals report by branch, group by vehicle type
	public DailyRentalReportModel[] generateRentalsReportByBranch(Timestamp date, String branch) {
		ArrayList<DailyRentalReportModel> result = new ArrayList<DailyRentalReportModel>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT V.vtname, COUNT(R.rid) AS typeCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= ? AND R.toDateTime >= ? AND V.location = ? GROUP BY V.vtname");
			ps.setTimestamp(1, date);
			ps.setTimestamp(2, date);
			ps.setString(3, branch);
			String sql = "SELECT V.vtname, COUNT(R.rid) AS typeCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= " + date + "AND R.toDateTime >= " + date +  "AND V.location = " + branch  + "GROUP BY V.vtname";
			System.out.println("Generate rentals report by branch ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DailyRentalReportModel report = new DailyRentalReportModel();
				report.vehicleType = rs.getString("vtname");
				report.typeCount = rs.getInt("typeCount");
				result.add(report);
			}
			ps.close();
		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return result.toArray(new DailyRentalReportModel[result.size()]);
	}

	// Generate new rental of a branch of the day
	public int generateTotalNewRentalbyBranch(Timestamp date, String branch) {
		int totalNew = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(R.rid) AS totalNew FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime = ? AND V.location = ?");
			ps.setTimestamp(1, date);
			ps.setString(2, branch);
			String sql = "SELECT COUNT(R.rid) AS totalNew FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime = " + date + "AND V.location = " + branch;
			System.out.println("Generate total new rentals by branch ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				totalNew = rs.getInt("totalNew");
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return totalNew;
	}

	// Generate total rental of a branch
	public int generateTotalRentalbyBranch(Timestamp date, String branch) {
		int totalNew = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(R.rid) AS totalCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= ? AND ? <= R.toDateTime  AND V.location = ?");
			ps.setTimestamp(1, date);
			ps.setTimestamp(2, date);
			ps.setString(3, branch);
			String sql = "SELECT COUNT(R.rid) AS totalCount FROM Rent R, Vehicle V WHERE R.vid = V.vid AND R.fromDateTime <= " + date + "AND" + date + " <= R.toDateTime  AND V.location = " + branch;
			System.out.println("Generate total rentals by branch ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				totalNew = rs.getInt("totalCount");
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return totalNew;
	}

	// Generate overall returns report, group by branch and vehicle type
	public DailyReturnReportModel[] generateReturnsReport(Timestamp date) {
		ArrayList<DailyReturnReportModel> result = new ArrayList<DailyReturnReportModel>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT V.location, V.vtname, COUNT(Ret.rid) AS typeCount, SUM(Ret.value) AS totalTypeValue FROM Rent Ren, Return Ret, Vehicle V WHERE Ren.vid = V.vid AND Ren.rid = Ret.rid AND Ret.returnDateTime = ? GROUP BY V.location, V.vtname");
			ps.setTimestamp(1, date);
			String sql = "SELECT V.location, V.vtname, COUNT(Ret.rid) AS typeCount, SUM(Ret.value) AS totalTypeValue FROM Rent Ren, Return Ret, Vehicle V WHERE Ren.vid = V.vid AND Ren.rid = Ret.rid AND Ret.returnDateTime = " + date + " GROUP BY V.location, V.vtname";
			System.out.println("Generate returns report ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DailyReturnReportModel report = new DailyReturnReportModel();
				report.branch = rs.getString("location");
				report.vehicleType = rs.getString("vtname");
				report.typeCount = rs.getInt("typeCount");
				report.value = rs.getInt("totalTypeValue");
				result.add(report);
			}
			ps.close();
		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return result.toArray(new DailyReturnReportModel[result.size()]);
	}

	// Generate subtotal returns report of all branches
	public DailyReturnReportPerBranchModel[] generateReturnsReportPerBranch(Timestamp date) {
		ArrayList<DailyReturnReportPerBranchModel> result = new ArrayList<DailyReturnReportPerBranchModel>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT V.location, COUNT(Ret.rid) AS totalReturnCount, SUM(Ret.value) AS totalBranchValue FROM Rent Ren, Return Ret, Vehicle V WHERE Ren.vid = V.vid AND Ren.rid = Ret.rid AND Ret.returnDateTime = ? GROUP BY V.location");
			ps.setTimestamp(1, date);
			String sql = "SELECT V.location, COUNT(Ret.rid) AS totalReturnCount, SUM(Ret.value) AS totalBranchValue FROM Rent Ren, Return Ret, Vehicle V WHERE Ren.vid = V.vid AND Ren.rid = Ret.rid AND Ret.returnDateTime = " + date + "GROUP BY V.location";
			System.out.println("Generate returns report by branch ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DailyReturnReportPerBranchModel report = new DailyReturnReportPerBranchModel();
				report.branch = rs.getString("location");
				report.totalReturnCount = rs.getInt("totalReturnCount");
				report.value = rs.getInt("totalBranchValue");
				result.add(report);
			}
			ps.close();
		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return result.toArray(new DailyReturnReportPerBranchModel[result.size()]);
	}

	// Generate total daily earning across all branches
	public int generateTotalDailyEarning(Timestamp date){
		int totalNew = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT SUM(R.value) AS totalNew FROM Return R WHERE R.returnDateTime = ?");
			ps.setTimestamp(1, date);
			String sql = "SELECT SUM(R.value) AS totalNew FROM Return R WHERE R.returnDateTime =  " + date;
			System.out.println("Generate total daily earnings ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				totalNew = rs.getInt("totalNew");
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return totalNew;
	}

	// Generate total number of new returns of this date, across all branches
	public int generateTotalNewReturn(Timestamp date) {
		int totalNew = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(R.rid) AS totalNew FROM Return R WHERE R.returnDateTime = ?");
			ps.setTimestamp(1, date);
			String sql = "SELECT COUNT(R.rid) AS totalNew FROM Return R WHERE R.returnDateTime = " + date;
			System.out.println("Generate new returns report ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				totalNew = rs.getInt("totalNew");
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return totalNew;
	}

	// Generate overal returns report of a branch, group by date
	public DailyReturnReportModel[] generateReturnsReportByBranch(Timestamp date, String branch) {
		ArrayList<DailyReturnReportModel> result = new ArrayList<DailyReturnReportModel>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT V.vtname, COUNT(Ret.rid) AS typeCount, SUM(Ret.value) AS totalTypeValue FROM Rent Ren, Return Ret, Vehicle V WHERE Ren.vid = V.vid AND Ren.rid = Ret.rid AND Ret.returnDateTime = ? AND V.location = ? GROUP BY V.vtname");
			ps.setTimestamp(1, date);
			ps.setString(2, branch);
			String sql = "SELECT V.vtname, COUNT(Ret.rid) AS typeCount, SUM(Ret.value) AS totalTypeValue FROM Rent Ren, Return Ret, Vehicle V WHERE Ren.vid = V.vid AND Ren.rid = Ret.rid AND Ret.returnDateTime = " + date + "AND V.location =" + branch + "GROUP BY V.vtname";
			System.out.println("Generate returns report by branch ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DailyReturnReportModel report = new DailyReturnReportModel();
				report.vehicleType = rs.getString("vtname");
				report.typeCount = rs.getInt("typeCount");
				report.value = rs.getInt("totalTypeValue");
				result.add(report);
			}
			ps.close();
		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return result.toArray(new DailyReturnReportModel[result.size()]);
	}

	// Generate total returns by branch
	public int generateTotalReturnByBranch(Timestamp date, String location) {
		int totalNew = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(Ret.rid) AS totalReturnOfBranch FROM Return Ret, Rent Ren, Vehicle V WHERE Ret.returnDateTime = ? AND V.location = ? AND Ret.rid = Ren.rid AND Ren.vid = V.vid");
			ps.setTimestamp(1, date);
			ps.setString(2, location);
			String sql = "SELECT COUNT(Ret.rid) AS totalReturnOfBranch FROM Return Ret, Rent Ren, Vehicle V WHERE Ret.returnDateTime = " + date + "AND V.location = " + location + " AND Ret.rid = Ren.rid AND Ren.vid = V.vid";
			System.out.println("Generate total return by branch ran query: " + sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				totalNew = rs.getInt("totalReturnOfBranch");
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return totalNew;
	}

	// Generate total earning by a branch
	public int generateTotalDailyEarningByBranch(Timestamp date, String location) {
		int totalNew = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT SUM(Ret.value) AS totalBranchEarning FROM Return Ret, Rent Ren, Vehicle V WHERE Ret.returnDateTime = ? AND V.location = ? AND Ret.rid = Ren.rid AND Ren.vid = V.vid");
			String sql = "SELECT SUM(Ret.value) AS totalBranchEarning FROM Return Ret, Rent Ren, Vehicle V WHERE Ret.returnDateTime = " + date + "AND V.location = " + location + " AND Ret.rid = Ren.rid AND Ren.vid = V.vid";
			System.out.println("Generate total daily earning by branch ran query: " + sql);
			ps.setTimestamp(1, date);
			ps.setString(2, location);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				totalNew = rs.getInt("totalBranchEarning");
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return totalNew;
	}


	public void createCustomer(String dLicense, String name, String address, String phone) throws SQLException {
		CustomerModel model = new CustomerModel(phone,name,address, dLicense);
		insert("Customer", model);
	}

	public boolean checkCustomer(String dLicense) throws SQLException {
		try {
			PreparedStatement stmt = connection.prepareStatement("select * from customer where dLicense = ?");
			stmt.setString(1, dLicense);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				return false;
			}
			stmt.close();
			rs.close();
			return true;
		}
		catch (SQLException e) {
			rollbackConnection();
			throw new SQLException(e.getMessage());
		}
	}

	public ReservationModel makeReservation (String vtname, String dLicense, Timestamp fromDateTime,Timestamp toDateTime, String location) throws SQLException {
		try{

		int defaultConfNo= 1000000;
		Statement stmt = connection.createStatement();
		ResultSet rs2 = stmt.executeQuery("select count(distinct confNo)  AS nums from reservation");


			while(rs2.next()){
				defaultConfNo = 1000000 + rs2.getInt("nums");
			}


			ReservationModel model = new ReservationModel(defaultConfNo, vtname, dLicense, fromDateTime, toDateTime, location);


			insert("Reservation", model);

		return model;

		}catch(SQLException e) {
			throw new SQLException(e.getMessage());
			}
	}

	public RentModel rentAVehicleWithoutReservation(String vtname, String dLicense, Timestamp fromDateTime, Timestamp toDateTime, String name, String cardName, int cardNo, Timestamp expDate, String location) throws SQLException {
		try{ReservationModel model = makeReservation(vtname, dLicense, fromDateTime, toDateTime,location);
			RentModel rentModel = rentAVehicleWithReservation(model.getConfNo(), cardName, cardNo, expDate);
			return rentModel;}
		catch (SQLException e){
			throw new SQLException(e.getMessage());
		}
	}

	public RentModel rentAVehicleWithReservation(int confNo,  String cardName, int cardNo, Timestamp expDate) throws SQLException {
		try{
			PreparedStatement reso = connection.prepareStatement("select * from reservation where confNo = ?");
			reso.setInt (1, confNo);
			ResultSet rs = reso.executeQuery();

			if (!rs.next()){
				System.out.println("Error - Invalid confirmation number, please retry");
				return new RentModel(0,0,null,null,null,null,0,null,0,null,0);
			}

			String vtName = rs.getString("vtname");
			Timestamp fromDateTime = rs.getTimestamp("fromDateTime");
			Timestamp toDateTime = rs.getTimestamp("toDateTime");
			String location = rs.getString("location");
			String dlicense = rs.getString("dlicense");

			PreparedStatement car = connection.prepareStatement("select * from vehicle where location = ? and vtname = ? and status = ?");
			car.setString(1, location);
			car.setString(2, vtName);
			car.setString(3, "Available");
			ResultSet carRs = car.executeQuery();
			if(!carRs.next()){
				System.out.println("No more available cars for selected. Please retry");
				return new RentModel(0,0,null,null,null,null,0,null,0,null,0);
			}
			int vid = carRs.getInt("vid");
			int odometer = carRs.getInt("odometer");

			int defaultRid = 1;
			Statement stmt = connection.createStatement();
			ResultSet rs2 = stmt.executeQuery("select count(distinct rid)  AS rids from rent");

			while(rs2.next()){
				defaultRid = 1 + rs2.getInt("rids");
			}

			RentModel model = new RentModel(defaultRid, vid, dlicense, fromDateTime, toDateTime, location, odometer, cardName, cardNo, expDate, confNo);
			insert("Rent", model);

			PreparedStatement vtType = connection.prepareStatement("update VehicleType set numAvail = numAvail - 1 where vtname = ?");
			vtType.setString(1,vtName);
			vtType.executeUpdate();

			PreparedStatement updateCar = connection.prepareStatement("update Vehicle set status = 'Not available' where vid = ?");
			updateCar.setInt(1,vid);
			updateCar.executeUpdate();

			rs.close();
			carRs.close();
			vtType.close();
			updateCar.close();
			reso.close();

			connection.commit();

			return model;

		} catch (SQLException e) {
			rollbackConnection();
			throw new SQLException(e.getMessage());
		}
	}

	public VehicleModel[] getAvailableCarInfo(String location, Timestamp fromDate, Timestamp toDate, String vtName) throws SQLException {
		ArrayList<VehicleModel> result = new ArrayList<VehicleModel>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Vehicle WHERE status = ? and location = ? and (vtname = ? or (? IS NULL or ? = '')) order by make");
			System.out.println("View all vehicles Ran Query: SELECT * FROM Vehicle WHERE status = 'Available' and location = " + location +  "and (vtname = " + vtName +  "or (" + vtName +" IS NULL or " +vtName + "= '')) order by make ");


			ps.setString(1, "Available");
			ps.setString(2, location);
			ps.setString(3, vtName);
			ps.setString(4, vtName);
			ps.setString(5, vtName);

			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				VehicleModel model = new VehicleModel(rs.getInt("vid"),
						rs.getString("vLicense"),
						rs.getString("make"),
						rs.getString("model"),
						rs.getString("year"),
						rs.getString("color"),
						rs.getInt("odometer"),
						rs.getString("status"),
						rs.getString("vtname"),
						rs.getString("location"),
						rs.getString("city"),
						rs.getString("fuelType"));
				result.add(model);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			rollbackConnection();
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			throw new SQLException(e.getMessage());
		}

		return result.toArray(new VehicleModel[result.size()]);
	}

	public ResultSet checkValidRentalId(int rid) throws SQLException {
		try{
			PreparedStatement rental = connection.prepareStatement("select r.odometer AS odometer, r.fromDateTime AS fromDateTime, v.vtname AS vtname, r.vid AS vid   from rent R,vehicle V where rid = ? and R.vid = V.vid");
			rental.setInt (1, rid);
			ResultSet rs = rental.executeQuery();
			return rs;
		}
		catch (SQLException e) {
			rollbackConnection();
			throw new SQLException(e.getMessage());
		}
	}

	public ReturnModel returnVehicle(int rid, Timestamp returnDateTime, int odometerReading, boolean isTankFull, ResultSet rs) throws SQLException {
		try{

			Timestamp fromDateTime = rs.getTimestamp("fromDateTime");
			int beginningOdometer = rs.getInt("odometer");
			String vtName = rs.getString("vtname");
			int vid = rs.getInt("vid");
			PreparedStatement vt = connection.prepareStatement("update VehicleType set numAvail = numAvail + 1 where vtname = ?");
			vt.setString (1, vtName);
			System.out.println("Return Ran Query: update VehicleType set numAvail = numAvail + 1 where vtname = " + vtName);
			vt.executeUpdate();
			PreparedStatement updateCar = connection.prepareStatement("update Vehicle set status = 'Available' where vid = ?");
			System.out.println("Return Ran Query: update Vehicle set status = 'Available' where vid = " + vid);
			updateCar.setInt(1,vid);
			updateCar.executeUpdate();
			RentalValue value = calculateValue(fromDateTime, returnDateTime, vtName, beginningOdometer, odometerReading, isTankFull);
			ReturnModel model = new ReturnModel(rid,returnDateTime,odometerReading,isTankFull, value.totalValue );
			insert("Return",model);
			rs.close();
			vt.close();
			updateCar.close();
			connection.commit();

			model.valueDetails = value;
			return model;

		}
		catch (SQLException e) {
			rollbackConnection();
			throw new SQLException((e.getMessage()));
		}

	}

	public RentalValue calculateValue(Timestamp fromDateTime, Timestamp toDateTime, String vtname, int bOdmtr, int eOdmtr, boolean tankFull) throws SQLException {

		RentalValue value= new RentalValue();
		long days = ChronoUnit.DAYS.between(fromDateTime.toLocalDateTime().toLocalDate(),toDateTime.toLocalDateTime().toLocalDate());
		long hours = ChronoUnit.HOURS.between(fromDateTime.toLocalDateTime().toLocalTime(),toDateTime.toLocalDateTime().toLocalTime());

		try{
			PreparedStatement vt = connection.prepareStatement("select * from VehicleType where vtname = ?");
			vt.setString(1, vtname);
			ResultSet rs = vt.executeQuery();

			rs.next();
			double dayRate = rs.getDouble("drate");
			double weekRate = rs.getDouble("wrate");
			double hourRate = rs.getDouble("hrate");
			double dayInsRate = rs.getDouble("dirate");
			double weekInsRate = rs.getDouble("wirate");
			double hourInsRate = rs.getDouble("hirate");
			double krate = rs.getDouble("krate");

			double rate = 0;
			double insRate = 0;
			double kmRate = 0;
			double tankRate = 0;

			if (!tankFull){
				tankRate += 100;
			}

			while (days >= 0){
				if (days >=7){
					rate += weekRate;
					insRate += weekInsRate;
					days = days - 7;
				}
				else
				if (days >=1){
					rate += dayRate;
					insRate += dayInsRate;
					days = days - 1;

				}
				else if (days < 1){
					rate += (hours * hourRate);
					insRate += (hours * hourInsRate);
					days = days - 1;
				}
			}

			int metersAllowed = (int)days * 50;
			if (bOdmtr - eOdmtr > metersAllowed) {
				int metersOver = eOdmtr - (bOdmtr + metersAllowed);
				kmRate += (metersOver * krate);
			}

			value.insuranceValue = insRate;
			value.kmValue = kmRate;
			value.rateValue = rate;
			value.numDays = (int) days;
			value.numHours = (int) hours;
			value.totalValue = insRate + kmRate + rate + tankRate;
			value.tankRate = tankRate;

			return value;
		}
		catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public void delete(String tableName, String id, String idColName) {
		try {
			String updateString = "DELETE FROM " + tableName + " WHERE " + idColName + " = ? ";
			PreparedStatement ps = connection.prepareStatement(updateString);

			if (idColName.equals("vid") || idColName.equals("odometer") || idColName.equals("confNo") || idColName.equals("rid")) {
				int idAsInt = Integer.parseInt(id);
				ps.setInt(1, idAsInt);
			}

			else if (idColName.equals("wrate") || idColName.equals("drate") || idColName.equals("hrate") || idColName.equals("wirate") ||
					idColName.equals("dirate") || idColName.equals("hirate") || idColName.equals("krate")) {
				double idAsDouble = Double.parseDouble(id);
				ps.setDouble(1, idAsDouble);
			}
			else {
				ps.setString(1,id);
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
	public void insert(String tableName, Object o) throws SQLException {
		try {
			PreparedStatement ps;
			switch (tableName) {
				case "Customer":
					CustomerModel customer = (CustomerModel) Class.forName("ca.ubc.cs304.model.CustomerModel").cast(o);
					ps = connection.prepareStatement("INSERT INTO customer VALUES (?,?,?,?)");
					ps.setString(1, customer.getdLicense());
					ps.setString(2, customer.getName());
					ps.setString(3, customer.getAddress());
					ps.setString(4, customer.getCellPhone());

					System.out.println("Query ran: INSERT INTO customer VALUES (" +customer.getdLicense()+"," + customer.getName()+","+customer.getAddress()+","+customer.getCellPhone()+")");



					ps.executeUpdate();
					connection.commit();

					ps.close();
					break;
				case "Return":
					ReturnModel returnModel = (ReturnModel) Class.forName("ca.ubc.cs304.model.ReturnModel").cast(o);
					ps = connection.prepareStatement("INSERT INTO return VALUES (?,?,?,?,?)");
					ps.setInt(1, returnModel.getRid());
					ps.setTimestamp(2, returnModel.getReturnDate());
					ps.setInt(3, returnModel.getOdometer());
					ps.setBoolean(4, returnModel.isFullTank());
					ps.setDouble(5, returnModel.getValue());
					System.out.println("Query ran: INSERT INTO return VALUES (" +returnModel.getRid()+"," + returnModel.getReturnDate() +","+returnModel.getOdometer()+","+returnModel.isFullTank() +"," + returnModel.getValue() +")");
					ps.executeUpdate();
					connection.commit();

					ps.close();
					break;
				case "Rent":
					RentModel rent = (RentModel) Class.forName("ca.ubc.cs304.model.RentModel").cast(o);
					ps = connection.prepareStatement("INSERT INTO rent VALUES (?,?,?,?,?,?,?,?,?,?)");
					ps.setInt(1, rent.getRid());
					ps.setInt(2, rent.getVid());
					ps.setString(3, rent.getdLicense());
					ps.setTimestamp(4, rent.getFromDate());
					ps.setTimestamp(5, rent.getToDate());
					ps.setInt(6, rent.getOdometer());
					ps.setString(7, rent.getCardName());
					ps.setInt(8, rent.getCardNo());
					ps.setTimestamp(9, rent.getExpDate());
					ps.setInt(10, rent.getConfNo());
					System.out.println("Query ran: INSERT INTO rent VALUES (" + rent.getRid()+"," + rent.getVid() +","+rent.getdLicense()+","+rent.getFromDate() +"," + rent.getToDate() +","+rent.getOdometer() +"," +
							rent.getCardName() + "," + rent.getCardNo() +","+rent.getExpDate() + "," + rent.getConfNo()+")");

					ps.executeUpdate();
					connection.commit();

					ps.close();
					break;
				case "Reservation":
					ReservationModel reservation = (ReservationModel) Class.forName("ca.ubc.cs304.model.ReservationModel").cast(o);
					ps = connection.prepareStatement("INSERT INTO reservation VALUES (?,?,?,?,?,?,?)");
					ps.setInt(1, reservation.getConfNo());
					ps.setString(2, reservation.getVtname());
					ps.setString(3, reservation.getdLicense());
					ps.setTimestamp(4, reservation.getFromDate());
					ps.setTimestamp(5, reservation.getToDate());
					ps.setString(6, reservation.getLocation());
					ps.setString(7, reservation.getCityFromLocation(reservation.getLocation()));

					System.out.println("Query ran: INSERT INTO reservation VALUES (" +reservation.getConfNo()+"," + reservation.getVtname() +","+reservation.getdLicense()+","+reservation.getFromDate() +"," + reservation.getToDate() +","+reservation.getLocation() +"," + reservation.getCityFromLocation(reservation.getLocation()) + ")");
					ps.executeUpdate();
					connection.commit();

					ps.close();
					break;
				case "Vehicle":
					VehicleModel vehicle = (VehicleModel) Class.forName("ca.ubc.cs304.model.VehicleModel").cast(o);
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

					System.out.println("Query ran: INSERT INTO Vehicle VALUES (" + vehicle.getVid()+"," + vehicle.getVLicense() +","+vehicle.getMake()+","+vehicle.getModel() +"," + vehicle.getColor() +","+vehicle.getOdometer() +"," +
							vehicle.getStatus() + "," + vehicle.getVtname() +","+vehicle.getLocation() + "," +vehicle.getCity() + "," + vehicle.getFuelType()+")");
					ps.executeUpdate();
					connection.commit();

					ps.close();
					break;
				case "VehicleType":
					VehicleTypeModel vType = (VehicleTypeModel) Class.forName("ca.ubc.cs304.model.VehicleTypeModel").cast(o);
					ps = connection.prepareStatement("INSERT INTO VehicleType VALUES (?,?,?,?,?,?,?,?,?)");
					ps.setString(1, vType.getVtname());
					ps.setString(2, vType.getFeatures());
					ps.setDouble(3, vType.getWRate());
					ps.setDouble(4, vType.getDRate());
					ps.setDouble(5, vType.getHRate());
					ps.setDouble(6, vType.getDiRate());
					ps.setDouble(7, vType.getHiRate());
					ps.setDouble(8,vType.getKRate());
					ps.setDouble(9,vType.getNumAvail());

					System.out.println("Query ran: INSERT INTO VehicleType VALUES (" + vType.getVtname()+"," + vType.getFeatures() +","+vType.getWRate()+","+vType.getDRate() +"," + vType.getHRate() +","+vType.getDiRate() +"," +
							vType.getHiRate() + "," + vType.getKRate() + vType.getNumAvail()+")");

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
			throw new SQLException(e.getMessage());

		}
		catch (ClassNotFoundException e) {
			System.out.println("The object type given was not found.");
			System.out.println(e.getMessage());
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

	public Object[] viewTable(String tableName) throws SQLException {
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
								rs.getString("vtname"),
								rs.getString("location"),
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
								rs.getDouble("krate"),
								rs.getDouble("numAvail"));
						result.add(vt);
						break;
					case "Reservation":
						ReservationModel res = new ReservationModel(
								rs.getInt("confNo"),
								rs.getString("vtname"),
								rs.getString("dLicense"),
								rs.getTimestamp("fromDateTime"),
								rs.getTimestamp("toDateTime"),
								rs.getString("location"));
						result.add(res);
						break;
					case "Rent":
						RentModel r = new RentModel(
								rs.getInt("rid"),
								rs.getInt("vid"),
								rs.getString("dLicense"),
								rs.getTimestamp("fromDateTime"),
								rs.getTimestamp("toDateTime"),
								"",
								rs.getInt("odometer"),
								rs.getString("cardName"),
								rs.getInt("cardNo"),
								rs.getTimestamp("ExpDate"),
								rs.getInt("confNo"));
						result.add(r);
						break;
					case "Return":
						ReturnModel ret = new ReturnModel(
								rs.getInt("rid"),
								rs.getTimestamp("returnDateTime"),
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
			throw new SQLException(var5.getMessage());
		}

		return (Object[]) result.toArray(new Object[result.size()]);
	}

	public ArrayList<String> viewAllTables() throws SQLException {
		ArrayList<String> result = new ArrayList<String>();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT table_name FROM user_tables");
			while (rs.next()) {
				result.add(rs.getString("table_name"));
			}

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + e.getMessage());
			throw new SQLException(e.getMessage());
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
