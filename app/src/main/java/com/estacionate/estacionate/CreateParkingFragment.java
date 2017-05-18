package com.estacionate.estacionate;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CreateParkingFragment extends Fragment {

    GoogleMap map;
    MapView mapView;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        View v = inflater.inflate(R.layout.fragment_create_parking, container, false);

        mapView = (MapView) v.findViewById(R.id.mapviewselector);
        mapView.onCreate(savedInstanceState);

        loadMap(v, savedInstanceState);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(-12.053623,-77.0852702), 15);
        map.animateCamera(cameraUpdate);
        return v;
    }

    private void loadMap(View v, Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        try {
            fm.beginTransaction().replace(R.id.mapviewselector, supportMapFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            MapsInitializer.initialize(this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CreateParkingFragment.this.map = googleMap;


                // my-location listener
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Log.e("MY_LOCATION", "ok");
                        final LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Log.e("GPS", "disabled");
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                        }
                        return false;
                    }
                });

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("error4", "error4");
                    return;
                } else {
                    googleMap.setMyLocationEnabled(true);
                }

            }
        });

    }
}
