package com.androidunipi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.JSONParser;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by ARISTEAA on 10/20/2016.
 */
public class UnknownUserFragment extends Fragment {

    CommonData common;
    Activity myActivity;
    CustomDialog dialog = new CustomDialog();
    public ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    TextView agef, gender_f,id, rate_data, username, gender, age, online, offline, phone_model, battery_per ,charger_yes,charger_no,usb_otg_yes,usb_otg_no, power_bag_yes,power_bag_no,wire_yes,wire_no ;
    ImageView img;
    JSONArray products = null;
    String   user_usb_otg = "",user_battery="",user_phone="",user_name="", user_surname="", mytext="",  user_gender="",  user_age="",  user_username="", user_image="",active="",user_power_bag="", user_usb="",user_charger="" ;
    Button btnAddUser,btnRequestBattery;
    private static String profile = "http://androidun.eu.pn/android/profile.php";
    private static String send_request="http://androidun.eu.pn/android/send_request.php";
    private static String current_rate="http://androidun.eu.pn/android/current_rate.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "users";
    RatingBar ratingBar;
    int user_num_of_rates=0;
    Double user_rate=0.0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.unknownuser_fragment, container, false);
        common = CommonData.getInstance();
        common.current_fragment = "ContactsDetails";

        img               = (ImageView) rootView.findViewById(R.id.photo);
        username          = (TextView) rootView.findViewById(R.id.username);
        ratingBar         =(RatingBar) rootView.findViewById(R.id.rating);
        rate_data         = (TextView) rootView.findViewById(R.id.rate_data);
        gender            = (TextView) rootView.findViewById(R.id.title_gender);
        gender_f          =(TextView) rootView.findViewById(R.id.title_gender_f);
        age               = (TextView) rootView.findViewById(R.id.age);
        agef              = (TextView) rootView.findViewById(R.id.agef);
        phone_model       = (TextView) rootView.findViewById(R.id.phone_model);
        battery_per       = (TextView) rootView.findViewById(R.id.battery_per);
        online            = (TextView) rootView.findViewById(R.id.online);
        offline           = (TextView) rootView.findViewById(R.id.offline);
        charger_yes        = (TextView) rootView.findViewById(R.id.charger_yes);
        charger_no         = (TextView) rootView.findViewById(R.id.charger_no);
        power_bag_yes      = (TextView) rootView.findViewById(R.id.power_bag_yes);
        power_bag_no       = (TextView) rootView.findViewById(R.id.power_bag_no);
        wire_yes           = (TextView) rootView.findViewById(R.id.usb_yes);
        wire_no            = (TextView) rootView.findViewById(R.id.usb_no);
        usb_otg_yes        = (TextView) rootView.findViewById(R.id.usb_otg_yes);
        usb_otg_no         = (TextView) rootView.findViewById(R.id.usb_otg_no);
        btnAddUser = (Button) rootView.findViewById(R.id.btn_add_user);


        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.grey_line), PorterDuff.Mode.SRC_ATOP);

        new LoadProfile().execute();

        btnAddUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                AlertDialog.Builder builder = new AlertDialog.Builder(myActivity, AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle(R.string.app_name).setMessage("Do you wish to add this user to your contacts?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AddContact().execute();
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
    class LoadProfile extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading user's Profile. Please wait...");
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
            params.add(new BasicNameValuePair("memail", common.token_email));

            JSONObject json = jsonParser.makeHttpRequest(
                    profile, "GET", params, "OBJECT");
            JSONObject jsonrate = jsonParser.makeHttpRequest(
                    current_rate, "GET", params, "OBJECT");

            Log.e("THIS profile", "" + json.toString());
            Log.e("With EMAIL", "" + common.contact_email.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_USER);

                    // looping through All Products

                    JSONObject c = products.getJSONObject(0);

                    // Storing each json item in variable
                    user_name = c.getString("name");
                    user_surname = c.getString("surname");
                    user_username = c.getString("username");
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

                    user_battery=user_battery+"%";


                    user_rate =jsonrate.getDouble("rate");
                    user_num_of_rates=jsonrate.getInt("num_of_rates");

                    mytext= user_rate+"/5 ("+user_num_of_rates+" Votes )";


                } else {
                    // no products found
                    // Launch Add New product Activity

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

            username.setText(user_username);
            Picasso.with(myActivity).load(user_image).into(img);
            float f = user_rate.floatValue();
            ratingBar.setRating(f);
            rate_data.setText(mytext);
            battery_per.setText(user_battery);
            phone_model.setText(user_phone);

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
        }
    }
    class AddContact extends AsyncTask<String, String, String> {
        int success=0;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Friend request is being sent. Please wait...");
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
            params.add(new BasicNameValuePair("email", common.token_email));
            params.add(new BasicNameValuePair("contacts_email", common.contact_email));

            JSONObject json = jsonParser.makeHttpRequest(
                    send_request, "POST", params, "OBJECT");

            // Check your log cat for JSON reponse
            Log.d("Request Response: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("User added: ", "ok");

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
        // TODO Auto-generated method stub
        super.onAttach(activity);

        myActivity = activity;
    }

}

