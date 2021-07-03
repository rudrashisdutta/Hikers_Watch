package com.example.hikersmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView appHead;
    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    private TextView accuracy;
    private TextView address;


    private LocationListener locationListener;
    private LocationManager locationManager;

    //TODO- TURN ON LOCATION (if off) or the app stops!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appHead = (TextView) findViewById(R.id.APP_NAME);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        accuracy = (TextView) findViewById(R.id.accuracy);
        altitude = (TextView) findViewById(R.id.altitude);
        address = (TextView) findViewById(R.id.address);

        appHead.setText(R.string.appHead);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = this::updateLocationToUser;
        permissionLocationCheckAndWork();
    }
    private void permissionLocationCheckAndWork(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        } else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation != null){
                updateLocationToUser(lastKnownLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            } else {
                permissionLocationCheckAndWork();
            }
        }
    }

    private void updateLocationToUser(Location location){
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        Log.i("LOCATION:",location.toString());
        latitude.setText(String.format(" LATITUDE:  %s", location.getLatitude()));
        longitude.setText(String.format(" LONGITUDE:  %s", location.getLongitude()));
        altitude.setText(String.format(" ALTITUDE:  %s", location.getAltitude()));
        accuracy.setText(String.format(" ACCURACY:  %s", location.getAccuracy()));
        String addressMessage = "No address found...    :(";
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList!= null && addressList.size()>0){
                addressMessage =  " ADDRESS:\n";
                if(addressList.get(0).getThoroughfare() != null){
                    addressMessage += addressList.get(0).getThoroughfare() + "\n";
                }
                if(addressList.get(0).getLocality() != null){
                    addressMessage += addressList.get(0).getLocality() + "\n";
                }
                if(addressList.get(0).getPostalCode() != null){
                    addressMessage += addressList.get(0).getPostalCode() + "\n";
                }
                if(addressList.get(0).getAdminArea() != null){
                    addressMessage += addressList.get(0).getAdminArea() + "\n";
                }
                if(addressList.get(0).getCountryName() != null){
                    addressMessage += addressList.get(0).getCountryName() + "\n";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        address.setText(addressMessage);
    }
}