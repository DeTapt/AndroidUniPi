package com.androidunipi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidunipi.model.JSONParser;

/**
 * Created by ARISTEAA on 10/17/2016.
 */
public class ChangeDistanceOfUsersFragment extends Fragment {

    CommonData common;
    Activity myActivity;
    public ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    Button btn_save;
    String success="", new_target;
    EditText new_d_insert;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.changedistanceofusers_fragment, container, false);
        common = CommonData.getInstance();
        common.current_fragment = "Settings_Distance";

        new_target=common.target_distance;
        new_d_insert    = (EditText) rootView.findViewById(R.id.new_d_insert);
        btn_save  = (Button) rootView.findViewById(R.id. btn_save);


        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(new_d_insert.equals("")){
                    Toast.makeText(myActivity, "Kilometer Distance cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    new ChangeDistance().execute();
                    common.target_distance=new_d_insert.getText().toString();
                }

            }
        });

        new_d_insert.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new_target = new_d_insert.getText().toString();

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

    class ChangeDistance extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog (myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            common.target_distance = new_target;
            Settings fragment = new Settings();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
            ft.replace(R.id.frame_container, fragment).commit();

        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return null;
        }
    }



    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        myActivity = activity;
    }

}
