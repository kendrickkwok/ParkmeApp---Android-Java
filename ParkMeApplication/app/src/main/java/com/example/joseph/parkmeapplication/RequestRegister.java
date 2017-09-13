package com.example.joseph.parkmeapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joseph on 3/13/2017.
 */
public class RequestRegister extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://sfsuse.com/~kkwok/Files/Register.php";
    private Map<String, String> parameters;

    //Constructor to ask for name, username, age, password, listener
    public RequestRegister(String name, String username, int age, String password, Response.Listener<String> listener)
    {
        //Pass through into volley
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("username", username);
        parameters.put("age", age + "");
        parameters.put("password", password);
    }

    public Map<String,String> getParams()
    {
        return parameters;
    }

}
