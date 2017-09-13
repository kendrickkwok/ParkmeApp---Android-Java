package com.example.joseph.parkmeapplication;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.StrictMode;

/**
 * Created by Joseph on 4/10/2017.
 */

public class GetCoordinates {
    /*
    Variable Declarations
     */
    ArrayAdapter<String> adapter;
    String address = "http://sfsuse.com/~kkwok/Files/GetCoordinates.php";
    ListView listview;
    String line = null;
    String result = null;
    String[] data = new String[100];
    InputStream is = null;
    Coordinates[] coordinates;


    public void getData() {
    /* important line for gson */
        Gson gson = new Gson();
        HttpURLConnection connection = null;
        URL url = null;
        String json = "";

        try {
            url = new URL("http://sfsuse.com/~kkwok/Files/GetCoordinates.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                json = sb.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

    /* important line for gson */
    Log.d("tag", json);
        coordinates = gson.fromJson(json, Coordinates[].class);

        /*
        // Output
        System.out.println("User ID: " + coordinates[0].getUserId());
        System.out.println("Username: " + coordinates[0].getLicense());
        System.out.println("Name: " + coordinates[0].getName());
        System.out.println("Latitude: " + coordinates[0].getLatitude() + " | " +
                "Longitude: " + coordinates[0].getLongitude());
        System.out.println("User ID: " + coordinates[1].getUserId());
        System.out.println("Username: " + coordinates[1].getLicense());
        System.out.println("Name: " + coordinates[1].getName());
        System.out.println("Latitude: " + coordinates[1].getLatitude() + " | " +
                "Longitude: " + coordinates[1].getLongitude());
*/

    }

    public Coordinates[] returnCoordinates() {
        return coordinates;
    }
/*
    public String[] returnData() {
        return data;
    }
    */
}