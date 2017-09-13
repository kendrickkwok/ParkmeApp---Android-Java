package com.example.joseph.parkmeapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import com.google.android.gms.maps.model.Polyline;

import retrofit.Callback;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private CountDownTimer countDownTimer;
    //TextView timer = (TextView)findViewById(R.id.time);
    double latitude;
    double longitude;
    double lat, longi;
    String name, username;
    int license;
    private int PROXIMITY_RADIUS = 1;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    Button Cancel, btnCircle, btnRestaurant, payButton;
    TextView timer;
    public Timer myTimer;
    private MyTimerSeller myTimerTask;
    private LatLng origin, dest;
    Polyline line;
    TextView calculateDistance;
    int Counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        myTimer = new Timer();
        myTimerTask= new MyTimerSeller();
        Cancel = (Button) findViewById(R.id.Cancel);
        payButton = (Button) findViewById(R.id.button4);
        calculateDistance = (TextView) findViewById(R.id.Directions);
        Cancel.setVisibility(View.GONE);
        payButton.setVisibility(View.GONE);
        calculateDistance.setVisibility((View.GONE));
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        username = intent.getStringExtra("username");
        license = intent.getIntExtra("age", -1);
        timer = (TextView)findViewById(R.id.time);

        timer.setVisibility(View.GONE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
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
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

         btnRestaurant = (Button) findViewById(R.id.btnRestaurant);
        //Sends the coordinatse to the database for parsing
        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            String Restaurant = "restaurant";
            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");

                myTimerTask.setMyTimerSeller(latitude, longitude, mMap);
                myTimer.scheduleAtFixedRate(myTimerTask, 0, 5000); //(timertask,delay,period)

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
                                Toast.makeText(getApplicationContext(), "Coordinates posted: 5 Minutes countdown", Toast.LENGTH_LONG).show();
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "Server Error: Posted", Toast.LENGTH_LONG).show();
                            }
                        }

                        catch(JSONException e)
                        {
                            System.out.println(e);
                        }
                    }
                };

                //RequestQueue needs : Network to perform transport of requests and cache for handling

                RequestCoordinates newRequest = new RequestCoordinates(name, username, latitude, longitude, getResponse);
                RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                queue.add(newRequest);

                //btnRestaurant.setEnabled(false);
                btnRestaurant.setVisibility(View.GONE);
                btnCircle.setVisibility(View.GONE);
                Cancel.setVisibility(View.VISIBLE);

                //start();


                timer.setVisibility(View.VISIBLE);
                timer.setBackgroundColor(Color.RED);

                countDownTimer = new CountDownTimer(300 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        timer.setText("" + millisUntilFinished / 1000);
                    }

                    @Override
                    public void onFinish() {
                        //  obj.finish;
                        Toast.makeText(MapsActivity.this,"Your timer has expired. Your pin has been deleted. Try again.", Toast.LENGTH_LONG).show();
                        timer.setBackgroundColor(Color.WHITE);
                        Cancel.setVisibility(View.GONE);
                        timer.setVisibility(View.GONE);
                        btnRestaurant.setVisibility(View.VISIBLE);
                        btnCircle.setVisibility(View.VISIBLE);
                        mMap.clear();
                        myTimer.cancel();

                        deleteEntry();
                    }
                };

                countDownTimer.start();
                //btnRestaurant.setVisibility(View.VISIBLE);
              //  btnRestaurant.setEnabled(true);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Canceled. Make another selection..", Toast.LENGTH_LONG).show();
                deleteEntry();
                Intent intent = new Intent(MapsActivity.this, UserAreaActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("username", username);
                intent.putExtra("age", license);
                MapsActivity.this.startActivity(intent);
                myTimerTask.cancel();
                countDownTimer.cancel();
                finish();
            }
        });


        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MapsActivity.this, Payment_seller.class);
                intent.putExtra("name", name);
                intent.putExtra("username", username);
                intent.putExtra("age", license);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                MapsActivity.this.startActivity(intent);
                myTimerTask.cancel();
                countDownTimer.cancel();
                finish();
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

                //Loop for the radius to delete any circles
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
    }


    public void deleteEntry()
    {
        //Request for the register to be posted on screen, JSONObject Request
        Response.Listener<String> getResponse1 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Once succesfful response, then output success
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success)
                    {
                        Toast.makeText(getApplicationContext(), "Pin deleted from database", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(), "Server Error: Posted", Toast.LENGTH_LONG).show();
                    }
                }

                catch(JSONException e)
                {
                    System.out.println(e);
                }
            }
        };

        //RequestQueue needs : Network to perform transport of requests and cache for handling

        DeleteCoordinates dc = new DeleteCoordinates(name, username, latitude, longitude, getResponse1);
        RequestQueue queue1 = Volley.newRequestQueue(MapsActivity.this);
        queue1.add(dc);
    }

    private void popButton()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                payButton.setVisibility(View.VISIBLE);
            }

        });
    }

    private void deselectButton()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                payButton.setVisibility(View.GONE);
            }

        });
    }

    private void MapDrop(final double latitude1, final double longitude1, final String name1)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                        mMap.clear();


                Marker buyersMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude1, longitude1))
                        .title(name1));

                dest = new LatLng(latitude1, longitude1);
                if(latitude1 == 0 && longitude1 == 0) {
                    calculateDistance.setVisibility((View.GONE));
                }
                calculateDistance.setVisibility((View.VISIBLE));
                build_retrofit_and_get_response("driving");
            }
        });
    }

    public class MyTimerSeller extends TimerTask {


        Coordinates[] coordinates;
        String name;
        //Own marker
        double alterLatitude, alterLongitude;
        //Buyer's marker
        double BuyerLatitude, BuyerLongitude;
        private GoogleMap mMap;
        public Thread t;


        public void setMyTimerSeller(double alterLatitude, double alterLongitude, GoogleMap mMap) {
            this.alterLatitude = alterLatitude;
            this.alterLongitude = alterLongitude;
            this.mMap = mMap;
        }

        public void setMyTimerSeller(double alterLatitude, double alterLongitude) {
            this.alterLatitude = alterLatitude;
            this.alterLongitude = alterLongitude;
        }

            @Override
            public void run () {
                GetCoordinates gc = new GetCoordinates();
                gc.getData();
                coordinates = gc.returnCoordinates();


                for (int i = 0; i < coordinates.length; i++) {


                    //grab the name/username of the pin and change the selected vertification to 1
                    if (coordinates[i].getLatitude() == alterLatitude && coordinates[i].getLongitude() == alterLongitude)
                    {
                        name = coordinates[i].getName();
                        BuyerLongitude = coordinates[i].getBuyerLongitude();
                        BuyerLatitude = coordinates[i].getBuyerLatitude();
                    }

                    System.out.println(coordinates[i].getBuyerName());

                    if (coordinates[i].getLatitude() > (coordinates[i].getBuyerLatitude() - 0.00040) && coordinates[i].getLatitude() < (coordinates[i].getBuyerLatitude() + 0.00040)) {
                        if (coordinates[i].getLongitude() > (coordinates[i].getBuyerLongitude() - 0.00040) && coordinates[i].getLongitude() < (coordinates[i].getBuyerLongitude() + 0.00040)) {
                            if (coordinates[i].getName().equals(name)) {
                                popButton();
                                    if(coordinates[i].getSelected() == 0)
                                    {
                                        deselectButton();
                                    }

                            }
                        }
                    }


                }

               MapDrop(BuyerLatitude, BuyerLongitude, name);

            }
    }


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
            public void onResponse(retrofit.Response<Example> response, Retrofit retrofit) {

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
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        origin = latLng;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(MapsActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();

        myTimerTask.setMyTimerSeller(latitude, longitude);
        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

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
            deleteEntry();
            Toast.makeText(this, "Seller Pin Removed. Restarting...", Toast.LENGTH_LONG).show();
            finish();
        }
        return super.onKeyDown(keyCode, event);
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
