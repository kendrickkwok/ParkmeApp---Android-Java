package com.example.joseph.parkmeapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joseph on 3/13/2017.
 */
public class AlterCoordinates extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://sfsuse.com/~kkwok/Files/AlterCoordinates.php";
    private Map<String, String> parameters;

    //Constructor to ask for name, username, age, password, listener
    public AlterCoordinates(String name, String license, double latitude, double longitude,
                            double buyerLatitude, double buyerLongitude, String buyerName, Response.Listener<String> listener)
    {
        //Pass through into volley
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("license", license);
        //"" is used to convert the double to a string
        parameters.put("latitude", latitude + "");
        parameters.put("longitude", longitude + "");
        parameters.put("BuyerLatitude", buyerLatitude + "");
        //"" is used to convert the double to a string
        parameters.put("BuyerLongitude", buyerLongitude + "");
        parameters.put("BuyerName", buyerName + "");
    }

    public Map<String,String> getParams()
    {
        return parameters;
    }

}