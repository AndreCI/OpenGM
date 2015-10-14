package ch.epfl.sweng.opengm.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ch.epfl.sweng.opengm.R;

public class Utils {

    public static void onTapOutsideBehaviour(View view, final Activity activity) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                try {
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    public static ProgressDialog getProgressDialog(Context c) {
        ProgressDialog dialog = new ProgressDialog(c);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(c.getString(R.string.loading_dialog));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

}
