package com.bluetooth.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bluetooth.bluetoothlib.BluetoothConnection;
import com.bluetooth.bluetoothlib.BluetoothFragment;
import com.bluetooth.bluetoothlib.BluetoothListener;
import com.nestrefreshlib.Adpater.Base.Holder;
import com.nestrefreshlib.Adpater.Impliment.BaseHolder;
import com.nestrefreshlib.Adpater.Impliment.SAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BluetoothListener {

    List<BluetoothDevice> devices = new ArrayList<>();
    private SAdapter sAdapter;
    BluetoothFragment bluetoothFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);
        bluetoothFragment=BluetoothFragment.CreatInstance(getSupportFragmentManager(),savedInstanceState,this,"xx");
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sAdapter = new SAdapter(devices).addType(R.layout.activity_main, new BaseHolder<BluetoothDevice>() {
            @Override
            public void onViewBind(Holder holder, final BluetoothDevice item, int position) {
                holder.setText(R.id.text, item.toString()+item.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bluetoothFragment.startBond(item)){
                            bluetoothFragment.startConnect(item);
                        }
                    }
                });
            }
        });
        recyclerView.setAdapter(sAdapter);
    }

    @Override
    public void onError(String error) {
        Log.d("-------------", "onError: "+error);
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        // 关于蓝牙设备分类参考 http://stackoverflow.com/q/23273355/4242112
//        if (device.getBluetoothClass().getMajorDeviceClass() == 1644)
        if(!devices.contains(device)) {
            devices.add(device);
            sAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectSuccess(BluetoothConnection connection) {
        try {
            connection.getOutputStream().write("nihao".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
