package com.androidunipi;

import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.GpsTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	CommonData common;
	CustomDialog dialog = new CustomDialog();
	public double latitude, longitude;
    GpsTracker gps;
    AlertDialog alert;
    Location mylocation;
	public int checkGPS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //set up notitle 
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_main);
		gps = new GpsTracker(MainActivity.this);
		common = CommonData.getInstance();

		//check if we can get location
	    if(gps.canGetLocation()){
	    	  mylocation= 	gps.getLocation();

	    	  latitude = gps.getLatitude();
	    	  longitude = gps.getLongitude();
	    	  
	    	  common.latitude = String.valueOf(latitude);
	    	  common.longitude = String.valueOf(longitude);
	    	  
	    	  checkGPS = 1;
	    	  
	    	  //network check
				if(common.NetworkExists(MainActivity.this)){
					
					
		            new Handler().postDelayed(new Runnable() {
		                @Override
		                public void run() {
		                    final Intent mainIntent = new Intent(MainActivity.this, Home.class);
		                    startActivity(mainIntent);
		                    overridePendingTransition(R.animator.maximize, R.animator.minimize);
		                    finish();
		                }
		            }, 3000);
				}
				else{
					@SuppressWarnings("deprecation")
			 		   AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			 		   builder.setTitle(R.string.app_name).setMessage("No network.")
			            .setCancelable(false)
			            .setPositiveButton("The app will close.", new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {
			                	 dialog.cancel();
			                     MainActivity.this.finish();
			                }
			            })

			           .setNegativeButton("Connect me.", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			            	   startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 1);
			               }
			           });
			 	        alert = builder.create();
			 	        alert.show();
			 	        dialog.ChangeDialogUI(alert);
				}
	      }
	      else{
	    	  checkGPS = 2;
	    	  gps.showSettingsAlert();
	      }
	}



    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		//return from wifi settings
	    if (requestCode == 1) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
	    }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();		
	}
}
