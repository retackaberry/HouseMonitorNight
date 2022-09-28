package com.riverseat.housemonitornight;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This custom class is used to handle exception.
 *
 * @author Chintan Rathod (http://www.chintanrathod.com)
 */
public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    Activity activity;
    private final Class<?> myActivityClass;
    public final Context myContext;


    public MyExceptionHandler(Activity activity,Class<?> c,Context ct) {
        this.activity = activity;
        myActivityClass = c;
        myContext = ct;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        try {
            Log.e("HANDLER","Inside Exception Handler");
            Intent intent = new Intent(activity, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    //MainActivity.getInstance().getBaseContext(),
                    myContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            //Following code will restart your application after 2 seconds
           /*
            AlarmManager mgr = (AlarmManager) MainActivity.getInstance().getBaseContext()
                    .getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    pendingIntent);
            */

            System.out.println("Inside Exception Handler");

            //This will finish your activity manually
            activity.finish();
            myContext.startActivity(intent);
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            //This will stop your application and take out from it.
            System.exit(2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
