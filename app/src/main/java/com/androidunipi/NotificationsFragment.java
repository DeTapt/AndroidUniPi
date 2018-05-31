package com.androidunipi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.androidunipi.data.NotificationObj;
import com.androidunipi.model.CustomDialog;
import com.androidunipi.model.JSONParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ARISTEAA on 10/26/2016.
 */

public class NotificationsFragment extends Fragment implements NotificationAdapter.onNotificationClickedListener, NotificationAdapter.onLongNotiicationClickedListener {

        private CommonData common;
        private Activity myActivity;
        private ListView listView;
        private TextView no_my_requests;
        private ArrayList<NotificationObj> arrayList;
        private SharedPreferences sharedPrefs;
        private NotificationAdapter adapter;


@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notifications_fragment, container, false);
        common = CommonData.getInstance();
        common.current_fragment = "Notifications";

        listView = (ListView) rootView.findViewById(R.id.list_my_not);
        no_my_requests  = (TextView) rootView.findViewById(R.id.no_my_not);
        no_my_requests.setVisibility(View.GONE);


        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(myActivity);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("Notifications", null);
        if(json != null) {
            Type type = new TypeToken<ArrayList<NotificationObj>>() {}.getType();
            arrayList = gson.fromJson(json, type);


            if(arrayList.size() > 0) {
                adapter = new NotificationAdapter(arrayList, myActivity, NotificationsFragment.this, NotificationsFragment.this);
                listView.setAdapter(adapter);
            }
            else{
                showNoNotifications();
            }

        }
        else{
            showNoNotifications();
        }



        return rootView;

        }

    public void showNoNotifications() {
        no_my_requests.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }


    @Override
    public void onNotificationClicked(int position) {
        //prepei na perasoyme kapoia stoixeia toy xristh pou esteile to notification
        arrayList.get(position).setRead(true);
        updateList();
        if(arrayList.get(position).isChat()){
            final Intent mainIntent = new Intent(getActivity(), ChatActivity.class);
            mainIntent.putExtra("data", arrayList.get(position));
            startActivity(mainIntent);
        }
        else{
            if(arrayList.get(position).getChat_token() == null) {
                arrayList.get(position).setChat_token(String.valueOf(Math.random() * 1000));
            }
            updateList();
            presentDialog(position,false, arrayList.get(position));
        }
    }

    @Override
    public void onLongNotificationClicked(int position) {
        //read to notification
        arrayList.get(position).setRead(true);
        updateList();
        presentDialog(position, true, null);
    }

    private void presentDialog(final int position, final boolean isLong, final NotificationObj notificationObj) {

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
        builder.setTitle(R.string.app_name).setMessage(isLong ? ("Do you want to delete the notification?") : ("Do you want to chat with the user " + arrayList.get(position).getUserName() + " " + arrayList.get(position).getsurName() + "?"))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.notifyDataSetChanged();

                        if(isLong) {
                            arrayList.remove(position);
                            updateList();
                            if(arrayList.size() == 0) {
                                showNoNotifications();
                            }
                        }
                        else{
                            if(arrayList.get(position).getChat_token() == null) {
                                arrayList.get(position).setChat_token(String.valueOf(Math.random() * 1000));
                            }

                            updateList();
                            adapter.notifyDataSetChanged();
                            //chat init
                            final Intent mainIntent = new Intent(getActivity(), ChatActivity.class);
                            mainIntent.putExtra("data", notificationObj);
                            startActivity(mainIntent);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void updateList(){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gsonSave = new Gson();
        String jsonSave = gsonSave.toJson(arrayList);
        editor.putString("Notifications", jsonSave);
        editor.apply();
    }


    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        myActivity = activity;
    }


}
