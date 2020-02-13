package rw.twaza.umushoferi;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rw.twaza.umushoferi.Common.Common;
import rw.twaza.umushoferi.Helper.DirectionsJSONParser;
import rw.twaza.umushoferi.Helper.LocationDetails;
import rw.twaza.umushoferi.Remote.IFMCService;
import rw.twaza.umushoferi.Remote.IGoogleAPI;
import rw.twaza.umushoferi.mode.FCMResponse;
import rw.twaza.umushoferi.mode.Notification;
import rw.twaza.umushoferi.mode.Sender;
import rw.twaza.umushoferi.mode.Token;


public class DriverTracking extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient
        .OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,
        LocationListener
{

    private GoogleMap mMap;
    String riderlat,riderlng;
    String customerId;
    private static  final int  PLAY_SERVICE_RES_REQUEST =7001;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    String time;
    public static int UPDATE_INTERVAL =5000;
    public static  int FATEST_INTERVAL =3000;
    public static int DISOLACEMENT =10;
    private Circle riderMark;
    private Marker driverMarker;
    private Polyline direction;
    IGoogleAPI mService;
    IFMCService mFCMService;
    GeoFire geoFire;
    Button btnstrattrips;
    Location pickeruplocation;
    TextView tvTrackLocation, tvTimeNow, tvStartTime, tvEndTime, tvDistanceCovered, tvTotalAmount;
    LocationDetails locationDetails;
    ArrayList<LocationDetails> locationDetailsList;
    double initialKilometerCost, otherKilometerCost;
    float distanceCovered;
    double googleCalculatedDistance;
    double totalDistanceCalculated, totalGoogleDistanceCalculated;
    double tripTotalAmount;
    boolean startTrip;
    double convertedDistance;
    String startTime, endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);
        locationDetails = null;
        locationDetailsList = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        distanceCovered = 0;
        startTrip = false;
        googleCalculatedDistance = 0;
        totalDistanceCalculated = 0;
        totalGoogleDistanceCalculated = 0;
        initialKilometerCost = 150;
        otherKilometerCost = 80;
        tripTotalAmount = 0;

        tvTimeNow = (TextView)findViewById(R.id.tv_time_now);
        tvTotalAmount = (TextView)findViewById(R.id.tv_total_fee);
        tvDistanceCovered = (TextView)findViewById(R.id.tv_distance_covered);
        tvEndTime = (TextView)findViewById(R.id.tv_end_time);
        tvStartTime = (TextView)findViewById(R.id.tv_start_time);
        Timer timer = new Timer("Display Timer");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Task to be executed every second

                DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cali = Calendar.getInstance();
                cali.getTime();
                time = timeFormat.format(cali.getTimeInMillis());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTimeNow.setText(time);
                    }
                });
            }
        };

        // This will invoke the timer every second
        timer.scheduleAtFixedRate(task, 1000, 1000);

        // END GET DATE AND TIME

