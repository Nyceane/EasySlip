/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.spotvite.packagedelivery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;


/**
 * Main activity - requests "Hello, World" messages from the server and provides
 * a menu item to invoke the accounts activity.
 */
public class PackageDeliveryActivity extends Activity {
    /**
     * Tag for logging.
     */
    private static final String TAG = "PackageDeliveryActivity";

    /**
     * The current context.
     */
    private Context mContext = this;

    private String url;
    
    /**
     * A {@link BroadcastReceiver} to receive the response from a register or
     * unregister request, and to update the UI.
     */
    private final BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(DeviceRegistrar.STATUS_EXTRA,
                    DeviceRegistrar.ERROR_STATUS);
            String message = null;
            if (status == DeviceRegistrar.REGISTERED_STATUS) {
                message = getResources().getString(R.string.registration_succeeded);
            } else if (status == DeviceRegistrar.UNREGISTERED_STATUS) {
                message = getResources().getString(R.string.unregistration_succeeded);
            } else {
                message = getResources().getString(R.string.registration_error);
            }

            // Display a notification
            SharedPreferences prefs = Util.getSharedPreferences(mContext);
            String accountName = prefs.getString(Util.ACCOUNT_NAME, "Unknown");
            Util.generateNotification(mContext, String.format(message, accountName));
        }
    };

    /**
     * Begins the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_world);	    

        if(getIntent().getExtras() != null)
        {
        	url = getIntent().getExtras().getString("url");
        }
        // Register a receiver to provide register/unregister notifications
        registerReceiver(mUpdateUIReceiver, new IntentFilter(Util.UPDATE_UI_INTENT));
    }

    /**
     * Shuts down the activity.
     */
    @Override
    public void onDestroy() {
        unregisterReceiver(mUpdateUIReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Invoke the Register activity
        menu.getItem(0).setIntent(new Intent(this, AccountsActivity.class));
        return true;
    }
    
    public void btnSign_Click(View target)
    {
    	Uri uri = Uri.parse(url); 
    	Log.e("Url", url);
    	startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
    
    public void btnCancel_Click(View target)
    {
    	finish();
    }
}
