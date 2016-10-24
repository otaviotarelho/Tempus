/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.tempus.R;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ViewMapActivity extends FragmentActivity implements OnMapReadyCallback, DistanceFinder.DF {
    private GoogleMap mMap;
    String currentPosition;
    LatLng currentLat;
    String destination;

    String APIKEY = "AIzaSyDDTNXhkFDJKh5-em6fVXcDpT4Wp5aA7ZI";

    double longitude;
    double latitude;

    LocationManager lm;
    Location location;

    TextView timeTextView, distanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        // ---------------   pegando a posição atual  --------------------
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //requisitando permissões
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        currentPosition = latitude + "," + longitude;
        currentLat = new LatLng(latitude, longitude);

        // ---------------   pegando a posição de destino  --------------------
        //Recebendo a string com as coordenadas, removendo sujeira e transformando em double pra entao transformar em LatLng
        Intent intent = getIntent();
        String destination = intent.getStringExtra("LOCATION");
        String aux[] = destination.split(":");
        destination = aux[1].substring(2, (aux[1].length() - 1));

        Log.v("origem", String.valueOf(currentPosition));
        Log.v("destino", String.valueOf(destination));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.viewMap);
        mapFragment.getMapAsync(this);

        //agora eu vou só chamar a classe que calcula distancia, só pra tu ver
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + currentPosition + "&destinations=" + destination + "&mode=driving&language=pt-BR&avoid=tolls&key=" + APIKEY;
        new DistanceFinder(ViewMapActivity.this).execute(url);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //moving camera into the actual position
        mMap.addMarker(new MarkerOptions().position(currentLat).title("Posição atual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLat));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void setDouble(String result) {
        String res[] = result.split(",");
        Double minutes = (Double.parseDouble(res[0]) / 60);
        int distance = (Integer.parseInt(res[1]) / 1000);
        timeTextView.setText((int) (minutes / 60) + " horas e " + (int) (minutes % 60) + " min");
        distanceTextView.setText(distance + " km");
    }

}
