package com.example.shalini.pubnubdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.shalini.pubnubdemo.Adapter.DividerItemDecoration;
import com.example.shalini.pubnubdemo.Adapter.RecyclerViewAdpater;
import com.example.shalini.pubnubdemo.GCM.RegistrationIntentService;
import com.example.shalini.pubnubdemo.Model.ChatMessage;
import com.pubnub.api.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

  //  private TextView mTextviewsMessage;
    private Button mButtonPublish;
    public static Pubnub pubnub;
    private RecyclerView mRecyclerViewChat;
    private LinearLayoutManager mLayoutManager;
    private RecyclerViewAdpater mRecyclerViewAdapter;
    List<ChatMessage> chatMessageList;
    private EditText mEditTextEnterMessage;
    private LinearLayout mLinearlayoutMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatMessageList = new ArrayList<>();
        linkViewsId();
        pubnub = new Pubnub(PubnubConfig.PUBLISH_KEY,PubnubConfig.SUBSCRIBER_KEY);

        Intent intent = new Intent(this,RegistrationIntentService.class);
        startService(intent);

        mButtonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextEnterMessage.getText().toString().trim();
                if(!message.isEmpty()){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage(message);
                    chatMessage.setPublisher(true);
                    chatMessageList.add(chatMessage);
                    if(mRecyclerViewAdapter == null){
                        mRecyclerViewAdapter = new RecyclerViewAdpater(MainActivity.this,chatMessageList);
                        mRecyclerViewChat.setAdapter(mRecyclerViewAdapter);
                    }else{
                        mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount());
                    }
                   publishMessage(message);
                }
            }
        });
        subscribeToChannel();
    }




    private void linkViewsId() {
        // mTextviewsMessage = (TextView) findViewById(R.id.textview_message);
        mButtonPublish = (Button) findViewById(R.id.button_publish);
        mEditTextEnterMessage = (EditText) findViewById(R.id.edittext_enter_message);
        mLinearlayoutMessage = (LinearLayout) findViewById(R.id.linearlayout_message);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewChat.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerViewChat.addItemDecoration(itemDecoration);
        mRecyclerViewChat.setHasFixedSize(true);
      //  mRecyclerViewAdapter = new RecyclerViewAdpater(this,chatMessageList);
      //  mRecyclerViewChat.setAdapter(mRecyclerViewAdapter);

    }

    private void publishMessage(String messageToPublish) {
        Callback publishCallback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        pubnub.publish(PubnubConfig.CHANNEL_NAME,messageToPublish, publishCallback);
    }

    private void subscribeToChannel() {
        try {
            pubnub.subscribe(PubnubConfig.CHANNEL_NAME, new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                           /* pubnub.publish(PubnubConfig.CHANNEL_NAME, "Hello from the PubNub Java SDK", new Callback() {
                            });
                           */ System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }
                        @Override
                        public void successCallback(final String channel, final Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            boolean sameMessages = false;
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setMessage(message.toString());
                            chatMessage.setPublisher(false);
                            for (int i = 0; i < chatMessageList.size(); i++) {
                                if((chatMessageList.get(i).equals(chatMessage))){
                                    sameMessages = true;
                                    break;
                                }
                            }
                            if(!sameMessages){
                                chatMessageList.add(chatMessage);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(mRecyclerViewAdapter == null){
                                            mRecyclerViewAdapter = new RecyclerViewAdpater(MainActivity.this,chatMessageList);
                                            mRecyclerViewChat.setAdapter(mRecyclerViewAdapter);
                                        }else{
                                            mRecyclerViewChat.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                                });
                            }


                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
