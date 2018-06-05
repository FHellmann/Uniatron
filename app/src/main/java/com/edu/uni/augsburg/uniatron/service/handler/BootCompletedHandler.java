package com.edu.uni.augsburg.uniatron.service.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.edu.uni.augsburg.uniatron.service.BroadcastService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;
import com.edu.uni.augsburg.uniatron.service.util.ServiceUtil;

/**
 * The BroadCastReceiver starts the StepCountService as soon as boot is completed.
 *
 * @author Leon Wöhrl
 */
public class BootCompletedHandler extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
            ServiceUtil.startServices(
                    context,
                    StepCountService.class,
                    BroadcastService.class
            );
        }
    }
}
