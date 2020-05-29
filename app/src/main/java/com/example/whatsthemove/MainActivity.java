package com.example.whatsthemove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mainRecyclerView;
    private MainAdapter mainAdapter;
    private LinearLayoutManager mainManager;

    private List<String> barNames = new ArrayList<>();
    private List<Integer> barPics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("whatsthemove", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        barNames.add("KK");
        barPics.add(R.drawable.kklub);
        barNames.add("UU");
        barPics.add(R.drawable.doubleu);

        setContentView(R.layout.activity_main);

        mainManager = new LinearLayoutManager(this);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(mainManager);
        mainAdapter = new MainAdapter(this, barNames, barPics);
        mainRecyclerView.setAdapter(mainAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mainRecyclerView.getContext(),
                mainManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        mainRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    public void gotoFilter(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }
}
