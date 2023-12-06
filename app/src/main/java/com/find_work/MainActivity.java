package com.find_work;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnWorker, btnClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWorker = findViewById(R.id.btnWorker);
        btnClient = findViewById(R.id.btnClient);

        btnWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent worker = new Intent(MainActivity.this, LoginActivity.class);
                worker.putExtra("userType", "Worker");
                startActivity(worker);
//                finish();
            }
        });

        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent client = new Intent(MainActivity.this, LoginActivity.class);
                client.putExtra("userType", "Client");
                startActivity(client);
//                finish();
            }
        });

    }

    //When try to back from Login page this method will execute
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit Find Work ?");

        //When click "Yes" it will execute this
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                onDestroy();
                System.exit(0);
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