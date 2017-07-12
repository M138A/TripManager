package com.mghartgring.tripmanager;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by MarkPC on 12-7-2017.
 */

public class RemoveConfirmationDialog extends DialogPreference {
    public RemoveConfirmationDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            // User selected OK
            DatabaseHelper dbh = new DatabaseHelper(getContext());
            dbh.RemoveAll();
        }
    }
}
