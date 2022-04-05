package com.matm.matmsdk.vriddhi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class AEMScrybeDevice {
private Context context;
private BluetoothAdapter localDevice;
private BluetoothDevice remoteDevice;
private BluetoothSocket bluetoothSocket, accBTSocket;
final String VERSION = "1.0";

Object mutex;

private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

private ArrayList<BluetoothDevice> remoteDeviceList = new ArrayList<BluetoothDevice>();

IAemScrybe scrybeDeviceInterface;

public AEMScrybeDevice(IAemScrybe impl)
{
    scrybeDeviceInterface = impl;
}

// getting all printers near
public  void startDiscover(final Context iContext)
{
    scanFlag = true;
    remoteDeviceList.clear();
    localDevice = null;

    context = iContext;

    // getting local device
    localDevice = BluetoothAdapter.getDefaultAdapter();

    if (localDevice == null)
        return;

    // check bluetooth status of local device
    try
    {
        if (!localDevice.isEnabled())
        {
            localDevice.enable();
        }
    }
    catch (Exception e)
    {
    }

    DiscoveryReciever discoverReceiver = new DiscoveryReciever();
    DeviceFoundReceiver deviceFoundReceiver = new DeviceFoundReceiver();
    iContext.registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    iContext.registerReceiver(discoverReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    BluetoothAdapter.getDefaultAdapter().startDiscovery();

}

private boolean deviceFound = false;
private boolean pairDevice = false;
private boolean scanFlag = true;

public  String pairPrinter(String printerName)
{

    for(int i = 0; i < remoteDeviceList.size(); i++)
    {
        String deviceName = remoteDeviceList.get(i).getName();
        if(printerName.contentEquals(deviceName))
        {
            deviceFound = true;
            pairDevice = pairDevice(remoteDeviceList.get(i));
            break;
        }
    }
    if (pairDevice==false)
        return "SHOW_PAIR_DEVICES";
    else
        return "NO_PAIR_DEVICES";
}

public String getSDKVersion()
{
    return VERSION;
}

// connecting the selected printer
public boolean connectToPrinter(String printerName) throws IOException
{
    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

    BluetoothDevice device = getPrinterByName(printerName);

    if (bluetoothSocket != null)
    {
        bluetoothSocket.close();
    }

    String deviceVersion = Build.VERSION.RELEASE;
    //if (deviceVersion.contentEquals("4.1.1") || deviceVersion.contentEquals("4.2"))
    //{
        bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_SECURE);
    //}
    if (bluetoothSocket == null)
    {
        try
        {
            Method m = device.getClass().getMethod("createInsecureRfcommSocket",new Class[] { int.class });
            bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
        }
        catch (Exception e)
        {

            return false;
        }
    }

    if (bluetoothSocket == null)
    {
        return false;
    }
    bluetoothSocket.connect();
    return true;
}

public boolean disConnectPrinter() throws IOException
{
    if (bluetoothSocket != null)
    {
        SystemClock.sleep(300);
        bluetoothSocket.getInputStream().close();
        bluetoothSocket.getOutputStream().close();
        bluetoothSocket.close();
        bluetoothSocket = null;
        return true;
    }
    return false;
}

private BluetoothDevice getPrinterByName(String printerName)
{
    Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

    for (BluetoothDevice device : pairedDevices)
    {
        if (device.getName() == null)
            continue;
        if (device.getName().contains(printerName))
        {
            remoteDevice = device;
            return device;
        }
        else
        {

        }
    }
    return null;
}
private boolean pairDevice(BluetoothDevice device)
{
    int bondState = device.getBondState();

    if (!(bondState == BluetoothDevice.BOND_BONDED))
    {
        try
        {
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            return true;
        }
        catch (Exception e)
        {
            e.getMessage();
            return false;
        }
    }
    return true;
}

private class DiscoveryReciever extends BroadcastReceiver
{
    public DiscoveryReciever() {

        // TODO Auto-generated constructor stub
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        {
            ArrayList<String> printerList = new ArrayList<String>();
            for (int i = 0; i < remoteDeviceList.size(); i++)
            {
                if (remoteDeviceList.get(i).getName().contains("rinter"))
                    printerList.add(remoteDeviceList.get(i).getName());
            }
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            context.unregisterReceiver(this);
            scrybeDeviceInterface.onDiscoveryComplete(printerList);
        }
    }
}

private class DeviceFoundReceiver extends BroadcastReceiver
{
    DeviceFoundReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        remoteDeviceList.add(device);
    }
}

public com.matm.matmsdk.vriddhi.CardReader getCardReader(com.matm.matmsdk.vriddhi.IAemCardScanner readerImpl)
{
    if (bluetoothSocket == null)
        return null;

    return new com.matm.matmsdk.vriddhi.CardReader(bluetoothSocket, readerImpl);
}

public com.matm.matmsdk.vriddhi.AEMPrinter getAemPrinter()
{
    if (bluetoothSocket == null)
        return null;

    return new com.matm.matmsdk.vriddhi.AEMPrinter(bluetoothSocket);
}

public ArrayList<String> getPairedPrinters()
{
    if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
    {
        BluetoothAdapter.getDefaultAdapter().enable();
        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    ArrayList<String> pairedPrinters = new ArrayList<String>();
    for (BluetoothDevice device : pairedDevices)
    {
        if (device.getName() == null)
            continue;
//        if ((device.getName().contains("rinter"))||(device.getName().contains("Teklogik"))||(device.getName().contains("MP57"))||(device.getName().contains("PP801"))||(device.getName().contains("CA411-UB"))||(device.getName().contains("MPT"))||(device.getName().contains("Rfid"))||(device.getName().contains("SURAJ50"))||(device.getName().contains("TM-P20"))||(device.getName().contains("IB"))||(device.getName().contains("BluPrints"))||(device.getName().contains("Reader"))||(device.getName().contains("V2B3"))||(device.getName().contains("P58")|| (device.getName().contains("BIS"))))
            pairedPrinters.add(device.getName());
             pairedPrinters.add(device.getBluetoothClass().toString());
    }
    return pairedPrinters;
}


}
