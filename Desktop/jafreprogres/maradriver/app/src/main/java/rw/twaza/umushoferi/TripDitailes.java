package rw.twaza.umushoferi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rw.twaza.umushoferi.Common.Common;
import rw.twaza.umushoferi.Helper.LocationDetails;
import rw.twaza.umushoferi.Remote.IGoogleAPI;


public class TripDitailes extends AppCompatActivity implements View.OnClickListener {

    private GoogleMap mMap;
    private TextView  tvDetailStartDate, tvDetailEndDate, tvDetailDistanceCovered, tvDetailTripTotalAmount;
    private RadioButton rdDetailMomo, rdDetailCash, rdDetailCard;
    Button btnDetailsConfirm;
    String strDetailStartDate, strDetailEndDate, strDetailDistanceCovered, strDetailTripTotalAmount;
    String strDetailMomo, strDetailCash, strDetailCard;
    int amountPaid;
    ArrayList<LocationDetails> locationDetails;
    DecimalFormat decimalFormat;
    IGoogleAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_ditailes);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        init();
try {
    if(getIntent() != null){
        locationDetails = getIntent().getParcelableArrayListExtra("trip_locations");
        strDetailStartDate = getIntent().getStringExtra("start_time");
        strDetailEndDate = getIntent().getStringExtra("end_time");
        strDetailDistanceCovered = getIntent().getStringExtra("distance");
        strDetailTripTotalAmount = getIntent().getStringExtra("total_amount");
        tvDetailStartDate.setText(Html.fromHtml("<b>Start time : </b>" + strDetailStartDate));
        tvDetailEndDate.setText(Html.fromHtml("<b>End time : </b>" + strDetailEndDate));
        tvDetailDistanceCovered.setText(Html.fromHtml("<b>Distance : </b>" + decimalFormat.format(Double.parseDouble(strDetailDistanceCovered)) + " Km"));
        tvDetailTripTotalAmount.setText(Html.fromHtml("<b>Total amount : </b>" + Integer.parseInt(strDetailTripTotalAmount) + " Frw"));
    }
}catch (Exception e){

}

    }

    private void init(){
        mService = Common.getGoogleApi();
        decimalFormat = new DecimalFormat("##.##");
        locationDetails = new ArrayList<>();
        tvDetailStartDate = (TextView)findViewById(R.id.tv_details_start_time);
        tvDetailEndDate = (TextView)findViewById(R.id.tv_details_end_time);
        tvDetailDistanceCovered = (TextView)findViewById(R.id.tv_details_distance_covered);
        tvDetailTripTotalAmount = (TextView)findViewById(R.id.tv_details_total_amount);
        btnDetailsConfirm = (Button)findViewById(R.id.btn_details_confirm);
        btnDetailsConfirm.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v == btnDetailsConfirm){

            String url = "http://tapangomara.ticket.rw/api/moto";
            String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3Q6ODAwMFwvYXBpXC9hdXRoZW50aWNhdGUiLCJpYXQiOjE1Nzk3NzAxODgsImV4cCI6MTU3OTc3Mzc4OCwibmJmIjoxNTc5NzcwMTg4LCJqdGkiOiJrbW1tMDVSZ2NrOVR3bGJFIiwic3ViIjoxLCJwcnYiOiI4N2UwYWYxZWY5ZmQxNTgxMmZkZWM5NzE1M2ExNGUwYjA0NzU0NmFhIn0.j0iwPwj5cRNOx6bi28h_t_h7yfEh39JmKS_9orbbNJk";

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization Bearer ", token)
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient();
            //okhttp3.Response response = okHttpClient


            try{

                mService.getPath(url)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response)
                        {
                            Log.d("Response", response.body().toString());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t)
                        {
                            Log.d("Failure", t.getMessage());

                        }
                    });

            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
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

}
