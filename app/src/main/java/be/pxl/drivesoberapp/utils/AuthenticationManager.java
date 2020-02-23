package be.pxl.drivesoberapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import be.pxl.drivesoberapp.MainActivity;
import be.pxl.drivesoberapp.R;
import be.pxl.drivesoberapp.models.User;

public class AuthenticationManager {


    public static void login(String email, String password, Activity activity) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        ProgressBar progressBar = activity.findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(activity, "login failed", Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseManager.getUserData(user.getUid(), activity);
                            saveIntroPrefsData(activity);
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                });
    }

    public static void register(String email, String password, User newUser, Activity activity) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        ProgressBar progressBar = activity.findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);

        ConstraintLayout cl = activity.findViewById(R.id.layout_register);
        if (cl != null) {
            cl.setVisibility(View.GONE);
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                                Toast.makeText(activity, "register failed", Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseManager.saveUser(user.getUid(), newUser);

                            login(email, password, activity);
                        }
                    }
                });
    }

    public static void saveIntroPrefsData(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("introPref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("notInitialStart",true);
        editor.apply();
    }
}
