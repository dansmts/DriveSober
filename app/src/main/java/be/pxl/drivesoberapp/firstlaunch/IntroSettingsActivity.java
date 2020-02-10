package be.pxl.drivesoberapp.firstlaunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;

import be.pxl.drivesoberapp.MainActivity;
import be.pxl.drivesoberapp.R;

public class IntroSettingsActivity extends AppCompatActivity {

    private NumberPicker npSetWeight;
    private ImageView ivFemale;
    private ImageView ivMale;
    private Button btnSetPreferences;

    private String weightValue;
    private String genderValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intro_settings);

        // Hide the action bar
        getSupportActionBar().hide();

        // Initiate Views
        npSetWeight = findViewById(R.id.np_set_weight);
        ivFemale = findViewById(R.id.iv_intro_settings_img_female);
        ivMale = findViewById(R.id.iv_intro_settings_img_male);
        btnSetPreferences = findViewById(R.id.btn_intro_settings_set_preferences);

        // Setup Numberpicker
        npSetWeight.setMinValue(25);
        npSetWeight.setMaxValue(300);
        npSetWeight.setValue(75);
        npSetWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                weightValue = Integer.toString(npSetWeight.getValue());
                Log.d("setWeight Value:", weightValue);
            }
        });

        ivFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderValue = getString(R.string.pref_gender_female_value);
                ivFemale.setAlpha((float) 1.0);
                ivMale.setAlpha((float) 0.4);
            }
        });

        ivMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderValue = getString(R.string.pref_gender_male_value);
                ivMale.setAlpha((float) 1.0);
                ivFemale.setAlpha((float) 0.4);
            }
        });

        // SetPrefences button clickListener
        btnSetPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open main activity
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                savePreferences();
                finish();
            }
        });
    }

    public void savePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_weight_key), weightValue);
        editor.putString(getString(R.string.pref_gender_key), genderValue);
        editor.apply();
    }


}
