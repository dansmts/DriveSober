package be.pxl.drivesoberapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import be.pxl.drivesoberapp.models.User;
import be.pxl.drivesoberapp.utils.AuthenticationManager;
import be.pxl.drivesoberapp.utils.DatabaseManager;

public class IntroRegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private NumberPicker npSetWeight;
    private ImageView ivFemale;
    private ImageView ivMale;
    private Button btnRegister;
    private Animation btnAnim;
    private ProgressBar progressBar;

    private String weightValue;
    private String genderValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intro_register);

        // Hide the action bar
        getSupportActionBar().hide();

        // create mAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initiate Views
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        npSetWeight = findViewById(R.id.np_set_weight);
        ivFemale = findViewById(R.id.iv_intro_settings_img_female);
        ivMale = findViewById(R.id.iv_intro_settings_img_male);
        btnRegister = findViewById(R.id.btn_intro_register);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

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

        // Setup genderpicker

        genderValue = getString(R.string.pref_gender_default);

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

        // Register button clickListener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                User newUser = new User(etUsername.getText().toString(), genderValue, weightValue);

                AuthenticationManager.register(email, password, newUser, IntroRegisterActivity.this);
            }
        });
    }

}
