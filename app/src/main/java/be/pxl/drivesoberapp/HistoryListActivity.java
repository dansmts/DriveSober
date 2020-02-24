package be.pxl.drivesoberapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;

import be.pxl.drivesoberapp.models.NightOut;
import be.pxl.drivesoberapp.utils.DatabaseManager;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class HistoryListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private DatabaseManager dbManager;
    private ArrayList<NightOut> nightOutArrayList;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        nightOutArrayList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);

            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.historynightoutitem_detail_container) != null) {
            mTwoPane = true;
        }

        // load RecyclerView
        RecyclerView rvConsumptieLijst = findViewById(R.id.historynightoutitem_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvConsumptieLijst.setLayoutManager(layoutManager);
        SimpleGeschiedenisListRecyclerViewAdapter geschiedenisListRecyclerViewAdapter = new SimpleGeschiedenisListRecyclerViewAdapter(nightOutArrayList, mTwoPane, this);
        rvConsumptieLijst.setAdapter(geschiedenisListRecyclerViewAdapter);


        Task<QuerySnapshot> task =
                FirebaseFirestore.getInstance().collection(mUser.getUid()).get();

        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                      @Override
                                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                          List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                          for (DocumentSnapshot d : documentSnapshots) {
                                              nightOutArrayList.add(DatabaseManager.createNewNightOut(d));
                                              geschiedenisListRecyclerViewAdapter.notifyDataSetChanged();
                                          }

                                      }
                                  });
        task.addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                // handle any errors here
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class SimpleGeschiedenisListRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleGeschiedenisListRecyclerViewAdapter.ViewHolder> {

        private ArrayList<NightOut> mDataset;
        private boolean mTwoPane;
        private final HistoryListActivity mParentActivity;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NightOut selectedNightOut = (NightOut) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(HistoryDetailFragment.ARG_ITEM_ID, selectedNightOut);
                    HistoryDetailFragment fragment = new HistoryDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.historynightoutitem_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, HistoryDetailActivity.class);
                    intent.putExtra(HistoryDetailFragment.ARG_ITEM_ID, selectedNightOut);

                    context.startActivity(intent);
                }
            }
        };

        SimpleGeschiedenisListRecyclerViewAdapter(ArrayList<NightOut> dataset, boolean twoPane, HistoryListActivity parentActivity) {
            mDataset = dataset;
            mTwoPane = twoPane;
            mParentActivity = parentActivity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_historylist_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            String icoon = mDataset.get(position).getDrinkIcon();
            try {
                holder.ivDrankIcoon.setImageResource(R.drawable.class.getField(icoon).getInt(null));
                holder.tvDatum.setText(mDataset.get(position).getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                holder.tvPromille.setText(Double.toString(mDataset.get(position).getMaxBAC()));
                holder.tvStandaardGlazen.setText(Double.toString(mDataset.get(position).getTotalUnits()));

            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

            holder. itemView.setTag(mDataset.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView ivDrankIcoon;
            final TextView tvDatum;
            final TextView tvPromille;
            final TextView tvStandaardGlazen;

            ViewHolder(View view) {
                super(view);
                ivDrankIcoon = view.findViewById(R.id.iv_drank_icoon);
                tvDatum = view.findViewById(R.id.tv_datum);
                tvPromille = view.findViewById(R.id.tv_promille);
                tvStandaardGlazen = view.findViewById(R.id.tv_standaard_glazen);
            }
        }
    }
}
