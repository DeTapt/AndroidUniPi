package com.androidunipi;

import java.util.List;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.androidunipi.model.JSONParser;
import com.google.firebase.iid.FirebaseInstanceId;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import java.io.InputStream;
import android.widget.Toast;


@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class RegisterFragment extends Fragment {

	private RadioGroup et_gender;
	private RadioButton radioSexButton;
	JSONParser jsonParser = new JSONParser();
	RelativeLayout rel_main;
	EditText et_name, et_surname, et_email, et_password, et_username,et_age;
	CheckBox charger,usb, powerbag, usb_otg;
	Button  btn_register;
	View view;
	Activity myActivity;
	CommonData common;
	String name="", surname="", username="", password="", email="", gender="", age="",user_email="",ch="", p="", u="", uo="";
	public ProgressDialog pDialog;


	private static String url_create_user = "http://androidun.eu.pn/android/create_user_on.php";



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.register_fragment, container, false);

		myActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		common = CommonData.getInstance();
		common.current_fragment = "Register";

		rel_main 		= (RelativeLayout) rootView.findViewById(R.id.rel_main);
		et_name  		= (EditText) rootView.findViewById(R.id.et_name);
		et_surname  	= (EditText) rootView.findViewById(R.id.et_surname);
		et_email  		= (EditText) rootView.findViewById(R.id.et_email);
		et_password  	= (EditText) rootView.findViewById(R.id.et_password);
		et_username  	= (EditText) rootView.findViewById(R.id.et_username);
		et_gender 		= (RadioGroup)rootView.findViewById(R.id.et_gender);
		et_age          = (EditText) rootView.findViewById(R.id.et_age);
		btn_register	= (Button) rootView.findViewById(R.id.btn_register);
		view 			= (View) rootView.findViewById(R.id.view);

		charger= (CheckBox) rootView.findViewById(R.id.charger);
		powerbag = (CheckBox) rootView.findViewById(R.id.powerbag);
		usb = (CheckBox) rootView.findViewById(R.id.usb);
		usb_otg = (CheckBox) rootView.findViewById(R.id.usb_otg);

		et_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				name = et_name.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		et_surname.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				surname = et_surname.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		et_email.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				email = et_email.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		et_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				password = et_password.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		et_username.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				username = et_username.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		et_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				password = et_password.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		et_gender.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int selectedId) {

				radioSexButton = (RadioButton)rootView.findViewById(selectedId);
				gender    = radioSexButton.getText().toString();
			}

		});
		et_age.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				age = et_age.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		btn_register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(charger.isChecked())
				{
					ch="1";
				}else{
					ch="0";
				}
				if(powerbag.isChecked())
				{
					p="1";
				}else{
					p="0";
				}
				if(usb.isChecked())
				{
					u="1";
				}else{
					u="0";
				}
				if(usb_otg.isChecked())
				{
					uo="1";
				}else{
					uo="0";
				}


				if(name.equals("")){
					if(surname.equals("")||email.equals("")||username.equals("")||password.equals("")||gender.equals("")||age.equals("")){
						Toast.makeText(myActivity, "All feilds are mandatory",Toast.LENGTH_SHORT).show();
					}else{
						Log.e("error", "no name inserted ");
						Toast.makeText(myActivity, "Name field cannot be empty",Toast.LENGTH_SHORT).show();
					}
				}else if(surname.equals("")){
					if(email.equals("")||username.equals("")||password.equals("")||gender.equals("")||age.equals("")){
						Toast.makeText(myActivity, "All feilds are mandatory",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(myActivity, "Surname field cannot be empty",Toast.LENGTH_SHORT).show();
					}
				}else if(email.equals("")){
					if(username.equals("")||password.equals("")||gender.equals("")||age.equals("")){
						Toast.makeText(myActivity, "All feilds are mandatory",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(myActivity, "Email field cannot be empty",Toast.LENGTH_SHORT).show();
					}
				}else if(username.equals("")){
					if(password.equals("")||gender.equals("")||age.equals("")){
						Toast.makeText(myActivity, "All feilds are mandatory",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(myActivity, "Username field cannot be empty",Toast.LENGTH_SHORT).show();
					}
				}else if(password.equals("")){
					if(gender.equals("")||age.equals("")){
						Toast.makeText(myActivity, "All feilds are mandatory",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(myActivity, "Password field cannot be empty",Toast.LENGTH_SHORT).show();
					}
				}else if(gender.equals("")){
					if(age.equals("")){
						Toast.makeText(myActivity, "All feilds are mandatory",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(myActivity, "Gender field cannot be empty",Toast.LENGTH_SHORT).show();
					}
				}else if(age.equals("")){
					Toast.makeText(myActivity, "Age field cannot be empty",Toast.LENGTH_SHORT).show();
				}else{
					new Register().execute();

				}
			}
		});
		return rootView;
	}
	class Register extends AsyncTask<String, String, String> {
		public String success ,url = url_create_user;
		JSONObject jObj = null;
		String json = "", response="";


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog (myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {


			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("surname", surname));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("gender", gender));
			params.add(new BasicNameValuePair("age", age));
			params.add(new BasicNameValuePair("longitude", common.longitude));
			params.add(new BasicNameValuePair("latitude", common.latitude));
			params.add(new BasicNameValuePair("phone_model",common.getDeviceName()));
			params.add(new BasicNameValuePair("battery",common.battery));
			params.add(new BasicNameValuePair("charger", ch));
			params.add(new BasicNameValuePair("powerbag", p));
			params.add(new BasicNameValuePair("usb", u));
			params.add(new BasicNameValuePair("usb_otg", u));
			params.add(new BasicNameValuePair("token", FirebaseInstanceId.getInstance().getToken()));

			JSONObject jObj = jsonParser.makeHttpRequest("http://androidun.eu.pn/android/create_user_on.php", "GET", params, "OBJECT");

			try {
				response = jObj.getString("code");
				user_email = jObj.getString("user_email");
				common.userName = jObj.getString("user_name")+ " "+ jObj.getString("user_surname");
				common.userImage = jObj.getString("user_img");
			}  catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}


		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

			if(response.equals("1")){
				common.is_logged = true;
				common.token_email = user_email;
				common.bottom_bar.setVisibility(View.VISIBLE);
				Toast.makeText(myActivity, "User is registered.", Toast.LENGTH_SHORT).show();

				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myActivity.getApplicationContext());
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("Log", common.is_logged);
				editor.putString("Email", common.token_email);
				editor.putString("UserName", common.userName);
				editor.putString("UserImage", common.userImage);
				editor.commit();

				Map fragment = new Map();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();
			}else if(response.equals("2")){
				Toast.makeText(myActivity, "Invalid email format.", Toast.LENGTH_SHORT).show();
			}else if(response.equals("3")){
				Toast.makeText(myActivity, "Error: there is already account with this email address.", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(myActivity, "An error occured. Please try again.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		myActivity = activity;
	}

}
