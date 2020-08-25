package com.example.whatsthemove;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BouncerActivity extends AppCompatActivity {

    private RecyclerView bounceRecyclerView;
    private BouncerAdapter bounceAdapter;
    private LinearLayoutManager bounceManager;

    private List<String> barnames = new ArrayList<>();
    private List<Integer> barpics = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouncer);

        addToArrays();

        bounceManager = new LinearLayoutManager(this);
        bounceRecyclerView = findViewById(R.id.bouncerRecycleView);
        bounceRecyclerView.setLayoutManager(bounceManager);
        bounceAdapter = new BouncerAdapter(this, barnames, barpics);
        bounceRecyclerView.setAdapter(bounceAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(bounceRecyclerView.getContext(),
                bounceManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        bounceRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addToArrays() {

        barnames.add("Chasers Bar & Grille");
        barpics.add(R.drawable.chasers);

        barnames.add("The Double U");
        barpics.add(R.drawable.doubleu);

        barnames.add("The Kollege Klub");
        barpics.add(R.drawable.kklub);

        barnames.add("Mondays");
        barpics.add(R.drawable.mondays);

        barnames.add("Whisky Jacks Saloon");
        barpics.add(R.drawable.whiskyjacks);

        barnames.add("The Karaoke Kid");
        barpics.add(R.drawable.kkid);
    }

}
