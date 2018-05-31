package com.androidunipi;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidunipi.model.JSONParser;
import com.google.firebase.iid.FirebaseInstanceId;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class LoginFragment extends Fragment {

	RelativeLayout rel_main;
	EditText et_mail, et_pass;
	Button btn_login, btn_register;
	TextView tv_or;
	String password="", email="";
	View view;
	Activity myActivity;
	CommonData common;
	public ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private static String url_check_user = "http://androidun.eu.pn/android/verify_user.php";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.login_fragment, container, false);

		myActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		common = CommonData.getInstance();
		common.current_fragment = "Login";

		rel_main 		= (RelativeLayout) rootView.findViewById(R.id.rel_main);
		et_mail  		= (EditText) rootView.findViewById(R.id.et_mail);
		et_mail.clearFocus();
		et_pass  		= (EditText) rootView.findViewById(R.id.et_pass);
		et_pass.clearFocus();
		btn_login 		= (Button) rootView.findViewById(R.id.btn_login);
		btn_register	= (Button) rootView.findViewById(R.id.btn_register);
		tv_or 			= (TextView) rootView.findViewById(R.id.tv_or);
		view 			=  rootView.findViewById(R.id.view);

		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RegisterFragment fragment = new RegisterFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).addToBackStack(null).commit();

			}
		});

		et_mail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				email = et_mail.getText().toString();
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

		et_pass.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				password = et_pass.getText().toString();
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

		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//regex for email
				if(email.equals("")){
					if(password.equals("")){
						Toast.makeText(myActivity, "Please fill all feilds",Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(myActivity, "Email field cannot be empty",Toast.LENGTH_SHORT).show();
					}
				}else if(password.equals("")){
					Toast.makeText(myActivity, "Password field cannot be empty",Toast.LENGTH_SHORT).show();
				} else{
					//execute async
					new Login().execute();
				}
			}
		});

		return rootView;
	}

	class Login extends AsyncTask<String, String, String> {
		public String success ,url = url_check_user;
		String json = "", response="", user_email="";

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

			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("longitude", common.longitude));
			params.add(new BasicNameValuePair("latitude", common.latitude));
			params.add(new BasicNameValuePair("token", FirebaseInstanceId.getInstance().getToken()));

			JSONObject jObj = jsonParser.makeHttpRequest(url_check_user, "POST", params, "OBJECT");

			try {
				response = jObj.getString("user_code");
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

				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myActivity.getApplicationContext());
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("Log", common.is_logged);
				editor.putString("Email", common.token_email);
				editor.putString("UserName", common.userName);
				editor.putString("UserImage", common.userImage);
				editor.commit();

				common.bottom_bar.setVisibility(View.VISIBLE);
				Map fragment = new Map();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
				ft.replace(R.id.frame_container, fragment).commit();

			}else if(response.equals("2")){
				Toast.makeText(myActivity, "Invalid Mail format.",Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(myActivity, "Invalid email or password. Please try again.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	//email check control
	public boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		myActivity = activity;
	}

}
