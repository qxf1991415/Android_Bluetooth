package com.qxf.android_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FindDeviceActivity extends AppCompatActivity implements View.OnClickListener, BtAdapter.OnItemClickListener {

    private Button mBtnScan;
    private BluetoothAdapter defaultAdapter;
    private List<String> deviceNames = new ArrayList<>();
    private List<String> deviceMacs = new ArrayList<>();
    private BtAdapter btAdapter;
    private MyBtFoundReceive myBtFoundReceive;
    private RecyclerView mDevices;
    private Handler MyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(FindDeviceActivity.this, "停止扫描",Toast.LENGTH_SHORT).show();
                    defaultAdapter.cancelDiscovery();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_device);
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        initBTFoundReceive();
        initView();
    }

    private void initBTFoundReceive() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        myBtFoundReceive = new MyBtFoundReceive();
        this.registerReceiver(myBtFoundReceive, intentFilter);
    }

    private void initView() {
        mBtnScan = findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(this);

        mDevices = findViewById(R.id.device);
        btAdapter = new BtAdapter(this);
        mDevices.setAdapter(btAdapter);
        mDevices.setLayoutManager(new LinearLayoutManager(this));
        mDevices.setItemAnimator(new DefaultItemAnimator());
        mDevices.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        btAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                ScanBluetooth();
                break;
            default:
                break;
        }
    }

    private void ScanBluetooth() {
        btAdapter.dataChanged();
        if (defaultAdapter.isEnabled()) {
            findVisibleDevices();
        }
    }

    private void findVisibleDevices() {
        boolean discovery = defaultAdapter.startDiscovery();
        Message msg = MyHandler.obtainMessage();
        msg.what = 0;
        MyHandler.sendEmptyMessageDelayed(msg.what, 60000);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    class MyBtFoundReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!deviceMacs.contains(device.getAddress())) {
                    deviceNames.add(device.getName());
                    deviceMacs.add(device.getAddress());
                }
                btAdapter.setData(deviceNames, deviceMacs);
                btAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBtFoundReceive);
    }
}
