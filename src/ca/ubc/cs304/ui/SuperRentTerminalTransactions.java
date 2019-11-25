package ca.ubc.cs304.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import ca.ubc.cs304.controller.SuperRent;
import ca.ubc.cs304.delegates.MainTerminalTransactionsDelegate;


/**
 * The class is only responsible for handling terminal text inputs.
 */
public class SuperRentTerminalTransactions {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";
    private static final int INVALID_INPUT = Integer.MIN_VALUE;
    private static final int EMPTY_INPUT = 0;

    private BufferedReader bufferedReader = null;
    private MainTerminalTransactionsDelegate delegate = null;

    public SuperRentTerminalTransactions() {
    }

    /**
     * Displays simple text interface
     */
    public void showMainMenu(MainTerminalTransactionsDelegate delegate) {
        this.delegate = delegate;

        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int choice = INVALID_INPUT;

        while (choice != 5) {
            System.out.println();
            System.out.println("1. View available cars");
            System.out.println("2. Make a reservation");
            System.out.println("3. Rent a vehicle");
            System.out.println("4. Return a vehicle");
            System.out.println("5. Generate a daily report");
            System.out.println("6. Quit");
            System.out.print("Please choose one of the above 6 options: ");

            choice = readInteger(false);

            System.out.println(" ");

            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 1:
                        showAvailableVehicles();
                    case 2:
                        makeReservation();
                        break;
                    case 3:
                        rentVehicle();
                        break;
                    case 4:
                        returnVehicle();
                        break;
                    case 5:
                        generateDailyReport();
                        break;
                    case 6:
                        handleQuitOption();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }
    }

    private void showAvailableVehicles() {
        String carType = selectCarType();
        String location = selectLocation();
        Date startDate = selectDate("start");
        Timestamp startTS = convertToSqlTimeStamp(startDate,12,30);
        Date endDate = selectDate("end");
        Timestamp endTs = convertToSqlTimeStamp(endDate,12,30);
        if (endDate.compareTo(startDate) > 0) {
            delegate.showAvailableVehicles(location, startTS, endTs, carType);
        } else {
            System.out.println("End date must be later than start date, quitting program...");
            handleQuitOption();
        }
    }

    private void makeReservation() {
        String carType = selectCarType();
        String location = selectLocation();
        Date startDate = selectDate("start");
        int startHour = selectHour();
        int startMin = selectMin();
        Timestamp startDateTime = convertToSqlTimeStamp(startDate,startHour,startMin);
        Date endDate = selectDate("end");
        int endHour = selectHour();
        int endMin = selectMin();
        Timestamp endDateTime = convertToSqlTimeStamp(endDate,endHour,endMin);
        String dLicense = enterAny("driver's license");
        if (isEndTimeLater(startDateTime, endDateTime)){
            delegate.reserveVehicle(carType, dLicense, startDateTime, endDateTime , location);
        } else {
            System.out.println("End time must be later than start time, quitting program...");
            handleQuitOption();
        }
    }

    private void rentVehicle() {
        int confNo = Integer.parseInt(enterAny("confirmation number"));
        String carType = selectCarType();
        String location = selectLocation();
        String dLicense = enterAny("driver's license");
        Date startDate = selectDate("start");
        int startHour = selectHour();
        int startMin = selectMin();
        Timestamp startDateTime =convertToSqlTimeStamp(startDate,startHour,startMin);
        Date endDate = selectDate("end");
        int endHour = selectHour();
        int endMin = selectMin();
        Timestamp endDateTime = convertToSqlTimeStamp(endDate,endHour,endMin);
        String name = enterAny("name");
        String cardName = enterAny("name on card");
        int cardNo = Integer.parseInt(enterAny("card number"));
        Date expDate = selectDate("card expiration date");
        Timestamp exp = convertToSqlTimeStamp(expDate,12,30);
        delegate.rentVehicle(confNo, carType, dLicense,startDateTime,endDateTime, name, cardName, cardNo, exp, location);
    }

    private void returnVehicle() {
        int rid = Integer.parseInt(enterAny("rid"));
        Date returnDate = selectDate("return");
        int hour = selectHour();
        int min = selectMin();
        Timestamp returnDateTime = convertToSqlTimeStamp(returnDate,hour,min);
        int odometer = Integer.parseInt(enterAny("odometer"));
        boolean isTankFull = selectBool();
        delegate.returnVehicle(rid, returnDateTime, odometer, isTankFull);
    }

    private void generateDailyReport() {
        int choice = INVALID_INPUT;
        while (choice != 5) {
            System.out.println();
            System.out.println("1. Report of all daily rentals");
            System.out.println("2. Report of daily rentals by branch");
            System.out.println("3. Report of all daily returns");
            System.out.println("4. Report of daily returns by branch");
            System.out.println("5. Quit");
            System.out.print("Please choose one of the above 5 options: ");
            choice = readInteger(false);
            System.out.println(" ");
            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 1:
                        Date date1 = selectDate("report");
                        Timestamp d1 = convertToSqlTimeStamp(date1,00,00);
                        delegate.generateRentalsReport(d1);
                        break;
                    case 2:
                        Date date2 = selectDate("report");
                        Timestamp d2 = convertToSqlTimeStamp(date2,00,00);
                        String branch1 = selectLocation();
                        delegate.generateRentalsBranchReport(d2, branch1);
                        break;
                    case 3:
                        Date date3 = selectDate("report");
                        Timestamp d3 = convertToSqlTimeStamp(date3,00,00);
                        delegate.generateReturnsReport(d3);
                        break;
                    case 4:
                        Date date4 = selectDate("report");
                        Timestamp d4 = convertToSqlTimeStamp(date4,00,00);
                        String branch2 = selectLocation();
                        delegate.generateReturnsBranchReport(d4, branch2);
                        break;
                    case 5:
                        handleQuitOption();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }
    }

