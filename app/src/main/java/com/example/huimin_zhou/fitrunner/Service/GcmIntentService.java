package com.example.huimin_zhou.fitrunner.Service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.huimin_zhou.fitrunner.Database.DatabaseSource;
import com.example.huimin_zhou.fitrunner.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lucidity on 17/2/22.
 */

public class GcmIntentService extends IntentService {
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                String id = extras.getString("message");
                DatabaseSource databaseSource = new DatabaseSource(getApplication());
                databaseSource.open();
                databaseSource.delete(databaseSource.queryOne(Long.valueOf(id)));
                databaseSource.close();
                showToast();
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                // update hisotry fragment
                MainActivity.history.updateAdapter();
                // Toast.makeText(getApplicationContext(), "Delete #" + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
