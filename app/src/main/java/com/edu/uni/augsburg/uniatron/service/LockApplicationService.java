package com.edu.uni.augsburg.uniatron.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

import java.util.Iterator;
import java.util.List;

public class LockApplicationService extends Service {


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
                result = info;
                break;
            }
        }
        Log.e(getClass().toString(),result.toString());
        return result;
    }

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



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // this causes the OS to restart the service if it has been force stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * The function to commit appUsageTime
     */
    private void commitAppUsageTime( final String appName, final int appUsageTime) {
        DataRepository dataRepository;
        dataRepository = ((MainApplication) getApplicationContext()).getRepository();
        dataRepository.addAppUsage(appName, appUsageTime);
    }



    // BroadcastReceiver mitverwenden und Service dort starten
}
