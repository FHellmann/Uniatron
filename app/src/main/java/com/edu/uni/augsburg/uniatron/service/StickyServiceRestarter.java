package com.edu.uni.augsburg.uniatron.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The BroadCastReceiver starts the StepCountService as soon as boot is completed.
 *
 * @author Leon Wöhrl
 */
public class StickyServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
            ServiceUtil.startService(context, StickyService.class);
        }
    }
}
