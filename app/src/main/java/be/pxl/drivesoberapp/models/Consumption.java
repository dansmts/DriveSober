package be.pxl.drivesoberapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consumption implements Parcelable {
    private int drinkId;
    private LocalDate consumptionDate;
    private LocalTime consumptionTime;

    public Consumption(int drinkId, LocalDate consumptionDate, LocalTime consumptionTime) {
        this.drinkId = drinkId;
        this.consumptionDate = consumptionDate;
        this.consumptionTime = consumptionTime;
    }

    public int getDrinkId() { return drinkId; }
    public void setDrinkId(int drinkId) { this.drinkId = drinkId; }

    public LocalDate getConsumptionDate() { return consumptionDate; }
    public void setConsumptionDate(LocalDate consumptionDate) { this.consumptionDate = consumptionDate; }

    public LocalTime getConsumptionTime() { return consumptionTime; }
    public void setConsumptionTime(LocalTime consumptionTime) { this.consumptionTime = consumptionTime; }


// Parcelling part
    public static final Creator CREATOR = new Creator() {
        public Consumption createFromParcel(Parcel in) { return new Consumption(in); }

        @Override
        public Consumption[] newArray(int size) { return new Consumption[0]; }
    };

    public Consumption(Parcel in){
        this.drinkId = in.readInt();
        this.consumptionDate = LocalDate.parse(in.readString());
        this.consumptionTime = LocalTime.parse(in.readString());
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.drinkId);
        dest.writeString(this.consumptionDate.toString());
        dest.writeString(this.consumptionTime.toString());
    }
}
