package com.example.joseph.parkmeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText textUsername = (EditText) findViewById(R.id.textUsername);
        final EditText textPassword = (EditText) findViewById(R.id.textPassword);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        final TextView registerLink = (TextView) findViewById(R.id.registerLink);

        //Using the link, click on register here to travel to another page
        registerLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Declare intents, open register class with from mainActivity
                Intent registerTransfer = new Intent(MainActivity.this, RegisterActivity.class);
                //Perform the intent from Main Activity
                MainActivity.this.startActivity(registerTransfer);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Variable declarations
                final String username = textUsername.getText().toString();
                final String password = textPassword.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        //Convert to JSONObject to work rwith the response
                        try {
                            //Once succesfful response, then output success
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success)
                            {
                                /*
                                Toast.makeText(getApplicationContext(), "Yes1", Toast.LENGTH_LONG).show();
                                String name = jsonResponse.getString("name");
                                int age = jsonResponse.getInt("age");
                                Toast.makeText(getApplicationContext(), "Yes2", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, UserAreaActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("username", username);
                                intent.putExtra("age", age);
                                Toast.makeText(getApplicationContext(), "Yes3", Toast.LENGTH_LONG).show();
                                MainActivity.this.startActivity(intent);
                                */
                                Intent intent = new Intent(MainActivity.this, UserAreaActivity.class);
                                String name = jsonResponse.getString("name");
                                int age = jsonResponse.getInt("age");
                                intent.putExtra("name", name);
                                intent.putExtra("username", username);
                                intent.putExtra("age", age);
                                MainActivity.this.startActivity(intent);
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

                RequestLogin requestLogin = new RequestLogin(username, password, responseListener);
                RequestQueue rq = Volley.newRequestQueue(MainActivity.this);
                rq.add(requestLogin);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}