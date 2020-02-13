package rw.twaza.umushoferi;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rw.twaza.umushoferi.Common.Common;
import rw.twaza.umushoferi.Remote.IGoogleAPI;
import rw.twaza.umushoferi.mode.Token;

public class HomeDriver extends AppCompatActivity
        implements OnNavigationItemSelectedListener,
        OnMapReadyCallback {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    private GoogleMap mMap;
    private static final int My_PERMISION_REQUESTY_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Button sendsms;
    public static int UPDATE_INTERVAL = 5000;
    public static int FATEST_INTERVAL = 3000;
    public static int DISPLACEMENT = 10;
    public FirebaseAuth Auth;
    public DatabaseReference drivar;
    public GeoFire geoFire;
    Marker mCurrent;
    private List<LatLng> polyLineList;
    private Marker carMaker;
    private float v;
    private double lat, lng;
    private LatLng startPostion, endpostion, currentPostion;
    private Handler hander;
    private int index;
    private int next;
    private PlaceAutocompleteFragment places;
    AutocompleteFilter typeFilter;
    private String destinationt;
    private PolylineOptions polylineOptions, blackpolylineoption;
    private Polyline blackpolyline, grayPoline;
    private IGoogleAPI mService;
    DatabaseReference onlineRef, currentUserRef;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    Runnable drawpathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < polyLineList.size() - 1) {
                startPostion = polyLineList.get(index);
                endpostion = polyLineList.get(next);
            }
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    v = valueAnimator.getAnimatedFraction();
                    lng = v*endpostion.longitude+(1-v)*startPostion.longitude;
// Aha niho hatuma imodoka ijya muri direction itari i yayio insted of lati tude long tude
                    lat= v*endpostion.latitude+(1-v)*startPostion.latitude;
                    LatLng newpos = new LatLng(lat, lng);
                    carMaker.setPosition(newpos);
                    carMaker.setAnchor(0.5f, 0.5f);
                    carMaker.setRotation(getBearing(startPostion, newpos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newpos)
                                    .zoom(15.5f)
                                    .build()
                    ));
                }
            });
            valueAnimator.start();
            hander.postDelayed(this, 3000);

        }
    };

    private float getBearing(LatLng startPostion, LatLng endpostion) {
        double lat = Math.abs(startPostion.latitude - endpostion.latitude);
        double lng = Math.abs(startPostion.longitude - endpostion.longitude);

        if (startPostion.latitude < endpostion.latitude && startPostion.longitude < endpostion.longitude)

            return (float) (Math.toDegrees(Math.atan(lng / lat)));

        else if (startPostion.latitude >= endpostion.latitude && startPostion.longitude < endpostion.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);

        else if (startPostion.latitude >= endpostion.latitude && startPostion.longitude >= endpostion.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);

        else if (startPostion.latitude < endpostion.latitude && startPostion.longitude >= endpostion.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    MaterialAnimatedSwitch location_switch;
    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView txtName = (TextView) navigationHeaderView.findViewById(R.id.textDrivername);
        ImageView imageAvatar = (ImageView) navigationHeaderView.findViewById(R.id.image_avatar);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        try {
            txtName.setText(Common.currentUser.getFname());


            if (Common.currentUser.getAvatarUri() != null
                    && !TextUtils.isEmpty(Common.currentUser.getAvatarUri())) {
                Picasso.with(this)
                        .load(Common.currentUser.getAvatarUri())
                        .into(imageAvatar);

            }

        }catch (Exception e){

        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        sendsms = (Button) findViewById(R.id.smssend);
        mapFragment.getMapAsync(this);
        final Handler hander = new Handler();


        sendsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri SMS_URI = Uri.parse("smsto:+250788522501"); //Replace the phone number
                Intent sms = new Intent(Intent.ACTION_VIEW, SMS_URI);
                sms.putExtra("sms_body", "This is test message"); //Replace the message witha a vairable
                startActivity(sms);

            }
        });

