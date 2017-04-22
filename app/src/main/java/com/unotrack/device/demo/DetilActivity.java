package com.unotrack.device.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.minew.device.MinewDevice;
import com.minew.device.MinewDeviceValue;
import com.unotrack.device.demo.interfaces.unobeacTagListener;
import com.unotrack.device.demo.unobeacTag.unobeacTag;
import com.unotrack.device.demo.unobeacTag.unobeacTagManager;
import com.minew.device.enums.InstrucIndex;
import com.minew.device.enums.ValueIndex;

import java.util.ArrayList;
import java.util.List;

public class DetilActivity extends AppCompatActivity {

    private unobeacTagManager munobeacTagManager;
    private unobeacTag munobeacTag;
    private RecyclerView mRecycle;
    private DetilListAdapter mAdapter;
    private TextView mDetil_connected;
    private boolean isSearch;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detil);

        initView();
        initManager();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        munobeacTag.setunobeacTagListener(new unobeacTagListener() {
            /**
             *  this method will call back if the value of device updated.
             *
             *  @param minewDevice  which device update a value.
             *  @param value   value of value which updated.
             */
            @Override
            public void onUpdateValue(final MinewDevice minewDevice, MinewDeviceValue value) {
                final List<String> deviceData = getDeviceData(minewDevice);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (minewDevice.getValue(ValueIndex.ValueIndex_Connected).isBool()) {
                            mDetil_connected.setText("Device Connected");
                        } else {
                            mDetil_connected.setText("Device Not Connected");
                            /*boolean applosealert = minewDevice.getValue(ValueIndex.ValueIndex_AppLoseAlert).isBool();
                            if (applosealert) {
                                playSound(0);
                            }*/
                        }
                        mAdapter.setData(deviceData);
                    }
                });
            }

            /**
             *  if you send a instruction to device, this method will call back, then you can know if iphone sent instruction successfully,
             *
             *  @param index   index of instruction
             *  @param minewDevice  which device you sent instruction
             *  @param success if sent successfully.
             */
            @Override
            public void onSendData(MinewDevice minewDevice, InstrucIndex index, boolean success) {
                switch (index) {
                    case InstrucIndex_LoseAlertHigh:
                        if (success) {
                            Log.e("tag", "losealerthigh");
                        }
                        break;
                    case InstrucIndex_LoseAlertNone:
                        if (success) {
                            Log.e("tag", "losealertnone");
                        }
                        break;
                }
            }

            /**
             *  if the device send a instruction to iphone, this method will call back
             *
             *  @param index  index of instruction
             *  @param device which device sent this instruction
             */
            @Override
            public void onReceiveInstructionfromDevice(InstrucIndex index, MinewDevice device) {
                switch (index) {
                    case InstrucIndex_ButtonPushed:
                        Log.e("tag", "InstrucIndex_ButtonPushed");
                       /* if (isplay) {
                            soundPool.stop(streamID);
                            isplay = false;
                        } else {
                            playSound(0);
                            isplay = true;
                        }*/
                        break;
                }
            }
        });
        super.onResume();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detil_toolbar);
        setSupportActionBar(toolbar);

        mRecycle = (RecyclerView) findViewById(R.id.detil_recyeler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(mLayoutManager);
        mAdapter = new DetilListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager
                .HORIZONTAL));

        mDetil_connected = (TextView) findViewById(R.id.detil_connected);
    }

    private void initManager() {
        munobeacTagManager = unobeacTagManager.getInstance(this);
    }

    private void initData() {
        Intent intent = getIntent();
        String deviceAddress = intent.getStringExtra("deviceAddress");
        for (unobeacTag minewDevice : unobeacTagManager.bindTags) {
            String address = minewDevice.mMinewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue();
            if (deviceAddress.equals(address)) {
                munobeacTag = minewDevice;
            }
        }
        if (munobeacTag.mMinewDevice.getValue(ValueIndex.ValueIndex_Connected).isBool()) {
            mDetil_connected.setText("Device Connected");
        } else {
            mDetil_connected.setText("Device Not Connected");
        }

        List<String> deviceData = getDeviceData(munobeacTag.mMinewDevice);


        mAdapter.setData(deviceData);

    }

    private List<String> getDeviceData(MinewDevice minewDevice) {
        String[] nameArray;
        if (minewDevice.getValue(ValueIndex.ValueIndex_FeatureSupport).isBool()) {
            nameArray = new String[]{"Name:", "DeviceId:", "MacAddress:", "Rssi:", "Distance:", "Battery:", "Bind:",
                    "LoseTime:", "Coordinates:", "Device Lose Alert:",
                    "App Lose Alert:", "Find Device:", "Feature Support:", "Alarm Distance:", "Alarm Deley:"};
        } else {
            nameArray = new String[]{"Name:", "DeviceId:", "MacAddress:", "Rssi:", "Distance:", "Battery:", "Bind:",
                    "LoseTime:", "Coordinates:", "Device Lose Alert:",
                    "App Lose Alert:", "Find Device:", "Feature Support:"};
        }

        mAdapter.setNameArray(nameArray);


        List<String> listData = new ArrayList<>();
        /**
         *  get value of device
         *
         *  @param index index of which value you want to get.
         *
         *  @return a MinewDeviceValue Instance
         */
        String name = minewDevice.getValue(ValueIndex.ValueIndex_Name).getStringValue();
        String deviceId = minewDevice.getValue(ValueIndex.ValueIndex_DeviceId).getStringValue();
        String macAddress = minewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue();
        int Rssi = minewDevice.getValue(ValueIndex.ValueIndex_Rssi).getIntValue();
        float distance = minewDevice.getValue(ValueIndex.ValueIndex_Distance).getFloatValue();
        int battery = minewDevice.getValue(ValueIndex.ValueIndex_Battery).getIntValue();
        boolean isBind = minewDevice.getValue(ValueIndex.ValueIndex_Bind).isBool();
        String losetime = minewDevice.getValue(ValueIndex.ValueIndex_DisappearTime).getStringValue();
        String coordinates = minewDevice.getValue(ValueIndex.ValueIndex_DisappearLong).getFloatValue()
                + ":" + minewDevice.getValue(ValueIndex.ValueIndex_DisappearLati).getFloatValue();
        boolean devicelosealert = minewDevice.getValue(ValueIndex.ValueIndex_DeviceLoseAlert).isBool();
        boolean applosealert = minewDevice.getValue(ValueIndex.ValueIndex_AppLoseAlert).isBool();
        boolean isConnected = minewDevice.getValue(ValueIndex.ValueIndex_Connected).isBool();
        String finddevice = "touch me find";
        boolean isFeatureSupport = minewDevice.getValue(ValueIndex.ValueIndex_FeatureSupport).isBool();
        int alarmDistance = minewDevice.getValue(ValueIndex.ValueIndex_AlarmDistance).getIntValue();
        int alarmDeley = minewDevice.getValue(ValueIndex.ValueIndex_AlarmDelay).getIntValue();
        if (name == null || "".equals(name)) {
            name = "N/A";
        }
        listData.add(name);
        listData.add(deviceId);
        listData.add(macAddress);
        if (isConnected) {
            listData.add(Rssi + "");
            listData.add(distance + "M");
            listData.add(battery + "%");
        } else {
            listData.add("N/A");
            listData.add("N/A");
            listData.add("N/A");
        }
        if (isBind) {
            listData.add("Yes");
        } else {
            listData.add("No");
        }
        listData.add(losetime);
        listData.add(coordinates);
        if (devicelosealert) {
            listData.add("Yes");
        } else {
            listData.add("No");
        }
        if (applosealert) {
            listData.add("Yes");
        } else {
            listData.add("No");
        }
        listData.add(finddevice);
        if (isFeatureSupport) {
            listData.add("Yes");
        } else {
            listData.add("No");
        }
        listData.add(alarmDistance + "");
        listData.add(alarmDeley + "");
        return listData;
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new DetilListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 9) {
                    boolean deviceLosealert = munobeacTag.mMinewDevice.getValue(ValueIndex.ValueIndex_DeviceLoseAlert).isBool();
                    if (!deviceLosealert) {
                        /**
                         *  send a instruction to device, if complete it will call back
                         *  - (void)didSendInstruction:(InstrucIndex)index toDevice:(MinewDevice *)device result:(BOOL)success methoud
                         *
                         *  @param index index of the instruction
                         */
                        // device will alert when disconnect
                        munobeacTag.mMinewDevice.sendInstruction(InstrucIndex.InstrucIndex_LoseAlertHigh);
                        /**
                         *  set value of device
                         *
                         *  @param value  the MinewDeviceValue instance
                         */
                        // device won't alert when disconnect
                        munobeacTag.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_DeviceLoseAlert, true));
                    } else {
                        munobeacTag.mMinewDevice.sendInstruction(InstrucIndex.InstrucIndex_LoseAlertNone);
                        munobeacTag.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_DeviceLoseAlert, false));
                    }
                    List<String> deviceData = getDeviceData(munobeacTag.mMinewDevice);
                    mAdapter.setData(deviceData);
                }
                if (position == 10) {
                    boolean appLosealert = munobeacTag.mMinewDevice.getValue(ValueIndex.ValueIndex_AppLoseAlert).isBool();
                    if (!appLosealert) {
                        munobeacTag.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_AppLoseAlert, true));
                    } else {
                        munobeacTag.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_AppLoseAlert, false));
                    }
                    List<String> deviceData = getDeviceData(munobeacTag.mMinewDevice);
                    mAdapter.setData(deviceData);
                }
                if (position == 11) {
                    if (isSearch) {
                        isSearch = false;
                        // cancel searching.
                        munobeacTag.mMinewDevice.sendInstruction(InstrucIndex.InstrucIndex_CancelSearch);
                    } else {
                        isSearch = true;
                        // search for device
                        munobeacTag.mMinewDevice.sendInstruction(InstrucIndex.InstrucIndex_Search);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mAdapter.setOnProgressChangeListener(new DetilListAdapter.OnSeekBarchangeListener() {
            @Override
            public void onProgressChanged(View view, int position, int progress) {

            }

            @Override
            public void onStopTrackingTouch(View view, int position, int progress) {
                if (position == 13) {
                    Log.e("progress", progress + "--distance");
                    munobeacTag.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_AlarmDistance, progress));
                }
                if (position == 14) {
                    Log.e("progress", progress + "--deley");
                    munobeacTag.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_AlarmDelay, progress));
                }
                List<String> deviceData = getDeviceData(munobeacTag.mMinewDevice);
                mAdapter.setData(deviceData);
            }
        });
    }
}
