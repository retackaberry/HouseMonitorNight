package com.riverseat.housemonitornight;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MyIMqttActionListener implements IMqttActionListener {

    MqttAndroidClient client;
    MainActivity A;

    MyIMqttActionListener(MainActivity A,MqttAndroidClient client){
       this.client = client;
       this.A = A;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.d("Ron","Inside onSuccess connect");
        // We are connected
        if (client.isConnected()) {
            //connected = true;
            A.subscribe();
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.d("ronx", "On Failure");
    }

}
