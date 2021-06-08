package com.example.allergydetect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allergydetect.Helpers.CloudFirestore;
import com.example.allergydetect.adapters.ListOfAllergensAdapter;
import com.example.allergydetect.adapters.UserAllergensAdapter;
import com.example.allergydetect.models.Allergen;

import java.util.ArrayList;
import java.util.List;

public class ListOfAllergens extends AppCompatActivity {

    private CloudFirestore db = LoginUser.getDb();

    private RecyclerView mRecyclerView;
    private ListOfAllergensAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Allergen> SelectedUsersAllergens;
    ArrayList<Allergen> DbAllergens;

    TextView allergensCount;
    Button btnSelectedAllergens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_list_of_allergens);

        allergensCount = findViewById(R.id.tvNumber);

        btnSelectedAllergens = findViewById(R.id.btnSelectedAllergens);
        btnSelectedAllergens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allergensCount.setText(String.valueOf(0));
                Intent intent = new Intent(ListOfAllergens.this, UserActivity.class);
                for(Allergen allergen : SelectedUsersAllergens){
                    db.PostUsersAllergen(allergen);
                }
                startActivity(intent);
            }
        });

        SelectedUsersAllergens = new ArrayList<Allergen>();
        DbAllergens = new ArrayList<Allergen>();

        DbAllergens = db.GetAll();
//        Bundle bundle = getIntent().getBundleExtra("bundle");
//        for (Allergen allergen : (ArrayList<Allergen>) bundle.getSerializable("allergens")){
//            if(DbAllergens.contains(allergen)){
//                DbAllergens.remove(allergen);
//            }
//        }

        mRecyclerView = findViewById(R.id.rvAddAllergens);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(ListOfAllergens.this);
        mAdapter = new ListOfAllergensAdapter(DbAllergens, ListOfAllergens.this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnAddClickListener(new ListOfAllergensAdapter.OnAddClickListener() {
            @Override
            public void onAddClick(int position) {
                SelectedUsersAllergens.add(DbAllergens.get(position));
                //DbAllergens.remove(position);
                mAdapter.notifyItemRemoved(position);
                int count = Integer.parseInt(allergensCount.getText().toString());
                count++;
                allergensCount.setText(String.valueOf(count));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.nav_searches);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}