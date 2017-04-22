package com.unotrack.device.demo.interfaces;

import com.unotrack.device.demo.unobeacTag.unobeacTag;
import com.minew.device.enums.BluetoothState;
import com.minew.device.enums.DeviceLinkStatus;

import java.util.List;

public interface unobeacTagManagerListener {
    // 设备连接状态更新
    void onDeviceChangeStatus(unobeacTag device, DeviceLinkStatus status);

    // 手机蓝牙状态更新
    void onUpdateBluetoothState(BluetoothState state);

    // 扫描到的所有设备
    void onRangeDevices(List<unobeacTag> devices);

    // 刚出现的新设备
    void onAppearDevices(List<unobeacTag> devices);

    // 刚消失的设备
    void onDisappearDevices(List<unobeacTag> devices);

    // 设备数据更新
    void onUpdateBindDevice(List<unobeacTag> devices);
}
