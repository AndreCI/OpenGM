package ch.epfl.sweng.opengm.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static String zipRole(String[] roles) {
        return zipRole(Arrays.asList(roles));
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

    public static String saveToInternalSorage(Bitmap bitmapImage, Context appContext, String fileName) throws IOException{
        ContextWrapper cw = new ContextWrapper(appContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,fileName+".jpg");
        FileOutputStream fos = null;
        fos = new FileOutputStream(mypath);
        // Use the compress method on the BitMap object to write image to the OutputStream
        if(bitmapImage == null){
            bitmapImage = BitmapFactory.decodeResource(appContext.getResources(),R.drawable.default_event);
        }
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        if (fos != null) {
            fos.close();
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path, String name) throws FileNotFoundException
    {
        File f=new File(path, name);
        return BitmapFactory.decodeStream(new FileInputStream(f));
    }
}
