package com.jason.app.smstoemail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jason.app.smstoemail.fragments.SettingsFragment;

public class NJReceiver extends BroadcastReceiver {
    private static final String TAG = "NJReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "NJReceiver-onReceive");
        SettingsFragment.load(context, EmailMager.getInstance());
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_LOW:
                EmailMager.getInstance().sendMail(context.getString(R.string.batteryStatus), context.getString(R.string.batteryLow));
                break;
            case Intent.ACTION_BATTERY_OKAY:
                EmailMager.getInstance().sendMail(context.getString(R.string.batteryStatus), context.getString(R.string.batteryOkay));
                break;
            case Intent.ACTION_POWER_CONNECTED:
                EmailMager.getInstance().sendMail(context.getString(R.string.batteryStatus), context.getString(R.string.powerConnected));
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                EmailMager.getInstance().sendMail(context.getString(R.string.batteryStatus), context.getString(R.string.powerDisConnected));
                break;
        }
    }
}
