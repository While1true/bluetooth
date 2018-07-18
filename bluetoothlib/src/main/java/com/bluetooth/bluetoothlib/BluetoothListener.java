package com.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothDevice;

public interface BluetoothListener {
    void onError(String error);
    void onDeviceFound(BluetoothDevice device);
    void onConnectSuccess(BluetoothConnection connection);
}
