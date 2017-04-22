package com.unotrack.device.demo.unobeacTag;

import android.content.Context;

import com.minew.device.MinewDevice;
import com.minew.device.MinewDeviceManager;
import com.minew.device.MinewDeviceManagerListener;
import com.unotrack.device.demo.interfaces.unobeacTagManagerListener;
import com.unotrack.device.demo.tools.NotifycationTools;
import com.minew.device.enums.BluetoothState;
import com.minew.device.enums.DeviceLinkStatus;
import com.minew.device.enums.ValueIndex;

import java.util.ArrayList;
import java.util.List;


public class unobeacTagManager {

    private static unobeacTagManager single;
    private static Context mContext;
    private unobeacTagManagerListener listener;

    // 绑定的设备
    public static List<unobeacTag> bindTags;

    public static unobeacTagManager getInstance(Context context) {
        if (single == null) {
            synchronized ((unobeacTagManager.class)) {
                if (single == null) {
                    single = new unobeacTagManager();
                    mContext = context;
                    bindTags = new ArrayList<>();
                }
            }
        }
        return single;
    }

    public void setunobeacTagManagerListener(unobeacTagManagerListener unobeacTagManagerListener) {
        listener = unobeacTagManagerListener;
    }

    private MinewDeviceManagerListener minewDeviceManagerListener = new MinewDeviceManagerListener() {
        @Override
        public void onDeviceChangeStatus(MinewDevice device, DeviceLinkStatus status) {
            switch (status) {
                case DeviceLinkStatus_Connected:
                    NotifycationTools.createNotifcation(device.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue() + " Connected", mContext);
                    break;
                case DeviceLinkStatus_Disconnect:
                    NotifycationTools.createNotifcation(device.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue() + " Disconnect", mContext);
                    break;
                case DeviceLinkStatus_ConnectFailed:
                    NotifycationTools.createNotifcation(device.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue() + " Disconnect", mContext);
                    break;
            }
            if (listener != null) {
                String macAddress = device.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue();
                for (unobeacTag unobeacTag : bindTags) {
                    if (unobeacTag.mMinewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue().equals(macAddress)) {
                        listener.onDeviceChangeStatus(unobeacTag, status);
                    }
                }
            }
        }

        @Override
        public void onUpdateBluetoothState(BluetoothState state) {
            switch (state) {
                case BluetoothStatePowerOn:
                    NotifycationTools.createNotifcation("Active", mContext);
                    break;
                case BluetoothStatePowerOff:
                    NotifycationTools.createNotifcation("Inactive", mContext);
                    break;
            }
            if (listener != null) {
                listener.onUpdateBluetoothState(state);
            }
        }

        @Override
        public void onRangeDevices(List<MinewDevice> devices) {
            if (listener != null) {
                List<unobeacTag> unobeacTags = new ArrayList<>();
                for (MinewDevice device : devices) {
                    unobeacTag unobeacTag = new unobeacTag(device, mContext);
                    unobeacTags.add(unobeacTag);
                }
                listener.onRangeDevices(unobeacTags);
            }
        }

        @Override
        public void onAppearDevices(List<MinewDevice> devices) {
            if (listener != null) {
                List<unobeacTag> unobeacTags = new ArrayList<>();
                for (MinewDevice device : devices) {
                    unobeacTag unobeacTag = new unobeacTag(device, mContext);
                    unobeacTags.add(unobeacTag);
                }
                listener.onAppearDevices(unobeacTags);
            }
        }

        @Override
        public void onDisappearDevices(List<MinewDevice> devices) {
            if (listener != null) {
                List<unobeacTag> unobeacTags = new ArrayList<>();
                for (MinewDevice device : devices) {
                    unobeacTag unobeacTag = new unobeacTag(device, mContext);
                    unobeacTags.add(unobeacTag);
                }
                listener.onDisappearDevices(unobeacTags);
            }
        }

        @Override
        public void onUpdateBindDevice(List<MinewDevice> devices) {
            if (listener != null) {
                listener.onUpdateBindDevice(bindTags);
            }
        }
    };

    public void startScan() {
        MinewDeviceManager.getInstance(mContext).startScan();
        MinewDeviceManager.getInstance(mContext).setMinewDeviceManagerListener(minewDeviceManagerListener);
    }

    public void stopScan() {
        MinewDeviceManager.getInstance(mContext).stopScan();
    }

    /**
     * 启动连接服务
     */
    public void startService() {
        MinewDeviceManager.getInstance(mContext).startService();
    }

    /**
     * 停止连接服务
     */
    public void stopService() {
        MinewDeviceManager.getInstance(mContext).stopService();
    }

    /**
     * 主动连接至某个设备，连接结果使用协议方法回调
     *
     * @param device
     */
    public void connect(unobeacTag device) {
        MinewDeviceManager.getInstance(mContext).connect(device.mMinewDevice);
    }

    /**
     * 主动断开某个设备，断开结果使用协议方法回调
     *
     * @param device
     */
    public void disconnect(unobeacTag device) {
        MinewDeviceManager.getInstance(mContext).disconnect(device.mMinewDevice);
    }

    /**
     * 添加绑定设备
     *
     * @param device
     */
    public void bindDevice(unobeacTag device) {
        MinewDeviceManager.getInstance(mContext).bindDevice(device.mMinewDevice);
        bindTags.add(device);
    }

    /**
     * 移除绑定设备
     *
     * @param device
     */
    public void unbindDevice(unobeacTag device) {
        MinewDeviceManager.getInstance(mContext).unbindDevice(device.mMinewDevice);
        bindTags.remove(device);
    }

    /**
     * 移除全部绑定设备，断开所有连接
     */
    public void reset() {
        MinewDeviceManager.getInstance(mContext).reset();
        for (unobeacTag minewDevice : bindTags) {
            disconnect(minewDevice);
        }
        bindTags.clear();
    }

    /**
     * 获取蓝牙状态
     *
     * @return
     */
    public BluetoothState checkBluetoothState() {
        BluetoothState bluetoothState = MinewDeviceManager.getInstance(mContext).checkBluetoothState();
        return bluetoothState;
    }

    /**
     * 获取已序列化到本地的已绑定的数组
     *
     * @param jsonString
     * @return
     */
    public List<unobeacTag> getBindDevicesInnative(String jsonString) {
        List<MinewDevice> bindDevicesInnative = MinewDeviceManager.getInstance(mContext).getBindDevicesInnative(jsonString);
        List<unobeacTag> bindNatives = new ArrayList<>();
        for (MinewDevice minewDevice : bindDevicesInnative) {
            unobeacTag unobeacTag = new unobeacTag(minewDevice, mContext);
            bindNatives.add(unobeacTag);
        }
        bindTags = bindNatives;
        return bindNatives;
    }
}
