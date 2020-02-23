package be.pxl.drivesoberapp.utils;

import java.util.ArrayList;

import be.pxl.drivesoberapp.models.Drink;

public class DrinkController {
    private ArrayList<Drink> drinksList;

    public DrinkController() {
        drinksList = new ArrayList<>();
        drinksList.add(new Drink(1, "Glas bier", 25, 5, 1, "ic_drink_pint_small"));
        drinksList.add(new Drink(2, "Glas bier", 50, 5, 1, "ic_drink_pint_large"));
        drinksList.add(new Drink(3, "Blik bier", 33, 5, 1, "ic_drink_beer_can"));
        drinksList.add(new Drink(4, "Glas wijn", 10, 12, 1, "ic_drink_wine_glass"));
        drinksList.add(new Drink(5, "Cocktail", 25, 5, 1, "ic_drink_cocktail"));
        drinksList.add(new Drink(6, "Glas champagne", 25, 5, 1, "ic_drink_champagne"));
        drinksList.add(new Drink(7, "Glas aperitief", 25, 5, 1, "ic_drink_sherry"));
        drinksList.add(new Drink(8, "Glas sterke drank", 25, 5, 1, "ic_drink_strong_alcohol"));
    }

    public ArrayList<Drink> getDrinksList() { return drinksList; }
    public void setDrinksList(ArrayList<Drink> drinksList) { this.drinksList = drinksList; }

    public Drink getDrink(int drinkId) {
        return drinksList.get(drinkId);
    }

}
