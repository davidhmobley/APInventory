package com.mobley.apinventory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;
import com.mobley.apinventory.sql.tables.Assets;

import java.util.Calendar;
import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomViewAssetsAdapter extends RecyclerView.Adapter<CustomViewAssetsAdapter.ViewHolder> {
    protected static final String TAG = CustomViewAssetsAdapter.class.getSimpleName();

    private APInventoryApp mApp;
    private List<Assets> mAssets;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView assetNum, assetDesc, assetCic, assetCmr, assetLastInvDate;

        public ViewHolder(View v) {
            super(v);
            assetNum = v.findViewById(R.id.assetsNum);
            assetDesc = v.findViewById(R.id.assetsDesc);
            assetCic = v.findViewById(R.id.assetsCicValue);
            assetCmr = v.findViewById(R.id.assetsCmrValue);
            assetLastInvDate = v.findViewById(R.id.assetsLastInvDateValue);
        }

        public TextView getAssetNum() {
            return assetNum;
        }

        public TextView getDesc() {
            return assetDesc;
        }

        public TextView getAssetCic() {
            return assetCic;
        }

        public TextView getAssetCmr() {
            return assetCmr;
        }

        public TextView getAssetLastInvDate() {
            return assetLastInvDate;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param assets List<Assets> containing the data to populate views to be used by RecyclerView.
     */
    public CustomViewAssetsAdapter(List<Assets> assets, APInventoryApp app) {
        mAssets = assets;
        mApp = app;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assets_row, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getAssetNum().setText(mAssets.get(position).getAssetNum());
        viewHolder.getDesc().setText(mAssets.get(position).getDescription());
        viewHolder.getAssetCic().setText(mAssets.get(position).getCIC());
        viewHolder.getAssetCmr().setText(mAssets.get(position).getCMR());

        if (mAssets.get(position).getLastInvDate() == 0) {
            viewHolder.getAssetLastInvDate().setText("");
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mAssets.get(position).getLastInvDate());
            viewHolder.getAssetLastInvDate().setText(
                    String.format(mApp.getString(R.string.timestamp_str),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            cal.get(Calendar.SECOND)));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mAssets.size();
    }
}
