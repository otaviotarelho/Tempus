package com.tempus.Alarm;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.tempus.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TravelTimeProvider extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    public static final String TAG = TravelTimeProvider.class.getSimpleName();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private boolean permissionIsGranted = false;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;



    LatLng myCurrentLocation;
    LatLng destLat = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_travel_time_provider);
        Bundle data = getIntent().getExtras();
        String destination = data.getString("EVENT_LOCATION");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        getDestinationPosition(destination);

        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(60 * 1000);
        //mLocationRequest.setFastestInterval(15 * 1000); não necessário, só util para testes
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }


    @Override
    public void onConnectionSuspended(int i) {}


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionIsGranted) {
            if (mGoogleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionIsGranted) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (permissionIsGranted) {
            mGoogleApiClient.disconnect();
        }
    }


    protected void getDestinationPosition(String destination) {
        if(destination != null){
            String aux[] = destination.split(":");
            destination = aux[1].substring(2, (aux[1].length() - 1));
            String aux2[] = destination.split(",");
            destLat = new LatLng(Double.valueOf(aux2[0]), Double.valueOf(aux2[1]));
        }
    }



    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permissão garantida
                    permissionIsGranted = true;
                } else {
                    //permissão negada
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), R.string.location_permission, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution() && getApplicationContext() instanceof Activity) {
            try {
                Activity activity = (Activity) getApplicationContext();
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(destLat != null) {
            myCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            String url = getUrl(myCurrentLocation, destLat);
            Log.d("URL", url);
            FetchUrl FetchUrl = new FetchUrl();
            FetchUrl.execute(url);
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        if(dest != null) {
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
            String sensor = "sensor=false";
            String parameters = str_origin + "&" + str_dest + "&" + sensor;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
            return url;
        }
        return null;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... jsonData) {
            String travelTime = "";
            JSONObject jObject;
            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                travelTime = parser.parseTravelTime(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return travelTime;
        }

        @Override
        protected void onPostExecute(String s) {
            setRouteInfo(s);
        }
    }
    public void setRouteInfo(String routeInfo) {
        String[] aux = routeInfo.split(",");
        routeInfo = aux[1].substring(8, (aux[1].length() - 1));
        int seconds = Integer.parseInt(routeInfo);
        int minutes = seconds / 60;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", String.valueOf(minutes));
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
