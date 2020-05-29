package com.example.whatsthemove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private LinearLayoutManager mllManager;

    private List<String> barNames = new ArrayList<>();
    private List<Integer> barPics = new ArrayList<>();
    private List<String> selectedBarNames = new ArrayList<>();
    private List<Integer> selectedBarPics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("whatsthemove", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        setContentView(R.layout.activity_filter);
        addToArrays();
        mllManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.filterRecycleView);
        recyclerView.setLayoutManager(mllManager);
        mAdapter = new MyAdapter(this, barNames, barPics, selectedBarNames, selectedBarPics);
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mllManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(FilterActivity.this, R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addToArrays() {

        barNames.add("Chasers Bar & Grille");
        barPics.add(R.drawable.chasers);

        barNames.add("The Double U");
        barPics.add(R.drawable.doubleu);

        barNames.add("The Kollege Klub");
        barPics.add(R.drawable.kklub);

        barNames.add("Mondays");
        barPics.add(R.drawable.mondays);

        barNames.add("Whisky Jacks Saloon");
        barPics.add(R.drawable.whiskyjacks);

        barNames.add("The Karaoke Kid");
        barPics.add(R.drawable.kkid);
    }

}
