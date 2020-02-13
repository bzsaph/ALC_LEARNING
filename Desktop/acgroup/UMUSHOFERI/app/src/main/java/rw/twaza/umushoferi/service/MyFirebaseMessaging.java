package rw.twaza.umushoferi.service;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import rw.twaza.umushoferi.CustommerCall;


public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d("EARLYBIRD", String.valueOf(remoteMessage));
        if (remoteMessage.getData() != null) {
            Map<String, String> data = remoteMessage.getData();
            String customer = data.get("customer");
            String lat = data.get("lat");
            String lng = data.get("lng");


//        LatLng Customer_location=new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);
            Intent intent = new Intent(getBaseContext(), CustommerCall.class);

            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.putExtra("customer", customer);

            startActivity(intent);
        }
    }
}
