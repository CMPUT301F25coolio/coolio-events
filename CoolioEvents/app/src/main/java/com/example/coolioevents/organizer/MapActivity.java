package com.example.coolioevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.R;
import com.example.coolioevents.WaitlistLocation;
import com.example.coolioevents.administrator.AdministratorHomeActivity;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap googleMap;
    private EventViewModel eventViewModel;
    private String currentEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        currentEventId = getIntent().getStringExtra("EVENT_ID");

        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        /*
        Taken From: https://developers.google.com/codelabs/maps-platform/maps-platform-101-android?_gl=1*1rkqlg5*_up*MQ..*_ga*MTk5NjQ2NjUxOS4xNzY0MDM1NzUy*_ga_NRWSTWS78N*czE3NjQwNDE1NjEkbzIkZzAkdDE3NjQwNDE1NjYkajU1JGwwJGgw*_ga_SM8HXJ53K2*czE3NjQwMzU3NTIkbzEkZzAkdDE3NjQwMzU3NTIkajYwJGwwJGgw#5
            License: http://www.apache.org/licenses/LICENSE-2.0
            Authored by: Android Studios
            Taken by: Avery Dancocks
            Taken on: 11/24/25
         */
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Establishing Button
        ImageButton backButton = findViewById(R.id.map_back_button);

        // Back Button onClick
        backButton.setOnClickListener(v ->
                startActivity(new Intent(this, OrganizerEventActivity.class)));

    }

    /*
    Taken From: Google Gemini
        Prompt: Why am I getting this error: Class 'MapActivity' must either be declared abstract or implement abstract method 'onMapReady(GoogleMap)' in 'OnMapReadyCallback'?
        Taken by: Avery Dancocks
        Taken on: 11/24/25
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        /*
        Taken From: https://stackoverflow.com/questions/42926188/cant-use-setmylocationbuttonenabled-android-google-maps
            Licensed by: CC BY-SA 3.0
            Author: alb
            Taken by: Avery Dancocks
            Taken on: 11/24/25
         */
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true); // Allows us to use zoom in and out buttons
        addWaitlistMarkers();
    }


    private void addWaitlistMarkers() {
        if (currentEventId == null || this.googleMap == null) {
            return;
        }

        eventViewModel.getWaitlistLocations(currentEventId).observe(this, new Observer<List<WaitlistLocation>>() {
            @Override
            public void onChanged(List<WaitlistLocation> locations) {
                if (locations != null && !locations.isEmpty()) {
                    Log.d("MapsActivity", "Received " + locations.size() + " waitlist locations.");

                    googleMap.clear(); // Restart map

                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                    for (WaitlistLocation location : locations) {
                        if (location.getLocation() != null) { // We have a location
                            // Convert Firestore GeoPoint to Maps LatLng
                            /*
                            Taken From: https://stackoverflow.com/questions/53799346/how-to-convert-geopoint-in-firestore-to-latlng
                                License: CC BY-SA 4.0
                                Authored by: Alex Mamo
                                Taken by: Avery Dancocks
                                Taken on: 11/24/25
                             */
                            LatLng position = new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude());

                            /*
                            Taken From: https://developers.google.com/codelabs/maps-platform/maps-platform-101-android?_gl=1*1rkqlg5*_up*MQ..*_ga*MTk5NjQ2NjUxOS4xNzY0MDM1NzUy*_ga_NRWSTWS78N*czE3NjQwNDE1NjEkbzIkZzAkdDE3NjQwNDE1NjYkajU1JGwwJGgw*_ga_SM8HXJ53K2*czE3NjQwMzU3NTIkbzEkZzAkdDE3NjQwMzU3NTIkajYwJGwwJGgw#5
                                License: http://www.apache.org/licenses/LICENSE-2.0
                                Authored by: Android Studios
                                Taken by: Avery Dancocks
                                Taken on: 11/24/25
                             */
                            googleMap.addMarker(
                                    new MarkerOptions()
                                            .position(position)
                                            .title("User: " + location.getUserId())
                            );

                            // Add location to the bounds builder
                            boundsBuilder.include(position);
                        }
                    }

                    /*
                    Taken From: https://developers.google.com/maps/documentation/android-sdk/views#maps_android_camera_and_view_setting_boundaries-java
                        License: http://www.apache.org/licenses/LICENSE-2.0
                        Authored by: Google Developers
                        Taken by: Avery Dancocks
                        Taken on: 11/24/25
                     */

                    if (locations.size() == 1) {
                        LatLng oneLocation = new LatLng(locations.get(0).getLocation().getLatitude(), locations.get(0).getLocation().getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oneLocation, 15f));
                    }
                    else {
                        LatLngBounds bounds = boundsBuilder.build();
                        int padding = 150;
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    }
                }
                else {
                    Log.d("MapsActivity", "No waitlist locations to display.");
                }
            }
        });
    }
}
