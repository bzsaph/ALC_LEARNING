package rw.twaza.umushoferi.Helper;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationDetails implements Parcelable {

    double longitude;
    double latitude;

    LocationDetails(){}

    public LocationDetails(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LocationDetails(Parcel in) {
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.latitude);
    }

    public void readFromParcel(Parcel parcel){
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
    }

    public static final Parcelable.Creator<LocationDetails> CREATOR = new Parcelable.Creator<LocationDetails>() {

        public LocationDetails createFromParcel(Parcel in) {
            return new LocationDetails(in);
        }

        public LocationDetails[] newArray(int size) {
            return new LocationDetails[size];
        }
    };

}
