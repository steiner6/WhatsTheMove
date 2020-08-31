package com.example.whatsthemove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainAdapter.AdapterInterface {

    private RecyclerView mainRecyclerView;
    private MainAdapter mainAdapter;
    private LinearLayoutManager mainManager;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private Context context = MainActivity.this;
    private MainAdapter.AdapterInterface listener = MainActivity.this;

    private List<String> barNames = new ArrayList<>();
    private List<Integer> barPics = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<Integer> barStatus = new ArrayList<>();
    private List<String> fences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!haveNetwork()) {
            Toast.makeText(MainActivity.this, "You are not connected to the internet. " +
                    "Please establish a network connection to get accurate line info", Toast.LENGTH_SHORT).show();
        }

        createGfences();

        SharedPreferences prefs = getSharedPreferences("whatsthemove", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        addToArrays();

        mainManager = new LinearLayoutManager(this);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(mainManager);
        mainAdapter = new MainAdapter(this, barNames, barPics, tags, listener, barStatus, fences);
        mainRecyclerView.setAdapter(mainAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mainRecyclerView.getContext(),
                mainManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        mainRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    private boolean haveNetwork() {
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }

    public void createGfences() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        geofencingClient = LocationServices.getGeofencingClient(this);

        Geofence chasersGeofence = buildGeofence("chasersGeofence", 43.074200, -89.392090, 25);
        Geofence doubleuGeofence = buildGeofence("doubleuGeofence", 43.073574, -89.396809, 25);
        Geofence mondaysGeofence = buildGeofence("mondaysGeofence", 43.074634, -89.394614, 20);
        Geofence kklubGeofence = buildGeofence("kklubGeofence", 43.075647, -89.397010, 20);
        Geofence whiskeysGeofence = buildGeofence("whiskeysGeofence", 43.075149, -89.394798, 25);

        geofencingClient.addGeofences(getGeofencingRequest(chasersGeofence), getGeofencePendingIntent());
        geofencingClient.addGeofences(getGeofencingRequest(doubleuGeofence), getGeofencePendingIntent());
        geofencingClient.addGeofences(getGeofencingRequest(mondaysGeofence), getGeofencePendingIntent());
        geofencingClient.addGeofences(getGeofencingRequest(kklubGeofence), getGeofencePendingIntent());
        geofencingClient.addGeofences(getGeofencingRequest(whiskeysGeofence), getGeofencePendingIntent());
    }

    private Geofence buildGeofence(String string, double lat, double lng, int rad) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(string)
                .setCircularRegion(lat, lng, rad)
                .setExpirationDuration(10800000) //3 hours in milliseconds
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                //.setLoiteringDelay(180000)
                .build();
        return geofence;
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }


    public void gotoUpdate(TextView myTextView, ImageView myImageView, TextView inbar) {
        Intent intent = new Intent(this, UpdateBarActivity.class);

        //Get selected bar-to-update name
        String a = myTextView.getText().toString();

        //Get selected bar-to-update status
        Integer s = Integer.parseInt(myTextView.getTag().toString());

        //Get bar-to-update geofence id
        String g = inbar.getText().toString();

        //Get selected bar-to-update picture
        String tag = (String) myImageView.getTag();
        Resources resource = context.getResources();
        int resourceID = resource.getIdentifier(tag, "drawable", context.getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        //Add name and picture to intent
        intent.putExtra("name", a);
        intent.putExtra("picture", b);
        intent.putExtra("tag", tag);
        intent.putExtra("stat", s);
        intent.putExtra("geofence", g);

        //Go to update bar activity
        startActivity(intent);
    }

    public void gotoFilter(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public void addToArrays() {
        int status;

        barNames.add("Chasers Bar & Grille");
        barPics.add(R.drawable.chasers);
        tags.add("chasers");
        status = checkChasers();
        barStatus.add(status);
        fences.add("chasersGeofence");

        barNames.add("The Double U");
        barPics.add(R.drawable.doubleu);
        tags.add("doubleu");
        status = checkUU();
        barStatus.add(status);
        fences.add("doubleuGeofence");

        barNames.add("The Kollege Klub");
        barPics.add(R.drawable.kklub);
        tags.add("kklub");
        status = checkKKlub();
        barStatus.add(status);
        fences.add("kklubGeofence");

        barNames.add("Mondays");
        barPics.add(R.drawable.mondays);
        tags.add("mondays");
        status = checkMondays();
        barStatus.add(status);
        fences.add("mondaysGeofence");

        barNames.add("Whiskey Jacks Saloon");
        barPics.add(R.drawable.whiskyjacks);
        tags.add("whiskyjacks");
        status = checkWhiskeys();
        barStatus.add(status);
        fences.add("whiskeysGeofence");

        //barNames.add("The Karaoke Kid");
        //barPics.add(R.drawable.kkid);
        //tags.add("kkid");
    }

    private int checkWhiskeys() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Sunday - Saturday: 11am - 2am
        switch (day) {
            case Calendar.SUNDAY:
                if (time > 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time > 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time > 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time > 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time > 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time > 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time > 2 && time < 11 ) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkMondays() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Sunday = Closed
        //Monday - Thursday = 7pm - 2am
        //Friday - Saturday = 4pm - 2am
        switch (day) {
            case Calendar.SUNDAY:
                if (time > 2) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time > 2 && time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time > 2 && time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time > 2 && time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time > 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time > 2 && time < 16) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkKKlub() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Sunday = 8pm - 2am
        //Monday = Closed
        //Tuesday - Friday = 2pm - 2am
        //Saturday = 11am - 2am
        switch (day) {
            case Calendar.SUNDAY:
                if (time > 2 && time < 20) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time > 2) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time < 14) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time > 2 && time < 14) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time > 2 && time < 14) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time > 2 && time < 14) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time > 2 && time < 11) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkUU() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Sunday = Closed
        //Monday = Closed
        //Tuesday - Saturday = 4pm - 2am
        switch (day) {
            case Calendar.SUNDAY:
                if (time > 2) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                if (time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time > 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time > 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time > 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time > 2 && time < 16) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkChasers() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Sunday = Noon - 2 am
        //Monday = Closed
        //Tuesday - Friday = 6pm - 2am
        //Saturday = Noon - 2am
        switch (day) {
            case Calendar.SUNDAY:
                if (time > 2 && time < 12) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time > 2) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time > 2 && time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time > 2 && time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time > 2 && time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time > 2 && time < 12) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

}
