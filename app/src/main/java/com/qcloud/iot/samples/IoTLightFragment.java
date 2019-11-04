package com.qcloud.iot.samples;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qcloud.iot.R;
import com.qcloud.iot.samples.data_template.DataTemplateSample;
import com.qcloud.iot.samples.scenarized.LightSample;
import com.qcloud.iot_explorer.common.Status;
import com.qcloud.iot_explorer.data_template.TXDataTemplateDownStreamCallBack;
import com.qcloud.iot_explorer.mqtt.TXMqttActionCallBack;
import com.qcloud.iot_explorer.mqtt.TXMqttRequest;
import com.qcloud.iot_explorer.utils.TXLog;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class IoTLightFragment extends Fragment {
    private static final String TAG = "IoTLightFragment";

    private IoTMainActivity mParent;

    private LightSample mLightSample;

    private Button mConnectBtn;
    private Button mCloseConnectBtn;
    private Button mCheckFirmwareBtn;
    private TextView mProperty;

    // Default testing parameters
    private String mBrokerURL = "ssl://iotcloud-mqtt.gz.tencentdevices.com:8883";
    private String mProductID = "Q82Y1XV1O7";
    private String mDevName = "tes";
    private String mDevPSK  = "xztPM4VYbhmd09HwoUvJdg=="; //若使用证书验证，设为null

    private String mDevCert = "";           // Cert String
    private String mDevPriv = "";           // Priv String

    private final static String mJsonFileName = "light_sample.json";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_demo, container, false);

        mParent = (IoTMainActivity) this.getActivity();

        mConnectBtn = view.findViewById(R.id.connect);
        mCloseConnectBtn = view.findViewById(R.id.close_connect);
        mCheckFirmwareBtn = view.findViewById(R.id.check_firmware);
        mProperty =  view.findViewById(R.id.property);

        mLightSample = new LightSample(mParent, mBrokerURL, mProductID, mDevName, mDevPSK, mJsonFileName);

       //mProperty.setText(info);
        new setPropertyText().start();

        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLightSample == null)
                    return;
                mLightSample.online();
            }
        });

        mCloseConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLightSample == null)
                    return;
                mLightSample.offline();
            }
        });

        mCheckFirmwareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLightSample == null)
                    return;
                mLightSample.checkFirmware();
            }
        });

        return view;
    }

    /**
     * 显示信息
     */
    private class setPropertyText extends Thread {
        public void run() {
            StringBuilder textInfo;
            while (!isInterrupted()) {
                if(mLightSample.isOnline()) {
                    textInfo = new StringBuilder("Status: online");
                } else {
                    textInfo = new StringBuilder("Status: offline");
                }
                textInfo.append("\r\nVersion: ").append(mLightSample.mVersion);

                for(Map.Entry<String, Object> entry: mLightSample.mProperty.entrySet())
                {
                    textInfo.append("\r\n").append(entry.getKey()).append(": ").append(entry.getValue());
                }

                mProperty.setText(textInfo.toString());

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    TXLog.e(TAG, "The thread has been interrupted");
                    break;
                }
            }
        }
    }

    public void closeConnection() {
        if (mLightSample == null)
            return;
        mLightSample.offline();
    }
}