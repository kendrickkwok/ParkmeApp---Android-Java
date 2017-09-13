package com.example.joseph.parkmeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.defaultValue;

public class Payment_seller extends AppCompatActivity {

    private Timer myTimer;
    private PaymentWait paymentWait;
    private String name, age, username, license;
    private Double latitude, longitude;
    private Button payButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_seller);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        username = intent.getStringExtra("username");
        license = intent.getStringExtra("license");
        latitude = intent.getDoubleExtra("latitude", defaultValue);
        longitude = intent.getDoubleExtra("longitude", defaultValue);

        //Variable declarations
        payButton = (Button) findViewById(R.id.button2);
        cancelButton = (Button) findViewById(R.id.btnCancel);
        payButton.setVisibility(View.GONE);
        myTimer = new Timer();
        paymentWait = new PaymentWait();
        paymentWait.setMyTimerTask(name, latitude, longitude);
        myTimer.scheduleAtFixedRate(paymentWait, 0, 5000); //(timertask,delay,period)

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Payment_seller.this, "Transaction successful!", Toast.LENGTH_LONG).show();
                //deleteEntry();
                Intent intent = new Intent(Payment_seller.this, UserAreaActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("username", username);
                intent.putExtra("age", license);
                Payment_seller.this.startActivity(intent);
                deleteEntry();
                myTimer.cancel();
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Payment_seller.this, "Canceled. Transaction not successful!", Toast.LENGTH_LONG).show();
                deleteEntry();
                Intent intent = new Intent(Payment_seller.this, UserAreaActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("username", username);
                intent.putExtra("age", license);
                Payment_seller.this.startActivity(intent);
                deleteEntry();
                myTimer.cancel();
                finish();
            }
        });
    }

    public void setButton() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                payButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public class PaymentWait extends TimerTask {


        Coordinates[] coordinates;
        String sellerName;
        double sellerLatitude, sellerLongitude;
        public Thread t;


        public void setMyTimerTask(String sellerName, double sellerLatitude, double sellerLongitude) {
            this.sellerLatitude = sellerLatitude;
            this.sellerLongitude = sellerLongitude;
            this.sellerName = sellerName;
        }


        @Override
        public void run() {

            GetCoordinates gc = new GetCoordinates();
            gc.getData();
            coordinates = gc.returnCoordinates();


            for (int i = 0; i < coordinates.length; i++) {

                //grab the name/username of the pin and change the selected vertification to 1
                // if (coordinates[i].getLatitude() == sellerLatitude && coordinates[i].getLongitude() == sellerLongitude
                //        && coordinates[i].getName() == sellerName && coordinates[i].getPayment() == 1)
                if (coordinates[i].getLatitude() == sellerLatitude && coordinates[i].getLongitude() == sellerLongitude
                        && coordinates[i].getPayment() == 1) {
                    setButton();
                }
            }

        }
    }

    public void deleteEntry() {
        //Request for the register to be posted on screen, JSONObject Request
        Response.Listener<String> getResponse1 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Once succesfful response, then output success
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        Toast.makeText(getApplicationContext(), "Timer ran out. Deleted pin from database. Try again.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Server Error: Posted", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        };

        DeleteCoordinates dc = new DeleteCoordinates(name, username, latitude, longitude, getResponse1);
        RequestQueue queue1 = Volley.newRequestQueue(Payment_seller.this);
        queue1.add(dc);
    }
}