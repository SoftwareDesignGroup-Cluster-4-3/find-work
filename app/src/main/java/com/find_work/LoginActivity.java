package com.find_work;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout loginEmail, loginPassword;
    Button btnLogin, btnRegister;
    TextView login;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    String userType;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            if (userType.equals("Worker") && currentUser.getEmail().startsWith("w")) {
                Intent worker = new Intent(LoginActivity.this, WorkerDashboardActivity.class);
                startActivity(worker);
                finish();
            } else if (userType.equals("Client") && currentUser.getEmail().startsWith("c")) {
                Intent client = new Intent(LoginActivity.this, ClientDashboardActivity.class);
                startActivity(client);
                finish();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Retrieving data from the Intent
        Intent receivedIntent = getIntent();
        userType = receivedIntent.getStringExtra("userType");

        login = findViewById(R.id.login);
        login.setText(userType + " Login");

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent client = new Intent(LoginActivity.this, RegisterActivity.class);
                client.putExtra("userType", userType);
                startActivity(client);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;

                if (userType.equals("Worker")) {
                    email = "w-"+String.valueOf(loginEmail.getEditText().getText());
                } else if (userType.equals("Client")) {
                    email = "c-"+String.valueOf(loginEmail.getEditText().getText());
                } else {
                    email = String.valueOf(loginEmail.getEditText().getText());
                }

                password = String.valueOf(loginPassword.getEditText().getText());

                if (email.equals("w-") || email.equals("c-")) {
                    Toast.makeText(LoginActivity.this, "email is empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "password is empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    if (userType.equals("Worker")) {
                                        Intent worker = new Intent(LoginActivity.this, WorkerDashboardActivity.class);
                                        startActivity(worker);
//                                        finish();
                                    } else if (userType.equals("Client")) {
                                        Intent client = new Intent(LoginActivity.this, ClientDashboardActivity.class);
                                        startActivity(client);
//                                        finish();
                                    }
                                    loginEmail.getEditText().setText("");
                                    loginPassword.getEditText().setText("");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}