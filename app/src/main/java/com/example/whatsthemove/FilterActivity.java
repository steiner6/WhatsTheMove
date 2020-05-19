package com.example.whatsthemove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;

    private List<String> barNames = new ArrayList<>();
    private List<Integer> barPics = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        addToArrays();
        recyclerView = findViewById(R.id.filterRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter(this, barNames, barPics);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        int x;
    }

    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addToArrays() {

        barNames.add("Chasers Bar & Grille");
        //barPics.add(R.drawable.chasers);

        barNames.add("The Double U");
        //barPics.add(R.drawable.doubleu);

        barNames.add("The Kollege Klub");
        //barPics.add(R.drawable.kklub);

        barNames.add("Mondays");
        //barPics.add(R.drawable.mondays);
    }

}
