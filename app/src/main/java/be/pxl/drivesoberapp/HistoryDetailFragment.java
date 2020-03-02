package be.pxl.drivesoberapp;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import be.pxl.drivesoberapp.models.NightOut;

public class HistoryDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private NightOut mNightOut;

    public HistoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
           // mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mNightOut = (NightOut) getArguments().getParcelable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
             //   appBarLayout.setTitle(mItem.content);
            }
        }
    }

    public String getDrunkFaseIcoon() {
        double maxPromille = mNightOut.getMaxBAC();
        if (maxPromille < 0.5) { return getString(R.string.ic_drunk_fase_1); }
        else if (maxPromille < 1.5) { return getString(R.string.ic_drunk_fase_2); }
        else if (maxPromille < 3) { return getString(R.string.ic_drunk_fase_3); }
        else if (maxPromille < 4) { return getString(R.string.ic_drunk_fase_4); }
        else if (maxPromille < 5) { return getString(R.string.ic_drunk_fase_5); }
        else { return getString(R.string.ic_drunk_fase_6); }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_detail, container, false);

        if (mNightOut != null) {

            try {
                ((TextView) rootView.findViewById(R.id.tv_bac_value)).setText(String.valueOf(mNightOut.getMaxBAC()));
                ((TextView) rootView.findViewById(R.id.tv_units_value)).setText(Double.toString(mNightOut.getTotalUnits()));
                ((TextView) rootView.findViewById(R.id.tv_date_value)).setText(mNightOut.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                ((TextView) rootView.findViewById(R.id.tv_firstdrink_value)).setText(mNightOut.getTimeFirstDrink().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                ((ImageView) rootView.findViewById(R.id.iv_drunk_fase)).setImageResource(R.drawable.class.getField(getDrunkFaseIcoon()).getInt(null));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        return rootView;
    }
}
