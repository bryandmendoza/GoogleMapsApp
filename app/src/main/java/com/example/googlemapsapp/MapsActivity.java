package com.example.googlemapsapp;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng quevedo = new LatLng(-1.0112573419177544, -79.46906008808244);
    private int mapType = 0;
    private int drawOption = 0; //0 = Nada, 1 = Posición, 2 = Marcador, 3 = Polyline, 4 = Polygon
    private Polyline polyline = null;
    private List<LatLng> latLngList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private Toast toast = null;
    private LinearLayout lytPolyline;
    private Button btnDrawPolyline;
    private FloatingActionMenu btnSettings;
    private FloatingActionButton btnMapType, btnMove, btnAnimate, btnPos,
            btnMarker, btnPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setupLayouts();
        setupButtons();
        hideButtons();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quevedo, 15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                switch (drawOption) {
                    case 1:
                        posAction(latLng);
                        break;
                    case 2:
                        markerAction(latLng);
                        break;
                    case 3:
                        polylineAction(latLng);
                        break;
                }
            }
        });
    }

    public void setupLayouts() {
        lytPolyline = findViewById(R.id.lytPolyline);
    }

    public void setupButtons() {
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    showButtons();
                } else {
                    hideButtons();
                }
            }
        });
        //
        btnMapType = findViewById(R.id.btnMapType);
        btnMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapTypeAction();
            }
        });
        //
        btnMove = findViewById(R.id.btnMove);
        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveAction();
            }
        });
        //
        btnAnimate = findViewById(R.id.btnAnimate);
        btnAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateAction();
            }
        });
        //
        btnPos = findViewById(R.id.btnPos);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawOption = 1;
            }
        });
        //
        btnMarker = findViewById(R.id.btnMarker);
        btnMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayouts();
                clearPolyline();
                mMap.clear();
                drawOption = 2;
            }
        });
        //
        btnPolyline = findViewById(R.id.btnPolyline);
        btnPolyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayouts();
                lytPolyline.setVisibility(View.VISIBLE);
                clearPolyline();
                mMap.clear();
                drawOption = 3;
            }
        });
        //

        //
        btnDrawPolyline = findViewById(R.id.btnDrawPolyline);
        btnDrawPolyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polyline != null) polyline.remove();
                polyline = mMap.addPolyline(new PolylineOptions().addAll(latLngList).clickable(true));
                polyline.setColor(Color.RED);
                polyline.setWidth(5);
            }
        });
        //
    }

    public void mapTypeAction() {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(MapsActivity.this, "", Toast.LENGTH_LONG);
        mapType = (mapType == 3) ? 0 : (mapType+1);
        switch (mapType) {
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                toast.setText("Normal"); toast.show();
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                toast.setText("Satélite"); toast.show();
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                toast.setText("Híbrido"); toast.show();
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                toast.setText("Terreno"); toast.show();
                break;
        }
    }

    public void moveAction() {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(quevedo)
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    public void animateAction() {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(quevedo)
                .zoom(18)
                .bearing(45)
                .tilt(70)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    public void posAction(LatLng point) {
        Projection proj = mMap.getProjection();
        Point coord = proj.toScreenLocation(point);
        if (toast != null) toast.cancel();
        toast = Toast.makeText(MapsActivity.this, "Posición Click\nLat: "+point.latitude+"\nLng: "+point.longitude+"\nX: "+coord.x+" - Y: "+coord.y, Toast.LENGTH_LONG);
        toast.show();
    }

    public void markerAction(LatLng point) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        mMap.addMarker(new MarkerOptions().position(point).title("Nuevo marcador"));
    }

    public void polylineAction(LatLng point) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(point));
        latLngList.add(point);
        markerList.add(marker);
    }

    public void clearPolyline() {
        if(polyline != null) polyline.remove();
        for (Marker marker : markerList) marker.remove();
        latLngList.clear();
        markerList.clear();
    }

    public void hideLayouts() {
        lytPolyline.setVisibility(View.GONE);
    }

    public void hideButtons() {
        btnMapType.setVisibility(View.GONE);
        btnMove.setVisibility(View.GONE);
        btnAnimate.setVisibility(View.GONE);
        btnPos.setVisibility(View.GONE);
        btnMarker.setVisibility(View.GONE);
        btnPolyline.setVisibility(View.GONE);
    }

    public void showButtons() {
        btnMapType.setVisibility(View.VISIBLE);
        btnMove.setVisibility(View.VISIBLE);
        btnAnimate.setVisibility(View.VISIBLE);
        btnPos.setVisibility(View.VISIBLE);
        btnMarker.setVisibility(View.VISIBLE);
        btnPolyline.setVisibility(View.VISIBLE);
    }
}