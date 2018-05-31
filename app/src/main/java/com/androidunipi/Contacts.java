package com.androidunipi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidunipi.data.ContactObj;
import com.androidunipi.model.JSONParser;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
 
import android.widget.TextView; 

@SuppressWarnings("deprecation")
public class Contacts extends Fragment implements ContactsAdapter.onContactClickedListener, RequestsAdapter.onRequestClickedListener, MyRequestsAdapter.onMyRequestClickedListener {//test123qqwesqweqs

	CommonData common;
	Activity myActivity;
	public ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private static String show_contacts = "http://androidun.eu.pn/android/show_contacts.php";
	private static String current_rate="http://androidun.eu.pn/android/current_rate.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "users";
	private static final String TAG_REQUESTS = "request";
	private static final String TAG_SUCCESSR = "success_r";
	private static final String TAG_MY_REQUESTS = "myrequest";
	private static final String TAG_MY_SUCCESSR = "my_success_r";
	ArrayList<ContactObj> usersList,request_list, my_request_list;
	TextView id, name, surname, username, gender, age, online, offline ;
	JSONArray users = null, requests=null, my_requests=null;
	private static final String TAG_PID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_SURNAME = "surname";
	private static final String TAG_ACTIVE = "active";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_IMG = "img";
	private static final String TAG_RATE = "rate";

	private static final String TAG_PID_R = "id";
	private static final String TAG_NAME_R = "name";
	private static final String TAG_SURNAME_R = "surname";
	private static final String TAG_ACTIVE_R = "active";
	private static final String TAG_EMAIL_R = "email";
	private static final String TAG_IMG_R = "img";
	private static final String TAG_CHAR = "charger";
	private static final String TAG_POWE = "powerbag";
	private static final String TAG_USB = "usb";

	private static final String TAG_MY_PID_R = "id";
	private static final String TAG_MY_USERNAME_R = "username";
	private static final String TAG_MY_ACTIVE_R = "active";
	private static final String TAG_MY_EMAIL_R = "email";
	private static final String TAG_MY_IMG_R = "img";
	private static final String TAG_MY_CHAR = "charger";
	private static final String TAG_MY_POWE = "powerbag";
	private static final String TAG_MY_USB = "usb";
	ListView listView,list_req, list_my_req;

	int success_r, success, my_success_r;
	TextView no_requests, no_contacts,no_my_requests;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
		common = CommonData.getInstance();
		common.current_fragment = "Contacts";

	
        listView    = (ListView) rootView.findViewById(R.id.list);
        list_req    = (ListView) rootView.findViewById(R.id.list_req);
		list_my_req =(ListView) rootView.findViewById(R.id.list_my_req);

		no_my_requests  = (TextView) rootView.findViewById(R.id.no_my_requests);
        no_requests     = (TextView) rootView.findViewById(R.id.no_requests);
        no_contacts	 	= (TextView) rootView.findViewById(R.id.no_contacts);
       
