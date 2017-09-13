package com.example.joseph.parkmeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Variable declarations
        final EditText textName = (EditText) findViewById(R.id.textName);
        final EditText textUsername = (EditText) findViewById(R.id.textUsername);
        final EditText textPassword = (EditText) findViewById(R.id.textPassword);
        final EditText textAge = (EditText) findViewById(R.id.textAge);
        final Button registerButton = (Button) findViewById(R.id.registerButton);

        //Once the register button is pressed
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = textName.getText().toString();
                final String username = textUsername.getText().toString();
                final int age = Integer.parseInt(textAge.getText().toString());
                final String password = textPassword.getText().toString();

                //Request for the register to be posted on screen, JSONObject Request
                Response.Listener<String> getResponse = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Once succesfful response, then output success
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success)
                            {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "Registration Error", Toast.LENGTH_LONG).show();
                            }
                        }

                        catch(JSONException e)
                        {
                            System.out.println(e);
                        }
                    }
                };

                //RequestQueue needs : Network to perform transport of requests and cache for handling

                RequestRegister newRequest = new RequestRegister(name, username, age, password, getResponse);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(newRequest);
            }
        });
    }
}
