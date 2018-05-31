package com.androidunipi;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidunipi.data.ContactObj;
import com.androidunipi.data.NotificationObj;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.androidunipi.R.id.contact;

/**
 * Created by ARISTEAA on 10/26/2016.
 */

public class NotificationAdapter extends BaseAdapter {

    private NotificationAdapter thisAdapter;
    private ArrayList<NotificationObj> list;
    private Activity myActivity;
    private onNotificationClickedListener listener;
    private onLongNotiicationClickedListener longListener;

    interface onNotificationClickedListener {
        void onNotificationClicked(int position);
    }

    interface onLongNotiicationClickedListener {
        void onLongNotificationClicked(int position);
    }

    NotificationAdapter (ArrayList<NotificationObj> list, Activity myActivity, onNotificationClickedListener listener, onLongNotiicationClickedListener longListener) {
        this.list = list;
        this.myActivity = myActivity;
        this.listener = listener;
        this.longListener = longListener;
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

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View myView;

        if(view == null) {
            NotificationAdapter.ViewHolder holder = new NotificationAdapter.ViewHolder();
            LayoutInflater inflator = myActivity.getLayoutInflater();

            myView = inflator.inflate(R.layout.notification_list_item, null);

            holder.message = (TextView) myView.findViewById(R.id.message);
            holder.date = (TextView) myView.findViewById(R.id.date);
            holder.time = (TextView) myView.findViewById(R.id.time);
            holder.photo = (ImageView) myView.findViewById(R.id.photo);
            holder.rel_main = (LinearLayout) myView.findViewById(R.id.rel_main1);

            myView.setTag(holder);

        }
        else{
            myView = view;
        }

        NotificationAdapter.ViewHolder holder = (NotificationAdapter.ViewHolder) myView.getTag();
        final NotificationObj notification = list.get(i);

        holder.message.setTypeface(notification.isRead() ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);

        String my_message= notification.getMessage()+" "+notification.getUserName()+" "+notification.getsurName();
        holder.message.setText(my_message);
        holder.date.setText(notification.getDate());
        holder.time.setText(notification.getTime());
        if(notification.getUserPhoto()==null) {
            Picasso.with(myActivity).load(R.drawable.user).into(holder.photo);
        }else {
            Picasso.with(myActivity).load(notification.getUserPhoto()).into(holder.photo);
        }
        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNotificationClicked(i);
            }
        });

        holder.rel_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longListener.onLongNotificationClicked(i);
                return false;
            }
        });


        return myView;
    }

    class ViewHolder {
        ImageView photo;
        TextView message,date,time;
        LinearLayout rel_main;


    }
}

