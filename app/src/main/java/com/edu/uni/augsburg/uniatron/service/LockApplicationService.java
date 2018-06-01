package com.edu.uni.augsburg.uniatron.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.edu.uni.augsburg.uniatron.MainApplication;

import java.util.List;

public class LockApplicationService extends Service {

    Handler handler;

    private BroadcastReceiver  appReadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if(action.equals(Intent.ACTION_SCREEN_OFF)){
                Log.d(getClass().toString(), "ScreenOFF");
                stopHandler();
            }

            // we only check the foreground app when the screen is on
            else if(action.equals(Intent.ACTION_SCREEN_ON)){
                Log.d(getClass().toString(), "ScreenON");
                startHandler();
            }
        }
    };

    private void startHandler() {
        // every <delay> seconds, we check which app is in the foreground
        handler = new Handler();
        int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                Log.d(getClass().toString(), "trying to get foreground app");
                checkForegroundApp();

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void stopHandler() {
        if (handler != null ) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(appReadReceiver, filter);
        Log.d(getClass().toString(),"Service created");

        // we have to start the handler once upon service start
        startHandler();

    }

       @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(getClass().toString(), "onStartCommand Function");
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appReadReceiver);
        Log.d(getClass().toString(),"onDestroy()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * The function to commit appUsageTime
     */
    private void commitAppUsageTime( final String appName, final int appUsageTime) {
        ((MainApplication) getApplicationContext()).getRepository().addAppUsage(appName,appUsageTime);
    }

    private void checkForegroundApp() {
        String actName = getRecentActivity(getBaseContext());
        Log.d(getClass().toString(), "App used: " + actName);
        SystemClock.sleep(1000);
        if (actName != "" ) {
            //commitAppUsageTime(actName, 1);
        }
    }

    //TODO handle get app for under lollipop
    //TODO ask for permission with library (@... get permission or simmilar)
    public String getRecentActivity(Context context) {
        String topActivityName = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            long time = System.currentTimeMillis();

            UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 30, System.currentTimeMillis() + (10 * 1000));
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
            }
            if (event != null && !TextUtils.isEmpty(event.getPackageName()) && event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                return event.getPackageName();
            } else {
                topActivityName = "didn't work";
                Log.d(getClass().toString(), "event: " + event.getPackageName());
                Log.d(getClass().toString(), "event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND: " + (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND?"true":"false"));

            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;

            topActivityName = componentInfo.getClassName();
        }
        return topActivityName;
    }

    /*
    private boolean isRunningService(String processname, Context context){
        if (processname == null || processname.isEmpty()){
            return false;
        }
        ActivityManager.RunningServiceInfo service;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = activityManager.getRunningServices(9999);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()){
            service = i.next();
            if(service.process.equals(processname)){
                return true;
            }
        }
        return false;
    }
    */

}