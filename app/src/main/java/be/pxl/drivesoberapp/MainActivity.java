package be.pxl.drivesoberapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Fragment mMainFragment;
    private static final String KEY_MAINFRAGMENT = "MAINFRAGMENT";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public void onCreate(Bundle savedInstanceState) {
        // LOG
        logDebugInfo("Activity onCreate method is called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            if (savedInstanceState != null) {
                //Restore the fragment's instance
                mMainFragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_MAINFRAGMENT);
            } else {
                mMainFragment = MainFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mMainFragment)
                        .commitNow();
            }
        } else {
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // LOG
        logDebugInfo("Activity onSaveInstanceState method is called.");

        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        //getSupportFragmentManager().putFragment(outState, KEY_MAINFRAGMENT, mMainFragment);
    }





    // debug Information
    private void logDebugInfo(String message)
    {
        Log.d("MainActivity", message);
    }
}
