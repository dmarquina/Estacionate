package com.estacionate.estacionate;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;

public class FindParkingFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationManager lm;
    FrameLayout mapContainer;
    Bundle savedInstanceState;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_find_parking, container, false);
        ButterKnife.bind(this, v);
        this.savedInstanceState = savedInstanceState;
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.e("PERMISSION", "Solicitando permiso de GPS");
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapContainer = (FrameLayout) v.findViewById(R.id.mapContainer);
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            loadMap(v, savedInstanceState);
        }
        return v;
    }

    public void loadMap(View v, Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        try {
            fm.beginTransaction().replace(R.id.mapContainer, supportMapFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MapsInitializer.initialize(this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                FindParkingFragment.this.map = googleMap;


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

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("error4", "error4");
                    return;
                } else {
                    googleMap.setMyLocationEnabled(true);
                    showParkings();
                }

            }
        });
    }

    public void showParkings() {
        if(MainActivity.myPosition!=null){
            double longitude = 0.0;
            double latitude = 0.0;
            latitude = MainActivity.myPosition.latitude;
            longitude = MainActivity.myPosition.longitude;
            Toast.makeText(getActivity(), ""+latitude+" "+longitude, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "Bienvenido.", Toast.LENGTH_SHORT).show();
        }
/*        if (MainActivity.myPosition == null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new MarkerOptions().position(new LatLng(-10.496425, -74.745169)).getPosition(), 4.0f));
        else
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new MarkerOptions().position(new LatLng(center.getLatitude(), center.getLongitude())).getPosition(), 16.0f));
*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) getActivity());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getActivity(), "lo sentimos no podemos obtener ubicación actual", Toast.LENGTH_SHORT).show();
        } else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            map.animateCamera(update);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("error5", "error5");
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("error6", "error6");
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        Log.e("error7", "error7");

                        mapContainer = (FrameLayout) v.findViewById(R.id.mapContainer);

                        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                        loadMap(v, savedInstanceState);
                    } else {
                        Toast.makeText(getActivity(), "lo sentimos no podemos obtener ubicación actual :/", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("PERMISSION", "Denied");
                    mapContainer = (FrameLayout) v.findViewById(R.id.mapContainer);
                    lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    loadMap(v, savedInstanceState);
                }
                return;
            }
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "lo sentimos no permisos", Toast.LENGTH_SHORT).show();
                } else {

                }
                return;
        }
    }
}
