package com.example.joseph.parkmeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        final TextView welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
        final Button button2 = (Button) findViewById(R.id.button200);

        //Get all the information from the intent

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        int age = intent.getIntExtra("age", -1);

        String message = "Hello " + name;
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        welcomeMessage.setText(message);
        //textUsername.setText(message);
        //textAge.setText(age);

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Declare intents, open register class with from mainActivity
                Intent registerTransfer = new Intent(UserAreaActivity.this, MapsActivity.class);
                //Perform the intent from Main Activity
                UserAreaActivity.this.startActivity(registerTransfer);
            }
        });


    }
}
