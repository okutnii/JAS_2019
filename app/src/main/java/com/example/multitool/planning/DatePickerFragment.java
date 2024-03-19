package com.example.multitool.planning;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.multitool.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year= c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day= c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),R.style.DatePickerTheme, (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
                dialog.create();


                return dialog;
    }
}

