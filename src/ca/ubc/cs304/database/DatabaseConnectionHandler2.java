package ca.ubc.cs304.database;

import ca.ubc.cs304.model.*;
import com.sun.jdi.Value;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This class handles all database related transactions
 */
public class DatabaseConnectionHandler2 {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	private int uniqueRId = 0;
	private int uniqueConfNo = 1000000;

	private Connection connection = null;

	public DatabaseConnectionHandler2() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public int getRId(){
		return uniqueRId++;
	}

	public int getConfNo(){
		return uniqueConfNo++;
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

	public void createCustomer(int dLicense, String name, String address, String phone){
		CustomerModel model = new CustomerModel(phone,name,address, dLicense);
		//insert("Customer", model);
	}



	public ReservationModel makeReservation (String vtname, String dLicense, Date fromDate, Time fromTime, Date toDate, Time toTime, String location){
		try{
			PreparedStatement stmt = connection.prepareStatement("select * from customer where dlicense = ?");
			stmt.setString(1, dLicense);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()){
				//CreateCustomer();
			}

			VehicleModel[] availableCars = getAvailableCarInfo(location, fromDate, toDate, vtname);
			if (availableCars.length == 0){
				//prompt user to try again - check in controller
				return new ReservationModel(0,null,null,null,null,null,null,null );
			}

			int confNo = getConfNo();

			ReservationModel model = new ReservationModel(confNo, vtname, dLicense, fromDate, fromTime, toDate, toTime, location);
			//insert("Reservation", ReservationModel);

			stmt.close();
			rs.close();
			connection.close();

			return model;
//
//			//move this stuff to controller
//			System.out.println("Reservation Completed!");
//			System.out.println("Confirmation number: " + confNo);

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public RentModel rentAVehicleWithoutReservation(String vtname, String dLicense, Date fromDate, Time fromTime, Date toDate, Time toTime, String name, String cardName, int cardNo, Date expDate, String location){
		ReservationModel model = makeReservation(vtname, dLicense, fromDate, fromTime, toDate, toTime,location);
		RentModel rentModel = rentAVehicleWithReservation(model.getConfNo(), dLicense, cardName, cardNo, expDate);
		return rentModel;

	}

	public RentModel rentAVehicleWithReservation(int confNo, String dLicense,  String cardName, int cardNo, Date expDate){
		try{
			PreparedStatement reso = connection.prepareStatement("select * from reservation where confNo = ?");
			reso.setInt (1, confNo);
			ResultSet rs = reso.executeQuery();

			if (!rs.next()){
				System.out.println("Error - Invalid confirmation number, please retry");
			}

			String vtName = rs.getString("vtname");
			Time fromTime = rs.getTime("fromTime");
			Date fromDate = rs.getDate("fromDate");
			Time toTime = rs.getTime("toTime");
			Date toDate = rs.getDate("toDate");
			String location = rs.getString("location");

			PreparedStatement car = connection.prepareStatement("select * from vehicle where location = ? and vtname = ? and status = ?");
			car.setString(1, location);
			car.setString(2, vtName);
			car.setString(3, "Available");
			ResultSet carRs = car.executeQuery();
			int vid = carRs.getInt("vid");
			int odometer = carRs.getInt("odometer");

			RentModel model = new RentModel(getRId(), vid, dLicense, fromDate, fromTime, toDate, toTime, location, odometer, cardName, cardNo, expDate, confNo);
			//insert("Rent", model);

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

			return RentModel;

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}





	public VehicleModel[] getAvailableCarInfo(String location, Date fromDate, Date toDate, String vtName) {
		ArrayList<VehicleModel> result = new ArrayList<VehicleModel>();
		//restrict that dates are only forward on the front end
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Vehicle WHERE status = ? and location = ? and (vtname = ? or (? IS NULL or ? = '' order by make)");
			ps.setString(1, "Available");
			ps.setString(2, location);
			ps.setString(3, vtName);
			ps.setString(4, vtName);
			ps.setString(5, vtName);

			ResultSet rs = ps.executeQuery();
//number of vehicles
			while(rs.next()) {
				VehicleModel model = new VehicleModel(rs.getInt("vid"),
													rs.getInt("vLicense"),
													rs.getString("make"),
													rs.getString("model"),
													rs.getInt("year"),
						                            rs.getString("color"),
													rs.getInt("odometer"),
													rs.getString("status"),
													rs.getString("vtname"),
													rs.getString("location"),
													rs.getString("city"),
													rs.getString("fueltype"));
				result.add(model);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		//numVehicles in controller
		return result.toArray(new VehicleModel[result.size()]);
	}

	public ReturnModel returnVehicle(int rid, Date returnDate, Time returnTime, int odometerReading, boolean isTankFull){
		try{
			PreparedStatement rental = connection.prepareStatement("select * from rent where rid = ?");
			rental.setInt (1, rid);
			ResultSet rs = rental.executeQuery();

			if (!rs.next()){
			//does this work?
				System.out.println("Error - Invalid confirmation number, please retry");
			}

			Date fromDate = rs.getDate("fromDate");
			Time fromTime = rs.getTime("fromTime");
			int beginningOdometer = rs.getInt("odometer");
			String vtName = rs.getString("vtname");

			PreparedStatement vt = connection.prepareStatement("update VehicleType set numAvail = numAvail + 1 where vtname = ?");
			vt.setString (1, vtName);
			vt.executeUpdate();

			RentalValue value = calculateValue(fromDate, fromTime, returnDate, returnTime, vtName, beginningOdometer, odometerReading, isTankFull);
			ReturnModel model = new ReturnModel(rid,returnDate,returnTime,odometerReading,isTankFull, value.totalValue );
			//insert("Return",model);

			model.valueDetails = value;
			return model;



		}
		catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

	}

	public RentalValue calculateValue(Date fromDate, Time fromTime, Date returnDate, Time returnTime, String vtname, int bOdmtr, int eOdmtr, boolean tankFull){

		RentalValue value= new RentalValue();
		long days = ChronoUnit.DAYS.between(fromDate.toLocalDate(), returnDate.toLocalDate());
		long hours = ChronoUnit.HOURS.between(fromTime.toLocalTime(), returnTime.toLocalTime());

		try{
			PreparedStatement vt = connection.prepareStatement("select * from VehicleType where vtname = ?");
			vt.setString(1, vtname);
			ResultSet rs = vt.executeQuery();
			float dayRate = rs.getFloat("drate");
			float weekRate = rs.getFloat("wrate");
			float hourRate = rs.getFloat("hrate");
			float dayInsRate = rs.getFloat("dirate");
			float weekInsRate = rs.getFloat("wirate");
			float hourInsRate = rs.getFloat("hirate");
			float krate = rs.getFloat("krate");

			float rate = 0;
			float insRate = 0;
			float kmRate = 0;
			float tankRate = 0;

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
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
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
