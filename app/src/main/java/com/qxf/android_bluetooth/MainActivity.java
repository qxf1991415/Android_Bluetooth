package com.qxf.android_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBlue;
    private BluetoothAdapter defaultAdapter;
    //必须大于0
    private final int REQUEST_ENABLE_BT = 1;
    private MyBtStatusChangedReceive myBtStatusChangedReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        myBtStatusChangedReceive = new MyBtStatusChangedReceive();
        this.registerReceiver(myBtStatusChangedReceive, intentFilter);
        initView();
    }

    private void initView() {
        mBtnBlue = (Button) findViewById(R.id.btn_blue);
        mBtnBlue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_blue:
                useBluetooth();
                break;
        }
    }

    private void useBluetooth() {
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return;
        }
        if (!defaultAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBtStatusChangedReceive);
    }
}
