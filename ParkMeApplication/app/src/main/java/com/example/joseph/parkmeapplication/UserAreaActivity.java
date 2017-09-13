package com.example.joseph.parkmeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.joseph.parkmeapplication.R.id.button3;
import static com.example.joseph.parkmeapplication.R.id.button200;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        final TextView welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
        final Button button2 = (Button) findViewById(button200);
        final Button buttonSeller = (Button) findViewById(button3);

        //Get all the information from the intent


        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String username = intent.getStringExtra("username");
        final int age = intent.getIntExtra("age", -1);

        String message = "Hello " + name;
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        welcomeMessage.setText(message);
        //textUsername.setText(message);
        //textAge.setText(age);


        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Declare intents, open register class with from mainActivity
                Intent registerTransfer = new Intent(UserAreaActivity.this, MapsActivityBuyer.class);
                registerTransfer.putExtra("name", name);
                registerTransfer.putExtra("username", username);
                registerTransfer.putExtra("age", age);
                //Perform the intent from Main Activity
                UserAreaActivity.this.startActivity(registerTransfer);
                finish();
            }
        });



        buttonSeller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Declare intents, open register class with from mainActivity
                Intent registerTransfer = new Intent(UserAreaActivity.this, MapsActivity.class);
                registerTransfer.putExtra("name", name);
                registerTransfer.putExtra("username", username);
                registerTransfer.putExtra("age", age);
                //Perform the intent from Main Activity
                UserAreaActivity.this.startActivity(registerTransfer);
            }
        });



    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Toast.makeText(this, "Activity Killed: Do another activity", Toast.LENGTH_LONG).show();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
