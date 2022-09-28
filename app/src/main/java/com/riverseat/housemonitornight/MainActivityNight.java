package com.riverseat.housemonitornight;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivityNight extends AppCompatActivity {

    TextView date;
    TextView time;
    Context context;
    ConstraintLayout mainLayout;
    WebView webView;
    private static Context mContext;
    public static MainActivityNight instace;
    LocalTime timeNow;
    Timer scheduledTimerNight;
    private ContentResolver contentResolver;
    private Window window;
    int BRIGHTNESS_VALUE=10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //android.os.SystemClock.sleep(6000);

        Log.d("Ron","Inside Night onCreate");
        setContentView(R.layout.activity_main_night);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = getApplicationContext();
        instace = this;
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, MainActivityNight.class,mContext));
        scheduledTimerNight = new Timer();

        context = this;
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        mainLayout = findViewById(R.id.mainLayoutNight);
        mainLayout.setFocusable(true);

        contentResolver = getContentResolver(); // for setting brightness
        window = getWindow(); // for setting brightness

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(Settings.System.canWrite(this)){
                //Toast.makeText(this,"Changing Brightness ok",Toast.LENGTH_SHORT).show();
            }else{
                Intent intentBright = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intentBright.setData(Uri.parse("package:"+getApplication().getPackageName()));
                startActivity(intentBright);
            }
        }
        Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,BRIGHTNESS_VALUE);


        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("http:192.168.5.136:8765"); //motioneye on doorbell raspi streaming the direct cam
    }

    @Override
    public void onStart(){
        super.onStart();
        scheduledTimerNight = new Timer();

        Log.d("Ron","Inside Night onStart");
        //android.os.SystemClock.sleep(6000);
        //Log.d("Ron","Inside Night Time Check finished delay");

        Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,BRIGHTNESS_VALUE);

        scheduledTimerNight.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                timeNow = LocalTime.now();
                Log.d("Ron","Inside Night Time Check");


                if((timeNow.getHour() >= 5)&& (timeNow.getHour() < 21)){
                    Intent intentNight = new Intent(MainActivityNight.this, MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentNight);
                    scheduledTimerNight.cancel();
                    scheduledTimerNight.purge();
                    //finish();
                }
            }
        }, 8000, 7000);//put here time 1000 milliseconds=1 second

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.d("Ron","Inside Night onNewIntent");
        Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,BRIGHTNESS_VALUE);

    }


    @Override
    protected void onStop(){
        super.onStop();
        Log.d("Ron","Inside Night onStop");
        //scheduledTimerNight.cancel();
        //scheduledTimerNight.purge();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("Ron","Inside Night onDestroy");
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MainActivityNight getInstance() {
        return instace;
    }

    //public void frTempLabelOnClick(View view){textView.setText("foo");}


}