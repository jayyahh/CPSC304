package ca.ubc.cs304.model;

import java.util.Objects;

public class CustomerModel {
    private final String cellphone;
    private final String name;
    private final String address;
    private final String dLicense;


    public CustomerModel(String cell, String name, String address, String dLicense) {
        this.cellphone = cell;
        this.name = name;
        this.address = address;
        this.dLicense = dLicense;
    }

    public String getdLicense() {
        return this.dLicense;
    }

    public String getCellPhone() {
        return Objects.requireNonNullElse(cellphone, "");

    }

    public String getName() {
        return Objects.requireNonNullElse(name, "");
    }

    public String getAddress() {
        return Objects.requireNonNullElse(address, "");
    }
}
