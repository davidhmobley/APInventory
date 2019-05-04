package com.mobley.apinventory.sql.tables;

public class Assets {
    protected static final String TAG = Assets.class.getSimpleName();

    public static final String ASSETS_TABLE_NAME = "Assets";

    public static final String ASSETS_COL_ID = "_id";
    public static final String ASSETS_COL_NUM = "AssetNum";
    public static final String ASSETS_COL_BCN = "BarcodeNum";
    public static final String ASSETS_COL_CIC = "CIC";
    public static final String ASSETS_COL_CMR = "CMR";

    public long mId;
    public String mAssetNum;
    public String mBarcodeNum;
    public String mCIC;
    public String mCMR;

    public Assets(String assetNum, String barcodeNum, String cic, String cmr) {
        mAssetNum = assetNum;
        mBarcodeNum = barcodeNum;
        mCIC = cic;
        mCMR = cmr;
    }

    public String getAssetNum() {
        return mAssetNum;
    }

    public void setAssetNum(String mAssetNum) {
        this.mAssetNum = mAssetNum;
    }

    public String getBarcodeNum() {
        return mBarcodeNum;
    }

    public void setBarcodeNum(String mBarcodeNum) {
        this.mBarcodeNum = mBarcodeNum;
    }

    public String getCIC() {
        return mCIC;
    }

    public void setCIC(String mCIC) {
        this.mCIC = mCIC;
    }

    public String getCMR() {
        return mCMR;
    }

    public void setCMR(String mCMR) {
        this.mCMR = mCMR;
    }
}
