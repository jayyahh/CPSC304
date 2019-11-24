package ca.ubc.cs304.model;

public class VehicleModel {
    private final int vid;
    private final int vLicense;
    private final String make;
    private final String model;
    private final int year;
    private final String color;
    private final int odometer;
    private final String status;
    private final String vtname;
    private final String locn;
    private final String city;
    private final String fuelType;


    public VehicleModel(int vid, int vLicense, String make, String model, int year, String color, int odometer, String status,
                        String vt, String locn, String city, String fuelType) {
        this.vid = vid;
        this.vLicense = vLicense;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.odometer = odometer;
        this.status = status;
        this.vtname = vt;
        this.locn = locn;
        this.city = city;
        this.fuelType = fuelType;

    }

    public int getVid(){
        return this.vid;
    }

    public int getVLicense(){
        return this.vLicense;
    }

    public String getMake() {
        return this.make;
    }

    public String getModel() { return this.model; }

    public String getStatus() {
        return this.status;
    }

    public String getVtname(){
        return this.vtname;
    }

    public String getLocation() {
        return this.locn;
    }

    public String getCity() {
        return this.city;
    }

    public String getFuelType() {
        return this.fuelType;
    }
}