package com.example.bimvendpro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MachineIngredientsFragment extends Fragment {
    private RecyclerView machineRecyclerView;

    private List<MachineIngredients> itemList = new ArrayList<>();
    private MachineIngredientsAdapter mAdapter;
    private String code;


    private SearchView searchViewExpense;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noIngredientsTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            code = bundle.getString("code");
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout = view.findViewById(R.id.refreshingLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readDataFromFirebase();
            }
        });


        searchViewExpense = view.findViewById(R.id.ingredient_search);
        searchViewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewExpense.setIconified(false);
            }
        });
        searchViewExpense.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });



        initializeRecyclerView();

        readDataFromFirebase();

        swipeRefreshLayout.setRefreshing(true);

        noIngredientsTextView = view.findViewById(R.id.noIngredentsMsg);


    }


    private void initializeRecyclerView() {
        machineRecyclerView = getView().findViewById(R.id.ingredientsRecyclerView);

        mAdapter = new MachineIngredientsAdapter(itemList, getContext(), code);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        machineRecyclerView.setLayoutManager(mLayoutManager);
        machineRecyclerView.setItemAnimator(new DefaultItemAnimator());
        machineRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ingredients, container, false);
    }


    public void readDataFromFirebase() {
        FirebaseUtilClass.getDatabaseReference().child("Machine").child("Items").child(code).child("machineIngredients").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    itemList.add(dsp.getValue(MachineIngredients.class)); //add result into array list
                }
                if (itemList.size() == 0) {
                    noIngredientsTextView.setVisibility(View.VISIBLE);
                } else {
                    noIngredientsTextView.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
