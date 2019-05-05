package com.mobley.apinventory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;
import com.mobley.apinventory.sql.tables.Locations;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomViewLocationsAdapter extends RecyclerView.Adapter<CustomViewLocationsAdapter.ViewHolder> {
    protected static final String TAG = CustomViewLocationsAdapter.class.getSimpleName();

    private APInventoryApp mApp;
    private List<Locations> mLocations;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationNum, locationDesc;

        public ViewHolder(View v) {
            super(v);
            locationNum = v.findViewById(R.id.locationsNum);
            locationDesc = v.findViewById(R.id.locationsDesc);
        }

        public TextView getLocationNum() {
            return locationNum;
        }

        public TextView getLocationDesc() {
            return locationDesc;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param locations List<Locations> containing the data to populate views to be used by RecyclerView.
     */
    public CustomViewLocationsAdapter(List<Locations> locations, APInventoryApp app) {
        mLocations = locations;
        mApp = app;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locations_row, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getLocationNum().setText(mLocations.get(position).getLocationNum());
        viewHolder.getLocationDesc().setText(mLocations.get(position).getLocationDesc());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLocations.size();
    }
}