//        tvTrackLocation.setText("" + dateFormat.format(date));
        if (getIntent() !=null)
        {
            riderlat = getIntent().getStringExtra("lat");
            riderlng = getIntent().getStringExtra("lng");
            customerId = getIntent().getStringExtra("customerId");
        }else{
            //riderlat = -1.9325464;
            //riderlng = 30.1011831;
        }
        mService = Common.getGoogleApi();
        mFCMService = Common.getFCMService();
        setUpLocation();
        btnstrattrips = (Button)findViewById(R.id.btnstrartTrip);
        btnstrattrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnstrattrips.getText().equals("START TRIP"))
                {
                    startTrip = true;
                    locationDetailsList.clear();
                    DateFormat startTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar startCali = Calendar.getInstance();
                    startCali.getTime();
                    startTime = startTimeFormat.format(startCali.getTimeInMillis());
                    tripTotalAmount = 0;
                    distanceCovered = 0;
                    tvDistanceCovered.setText(Html.fromHtml("<b>Total distance : </b>" + distanceCovered + " Km"));
                    tvTotalAmount.setText(Html.fromHtml("<b>Total amount : </b>" + tripTotalAmount + " Frw"));
                    tvEndTime.setText(Html.fromHtml("<b>End time :</b> Moving"));
                    tvStartTime.setText(Html.fromHtml("<b>Start time : </b>" + startTime));
                    tvDistanceCovered.setVisibility(View.VISIBLE);
                    tvTotalAmount.setVisibility(View.VISIBLE);
                    tvStartTime.setVisibility(View.VISIBLE);
                    tvEndTime.setVisibility(View.VISIBLE);
                    pickeruplocation = Common.mLastLocation;
                    btnstrattrips.setText("END TRIP");
                }
                else if (btnstrattrips.getText().equals("END TRIP"))
                {
                    DateFormat endTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar endTimeCali = Calendar.getInstance();
                    endTimeCali.getTime();
                    endTime = endTimeFormat.format(endTimeCali.getTimeInMillis());
                    tvEndTime.setText(Html.fromHtml("<b>End time : </b>" + endTime));
                    Runnable toDetailsRunnable = new Runnable() {
                        @Override
                        public void run() {
                            calculatecashfee();
                        }
                    };

                    new Handler().postDelayed(toDetailsRunnable, 2000);

                    //Toast.makeText(DriverTracking.this, "Drop off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculatecashfee() {

        Intent intent = new Intent(DriverTracking.this, TripDitailes.class);
        intent.putExtra("start_time", startTime);
        intent.putExtra("end_time", endTime);
        intent.putExtra("distance", String.valueOf(convertedDistance));
        intent.putExtra("total_amount", String.valueOf(tripTotalAmount));
        intent.putParcelableArrayListExtra("trip_locations", locationDetailsList);
        startActivity(intent);
        finish();

    }

    private void setUpLocation()

    {
        if (ckeckplayServices())
        {
            buildGoogleApiClient();
            createLocationRequest();

        }

    }
    private void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISOLACEMENT);

    }

    private void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    private boolean ckeckplayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)

        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This divice is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }


        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean isSuccess =googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this,R.raw.twaza_style_map)
            );
            if (!isSuccess)
                Log.e("ERROR","Map style load failed");
        }
        catch (Resources.NotFoundException ex)
        {
            ex.printStackTrace();
        }
        mMap = googleMap;
        riderMark = mMap.addCircle(new CircleOptions()
                .center(new LatLng(Double.parseDouble(riderlat), Double.parseDouble(riderlng)))
                .radius(50)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(Common.driver_tbl));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(Double.parseDouble(riderlat), Double.parseDouble(riderlng)),0.05f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
//                sendArriveNotification(customerId);
                btnstrattrips.setEnabled(true);

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

