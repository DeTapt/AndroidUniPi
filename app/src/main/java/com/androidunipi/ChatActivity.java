package com.androidunipi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidunipi.data.MessageObj;
import com.androidunipi.data.NotificationObj;
import com.androidunipi.model.JSONParser;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    public static final String MESSAGES_CHILD = "messages";
    private String mUsername;
    private String mPhotoUrl;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<MessageObj, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private EditText mMessageEditText;

    private NotificationObj dataObj;

    private CommonData common;

    private JSONParser jsonParser = new JSONParser();
    private static String profile = "http://androidun.eu.pn/android/profile.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_Token = "user_code";
    private static final String TAG_USER = "users";
    private static String get_token="http://androidun.eu.pn/android/get_token.php";

    private String my_user_name = "", my_user_surname = "", my_user_image = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        common = CommonData.getInstance();

        common.isChatVisible = true;


        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                dataObj = (NotificationObj) extras.getSerializable("data");
            }

        }


        mUsername = common.userName;
        mPhotoUrl = common.userImage;


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query items = mFirebaseDatabaseReference.child(MESSAGES_CHILD).orderByChild("authIDs").equalTo(dataObj.getChat_token());

        mFirebaseAdapter = new FirebaseRecyclerAdapter<MessageObj, MessageViewHolder>(
                MessageObj.class,
                R.layout.item_message,
                MessageViewHolder.class,
                items) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, MessageObj friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getText());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(ChatActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SendNotification().execute();
                MessageObj friendlyMessage = new MessageObj(mMessageEditText.getText().toString(), mUsername,
                            mPhotoUrl, dataObj.getChat_token());
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
                mMessageEditText.setText("");

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    class SendNotification extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    JSONArray my_user = json_profile.getJSONArray(TAG_USER);
                    c = my_user.getJSONObject(0);

                    my_user_name = c.getString("name");
                    my_user_surname = c.getString("surname");
                    my_user_image = c.getString("img");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", dataObj.getEmail()));

            JSONObject token_json = jsonParser.makeHttpRequest(get_token, "GET", params, "OBJECT");

            try {
                int token_success = token_json.getInt(TAG_Token);
                if (token_success == 1) {
                    JSONArray tokens = token_json.getJSONArray("token");
                    List<NameValuePair> params_token = new ArrayList<NameValuePair>();
                    params_token.add(new BasicNameValuePair("name", my_user_name));
                    params_token.add(new BasicNameValuePair("surname", my_user_surname));
                    params_token.add(new BasicNameValuePair("img", my_user_image));
                    params_token.add(new BasicNameValuePair("email", common.token_email));
                    params_token.add(new BasicNameValuePair("message", "You have a new message "));
                    params_token.add(new BasicNameValuePair("body", "You have a new message "));
                    params_token.add(new BasicNameValuePair("chat_token", dataObj.getChat_token()));

                    for (int i = 0; i < tokens.length(); i++) {
                        JSONObject c = tokens.getJSONObject(i);
                        params_token.add(new BasicNameValuePair("token", c.getString("token")));
                    }

                    JSONObject json = jsonParser.makeHttpRequest(
                            "http://unipi.netne.net/android/send_message_not.php", "GET", params_token, "OBJECT");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
