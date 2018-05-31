package com.androidunipi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.JSONParser;
import com.google.android.gms.maps.GoogleMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class Settings  extends Fragment {

	CommonData common;
	Activity myActivity;
	CustomDialog dialog = new CustomDialog();
	public ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	Button btn_change_credentials, btn_logout, btn_map_pref,btn_klm_pref;
	private static String logout = "http://androidun.eu.pn/android/logout.php";
	private static final String TAG_SUCCESS = "success";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.settings_fragment, container, false);
		common = CommonData.getInstance();
		common.current_fragment = "Settings";

		btn_change_credentials  = (Button) rootView.findViewById(R.id.btn_change_credentials);
		btn_klm_pref            = (Button) rootView.findViewById(R.id.btn_klm_pref);
		btn_map_pref            = (Button) rootView.findViewById(R.id.btn_map_pref);
		btn_logout              = (Button) rootView.findViewById(R.id.btn_logout);

		btn_change_credentials.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ChangeCredentialsFragment fragment = new ChangeCredentialsFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();


			}
		});
		btn_klm_pref.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ChangeDistanceOfUsersFragment fragment = new ChangeDistanceOfUsersFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();


			}
		});


		btn_map_pref.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CharSequence colors[] = new CharSequence[] {"Normal", "Hybrid", "Satellite", "Terrain"};

				AlertDialog.Builder builder = new AlertDialog.Builder(myActivity, AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle("Choose type of map");
				builder.setItems(colors, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0){
							common.MAP_TYPE = GoogleMap.MAP_TYPE_NORMAL;
						}
						else if(which == 1){
							common.MAP_TYPE = GoogleMap.MAP_TYPE_HYBRID;
						}
						else if(which == 2){
							common.MAP_TYPE = GoogleMap.MAP_TYPE_SATELLITE;
						}
						else{
							common.MAP_TYPE = GoogleMap.MAP_TYPE_TERRAIN;
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

				dialog.ChangeDialogUI(alert);

			}
		});

		btn_logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(myActivity, AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(R.string.app_name).setMessage("Are you sure you want to logout?")
						.setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new Logout().execute();
							}
						})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();

				dialog.ChangeDialogUI(alert);

			}

		});
		return rootView;
	}
	class Logout extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Logging out. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting user details from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", common.contact_email));

			JSONObject json = jsonParser.makeHttpRequest(
					logout, "POST", params, "OBJECT");

			// Check your log cat for JSON reponse
			Log.d("Logout Response: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					common.is_logged = false;

				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			//clear sharedPrefs

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myActivity.getApplicationContext());
			sharedPreferences.edit().clear().commit();

			common.is_logged = false;
			common.token_email = "";

			pDialog.dismiss();
			common.bottom_bar.setVisibility(View.GONE);
			LoginFragment fragment = new LoginFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
			ft.replace(R.id.frame_container, fragment).commit();

		}}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		myActivity = activity;
	}
}