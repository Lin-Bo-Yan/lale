package com.flowring.laleents.tools.cloud.mqtt;

import static com.pubnub.api.vendor.Base64.NO_WRAP;

import android.os.Handler;
import android.os.HandlerThread;

import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.LocalBroadcastControlCenter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;
import java.util.Date;


public class MqttControlCenter {
    String pubTopic = "/event/send";

    public MqttClient client;
    int qos = 2;
    int retry = 0;
    android.os.Handler handler;
    Runnable subscribe = new Runnable() {
        @Override
        public void run() {
            StringUtils.HaoLog("subscribe " + Thread.currentThread().getName());
            boolean subscribeOk = false;
            int ret = 0;
            while (!subscribeOk && ret < 100) {
                if (client.isConnected()) {
                    try {
                        client.subscribe(getSubTopic());
                        subscribeOk = true;
                    } catch (MqttException e) {

                        StringUtils.HaoLog("訂閱失敗重新嘗試" + retry++);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                } else {
                    ret++;
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
            if (ret >= 100) {
                try {
                    client.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                    StringUtils.HaoLog("訂閱失敗重新嘗試 強制關閉失敗");
                }
            }
            StringUtils.HaoLog("subscribe 訂閱成功");
        }
    };

    public void DisConnect() {
        stopNew = true;
        handler.post(EndMqtt);

    }

    boolean stopNew = false;

    public void NewConnect() {
        StringUtils.HaoLog("handler.post 2");
        stopNew = false;
        handler.post(NewConnect);
    }

    Runnable NewConnect = new Runnable() {
        @Override
        synchronized public void run() {
            if (stopNew){
                return;
            }
            StringUtils.HaoLog("NewConnect " + Thread.currentThread().getName());
            if (UserControlCenter.getUserMinInfo() == null || !UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim){
                return;
            }
            try {
                if (client != null) {
                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                    if (client.isConnected()) {
                        client.disconnectForcibly();
                    }
                    client.close();
                }
            } catch (MqttException | NullPointerException e) {
                e.printStackTrace();
            }
            try {
                connection();
            } catch (MqttException e) {
                e.printStackTrace();
                if (AllData.context != null) {
                    LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_Error, e.toString());
                }
                StringUtils.HaoLog("重新連線失敗 " + e);
            }
        }
    };

    MqttConnectOptions connOpts;

    synchronized String getBroker() {
        String Broker = UserControlCenter.getUserMinInfo().getExternalServerSetting().mqttUrl;
        StringUtils.HaoLog("Broker=" + Broker);
        return Broker;
    }

    synchronized String getClientId() {
        String ClientId = UserControlCenter.getUserMinInfo().token + "@" + System.currentTimeMillis();
        StringUtils.HaoLog("ClientId=" + ClientId);
        return ClientId;
    }

    synchronized String getSubTopic() {
        String subTopic = null;
        try {
            StringUtils.HaoLog("userId=" + UserControlCenter.getUserMinInfo().userId);
            subTopic = "/event/" + new String(android.util.Base64.encode(UserControlCenter.getUserMinInfo().userId.getBytes("UTF-8"), NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            StringUtils.HaoLog("SubTopic e=" + e);
            e.printStackTrace();
        }
        StringUtils.HaoLog("SubTopic=" + subTopic);
        return subTopic;
    }

    MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void connectionLost(Throwable cause) {
            StringUtils.HaoLog("發生錯誤=" + cause + new Date().getTime());
            handler.removeCallbacks(NewConnect);
            handler.postDelayed(NewConnect, 1000);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // subscribe後得到的消息會執行到這裡面
            new Thread(() -> {
                StringUtils.HaoLog("接收消息内容" + new String(message.getPayload()));
                MsgControlCenter.receiveMsg(new String(message.getPayload()), MsgControlCenter.Source.mqtt);
            }).start();


        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            new Thread(() -> {
                System.out.println("hao deliveryComplete" + token.toString());
            }).start();

        }
    };

    public MqttControlCenter() {
        HandlerThread thread = new HandlerThread("mqtt");
        thread.start();
        handler = new Handler(thread.getLooper());
        stopNew = false;
        handler.post(EndMqtt);
        // MQTT 連接選項
        initConnOpts();
        StringUtils.HaoLog("handler.post 3");
        handler.post(NewConnect);
    }

    void initConnOpts() {
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        connOpts.setKeepAliveInterval(20);
        connOpts.setConnectionTimeout(2);
        connOpts.setConnectionTimeout(10);
    }

    Runnable EndMqtt = new Runnable() {
        @Override
        synchronized public void run() {
            StringUtils.HaoLog("EndMqtt " + Thread.currentThread().getName());

            if (client != null) {
                try {
                    client.disconnectForcibly();
                    client.close();
                } catch (MqttException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void publishMessage(String data) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                StringUtils.HaoLog("publishMessage " + Thread.currentThread().getName());
                if (client != null) {
                    StringUtils.HaoLog("publishMessage:" + data);
                    MqttMessage message = new MqttMessage(data.getBytes());
                    message.setQos(qos);
                    try {
                        client.publish(pubTopic, message);
                    } catch (MqttException e) {
                        StringUtils.HaoLog("發送失敗 " + e);
                        StringUtils.HaoLog("handler.post 4");
                        handler.post(NewConnect);
                        e.printStackTrace();
                    }
                } else {
                    StringUtils.HaoLog("handler.post 5");
                    handler.post(NewConnect);
                }
            }
        });
    }

    private void connection() throws MqttException{
        client = new MqttClient(getBroker(), getClientId(), new MemoryPersistence());
        StringUtils.HaoLog("建立連線");
        client.setCallback(mqttCallback);
        StringUtils.HaoLog("mqttCallback");
        initConnOpts();
        client.connect(connOpts);
        StringUtils.HaoLog("connOpts");
        handler.post(subscribe);
        StringUtils.HaoLog("重新連線成功");
    }

}

