package com.androidunipi;
import java.util.ArrayList;

import com.androidunipi.data.ContactObj;
import com.androidunipi.data.NotificationObj;
import com.androidunipi.data.UsersObj;
import com.google.android.gms.maps.GoogleMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommonData {
	public String battery = "";
	public int bat=0;

	public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			bat=level;
			battery = String.valueOf(level)+"%";
		}
	};
	//appcontrol
	int back = 0;
	public String current_fragment = "";
	LinearLayout bottom_bar;
	String target_distance = "1";
	String longitude = "";
	String latitude = "";
	int MAP_TYPE = GoogleMap.MAP_TYPE_NORMAL;

	//vital vars
	public Activity myActivity;
	private static CommonData mInstance = null;

	//login
	boolean is_logged = false;
	public String token_email;
	public String contact_email;
	String userName;
	String userImage;

	ArrayList<UsersObj> requests_on_map = new ArrayList<UsersObj>();
	ArrayList<UsersObj> friends_on_map = new ArrayList<UsersObj>();
	ArrayList<UsersObj> unknown_on_map = new ArrayList<UsersObj>();
	ArrayList<UsersObj> my_requests_on_map = new ArrayList<UsersObj>();
	public ArrayList<ContactObj> contacts = new ArrayList<ContactObj>();
	ArrayList<ContactObj> requests;

	TextView tv_profile, tv_map, tv_contacts, tvBadge;

	boolean isChatVisible;

	NotificationObj myNotification;

	public CommonData() {
		requests = new ArrayList<>();
	}

	////////////////////////////////////COMMON METHODS///////////////////////////////////////////////////////////////////
	//fragments - activities
	public static synchronized CommonData getInstance(){

		if(null == mInstance){
			mInstance = new CommonData();
		}
		return mInstance;
	}
	@SuppressWarnings("deprecation")
	public Boolean NetworkExists(Activity ctx){
		myActivity = ctx;

		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();

		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}
}