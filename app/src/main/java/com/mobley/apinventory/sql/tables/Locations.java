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

    public Locations(long id, String locationNum, String locationDesc) {
        mId = id;
        mLocationNum = locationNum;
        mLocationDesc = locationDesc;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
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
