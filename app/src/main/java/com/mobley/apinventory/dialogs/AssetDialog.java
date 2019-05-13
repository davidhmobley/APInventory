package com.mobley.apinventory.dialogs;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;

import java.util.Calendar;

public class AssetDialog extends AppCompatDialogFragment implements View.OnClickListener {
    protected static final String TAG = AssetDialog.class.getSimpleName();

    private APInventoryApp mApp;
    private TextView mAssetNum, mAssetCIC, mAssetCMR, mAssetLastInvDate;
    private Button mGoButton;
    public String mNum;
    public String mCIC;
    public String mCMR;
    public long mLastInvDate;

    public static AppCompatDialogFragment newInstance() {
        AppCompatDialogFragment dialogFragment = new AssetDialog();
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
        dialog.setTitle(getString(R.string.asset_dlg_title));

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_asset, null);

        mAssetNum = view.findViewById(R.id.assetDlgAssetNum);
        mAssetCIC = view.findViewById(R.id.assetDlgAssetCIC);
        mAssetCMR = view.findViewById(R.id.assetDlgAssetCMR);
        mAssetLastInvDate = view.findViewById(R.id.assetDlgLastInvDate);

        // values
        mAssetNum.setText(mNum);
        mAssetCIC.setText(mCIC);
        mAssetCMR.setText(mCMR);

        // special case for Last Inventory Date
        if (mLastInvDate == 0) {
            mAssetLastInvDate.setText("");
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mLastInvDate);
            mAssetLastInvDate.setText(
                    String.format(mApp.getString(R.string.timestamp_str),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            cal.get(Calendar.SECOND)));
        }

        mGoButton = view.findViewById(R.id.assetDlgGoButton);
        mGoButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        boolean bGood = true;

        if (view == mGoButton) {
            //hideKeyboard(this);

            dismiss();
        }
    }

    public void setNum(String mNum) {
        this.mNum = mNum;
    }

    public void setCIC(String mCIC) {
        this.mCIC = mCIC;
    }

    public void setCMR(String mCMR) {
        this.mCMR = mCMR;
    }

    public void setLastInvDate(long mLastInvDate) {
        this.mLastInvDate = mLastInvDate;
    }
}