    private boolean isEndTimeLater(Timestamp start, Timestamp end) {
        return end.after(start);
    }

    private boolean selectBool() {
        boolean ret = false;
        int choice = INVALID_INPUT;
        System.out.println();
        System.out.println("Please select true or false: ");
        System.out.println("1. True");
        System.out.println("2. False");
        System.out.println("3. Quit");
        while (choice < 0 || choice > 3) {
            choice = readInteger(false);
            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 1:
                        ret = true;
                        break;
                    case 2:
                        ret = false;
                        break;
                    case 3:
                        handleQuitOption();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }
        return ret;
    }

    public String enterAny(String what) {
        String choice = "";
        while (choice == ""){
            System.out.println("Please enter your " + what + " : ");
            choice = readLine();
        }
        return choice;
    }

    private int selectHour() {
        int choice = INVALID_INPUT;
        while (choice < 0 || choice > 23) {
            System.out.println("Valid hour: ");
            choice = readInteger(false);
        }
        return choice;
    }

    private int selectMin() {
        int choice = INVALID_INPUT;
        while (choice < 0 || choice > 59) {
            System.out.println("Valid minute: ");
            choice = readInteger(false);
        }
        return choice;
    }

    private Date selectDate(String time) {
        System.out.println("Please select " + time + " date: ");
        int year = selectYear();
        int month = selectMonth();
        int day = selectDay(month);
        String date = year + "-" + month + "-" + day;
        return Date.valueOf(date);
    }

    private int selectYear() {
        int choice = INVALID_INPUT;
        while (choice < 2000 || choice > 3000) {
            System.out.println("Valid year between 2000 and 3000 ");
            choice = readInteger(false);
        }
        return choice;
    }

    private int selectMonth() {
        int choice = INVALID_INPUT;
        while (choice < 1 || choice > 12) {
            System.out.println("Valid month: ");
            choice = readInteger(false);
        }
        return choice;
    }

    private int selectDay(int month){
        int choice = INVALID_INPUT;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            while (choice < 1 || choice > 31) {
                System.out.println("Valid day of month " + month + " : ");
                choice = readInteger(false);
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            while (choice < 1 || choice > 30) {
                System.out.println("Valid day of month " + month + " : ");
                choice = readInteger(false);
            }
        } else {
            while (choice < 1 || choice > 28) {
                System.out.println("Valid day of month" + month + " : ");
                choice = readInteger(false);
            }
        }
        return choice;
    }

    private String selectLocation() {
        String location = "";
        int choice = INVALID_INPUT;
        System.out.println("Please select one of the following locations: ");
        System.out.println("1. Cambie, Vancouver");
        System.out.println("2. Oak, Vancouver");
        System.out.println("3. Granville, Vancouver");
        System.out.println("4. SunnyCoast, Whistler");
        System.out.println("5. Bridgeport, Richmond");
        System.out.println("6. Bay, Seattle");
        System.out.println("7. Quit");
        while (choice < 0 || choice > 7) {
            choice = readInteger(true);
            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 0:
                        location = "Any";
                        break;
                    case 1:
                        location = "Cambie";
                        break;
                    case 2:
                        location = "Oak";
                        break;
                    case 3:
                        location = "Granville";
                        break;
                    case 4:
                        location = "SunnyCoast";
                        break;
                    case 5:
                        location = "Bridgeport";
                        break;
                    case 6:
                        location = "Bay";
                        break;
                    case 7:
                        handleQuitOption();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }
        return location;
    }

    private String selectCarType() {
        String carType = "";
        int choice = INVALID_INPUT;
        System.out.println();
        System.out.println("Please select one of the following car types: ");
        System.out.println("1. Economy");
        System.out.println("2. Compact");
        System.out.println("3. Mid-size");
        System.out.println("4. Standard");
        System.out.println("5. Full-size");
        System.out.println("6. SUV");
        System.out.println("7. Truck");
        System.out.println("8. Quit");
        while (choice < 0 || choice > 8) {
            choice = readInteger(true);
            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 0:
                        carType = "Any";
                        break;
                    case 1:
                        carType = "Economy";
                        break;
                    case 2:
                        carType = "Compact";
                        break;
                    case 3:
                        carType = "Mid-size";
                        break;
                    case 4:
                        carType = "Standard";
                        break;
                    case 5:
                        carType = "Full-size";
                        break;
                    case 6:
                        carType = "SUV";
                        break;
                    case 7:
                        carType = "Truck";
                        break;
                    case 8:
                        handleQuitOption();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }
        return carType;
    }

    public void handleQuitOption() {
        System.out.println("Good Bye!");

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println("IOException!");
            }
        }

        delegate.terminalTransactionsFinished();
    }

    private int readInteger(boolean allowEmpty) {
        String line = null;
        int input = INVALID_INPUT;
        try {
            line = bufferedReader.readLine();
            input = Integer.parseInt(line);
        } catch (IOException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        } catch (NumberFormatException e) {
            if (allowEmpty && line.length() == 0) {
                input = EMPTY_INPUT;
            } else {
                System.out.println(WARNING_TAG + " Your input was not an integer");
            }
        }
        return input;
    }
    private Timestamp convertToSqlTimeStamp(Date date,int hour, int minute){
        String dateString = date.toString();
        String timeString = hour+":"+minute+":"+"00";
        return Timestamp.valueOf(dateString + " " +timeString);
    }

    private String readLine() {
        String result = null;
        try {
            result = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }
}
