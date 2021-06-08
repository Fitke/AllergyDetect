package com.example.allergydetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allergydetect.Helpers.CloudFirestore;
import com.example.allergydetect.adapters.UserAllergensAdapter;
import com.example.allergydetect.models.Allergen;
import com.example.allergydetect.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    CollectionReference UsersCollection;
    FirebaseUser firebaseUser;
    FirebaseAuth fAuth;
    private  static final String UsersCollectionName = "Users";
    private  static final String UsersSubCollection = "User Allergies";

    private ArrayList<Allergen> UsersAllergens;

    private FirebaseAuth mAuth;
    private Button btnAddAllergens, btnLogOut, ibMainActivity;

    private RecyclerView mRecyclerView;
    private UserAllergensAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private CloudFirestore db = LoginUser.getDb();
    private FirebaseFirestore ffDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        UsersCollection = ffDB.collection(UsersCollectionName);

        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();

       // UsersAllergens = db.GetAllUsersAllergies();

        CreateRecyclerView();

        mAuth = FirebaseAuth.getInstance();

        ibMainActivity = findViewById(R.id.ibMainActivity);


        btnAddAllergens = findViewById(R.id.btnAddAllergens);
        btnLogOut = findViewById(R.id.btnLogOut);


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                mAuth.signOut();

                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                Toast.makeText(UserActivity.this, "Logging out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UserActivity.this, LoginUser.class);
                startActivity(intent);
            }
        });

        btnAddAllergens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, ListOfAllergens.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("allergens", (Serializable) UsersAllergens);
//                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });
        ibMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void CreateRecyclerView() {
        UsersCollection
                .document(firebaseUser.getUid())
                .collection(UsersSubCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Allergen> temp = new ArrayList<Allergen>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                Allergen allergen = new Allergen(documentSnapshot.getId(), documentSnapshot.getString("allergy name"));
                                temp.add(allergen);
                            }

                                UsersAllergens = temp;

                                mRecyclerView = findViewById(R.id.rvAllergens);
                                mRecyclerView.setHasFixedSize(true);

                                mLayoutManager = new LinearLayoutManager(UserActivity.this);
                                mAdapter = new UserAllergensAdapter(UsersAllergens, UserActivity.this);

                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mRecyclerView.setAdapter(mAdapter);

                                mAdapter.setOnItemClickListener(new UserAllergensAdapter.OnItemClickListener() {
                                @Override
                                public void onDeleteClick(int position) {
                                    removeItem(position);
                                }
                            });
                        } else {
                            Log.d("UserActivity", "Error getting Users allergies: ", task.getException());
                        }
                    }
                });
    }

    public void removeItem(int position){
        db.DeleteUsersAllergen(UsersAllergens.get(position).getAllergenCode());
        UsersAllergens.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

}