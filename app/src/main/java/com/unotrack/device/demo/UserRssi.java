package com.unotrack.device.demo;


import com.unotrack.device.demo.unobeacTag.unobeacTag;
import com.minew.device.enums.ValueIndex;

import java.util.Comparator;

public class UserRssi implements Comparator<unobeacTag> {

    @Override
    public int compare(unobeacTag minewDevice, unobeacTag t1) {
        float floatValue1 = minewDevice.mMinewDevice.getValue(ValueIndex.ValueIndex_Rssi).getIntValue();
        float floatValue2 = t1.mMinewDevice.getValue(ValueIndex.ValueIndex_Rssi).getIntValue();
        if (floatValue1 < floatValue2) {
            return 1;
        } else if (floatValue1 == floatValue2) {
            return 0;
        } else {
            return -1;
        }
    }
}
