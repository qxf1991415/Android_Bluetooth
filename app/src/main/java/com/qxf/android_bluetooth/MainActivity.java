package com.qxf.android_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBlue;
    private Button mBtnPair;
    private Button mBtnScan;
    private BluetoothAdapter defaultAdapter;
    private List<String> pairedDeviceNames = new ArrayList<>();
    private List<String> pairedDeviceMacs = new ArrayList<>();
    //必须大于0
    private final int REQUEST_ENABLE_BT = 1;
    private MyBtStatusChangedReceive myBtStatusChangedReceive;
    private RecyclerView mDevices;
    private Set<BluetoothDevice> bondedDevices;
    private BtAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        initBTReceive();
        initView();
    }



    private void initBTReceive() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        myBtStatusChangedReceive = new MyBtStatusChangedReceive();
        this.registerReceiver(myBtStatusChangedReceive, intentFilter);
    }

    private void initView() {
        mBtnBlue = findViewById(R.id.btn_blue);
        mBtnPair = findViewById(R.id.btn_pair);
        mBtnScan = findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(this);
        mBtnBlue.setOnClickListener(this);
        mBtnPair.setOnClickListener(this);

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
            case R.id.btn_blue:
                useBluetooth();
                break;
            case R.id.btn_pair:
                PairBluetooth();
                break;
            case R.id.btn_scan:
                Intent intent = new Intent(this, FindDeviceActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void PairBluetooth() {
        btAdapter.dataChanged();
        if (defaultAdapter.isEnabled()) {
            findBoundDevices();
        } else {
            useBluetooth();
        }
    }

    private void findBoundDevices() {
        showToast("正在扫描设备");
        bondedDevices = defaultAdapter.getBondedDevices();
        if (bondedDevices == null) {
            return;
        }
        pairedDeviceNames.clear();
        pairedDeviceMacs.clear();
        for (BluetoothDevice device : bondedDevices) {
            pairedDeviceNames.add(device.getName());
            pairedDeviceMacs.add(device.getAddress());
        }
        btAdapter.setData(pairedDeviceNames, pairedDeviceMacs);
        btAdapter.notifyDataSetChanged();
    }



    private void useBluetooth() {
        if (defaultAdapter == null) {
            return;
        }
        if (!defaultAdapter.isEnabled()) {
//            强制打开蓝牙  不推荐使用
//            defaultAdapter.enable();
//            弹窗提醒用户打开蓝牙
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            defaultAdapter.disable();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case RESULT_OK:
                    showToast("打开蓝牙成功");
                    break;
                case RESULT_CANCELED:
                    showToast("打开蓝牙失败");
                    break;
                default:
                    break;
            }
        }
    }

    class MyBtStatusChangedReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                String msg = "";
                switch (status) {
                    case BluetoothAdapter.STATE_ON:
                        msg = "蓝牙已打开";
                        showToast(msg);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        msg = "蓝牙已关闭";
                        showToast(msg);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        msg = "蓝牙正在打开";
                        showToast(msg);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        msg = "蓝牙正在关闭";
                        showToast(msg);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBtStatusChangedReceive);
    }
}
