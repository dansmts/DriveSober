package be.pxl.drivesoberapp.utils;

import android.content.Context;
import android.content.res.Resources;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

import be.pxl.drivesoberapp.R;
import be.pxl.drivesoberapp.models.Drink;

public class DrinkController {
    private ArrayList<Drink> drinksList;

    public DrinkController() {
        Resources res = GlobalApplication.getAppContext().getResources();

        drinksList = new ArrayList<>();
        drinksList.add(new Drink(1, res.getString(R.string.con_beer_pint), 25, 5, 1, "ic_drink_beer_bottle"));
        drinksList.add(new Drink(2, res.getString(R.string.con_beer_pint), 50, 5, 2, "ic_drink_pint_large"));
        drinksList.add(new Drink(3, res.getString(R.string.con_beer_can), 33, 5, 1.3, "ic_drink_beer_can"));
        drinksList.add(new Drink(4, res.getString(R.string.con_wine_glass), 10, 12, 1, "ic_drink_wine_glass"));
        drinksList.add(new Drink(5, res.getString(R.string.con_beer_special), 33, 6.5, 1.7, "ic_drink_pint_small"));
        drinksList.add(new Drink(5, res.getString(R.string.con_beer_special), 33, 8, 2, "ic_drink_pint_small"));
        drinksList.add(new Drink(5, res.getString(R.string.con_mix_drink), 27.5, 5.6, 1.25, "ic_drink_cocktail"));
        drinksList.add(new Drink(6, res.getString(R.string.con_champagne), 12.5, 5, 1, "ic_drink_champagne"));
        drinksList.add(new Drink(7, res.getString(R.string.con_spirit_glass), 3.5, 35, 1, "ic_drink_strong_alcohol"));
        drinksList.add(new Drink(5, res.getString(R.string.con_wine_bottle), 75, 12, 7, "ic_drink_wine_bottle"));
        drinksList.add(new Drink(8, res.getString(R.string.con_spirit_bottle), 75, 40, 25, "ic_drink_whisky_bottle"));
    }

    public ArrayList<Drink> getDrinksList() { return drinksList; }
    public void setDrinksList(ArrayList<Drink> drinksList) { this.drinksList = drinksList; }

    public Drink getDrink(int drinkId) {
        return drinksList.get(drinkId);
    }

}