//    private void sendArriveNotification(String customerId) {
//        Token token = new Token(customerId);
//        Notification notification = new Notification("MARA DRAVA",String.format("DRIVER  WAITING FOR YOU",Common.currentUser.getFname()));
//        Sender sender = new Sender(token.getToken(),notification);
//
//        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
//            @Override
//            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                if (response.body().success!=1)
//                {
//                    Toast.makeText(DriverTracking.this, "habayemo ikosa", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//            }
//        });
//    }

    private void startLocationUpdate()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED   )
        {
            Log.d("LocationUpdateIssue", "Error in updating locations");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
    }

    private void displayLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED   )
            Common.mLastLocation =LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (Common.mLastLocation !=null)
        {

            final double latitude = Common.mLastLocation.getLatitude();
            final double longitude = Common.mLastLocation.getLongitude();
            locationDetails = new LocationDetails(longitude, latitude);
            locationDetailsList.add(locationDetails);

            if(locationDetailsList.size() > 1) {
                for (int i = 0; i < locationDetailsList.size() - 1; i++) {
                    LocationDetails originLocation = locationDetailsList.get(i);
                    LocationDetails destinationLocation = locationDetailsList.get(i + 1);
                    double originLongitude = originLocation.getLongitude();
                    double originLatitude = originLocation.getLatitude();

                    double nextLongitude = destinationLocation.getLongitude();
                    double nextLatitude = destinationLocation.getLatitude();

                    distanceCovered = GetDistanceFromCurrentPosition(originLatitude, originLongitude, nextLatitude, nextLongitude);
                    LatLng originLatLong = new LatLng(originLatitude, originLongitude);
                    LatLng nextLatLong = new LatLng(nextLatitude, nextLongitude);
                    googleCalculatedDistance = SphericalUtil.computeDistanceBetween(originLatLong, nextLatLong);
                    totalDistanceCalculated += distanceCovered;
                    totalGoogleDistanceCalculated += googleCalculatedDistance;

                }
            }
            convertedDistance = totalGoogleDistanceCalculated / 1000;
            String formulaUsed = "";
            if(convertedDistance < 1){
                if(convertedDistance > 0) {
                    tripTotalAmount = initialKilometerCost * convertedDistance;
                    formulaUsed = "" + initialKilometerCost + " / " + convertedDistance;
                }
                else {
                    tripTotalAmount = 0;
                    formulaUsed = " i " + tripTotalAmount;
                }
            }else{
                double remainderDistance = convertedDistance - 1;
                double remainderDistanceCost = remainderDistance * otherKilometerCost;
                tripTotalAmount = remainderDistanceCost + initialKilometerCost;
                formulaUsed = "" + remainderDistanceCost + " + " + initialKilometerCost;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            tvTotalAmount.setText(Html.fromHtml("<b>Total amount : </b>" + (int)tripTotalAmount  + " Frw"));
            tvDistanceCovered.setText(Html.fromHtml("<b>Total distance : </b>" + decimalFormat.format(convertedDistance) + " Km"));
            Log.d("Locations", "displayLocation: " + startTrip);
            //tvTrackLocation.setText("" + totalDistanceCalculated + " / " + totalGoogleDistanceCalculated);
            if (driverMarker != null)
                driverMarker.remove();
            driverMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude))
                    .title("Twaza")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),17.0f));

            if (direction !=null)
                direction.remove();
            //getDirection();
        }


        else {
            Log.d("ERROR","can not get your location");
        }
    }

    public static float GetDistanceFromCurrentPosition(double lat1, double lng1, double lat2, double lng2)
    {
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        int meterConversion = 1609;

        return new Float(dist * meterConversion).floatValue();

    }

    private void getDirection()
    {
        LatLng   currentPostion = new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude());
        String requestApi = null;
        try{
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+currentPostion.latitude+","+currentPostion.longitude+"&"+
                    "destination = " + riderlat + "," + riderlng+"&"+"key="+
                    getResources().getString(R.string.google_direction_api);



            mService.getPath( requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response)
                        {

                            try {

                                //new  parserTask().execute(response.body().toString());
                                //tvTrackLocation.setText(response.body().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t)
                        {
                            Toast.makeText(DriverTracking.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Common.mLastLocation = location;
        displayLocation();
    }

    private class parserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
        ProgressDialog mDialog = new ProgressDialog(DriverTracking.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("mwihangane gakeya.......");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jobject;
            List<List<HashMap<String, String>>> routes =null;
            try {
                jobject = new JSONObject(strings[0]);
                DirectionsJSONParser parser = new  DirectionsJSONParser();
                routes = parser.parse(jobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = new ArrayList<LatLng>();;
            PolylineOptions lineOptions = new PolylineOptions();;
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            MarkerOptions markerOptions = new MarkerOptions();
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                mDialog.dismiss();

            }
            // Drawing polyline in the Google Map for the i-th route
            if(points.size()!=0)mMap.addPolyline(lineOptions);//to avoid crash
        }

    }

}




