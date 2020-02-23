package be.pxl.drivesoberapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.preference.PreferenceManager;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import be.pxl.drivesoberapp.R;
import be.pxl.drivesoberapp.utils.DrinkController;

import static java.time.temporal.ChronoUnit.MINUTES;

@IgnoreExtraProperties
public class NightOut implements Parcelable {
    private String id;
    private LocalDate date;
    private LocalTime timeFirstDrink;
    private ArrayList<Consumption> consumptionsList;
    private double maxBAC;
    private String gender;
    private int weight;

    public NightOut() {
        this.consumptionsList = new ArrayList<>();
    }

    public NightOut(Context context) {
        this.date = LocalDate.now();
        this.consumptionsList = new ArrayList<>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.weight = Integer.parseInt(sp.getString(context.getString(R.string.pref_weight_key),
                context.getString(R.string.pref_weight_default)));
        this.gender = sp.getString(context.getString(R.string.pref_gender_key),
                context.getString(R.string.pref_gender_default));
    }

    public NightOut(String id, LocalDate date, LocalTime timeFirstDrink, ArrayList<Consumption> consumptionsList, double maxBAC, String gender, int weight) {
        this.id = id;
        this.date = date;
        this.timeFirstDrink = timeFirstDrink;
        this.consumptionsList = consumptionsList;
        this.maxBAC = maxBAC;
        this.gender = gender;
        this.weight = weight;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTimeFirstDrink() { return timeFirstDrink; }
    public void setTimeFirstDrink(LocalTime timeFirstDrink) { this.timeFirstDrink = timeFirstDrink; }

    public ArrayList<Consumption> getConsumptionsList() { return consumptionsList; }
    public void setConsumptionsList(ArrayList<Consumption> consumptionsList) { this.consumptionsList = consumptionsList; }

    public double getMaxBAC() { return maxBAC; }
    public void setMaxBAC(double maxBAC) { this.maxBAC = maxBAC; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public void voegConsumptieToe(Consumption consumption) {
        if (this.timeFirstDrink == null) {
            this.timeFirstDrink = consumption.getConsumptionTime();
        }
        this.consumptionsList.add(0, consumption);
    }

    public int getAantalConsumpties() {
        return this.consumptionsList.size();
    }

    public double calculateBloodAlcoholContent() {
        // Formule BAG
        // BAG = (a x 10)/(g x r) - (u - 0,5) x (g x 0,002)
        // Waarbij:
        // a = aantal glazen
        // g = lichaamsgewicht (kg)
        // r = bij mannen 0,7 en bij vrouwen 0,5
        // u = het aantal uren vanaf het eerste glas
        if (getAantalConsumpties() != 0) {
            float vochtPerKilo = (float) (gender.equals("man") ? 0.7 : 0.5);

            long minutenTussenEersteEnLaatsteConsumptie = MINUTES.between(this.getTimeFirstDrink(), LocalTime.now());
            double urenVanafEersteGlas = Math.ceil(minutenTussenEersteEnLaatsteConsumptie / 60);
            double BAC = (getTotalUnits() * 10) / (this.weight * vochtPerKilo) - (urenVanafEersteGlas - 0.5) * (this.weight * 0.002);
            BAC = Double.parseDouble(new DecimalFormat("##.###").format(BAC));
            if (BAC > 0) {
                if (BAC > maxBAC) { maxBAC = BAC; }
                return BAC;
            }
        }
        return 0;
    }

    public double calculateAlcoholBreakdownSpeed() {
        // afbraaksnelheid = 0,002 x g x g x r
        // Waarbij:
        // g = lichaamsgewicht (kg)
        // f = bij mannen 0,71 en bij vrouwen 0,62

        float factorGender = (float) (gender.equals("man") ? 0.71 : 0.62);
        double ABS = 0.002 * this.weight * this.weight * factorGender;
        return Double.parseDouble(new DecimalFormat("##.###").format(ABS));
    }

    public LocalTime returnTimeSober() {
        double gramAlcohol = getTotalUnits() * 10;
        double AAS = calculateAlcoholBreakdownSpeed();

        long afbraakTijdInMinuten = Math.round(gramAlcohol / AAS * 60);
        return getTimeFirstDrink().plusMinutes(afbraakTijdInMinuten);
    }

    public double getTotalUnits() {
        double totalUnits = 0;
        DrinkController dc = new DrinkController();

        for (Consumption c : consumptionsList) {
            totalUnits += dc.getDrink(c.getDrinkId()).getUnits();
        }

        return totalUnits;
    }

    public String getDrinkIcon() {
        Map<Integer, Integer> map = new HashMap<>();

        for (Consumption c : getConsumptionsList()) {
            Integer val = map.get(c.getDrinkId());
            map.put(c.getDrinkId(), val == null ? 1 : val + 1);
        }

        Map.Entry<Integer, Integer> max = null;

        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        int drinkId =  max.getKey();
        return new DrinkController().getDrink(drinkId).getIcon();
    }


// Parcelling part
    public static final Creator CREATOR = new Creator() {
        public NightOut createFromParcel(Parcel in) { return new NightOut(in); }

        @Override
        public NightOut[] newArray(int size) { return new NightOut[0]; }
    };

    public NightOut(Parcel in){
        this.id = in.readString();
        this.date = LocalDate.parse(in.readString());
        this.timeFirstDrink = LocalTime.parse(in.readString());
        this.consumptionsList = in.readArrayList(Consumption.class.getClassLoader());
        this.maxBAC = in.readDouble();
        this.gender = in.readString();
        this.weight = in.readInt();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id) ;
        dest.writeString(this.date.toString());
        dest.writeString(this.timeFirstDrink.toString());
        dest.writeList(this.consumptionsList);
        dest.writeDouble(this.maxBAC);
        dest.writeString(this.gender);
        dest.writeInt(this.weight);
    }
}
