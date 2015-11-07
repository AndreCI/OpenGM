package ch.epfl.sweng.opengm.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer;

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


    public static List<String[]> unzipRoles(List<String> rolesZip) {
        List<String[]> res = new ArrayList<>();
        for(String s : rolesZip) {
            res.add(s.split(" -- "));
        }
        return res;
    }

    public static String zipRole(List<String> roles) {
        StringBuilder stringBuilder = new StringBuilder();
        if(roles.size() > 0 ) {
            for (int i = 0; i < roles.size() - 1; ++i) {
                stringBuilder.append(roles.get(i));
                stringBuilder.append(" -- ");
            }
            stringBuilder.append(roles.get(roles.size()-1));
        }
        return stringBuilder.toString();
    }


    public static String stripAccents(String s) {
        String res = s.toLowerCase();
        res = Normalizer.normalize(res, Normalizer.Form.NFD);
        res = res.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return res;
    }
}
