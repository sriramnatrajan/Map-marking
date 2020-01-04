package com.example.taskapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.taskapp.map_server.HyperlinkGoogleMap;
import com.example.taskapp.map_server.Route;
import com.example.taskapp.map_server.Routing;
import com.example.taskapp.map_server.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback, RoutingListener {





    private boolean anyLocationProv;

    public static int flagforcamerafocus = 0;
/*    @InjectView(R.id.line)
    ImageView line;*/
    public static  final int EXTERNAL_READ=1;
    TextView txtDropoff;


    LinearLayout linearLayoutTextDetail;
    LinearLayout linearlayoutCar;

    List<Marker> listOfRemovableMarker;
    TextView txtPickup;

    private float currentZoom = 12.8F, IsZoome = 0, ZoomLavel = 12.8F, ISHide = 0;
    private LatLng center;
    boolean isBottom = true;

    int decision;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    LatLng currentLatlng;
   // UserDetails mUserDetails;
    double mLat,mLng;


    private Location prevLoc = new Location("");
    private Location newLoc = new Location("");
    private int mapType = 1;
    private Marker myMarkerPickUp;
    private Marker myMarkerDropOff;
    private Polyline polyline;
    String currentCity = "";
    String currentCountry = "";
    String dropoffCity = "";
    String dropoffCountry = "";

    public static Handler handler,mapHandler;
    public static Runnable myRunnable;

    private boolean handler_updats = true;

    private boolean skip = false;

    private GoogleMap map;
    public static LatLng currentLatLng = null;
    Marker marker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkPermision();
        //@InjectView(R.id.txtDropoff)
        txtDropoff=findViewById(R.id.txt_pickup);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        anyLocationProv |= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        anyLocationProv |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //if (NetworkUtil.isConnectingToInternet(this)) {
        if (anyLocationProv) {

        } else {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==EXTERNAL_READ){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                map.setMyLocationEnabled(true);
            }
        }

    }

    @Override
    public void onResume() {

        super.onResume();
        checkLocation(this);

        if (mapHandler == null && handler_updats) {

            mapHandler = new Handler();

            mapHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(MapsActivity.this);
                    }
                }
            }, 500);
        }

        //Toast.makeText(getContext(), "on Resume alled", Toast.LENGTH_SHORT).show();
        HyperlinkGoogleMap.getInstance().stopLocationUpdate();
        HyperlinkGoogleMap.getInstance().startLocationUpdate(getApplicationContext(),
                new HyperlinkGoogleMap.ProvideLocation() {
            @Override
            public void currentLocation(Location location) {
                currentLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                //stupMap(currentLatlng);
            }

            @Override
            public void onError(String str) {

            }
        });
    }


    @OnClick({R.id.txt_pickup,R.id.txtDropoff})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.txt_pickup:
                BaseApp.currentRide.setFillAddressDropoff(false);
                BaseApp.currentRide.setFillAddressPickup(true);
                getAddress(1);
                break;

            case R.id.txtDropoff:
                BaseApp.currentRide.setFillAddressDropoff(true);
                BaseApp.currentRide.setFillAddressPickup(false);
                getAddress(2);
                break;
        }

    }

    public boolean checkPermision() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION))) {

                //SnackBarAlert.createSnackBar(coordinatorLayout, getResources().getString(R.string.permissions_message), this, this);

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}, EXTERNAL_READ);

            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, EXTERNAL_READ);

            }
            return false;
        } else {
            return true;
        }
    }

    public void checkLocation(Context context) {
        boolean anyLocationProv = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        anyLocationProv |= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        anyLocationProv |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (anyLocationProv) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermision()) {


                }
            } else {
                Intent i=new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(i);
                //   openLogin();
            }
        }
        else
        {
            CustomDialogAgree.onDissmiss();
            CustomDialogAgree.createDialogWithExitGo(this, getResources().getString(R.string.app_name), getString(R.string.message_enable_location), new CustomDialogAgree.DialogAgree() {
                @Override
                public void onAgree() {
                    Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(viewIntent);
                }

                @Override
                public void onCancel() {
                    // finish();
                }
            });

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        if (map != null && getApplicationContext() != null) {

            MapsInitializer.initialize(getApplicationContext());
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setCompassEnabled(false);
            map.setMapType(mapType);

            map.getUiSettings().setScrollGesturesEnabled(true);
            map.setIndoorEnabled(true);


            if (currentLatlng == null) {
                if (BaseApp.currentRide.getCurrentLatitude() != null && BaseApp.currentRide.getCurrentLOngitude() != null) {
                    currentLatlng = new LatLng(Double.parseDouble(BaseApp.currentRide.getCurrentLatitude()), Double.parseDouble(BaseApp.currentRide.getCurrentLOngitude()));
                }
            }


            if (currentLatlng != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLatlng).zoom(ZoomLavel).build();

                map.moveCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
               /* if (getView() != null) {
                    linearLayoutProgressbar = (LinearLayout) getView().findViewById(R.id.linearLayoutProgressbar);
                    linearLayoutProgressbar.setVisibility(View.GONE);
                }*/
                center = currentLatlng;
                getAdressFromLatlong(center);
                if (BaseApp.currentRide.getDropOffAddress() != null) {
                    if (!BaseApp.currentRide.getDropOffAddress().equalsIgnoreCase("")) {

                        if (BaseApp.currentRide.getDropOffLatitude() != 0.0 && BaseApp.currentRide.getDropOffLongitude() != 0.0) {
                            setPickupPin(center);
                            setDropOffPin(new LatLng(BaseApp.currentRide.getDropOffLatitude(), BaseApp.currentRide.getDropOffLongitude()));

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(center);
                            builder.include(new LatLng(BaseApp.currentRide.getDropOffLatitude(), BaseApp.currentRide.getDropOffLongitude()));
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, (int) getResources().getDimension(R.dimen.dp_70));
                            map.moveCamera(cameraUpdate);

                            Routing routing = new Routing(Routing.TravelMode.DRIVING);
                            routing.registerListener(MapsActivity.this);
                            routing.execute(center, new LatLng(BaseApp.currentRide.getDropOffLatitude(), BaseApp.currentRide.getDropOffLongitude()));
                        }
                    }
                }
            }
        }
    }

    //get address from location

    public void getAdressFromLatlong(LatLng latLng) {
        // DebugLog.e("latlong is::::"+ latLng);

        try {
            if (getApplicationContext() != null && latLng != null) {
                txtPickup = (TextView) findViewById(R.id.txt_pickup);

                Geocoder geocoder;
                if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                    List<Address> addresses = new ArrayList<Address>();
                    geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

                    try {
                        if (geocoder != null) {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // DebugLog.e(addresses.toString());
                    if (!addresses.isEmpty()) {
                        String area = null;
                        Address location = addresses.get(0);

                        if (addresses.get(0).getAddressLine(0) != null) {
                            area = addresses.get(0).getAddressLine(0);
                            if (addresses.get(0).getAddressLine(1) != null) {
                                area = area + " " + addresses.get(0).getAddressLine(1);
                            }

                        }
                        if (location.getSubAdminArea() != null) {
                            currentCity = location.getLocality();
                            currentCountry = location.getCountryName();
                        } else {
                            currentCity = location.getLocality();
                            currentCountry = location.getCountryName();
                        }
                        Log.d("Address",area);
                        txtPickup.setText(area);
                        setPickupPin(latLng);
                        BaseApp.currentRide.setCurrentCity(currentCity);
                        BaseApp.currentRide.setCurrentCountry(currentCountry);
                        BaseApp.currentRide.setPickupAddress(area);
                        BaseApp.currentRide.setPickLatitude(latLng.latitude);
                        BaseApp.currentRide.setPickLongitude(latLng.longitude);

//
//                        }

                    } else {

                        txtPickup.setText("");

                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            try {
                String result = data.getStringExtra("result");


                String[] LocationData = result.split(",,,");

                if (decision == 1) {

                    if (LocationData[0] != null && LocationData[1] != null) {

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(LocationData[0]), Double.parseDouble(LocationData[1]))).zoom(ZoomLavel).build();

                        map.moveCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                        center = new LatLng(Double.parseDouble(LocationData[0]), Double.parseDouble(LocationData[1]));
                        clearRemovableMarker();
                        handler_updats = false;
                        setPickupPin(new LatLng(Double.parseDouble(LocationData[0]), Double.parseDouble(LocationData[1])));
                        //wsCallNearFreeDriver(center);
                        flagforcamerafocus = 1;


                        BaseApp.currentRide.setPickLatitude(Double.parseDouble(LocationData[0]));
                        BaseApp.currentRide.setPickLongitude(Double.parseDouble(LocationData[1]));
                        BaseApp.currentRide.setPickupAddress(LocationData[7]);
                        txtPickup.setText(LocationData[7]);
                        currentCity = LocationData[3];
                        currentCountry = LocationData[6];
                        BaseApp.currentRide.setCurrentCity(currentCity);
                        BaseApp.currentRide.setCurrentCountry(currentCountry);

                        if (!txtDropoff.getText().toString().equalsIgnoreCase("")) {

                            if (!dropoffCountry.equalsIgnoreCase(LocationData[6])) {
                                BaseApp.currentRide.setDropOffAddress("");

                                clearRemovableMarker();
                                removeDropOffPin();
                                if (polyline != null) {
                                    polyline.remove();
                                }
                                BaseApp.currentRide.setDropOffLatitude(0.0);
                                BaseApp.currentRide.setDropOffLongitude(0.0);
                                /*CustomDialogAgree.onDissmiss();

                                CustomDialogAgree.createDialog(getApplicationContext(), getResources().getString(R.string.app_name), getString(R.string.city_bound_message), new CustomDialogAgree.DialogAgree() {
                                    @Override
                                    public void onAgree() {
                                        CustomDialogAgree.onDissmiss();
                                    }

                                    @Override
                                    public void onCancel() {
                                    }
                                });*/
                                txtDropoff.setText("");
                            }
                        }

                        if (!txtDropoff.getText().toString().equalsIgnoreCase("")) {
                            if (BaseApp.currentRide.getDropOffLatitude() != 0.0 && BaseApp.currentRide.getDropOffLongitude() != 0.0) {
                                setPickupPin(center);
                                setDropOffPin(new LatLng(BaseApp.currentRide.getDropOffLatitude(), BaseApp.currentRide.getDropOffLongitude()));

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(center);
                                builder.include(new LatLng(BaseApp.currentRide.getDropOffLatitude(), BaseApp.currentRide.getDropOffLongitude()));
                                LatLngBounds bounds = builder.build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, (int) getResources().getDimension(R.dimen.dp_70));
                                map.moveCamera(cameraUpdate);

                                Routing routing = new Routing(Routing.TravelMode.DRIVING);
                                routing.registerListener(MapsActivity.this);
                                routing.execute(center, new LatLng(BaseApp.currentRide.getDropOffLatitude(), BaseApp.currentRide.getDropOffLongitude()));
                            }
                        }

                    }


                } else {


                    if (LocationData[6] != null && center != null) {

                        Log.e("DATATTA","address is a" + LocationData[7] + " city is" + LocationData[3]);

                        if (LocationData[6].equalsIgnoreCase(currentCountry)) {


                            dropoffCity = LocationData[3];
                            dropoffCountry = LocationData[6];
                            BaseApp.currentRide.setDropoffCity(dropoffCity);
                            BaseApp.currentRide.setDropoffCountry(dropoffCountry);
                            BaseApp.currentRide.setDropOffAddress(LocationData[7]);
                            txtDropoff.setText(LocationData[7]);
                            BaseApp.currentRide.setDropOffLatitude(Double.parseDouble(LocationData[0]));
                            BaseApp.currentRide.setDropOffLongitude(Double.parseDouble(LocationData[1]));

                            setPickupPin(center);
                            setDropOffPin(new LatLng(Double.parseDouble(LocationData[0]), Double.parseDouble(LocationData[1])));

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(center);
                            builder.include(new LatLng(Double.parseDouble(LocationData[0]), Double.parseDouble(LocationData[1])));
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, (int) getResources().getDimension(R.dimen.dp_70));
                            map.moveCamera(cameraUpdate);

                            Routing routing = new Routing(Routing.TravelMode.DRIVING);
                            routing.registerListener(MapsActivity.this);
                            routing.execute(center, new LatLng(Double.parseDouble(LocationData[0]), Double.parseDouble(LocationData[1])));
                        } else {
                            BaseApp.currentRide.setDropOffAddress("");
                            txtDropoff.setText("");
                            BaseApp.currentRide.setDropOffLatitude(0.0);
                            BaseApp.currentRide.setDropOffLongitude(0.0);

                            if (polyline != null) {
                                polyline.remove();
                            }
                            removeDropOffPin();
                        /*    CustomDialogAgree.onDissmiss();
                            CustomDialogAgree.createDialog(getApplicationContext(), getResources().getString(R.string.app_name), getString(R.string.city_bound_message), new CustomDialogAgree.DialogAgree() {
                                @Override
                                public void onAgree() {
                                    CustomDialogAgree.onDissmiss();


                                }

                                @Override
                                public void onCancel() {

                                }
                            });*/
                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
        if (map != null) {


            PolylineOptions polyoptions = new PolylineOptions();
            if (polyline != null) {
                polyline.remove();
            }
            polyoptions.color(Color.parseColor("#101010"));
            polyoptions.width(4);
            polyoptions.addAll(mPolyOptions.getPoints());
            polyline = map.addPolyline(polyoptions);


        }
    }

    private void setPickupPin(LatLng latLng) {
        if (map != null) {
            if (myMarkerPickUp != null) {
                myMarkerPickUp.remove();
//                mapHandler = null;
            }
            myMarkerPickUp = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.picup)));
        }
    }



    public void getAddress(int i) {
        decision = i;
    /*    Intent intent = new Intent(getApplicationContext(), LocationSearchActivity.class);
        startActivityForResult(intent, 111);
    */}
    private void setDropOffPin(LatLng latLng) {
        if (map != null) {
            if (myMarkerDropOff != null) {
                myMarkerDropOff.remove();
            }
            myMarkerDropOff = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff)));
        }
    }

    private void removeDropOffPin() {
        if (map != null) {
            if (myMarkerDropOff != null) {
                myMarkerDropOff.remove();
            }

        }
    }
    private void clearRemovableMarker() {
        if (listOfRemovableMarker != null) {
            for (int i = 0; i < listOfRemovableMarker.size(); i++) {
                listOfRemovableMarker.get(i).remove();
            }
        }
    }
    @Override
    public void onCameraIdle() {

        center = map.getCameraPosition().target;
        if (center != null) {
            getAdressFromLatlong(center);
            // wsCallNearFreeDriver(center);
            BaseApp.currentRide.setPickLatitude(center.latitude);
            BaseApp.currentRide.setPickLongitude(center.longitude);
        }
    }


}
