package com.bluetooth.bluetoothlib;/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

 public class BluetoothConnection {

    /**
     * This is the well-known UUID for the Bluetooth SPP (Serial Port Profile)
     */
    private static final UUID SERIAL_PORT_PROFILE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothDevice mDevice;
    private BluetoothSocket mSocket = null;

    public static final String TAG = BluetoothConnection.class.getSimpleName();

    public BluetoothConnection(String bluetoothAddress) {
        this(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddress));
    }

    public BluetoothConnection(BluetoothDevice device) {
        mDevice = device;
        connect();
    }

    public String getAddress() {
        return mDevice.getAddress();
    }

    /**
     * Establish an RFCOMM connection to the remote device.
     *
     * Assumes there is no existing connection.
     *
     * This method may take time to return (or even not return in pathological cases).
     * It is a good idea to wrap it in some kind of Promise-like object.
     *
     * @return true if it could connect, false otherwise
     */
    private boolean connect() {
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_PROFILE);
            mSocket.connect();
        } catch (IOException e) {
            Log.w(TAG, "BluetoothConnection couldn't be established due to an exception: " + e);
            mSocket = null;
            return false;
        }
        return mSocket.isConnected();
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    private void close() {
        if (isConnected()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                // we are letting go of the connection anyway, so log and continue
                Log.w(TAG, "IOException during BluetoothSocket close(): " + e);
            } finally {
                mSocket = null;
            }
        }
    }

    public boolean reconnect() {
        close();
        return connect();
    }

    public InputStream getInputStream() {
        if (isConnected()) {
            try {
                return mSocket.getInputStream();
            } catch (IOException e) {
                Log.w(TAG, "failed to get Bluetooth input stream: " + e);
            }
        }
        return null;
    }

    public OutputStream getOutputStream() {
        if (isConnected()) {
            try {
                return mSocket.getOutputStream();
            } catch (IOException e) {
                Log.w(TAG, "failed to get Bluetooth output stream: " + e);
            }
        }
        return null;
    }
}
