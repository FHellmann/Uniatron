package com.edu.uni.augsburg.uniatron.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

import java.util.Calendar;
import java.util.List;

public class LockApplicationService extends Service {

    private boolean serviceRunning = false;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getForegroundApp(Context context){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && !mIsChecked) {
            if (getUsageStatsList(getActivity()).isEmpty()) {

                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }



    }

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
                return event.getClassName();
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

    public String getRecentApps(Context context) {
        String topPackageName = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            long time = System.currentTimeMillis();

            UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 30, System.currentTimeMillis() + (10 * 1000));
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
            }

            if (event != null && !TextUtils.isEmpty(event.getPackageName()) && event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                if (AndroidUtils.isRecentActivity(event.getClassName())) {
                    return event.getClassName();
                }
                return event.getPackageName();
            } else {
                topPackageName = "";
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;

            if (AndroidUtils.isRecentActivity(componentInfo.getClassName())) {
                return componentInfo.getClassName();
            }

            topPackageName = componentInfo.getPackageName();
        }


        return topPackageName;
    }


    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);

        return usageStatsList;
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
        serviceRunning = true;
        checkForegroundedApp();
        Log.e(getClass().toString(),"Service created");

    }

       @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // this causes the OS to restart the service if it has been force stopped
        serviceRunning = true;
        checkForegroundedApp();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(getClass().toString(),"onDestroy()");
        serviceRunning = false;
    }


    /**
     * The function to commit appUsageTime
     */
    private void commitAppUsageTime( final String appName, final int appUsageTime) {
        DataRepository dataRepository;
        dataRepository = ((MainApplication) getApplicationContext()).getRepository();
        dataRepository.addAppUsage(appName, appUsageTime);
    }

    private void checkForegroundedApp(){
        //while(serviceRunning == true) {
            //ActivityManager.RunningAppProcessInfo process;
            //process = getForegroundApp(getApplicationContext());
            //if (process != null) {
        while(serviceRunning){
            SystemClock.sleep(1000);
            String result = getForegroundApp(getBaseContext());
            String str = "check foreground "+ String.valueOf(serviceRunning) + " Foreground App"+result;
            //Log.e(getClass().toString(), str);
        }
            //}
           // SystemClock.sleep(5000);
        //}
    }
    // BroadcastReceiver mitverwenden und Service dort starten
}