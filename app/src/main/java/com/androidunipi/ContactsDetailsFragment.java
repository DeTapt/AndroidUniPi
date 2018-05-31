package com.androidunipi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.ImageLoader;
import com.androidunipi.data.ContactObj;
import com.androidunipi.model.AppController;
import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.JSONParser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.GONE;
import static java.lang.Float.parseFloat;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class ContactsDetailsFragment extends Fragment {
	
	CommonData common;
	Activity myActivity;
	CustomDialog dialog = new CustomDialog();
	public ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
    TextView agef,gender_f,id,rate_data, name, surname, username, gender, age, online, offline, phone_model, battery_per ,charger_yes,charger_no, power_bag_yes,power_bag_no,wire_yes,wire_no, usb_otg_yes,usb_otg_no ;
    ImageView img;
    JSONArray user = null ,ratej = null, tokens=null,my_user=null;
    String  user_usb_otg = "",user_battery="0",charging="",user_phone="",my_user_name="",my_user_surname="",my_user_image="",user_name="", mytext="", user_surname="",  user_gender="",  user_age="",  user_username="", user_image="",active="",user_power_bag="", user_usb="",user_charger="" ;
    Button btnChargingBattery,btnDeleteContact,btnRequestBattery;
    Double user_rate=0.0;
    private static String profile = "http://androidun.eu.pn/android/profile.php",name_surname="";
    private static String delete_contact="http://androidun.eu.pn/android/delete_contact.php";
    private static String current_rate="http://androidun.eu.pn/android/current_rate.php";
    private static String send_notification="http://unipi.netne.net/android/sendnotification4.php";
    private static String rate="http://androidun.eu.pn/android/rate.php";
    private static String get_token="http://androidun.eu.pn/android/get_token.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_Token = "user_code";
    private static final String TAG_USER = "users";
    RatingBar ratingBar;
    int user_num_of_rates=0;
    private static Float new_user_rating;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.contactsdetails_fragment, container, false);
		common = CommonData.getInstance();
		common.current_fragment = "ContactsDetails";
		
		img               = (ImageView) rootView.findViewById(R.id.photo);
        ratingBar         =(RatingBar) rootView.findViewById(R.id.rating);
        name              = (TextView) rootView.findViewById(R.id.name_surname);
        rate_data         = (TextView) rootView.findViewById(R.id.rate_data);
        gender            = (TextView) rootView.findViewById(R.id.title_gender);
        gender_f          = (TextView) rootView.findViewById(R.id.title_gender_f);
        age               = (TextView) rootView.findViewById(R.id.age);
        agef               = (TextView) rootView.findViewById(R.id.agef);
        phone_model       = (TextView) rootView.findViewById(R.id.phone_model);
        battery_per       = (TextView) rootView.findViewById(R.id.battery);
        online            = (TextView) rootView.findViewById(R.id.online);
        offline           = (TextView) rootView.findViewById(R.id.offline);
        charger_yes       = (TextView) rootView.findViewById(R.id.charger_yes);
        charger_no        = (TextView) rootView.findViewById(R.id.charger_no);
        power_bag_yes     = (TextView) rootView.findViewById(R.id.power_bag_yes);
        power_bag_no      = (TextView) rootView.findViewById(R.id.power_bag_no);
        wire_yes          = (TextView) rootView.findViewById(R.id.usb_yes);
        wire_no           = (TextView) rootView.findViewById(R.id.usb_no);
        usb_otg_yes        = (TextView) rootView.findViewById(R.id.usb_otg_yes);
        usb_otg_no         = (TextView) rootView.findViewById(R.id.usb_otg_no);
        usb_otg_yes        = (TextView) rootView.findViewById(R.id.usb_otg_yes);
        usb_otg_no         = (TextView) rootView.findViewById(R.id.usb_otg_no);
        btnDeleteContact  = (Button) rootView.findViewById(R.id.btn_delete_contact);
        btnRequestBattery = (Button) rootView.findViewById(R.id.btn_request_battery);
        btnChargingBattery = (Button) rootView.findViewById(R.id.btn_charging_battery);




      LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
      stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.grey_line), PorterDuff.Mode.SRC_ATOP);


        new LoadProfile().execute();
       
        btnDeleteContact.setOnClickListener(new View.OnClickListener() {
       	 
            @Override
            public void onClick(View arg0) {
             
             
  			   AlertDialog.Builder builder = new AlertDialog.Builder(myActivity, AlertDialog.THEME_HOLO_LIGHT);
  			   builder.setTitle(R.string.app_name).setMessage("Are you sure you want to delete this contact?")
  	           .setCancelable(false)
  	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
  	               public void onClick(DialogInterface dialog, int id) {
  	            	 new DeleteContact().execute();
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

        btnRequestBattery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new SendNotification().execute();

            }
        });


        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = ratingBar.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    float stars = (int)starsf + 1;
                    ratingBar.setRating(stars);
                    new_user_rating=stars;
                    new RateUser().execute();
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }




                return true;
            }});

		return rootView;
	}

	class LoadProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading user's Profile. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.contact_email));
            params.add(new BasicNameValuePair("memail", common.token_email));

            JSONObject json = jsonParser.makeHttpRequest(
                    profile, "GET", params, "OBJECT");

            JSONObject jsonrate = jsonParser.makeHttpRequest(
                    current_rate, "GET", params, "OBJECT");

            Log.e("THIS profile", "" + json.toString());
            Log.e("THIS rate", "" + jsonrate.toString());
            Log.e("With EMAIL", "" + common.contact_email.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    user = json.getJSONArray(TAG_USER);
 
                    // looping through All Products
             
                        JSONObject c = user.getJSONObject(0);
 
                        // Storing each json item in variable
                        user_name = c.getString("name");
                        user_surname = c.getString("surname");
                        user_gender = c.getString("gender");
                        user_age = c.getString("age");
                        user_image = c.getString("img");
                        user_charger = c.getString("charger");
                        user_usb = c.getString("usb");
                        user_usb_otg = c.getString("usb_otg");
                        user_power_bag = c.getString("powerbag");
                        user_battery=c.getString("battery");
                        user_phone=c.getString("phone");
                        active = c.getString("active");
                        charging=c.getString("charging");

                    user_battery=user_battery+"%";

                    user_rate =jsonrate.getDouble("rate");
                    user_num_of_rates=jsonrate.getInt("num_of_rates");

                    mytext= user_rate+"/5 ("+user_num_of_rates+" Votes )";
                    name_surname= user_name+" "+user_surname;
            
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
            pDialog.dismiss();

            name.setText(name_surname);

            battery_per.setText(user_battery);
            phone_model.setText(user_phone);
            Picasso.with(myActivity).load(user_image).into(img);
            float f = user_rate.floatValue();
            ratingBar.setRating(f);
            rate_data.setText(mytext);

            if(user_gender.equals("Male")){

                gender_f.setVisibility(GONE);
                age.setText(user_age+" Years Old");
            }else{
                gender.setVisibility(GONE);
                agef.setText(user_age+" Years Old");
            }


            if(active.equals("1"))
            {
            	online.setVisibility(TextView.VISIBLE);
            	offline.setVisibility(TextView.INVISIBLE);
            }else{
            	online.setVisibility(TextView.INVISIBLE);
            	offline.setVisibility(TextView.VISIBLE);
            }
            if(user_charger.equals("1"))
            {
                charger_yes.setVisibility(TextView.VISIBLE);
                charger_no.setVisibility(TextView.INVISIBLE);
            }else{
                charger_yes.setVisibility(TextView.INVISIBLE);
                charger_no.setVisibility(TextView.VISIBLE);
            }
            if(user_usb.equals("1"))
            {
                wire_yes.setVisibility(TextView.VISIBLE);
                wire_no.setVisibility(TextView.INVISIBLE);
            }else{
                wire_yes.setVisibility(TextView.INVISIBLE);
                wire_no.setVisibility(TextView.VISIBLE);
            }
            if(user_usb_otg.equals("1"))
            {
                usb_otg_yes.setVisibility(TextView.VISIBLE);
                usb_otg_no.setVisibility(TextView.INVISIBLE);
            }else{
                usb_otg_yes.setVisibility(TextView.INVISIBLE);
                usb_otg_no.setVisibility(TextView.VISIBLE);
            }
            if(user_power_bag.equals("1"))
            {
                power_bag_yes.setVisibility(TextView.VISIBLE);
                power_bag_no.setVisibility(TextView.INVISIBLE);
            }else{
                power_bag_yes.setVisibility(TextView.INVISIBLE);
                power_bag_no.setVisibility(TextView.VISIBLE);
            }
            if(charging.equals("1"))
            {
                btnChargingBattery.setVisibility(Button.VISIBLE);
                btnRequestBattery.setVisibility(Button.INVISIBLE);
            }else{
                btnChargingBattery.setVisibility(Button.INVISIBLE);
                btnRequestBattery.setVisibility(Button.VISIBLE);
            }
        }
  }
    class SendNotification extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Sending notification. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params_profile = new ArrayList<NameValuePair>();
            params_profile.add(new BasicNameValuePair("email", common.token_email));

            JSONObject json_profile = jsonParser.makeHttpRequest(profile, "GET", params_profile, "OBJECT");

                int success = 0;

                try {
                    success = json_profile.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (success == 1) {
                    JSONObject c = null;
                    try {
                        my_user = json_profile.getJSONArray(TAG_USER);
                        c = my_user.getJSONObject(0);

                        my_user_name = c.getString("name");
                        my_user_surname = c.getString("surname");
                        my_user_image = c.getString("img");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.contact_email));

            JSONObject token_json = jsonParser.makeHttpRequest(get_token, "GET", params, "OBJECT");

            try {
                int token_success = token_json.getInt(TAG_Token);
                if (token_success == 1) {
                    tokens = token_json.getJSONArray("token");
                    List<NameValuePair> params_token = new ArrayList<NameValuePair>();
                    params_token.add(new BasicNameValuePair("name", my_user_name));
                    params_token.add(new BasicNameValuePair("surname", my_user_surname));
                    params_token.add(new BasicNameValuePair("img", my_user_image));
                    params_token.add(new BasicNameValuePair("email", common.token_email));
                    params_token.add(new BasicNameValuePair("message", "You have a new battery request from "));
                    params_token.add(new BasicNameValuePair("body", "You have a new battery request from"));

                    for (int i = 0; i < tokens.length(); i++) {
                        JSONObject c = tokens.getJSONObject(i);
                        params_token.add(new BasicNameValuePair("token", c.getString("token")));
                    }

                    JSONObject json = jsonParser.makeHttpRequest(
                            send_notification, "GET", params_token, "OBJECT");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }

    }
   class RateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Βαθμολόγηση του χρήστη. Παρακαλώ περιμένετε...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public String doInBackground(String... args) {
            // Building Parameters
            RateUser value= new RateUser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.contact_email));
            params.add(new BasicNameValuePair("memail", common.token_email));
            params.add(new BasicNameValuePair("rating",ContactsDetailsFragment.new_user_rating.toString()));

            JSONObject jsonrate = jsonParser.makeHttpRequest(
                    rate, "GET", params, "OBJECT");

            Log.e("THIS rate", "" + jsonrate.toString());
            Log.e("With EMAIL", "" + common.contact_email.toString());

            try {

                    user_rate =jsonrate.getDouble("rate");
                    user_num_of_rates=jsonrate.getInt("num_of_rates");

                    mytext= user_rate+"/5 ("+user_num_of_rates+" Ψήφοι )";


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            float f = user_rate.floatValue();
            ratingBar.setRating(f);
            rate_data.setText(mytext);
        }
    }
	class DeleteContact extends AsyncTask<String, String, String> {
		int success=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Deleting Contact. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.token_email));
            params.add(new BasicNameValuePair("contacts_email", common.contact_email));
           
            JSONObject json = jsonParser.makeHttpRequest(
            		delete_contact, "POST", params, "OBJECT");

            Log.d("Delete Response: ", json.toString());
 
            try {
                success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                   Log.d("User Deleted: ", "ok");
                   
                } else {
                	Log.d("Error occured: ","nop");
                 
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
            pDialog.dismiss();
            if (success == 1) {

           Contacts fragment = new Contacts();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
            ft.replace(R.id.frame_container, fragment).commit();
            
            }
            else{
            	Log.d("Error occured: ","nop");
            }
       }
  }
	
	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		myActivity = activity;
	}

}
