package ch.epfl.sweng.opengm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseObject;

public class MainActivity extends AppCompatActivity {

    private final static String PARSE_APP_ID = "LiaIqx4G3cgt0LSZ6aYcZB7mGI5V2zx3fek03HGc";
    private final static String PARSE_KEY = "tQSqozHYj1d9hVhMAwKnEslDVXuzyATAQcOstEor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, PARSE_APP_ID, PARSE_KEY);

    }
}
