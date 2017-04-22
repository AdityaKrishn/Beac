package com.unotrack.device.demo.interfaces;

import com.minew.device.MinewDevice;
import com.minew.device.MinewDeviceValue;
import com.minew.device.enums.InstrucIndex;

public interface unobeacTagListener {
    // 设备的某项数据已经更新
    void onUpdateValue(MinewDevice minewDevice, MinewDeviceValue value);

    // 指令发送是否成功
    void onSendData(MinewDevice minewDevice, InstrucIndex index, boolean success);

    // 从设备端接收到指令
    void onReceiveInstructionfromDevice(InstrucIndex index, MinewDevice device);
}
