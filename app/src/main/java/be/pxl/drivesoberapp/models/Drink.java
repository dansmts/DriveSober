package be.pxl.drivesoberapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Drink implements Parcelable {
    private int id;
    private String name;
    private double sizeInCentiliter;
    private double ABV;
    private double units;
    private String icon;


    public Drink(int id, String name, double sizeInCentiliter, double ABV, double units, String icon) {
        this.id = id;
        this.name = name;
        this.sizeInCentiliter = sizeInCentiliter;
        this.ABV = ABV;
        this.units = units;
        this.icon = icon;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSizeInCentiliter() { return sizeInCentiliter; }
    public void setSizeInCentiliter(double sizeInCentiliter) { this.sizeInCentiliter = sizeInCentiliter; }

    public double getABV() { return ABV; }
    public void setABV(double ABV) { this.ABV = ABV; }

    public double getUnits() { return units; }
    public void setUnits(double units) { this.units = units; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    @Override
    public String toString() {
        return getName() + " " + getSizeInCentiliter() + " cl - " + getABV() + "%";
    }


    // Parcelling part
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Drink createFromParcel(Parcel in) { return new Drink(in); }

        @Override
        public Drink[] newArray(int size) { return new Drink[0]; }
    };
    public Drink(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.sizeInCentiliter = in.readDouble();
        this.ABV = in.readDouble();
        this.units = in.readDouble();
        this.icon = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.sizeInCentiliter);
        dest.writeDouble(this.ABV);
        dest.writeDouble(this.units);
        dest.writeString(this.icon);
    }
}
