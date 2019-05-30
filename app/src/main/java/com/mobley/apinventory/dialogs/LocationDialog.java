package com.mobley.apinventory.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;
import com.mobley.apinventory.activities.MainActivity;
import com.mobley.apinventory.activities.ViewLocationsActivity;

public class LocationDialog extends AppCompatDialogFragment implements View.OnClickListener {
    protected static final String TAG = LocationDialog.class.getSimpleName();

    private APInventoryApp mApp;
    private TextView mLocationNum, mLocationDesc;
    private Button mGoButton, mSetLocationButton;
    public String mNum;
    public String mDesc;
    public Context mContext;

    public static AppCompatDialogFragment newInstance() {
        AppCompatDialogFragment dialogFragment = new LocationDialog();
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setStyle(DialogFragment.STYLE_NORMAL, R.style.My_DialogStyle);

        mApp = ((APInventoryApp) getActivity().getApplication());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.location_dlg_title));

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_location, null);

        mLocationNum = view.findViewById(R.id.locationDlgLocationNum);
        mLocationDesc = view.findViewById(R.id.locationDlgLocationDesc);

        // values
        mLocationNum.setText(mNum);
        mLocationDesc.setText(mDesc);

        mGoButton = view.findViewById(R.id.locationDlgGoButton);
        mGoButton.setOnClickListener(this);
        mSetLocationButton = view.findViewById(R.id.locationDlgSetLocationButton);
        mSetLocationButton.setEnabled(true);
        mSetLocationButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        boolean bGood = true;

        if (view == mGoButton) {
            //hideKeyboard(this);

            dismiss();
        } else if (view == mSetLocationButton) {
            SharedPreferences.Editor editor = mApp.getAppPrefs().edit();
            editor.putString(APInventoryApp.PREF_LOCATION_KEY, mDesc);
            editor.commit();

            mSetLocationButton.setEnabled(false);
            ((ViewLocationsActivity) mContext).showSetLocation(mDesc);
        }
    }

    public void setNum(String mNum) {
        this.mNum = mNum;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
