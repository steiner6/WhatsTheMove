package com.example.whatsthemove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainAdapter.AdapterInterface {

    private RecyclerView mainRecyclerView;
    private MainAdapter mainAdapter;

    private Context context = MainActivity.this;
    private MainAdapter.AdapterInterface listener = MainActivity.this;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private List<String> barNames = new ArrayList<>();
    private List<Integer> barPics = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<Integer> barStatus = new ArrayList<>();
    private LinearLayoutManager mainManager;
    private List<String> fences = new ArrayList<>();
    private List<Double> lat = new ArrayList<>();
    private List<Double> lng = new ArrayList<>();

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("locations");
    final GeoFire geofire = new GeoFire(ref);
    private GeoQuery query = null;

    final private double RADIUS = 0.025;
    private String gkey;
    private boolean presentFlag = false;

    GeoQueryEventListener geoQueryEventListener = new GeoQueryEventListener() {

        @Override
        public void onKeyEntered(final String key, GeoLocation location) {
            gkey = key;
            presentFlag = true;
            ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.child("tracked").getValue(String.class);
                    Integer val = Integer.parseInt(value);
                    val = val + 1;
                    String sval = String.valueOf(val);
                    ref.child(key).child("tracked").setValue(sval);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    error.toException();
                }
            });

        }

        @Override
        public void onKeyExited(final String key) {
            presentFlag = false;
            ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.child("tracked").getValue(String.class);
                    Integer val = Integer.parseInt(value);
                    val = val - 1;
                    if (val < 0) {
                        val = 0;
                    }
                    String sval = String.valueOf(val);
                    ref.child(key).child("tracked").setValue(sval);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    error.toException();
                }
            });
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {
            error.toException();
        }
    };


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addToArrays();

        if (checkLocationPermission()) {
            startListening();
            locationListen();
            getLocation();
        }

        /*int i = 0;
        for (String fence : fences) {
            createGfences(fence, lat.get(i), lng.get(i));
            i++;
        }*/

        //SharedPreferences prefs = getSharedPreferences("usrpref", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = prefs.edit();

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


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Why we use your location")
                        .setMessage("This app uses your location to tell when you enter and exit a bar and provide how many people are in a bar based on this data. " +
                                "Your identity is not recorded. While you can still use this app if you deny location permission, data provided will be less accurate for all users.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        startListening();
                    }
                } else {
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            updateLocationInfo(location);
        }
    }

    private void startListening() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    private void updateLocationInfo(Location location) {
        Log.i("Location info", location.toString());

        GeoLocation geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        if (query == null) {
            query = geofire.queryAtLocation(geoLocation, RADIUS);
            query.addGeoQueryEventListener(geoQueryEventListener);
        } else {
            query.setCenter(geoLocation);
        }
    }

    private void locationListen() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    //@SuppressWarnings("MissingPermission")
    public void createGfences(String fence, Double lat, Double lng) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(fence, new GeoLocation(lat, lng));

        DatabaseReference childref = FirebaseDatabase.getInstance().getReference("locations/" + fence);
        //createTrackedChildren(childref);
        //removeTrackedChildren(childref);

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

        //Add name and picture to intent
        intent.putExtra("name", a);
        intent.putExtra("tag", tag);
        intent.putExtra("stat", s);
        intent.putExtra("geofence", g);
        intent.putExtra("gkey", gkey);
        intent.putExtra("presentFlag", presentFlag);

        //Go to update bar activity
        startActivity(intent);
    }

    public void gotoFilter(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public void addToArrays() {

        barNames.add("Chasers Bar & Grille");
        barPics.add(R.drawable.chasers);
        tags.add("chasers");
        barStatus.add(checkChasers());
        fences.add("chasersGeofence");
        lat.add(43.074200);
        lng.add(-89.392090);

        barNames.add("The Double U");
        barPics.add(R.drawable.doubleu);
        tags.add("doubleu");
        barStatus.add(checkUU());
        fences.add("doubleuGeofence");
        lat.add(43.073574);
        lng.add(-89.396809);

        barNames.add("The Kollege Klub");
        barPics.add(R.drawable.kollegeklub);
        tags.add("kollegeklub");
        barStatus.add(checkKKlub());
        fences.add("kollegeklubGeofence");
        lat.add(43.075647);
        lng.add(-89.397010);

        barNames.add("Mondays");
        barPics.add(R.drawable.mondays);
        tags.add("mondays");
        barStatus.add(checkMondays());
        fences.add("mondaysGeofence");
        lat.add(43.074634);
        lng.add(-89.394614);

        barNames.add("Whiskey Jacks Saloon");
        barPics.add(R.drawable.whiskeyjackssaloon);
        tags.add("whiskeyjackssaloon");
        barStatus.add(checkWhiskeys());
        fences.add("whiskeysGeofence");
        lat.add(43.075149);
        lng.add(-89.394798);

        barNames.add("Blue Velvet Lounge");
        barPics.add(R.drawable.bluevelvetlounge);
        tags.add("bluevelvetlounge");
        barStatus.add(checkBlueVelvet());
        fences.add("bluevelvetloungeGeofence");
        lat.add(43.074341);
        lng.add(-89.394557);

        barNames.add("The Churchkey");
        barPics.add(R.drawable.churchkey);
        tags.add("churchkey");
        barStatus.add(checkChurchkey());
        fences.add("churchkeyGeofence");
        lat.add(43.073425);
        lng.add(-89.397021);

        barNames.add("City Bar");
        barPics.add(R.drawable.citybar);
        tags.add("citybar");
        barStatus.add(checkCityBar());
        fences.add("citybarGeofence");
        lat.add(43.075146);
        lng.add(-89.396165);

        barNames.add("Danny's Pub");
        barPics.add(R.drawable.dannyspub);
        tags.add("dannyspub");
        barStatus.add(checkDannysPub());
        fences.add("dannyspubGeofence");
        lat.add(43.074424);
        lng.add(-89.392631);

        barNames.add("Jordan's Big 10 Pub");
        barPics.add(R.drawable.jordansbigtenpub);
        tags.add("jordansbigtenpub");
        barStatus.add(checkJordansBigTenPub());
        fences.add("jordansbigtenpubGeofence");
        lat.add(43.068083);
        lng.add(-89.408326);

        barNames.add("Lucky's 1313 Pub");
        barPics.add(R.drawable.luckys);
        tags.add("luckys");
        barStatus.add(checkLuckysPub());
        fences.add("luckysGeofence");
        lat.add(43.067563);
        lng.add(-89.408165);

        barNames.add("The Nitty Gritty");
        barPics.add(R.drawable.nittygritty);
        tags.add("nittygritty");
        barStatus.add(checkNittyGritty());
        fences.add("nittygrittyGeofence");
        lat.add(43.071812);
        lng.add(-89.395594);

        barNames.add("Plaza Tavern");
        barPics.add(R.drawable.plazatavern);
        tags.add("plazatavern");
        barStatus.add(checkPlazaTavern());
        fences.add("plazataverGeofence");
        lat.add(43.075341);
        lng.add(-89.390396);

        barNames.add("Red Rock Saloon");
        barPics.add(R.drawable.redrocksaloon);
        tags.add("redrocksaloon");
        barStatus.add(checkRedRockSaloon());
        fences.add("redrocksaloonGeofence");
        lat.add(43.074156);
        lng.add(-89.391287);

        barNames.add("The Red Shed");
        barPics.add(R.drawable.redshed);
        tags.add("redshed");
        barStatus.add(checkRedShed());
        fences.add("redshedGeofence");
        lat.add(43.073559);
        lng.add(-89.396071);

        barNames.add("Sconnie Bar");
        barPics.add(R.drawable.sconniebar);
        tags.add("sconniebar");
        barStatus.add(checkSconnieBar());
        fences.add("sconniebarGeofence");
        lat.add(43.067611);
        lng.add(-89.410247);

        barNames.add("State Street Brats");
        barPics.add(R.drawable.statestreetbrats);
        tags.add("statestreetbrats");
        barStatus.add(checkStateStreetBrats());
        fences.add("statestreetbratsGeofence");
        lat.add(43.074692);
        lng.add(-89.395929);

        barNames.add("Wando's");
        barPics.add(R.drawable.wandos);
        tags.add("wandos");
        barStatus.add(checkWandos());
        fences.add("wandosGeofence");
        lat.add(43.073413);
        lng.add(-89.396012);

        barNames.add("The Karaoke Kid");
        barPics.add(R.drawable.kkid);
        tags.add("kkid");
        barStatus.add(checkKaraokeKid());
        fences.add("kkidGeofence");
        lat.add(43.073385);
        lng.add(-89.396374);
    }

    private int checkKaraokeKid() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
    }

    private int checkWandos() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                if (time < 11 || time >= 22) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time < 11 || time >= 22) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time < 11 || time >= 22) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time < 11 || time >= 22) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time < 11 || time >= 22) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time < 11 || time >= 22) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkStateStreetBrats() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
    }

    private int checkSconnieBar() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkRedShed() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
    }

    private int checkRedRockSaloon() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
    }

    private int checkPlazaTavern() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
    }

    private int checkNittyGritty() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
    }

    private int checkLuckysPub() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Sunday - Thursday: 11am - 2am
        //Friday - Saturday: 11am - 2:30am

        switch (day) {
            case Calendar.SUNDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkJordansBigTenPub() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Sunday - Saturday: 11am - 2:30am

        switch (day) {
            case Calendar.SUNDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2.5 && time < 11) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkDannysPub() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
    }

    private int checkCityBar() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Monday - Friday: 3pm - 1am
        //Saturday - Sunday: 12pm - 1am

        switch (day) {
            case Calendar.SUNDAY:
                if (time >= 1 && time < 12) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time >= 1 && time < 15) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time >= 1 && time < 15) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 1 && time < 15) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 1 && time < 15) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 1 && time < 15) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 1 && time < 12) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkChurchkey() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        //Hours
        //Tuesday: 7pm - 2am
        //Wednesday - Thursday: 11am - 2am
        //Friday: 11am - 2:30am
        //Saturday: 7pm - 2:30am


        switch (day) {
            case Calendar.SUNDAY:
                if (time >= 2.5) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                if (time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2 && time < 11) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2.5 && time < 19) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

    private int checkBlueVelvet() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int stats = 1;

        switch (day) {
            case Calendar.SUNDAY:
                stats = 0;
                break;
            case Calendar.MONDAY:
                stats = 0;
                break;
            case Calendar.TUESDAY:
                stats = 0;
                break;
            case Calendar.WEDNESDAY:
                stats = 0;
                break;
            case Calendar.THURSDAY:
                stats = 0;
                break;
            case Calendar.FRIDAY:
                stats = 0;
                break;
            case Calendar.SATURDAY:
                stats = 0;
                break;
        }
        return stats;
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
                if (time >= 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time >= 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time >= 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2 && time < 11 ) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2 && time < 11 ) {
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
                if (time >= 2) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time >= 2 && time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 2 && time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2 && time < 19) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2 && time < 16) {
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
        int stats = 0;

        //Hours
        //Sunday = 8pm - 2am
        //Monday = Closed
        //Tuesday - Friday = 2pm - 2am
        //Saturday = 11am - 2am
        /*switch (day) {
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
        }*/
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
                if (time >= 2) {
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
                if (time >= 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2 && time < 16) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2 && time < 16) {
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
                if (time >= 2 && time < 12) {
                    stats = 0;
                }
                break;
            case Calendar.MONDAY:
                if (time >= 2) {
                    stats = 0;
                }
                break;
            case Calendar.TUESDAY:
                if (time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.WEDNESDAY:
                if (time >= 2 && time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.THURSDAY:
                if (time >= 2 && time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.FRIDAY:
                if (time >= 2 && time < 18) {
                    stats = 0;
                }
                break;
            case Calendar.SATURDAY:
                if (time >= 2 && time < 12) {
                    stats = 0;
                }
                break;
        }
        return stats;
    }

}
