package com.bluetooth.bluetoothlib;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Toast;

public class BluetoothFragment extends Fragment {
    private static final int REQUEST_CODE = 1001;
    private BroadcastReceiver broadcastReceiver;
    private BluetoothListener bluetoothListener;
    private BluetoothDiscoverier bluetoothDiscoverier;
    private BluetoothConnection connection;

    private boolean autoDiscovery=false;
    private boolean autoPair=true;

    private String PIN="0000";

    public void setBluetoothListener(BluetoothListener bluetoothListener) {
        this.bluetoothListener = bluetoothListener;
    }

    public void setAutoPair(boolean autoPair,String PIN) {
        this.autoPair = autoPair;
        this.PIN=PIN;
    }

    public void setAutoDiscovery(boolean autoDiscovery) {
        this.autoDiscovery = autoDiscovery;
    }

    private void checkPermissions() {
        requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(autoDiscovery) {
            startDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            String granted = "";
            String notGranted = "";
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted += permissions[i] + "\n";
                } else {
                    notGranted += permissions[i] + "\n";
                }
            }
            if (TextUtils.isEmpty(notGranted)) {
                startInternalDiscovery();
            } else {
                checkPermissions();
            }
        }
    }

    private void startInternalDiscovery() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(getActivity(), action, Toast.LENGTH_SHORT).show();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state =intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.STATE_OFF);
                   if(state==BluetoothAdapter.STATE_ON){
                        bluetoothDiscoverier.startDiscovery();
                   }
                }
                // 获得已经搜索到的蓝牙设备
                else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    if (bluetoothListener != null)
                        bluetoothListener.onDeviceFound(device);
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        startConnect(device);
                    } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        if (bluetoothListener != null)
                            bluetoothListener.onError("pair cancel");
                    }
                } else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                        abortBroadcast();
                        pairDevice(device,PIN);
                }
            }
        };
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        foundFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        foundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        if(autoPair) {
            foundFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        }
        getActivity().registerReceiver(broadcastReceiver, foundFilter);
        bluetoothDiscoverier = new BluetoothDiscoverier(bluetoothListener);
        bluetoothDiscoverier.startDiscovery();
    }

    private void pairDevice(final BluetoothDevice device,String PIN) {
        if (BondUtils.pairDevice(device, PIN)) {
            startConnect(device);
        }
    }

    private boolean startBond(BluetoothDevice device) {
        return BondUtils.startBond(device);
    }

    public static BluetoothFragment CreatInstance(FragmentManager manager, @Nullable Bundle savedInstanceState, BluetoothListener listener, String tag) {
        BluetoothFragment bluetoothFragment = null;
        if (savedInstanceState != null)
            bluetoothFragment = (BluetoothFragment) manager.getFragment(savedInstanceState, tag);
        if (bluetoothFragment == null) {
            bluetoothFragment = new BluetoothFragment();
        }
        bluetoothFragment.setBluetoothListener(listener);
        manager.beginTransaction().add(bluetoothFragment, tag).commit();
        return bluetoothFragment;
    }

    public void startDiscovery() {
        checkPermissions();
    }

    public void startConnect(BluetoothDevice device) {
        if(startBond(device)){
            startConnectInteral(device);
        }
    }
    private void startConnectInteral(BluetoothDevice device) {
        if (connection == null || !connection.isConnected())
            connection = new BluetoothConnection(device);
        if(connection.isConnected()){
            bluetoothListener.onConnectSuccess(connection);
        }else{
            bluetoothListener.onError("connect failed");
        }
    }
    public void stopDiscovery() {
        bluetoothDiscoverier.stopDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
        stopDiscovery();

    }
}
