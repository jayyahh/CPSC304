package ca.ubc.cs304.model;

import java.util.*;
public class VehicleTypeModel {

    private final String vtname;
    private final String features;
    private final int wrate;
    private final int drate;
    private final int hrate;
    private final int wirate;
    private final int dirate;
    private final int hirate;
    private final int krate;

    public VehicleTypeModel(String vtname, String features, int wr, int dr, int hr, int wi, int di, int hi, int kr) {
        this.vtname = vtname;
        this.features = features;
        wrate = wr;
        drate = dr;
        hrate = hr;
        wirate = wi;
        dirate = di;
        hirate = hi;
        krate = kr;
    }

    public String getVtname() {
        return this.vtname;
    }

    public String getFeatures() {
        return this.features;
    }

    public ArrayList<Integer> getRates() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(wrate);
        list.add(drate);
        list.add(hrate);
        list.add(wirate);
        list.add(dirate);
        list.add(hirate);
        list.add(krate);

        return list;
    }


}
