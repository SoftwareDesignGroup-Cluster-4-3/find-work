package com.find_work;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WorkerDashboardActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnLogout, editDetails;
    SwitchMaterial switchAvailability;
    TextView userEmail;
    TextView txtName, txtPhone, txtAddress, txtSkillType;
    EditText edtName, edtPhone, edtAddress, edtSkillType;
    LinearLayout linearSkillList;
    CardView cardSkill1, cardSkill2, cardSkill3, cardSkill4, cardSkill5, cardSkill6, cardSkill7, cardSkill8, cardSkill9, cardSkill10;
    TextView txtSkill1, txtSkill2, txtSkill3, txtSkill4, txtSkill5, txtSkill6, txtSkill7, txtSkill8, txtSkill9, txtSkill10;

    String strName, strPhone, strAddress, strSkillType;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    Address geoCode;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard);

        //change ActionBar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Worker Dashboard");
        }

       // Initialize location manager and listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        // Request location updates
        try {
            locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    locationListener,
                    null
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        userEmail = findViewById(R.id.userDetails);

        user = auth.getCurrentUser();
        if (user==null) {
            Intent client = new Intent(WorkerDashboardActivity.this, LoginActivity.class);
            startActivity(client);
            finish();
        } else {
            String[] emailParts = user.getEmail().split("-");
            String[] actualEmail = Arrays.copyOfRange(emailParts, 1, emailParts.length);
            userEmail.setText(String.join("", actualEmail));
        }

        switchAvailability = findViewById(R.id.switchAvailability);
        switchAvailability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    setAvailability(userEmail.getText().toString(), true);
                } else {
                    setAvailability(userEmail.getText().toString(), false);
                }
            }
        });

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtSkillType = findViewById(R.id.txtSkillType);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtSkillType = findViewById(R.id.edtSkillType);

        editDetails = findViewById(R.id.editDetails);
        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editDetails.getText().equals("Edit Details")) {

                    txtName.setVisibility(View.GONE);
                    txtPhone.setVisibility(View.GONE);
                    txtAddress.setVisibility(View.GONE);
                    txtSkillType.setVisibility(View.GONE);

                    edtName.setVisibility(View.VISIBLE);
                    edtPhone.setVisibility(View.VISIBLE);
                    edtAddress.setVisibility(View.VISIBLE);
                    edtSkillType.setVisibility(View.VISIBLE);

                    edtName.setText(txtName.getText());
                    edtPhone.setText(txtPhone.getText());
                    edtAddress.setText(txtAddress.getText());
                    edtSkillType.setText(txtSkillType.getText());

                    editDetails.setText("Save Details");

                } else if (editDetails.getText().equals("Save Details")) {

                    strName = String.valueOf(edtName.getText());
                    strPhone = String.valueOf(edtPhone.getText());
                    strAddress = String.valueOf(edtAddress.getText());
                    strSkillType = String.valueOf(edtSkillType.getText());

                    txtName.setText(strName);
                    txtPhone.setText(strPhone);
                    txtAddress.setText(strAddress);
                    txtSkillType.setText(strSkillType);

                    txtName.setVisibility(View.VISIBLE);
                    txtPhone.setVisibility(View.VISIBLE);
                    txtAddress.setVisibility(View.VISIBLE);
                    txtSkillType.setVisibility(View.VISIBLE);

                    edtName.setVisibility(View.GONE);
                    edtPhone.setVisibility(View.GONE);
                    edtAddress.setVisibility(View.GONE);
                    edtSkillType.setVisibility(View.GONE);

                    addToFirestore(userEmail.getText().toString(), strName, strPhone, strAddress, strSkillType);

                    editDetails.setText("Edit Details");

                }
            }
        });

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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WorkerDashboardActivity.this);
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Do you want to Logout ?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent client = new Intent(WorkerDashboardActivity.this, MainActivity.class);
                        startActivity(client);
                        finish();
                    }
                });

                //When click "No" it will execute this
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        getUserDetails(userEmail.getText().toString());

    }

    //When try to back from Login page this method will execute
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit from Find Work ?");

        //When click "Yes" it will execute this
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent client = new Intent(WorkerDashboardActivity.this, MainActivity.class);
                startActivity(client);
                finish();
            }
        });

        //When click "No" it will execute this
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardSkill1:
                edtSkillType.setText(txtSkill1.getText());
                break;
            case R.id.cardSkill2:
                edtSkillType.setText(txtSkill2.getText());
                break;
            case R.id.cardSkill3:
                edtSkillType.setText(txtSkill3.getText());
                break;
            case R.id.cardSkill4:
                edtSkillType.setText(txtSkill4.getText());
                break;
            case R.id.cardSkill5:
                edtSkillType.setText(txtSkill5.getText());
                break;
            case R.id.cardSkill6:
                edtSkillType.setText(txtSkill6.getText());
                break;
            case R.id.cardSkill7:
                edtSkillType.setText(txtSkill7.getText());
                break;
            case R.id.cardSkill8:
                edtSkillType.setText(txtSkill8.getText());
                break;
            case R.id.cardSkill9:
                edtSkillType.setText(txtSkill9.getText());
                break;
            case R.id.cardSkill10:
                edtSkillType.setText(txtSkill10.getText());
                break;

        }
        linearSkillList.setVisibility(View.GONE);
    }

    private void addToFirestore(String email, String name, String phone, String address, String skill) {
        // Create a map to store user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("address", address);
        userData.put("skill", skill);

        // Add the data to Firestore with the email as the document ID
        db.collection("workers")
                .document(email)
                .set(userData, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WorkerDashboardActivity.this, "Successfully Updated !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WorkerDashboardActivity.this, "Failed to Update !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setAvailability(String email, boolean availability) {
        // Create a map to store user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("availability", availability);

        // Add the data to Firestore with the email as the document ID
        db.collection("workers")
                .document(email)
                .set(userData, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WorkerDashboardActivity.this, "Availability Updated !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WorkerDashboardActivity.this, "Failed to Update !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUserDetails(String email) {
        DocumentReference docRef = db.collection("workers").document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        // Retrieve specific fields
                        String userName = document.getString("name");
                        String userPhone = document.getString("phone");
                        String userAddress = document.getString("address");
                        String userSkill = document.getString("skill");
                        Boolean userAvailability = document.getBoolean("availability");

                        // Use the retrieved data as needed
                        txtName.setText(userName);
                        txtPhone.setText(userPhone);
                        txtAddress.setText(userAddress);
                        txtSkillType.setText(userSkill);
                        switchAvailability.setChecked(Boolean.TRUE.equals(userAvailability));

                    } else {
                        Toast.makeText(WorkerDashboardActivity.this, "Please fill your details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WorkerDashboardActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addLocationToFirebase(String email, double latitude, double longitude) {
        // Create a map to store user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("latitude", latitude);
        userData.put("longitude", longitude);

        // Add the data to Firestore with the email as the document ID
        db.collection("workers")
                .document(email)
                .set(userData, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WorkerDashboardActivity.this, "Location Updated !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WorkerDashboardActivity.this, "Failed to Update Location !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // Called when the location changes
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Get geocode (address) from the current location
            getAddressFromLocation(latitude, longitude);

            //add location to firebase
            addLocationToFirebase(userEmail.getText().toString(), latitude, longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Called when the provider status changes
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Called when the provider is enabled by the user
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Called when the provider is disabled by the user
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);
                Log.d("Geocode", "Full Address: " + fullAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove location updates when the activity is destroyed
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}