package com.example.joseph.parkmeapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.joseph.parkmeapplication.POJO.Example;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.R.attr.name;

public class MapsActivityBuyer extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private CountDownTimer countDownTimer;
    private GoogleMap mMap;
    double latitude, alterLatitude, alterLongitude;
    double longitude;
    double lat, longi;
    private int PROXIMITY_RADIUS = 1;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ArrayAdapter<String> adapter;
    String[] listData;
    Coordinates[] coordinates;
    LatLng origin, dest;
    LatLng originTest;
    Polyline line;
    TextView calculateDistance, timer;
    Button btnCircle, findPins, btnRestaurant, btnDropAPin, btnGo, btnCancel, payButton, btnClear, btnRefresh;
    String name1, username, alterName, name2, username2;
    public Timer myTimer;
    public MyTimerBuyer myTimerTask;
    int license, license2;
    private int Counter = 0;
    private int Counter1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_buyer);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        myTimer = new Timer();
        myTimerTask = new MyTimerBuyer();

        //Initializing
        Intent intent = getIntent();
        name1 = intent.getStringExtra("name");
        username = intent.getStringExtra("username");
        license = intent.getIntExtra("age", -1);

        name2 = name1;
        username2 = username;
        license2 = license;

        payButton = (Button) findViewById(R.id.btnPay);
        calculateDistance = (TextView) findViewById(R.id.calculateDistance);
        timer = (TextView) findViewById(R.id.goText);
        btnGo = (Button) findViewById(R.id.btnGo);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        calculateDistance.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        btnGo.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        payButton.setVisibility(View.GONE);
        btnRefresh.setVisibility(View.GONE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //String[] countries = new String[] { "Hello", "Stupid", "Motherucker"};
        //Get the coordinates
/*
        myTimer = new Timer();
        myTimerTask = new MyTimerBuyer();
        Toast.makeText(MapsActivityBuyer.this,"Fetching from Database...", Toast.LENGTH_LONG).show();
        myTimer.scheduleAtFixedRate(myTimerTask, 0, 5000); //(timertask,delay,period)
*/
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        btnRestaurant = (Button) findViewById(R.id.btnRestaurant);
        btnRestaurant.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        mMap.clear();
                        LatLng position = marker.getPosition();

                        //used to alter the pins number to 1
                        alterLatitude = position.latitude;
                        alterLongitude = position.longitude;

                        MarkerOptions marker1 = new MarkerOptions().position(
                                new LatLng(position.latitude, position.longitude))
                                .title("Potential Seller");
                        marker1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mMap.addMarker(marker1);

                        //Get the URL to find the path of the map
                        dest = new LatLng(position.latitude, position.longitude);
                        // alterLatitude = position.latitude;
                        //alterLongitude = position.longitude;

                        //Move the camera with the origin of the points from the map
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(50));

                        build_retrofit_and_get_response("driving");

                        calculateDistance.setVisibility(View.VISIBLE);
                        btnRestaurant.setVisibility(View.GONE);
                        btnCircle.setVisibility(View.GONE);
                        btnDropAPin.setVisibility(View.GONE);
                        findPins.setVisibility(View.GONE);
                        btnClear.setVisibility(View.GONE);
                        btnGo.setVisibility(View.VISIBLE);

                        //When you press the Go button
                        btnGo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnGo.setVisibility(View.GONE);
                                timer.setVisibility(View.VISIBLE);
                                timer.setBackgroundColor(Color.RED);
                                btnCancel.setVisibility(View.VISIBLE);
