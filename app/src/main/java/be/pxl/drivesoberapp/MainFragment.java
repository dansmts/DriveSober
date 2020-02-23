package be.pxl.drivesoberapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import be.pxl.drivesoberapp.utils.AuthenticationManager;
import be.pxl.drivesoberapp.utils.DatabaseManager;
import be.pxl.drivesoberapp.utils.DrinkController;
import be.pxl.drivesoberapp.models.Consumption;
import be.pxl.drivesoberapp.models.NightOut;
import be.pxl.drivesoberapp.utils.ConsumptionListAdapter;
import be.pxl.drivesoberapp.utils.DrinksListDialogFragment;

import static android.content.ContentValues.TAG;

public class MainFragment extends Fragment implements DrinksListDialogFragment.Listener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Bundle mSavedState;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    ///////////////////////////////////////////////
    // Layout
    private RecyclerView rvConsumptieLijst;
    private RecyclerView.LayoutManager layoutManager;
    private TextView tvDatum;
    private TextView tvEersteConsumptie;
    private TextView tvPromille;
    private TextView tvAantalGlazen;
    private TextView tvNuchterValue;
    private TextView tvNuchterLabel;
    private ImageView ivSaveButton;

    // Adapters
    private ConsumptionListAdapter mConsumptionAdapter;


    // Andere
    private SharedPreferences sharedPreferences;
    private DatabaseManager dbManager;
    private NightOut mNightOut;
    private DrinkController dc;
    private Menu mOptionsMenu;
    private static final String KEY_NIGHTOUT = "NIGHTOUT";
    ////////////////////////////////////////////////////////////////////

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // LOG
        logDebugInfo("Fragment onCreateView is called.");

        // set View
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        // load SharedPreferences
        setupSharedPreferences();

        // load FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // load DatabaseManager
        dbManager = new DatabaseManager();

        // create DrinkController
        dc = new DrinkController();

        // load NightOut layout TextViews
        tvDatum = view.findViewById(R.id.tv_datum_value);
        tvEersteConsumptie = view.findViewById(R.id.tv_eerste_consumptie_value);
        tvPromille = view.findViewById(R.id.tv_promille_value);
        tvAantalGlazen = view.findViewById(R.id.tv_standaardglazen_value);
        tvNuchterValue = view.findViewById(R.id.tv_nuchter_value);
        tvNuchterLabel = view.findViewById(R.id.tv_nuchter_label);

        // load Save Button
        ivSaveButton = view.findViewById(R.id.iv_save);
        ivSaveButton.setOnClickListener(save -> onSaveClicked());

        // load SavedInstanceState
        if (savedInstanceState != null) {
            mNightOut = savedInstanceState.getParcelable(KEY_NIGHTOUT);
            updateTextViews();
        } else {
            mNightOut = new NightOut(getContext());
            tvNuchterValue.setText(getText(R.string.nightout_sober));
            tvNuchterLabel.setText(getText(R.string.nightout_add_consumption));
            ivSaveButton.setVisibility(View.GONE);
        }

        // load RecyclerView
        rvConsumptieLijst = view.findViewById(R.id.rv_consumptie_lijst);
        layoutManager = new LinearLayoutManager(getContext());
        rvConsumptieLijst.setLayoutManager(layoutManager);
        mConsumptionAdapter = new ConsumptionListAdapter(mNightOut.getConsumptionsList());
        rvConsumptieLijst.setAdapter(mConsumptionAdapter);

        // load ItemTouchHelper for SwipeToDelete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // LOG
                logDebugInfo("onSwiped method is called.");

                int deletedConsumption = viewHolder.getAdapterPosition();
                mConsumptionAdapter.deleteItem(deletedConsumption);
                updateTextViews();
                mConsumptionAdapter.notifyItemRemoved(deletedConsumption);
            }
        }).attachToRecyclerView(rvConsumptieLijst);

        // load Floating Action Button
        final DrinksListDialogFragment drinksListFragment = DrinksListDialogFragment.newInstance();
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(dialog -> drinksListFragment.show(getChildFragmentManager(), drinksListFragment.getTag()));

        // return view
        return view;

    }

    public void updateTextViews() {
        // LOG
        logDebugInfo("updateTextViews method is called.");

        if (mNightOut.getConsumptionsList().size() == 0) {

            // reset mNightOut
            mNightOut = new NightOut(getContext());
            mConsumptionAdapter = new ConsumptionListAdapter(mNightOut.getConsumptionsList());
            rvConsumptieLijst.setAdapter(mConsumptionAdapter);

            // reset TextViews
            tvNuchterValue.setText(getText(R.string.nightout_sober));
            tvNuchterLabel.setText(getText(R.string.nightout_add_consumption));
            tvDatum.setText(mNightOut.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
            tvEersteConsumptie.setText("");
            tvPromille.setText("");
            tvAantalGlazen.setText("");

            ivSaveButton.setVisibility(View.GONE);

        } else {
            tvNuchterLabel.setText(getText(R.string.nightout_sober_at));
            tvNuchterValue.setText(mNightOut.returnTimeSober().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
            tvDatum.setText(mNightOut.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
            tvEersteConsumptie.setText(mNightOut.getTimeFirstDrink().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
            tvPromille.setText(mNightOut.calculateBloodAlcoholContent() + " ‰");

            String sTotalUnits = Double.toString(mNightOut.getTotalUnits()) + " ";
            sTotalUnits += mNightOut.getTotalUnits() > 1 ? getText(R.string.consumption_units) : getText(R.string.consumption_unit);
            tvAantalGlazen.setText(sTotalUnits);

            ivSaveButton.setVisibility(View.VISIBLE);
        }
    }

    // instanceState
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // LOG
        logDebugInfo("Fragment onSaveInstanceState is called.");

        // save State
        super.onSaveInstanceState(outState);
        if (mNightOut.getAantalConsumpties() != 0) {
            outState.putParcelable(KEY_NIGHTOUT, mNightOut);
            if (mSavedState == null) mSavedState = new Bundle();
            mSavedState.putParcelable(KEY_NIGHTOUT, mNightOut);
        }
    }

    // sharedPreferences
    private void setupSharedPreferences() {
        // LOG
        logDebugInfo("setupSharedPreferences is called.");

        // load SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // LOG
        logDebugInfo("Fragment onSaveInstanceState is called.");

        String value;

        if (key.equals(getString(R.string.pref_weight_key))) {
            // LOG
            this.logDebugInfo("Weight preference changed.");

            value = sharedPreferences.getString(getString(R.string.pref_weight_key),
                    getString(R.string.pref_weight_default));
            mNightOut.setWeight(Integer.parseInt(value));
            savePreferenceChangesToDatabase("weight", value);

        } else if (key.equals(getString(R.string.pref_gender_key))) {
            // LOG
            this.logDebugInfo("Gender preference changed.");

            value = sharedPreferences.getString(getString(R.string.pref_gender_key),
                    getString(R.string.pref_gender_default));
            mNightOut.setGender(value);
            savePreferenceChangesToDatabase("gender", value);

        } else if (key.equals(getString(R.string.pref_name_key))) {
            // LOG
            this.logDebugInfo("Username preference changed.");

            value = sharedPreferences.getString(getString(R.string.pref_name_key),
                    getString(R.string.pref_name_default));
            MenuItem mItem = mOptionsMenu.getItem(0);
            mItem.setTitle("Hello " + value);
            savePreferenceChangesToDatabase("displayName", value);
        }

        // update NightOut layout
        updateTextViews();
    }

    public void savePreferenceChangesToDatabase(String key, String value) {
        Map<String, Object> updateValue = new HashMap<>();
        updateValue.put(key, value);
        DatabaseManager.updateUser(mUser.getUid(), updateValue);
    }

    // iv_save Method onSaveClicked
    public void onSaveClicked() {
        Log.d("MainFragment", "onSaveClicked method is called.");
       dbManager.saveNightOutToDatabase(mNightOut);

    }



    // drinksListFragment Listener Method
    @Override
    public void onDrinkClicked(int position) {
        // LOG
        logDebugInfo("OnDrinkClicked method is called");

        // add Consumption and update RecyclerView
        mNightOut.voegConsumptieToe(new Consumption(position, LocalDate.now(), LocalTime.now()));
        mConsumptionAdapter.notifyDataSetChanged();

        // update NightOut layout
        updateTextViews();

        // show Toast
        Toast.makeText(getContext(), new DrinkController().getDrink(position).toString(), Toast.LENGTH_SHORT).show();
    }


    // Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // LOG
        logDebugInfo("Activity onCreateOptionsMenu method is called.");

        mOptionsMenu = menu;

        MenuCompat.setGroupDividerEnabled(mOptionsMenu, true);
        inflater.inflate(R.menu.menu_main, mOptionsMenu);
        MenuItem mItem = mOptionsMenu.getItem(0);
        mItem.setTitle("Hello " + sharedPreferences.getString(getString(R.string.pref_name_key),
                getString(R.string.pref_name_default)));


        super.onCreateOptionsMenu(mOptionsMenu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // LOG
        logDebugInfo("Activity onOptionsItemSelected method is called.");

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(getContext(), PreferencesActivity.class);
            startActivity(settingsActivity);
            return true;
        } else if (id == R.id.action_history) {
            Intent historyActivity = new Intent(getContext(), HistoryListActivity.class);
            startActivity(historyActivity);
            return true;
        } else if (id == R.id.action_logout) {

            mAuth.signOut();
            AuthenticationManager.saveIntroPrefsData(getActivity());
            Intent introActivity = new Intent(getContext(), IntroActivity.class);
            startActivity(introActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }












    // debug Information
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // LOG
        logDebugInfo("Fragment onCreate method is called.");

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        // LOG
        logDebugInfo("Fragment onPause method is called.");

        super.onPause();
    }

    @Override
    public void onResume() {
        // LOG
        logDebugInfo("Fragment onResume method is called.");

        super.onResume();
    }

    @Override
    public void onDestroy() {
        // LOG
        logDebugInfo("Fragment onDestroy method is called.");

        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        // LOG
        logDebugInfo("Fragment onDestroyView method is called.");

        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // LOG
        logDebugInfo("Fragment onActivityCreated method is called.");

        super.onActivityCreated(savedInstanceState);
    }

    private void logDebugInfo(String message)
    {
        Log.d("MainFragment", message);
    }

}
