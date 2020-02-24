package be.pxl.drivesoberapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import be.pxl.drivesoberapp.R;
import be.pxl.drivesoberapp.models.Consumption;
import be.pxl.drivesoberapp.models.NightOut;
import be.pxl.drivesoberapp.models.User;

public class DatabaseManager {
    private static final String TAG_DATABASEMANAGER = "DATABASEMANAGER";
    private FirebaseFirestore db;

    public DatabaseManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    // save user to Firestore
    public static void saveUser(String UserUID, User user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UserUID).set(user);
    }

    public static void updateUser(String UserUID, Map<String, Object> updateValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UserUID).update(updateValue);
    }

    public static void getUserData(String UserUID, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(UserUID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Resources res = context.getResources();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(res.getString(R.string.pref_weight_key), String.valueOf(user.getWeight()));
                editor.putString(res.getString(R.string.pref_name_key), user.getDisplayName());
                editor.putString(res.getString(R.string.pref_gender_key), user.getGender());
                editor.apply();
            }
        });
    }

    // save to FireStore
    public static void saveNightOutToDatabase(String userUID, NightOut nightOut) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (nightOut.getId() == null) {
            DocumentReference newNightOutRef = db.collection(userUID).document();
            nightOut.setId(newNightOutRef.getId());
        }

        Map<String, Object> nightOutMap = createNightOutMap(nightOut);

        DocumentReference nightOutRef = db.collection(userUID).document(nightOut.getId());
        nightOutRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();

                if (!document.exists()) {
                    Log.d(TAG_DATABASEMANAGER,"NightOut is not in DB -> Create New");
                    nightOutRef.set(nightOutMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void onSuccess(Void aVoid){
                                    Log.d(TAG_DATABASEMANAGER,"Document succesfully created.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener(){
                                @Override
                                public void onFailure(@NonNull Exception e){
                                    Log.d(TAG_DATABASEMANAGER,"Error creating document", e);
                                }
                            });
                } else {
                    Log.d(TAG_DATABASEMANAGER,"NightOut is already in DB -> Update");
                    nightOutRef.update(nightOutMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void  onSuccess(Void aVoid){
                                    Log.d(TAG_DATABASEMANAGER,"Document sucessfully updated");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener(){
                                @Override
                                public void onFailure(@NonNull Exception e){
                                    Log.d(TAG_DATABASEMANAGER,"Error creating document", e);
                                }
                            });
                }
            } else {
                Log.d(TAG_DATABASEMANAGER, "Failed with: ", task.getException());
            }
        });
    }

    private static Map<String, Object> createNightOutMap(NightOut nightOut) {

        Map<String, Object> nightOutMap = new HashMap<>();
        nightOutMap.put("date", nightOut.getDate().toString());
        nightOutMap.put("timeFirstDrink", nightOut.getTimeFirstDrink().toString());
        nightOutMap.put("maxBAC", nightOut.getMaxBAC());
        nightOutMap.put("gender", nightOut.getGender());
        nightOutMap.put("weight", nightOut.getWeight());

        Map<String, Object> consumptionsListMap = new HashMap<>();
        for (int i = 0; i < nightOut.getConsumptionsList().size(); i++) {

            Consumption c = nightOut.getConsumptionsList().get(i);

            Map<String, Object> consumptionMap = new HashMap<>();
            consumptionMap.put("drinkId", String.valueOf(c.getDrinkId()));
            consumptionMap.put("date", c.getConsumptionDate().toString());
            consumptionMap.put("time", c.getConsumptionTime().toString());

            consumptionsListMap.put(String.valueOf(i), consumptionMap);
        }
        nightOutMap.put("consumptions", consumptionsListMap);

        return nightOutMap;
    }

    // read from FireStore
    public ArrayList<NightOut> getNightOutsFromDatabase(String userUID) {

        ArrayList<NightOut> nightOutArrayList = new ArrayList<>();

        db.collection(userUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                nightOutArrayList.add(createNewNightOut(document));
                                Log.d(TAG_DATABASEMANAGER, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG_DATABASEMANAGER, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return nightOutArrayList;
    }

    public static NightOut readNightOut(String userUID, String nightOutID) {
        final NightOut[] returnNightOut = new NightOut[1];
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(userUID).document(nightOutID);
        docRef.get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    returnNightOut[0] = createNewNightOut(doc);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        return returnNightOut[0];
    }

    public static NightOut createNewNightOut(DocumentSnapshot document) {
        NightOut nightOut = new NightOut();

        nightOut.setId((String) document.getId());
        nightOut.setDate(LocalDate.parse((String) document.get("date")));
        nightOut.setTimeFirstDrink(LocalTime.parse((String) document.get("timeFirstDrink")));
        document.getData().get("consumptions");
        Map<String, Object> consumptionsMap = (Map<String, Object>) document.getData().get("consumptions");
        for (Map.Entry<String, Object> e : consumptionsMap.entrySet()) {
            Map<String, Object> consumption = (Map<String, Object>) e.getValue();
            Consumption c = new Consumption(
                    Integer.parseInt((String) consumption.get("drinkId")),
                    LocalDate.parse((String) consumption.get("date")),
                    LocalTime.parse((String) consumption.get("time"))
            );
            nightOut.voegConsumptieToe(c);
        }
        nightOut.setMaxBAC(document.getDouble("maxBAC"));

        nightOut.setGender((String) document.get("gender"));
        nightOut.setWeight(Math.toIntExact(document.getLong("weight")));

        return nightOut;
    }
}
