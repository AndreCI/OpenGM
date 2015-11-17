package ch.epfl.sweng.opengm.events;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.opengm.R;

import static ch.epfl.sweng.opengm.events.Utils.stringToDate;

/**
 * Created by virgile on 19/10/2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String s = getTag();
        if(s.length() != 0) {
            Date date = stringToDate(s);
            hour = date.getHours();
            minute = date.getMinutes();
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = String.format("%d : %02d", hourOfDay, minute);
        ((Button) getActivity().findViewById(R.id.CreateEditEventTimeText)).setText(time);
    }
}