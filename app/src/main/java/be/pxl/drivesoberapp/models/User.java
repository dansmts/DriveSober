package be.pxl.drivesoberapp.models;


public class User {

    private String displayName;
    private String gender;
    private String weight;

    public User () {}

    public User(String displayName, String gender, String weight) {
        this.displayName = displayName;
        this.gender = gender;
        this.weight = weight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGender() { return gender; }

    public String getWeight() { return weight; }
}
