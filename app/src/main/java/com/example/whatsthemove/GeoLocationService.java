package com.example.whatsthemove;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GeoLocationService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;
    final private double RADIUS = 0.025;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("locations");
    final GeoFire geofire = new GeoFire(ref);
    GeoQuery query = null;
    String gkey;

    GeoQueryEventListener geoQueryEventListener = new GeoQueryEventListener() {

        @Override
        public void onKeyEntered(final String key, GeoLocation location) {
            gkey = key;
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

                }
            });

        }

        @Override
        public void onKeyExited(final String key) {
            ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.child("tracked").getValue(String.class);
                    Integer val = Integer.parseInt(value);
                    val = val - 1;
                    String sval = String.valueOf(val);
                    ref.child(key).child("tracked").setValue(sval);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListen();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        startListening();

        getLocation();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
       this.sendBroadcast(broadcastIntent);
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

}
