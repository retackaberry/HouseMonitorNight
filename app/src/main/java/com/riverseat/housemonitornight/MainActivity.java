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

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity  {
    MqttAndroidClient client;
    MqttConnectOptions options;
    String clientId;
    String topic;
    String user = "mqtt";
    String pw = "Eallen1ha";
    TextView frTemp;
    TextView offTemp;
    TextView brTemp;
    TextView entTemp;
    TextView date;
    TextView time;
    Context context;
    boolean connected;
    ConstraintLayout mainLayout;
    WebView webView;
    private static Context mContext;
    public static MainActivity instace;
    private TextView textView;
    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    LocalTime timeNow;
    Timer scheduledTimerMain;
    Timer scheduledTimerMain2;
    private ContentResolver contentResolver;
    private Window window;
    Boolean notFlipped=true;
    int BRIGHTNESS_VALUE=90;


    @Override
    protected void onStart(){

        super.onStart();
        Log.d("Ron","Inside Main onStart");

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        scheduledTimerMain2 = new Timer();
        //android.os.SystemClock.sleep(10000);
        Log.d("Ron","Inside Main onNewIntent");
        Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,BRIGHTNESS_VALUE);


        scheduledTimerMain2.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                timeNow = LocalTime.now();
                Log.d("Ron","Inside Main Time Check");
                if((timeNow.getHour() >= 21) || (timeNow.getHour()>=0 && timeNow.getHour()<5)){
                    notFlipped = false;
                    Intent intentMain = new Intent(MainActivity.this, MainActivityNight.class);
                    //intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentMain);
                    scheduledTimerMain2.cancel();
                    scheduledTimerMain2.purge();
                    //finish();
                }

            }
        }, 8000,7000);//put here time 1000 milliseconds=1 second
        //setIntent(intent);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("Ron","Inside Main onStop");
        //scheduledTimerMain.cancel();
        //scheduledTimerMain.purge();
        //scheduledTimerMain2.cancel();
        //scheduledTimerMain2.purge();
        notFlipped = false;
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("Ron","Inside Main onRestart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("Ron","Inside Main onResume");
        SubscribeRunnable subscribeRunnable = new SubscribeRunnable(this);
        Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,BRIGHTNESS_VALUE);
        //new  Thread(subscribeRunnable).start();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("Ron","Inside Main onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        scheduledTimerMain = new Timer();
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = getApplicationContext();
        instace = this;
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, MainActivity.class,mContext));
        Log.d("Ron","Inside Main onCreate and notFlipped = "+ notFlipped.toString());

        context = this;
        connected = false;
        frTemp = findViewById(R.id.frTemp);
        offTemp = findViewById(R.id.offTemp);
        brTemp = findViewById(R.id.brTemp);
        entTemp = findViewById(R.id.entTemp);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setFocusable(true);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("http:192.168.5.136:8765"); //motioneye on doorbell raspi streaming the direct cam
//        webView.loadUrl("http:192.168.5.200:8081"); //motioneye on HA streaming the tapo cam

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityNight.class);
                startActivity(intent);
            }
        });

        scheduledTimerMain.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                timeNow = LocalTime.now();
                Log.d("Ron","Inside Main Time Check");
                if((timeNow.getHour() >= 21) || (timeNow.getHour()>=0 && timeNow.getHour()<5)){
                    notFlipped = false;
                    Intent intentMain = new Intent(MainActivity.this, MainActivityNight.class);
                    //intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentMain);
                    scheduledTimerMain.cancel();
                    scheduledTimerMain.purge();
                }

            }
        }, 8000, 7000);//put here time 1000 milliseconds=1 second

        init();
    }


    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MainActivity getInstance() {
        return instace;
    }

    public void frTempLabelOnClick(View view){
        textView.setText("foo"); //Since textView doesn't exist, throws uncaught exception forcing a restart of program
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    public void init(){
        clientId = MqttClient.generateClientId();
        options = new MqttConnectOptions();
        options.setUserName("mqtt");
        options.setPassword("Eallen1ha".toCharArray());
        options.setAutomaticReconnect(true);
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.5.200:1883", clientId);

        connectX();
    }

    public void connectX()  {
        IMqttToken token = new MyMqttToken();

        try {
            token = client.connect(options);
            MyIMqttActionListener myIMqttActionListener = new MyIMqttActionListener(this,client);
            token.setActionCallback(myIMqttActionListener);
         } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void subscribe(){
        Log.d("Ron","Inside Subscribe");
        try {
            MyMqttCallback myMqttCallback = new MyMqttCallback(this);
            topic = "home-assistant/sensor/family_room_temp_sensor_temperature/state";
            client.subscribe(topic, 0);
            client.setCallback(myMqttCallback);
            topic = "home-assistant/sensor/office_temp_sensor_temperature/state";
            client.subscribe(topic, 0);
            topic = "home-assistant/sensor/bedroom_temp_sensor_temperature/state";
            client.subscribe(topic, 0);
            topic = "home-assistant/sensor/entry_temp_sensor_c226_temperature/state";
            client.subscribe(topic, 0);

        } catch (Exception b) {
        }
    }

    class SubscribeRunnable implements Runnable{
        Activity A;

        SubscribeRunnable(Activity A){
            Log.d("Ron","Inside SubscribeRunnable");
            this.A = A;
        }

        @Override
        public void run() {
            Log.d("Ron","Inside run");
       }
    }

}
