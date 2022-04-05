package com.matm.matmsdk.Utils;

import com.matm.matmsdk.Interface.Bluetooth.IObservable;
import com.paxsz.easylink.device.DeviceInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhanzc on 2017/7/18.
 * 观察者类
 */
public class DataWatcher {

    private static DataWatcher dataWatcher;

    private DataWatcher(){
    }
    public static DataWatcher getInstance() {
        if (dataWatcher == null) {
            initWatcher();
        }
        return dataWatcher;
    }
    private static synchronized void initWatcher() {
        if (dataWatcher == null) {
            dataWatcher = new DataWatcher();
        }
    }
    private List<IObservable> observable = new ArrayList<IObservable>();

    public void register(IObservable observable) {
        this.observable.add(observable);
    }

    public void unregister(IObservable ob) {
        this.observable.remove(ob);
    }

    public void notifyObservable(DeviceInfo paramDeviceInfo) {
        if (this.observable != null) {
            for (IObservable observable : this.observable) {
                observable.update(paramDeviceInfo);
            }

        }
    }

    public void notifyFinish() {
        if (this.observable != null) {
            for (IObservable observable : this.observable) {
                observable.onSearchFinish();
            }

        }
    }
}