package com.example.joseph.parkmeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;


import static android.R.attr.defaultValue;

public class Payment_buyer extends AppCompatActivity {
    String name, username, license, name2, username2;
    double alterLatitude, alterLongitude, BuyerLatitude, BuyerLongitude;
    int license2;
    String BuyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_buyer);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        license = intent.getStringExtra("license");
        name2 = intent.getStringExtra("name2");
        license2 = intent.getIntExtra("license2", defaultValue);
        username2 = intent.getStringExtra("username2");

        alterLatitude = intent.getDoubleExtra("alterLatitude", defaultValue);
        alterLongitude = intent.getDoubleExtra("alterLongitude", defaultValue);
        BuyerLatitude = intent.getDoubleExtra("BuyerLatitude", defaultValue);
        BuyerLongitude = intent.getDoubleExtra("BuyerLongitude", defaultValue);
        BuyerName = intent.getStringExtra("BuyerName");


        Button cancelButton = (Button) this.findViewById(R.id.button5);
        Button sendButton = (Button) this.findViewById(R.id.button2);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Payment_buyer.this, UserAreaActivity.class);
                intent.putExtra("name", name2);
                intent.putExtra("username", username2);
                intent.putExtra("age", license2);
                Payment_buyer.this.startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Payment_buyer.this,"Working it's way...", Toast.LENGTH_LONG).show();

                com.android.volley.Response.Listener<String> responseListener = new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Once succesfful response, then output success
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success)
                            {
                                Toast.makeText(Payment_buyer.this, "Selected Changed to 1", Toast.LENGTH_LONG).show();
                            }

                            else
                            {
                                Toast.makeText(Payment_buyer.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        }

                        catch(JSONException e)
                        {
                            System.out.println(e);
                        }
                    }
                };


                CheckPayment cp = new CheckPayment(name, license, alterLatitude, alterLongitude,
                        BuyerLatitude, BuyerLongitude, BuyerName, responseListener);
                RequestQueue queue2 = Volley.newRequestQueue(Payment_buyer.this);
                queue2.add(cp);

                Intent intent = new Intent(Payment_buyer.this, UserAreaActivity.class);
                intent.putExtra("name", name2);
                intent.putExtra("username", username2);
                intent.putExtra("age", license2);
                Payment_buyer.this.startActivity(intent);
                finish();
            }
        });

    }

}
