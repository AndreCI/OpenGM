package ch.epfl.sweng.opengm;

import android.app.Application;

import com.parse.Parse;

public class OpenGMApplication extends Application {

    private final static String PARSE_APP_ID = "LiaIqx4G3cgt0LSZ6aYcZB7mGI5V2zx3fek03HGc";
    private final static String PARSE_KEY = "tQSqozHYj1d9hVhMAwKnEslDVXuzyATAQcOstEor";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_KEY);

    }
}