//                                btnRefresh.setVisibility(View.VISIBLE);


                                for (int i = 0; i < coordinates.length; i++) {
                                    //grab the name/username of the pin and change the selected vertification to 1
                                    if (coordinates[i].getLatitude() == alterLatitude && coordinates[i].getLongitude() == alterLongitude) {
                                        if (coordinates[i].getSelected() != 1) {
                                            //You have been selcted...Now you can parse through the databases
                                            alterName = coordinates[i].getName();

                                            countDownTimer = new CountDownTimer(300 * 1000, 1000) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {
                                                    timer.setText("" + millisUntilFinished / 1000);
                                                }

                                                @Override
                                                public void onFinish() {
                                                    //  obj.finish;
                                                    Toast.makeText(MapsActivityBuyer.this, "Your timer has expired. You lost your reservation.", Toast.LENGTH_LONG).show();

                                                    myTimerTask.cancel();
                                                    myTimer.cancel();
                                                    Intent intent = new Intent(MapsActivityBuyer.this, UserAreaActivity.class);
                                                    intent.putExtra("name", name1);
                                                    intent.putExtra("username", username);
                                                    intent.putExtra("age", license);
                                                    MapsActivityBuyer.this.startActivity(intent);
                                                    finish();
                                                }
                                            };
                                            countDownTimer.start();
                                            //Set a timer to evoke and send coordinates to the database
                                            Toast.makeText(MapsActivityBuyer.this, "Fetching from Database...", Toast.LENGTH_LONG).show();
                                            myTimer.scheduleAtFixedRate(myTimerTask, 0, 5000); //(timertask,delay,period)

                                        } else {
                                            mMap.clear();
                                            calculateDistance.setVisibility(View.GONE);
                                            timer.setVisibility(View.GONE);
                                            btnGo.setVisibility(View.GONE);
                                            btnCancel.setVisibility(View.GONE);
                                            btnRestaurant.setVisibility(View.VISIBLE);
                                            btnCircle.setVisibility(View.VISIBLE);
                                            btnDropAPin.setVisibility(View.VISIBLE);
                                            findPins.setVisibility(View.VISIBLE);
                                            btnClear.setVisibility(View.VISIBLE);

                                            Toast.makeText(MapsActivityBuyer.this, "Pin already selected! Select another!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                        });
                        return true;
                    }
                });
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MapsActivityBuyer.this, Payment_buyer.class);

                intent.putExtra("name", alterName);
                intent.putExtra("license", username);
                intent.putExtra("alterLatitude", alterLatitude);
                intent.putExtra("alterLongitude", alterLongitude);
                intent.putExtra("BuyerLatitude", latitude);
                intent.putExtra("BuyerLongitude", longitude);
                intent.putExtra("BuyerName", name1);
                intent.putExtra("name2", name2);
                intent.putExtra("username2", username2);
                intent.putExtra("license2", license2);
                MapsActivityBuyer.this.startActivity(intent);
                finish();
                myTimerTask.cancel();
                myTimer.cancel();
            }
        });


        btnCircle = (Button) findViewById(R.id.btnCircle);
        btnCircle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(latitude, longitude))
                        .radius(1000)
                        .strokeColor(Color.RED));

                if(Counter == 0){
                    Counter = 1;
                }
                else
                {
                    mMap.clear();
                    Marker currentLocation = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title("Current Location"));
                    Counter = 0;
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivityBuyer.this, "Canceled. Seller will be notified.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MapsActivityBuyer.this, UserAreaActivity.class);
                intent.putExtra("name", name1);
                intent.putExtra("username", username);
                intent.putExtra("age", license);
                setTo0();
                MapsActivityBuyer.this.startActivity(intent);
                myTimerTask.cancel();
                finish();
            }
        });

        findPins = (Button) findViewById(R.id.findPin);
        findPins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivityBuyer.this, "Refreshed Seller Pins", Toast.LENGTH_LONG).show();
                GetCoordinates gc = new GetCoordinates();
                gc.getData();
                coordinates = gc.returnCoordinates();

                for (int i = 0; i < coordinates.length; i++) {
                    //Check the radius and drop pins
                    if (coordinates[i].getLatitude() > (lat - 0.086205905) && coordinates[i].getLatitude() < (lat + 0.086205905)) {
                        if (coordinates[i].getLongitude() > (longi - 0.10969162) && coordinates[i].getLongitude() < (longi + 0.10969162)) {
                            MarkerOptions marker = new MarkerOptions().position(
                                    new LatLng(coordinates[i].getLatitude(), coordinates[i].getLongitude()))
                                    .title(coordinates[i].getName() + " | " + coordinates[i].getLicense());
                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            mMap.addMarker(marker);
                        }
                    }
                }

            }
        });

        //Clears the map of everything but current location
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mMap.clear();

                Counter = 0;

                Marker currentLocation = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Current Location"));
            }
        });


        btnDropAPin = (Button) findViewById(R.id.btnDropAPin);
        btnDropAPin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng point) {
                        // TODO Auto-generated method stub
                        mMap.clear();

                        Marker dragging = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(point.latitude, point.longitude))
                                .title("Seller Pin")
                                .draggable(true));

                        Marker currentLocation = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title("Buyer Pin"));

                        LatLng latLng = dragging.getPosition();
                        lat = latLng.latitude;
                        longi = latLng.longitude;
                    }
                });

            }
        });
    }

    private void popButton() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("This is still running....");
                payButton.setVisibility(View.VISIBLE);
            }

        });
    }

    //grab the URL for parsing
    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        //String str_origin = "origin=" + 37.12 + "," + -120.11;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }



    /*
    private void MapDrop(final double latitude1, final double longitude1)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.clear();

                Marker buyersMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude1, longitude1))
                        .title(name));
            }
        });
    }
*/

    public class MyTimerBuyer extends TimerTask {


        Coordinates[] coordinates;
        /*
        private String name, license, buyerName;
        //Own marker
        private double alterLatitude, alterLongitude;
        //Buyer's marker
        private double BuyerLatitude, BuyerLongitude;
        private GoogleMap mMap;
        */
     //   private double latitude, longitude;
/*
        public void setMyTimerBuyer(double latitude, double longitude)
        {
            this.latitude = latitude;
        }
  */
        private double BuyerLatitude, BuyerLongitude;
        private String name;

        @Override
        public void run() {
            //Run this code to get location
            Log.d("Logger", "Hello2");

            com.android.volley.Response.Listener<String> getResponse2 = new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //Once succesfful response, then output success
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                        } else {
                        }
                    } catch (JSONException e) {
                        System.out.println(e);
                    }
                }
            };

            //RequestQueue needs : Network to perform transport of requests and cache for handling
            AlterCoordinates ac = new AlterCoordinates(alterName, username, alterLatitude, alterLongitude,
                    latitude, longitude, name1, getResponse2);
            RequestQueue queue2 = Volley.newRequestQueue(MapsActivityBuyer.this);
            queue2.add(ac);

            System.out.println("latitude = " + latitude +  "    longitude = " + longitude);
            //Toast.makeText(MapsActivityBuyer.this,"Latitude" + Double.toString(BuyerLatitude), Toast.LENGTH_LONG).show();
            //Toast.makeText(MapsActivityBuyer.this,"longitude" + Double.toString(BuyerLongitude), Toast.LENGTH_LONG).show();

            //Retrieve the points to compare and contrast
            GetCoordinates gc = new GetCoordinates();
            gc.getData();
            coordinates = gc.returnCoordinates();

            for (int i = 0; i < coordinates.length; i++) {

                if (coordinates[i].getLatitude() > (coordinates[i].getBuyerLatitude() - 0.00040) && coordinates[i].getLatitude() < (coordinates[i].getBuyerLatitude() + 0.00040)) {
                    Log.d("TestinggetLatitude", coordinates[i].getLatitude() + " ");
                    Log.d("TestinggetBuyerLatitude", coordinates[i].getBuyerLatitude() + " ");
                    if (coordinates[i].getLongitude() > (coordinates[i].getBuyerLongitude() - 0.00040) && coordinates[i].getLongitude() < (coordinates[i].getBuyerLongitude() + 0.00040)) {
                        Log.d("TestinggetLongitude", coordinates[i].getLongitude() + " ");
                        Log.d("TestinggeBuyerLongitude", coordinates[i].getBuyerLongitude() + " ");
                        Log.d("Testing", name1);
                        Log.d("TestingSellierName", coordinates[i].getName());
                        Log.d("Testing", coordinates[i].getBuyerName());
                        if (coordinates[i].getBuyerName().equals(name1)) {
                            Log.d("Testing", "Do you see this?");
                            popButton();
                        }
                    }
                }
            }
        }
    }



/* CALCULATE THE DISTANCE AND THE TIME IT TAKES FOR THE USER TO REACH DESTINATION */

    private void build_retrofit_and_get_response(String type) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", origin.latitude + "," + origin.longitude,dest.latitude + "," + dest.longitude, type);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {

                try {
                    //Remove previous line from map
                    if (line != null) {
                        line.remove();
                    }
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        calculateDistance.setText("Distance:" + distance + ", Duration:" + time);
                        String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = decodePoly(encodedString);
                        line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(20)
                                .color(Color.RED)
                                .geodesic(true)
                        );
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(1);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        origin = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(origin);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        if (Counter1 == 0) {
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
        Counter1 ++;
        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));
        this.latitude = latitude;


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
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


    public void setTo0()
    {
        com.android.volley.Response.Listener<String> getResponse3 = new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Once succesfful response, then output success
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                    } else {
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        };

        //RequestQueue needs : Network to perform transport of requests and cache for handling
        setBuyerTo0 sb0= new setBuyerTo0(alterName, username, alterLatitude, alterLongitude,
                latitude, longitude, name1, getResponse3);
        RequestQueue queue3 = Volley.newRequestQueue(MapsActivityBuyer.this);
        queue3.add(sb0);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

}
