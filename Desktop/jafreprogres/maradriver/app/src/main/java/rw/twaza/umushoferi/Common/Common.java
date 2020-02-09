package rw.twaza.umushoferi.Common;

import android.location.Location;

import rw.twaza.umushoferi.Remote.FCMClient;
import rw.twaza.umushoferi.Remote.IFMCService;
import rw.twaza.umushoferi.Remote.IGoogleAPI;
import rw.twaza.umushoferi.Remote.RetrofitClient;
import rw.twaza.umushoferi.mode.User;

public class Common
{
    public  static String currentToken="";
    public static  final String driver_tbl ="location";
    public static  final String user_driver_tbl ="driver_location";
    public static  final String User_rider_tbl ="passanger_information";
    public static  final String pickup_request_tbl ="pickupRequest";
    public static  final String tokeni_tbl ="Tokens";
    public static  final  String LoGINURL="http://tapangomara.ticket.rw/api/authenticate";






    public static  final String rate_detail_tb1 ="RateDetails";



    public static final int Pick_IMAGE_REQUEST=9999;
    public  static  double base_fare =2.5;
    private  static   double time_rate = 0.35;
    private static  double distance_rate =1.75;
    public  static  double formulaprice (double km ,double min)
    {
        return base_fare+(distance_rate*km)+(time_rate*min);
    }

    public  static User currentUser;

  public static Location mLastLocation = null;

    public  static final  String baseURL ="https://maps.googleapis.com";
    public  static final  String fcmURL ="https://fcm.googleapis.com/";
    public  static IGoogleAPI getGoogleApi()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
    public  static IFMCService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFMCService.class);
    }
}
