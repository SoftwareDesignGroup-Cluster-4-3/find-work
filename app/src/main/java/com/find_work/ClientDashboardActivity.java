package com.find_work;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientDashboardActivity extends AppCompatActivity {

    Button btnLogout, editDetails, btnMap;
    TextView userEmail;
    TextView txtName, txtPhone, txtAddress;
    EditText edtName, edtPhone, edtAddress;

    String strName, strPhone, strAddress;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);

        //change ActionBar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Client Dashboard");
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        btnMap = findViewById(R.id.btnMap);
        userEmail = findViewById(R.id.userDetails);

        user = auth.getCurrentUser();
        if (user==null) {
            Intent client = new Intent(ClientDashboardActivity.this, LoginActivity.class);
            startActivity(client);
            finish();
        } else {
            String[] emailParts = user.getEmail().split("-");
            String[] actualEmail = Arrays.copyOfRange(emailParts, 1, emailParts.length);
            userEmail.setText(String.join("", actualEmail));
        }

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);

        editDetails = findViewById(R.id.editDetails);
        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editDetails.getText().equals("Edit Details")) {

                    txtName.setVisibility(View.GONE);
                    txtPhone.setVisibility(View.GONE);
                    txtAddress.setVisibility(View.GONE);

                    edtName.setVisibility(View.VISIBLE);
                    edtPhone.setVisibility(View.VISIBLE);
                    edtAddress.setVisibility(View.VISIBLE);

                    edtName.setText(txtName.getText());
                    edtPhone.setText(txtPhone.getText());
                    edtAddress.setText(txtAddress.getText());

                    editDetails.setText("Save Details");

                } else if (editDetails.getText().equals("Save Details")) {

                    strName = String.valueOf(edtName.getText());
                    strPhone = String.valueOf(edtPhone.getText());
                    strAddress = String.valueOf(edtAddress.getText());

                    txtName.setText(strName);
                    txtPhone.setText(strPhone);
                    txtAddress.setText(strAddress);

                    txtName.setVisibility(View.VISIBLE);
                    txtPhone.setVisibility(View.VISIBLE);
                    txtAddress.setVisibility(View.VISIBLE);

                    edtName.setVisibility(View.GONE);
                    edtPhone.setVisibility(View.GONE);
                    edtAddress.setVisibility(View.GONE);

                    addToFirestore(userEmail.getText().toString(), strName, strPhone, strAddress);

                    editDetails.setText("Edit Details");

                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClientDashboardActivity.this);
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Do you want to Logout ?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent client = new Intent(ClientDashboardActivity.this, MainActivity.class);
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

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent(ClientDashboardActivity.this, MapsActivity.class);
                startActivity(map);
                finish();
            }
        });

        getUserDetails(userEmail.getText().toString());

    }

    private void addToFirestore(String email, String name, String phone, String address) {
        // Create a map to store user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("address", address);

        // Add the data to Firestore with the email as the document ID
        db.collection("clients")
                .document(email)
                .set(userData, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ClientDashboardActivity.this, "Successfully Updated !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ClientDashboardActivity.this, "Failed to Update !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUserDetails(String email) {
        DocumentReference docRef = db.collection("clients").document(email);

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

                        // Use the retrieved data as needed
                        txtName.setText(userName);
                        txtPhone.setText(userPhone);
                        txtAddress.setText(userAddress);

                    } else {
                        Toast.makeText(ClientDashboardActivity.this, "Please fill your details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClientDashboardActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                Intent client = new Intent(ClientDashboardActivity.this, MainActivity.class);
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
}