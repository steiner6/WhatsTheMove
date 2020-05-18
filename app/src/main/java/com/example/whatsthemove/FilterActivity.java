package com.example.whatsthemove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FilterActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;

    private String[] barNames = new String[4];
    private Integer[] barPics = new Integer[4];


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

        barNames[0] = "Chasers Bar & Grille";
        barPics[0] = R.drawable.chasers;

        barNames[1] = "The Double U";
        barPics[1] = R.drawable.doubleu;

        barNames[2] = "The Kollege Klub";
        barPics[2] = R.drawable.kklub;

        barNames[3] = "Mondays";
        barPics[3] = R.drawable.mondays;
    }

}
