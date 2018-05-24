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
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

import java.util.List;

public class LockApplicationService extends Service {

    private boolean screenOn = true;

    private BroadcastReceiver  appReadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if(action.equals(Intent.ACTION_SCREEN_OFF)){
                Log.e(getClass().toString(), "ScreenOFF");
                screenOn = false;
            }
            else if(action.equals(Intent.ACTION_SCREEN_ON)){
                Log.e(getClass().toString(), "ScreenON");
                screenOn = true;
            }
        }
    };
    /*
    private ActivityManager.RunningAppProcessInfo getForegroundApp(Context context){
        ActivityManager.RunningAppProcessInfo result = null, info = null;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
        List<ActivityManager.RunningAppProcessInfo> l = activityManager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
        while(i.hasNext()){
            info = i.next();
            if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && !isRunningService(info.processName,context)){
                Log.e(getClass().toString(), "found process");
                result = info;
                break;
            }
        }
        String str = "Processname: " + result.processName;
        //Log.e(getClass().toString(),str);
        return result;
    }*/

    private void checkForegroundApp(){
        while(screenOn){
            String actName = getRecentActivity(getBaseContext());
            SystemClock.sleep(1000);
            Log.d(getClass().toString(), actName);
        }
    }


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
                topActivityName = "";
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(appReadReceiver, filter);
        Log.e(getClass().toString(),"Service created");
    }

       @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        checkForegroundApp();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appReadReceiver);
        Log.e(getClass().toString(),"onDestroy()");
    }


    /**
     * The function to commit appUsageTime
     */
    private void commitAppUsageTime( final String appName, final int appUsageTime) {
        DataRepository dataRepository;
        dataRepository = ((MainApplication) getApplicationContext()).getRepository();
        dataRepository.addAppUsage(appName, appUsageTime);
    }

}