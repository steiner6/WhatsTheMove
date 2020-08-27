package com.example.whatsthemove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainAdapter.AdapterInterface {

    private RecyclerView mainRecyclerView;
    private MainAdapter mainAdapter;
    private LinearLayoutManager mainManager;
    public Context context = MainActivity.this;
    private MainAdapter.AdapterInterface listener = MainActivity.this;

    private List<String> barNames = new ArrayList<>();
    private List<Integer> barPics = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("whatsthemove", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        addToArrays();

        setContentView(R.layout.activity_main);

        mainManager = new LinearLayoutManager(this);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(mainManager);
        mainAdapter = new MainAdapter(this, barNames, barPics, tags, listener);
        mainRecyclerView.setAdapter(mainAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mainRecyclerView.getContext(),
                mainManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        mainRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    public void gotoUpdate(TextView myTextView, ImageView myImageView) {
        Intent intent = new Intent(this, UpdateBarActivity.class);

        String a = myTextView.getText().toString();

        //TextView name = findViewById(R.id.barname);
        //String a = name.getText().toString();


        String tag = (String) myImageView.getTag();
        Resources resource = context.getResources();
        int resourceID = resource.getIdentifier(tag, "drawable", context.getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        intent.putExtra("name", a);
        intent.putExtra("picture", b);

        startActivity(intent);
    }

    public void gotoFilter(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public void gotoBouncer(View view) {
        Intent intent = new Intent(this, BouncerActivity.class);
        startActivity(intent);
    }

    public void addToArrays() {

        barNames.add("Chasers Bar & Grille");
        barPics.add(R.drawable.chasers);
        tags.add("chasers");

        barNames.add("The Double U");
        barPics.add(R.drawable.doubleu);
        tags.add("doubleu");

        barNames.add("The Kollege Klub");
        barPics.add(R.drawable.kklub);
        tags.add("kklub");

        barNames.add("Mondays");
        barPics.add(R.drawable.mondays);
        tags.add("mondays");

        barNames.add("Whisky Jacks Saloon");
        barPics.add(R.drawable.whiskyjacks);
        tags.add("whiskeyjacks");

        barNames.add("The Karaoke Kid");
        barPics.add(R.drawable.kkid);
        tags.add("kkid");
    }

}
