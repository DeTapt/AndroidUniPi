package com.androidunipi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.androidunipi.data.NotificationObj;
import com.androidunipi.data.UsersObj;
import com.androidunipi.model.JSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static com.androidunipi.R.id.rel_map;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
public class Map extends Fragment {

	CommonData common;
	Activity mActivity;
	MapFragment googleMap;
	public double latitude, longitude;
	Button btn_find;
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	GoogleMap myGoogleMap, myGoogleMap1;
	TextView tvBadge;
	ImageView notifications;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_fragment, container, false);

		tvBadge = (TextView) rootView.findViewById(R.id.tv_badge);
		notifications = (ImageView) rootView.findViewById(R.id.btn_notification);
		btn_find = (Button) rootView.findViewById(R.id.btn_find);
		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		common = CommonData.getInstance();
		common.current_fragment = "Map";

		common.tvBadge = tvBadge;


		common.tv_profile.setBackgroundResource(R.drawable.prof_inactive);
		common.tv_map.setBackgroundResource(R.drawable.map_act);
		common.tv_contacts.setBackgroundResource(R.drawable.contacts_inactive);


		//check the notification list for unread notifications
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		Gson gson = new Gson();
		String json = sharedPrefs.getString("Notifications", null);
		if(json != null) {
			Type type = new TypeToken<ArrayList<NotificationObj>>() {}.getType();
			ArrayList<NotificationObj> arrayList = gson.fromJson(json, type);
			int counter = 0;
			for (int i = 0 ; i < arrayList.size() ; i++) {
				if(!arrayList.get(i).isRead()) {
					counter++;
				}
			}

			common.tvBadge.setText(counter != 0 ? ""+counter : "");

		}

		if (!common.latitude.equals("") && !common.longitude.equals("")) {
			latitude = Double.parseDouble(common.latitude);
			longitude = Double.parseDouble(common.longitude);
		}

		if (googleMap == null) {

			googleMap = getMapFragment();

			googleMap.getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap googleMap) {

					googleMap.setMapType(common.MAP_TYPE);

					CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(17).build();
					googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

					if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						return;
					}
					else{
						googleMap.setMyLocationEnabled(true);
					}
					myGoogleMap = googleMap;
					myGoogleMap1 = googleMap;
				}
			});



			if (googleMap == null) {
				Toast.makeText(mActivity.getApplicationContext(),"Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(mActivity.getApplicationContext(),"Press the button to load users nearby..", Toast.LENGTH_SHORT).show();
			}
		}

		btn_find.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new LoadUsersOnMap().execute();
			}
		});

		notifications.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NotificationsFragment fragment = new NotificationsFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();

			}
		});
		return rootView;
	}


	class LoadUsersOnMap extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Loading Users... ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", common.token_email));
			params.add(new BasicNameValuePair("target", common.target_distance));
			JSONObject json = jsonParser.makeHttpRequest( "http://androidun.eu.pn/android/return_users.php", "POST", params, "OBJECT");

			JSONArray Jrequests;
			JSONArray Jfriends;
			JSONArray Jmy_requests;
			JSONArray Junknown;
			try {
				Jrequests = json.getJSONArray("request");
				if(Jrequests.length()>0){
					for(int i = 0 ; i < Jrequests.length() ; i++){
						UsersObj currRequest = new UsersObj();
						JSONObject currJsonUser = Jrequests.getJSONObject(i);

						currRequest.setAge(currJsonUser.getString("age"));
						currRequest.setEmail( currJsonUser.getString("email"));
						currRequest.setGender( currJsonUser.getString("gender"));
						currRequest.setId( currJsonUser.getString("id"));
						currRequest.setLatitude( currJsonUser.getString("latitude"));
						currRequest.setLongitude( currJsonUser.getString("longitude"));
						currRequest.setName( currJsonUser.getString("name"));
						currRequest.setSurname( currJsonUser.getString("surname"));

						common.requests_on_map.add(currRequest);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Jfriends = json.getJSONArray("friends");
				if(Jfriends.length()>0){
					for(int i = 0 ; i < Jfriends.length() ; i++){
						UsersObj currFriends = new UsersObj();
						JSONObject currJsonUser = Jfriends.getJSONObject(i);

						currFriends.setAge(currJsonUser.getString("age"));
						currFriends.setEmail( currJsonUser.getString("email"));
						currFriends.setGender( currJsonUser.getString("gender"));
						currFriends.setId( currJsonUser.getString("id"));
						currFriends.setLatitude( currJsonUser.getString("latitude"));
						currFriends.setLongitude( currJsonUser.getString("longitude"));
						currFriends.setName( currJsonUser.getString("name"));
						currFriends.setSurname( currJsonUser.getString("surname"));

						common.friends_on_map.add(currFriends);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Jmy_requests = json.getJSONArray("my_request");
				if(Jmy_requests.length()>0){
					for(int i = 0 ; i < Jmy_requests.length() ; i++){
						UsersObj currMyRequests = new UsersObj();
						JSONObject currJsonUser = Jmy_requests.getJSONObject(i);

						currMyRequests.setAge(currJsonUser.getString("age"));
						currMyRequests.setEmail( currJsonUser.getString("email"));
						currMyRequests.setGender( currJsonUser.getString("gender"));
						currMyRequests.setId( currJsonUser.getString("id"));
						currMyRequests.setLatitude( currJsonUser.getString("latitude"));
						currMyRequests.setLongitude( currJsonUser.getString("longitude"));
						currMyRequests.setName( currJsonUser.getString("name"));
						currMyRequests.setSurname( currJsonUser.getString("surname"));

						common.my_requests_on_map.add(currMyRequests);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Junknown = json.getJSONArray("unknown");
				if(Junknown.length()>0){
					for(int i = 0 ; i < Junknown.length() ; i++){
						UsersObj currUnknown = new UsersObj();
						JSONObject currJsonUser = Junknown.getJSONObject(i);

						currUnknown.setAge(currJsonUser.getString("age"));
						currUnknown.setEmail( currJsonUser.getString("email"));
						currUnknown.setGender( currJsonUser.getString("gender"));
						currUnknown.setId( currJsonUser.getString("id"));
						currUnknown.setLatitude( currJsonUser.getString("latitude"));
						currUnknown.setLongitude( currJsonUser.getString("longitude"));
						currUnknown.setName( currJsonUser.getString("name"));
						currUnknown.setSurname( currJsonUser.getString("surname"));

						common.unknown_on_map.add(currUnknown);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}


		protected void onPostExecute(String file_url) {

			for(int i = 0 ; i < common.requests_on_map.size() ; i++){
				Double lat = Double.valueOf(common.requests_on_map.get(i).getLatitude());
				Double lon = Double.valueOf(common.requests_on_map.get(i).getLongitude());

				String full_name = common.requests_on_map.get(i).getName()+" "+common.requests_on_map.get(i).getSurname();

				MarkerOptions markerrequest = new MarkerOptions().position(new LatLng(lat, lon)).title(full_name);
				markerrequest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
				myGoogleMap.addMarker(markerrequest).showInfoWindow();

			}


			for(int i = 0 ; i < common.my_requests_on_map.size() ; i++){
				Double lat = Double.valueOf(common.my_requests_on_map.get(i).getLatitude());
				Double lon = Double.valueOf(common.my_requests_on_map.get(i).getLongitude());

				String full_name = common.my_requests_on_map.get(i).getName()+" "+common.my_requests_on_map.get(i).getSurname();

				MarkerOptions markermy = new MarkerOptions().position(new LatLng(lat, lon)).title(full_name);
				markermy.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
				myGoogleMap.addMarker(markermy).showInfoWindow();
			}
			for( int i = 0 ; i < common.friends_on_map.size() ; i++){
				Double lat = Double.valueOf(common.friends_on_map.get(i).getLatitude());
				Double lon = Double.valueOf(common.friends_on_map.get(i).getLongitude());

				String full_name = common.friends_on_map.get(i).getName()+" "+common.friends_on_map.get(i).getSurname();

				MarkerOptions markerfriend = new MarkerOptions().position(new LatLng(lat, lon)).title(full_name);
				markerfriend.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				myGoogleMap1.addMarker(markerfriend).showInfoWindow();

			}
			for(int i = 0 ; i < common.unknown_on_map.size() ; i++){
				Double lat = Double.valueOf(common.unknown_on_map.get(i).getLatitude());
				Double lon = Double.valueOf(common.unknown_on_map.get(i).getLongitude());

				String full_name = common.unknown_on_map.get(i).getName()+" "+common.unknown_on_map.get(i).getSurname();

				MarkerOptions markerun = new MarkerOptions().position(new LatLng(lat, lon)).title(full_name);
				markerun.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				myGoogleMap.addMarker(markerun).showInfoWindow();
			}
			myGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker arg0) {

					String titleToCheck = arg0.getTitle();
					return false;
				}
			});
			myGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

				@Override
				public void onInfoWindowClick(Marker arg0) {
					String titleToCheck = arg0.getTitle();
					for(int i = 0 ; i< common.unknown_on_map.size() ; i++){

						String name_to_find = common.unknown_on_map.get(i).getName()+" "+common.unknown_on_map.get(i).getSurname();
						if(titleToCheck.equals(name_to_find)){

							if(titleToCheck.equals(name_to_find)){

								String email = common.unknown_on_map.get(i).getEmail();
								common.contact_email = email;
								UnknownUserFragment fragment = new UnknownUserFragment();
								FragmentTransaction ft = getFragmentManager().beginTransaction();
								ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
								ft.replace(R.id.frame_container, fragment).commit();

							}

						}

					}
					for(int i = 0 ; i< common.friends_on_map.size() ; i++){

						String name_to_find = common.friends_on_map.get(i).getName()+" "+common.friends_on_map.get(i).getSurname();
						if(titleToCheck.equals(name_to_find)){

							if(titleToCheck.equals(name_to_find)){

								String email = common.friends_on_map.get(i).getEmail();
								common.contact_email = email;
								ContactsDetailsFragment fragment = new  ContactsDetailsFragment();
								FragmentTransaction ft = getFragmentManager().beginTransaction();
								ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
								ft.replace(R.id.frame_container, fragment).commit();

							}

						}

					}
					for(int i = 0 ; i< common.requests_on_map.size() ; i++){

						String name_to_find = common.requests_on_map.get(i).getName()+" "+common.requests_on_map.get(i).getSurname();
						if(titleToCheck.equals(name_to_find)){

							if(titleToCheck.equals(name_to_find)){

								String email = common.requests_on_map.get(i).getEmail();
								common.contact_email = email;
								ContactRequestFragment fragment = new  ContactRequestFragment();
								FragmentTransaction ft = getFragmentManager().beginTransaction();
								ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
								ft.replace(R.id.frame_container, fragment).commit();

							}

						}

					}
					for(int i = 0 ; i< common.my_requests_on_map.size() ; i++){

						String name_to_find = common.my_requests_on_map.get(i).getName()+" "+common.my_requests_on_map.get(i).getSurname();
						if(titleToCheck.equals(name_to_find)){

							if(titleToCheck.equals(name_to_find)){

								String email = common.my_requests_on_map.get(i).getEmail();
								common.contact_email = email;
								MyRequestFragment fragment = new MyRequestFragment();
								FragmentTransaction ft = getFragmentManager().beginTransaction();
								ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
								ft.replace(R.id.frame_container, fragment).commit();

							}

						}

					}


				}

			});

			pDialog.dismiss();


		}
	}



	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private MapFragment getMapFragment() {
		FragmentManager fm = null;



		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			fm = getFragmentManager();
		} else {
			fm = getChildFragmentManager();
		}

		return (MapFragment) fm.findFragmentById(R.id.map);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MapFragment f = (MapFragment) mActivity.getFragmentManager().findFragmentById(R.id.map);
		if (f != null)
			getFragmentManager().beginTransaction().remove(f).commit();
	}


}
