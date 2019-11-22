package ca.ubc.cs304.model;

import java.util.*;
public class VehicleTypeModel {

    private final String vtname;
    private final String features;
    private final double wrate;
    private final double drate;
    private final double hrate;
    private final double wirate;
    private final double dirate;
    private final double hirate;
    private final double krate;

    public VehicleTypeModel(String vtname, String features, double wr, double dr, double hr, double wi, double di, double hi, double kr) {
        this.vtname = vtname;
        this.features = features;
        this.wrate = wr;
        this.drate = dr;
        this.hrate = hr;
        this.wirate = wi;
        this.dirate = di;
        this.hirate = hi;
        this.krate = kr;
    }

    public String getVtname() {
        return this.vtname;
    }

    public String getFeatures() {
        return this.features;
    }

    public double getWRate(){return this.wrate; }

    public double getDRate(){return this.drate; }

    public double getHRate(){return this.hrate; }

    public double getWiRate(){return this.wirate; }

    public double getDiRate(){return this.dirate; }

    public double getHiRate(){return this.hirate; }

    public double getKRate(){return this.krate; }
}
