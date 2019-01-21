/**
 * @file FragmentMainBis.java
 * @version 0.2
 * @author Pierre Pavlovic
 * @date 21/01/2018
 *
 * @section License
 *
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * Copyright (C) 2018  Mehdi Bouafia & Pierre Pavlovic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package pfe.thebikelock.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pfe.thebikelock.R;
import pfe.thebikelock.communication.ToolsHttps;

public class FragmentMainBis extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean isMapReady = false;
    private GoogleMap googleMap;
    private Button btnUnlock;
    private ImageButton btnRefresh;
    private RequestQueue mRequestQueue;
    private Handler myHandler;
    private FancyAlertDialog.Builder popupConnectionError;
    private ImageView img_battery100, img_battery75, img_battery50, img_battery25, img_battery0;

    public double latitude;
    public double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main_bis, container, false);

        // Set ImageView with fragment
        img_battery100 = (ImageView) view.findViewById(R.id.imageViewBattery100);
        img_battery75 = (ImageView) view.findViewById(R.id.imageViewBattery75);
        img_battery50 = (ImageView) view.findViewById(R.id.imageViewBattery50);
        img_battery25 = (ImageView) view.findViewById(R.id.imageViewBattery25);
        img_battery0 = (ImageView) view.findViewById(R.id.imageViewBattery0);

        /** Json parse data */

        // Request Volley
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Map view
        final MapView mMapView = view.findViewById(R.id.mapMain);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        // Refresh any 0,3s
        myHandler = new Handler();
        myHandler.postDelayed(myRunnable, 300);

        // Delete certificat
        ToolsHttps.nuke();

        // Progress dialog
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Location in progress...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        /** Google Map identification */
        // Refresh map for position
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        isMapReady = true;

                        googleMap = mMap;
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        googleMap.clear();

                        //LatLng angers = new LatLng(47.4698, -0.5593);
                        LatLng angers = new LatLng(latitude, longitude);

                        //Hide dialog position
                        if (latitude != 0) {
                            dialog.hide();
                        }

                        googleMap.addMarker(new MarkerOptions().position(angers).title("Your bike is here"));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(angers));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(angers, 17));
                        // Zoom in, animating the camera.
                        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                    }
                });

                btnRefresh = view.findViewById(R.id.button_refresh);
                btnRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Snackbar.make(view, "Refresh", Snackbar.LENGTH_LONG).setAction("Refresh", null).show();

                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap mMap) {
                                isMapReady = true;

                                googleMap = mMap;
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                googleMap.clear();

                                //LatLng angers = new LatLng(47.4698, -0.5593);
                                LatLng angers = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions().position(angers).title("Your bike is here"));
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(angers));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(angers, 17));
                                // Zoom in, animating the camera.
                                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                            }
                        });

                    }
                });

                btnUnlock = view.findViewById(R.id.button_unlock_main);
                btnUnlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Go to the unlock fragment
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.animator.from_bottom, R.animator.to_top)
                                .replace(R.id.actual_fragment, ((MainActivity) getActivity()).getFragmentUnlock(), "FRAGMENT_UNLOCK")
                                .commit();
                    }
                });
            }
        }, 6000);
        Log.e(TAG, "Refresh Run 1 OK \n " + this.toString());
        return view;
    }

    // Parse Json data
    private void parseJSON() {
        String url = "https://5bc99faa0499f53744d6974e:3812c36ad79b818c1658d7e55ded1b2a@backend.sigfox.com/api/devicetypes/5c3f05ece833d917af9eb207/messages";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("MainActivity", "JSON receive");

                            JSONArray jsonArray = response.getJSONArray("data");
                            Log.d("MainActivity", "GLOBJ = " + jsonArray);

                            for (int i = 0; i < 1; i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String device = hit.getString("data");

                                String dataParseLat = device.substring(0, 2);
                                String dataParseLat2 = device.substring(2, 7);

                                String dataParseLng = device.substring(7, 9);
                                String dataParseLng2 = device.substring(9, 14);

                                if (device.length() == 16) {
                                    String dataParseBattery = device.substring(14, 16);
                                    int resultDataBattery = hex2decimal(dataParseBattery);

                                    if (resultDataBattery == 100){
                                        img_battery100.setVisibility(View.VISIBLE);
                                        img_battery75.setVisibility(View.INVISIBLE);
                                        img_battery50.setVisibility(View.INVISIBLE);
                                        img_battery25.setVisibility(View.INVISIBLE);
                                        img_battery0.setVisibility(View.INVISIBLE);
                                    }else if(resultDataBattery == 75) {
                                        img_battery100.setVisibility(View.INVISIBLE);
                                        img_battery75.setVisibility(View.VISIBLE);
                                        img_battery50.setVisibility(View.INVISIBLE);
                                        img_battery25.setVisibility(View.INVISIBLE);
                                        img_battery0.setVisibility(View.INVISIBLE);
                                    }else if (resultDataBattery == 50){
                                        img_battery100.setVisibility(View.INVISIBLE);
                                        img_battery75.setVisibility(View.INVISIBLE);
                                        img_battery50.setVisibility(View.VISIBLE);
                                        img_battery25.setVisibility(View.INVISIBLE);
                                        img_battery0.setVisibility(View.INVISIBLE);
                                    }else if (resultDataBattery == 25){
                                        img_battery100.setVisibility(View.INVISIBLE);
                                        img_battery75.setVisibility(View.INVISIBLE);
                                        img_battery50.setVisibility(View.INVISIBLE);
                                        img_battery25.setVisibility(View.VISIBLE);
                                        img_battery0.setVisibility(View.INVISIBLE);
                                    }else if (resultDataBattery == 0){
                                        img_battery100.setVisibility(View.INVISIBLE);
                                        img_battery75.setVisibility(View.INVISIBLE);
                                        img_battery50.setVisibility(View.INVISIBLE);
                                        img_battery25.setVisibility(View.INVISIBLE);
                                        img_battery0.setVisibility(View.VISIBLE);
                                    }
                                }

                                int resultDataLat = hex2decimal(dataParseLat);
                                int resultDataLat2 = hex2decimal(dataParseLat2);

                                int resultDataLng = hex2decimal(dataParseLng);
                                int resultDataLng2 = hex2decimal(dataParseLng2);

                                double resultDataLat3 = Double.parseDouble(resultDataLat + "." + resultDataLat2);
                                double resultDataLng3 = Double.parseDouble(resultDataLng + "." + resultDataLng2);

                                latitude = resultDataLat3 - 90;
                                longitude = resultDataLng3 - 90;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Error Response \n" + error.networkResponse.statusCode);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "5bc99faa0499f53744d6974e:3812c36ad79b818c1658d7e55ded1b2a";
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mRequestQueue.add(request);
    }

    // Needed to get the map to display immediately
    @Override
    public void onResume() {
        super.onResume();
    }

    // SetMap visible when no connection
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isMapReady = false;
    }

    // Refresh data
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            parseJSON();

            // Connection WIFI
            ConnectivityManager com = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = com.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            setIsConnected(isConnected);

            Log.d("WIFI", String.valueOf(isConnected));

            myHandler.postDelayed(this, 300);
            Log.e(TAG, "Refresh OK \n " + this.toString());
        }
    };

    // Stop refresh data
    public void onPause() {
        super.onPause();
        if (myHandler != null)
            myHandler.removeCallbacks(myRunnable);
    }

    // Convert hex to decimal
    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }

    // Verify wifi connection
    public void setIsConnected(final boolean isConnected) {
        if (!isConnected) {
            // if not in the not connected state
            if (popupConnectionError == null) {
                popupConnectionError = new FancyAlertDialog.Builder(getActivity());
                popupConnectionError
                        .setTitle("Error Connection")
                        .setBackgroundColor(Color.parseColor("#bc0000"))
                        .setMessage("You don't seem to have an active internet connection, please check your connection and try again.")
                        .setNegativeBtnText("Settings")
                        .setNegativeBtnBackground(Color.parseColor("#B3B3B3"))
                        .setPositiveBtnText("Close")
                        .setPositiveBtnBackground(Color.parseColor("#bc0000"))
                        .setAnimation(Animation.POP)
                        .isCancellable(false)
                        .setIcon(R.drawable.ic_false_white_24px, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                            }
                        })
                        .OnNegativeClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                            }
                        })
                        .build();
            }
        } else {
            popupConnectionError = null;
        }
    }
}