//        remove drive online in case he she dont want to be online

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected ");
        currentUserRef = FirebaseDatabase.getInstance().getReference(Common.driver_tbl)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserRef.onDisconnect().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });      //        end of the dirive to be onlineRef



        location_switch = (MaterialAnimatedSwitch) findViewById(R.id.location_switch);
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isonline) {
                if (isonline) {

                    FirebaseDatabase.getInstance().goOnline();
                    if (ActivityCompat.checkSelfPermission(HomeDriver.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(HomeDriver.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    buildLocationRequest();
                    buildLocationCallback();
                    fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                    displayLocation();
                    Snackbar.make(mapFragment.getView(), "you are online", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    FirebaseDatabase.getInstance().goOffline();
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    mCurrent.remove();
                    mMap.clear();
                    hander.removeCallbacks(drawpathRunnable);
                    Snackbar.make(mapFragment.getView(), "you are offline", Snackbar.LENGTH_SHORT)
                            .show();
                }

            }
        });
        polyLineList = new ArrayList<>();
        polyLineList = new ArrayList<>();
        Places.initialize(getApplicationContext(), "AIzaSyCpmMHo0xQs-U_mXlGUOxFOReO0NlKv3CU");

        PlacesClient placesClient = Places.createClient(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCpmMHo0xQs-U_mXlGUOxFOReO0NlKv3CU");
        }

        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {


                try {
                    if (location_switch.isChecked()) {
                        destinationt = place.getAddress().toString();
                        destinationt = destinationt.replace("", "+");

                        getDirection();
                    } else {
                        Toast.makeText(HomeDriver.this, "plase change your stutas online", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                    ex.printStackTrace();

                }
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(HomeDriver.this, "" + status.toString(), Toast.LENGTH_SHORT).show();

            }
        });


        drivar = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
        geoFire = new GeoFire(drivar);

        setUpLocation();

        mService = Common.getGoogleApi();
        updateFirebaseToken();
    }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference tokens = db.getReference(Common.tokeni_tbl);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);
    }

    private void getDirection() {
        currentPostion = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPostion.latitude + "," + currentPostion.longitude + "&" +
                    "destination=" + destinationt + "&" + "key=" +
                    getResources().getString(R.string.google_direction_api);



            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {


                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
                                }

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng LatLng : polyLineList)
                                    builder.include(LatLng);
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                grayPoline = mMap.addPolyline(polylineOptions);


                                blackpolylineoption = new PolylineOptions();
                                blackpolylineoption.color(Color.BLACK);
                                blackpolylineoption.width(5);
                                blackpolylineoption.startCap(new SquareCap());
                                blackpolylineoption.endCap(new SquareCap());
                                blackpolylineoption.jointType(JointType.ROUND);
                                blackpolyline = mMap.addPolyline(blackpolylineoption);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1))
                                        .title("pick Location"));

//                                ANIMATION
                                ValueAnimator polyLinesAnimater = ValueAnimator.ofInt(0, 100);
                                polyLinesAnimater.setDuration(2000);
                                polyLinesAnimater.setInterpolator(new LinearInterpolator());
                                polyLinesAnimater.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = grayPoline.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newpoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newpoints);
                                        blackpolyline.setPoints(p);


                                    }
                                });


                                polyLinesAnimater.start();
                                carMaker = mMap.addMarker(new MarkerOptions().position(currentPostion)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                hander = new Handler();
                                index = -1;
                                next = 1;
                                hander.postDelayed(drawpathRunnable, 3000);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(HomeDriver.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
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

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    //    Because we request  runtime permissin ,we need override on Request permistion methods
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case My_PERMISION_REQUESTY_CODE:
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {

                    buildLocationCallback();
                    buildLocationRequest();
                        if (location_switch.isChecked())
                            displayLocation();
                    }

                }


    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            request  runtime permission
            ActivityCompat.requestPermissions(this, new String[]
                    {

                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION

                    }, My_PERMISION_REQUESTY_CODE);

        } else {
            buildLocationRequest();
            buildLocationCallback();
            if (location_switch.isChecked())
                displayLocation();
        }

    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Common.mLastLocation = location;

                }
                displayLocation();
            }
        };
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Common.mLastLocation = location;

                        if (Common.mLastLocation != null) {
                            if (location_switch.isChecked()) {
                                final double latitude = Common.mLastLocation.getLatitude();
                                final double longitude = Common.mLastLocation.getLongitude();

                                //up date to fire base twazafirebase
                                LatLng Center = new LatLng(latitude, longitude);
                                LatLng northside = SphericalUtil.computeOffset(Center, 100000, 0);
                                LatLng Southside = SphericalUtil.computeOffset(Center, 100000, 180);
                                LatLngBounds bounds = LatLngBounds.builder()
                                        .include(northside)
                                        .include(Southside)
                                        .build();
//                                places.setBoundsBias(bounds);
//                                places.setFilter(typeFilter);


                                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
//                         add marker
                                        if (mCurrent != null)
                                            mCurrent.remove();// remove already mark
                                        mCurrent = mMap.addMarker(new MarkerOptions()
//
                                                .position(new LatLng(latitude, longitude))
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                                .title("MARA DRIVE"));

                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                                    }
                                });
