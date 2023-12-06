package com.find_work;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener  {

    private GoogleMap mMap;

    private static final int LOCATION_PERMISSION_CODE = 101;
    static int PERMISSION_CODE = 100;

    LatLng currentLatLng;
    private List<Marker> workerMarkers = new ArrayList<>();

    FirebaseFirestore db;

    EditText edtSkillType;
    LinearLayout linearSkillList;
    TextView txtSkill1, txtSkill2, txtSkill3, txtSkill4, txtSkill5, txtSkill6, txtSkill7, txtSkill8, txtSkill9, txtSkill10;
    CardView cardSkill1, cardSkill2, cardSkill3, cardSkill4, cardSkill5, cardSkill6, cardSkill7, cardSkill8, cardSkill9, cardSkill10;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();

        edtSkillType = findViewById(R.id.edtSkillType);
        linearSkillList = findViewById(R.id.linearSkillList);
        edtSkillType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Disable typing in the EditText
                edtSkillType.setKeyListener(null);
                linearSkillList.setVisibility(View.VISIBLE);
                return false;
            }
        });

        txtSkill1 = findViewById(R.id.txtSkill1);
        txtSkill2 = findViewById(R.id.txtSkill2);
        txtSkill3 = findViewById(R.id.txtSkill3);
        txtSkill4 = findViewById(R.id.txtSkill4);
        txtSkill5 = findViewById(R.id.txtSkill5);
        txtSkill6 = findViewById(R.id.txtSkill6);
        txtSkill7 = findViewById(R.id.txtSkill7);
        txtSkill8 = findViewById(R.id.txtSkill8);
        txtSkill9 = findViewById(R.id.txtSkill9);
        txtSkill10 = findViewById(R.id.txtSkill10);

        cardSkill1 = findViewById(R.id.cardSkill1);
        cardSkill2 = findViewById(R.id.cardSkill2);
        cardSkill3 = findViewById(R.id.cardSkill3);
        cardSkill4 = findViewById(R.id.cardSkill4);
        cardSkill5 = findViewById(R.id.cardSkill5);
        cardSkill6 = findViewById(R.id.cardSkill6);
        cardSkill7 = findViewById(R.id.cardSkill7);
        cardSkill8 = findViewById(R.id.cardSkill8);
        cardSkill9 = findViewById(R.id.cardSkill9);
        cardSkill10 = findViewById(R.id.cardSkill10);

        cardSkill1.setOnClickListener(this);
        cardSkill2.setOnClickListener(this);
        cardSkill3.setOnClickListener(this);
        cardSkill4.setOnClickListener(this);
        cardSkill5.setOnClickListener(this);
        cardSkill6.setOnClickListener(this);
        cardSkill7.setOnClickListener(this);
        cardSkill8.setOnClickListener(this);
        cardSkill9.setOnClickListener(this);
        cardSkill10.setOnClickListener(this);

        if (isLocationPermissionGranted()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            requestLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //Request permission to make call
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setPadding(0, 180, 0, 0);
        mMap.setOnMarkerClickListener(this);

        // THIS WILL UNCOMMENT WHEN WANT TO USE GPS OPTION
        //Request Runtime Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            //Get the current location
            mMap.setMyLocationEnabled(true);

            // Get the last known location from the FusedLocationProviderClient
            FusedLocationProviderClient fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this);

            try {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.0f));
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    //Check whether the location permission is granted or not
    private boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //Request location permission
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardSkill1:
                edtSkillType.setText(txtSkill1.getText());
                getDocumentsFilteredBySkillType(txtSkill1.getText().toString());
                break;
            case R.id.cardSkill2:
                edtSkillType.setText(txtSkill2.getText());
                getDocumentsFilteredBySkillType(txtSkill2.getText().toString());
                break;
            case R.id.cardSkill3:
                edtSkillType.setText(txtSkill3.getText());
                getDocumentsFilteredBySkillType(txtSkill3.getText().toString());
                break;
            case R.id.cardSkill4:
                edtSkillType.setText(txtSkill4.getText());
                getDocumentsFilteredBySkillType(txtSkill4.getText().toString());
                break;
            case R.id.cardSkill5:
                edtSkillType.setText(txtSkill5.getText());
                getDocumentsFilteredBySkillType(txtSkill5.getText().toString());
                break;
            case R.id.cardSkill6:
                edtSkillType.setText(txtSkill6.getText());
                getDocumentsFilteredBySkillType(txtSkill6.getText().toString());
                break;
            case R.id.cardSkill7:
                edtSkillType.setText(txtSkill7.getText());
                getDocumentsFilteredBySkillType(txtSkill7.getText().toString());
                break;
            case R.id.cardSkill8:
                edtSkillType.setText(txtSkill8.getText());
                getDocumentsFilteredBySkillType(txtSkill8.getText().toString());
                break;
            case R.id.cardSkill9:
                edtSkillType.setText(txtSkill9.getText());
                getDocumentsFilteredBySkillType(txtSkill9.getText().toString());
                break;
            case R.id.cardSkill10:
                edtSkillType.setText(txtSkill10.getText());
                getDocumentsFilteredBySkillType(txtSkill10.getText().toString());
                break;

        }
        linearSkillList.setVisibility(View.GONE);
    }

    private void getDocumentsFilteredBySkillType(String skillType) {
        clearMarkers();
        // Reference to the "users" collection
        // Update "users" with your actual collection name
        Query query = db.collection("workers").whereEqualTo("skill", skillType);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        // Handle each document here
                        String userEmail = document.getId();
                        String userName = document.getString("name");
                        String userPhone = document.getString("phone");
                        String userAddress = document.getString("address");
                        double userLatitude = document.getDouble("latitude");
                        double userLongitude = document.getDouble("longitude");
                        boolean userAvailability = Boolean.TRUE.equals(document.getBoolean("availability"));

                        String title = userName+"\n"+userEmail+"\n"+userAddress+"\n"+userPhone;

                        if (userAvailability) {
                            LatLng latLng = new LatLng(userLatitude, userLongitude);
                            if (latLng == null) {
                                Toast.makeText(MapsActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            } else {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(title));
                                workerMarkers.add(marker);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                mMap.getUiSettings().setZoomControlsEnabled(true);
                            }
                        }

                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                } else {
                    Toast.makeText(MapsActivity.this, "Failed to load locations", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearMarkers() {
        for (Marker marker : workerMarkers) {
            marker.remove();
        }
        workerMarkers.clear();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent client = new Intent(MapsActivity.this, ClientDashboardActivity.class);
        startActivity(client);
        finish();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String markerTitle = marker.getTitle();
        String[] titlePart = markerTitle.split("\n");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Worker Details");
        alertDialog.setMessage(markerTitle);

        //When click "Yes" it will execute this
        alertDialog.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+titlePart[3]));
                startActivity(i);
            }
        });

        //When click "No" it will execute this
        alertDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

//        alertDialog.setNeutralButton("Neutral", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//            }
//        });

        alertDialog.show();

        return false;
    }
}