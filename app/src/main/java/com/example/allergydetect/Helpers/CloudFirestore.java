package com.example.allergydetect.Helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.allergydetect.models.Allergen;
import com.example.allergydetect.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CloudFirestore {

    private  static final String CollectionName = "Allergies";
    private  static final String UsersCollectionName = "Users";
    private  static final String UsersSubCollection = "User Allergies";
    private static final String TAG  = "Allergy";

    //Database
    FirebaseFirestore db;
    CollectionReference allergiesCollection;
    CollectionReference UsersCollection;

    //Authentication
    FirebaseUser firebaseUser;
    FirebaseAuth fAuth;

    //variables for return
    ArrayList<Allergen> AllergyNames;
    ArrayList<String> UsersAllergies;
    String AllergyName;
    User user;

    public CloudFirestore(boolean noUser){

        AllergyNames = new ArrayList<>();
        UsersAllergies = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        allergiesCollection = db.collection(CollectionName);
        UsersCollection = db.collection(UsersCollectionName);

        if(!noUser) {
            fAuth = FirebaseAuth.getInstance();
            firebaseUser = fAuth.getCurrentUser();
            user = new User();
            GetAllUserAllergies();
        }

        GetAllAllergies();

    }

    public boolean PostUser(User regUser){
        return UsersCollection.document(regUser.getUid()).set(regUser).isSuccessful();
    }

    public boolean DeleteUsersAllergen(String AllergenCode){
        boolean success = UsersCollection
                .document(firebaseUser.getUid())
                .collection(UsersSubCollection)
                .document(AllergenCode)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Failed to delete allergen:" + e.getMessage());
            }
        }).isSuccessful();
        return success;
    }

    public boolean PostUsersAllergen(Allergen userAllergen){

       Map<String, Object> allergen = new HashMap<>();
        allergen.put("allergy name", userAllergen.getAllergenName());
        boolean success = UsersCollection.document(firebaseUser.getUid())
                .collection(UsersSubCollection)
                .document(userAllergen.getAllergenCode())
                .set(allergen)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Users allergens have been posted to DB");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Users allergens have not been posted: " + e.getMessage());
            }
        }).isSuccessful();
        return success;
    }

    public Allergen Get(Allergen allergyName) {
        return AllergyNames.get(AllergyNames.indexOf(allergyName));
    }
    public ArrayList<String> GetAllUsersAllergies() {
        return UsersAllergies;
    }

    public String GetUsersAllergy(String allergyName) {
        return UsersAllergies.get(UsersAllergies.indexOf(allergyName));
    }

    public ArrayList<Allergen> GetAll(){
        return AllergyNames;
    }


    private void GetAllergy(String allergyName){
        db.collection(CollectionName)
                .whereEqualTo("allergy name", allergyName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AllergyName = document.getString("allergy name");

                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //temp = document.getData().get("allergy name").toString();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void GetSubCollection(String docName, String subCollection){
        db.collection(CollectionName)
                .document(docName)
                .collection(subCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        //UsersAllergies.add(documentSnapshot.getString("allergy name"));
                        Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void GetAllUserAllergies() {
        UsersCollection
                .document(firebaseUser.getUid())
                .collection(UsersSubCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<String> temp = new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                //GetSubCollection(documentSnapshot.getId(), UsersSubCollection);
                                temp.add(documentSnapshot.getString("allergy name"));
                            }

                            UsersAllergies = temp;

                        } else {
                            Log.d(TAG, "Error getting Users allergies: ", task.getException());
                        }
                    }
                });
    }
    public void GetAllAllergies(){
        db.collection(CollectionName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //querySnapshot = task.getResult();
                    ArrayList<Allergen> temp = new ArrayList<Allergen>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Allergen allergen = new Allergen(documentSnapshot.getId(), documentSnapshot.getString("allergy name"));
                        temp.add(allergen);
                        Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                    }

                    AllergyNames = temp;
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    /*public void GettAllAllergies(){
        db.coll
        db.collection(CollectionName).get();
    }*/
    public FirebaseFirestore getDb() {
        return db;
    }
}
