package com.androidunipi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidunipi.model.JSONParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi") 
public class ChangeCredentialsFragment extends Fragment{

	CommonData common;
	Activity myActivity;
	public ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
    Button btn_save;
    String success="";
    EditText email, pass_c, pass_r, pass_n;
    String user_email="", user_pass_c="", user_pass_r="", user_pass_n="";
    private static String update_user_cred = "http://androidun.eu.pn/android/change_credentials.php";
    int user_code;
	//test commit
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.changecredentials_fragment, container, false);
		common = CommonData.getInstance();
		common.current_fragment = "Settings";
		
		 user_email=common.token_email;
		 email     = (EditText) rootView.findViewById(R.id.email);
	     pass_c    = (EditText) rootView.findViewById(R.id.pass_c);
	     pass_n    = (EditText) rootView.findViewById(R.id.pass_n);
	     pass_r    = (EditText) rootView.findViewById(R.id.pass_r);
		 btn_save  = (Button) rootView.findViewById(R.id. btn_save);
		 //test
		 email.setText(user_email);
		 btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 if(user_email.equals("")){
			    	   if(user_pass_c.equals("")||user_pass_n.equals("")||user_pass_r.equals("")){
			    		   Toast.makeText(myActivity, "Please fill all feilds",Toast.LENGTH_SHORT).show();
			    	   }else { 
			    		   Toast.makeText(myActivity, "Email field cannot be empty",Toast.LENGTH_SHORT).show();
			    	   }
			       }else if(user_pass_c.equals("")){
			    	   if(user_pass_n.equals("")||user_pass_r.equals("")){
			    		   Toast.makeText(myActivity, "Please fill all feilds",Toast.LENGTH_SHORT).show();
			    	   }else { 
			        	Toast.makeText(myActivity, "Current password field cannot be empty",Toast.LENGTH_SHORT).show();
			        	}
			        }else if(user_pass_n.equals("")){
			    	   if(user_pass_r.equals("")){
			    		   Toast.makeText(myActivity, "Please fill all feilds",Toast.LENGTH_SHORT).show();
			    	      }else { 
			        	Toast.makeText(myActivity, "New password field cannot be empty",Toast.LENGTH_SHORT).show();
			        	}
		           }else if(user_pass_r.equals("")){
			    	  
			        	Toast.makeText(myActivity, "Retype password field cannot be empty",Toast.LENGTH_SHORT).show();
			        	
			        }else {
			        	if(user_pass_n.equals(user_pass_r)){
			        		new ChangeCredentials().execute();
			        		}else
			    	  
			        	Toast.makeText(myActivity, "New password must much the retypen one",Toast.LENGTH_SHORT).show();
			        	
			        }}
	        
		
			});
		
	        email.addTextChangedListener(new TextWatcher() {
	     		
	     		@Override
	     		public void onTextChanged(CharSequence s, int start, int before, int count) {
	     			user_email = email.getText().toString();
	     			
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
	        pass_c.addTextChangedListener(new TextWatcher() {
	     		
	     		@Override
	     		public void onTextChanged(CharSequence s, int start, int before, int count) {
	     			user_pass_c = pass_c.getText().toString();
	     			
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
	        pass_n.addTextChangedListener(new TextWatcher() {
	     	
	     		@Override
	     		public void onTextChanged(CharSequence s, int start, int before, int count) {
	     			user_pass_n = pass_n.getText().toString();
	     			
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
	        pass_r.addTextChangedListener(new TextWatcher() {
	     		
	     		@Override
	     		public void onTextChanged(CharSequence s, int start, int before, int count) {
	     			user_pass_r = pass_r.getText().toString();
	     			
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
		return rootView;
	}
	
	class ChangeCredentials extends AsyncTask<String, String, String> {
	   	 public String url = update_user_cred;
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

    	params.add(new BasicNameValuePair("email", user_email));
    	params.add(new BasicNameValuePair("password_c", user_pass_c));
    	params.add(new BasicNameValuePair("password_n", user_pass_n));
    	params.add(new BasicNameValuePair("password_r", user_pass_r));

    	Log.e("SHOW cur", ""+ user_pass_c);
    	Log.e("SHOW n", ""+ user_pass_n);
    	Log.e("SHOW r", ""+ user_pass_r);
       jObj = jsonParser.makeHttpRequest(url, "POST", params, "OBJECT");
       
       Log.e("CHANGE", ""+ jObj.toString());
   

       try {
	          response = jObj.getString("user_code");
	          Log.e("SHOW RESPONSE", ""+ response);
      
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
			   
		        Log.e("SHOW SOMETHONG", ""+ common.token_email);
			   
		        Settings fragment = new Settings();
	            FragmentTransaction ft = getFragmentManager().beginTransaction();
	            ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
	            ft.replace(R.id.frame_container, fragment).commit();
		   }else if(response.equals("2")){
			   Toast.makeText(myActivity, "Current Password is not correct.",Toast.LENGTH_SHORT).show();
		   }else if(response.equals("3")){
			   Toast.makeText(myActivity, "New password dont match.",Toast.LENGTH_SHORT).show();
		   }else if(response.equals("4")){
			   Toast.makeText(myActivity, "error",Toast.LENGTH_SHORT).show();
		   }
		   else{
			   Toast.makeText(myActivity, "Required field(s) is missing.", Toast.LENGTH_SHORT).show();
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



