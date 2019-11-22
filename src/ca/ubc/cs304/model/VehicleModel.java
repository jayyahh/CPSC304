package ca.ubc.cs304.model;

public class VehicleModel {
    private final int vid;
    private final int vLicense;
    private final String make;
    private final int year;
    private final String color;
    private final int odometer;
    private final String status;
    private final String vtname;
    private final String locn;
    private final String city;


    public VehicleModel(int vid, int vLicense, String make, int year, String color, int odometer, String status,
                        VehicleTypeModel vt, String locn, String city) {
        this.vid = vid;
        this.vLicense = vLicense;
        this.make = make;
        this.year = year;
        this.color = color;
        this.odometer = odometer;
        this.status = status;
        this.vtname = vt.getVtname();
        this.locn = locn;
        this.city = city;


    }

    public int getVid(){
        return this.vid;
    }

    public int getvLicense(){
        return this.vLicense;
    }

    public String getMake() {
        return this.make;
    }

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



}
