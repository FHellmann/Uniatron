package com.edu.uni.augsburg.uniatron.Services;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Iterator;
import java.util.List;

public class LockApplication extends AsyncTask<Context,Void, Boolean> {

    @Override
    protected Boolean doInBackground(Context... params){
        final Context context = params[0].getApplicationContext();
        return isAppOnForeground(context);
    }

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

}
