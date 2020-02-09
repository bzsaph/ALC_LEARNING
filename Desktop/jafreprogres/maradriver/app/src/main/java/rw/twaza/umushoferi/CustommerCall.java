package rw.twaza.umushoferi;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rw.twaza.umushoferi.Common.Common;
import rw.twaza.umushoferi.Remote.IFMCService;
import rw.twaza.umushoferi.Remote.IGoogleAPI;
import rw.twaza.umushoferi.mode.FCMResponse;

import rw.twaza.umushoferi.mode.Notification;
import rw.twaza.umushoferi.mode.Sender;
import rw.twaza.umushoferi.mode.Token;


public class CustommerCall extends AppCompatActivity {
    TextView txtTime,txtAddress,txtDistance;
    Button btnCancel,btnAccept;

    MediaPlayer mediaPlayer;
    IGoogleAPI mService;
    IFMCService mFCMService;
    String customerId;
    String lat;
    String lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);
        mService = Common.getGoogleApi();
        mFCMService =Common.getFCMService();

        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        btnCancel = (Button)findViewById(R.id.btnDecline);
        btnAccept =(Button)findViewById(R.id.btnAccept);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(customerId))
                {
                    Toast.makeText(CustommerCall.this, "cancel driver", Toast.LENGTH_SHORT).show();
                    cancelBooking(customerId);
                }

            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustommerCall.this,DriverTracking.class);

//                send customer notification to another activity

                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("customerId",customerId);
                startActivity(intent);
                finish();
            }
        });


        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        if (getIntent() != null) {
            lat =  getIntent().getStringExtra("lat");
            lng = getIntent().getStringExtra("lng");
            customerId =getIntent().getStringExtra("customer");
            getDirection(lat,lng);
        }
    }

    private void cancelBooking(String customerId) {
        Token token = new Token(customerId);
        Notification notification = new Notification("Cancel","Driver has cancelled your request");
        Sender sender = new Sender(token.getToken(),notification);
        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success ==1)
                        {
                            Toast.makeText(CustommerCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
    }

    private void getDirection(String lat,String lng )
    {

        String requestApi = null;
        try{
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+"mode=driving&"
                    +"transit_routing_preference=less_driving&"
                    +"origin="+Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"
                    +"destination="+lat+","+lng+"&"
                    +"key="+getResources().getString(R.string.google_direction_api);


            Log.d("EDMTDEV",requestApi); //print URL for debug

            mService.getPath( requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response)
                        {

                            try {


                                JSONObject jsonObject= new JSONObject(response.body().toString());
                                JSONArray routers = jsonObject.getJSONArray("routes");
                                JSONObject object = routers.getJSONObject(0);
                                JSONArray legs = object.getJSONArray("legs");
                                JSONObject legsobject = legs.getJSONObject(0);
                                JSONObject  distance = legsobject.getJSONObject("distance");

                                txtDistance.setText(distance.getString("text"));


                                JSONObject time = legsobject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));


                                String address = legsobject.getString("end_address");
                                txtAddress.setText(address);






                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t)
                        {
                            Toast.makeText(CustommerCall.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.release();
        super.onStop();

    }

    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying())
            mediaPlayer.start();
    }
}
