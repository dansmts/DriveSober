package be.pxl.drivesoberapp.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import be.pxl.drivesoberapp.R;
import be.pxl.drivesoberapp.models.Drink;

public class DrinksListDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_DRINKSLIST = "drinksList";
    private static DrinkController drinkController;
    private Listener mListener;

    public static DrinksListDialogFragment newInstance() {
        final DrinksListDialogFragment fragment = new DrinksListDialogFragment();
        drinkController = new DrinkController();

        final Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_DRINKSLIST, drinkController.getDrinksList());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drinkslist_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new DrinksAdapter(getArguments().<Drink>getParcelableArrayList(ARG_DRINKSLIST)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onDrinkClicked(int position);
    }


// Adapter
    private class DrinksAdapter extends RecyclerView.Adapter<DrinksAdapter.ViewHolder> {

        private ArrayList<Drink> mDrinksList;

// ViewHolder
        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            ViewHolder(LinearLayout ll) {
                super(ll);
                textView = ll.findViewById(R.id.tv_drank_item);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onDrinkClicked(getAdapterPosition());
                            dismiss();
                        }
                    }
                });
            }
        }

        DrinksAdapter(ArrayList<Drink> drinksList) {
            mDrinksList = drinksList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder((LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_drinkslist_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(mDrinksList.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return mDrinksList.size();
        }
    }

}
