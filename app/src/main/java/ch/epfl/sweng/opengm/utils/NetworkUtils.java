package ch.epfl.sweng.opengm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtils {
    public static boolean haveInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean haveInternet = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!haveInternet){
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
        }
        return haveInternet;
    }
}