//                Log.d("EDMTDEV",String.format("your location was changed:%f/%f",latitude,longitude));
                            }

                        } else {
                            Log.d("ERROR", "can not get your location");
                        }

                    }
                });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trip_history) {
            // Handle the camera action
        } else if (id == R.id.nav_way_bill) {

        } else if (id == R.id.nav_Help) {

        } else if (id == R.id.nav_settings) {


        } else if (id == R.id.nav_change_pwd) {
            showDialogChangepwd();
        } else if (id == R.id.nav_sign_out) {
            signout();

        } else if (id == R.id.nav_update_info) {
            showDialogUpdateInfo();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialogUpdateInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeDriver.this);
        alertDialog.setTitle("UPDATING INFORMATION");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pswd = inflater.inflate(R.layout.layout_update_information, null);

        final MaterialEditText edtName = (MaterialEditText) layout_pswd.findViewById(R.id.edtName);
        final MaterialEditText edtphone = (MaterialEditText) layout_pswd.findViewById(R.id.edtphone);
        final ImageView image_uploade = (ImageView) layout_pswd.findViewById(R.id.image_uploade);
        image_uploade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        alertDialog.setView(layout_pswd);

        alertDialog.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingdialog = new SpotsDialog(HomeDriver.this);
                waitingdialog.show();
                String name = edtName.getText().toString();
                String phone = edtphone.getText().toString();

                Map<String, Object> updateInfo = new HashMap<>();
                if (!TextUtils.isEmpty(name))
                    updateInfo.put("name", name);
                if (!TextUtils.isEmpty(phone))
                    updateInfo.put("phone", phone);

                DatabaseReference driverOnformation = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);
                driverOnformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())

                                    Toast.makeText(HomeDriver.this, "information Updated !", Toast.LENGTH_SHORT).show();

                                else

                                    Toast.makeText(HomeDriver.this, "information Update error ", Toast.LENGTH_SHORT).show();
                                waitingdialog.dismiss();

                            }
                        });
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


    }

    //  uploade image
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Hitamo ifoto ushaka:"), Common.Pick_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.Pick_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri saveUri = data.getData();
            if (saveUri != null) {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading......");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();//  random name image uploade
                final StorageReference imageFolder = storageReference.child("images/" + imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();

                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
//                                                update this url avatare proprty  of user
//                                                        first you need add avatar property on user moder
                                        Map<String, Object> avataruploade = new HashMap<>();
                                        avataruploade.put("avatarUri", uri.toString());
                                        DatabaseReference driverOnformation = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);
                                        driverOnformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(avataruploade)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())

                                                            Toast.makeText(HomeDriver.this, "uploaded!", Toast.LENGTH_SHORT).show();

                                                        else

                                                            Toast.makeText(HomeDriver.this, "uploaded error", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("uploaded" + progress + "%");

                            }
                        });
            }
        }
    }

    private void showDialogChangepwd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeDriver.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pswd = inflater.inflate(R.layout.layout_change_pswd, null);

        final MaterialEditText edtpassword = (MaterialEditText) layout_pswd.findViewById(R.id.edtpassword);
        final MaterialEditText edtNewpassword = (MaterialEditText) layout_pswd.findViewById(R.id.edtNewpassword);
        final MaterialEditText edtRepeatpassword = (MaterialEditText) layout_pswd.findViewById(R.id.edtRepeatpassword);
        alertDialog.setView(layout_pswd);

        alertDialog.setPositiveButton("CHANGE PASSWORD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeDriver.this);
                waitingDialog.show();
                if (edtNewpassword.getText().toString().equals(edtRepeatpassword.getText().toString())) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    // get  auth credentials from the user for re- authentication
                    //example with only email
                    AuthCredential credential = EmailAuthProvider.getCredential(email, edtpassword.getText().toString());
                    FirebaseAuth.getInstance().getCurrentUser()
                            .reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser()
                                                .updatePassword(edtRepeatpassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> password = new HashMap<>();
                                                            password.put("password", edtRepeatpassword.getText().toString());

                                                            DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);
                                                            driverInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .updateChildren(password)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())

                                                                                Toast.makeText(HomeDriver.this, "umubare wi ibanga wahindutse", Toast.LENGTH_SHORT).show();

                                                                            else
                                                                                Toast.makeText(HomeDriver.this, "umubare wi ibanga wahindutse ariko ntago wabitse mububiko", Toast.LENGTH_SHORT).show();
                                                                            waitingDialog.dismiss();
                                                                        }
                                                                    });

                                                        } else {
                                                            Toast.makeText(HomeDriver.this, "Password doesn't change", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                });
                                    } else {
                                        waitingDialog.dismiss();
                                        Toast.makeText(HomeDriver.this, " wrong password", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(HomeDriver.this, "password doesn't match", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    //     sign out only this code for sign out firebase
    private void signout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeDriver.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean isSuccess = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.twaza_style_map)
            );
            if (!isSuccess)
                Log.e("ERROR", "Map style load failed");
        } catch (Resources.NotFoundException ex) {
            ex.printStackTrace();
        }

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());

            }
        }
