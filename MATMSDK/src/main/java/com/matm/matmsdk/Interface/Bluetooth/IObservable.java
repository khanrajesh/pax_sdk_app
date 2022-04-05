package com.matm.matmsdk.Interface.Bluetooth;

import com.paxsz.easylink.device.DeviceInfo;

public interface IObservable {

    void update(DeviceInfo paramDeviceInfo);

    void onSearchFinish();
}
