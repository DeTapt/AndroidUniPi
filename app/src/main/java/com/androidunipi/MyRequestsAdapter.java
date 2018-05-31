package com.androidunipi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidunipi.data.ContactObj;
import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.JSONParser;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARISTEAA on 10/20/2016.
 */
public class MyRequestsAdapter extends BaseAdapter {

    private RequestsAdapter thisAdapter;
    public ProgressDialog pDialog;
    CustomDialog dialog = new CustomDialog();
    CommonData common;
    private ArrayList<ContactObj> list;
    private Activity myActivity;
    private onMyRequestClickedListener listener;
    private static String delete_contact="http://androidun.eu.pn/android/delete_contact.php";
    private static final String TAG_SUCCESS = "success";
    JSONParser jsonParser = new JSONParser();
    ArrayList<ContactObj> myList= new ArrayList<>();
    String req_email;
    int pos;
    Activity motherActivity;

    public interface onMyRequestClickedListener {
        void onMyRequestClicked(String email);
    }

    public MyRequestsAdapter(ArrayList<ContactObj> list, Activity myActivity, onMyRequestClickedListener listener) {
        this.list = list;
        this.myActivity = myActivity;
        common = CommonData.getInstance();
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = null;

        if(view == null) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater inflator = myActivity.getLayoutInflater();

            myView = inflator.inflate(R.layout.myrequest_list_item, null);

            holder.email = (TextView) myView.findViewById(R.id.email);
            holder.username = (TextView) myView.findViewById(R.id.username);
            holder.photo = (ImageView) myView.findViewById(R.id.photo);
            holder.decline=(Button)myView.findViewById(R.id.decline);
            holder.rel_main = (RelativeLayout) myView.findViewById(R.id.rel_main1);

            myView.setTag(holder);
            //exampel how setTag works
            //holder.name.setTag(list.get(i).getId());
        }
        else{
            myView = view;
        }

        final ViewHolder holder = (ViewHolder) myView.getTag();
        final ContactObj contact = list.get(i);

        holder.email.setText(contact.getEmail());
        holder.username.setText(contact.getUsername());
        Picasso.with(myActivity).load(contact.getImage()).into(holder.photo);

        holder.email.setTextColor(contact.getActive().equals("1") ? R.color.green : R.color.red);


        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMyRequestClicked(contact.getEmail());
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( myActivity, AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle(R.string.app_name).setMessage("Είστε σίγουρος οτι θέλετε να ακυρώσετε αυτό το αίτημα φιλίας;")
                        .setCancelable(false)
                        .setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                common.contact_email=contact.getEmail();
                                new DeleteContact().execute();
                            }
                        })
                        .setNegativeButton("Οχι", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                dialog.ChangeDialogUI(alert);

            }
        });


        return myView;
    }

    class DeleteContact extends AsyncTask<String, String, String> {
        int success=0;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog( myActivity, ProgressDialog.THEME_HOLO_LIGHT);
            pDialog.setMessage("Ακύρωση αιτήματος. Παρακαλώ περιμένετε...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * getting user details from url
         * */
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", common.token_email));
            params.add(new BasicNameValuePair("contacts_email", common.contact_email));

            JSONObject json = jsonParser.makeHttpRequest(
                    delete_contact, "POST", params, "OBJECT");

            // Check your log cat for JSON reponse
            Log.d("Delete Response: ", json.toString());

            try {
                // Checking for SUCCESS TAG
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

                //common.requests.remove(pos);
                Contacts fragment = new Contacts();
                FragmentTransaction ft = myActivity.getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                ft.replace(R.id.frame_container, fragment).commit();

            }
            else{
                Log.d("Error occured: ","nop");
            }
        }
    }



    class ViewHolder {
        ImageView photo;
        TextView name,email,username;
        RelativeLayout rel_main;
        Button decline;
    }
}


