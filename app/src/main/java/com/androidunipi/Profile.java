package com.androidunipi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.ImageLoader;
import com.androidunipi.model.AppController;
import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.JSONParser;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class Profile extends Fragment {

    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    Button btn_change_credentials, btn_logout, btn_map_pref,btn_klm_pref;
    private static String logout = "http://unipi.orgfree.com/android/logout.php";
    CustomDialog dialog = new CustomDialog();

    int success = 0;
    CommonData common;
    Activity myActivity;
    public ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    EditText id, name, surname, username, gender, age;
    TextView phone_model,rate_data, battery_per,charger_yes,charger_no, power_bag_yes,power_bag_no,wire_yes,wire_no,usb_otg_yes,usb_otg_no ;
    ImageView img;
    JSONArray products = null;
    String mytext="", new_user_image = "", user_name = "", user_surname = "",user_power_bag="", user_usb="",user_charger="", user_gender = "", user_age = "", user_username = "", user_image = "", user_usb_otg = "";
    Button btnSave, btn_charging;
    String image_str;
    Bitmap thumbnail;
    private static String profile = "http://androidun.eu.pn/android/profile.php";
    private static String update_user_info = "http://androidun.eu.pn/android/update_user_info.php";
    private static String update_profile_img = "http://androidun.eu.pn/android/update_profile_img.php";
    private static String current_rate="http://androidun.eu.pn/android/current_rate.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "users";
    private static final int REQUEST_CAMERA = 1;
    protected static final int GET_FROM_GALLERY = 0;
    ImageView settings,avatar;
    RatingBar ratingBar;
    int user_num_of_rates=0;
    Double user_rate=0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        common = CommonData.getInstance();
        common.current_fragment = "Profile";


        img                     = (ImageView) rootView.findViewById(R.id.photo);
        name                    = (EditText) rootView.findViewById(R.id.name);
        surname                 = (EditText) rootView.findViewById(R.id.surname);
        ratingBar               =(RatingBar) rootView.findViewById(R.id.rating);
        rate_data               = (TextView) rootView.findViewById(R.id.rate_data);
        username                = (EditText) rootView.findViewById(R.id.username);
        gender                  = (EditText) rootView.findViewById(R.id.gender);
        age                     = (EditText) rootView.findViewById(R.id.age);
        btnSave                 = (Button) rootView.findViewById(R.id.btn_save);
        phone_model             = (TextView) rootView.findViewById(R.id.phone_model);
        battery_per             = (TextView) rootView.findViewById(R.id.battery_per);
        charger_yes             = (TextView) rootView.findViewById(R.id.charger_yes);
        charger_no              = (TextView) rootView.findViewById(R.id.charger_no);
        power_bag_yes           = (TextView) rootView.findViewById(R.id.power_bag_yes);
        power_bag_no            = (TextView) rootView.findViewById(R.id.power_bag_no);
        wire_yes                = (TextView) rootView.findViewById(R.id.usb_yes);
        wire_no                 = (TextView) rootView.findViewById(R.id.usb_no);
        usb_otg_yes             = (TextView) rootView.findViewById(R.id.usb_otg_yes);
        usb_otg_no              = (TextView) rootView.findViewById(R.id.usb_otg_no);
        settings                = (ImageView) rootView.findViewById(R.id.settings);
        avatar                  = (ImageView) rootView.findViewById(R.id.avatar);
        btn_change_credentials  = (Button) rootView.findViewById(R.id.btn_change_credentials);
        btn_klm_pref            = (Button) rootView.findViewById(R.id.btn_klm_pref);
        btn_map_pref            = (Button) rootView.findViewById(R.id.btn_map_pref);
        btn_charging            =(Button) rootView.findViewById(R.id.btn_charging);
        btn_logout              = (Button) rootView.findViewById(R.id.btn_logout);


        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) rootView.findViewById(R.id.drawerPane);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.grey_line), PorterDuff.Mode.SRC_ATOP);

        // initializing navigation menu
        mDrawerToggle = new ActionBarDrawerToggle(myActivity, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Log.d( "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        btn_change_credentials.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ChangeCredentialsFragment fragment = new ChangeCredentialsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                ft.replace(R.id.frame_container, fragment).commit();


            }
        });
        btn_charging.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ChangeChargingOptionsFragment fragment = new ChangeChargingOptionsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                ft.replace(R.id.frame_container, fragment).commit();


            }
        });
        btn_klm_pref.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ChangeDistanceOfUsersFragment fragment = new ChangeDistanceOfUsersFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                ft.replace(R.id.frame_container, fragment).commit();


            }
        });


        btn_map_pref.setOnClickListener(new View.OnClickListener() {

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

        btn_logout.setOnClickListener(new View.OnClickListener() {

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


        settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);

            }
        });


        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                selectImage();

            }
        });
        new LoadProfile().execute();

        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_name = name.getText().toString();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        surname.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_surname = surname.getText().toString();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_username = username.getText().toString();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        gender.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_gender = gender.getText().toString();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        age.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_age = age.getText().toString();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });


        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveUsersDetails().execute();
            }
        });

        return rootView;
    }

    private void invalidateOptionsMenu(){}

    class LoadProfile extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
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
         */
        protected String doInBackground(String... args) {
            // Building Parameters

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.token_email));

            JSONObject json = jsonParser.makeHttpRequest(
                    profile, "GET", params, "OBJECT");

            JSONObject jsonrate = jsonParser.makeHttpRequest(
                    current_rate, "GET", params, "OBJECT");
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    products = json.getJSONArray(TAG_USER);
                    JSONObject c = products.getJSONObject(0);

                    user_name = c.getString("name");
                    user_surname = c.getString("surname");
                    user_username = c.getString("username");
                    user_gender = c.getString("gender");
                    user_age = c.getString("age");
                    user_charger = c.getString("charger");
                    user_usb = c.getString("usb");
                    user_usb_otg = c.getString("usb_otg");
                    user_power_bag = c.getString("powerbag");
                    user_image = c.getString("img");

                    user_rate =jsonrate.getDouble("rate");
                    user_num_of_rates=jsonrate.getInt("num_of_rates");

                    mytext= user_rate+"/5 ("+user_num_of_rates+" Votes )";

                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            name.setText(user_name);
            surname.setText(user_surname);
            username.setText(user_username);
            gender.setText(user_gender);
            age.setText(user_age);
            Picasso.with(myActivity).load(user_image).into(img);
            Picasso.with(myActivity).load(user_image).into(avatar);
            battery_per.setText(common.battery);
            phone_model.setText(common.getDeviceName());

            float f = user_rate.floatValue();
            ratingBar.setRating(f);
            rate_data.setText(mytext);

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

    /**
     * Background Async Task to  Save users Details
     */
    class SaveUsersDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Saving user's details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving user's details
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", user_name));
            params.add(new BasicNameValuePair("surname", user_surname));
            params.add(new BasicNameValuePair("username", user_username));
            params.add(new BasicNameValuePair("gender", user_gender));
            params.add(new BasicNameValuePair("age", user_age));
            params.add(new BasicNameValuePair("email", common.token_email));

            JSONObject json = jsonParser.makeHttpRequest(update_user_info,
                    "POST", params, "OBJECT");

            try {
                int success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product updated
            pDialog.dismiss();
            Toast.makeText(myActivity, "Profile updated succesfully.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(myActivity,AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Change Profile Photo");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);


                } else if (items[item].equals("Choose from Library")) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

                thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;

                byte[] byte_arr = bytes.toByteArray();

                image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                //img.setImageBitmap(thumbnail);
                Picasso.with(myActivity).load(user_image).into(img);
                new SaveUsersImage().execute();


                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == GET_FROM_GALLERY) {

                Uri selectedImageUri = data.getData();
                String[] projection = {MediaColumns.DATA};
                Cursor cursor = myActivity.managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                // img.setImageBitmap(bm);
                Picasso.with(myActivity).load(user_image).into(img);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                image_str = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                new SaveUsersImage().execute();

            }
        }
    }

    /**
     * Background Async Task to  Update users image
     */
    class SaveUsersImage extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Saving user's image...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving user's details
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("img", image_str));
            params.add(new BasicNameValuePair("email", common.token_email));
            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(update_profile_img,
                    "POST" +
                            "", params, "OBJECT");
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    products = json.getJSONArray(TAG_USER);
                    JSONObject c = products.getJSONObject(0);
                    new_user_image = c.getString("img");

                } else {
                    // failed to update product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product updated
            pDialog.dismiss();
            Picasso.with(myActivity).load(new_user_image).into(img);
            Toast.makeText(myActivity, "Profile updated succesfully.", Toast.LENGTH_SHORT).show();
        }
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
            params.add(new BasicNameValuePair("email", common.token_email));

            JSONObject json = jsonParser.makeHttpRequest(
                    logout, "POST", params, "OBJECT");

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

            //token regeneration
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }

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