package com.mobley.apinventory.sql.tables;

public class Locations {
    protected static final String TAG = Locations.class.getSimpleName();

    public static final String LOCATIONS_TABLE_NAME = "Locations";

    public static final String LOCATIONS_COL_ID = "_id";
    public static final String LOCATIONS_COL_NUM = "LocationNum";
    public static final String LOCATIONS_COL_DESC = "LocationDesc";

    public long mId;
    public String mLocationNum;
    public String mLocationDesc;

    public Locations(String locationNum, String locationDesc) {
        mLocationNum = locationNum;
        mLocationDesc = locationDesc;
    }

    public String getLocationNum() {
        return mLocationNum;
    }

    public void setLocationNum(String mLocationNum) {
        this.mLocationNum = mLocationNum;
    }

    public String getLocationDesc() {
        return mLocationDesc;
    }

    public void setLocationDesc(String mLocationDesc) {
        this.mLocationDesc = mLocationDesc;
    }
}
