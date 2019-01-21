/**
 * @file FragmentInformation.java
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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import pfe.thebikelock.R;
import pfe.thebikelock.communication.ToolsHttps;
import pfe.thebikelock.model.DataNFC;

public class FragmentInformation extends Fragment {

    public static final String TAG = FragmentInformation.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private Handler myHandler;
    private FancyAlertDialog.Builder popupConnectionError;
    private TextView tv_battery, tv_latitude1, tv_latitude2, tv_latitude3, tv_longitude1, tv_longitude2, tv_longitude3;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_informations, container, false);
        final Context context = getActivity();

        // Set TextView with fragment
        tv_battery = (TextView) view.findViewById(R.id.textView_pourc);
        tv_latitude1 = (TextView) view.findViewById(R.id.tv_latitude);
        tv_latitude2 = (TextView) view.findViewById(R.id.tv_latitude1);
        tv_latitude3 = (TextView) view.findViewById(R.id.tv_latitude2);
        tv_longitude1 = (TextView) view.findViewById(R.id.tv_longitude);
        tv_longitude2 = (TextView) view.findViewById(R.id.tv_longitude1);
        tv_longitude3 = (TextView) view.findViewById(R.id.tv_longitude2);

        // Refresh any 0,3s
        myHandler = new Handler();
        myHandler.postDelayed(myRunnable,300);

        // Delete certificat
        ToolsHttps.nuke();

        // Request Volley
        mRequestQueue = Volley.newRequestQueue(getContext());

        // Button
        Button button = (Button) view.findViewById(R.id.button_nfc_data);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DataNFC.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Refresh data with handler
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            parseJSON();

            // Connection WIFI
            ConnectivityManager com = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = com.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            setIsConnected(isConnected);

            myHandler.postDelayed(this,300);
            Log.e(TAG, "Mise a jour OK \n " + this.toString());
        }
    };

    // Remove callback to handler
    public void onPause() {
        super.onPause();
        if(myHandler != null)
            myHandler.removeCallbacks(myRunnable);
    }

    // Parse data from Sigfox
    private void parseJSON() {
        String url = "https://5bc99faa0499f53744d6974e:3812c36ad79b818c1658d7e55ded1b2a@backend.sigfox.com/api/devicetypes/5c3f05ece833d917af9eb207/messages";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("MainActivity", "JSON receive");

                            JSONArray jsonArray = response.getJSONArray("data");
                            Log.d("MainActivity","GLOBJ = " + jsonArray);

                            for (int i = 0; i < 1; i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String device = hit.getString("data");

                                String dataParseLat = device.substring(0, 2);
                                String dataParseLat2 = device.substring(2, 7);

                                String dataParseLng = device.substring(7, 9);
                                String dataParseLng2 = device.substring(9, 14);

                                Log.d("DEVICED", "DEVICED = " + device.length());

                                if (device.length() == 16) {
                                    String dataParseBattery = device.substring(14, 16);
                                    int resultDataBattery = hex2decimal(dataParseBattery);

                                    tv_battery.setText("Pourcentage : " + String.valueOf(resultDataBattery));
                                }

                                int resultDataLat = hex2decimal(dataParseLat);
                                int resultDataLat2 = hex2decimal(dataParseLat2);

                                int resultDataLng = hex2decimal(dataParseLng);
                                int resultDataLng2 = hex2decimal(dataParseLng2);

                                double resultDataLat3 = Double.parseDouble(resultDataLat + "." + resultDataLat2);
                                double resultDataLng3 = Double.parseDouble(resultDataLng + "." + resultDataLng2);

                                double resultDataLat4 = resultDataLat3 - 90;
                                double resultDataLng4 = resultDataLng3 - 90;

                                tv_latitude1.setText("Hex : " + String.valueOf(resultDataLat));
                                tv_latitude2.setText("Dec : " + String.valueOf(resultDataLat3));
                                tv_latitude3.setText("Dec - 90 : " + String.valueOf(resultDataLat4));

                                tv_longitude1.setText("Hex : " + String.valueOf(resultDataLng));
                                tv_longitude2.setText("Dec : " + String.valueOf(resultDataLng3));
                                tv_longitude3.setText("Dec - 90 : " + String.valueOf(resultDataLng4));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "ERROR CONNECT \n" + error.networkResponse.statusCode);
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

    // Convert hexa to decimal
    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val ;
    }

    // Verify wifi connection
    public void setIsConnected(final boolean isConnected) {
        if (!isConnected) {
            // If not in the not connected state
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