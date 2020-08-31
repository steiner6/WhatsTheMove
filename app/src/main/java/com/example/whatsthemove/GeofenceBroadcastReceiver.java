package com.example.whatsthemove;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        getGeofenceTrasitionDetails(geofenceTransition, triggeringGeofences );


    }

    private void getGeofenceTrasitionDetails(final int geofenceTransition, List<Geofence> triggeringGeofences) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final int[] peopleinbar = new int[1];
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            //triggeringGeofencesList.add( geofence.getRequestId() );

            final DatabaseReference myRef = database.getReference(geofence.getRequestId());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.getValue(String.class);
                    Integer val = Integer.parseInt(value);
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                        val = val + 1;
                        myRef.setValue(val);
                    } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        if (val <= 0) {
                            myRef.setValue("0");
                        } else {
                            val = val - 1;
                            myRef.setValue(val);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    error.toException();
                }
            });


        }
    }
}



