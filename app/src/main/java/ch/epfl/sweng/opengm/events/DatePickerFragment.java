package ch.epfl.sweng.opengm.events;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.opengm.R;

import static ch.epfl.sweng.opengm.events.Utils.stringToDate;

/**
 * Created by virgile on 19/10/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String s = getTag();
        if (s.length() != 0) {
            Date date = stringToDate(s);
            year = date.getYear();
            month = date.getMonth();
            day = date.getDate();
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = String.format("%d/%02d/%04d", day, month + 1, year);
        ((Button) getActivity().findViewById(R.id.CreateEditEventDateText)).setText(date);
    }
}