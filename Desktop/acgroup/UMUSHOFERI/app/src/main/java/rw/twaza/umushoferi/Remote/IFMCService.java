package rw.twaza.umushoferi.Remote;

import com.google.android.gms.common.api.GoogleApiClient;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rw.twaza.umushoferi.mode.FCMResponse;
import rw.twaza.umushoferi.mode.Sender;

public interface IFMCService {
    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAA8sQ82gE:APA91bHBQ7eWsXYWBIkpaRD8YERioRHXGZne63SILIPDUmWHMHORaHO7_S9wgcoTNyyR_D9vg9wGt54dbIFEzlUnqk_Wq8BwfDO54YFvOEpnjmXkkX5dzbQgyHuUKaCMzqrLP_4amjWV"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);


}
