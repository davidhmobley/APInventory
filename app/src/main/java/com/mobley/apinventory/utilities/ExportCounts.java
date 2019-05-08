package com.mobley.apinventory.utilities;

public class ExportCounts {
    protected static final String TAG = ExportCounts.class.getSimpleName();

    private long mModifiedAssets;
    private long mExportedAssets;

    public ExportCounts(long mods, long exported) {
        mModifiedAssets = mods;
        mExportedAssets = exported;
    }

    public long getModifiedAssets() {
        return mModifiedAssets;
    }

    public void setModifiedAssets(long mModifiedAssets) {
        this.mModifiedAssets = mModifiedAssets;
    }

    public long getExportedAssets() {
        return mExportedAssets;
    }

    public void setExportedAssets(long mExportedAssets) {
        this.mExportedAssets = mExportedAssets;
    }
}
