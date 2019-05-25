package com.mobley.apinventory.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;
import com.mobley.apinventory.activities.MainActivity;

import java.util.Calendar;

public class InfoDialog extends AppCompatDialogFragment implements View.OnClickListener {
    protected static final String TAG = InfoDialog.class.getSimpleName();

    private APInventoryApp mApp;
    private TextView mInfoDlgAinTV, mInfoDlgCicTV, mInfoDlgCmrTV;
    private EditText mInfoDlgAinET, mInfoDlgCicET, mInfoDlgCmrET;
    private Button mGoButton;
    private Context mContext;

    public static AppCompatDialogFragment newInstance() {
        AppCompatDialogFragment dialogFragment = new InfoDialog();
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
        dialog.setTitle(getString(R.string.info_dlg_title));

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_info, null);

        // TextViews
        mInfoDlgAinTV = view.findViewById(R.id.infoDlgAinTV);
        mInfoDlgCicTV = view.findViewById(R.id.infoDlgCicTV);
        mInfoDlgCmrTV = view.findViewById(R.id.infoDlgCmrTV);

        // EditTexts
        mInfoDlgAinET = view.findViewById(R.id.infoDlgAinET);
        mInfoDlgCicET = view.findViewById(R.id.infoDlgCicET);
        mInfoDlgCmrET = view.findViewById(R.id.infoDlgCmrET);

        mGoButton = view.findViewById(R.id.infoDlgGoButton);
        mGoButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        boolean bGood = true;
        String szAIN=null, szCIC=null, szCMR=null;

        if (view == mGoButton) {
            hideKeyboard(this.getActivity());

            if (TextUtils.isEmpty(mInfoDlgAinET.getText())) {
                bGood = false;
                mInfoDlgAinTV.setTextColor(Color.RED);
                mInfoDlgAinET.requestFocus();
            } else {
                mInfoDlgAinTV.setTextColor(Color.BLACK);
                szAIN = mInfoDlgAinET.getText().toString();
            }

            if (bGood && TextUtils.isEmpty(mInfoDlgCicET.getText())) {
                bGood = false;
                mInfoDlgCicTV.setTextColor(Color.RED);
                mInfoDlgCicET.requestFocus();
            } else {
                mInfoDlgCicTV.setTextColor(Color.BLACK);
                szCIC = mInfoDlgCicET.getText().toString();
            }

            if (bGood && TextUtils.isEmpty(mInfoDlgCmrET.getText())) {
                bGood = false;
                mInfoDlgCmrTV.setTextColor(Color.RED);
                mInfoDlgCmrET.requestFocus();
            } else {
                mInfoDlgCmrTV.setTextColor(Color.BLACK);
                szCMR = mInfoDlgCmrET.getText().toString();
            }

            if (bGood) {
                // save values in Shared Preferences
                SharedPreferences.Editor editor = mApp.getAppPrefs().edit();
                editor.putString(APInventoryApp.PREF_AIN_KEY, szAIN);
                editor.commit();

                editor = mApp.getAppPrefs().edit();
                editor.putString(APInventoryApp.PREF_CIC_KEY, szCIC);
                editor.commit();

                editor = mApp.getAppPrefs().edit();
                editor.putString(APInventoryApp.PREF_CMR_KEY, szCMR);
                editor.commit();

                ((MainActivity) mContext).setSharedPrefs();
                dismiss();
            }
        }
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
