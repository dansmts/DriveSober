package be.pxl.drivesoberapp.utils;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

import be.pxl.drivesoberapp.R;
import be.pxl.drivesoberapp.models.Consumption;

public class ConsumptionListAdapter extends RecyclerView.Adapter<ConsumptionListAdapter.MyViewHolder> {

    private ArrayList<Consumption> mConsumptionList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDrankSoort;
        public TextView tvUurConsumptie;
        public TextView tvStandaardGlazen;
        public ImageView ivDrankIcoon;

        public MyViewHolder(ConstraintLayout cl) {
            super(cl);
            ivDrankIcoon = cl.findViewById(R.id.iv_drank_icoon);
            tvDrankSoort = cl.findViewById(R.id.tv_drank_soort);
            tvUurConsumptie = cl.findViewById(R.id.tv_uur_consumptie);
            tvStandaardGlazen = cl.findViewById(R.id.tv_standaard_glazen);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConsumptionListAdapter(ArrayList<Consumption> consumptionList) {
        mConsumptionList = consumptionList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConsumptionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_consumptionlist_item, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Resources res = holder.itemView.getContext().getResources();
        DrinkController dc = new DrinkController();

        try {
            String icoon = dc.getDrink(mConsumptionList.get(position).getDrinkId()).getIcon();
            holder.ivDrankIcoon.setImageResource(R.drawable.class.getField(icoon).getInt(null));
            holder.tvDrankSoort.setText(dc.getDrink(mConsumptionList.get(position).getDrinkId()).toString());
            holder.tvUurConsumptie.setText(mConsumptionList.get(position).getConsumptionTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));

            double drinkUnits = dc.getDrink(mConsumptionList.get(position).getDrinkId()).getUnits();
            String unitsOutput = drinkUnits + " " + (drinkUnits > 1 ? res.getString(R.string.consumption_units) : res.getString(R.string.consumption_unit));
            holder.tvStandaardGlazen.setText(unitsOutput);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mConsumptionList.size();
    }

    //
    public void deleteItem(int position) {
        mConsumptionList.remove(position);
        notifyItemRemoved(position);
    }
}