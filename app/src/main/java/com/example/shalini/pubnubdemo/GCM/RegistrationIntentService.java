package com.example.shalini.pubnubdemo.GCM;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.shalini.pubnubdemo.MainActivity;
import com.example.shalini.pubnubdemo.PubnubConfig;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;

import java.io.IOException;

/**
 * Created by shalini on 6/5/16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "REG_INTENT_SERVICE";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String GCM_TOKEN = "gcmToken";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // make a call to instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(GCMConfig.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.e(TAG, "GCM Registration Token: " + token);

            //save token
            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();

            enablePushNotification();
            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }

    }
    private void enablePushNotification() {
        SharedPreferences registrationToken = PreferenceManager.getDefaultSharedPreferences(this);
        String token = registrationToken.getString(RegistrationIntentService.GCM_TOKEN,"default");
        (MainActivity.pubnub).enablePushNotificationsOnChannel(PubnubConfig.CHANNEL_NAME, token, new Callback() {
            @Override
            public void successCallback(String s, Object o) {
                super.successCallback(s, o);
            }

            @Override
            public void errorCallback(String s, PubnubError pubnubError) {
                super.errorCallback(s, pubnubError);
            }
        });
    }
    private void sendRegistrationToServer(String token) {
        // send network request

        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();

        Intent intentHome = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentHome);
    }
}
