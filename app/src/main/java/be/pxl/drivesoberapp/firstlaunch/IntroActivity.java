package be.pxl.drivesoberapp.firstlaunch;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import be.pxl.drivesoberapp.MainActivity;
import be.pxl.drivesoberapp.R;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter ;
    private TabLayout tabIndicator;
    private TextView btnNext;
    private int position = 0 ;
    private Button btnGetStarted;
    private Animation btnAnim ;
    private TextView tvSkip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // Check if its openened before or not
        if (restoreIntroPrefData()) {
            // Load MainActivity
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class );
            startActivity(mainActivity);
            finish();
        }

        setContentView(R.layout.activity_intro);

        // Hide the action bar
        getSupportActionBar().hide();

        // Initiate Views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        // fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem(
                "Fresh Food",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.ic_drink_beer_can));
        mList.add(new ScreenItem(
                "Fast Delivery",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.ic_drink_pint_large));
        mList.add(new ScreenItem(
                "Easy Payment",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.ic_drink_cocktail));

        // Setup viewpager
        screenPager = findViewById(R.id.vp_intro_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // Setup TabLayout
        tabIndicator.setupWithViewPager(screenPager);

        // Next button clickListener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size()-1) {
                    loaddLastScreen();
                }
            }
        });

        // TabLayout add changeListener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // GetStarted button clickListener
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open initial settings activity
                Intent introSettingsActivity = new Intent(getApplicationContext(), IntroSettingsActivity.class);
                startActivity(introSettingsActivity);
                saveIntroPrefsData();
                finish();
            }
        });

        // Skip button clickListener
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });
    }

    private boolean restoreIntroPrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("introPref" ,MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("notInitialStart",false);
        return  isIntroActivityOpnendBefore;
    }

    private void saveIntroPrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("introPref" ,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("notInitialStart",true);
        editor.apply();
    }

    // Show the GetStarted Button and hide the indicator and the next button
    private void loaddLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
    }
}