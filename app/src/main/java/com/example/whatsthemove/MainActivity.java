package com.example.whatsthemove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TooManyListenersException;

public class MainActivity extends AppCompatActivity implements MainAdapter.AdapterInterface {

    private RecyclerView mainRecyclerView;
    private MainAdapter mainAdapter;

    private Context context = MainActivity.this;
    private MainAdapter.AdapterInterface listener = MainActivity.this;

    private List<String> barNames = new ArrayList<>();
    private List<Integer> barPics = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<Integer> barStatus = new ArrayList<>();
    private LinearLayoutManager mainManager;
    private List<String> fences = new ArrayList<>();
    private List<Double> lat = new ArrayList<>();
    private List<Double> lng = new ArrayList<>();




    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (!haveNetwork()) {
            Toast.makeText(MainActivity.this, "You are not connected to the internet. " +
                    "Please establish a network connection to get accurate line info", Toast.LENGTH_SHORT).show();
        }*/

        /*locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListen();
        startListening();
        if (!checkPermissions()) {
            requestPermissions();
        }*/

        addToArrays();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (!isMyServiceRunning(GeoLocationService.class)) {
            Intent intent = new Intent(this, GeoLocationService.class);
            startService(intent);
        }

        /*int i = 0;
        for (String fence : fences) {
            createGfences(fence, lat.get(i), lng.get(i));
            i++;
        }*/

        SharedPreferences prefs = getSharedPreferences("usrpref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

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

    /*private boolean haveNetwork() {
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }*/


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!isMyServiceRunning(GeoLocationService.class)) {
                Intent intent = new Intent(this, GeoLocationService.class);
                startService(intent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //@SuppressWarnings("MissingPermission")
    public void createGfences(String fence, Double lat, Double lng) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(fence, new GeoLocation(lat, lng));

        DatabaseReference childref = FirebaseDatabase.getInstance().getReference("locations/" + fence);
        //createTrackedChildren(childref);
        //removeTrackedChildren(childref);

        /*if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }*/

    }

    private void createTrackedChildren(DatabaseReference ref) {
        ref.child("tracked").setValue("0");
    }

    private void removeTrackedChildren(DatabaseReference ref) {
        ref.child("tracked").removeValue();
    }


    public void gotoUpdate(TextView myTextView, ImageView myImageView, TextView gfencename) {
        Intent intent = new Intent(this, UpdateBarActivity.class);

        //Get selected bar-to-update name
        String a = myTextView.getText().toString();

        //Get selected bar-to-update status
        Integer s = Integer.parseInt(myTextView.getTag().toString());

        //Get bar-to-update geofence id
        String g = gfencename.getTag().toString();

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
        lat.add(43.074200);
        lng.add(-89.392090);

        barNames.add("The Double U");
        barPics.add(R.drawable.doubleu);
        tags.add("doubleu");
        status = checkUU();
        barStatus.add(status);
        fences.add("doubleuGeofence");
        lat.add(43.073574);
        lng.add(-89.396809);

        barNames.add("The Kollege Klub");
        barPics.add(R.drawable.kklub);
        tags.add("kklub");
        status = checkKKlub();
        barStatus.add(status);
        fences.add("kklubGeofence");
        lat.add(43.075647);
        lng.add(-89.397010);

        barNames.add("Mondays");
        barPics.add(R.drawable.mondays);
        tags.add("mondays");
        status = checkMondays();
        barStatus.add(status);
        fences.add("mondaysGeofence");
        lat.add(43.074634);
        lng.add(-89.394614);

        barNames.add("Whiskey Jacks Saloon");
        barPics.add(R.drawable.whiskyjacks);
        tags.add("whiskyjacks");
        status = checkWhiskeys();
        barStatus.add(status);
        fences.add("whiskeysGeofence");
        lat.add(43.075149);
        lng.add(-89.394798);

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

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

}
