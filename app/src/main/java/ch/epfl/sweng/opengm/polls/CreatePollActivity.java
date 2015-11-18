package ch.epfl.sweng.opengm.polls;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class CreatePollActivity extends AppCompatActivity {

    private NumberPicker possibleAnswersPicker;
    private List<String> answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        answers = new ArrayList<>();

        possibleAnswersPicker = (NumberPicker) findViewById(R.id.possibleAnswersPicker);
        possibleAnswersPicker.setMinValue(1);
        possibleAnswersPicker.setValue(1);
        possibleAnswersPicker.setMaxValue(1);
        possibleAnswersPicker.setWrapSelectorWheel(false);
        possibleAnswersPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub

                String Old = "Old Value : ";
                String New = "New Value : ";

                Log.d("OLD", Old.concat(String.valueOf(oldVal)));
                Log.d("NEW", New.concat(String.valueOf(newVal)));
            }
        });
    }

    public void addAnswer(View view) {
        answers.add("");
        possibleAnswersPicker.setMaxValue(answers.size() + 1);
        Log.d("OLD", "CLICK");
    }

}
