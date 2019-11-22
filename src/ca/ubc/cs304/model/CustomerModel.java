package ca.ubc.cs304.model;

public class CustomerModel {
    private final String cellphone;
    private final String name;
    private final String address;
    private final int dLicense;

    public CustomerModel(String cell, String name, String address, int dLicense) {
        this.cellphone = cell;
        this.name = name;
        this.address = address;
        this.dLicense = dLicense;
    }

    public int getdLicense() {
        return this.dLicense;
    }

    public String getCellPhone() {
        return this.cellphone;

    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }
}
