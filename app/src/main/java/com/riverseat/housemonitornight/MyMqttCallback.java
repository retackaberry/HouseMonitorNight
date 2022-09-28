package com.riverseat.housemonitornight;

import android.app.Activity;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttCallback extends AppCompatActivity implements MqttCallback {

    TextView frTemp;
    TextView offTemp;
    TextView brTemp;
    TextView entTemp;
    TextView date;
    TextView time;
    Activity A;
    MainActivity mainActivity;

    MyMqttCallback(Activity A){
        this.A = A;
        frTemp = A.findViewById(R.id.frTemp);
        offTemp = A.findViewById(R.id.offTemp);
        brTemp = A.findViewById(R.id.brTemp);
        entTemp = A.findViewById(R.id.entTemp);

        //date = A.findViewById(R.id.date);
        //date.setText("Callback");
    }
    @Override
    public void connectionLost(Throwable cause) {
        mainActivity.connectX();
        System.out.println("Connection was lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload());

        switch(topic){
            case "home-assistant/sensor/family_room_temp_sensor_temperature/state": {
                frTemp.setText(msg+ " °C");
                break;
            }
            case "home-assistant/sensor/office_temp_sensor_temperature/state": {
                offTemp.setText(msg+ " °C");
                break;
            }
            case "home-assistant/sensor/bedroom_temp_sensor_temperature/state": {
                brTemp.setText(msg+ " °C");
                break;
            }
            case "home-assistant/sensor/entry_temp_sensor_c226_temperature/state": {
                entTemp.setText(msg+ " °C");
                break;
            }
            default: {
                break;
            }
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery Complete!");
    }


}
