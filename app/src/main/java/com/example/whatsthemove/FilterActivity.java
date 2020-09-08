package com.example.whatsthemove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private RecyclerView filterRecyclerView;
    private MyAdapter myAdapter;
    private LinearLayoutManager filterManager;

    private List<String> filterBarNames = new ArrayList<>();
    private List<Integer> filterBarPics = new ArrayList<>();
    private List<String> selectedBarNames = new ArrayList<>();
    private List<Integer> selectedBarPics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        SharedPreferences prefs = getSharedPreferences("whatsthemove", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        filterManager = new LinearLayoutManager(this);
        filterRecyclerView = findViewById(R.id.filterRecycleView);
        filterRecyclerView.setLayoutManager(filterManager);
        myAdapter = new MyAdapter(this, filterBarNames, filterBarPics, selectedBarNames, selectedBarPics);
        filterRecyclerView.setAdapter(myAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(filterRecyclerView.getContext(),
                filterManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(FilterActivity.this, R.drawable.divider));
        filterRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void gotoMain(View view) {
        finish();
    }

}
