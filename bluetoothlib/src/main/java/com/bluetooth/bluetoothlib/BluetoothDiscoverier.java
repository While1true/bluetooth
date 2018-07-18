package com.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;
 class BluetoothDiscoverier {
    public static final String TAG = "BluetoothDiscoverier";
    private WeakReference<BluetoothListener>weakReference;
    public BluetoothDiscoverier(BluetoothListener bluetoothListener){
        weakReference=new WeakReference<>(bluetoothListener);
    }
    public void startDiscovery() {
        if (isSupportBluetooth()) {
              if(isEnableBluetooth()){
                  if(!discover()){
                      if(weakReference.get()!=null){
                          weakReference.get().onError("startDiscovery: can't start discovery");
                      }
                  }
              }else{
                  if(!enable()){
                      if(weakReference.get()!=null){
                          weakReference.get().onError("startDiscovery: Bluetooth can't enable");
                      }
                  }
              }
        } else {
            if(weakReference.get()!=null){
                weakReference.get().onError("startDiscovery: Bluetooth is not support");
            }
        }
    }
    public boolean stopDiscovery(){
        return stop();
    }
    private boolean stop(){
        return BluethoothAdapterHolder.bluetoothAdapter.cancelDiscovery();
    }
    private boolean discover(){
        return BluethoothAdapterHolder.bluetoothAdapter.startDiscovery();
    }
    private boolean enable(){
        return BluethoothAdapterHolder.bluetoothAdapter.enable();
    }

    private boolean isSupportBluetooth() {
        return BluethoothAdapterHolder.bluetoothAdapter != null;
    }
    private boolean isEnableBluetooth() {
        return BluethoothAdapterHolder.bluetoothAdapter.isEnabled();
    }

    private static class BluethoothAdapterHolder {
        private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
}
