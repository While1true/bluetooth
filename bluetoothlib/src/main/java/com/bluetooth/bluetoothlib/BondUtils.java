package com.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import java.util.List;

public class BondUtils {
    public static boolean startBond(BluetoothDevice btDevice) {
        if (btDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                btDevice.createBond();
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean pairDevice(BluetoothDevice device,String pasword) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if(device.setPairingConfirmation(true)&&device.setPin(pasword.getBytes()))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