		usersList       = new ArrayList<>();
		request_list    = new ArrayList<>();
		my_request_list = new ArrayList<>();

	 
		  listView.setOnItemClickListener(new OnItemClickListener() {
			  
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                // getting values from selected ListItem
	                String mail = ((TextView) view.findViewById(R.id.email)).getText()
	                        .toString();
	                common.contact_email = mail;
	                ContactsDetailsFragment fragment = new ContactsDetailsFragment();
		            FragmentTransaction ft = getFragmentManager().beginTransaction();
		            ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
		            ft.replace(R.id.frame_container, fragment).commit();
	            }

			
	        });
		  list_req.setOnItemClickListener(new OnItemClickListener() {
			  
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                // getting values from selected ListItem
	                String mail = ((TextView) view.findViewById(R.id.email)).getText()
	                        .toString();
	                common.contact_email = mail;
	                ContactRequestFragment fragment = new  ContactRequestFragment();
		            FragmentTransaction ft = getFragmentManager().beginTransaction();
		            ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
		            ft.replace(R.id.frame_container, fragment).commit();
	 
	             
	            }

			
	        });


        new AllUsers().execute();
      
		return rootView;

   }

	public static class Utility {

		public static void setListViewHeightBasedOnChildren(ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = 0;
			int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
			listView.requestLayout();
		}
	}



	@Override
	public void onContactClicked(String email) {
		common.contact_email = email;
		ContactsDetailsFragment fragment = new ContactsDetailsFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
		ft.replace(R.id.frame_container, fragment).commit();
	}

	@Override
	public void onRequestClicked(String email) {
		common.contact_email = email;
		ContactRequestFragment fragment = new ContactRequestFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
		ft.replace(R.id.frame_container, fragment).commit();
	}

	@Override
	public void onMyRequestClicked(String email) {
		common.contact_email = email;
		MyRequestFragment fragment = new MyRequestFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
		ft.replace(R.id.frame_container, fragment).commit();
	}

	class AllUsers extends AsyncTask<String, String, String> {
	
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(myActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
	            pDialog.setMessage("Loading user's Contacts. Please wait...");
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
	           
	            JSONObject json = jsonParser.makeHttpRequest(
	                    show_contacts, "GET", params, "OBJECT");
	 
	            try {
	            	success = json.getInt(TAG_SUCCESS);
	                success_r = json.getInt(TAG_SUCCESSR);
					my_success_r =json.getInt(TAG_MY_SUCCESSR);

	                if (success == 1) {
	                    users = json.getJSONArray(TAG_USER);
	                    
	                    for (int i = 0; i < users.length(); i++) {
	                        JSONObject c = users.getJSONObject(i);
	 						ContactObj currentContact = new ContactObj();


							currentContact.setId(c.getString(TAG_PID));
							currentContact.setName(c.getString(TAG_NAME));
							currentContact.setSurname(c.getString(TAG_SURNAME));
							currentContact.setEmail(c.getString(TAG_EMAIL));
							currentContact.setActive(c.getString(TAG_ACTIVE));
							currentContact.setImage(c.getString(TAG_IMG));
							currentContact.setCharger(c.getString(TAG_CHAR));
							currentContact.setPowerbag(c.getString(TAG_POWE));
							currentContact.setUsb(c.getString(TAG_USB));
							currentContact.setRate(c.getDouble(TAG_RATE));

							usersList.add(currentContact);
						}
	            
	                } else {
	                 //TODO
	                }
	                if (success_r == 1) {
	                    requests = json.getJSONArray(TAG_REQUESTS);
	           
	                    for (int i = 0; i < requests.length(); i++) {
	                        JSONObject c = requests.getJSONObject(i);
							ContactObj currentRequest = new ContactObj();

							currentRequest.setId(c.getString(TAG_PID_R));
							currentRequest.setName(c.getString(TAG_NAME_R));
							currentRequest.setSurname(c.getString(TAG_SURNAME_R));
							currentRequest.setEmail(c.getString(TAG_EMAIL_R));
							currentRequest.setActive(c.getString(TAG_ACTIVE_R));
							currentRequest.setImage(c.getString(TAG_IMG_R));
							currentRequest.setCharger(c.getString(TAG_CHAR));
							currentRequest.setPowerbag(c.getString(TAG_POWE));
							currentRequest.setUsb(c.getString(TAG_USB));

							request_list. add(currentRequest);

	                    }
	            
	                } else {
	                 
	                }
					if (my_success_r == 1) {
						my_requests = json.getJSONArray(TAG_MY_REQUESTS);
						Log.e("THIS ID", "" + my_requests);

						for (int i = 0; i < my_requests.length(); i++) {
							JSONObject c = my_requests.getJSONObject(i);
							ContactObj currentMyRequest = new ContactObj();

							currentMyRequest.setId(c.getString(TAG_MY_PID_R));

							currentMyRequest.setUsername(c.getString(TAG_MY_USERNAME_R));
							currentMyRequest.setEmail(c.getString(TAG_MY_EMAIL_R));
							currentMyRequest.setActive(c.getString(TAG_MY_ACTIVE_R));
							currentMyRequest.setImage(c.getString(TAG_MY_IMG_R));
							currentMyRequest.setCharger(c.getString(TAG_MY_CHAR));
							currentMyRequest.setPowerbag(c.getString(TAG_MY_POWE));
							currentMyRequest.setUsb(c.getString(TAG_MY_USB));

							my_request_list. add(currentMyRequest);

						}

					} else {
						Log.e("Nothing ", "");

					}
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	 
	            return null;
	        }
	 

			protected void onPostExecute(String file_url) {
	            // dismiss the dialog after getting all products
	            pDialog.dismiss();

				ContactsAdapter adapter = new ContactsAdapter(usersList, myActivity, Contacts.this);
				listView.setAdapter(adapter);

				RequestsAdapter requests = new RequestsAdapter(request_list, myActivity, Contacts.this);
				list_req.setAdapter(requests);

				MyRequestsAdapter myrequests = new MyRequestsAdapter(my_request_list, myActivity, Contacts.this);
				list_my_req.setAdapter(myrequests);

				Utility.setListViewHeightBasedOnChildren(listView);
				Utility.setListViewHeightBasedOnChildren(list_my_req);
				Utility.setListViewHeightBasedOnChildren(list_req);
	            if(success==1)
	            {
	            	no_contacts.setVisibility(TextView.INVISIBLE);
	            }else{
	            	no_contacts.setVisibility(TextView.VISIBLE);
	            	
	            }
	            if(success_r==1)
	            {
	            	no_requests.setVisibility(TextView.INVISIBLE);
	            }else{
	            	no_requests.setVisibility(TextView.VISIBLE);
	            	
	            }
				if(my_success_r==1)
				{
					no_my_requests.setVisibility(TextView.INVISIBLE);
				}else{
					no_my_requests.setVisibility(TextView.VISIBLE);

				}

	        }
	  }
	
	 static int[] addElement(int[] a, int e) {
		    a  = Arrays.copyOf(a, a.length + 1);
		    a[a.length - 1] = e;
		    return a;
		}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		myActivity = activity;
	}
}