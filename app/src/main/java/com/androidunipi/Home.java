package com.androidunipi;


import com.androidunipi.data.NotificationObj;
import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.JSONParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends Activity {

	FrameLayout frame_container;
	LinearLayout bottom_bar;
	RelativeLayout rel_home, rel_map, rel_contacts;
	View view_home, view_contacts;
	TextView tv_icon_home, tv_map, tv_icon_contacts;
	FragmentManager fragmentManager;
	CommonData common;
	CustomDialog dialog = new CustomDialog();
	private Handler h;
	private int delay;
	JSONParser jsonParser = new JSONParser();
	private static String battery = "http://androidun.eu.pn/android/battery.php";
	private static String change_battery = "http://androidun.eu.pn/android/change_battery.php";
	private static String long_lat = "http://androidun.eu.pn/android/long_lat.php";
	private static String change_long_lat = "http://androidun.eu.pn/android/change_long_lat.php";
	private static String url_create_user = "http://androidun.eu.pn/android/create_user_on.php";
	private static String chargin_url = "http://androidun.eu.pn/android/charging.php";
	private static String no_chargin_url ="http://androidun.eu.pn/android/no_charging.php";
	private int mybattery=0;
	private int batteryDifference=0;
	private double longDifference=0.0;
	private double latDifference=0.0;
	private double mylong=0.0;
	private double mylat=0.0;
	private FirebaseRemoteConfig remoteConfig;

	@Override
	public void onResume() {
		super.onResume();
		if(common != null) {
			common.isChatVisible = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		common = CommonData.getInstance();
		this.registerReceiver(common.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
				.setDeveloperModeEnabled(BuildConfig.DEBUG)
				.build();

		remoteConfig = FirebaseRemoteConfig.getInstance();
		remoteConfig.setConfigSettings(remoteConfigSettings);
		remoteConfig.setDefaults(R.xml.remote_config_defaults);

		fetch();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		common.is_logged = sharedPreferences.getBoolean("Log", false);
		common.token_email = sharedPreferences.getString("Email", "");
		common.userImage = sharedPreferences.getString("UserImage", "");
		common.userName = sharedPreferences.getString("UserName", "");

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			for (String key : getIntent().getExtras().keySet()) {
				if(key.equals("ID")){
					common.myNotification = (NotificationObj) getIntent().getExtras().get(key);
					if(common.myNotification.isChat()) {
						final Intent mainIntent = new Intent(this, ChatActivity.class);
						mainIntent.putExtra("data", common.myNotification);
						startActivity(mainIntent);
					}
				}
			}
		}

		h = new Handler();
		delay = Integer.parseInt(remoteConfig.getString("delay"));

		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			public void run() {
				if(delay != 0 && common.is_logged) {
					new CheckBatteryOn().execute();
					new CheckLocationOn().execute();
				}
				handler.postDelayed(this, 50000);
			}
		};

		// Are we charging
//		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//		Intent batteryStatus = this.registerReceiver(null, ifilter);
//		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;


		runnable.run();

		//set up notitle 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//set up full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_home);


		fragmentManager = getFragmentManager();

		frame_container  	= (FrameLayout) findViewById(R.id.frame_container);
		bottom_bar       	= (LinearLayout) findViewById(R.id.bottom_bar);
		common.bottom_bar 	= bottom_bar;
		rel_home 		 	= (RelativeLayout) findViewById(R.id.rel_home);
		rel_map 		 	= (RelativeLayout) findViewById(R.id.rel_map);
		rel_contacts 		= (RelativeLayout) findViewById(R.id.rel_contacts);

		view_home 		 	= findViewById(R.id.view_home);
		view_contacts 	 	= findViewById(R.id.view_contacts);

		tv_icon_home 	 	= (TextView) findViewById(R.id.tv_icon_home);
		tv_map 	 		 	= (TextView) findViewById(R.id.tv_map);
		tv_icon_contacts  	= (TextView) findViewById(R.id.tv_icon_contacts);

		common.tv_contacts = tv_icon_contacts;
		common.tv_map = tv_map;
		common.tv_profile = tv_icon_home;


		if(common.is_logged){
			//load home fragment and make bottom bar visible

			bottom_bar.setVisibility(View.VISIBLE);
			tv_icon_home.setBackgroundResource(R.drawable.prof_inactive);
			tv_map.setBackgroundResource(R.drawable.map_act);
			tv_icon_contacts.setBackgroundResource(R.drawable.contacts_inactive);
			rel_map.setEnabled(false);
			Map fragment = new Map();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
			ft.replace(R.id.frame_container, fragment).commit();
		}
		else{
			//load login/register fragment and make bottom bar gone
			bottom_bar.setVisibility(View.GONE);

			fragmentManager.popBackStack();
			fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

			LoginFragment fragment = new LoginFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
			ft.replace(R.id.frame_container, fragment).commit();

		}

		rel_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				view_contacts.setVisibility(View.INVISIBLE);
				view_home.setVisibility(View.VISIBLE);
				rel_map.setEnabled(true);

				rel_contacts.setBackgroundColor(Color.parseColor("#323232"));
				rel_home.setBackgroundColor(Color.parseColor("#000000"));
				tv_icon_home.setBackgroundResource(R.drawable.prof_active);
				tv_map.setBackgroundResource(R.drawable.map_in);
				tv_icon_contacts.setBackgroundResource(R.drawable.contacts_inactive);

				Profile fragment = new Profile();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();


			}
		});

		rel_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				view_contacts.setVisibility(View.INVISIBLE);
				view_home.setVisibility(View.INVISIBLE);
				rel_map.setEnabled(false);

				rel_contacts.setBackgroundColor(Color.parseColor("#323232"));
				rel_home.setBackgroundColor(Color.parseColor("#323232"));
				tv_icon_home.setBackgroundResource(R.drawable.prof_inactive);
				tv_map.setBackgroundResource(R.drawable.map_act);
				tv_icon_contacts.setBackgroundResource(R.drawable.contacts_inactive);

				Map fragment = new Map();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();

			}
		});

		rel_contacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				view_home.setVisibility(View.INVISIBLE);
				rel_map.setEnabled(true);
				view_contacts.setVisibility(View.VISIBLE);

				rel_home.setBackgroundColor(Color.parseColor("#323232"));
				rel_contacts.setBackgroundColor(Color.parseColor("#000000"));
				tv_icon_home.setBackgroundResource(R.drawable.prof_inactive);
				tv_map.setBackgroundResource(R.drawable.map_in);
				tv_icon_contacts.setBackgroundResource(R.drawable.contacts_active);

				Contacts fragment = new Contacts();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();

			}
		});

	}

	private void fetch() {
		long cacheExpiration = 1000;

		remoteConfig.fetch(cacheExpiration)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {

						String delayStr = remoteConfig.getString("delay");
						delay = Integer.parseInt(delayStr);
						remoteConfig.activateFetched();
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
					}
				});

	}



	class CheckBatteryOn extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", common.token_email));
			params.add(new BasicNameValuePair("battery",String.valueOf(common.bat)));

			JSONObject json = jsonParser.makeHttpRequest(
					battery, "GET", params, "OBJECT");
			try {

				mybattery = json.getInt("battery");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			batteryDifference= Math.abs(mybattery-common.bat);
			if(batteryDifference>=5){
				JSONObject changejson = jsonParser.makeHttpRequest(
						change_battery, "GET", params, "OBJECT");
			}
			return null;
		}
	}
	class CheckLocationOn extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", common.token_email));
			params.add(new BasicNameValuePair("longitude",String.valueOf(common.longitude)));
			params.add(new BasicNameValuePair("latitude",String.valueOf(common.latitude)));

			JSONObject json = jsonParser.makeHttpRequest(
					long_lat, "GET", params, "OBJECT");
			try {

				mylong = json.getDouble("longitude");
				mylat = json.getDouble("latitude");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			longDifference= Math.abs(mylong-Double.valueOf(common.longitude));
			latDifference= Math.abs(mylat-Double.valueOf(common.latitude));
			if(longDifference!=0 || latDifference!=0){

				jsonParser.makeHttpRequest(change_long_lat, "GET", params, "OBJECT");
			}

			return null;
		}
	}


	public void appControl(){
		common.back++;

		if(common.back == 2){
			@SuppressWarnings("deprecation")
			AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			builder.setTitle(R.string.app_name).setMessage("Are you sure you what to exit the app?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Home.this.finish();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							common.back = 0;
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			dialog.ChangeDialogUI(alert);
		}
		else{
			if(common.current_fragment.equals("Register")){
				common.back = 0;
				super.onBackPressed();
			}
		}
	}

	@Override
	public void onBackPressed() {
		appControl();
	}
}

