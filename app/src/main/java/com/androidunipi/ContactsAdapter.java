package com.androidunipi;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidunipi.data.ContactObj;
import com.androidunipi.data.NotificationObj;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ARISTEAA on 10/10/2016.
 */
public class ContactsAdapter extends BaseAdapter {
    private ContactsAdapter thisAdapter;
    private ArrayList<ContactObj> list;
    private Activity myActivity;
    private onContactClickedListener listener;

    public interface onContactClickedListener {
        void onContactClicked(String email);
    }

    public ContactsAdapter(ArrayList<ContactObj> list, Activity myActivity, onContactClickedListener listener) {
        this.list = list;
        this.myActivity = myActivity;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = null;

        if(view == null) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater inflator = myActivity.getLayoutInflater();

            myView = inflator.inflate(R.layout.contacts_list_item, null);

            holder.email = (TextView) myView.findViewById(R.id.email);
            holder.name = (TextView) myView.findViewById(R.id.name);
            holder.surname = (TextView) myView.findViewById(R.id.surname);
            holder.photo = (ImageView) myView.findViewById(R.id.photo);
            holder.rel_main = (LinearLayout) myView.findViewById(R.id.rel_main1);
            holder.ratingBar=(RatingBar) myView.findViewById(R.id.rating);
            holder.online=(TextView)myView.findViewById(R.id.online);
            holder.offline=(TextView)myView.findViewById(R.id.offline);

            myView.setTag(holder);
            //exampel how setTag works
            holder.name.setTag(list.get(i).getId());
        }
        else{
            myView = view;
        }

        ViewHolder holder = (ViewHolder) myView.getTag();
        final ContactObj contact = list.get(i);

        holder.email.setText(contact.getEmail());
        holder.name.setText(contact.getName());
        holder.surname.setText(contact.getSurname());
        float f = contact.getRate().floatValue();
        holder.ratingBar.setRating(f);

        Picasso.with(myActivity).load(contact.getImage()).into(holder.photo);


      //  holder.email.setTextColor(contact.getActive().equals("1") ? R.color.green : R.color.red);
        if(contact.getActive().equals("1"))
        {
            holder.online.setVisibility(TextView.VISIBLE);
            holder.offline.setVisibility(TextView.INVISIBLE);
        }else{
            holder.online.setVisibility(TextView.INVISIBLE);
            holder.offline.setVisibility(TextView.VISIBLE);
        }


        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onContactClicked(contact.getEmail());
            }
        });


        return myView;
    }

    class ViewHolder {
        ImageView photo;
        TextView name, email, surname,online, offline;
        LinearLayout rel_main;
        RatingBar ratingBar;

    }
}
