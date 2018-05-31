package com.androidunipi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

import com.androidunipi.model.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARISTEAA on 12/1/2016.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    CommonData common;
    JSONParser jsonParser = new JSONParser();
    public void onReceive(Context context , Intent intent) {
        common = CommonData.getInstance();
        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            new ChangeChargingState().execute();
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            new NoCharging().execute();
        }
    }
    class ChangeChargingState extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.token_email));
            JSONObject json = jsonParser.makeHttpRequest(
                    "http://androidun.eu.pn/android/charging.php", "GET", params, "OBJECT");
            return null;
        }
    }
    class NoCharging extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.token_email));
            JSONObject json = jsonParser.makeHttpRequest(
                    "http://androidun.eu.pn/android/no_charging.php", "GET", params, "OBJECT");
            return null;
        }
    }

}

