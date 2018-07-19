package com.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import java.util.List;

public class BondUtils {
    public static boolean startBond(BluetoothDevice btDevice) {
        if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            return true;

        }
        if (btDevice.getBondState() == BluetoothDevice.BOND_NONE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            btDevice.createBond();
        }
        return false;
    }

    public static boolean pairDevice(BluetoothDevice device, String pasword) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                device.setPairingConfirmation(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (device.setPin(pasword.getBytes()))
                return true;
        }

        return false;
    }
}
