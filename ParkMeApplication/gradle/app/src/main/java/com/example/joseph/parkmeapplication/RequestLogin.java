package com.example.joseph.parkmeapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joseph on 3/13/2017.
 */
public class RequestLogin extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://parkmeapp.hostei.com/Login.php";
    private Map<String, String> parameters;

    //Constructor to ask for name, username, age, password, listener
    public RequestLogin(String username, String password, Response.Listener<String> listener)
    {
        //Pass through into volley
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
    }

    public Map<String,String> getParams()
    {
        return parameters;
    }

}
