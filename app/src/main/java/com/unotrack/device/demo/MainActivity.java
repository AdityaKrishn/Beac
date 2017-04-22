package com.unotrack.device.demo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minew.device.MinewDevice;
import com.minew.device.MinewDeviceValue;
import com.unotrack.device.demo.interfaces.unobeacTagManagerListener;
import com.unotrack.device.demo.unobeacTag.unobeacTag;
import com.unotrack.device.demo.unobeacTag.unobeacTagManager;
import com.unotrack.device.demo.service.ManagerService;
import com.minew.device.enums.BluetoothState;
import com.minew.device.enums.DeviceLinkStatus;
import com.minew.device.enums.ValueIndex;
import com.minew.device.service.ConnectService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.minew.device.MinewDeviceManager.bindDevices;
import static com.unotrack.device.demo.unobeacTag.unobeacTagManager.bindTags;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycle;
    private DeviceListAdapter mAdapter;
    private unobeacTagManager munobeacTagManager;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initView();
        initManager();
        checkBluetooth();
        initData();
        initListener();
        Intent intent = new Intent(MainActivity.this, ManagerService.class);
        startService(intent);
    }



    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecycle = (RecyclerView) findViewById(R.id.recyeler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new DeviceListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager
                .HORIZONTAL));
    }

    private void initManager() {
        /**
         *  get instance
         *
         *  @return the get instance of device manager
         */
        munobeacTagManager = unobeacTagManager.getInstance(this);
    }

    /**
     * check Bluetooth state
     */
    private void checkBluetooth() {
        BluetoothState bluetoothState = munobeacTagManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }

    private void initData() {
        munobeacTagManager.startService();

        if (unobeacTagManager.bindTags == null) {
            List<unobeacTag> bindTags = getObject();
            if (bindTags != null) {
                List<MinewDevice> devices = new ArrayList<>();
                for (unobeacTag unobeacTag : bindTags) {
                    devices.add(unobeacTag.mMinewDevice);
                }
                bindDevices = devices;
            }
        }

    }

    private void initListener() {
        mAdapter.setOnItemClickLitener(new DeviceListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                unobeacTag minewDevice = mAdapter.getData(position);
                String macAddress = minewDevice.mMinewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue();
                Intent intent = new Intent(MainActivity.this, DetilActivity.class);
                intent.putExtra("deviceAddress", macAddress);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.deletebindDevice)
                        .setMessage(R.string.deletebindMessage)
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                unobeacTag minewDevice = mAdapter.getData(position);
                                munobeacTagManager.unbindDevice(minewDevice);
                                //将以绑定设备序列化到本地
                                if (bindDevices != null) {
                                    Log.e("tag", "saveObject");
                                    // all bind devices
                                    saveObject(bindDevices);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                builder.show();

            }
        });
    }

    private <T> void saveObject(List<T> bindDevices) {
        SharedPreferences mSharedPreferences = getSharedPreferences("devicesdk", Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create();
        String bindString = gson.toJson(bindDevices);
        Log.e("tagsave", bindString);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("bindlist", bindString);
        editor.commit();
    }


    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        munobeacTagManager.stopService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_add:
                Intent intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        /**
         *  start scan for devices , if you bind some devices, the manager will try to connect automatically.
         */
        munobeacTagManager.startScan();
        if (bindTags != null) {
            Log.e("tag", bindTags.size() + "");
            Set<String> allAddress = ConnectService.mMinewDevicesMap.keySet();
            for (String address : allAddress) {
                MinewDevice minewDevice = ConnectService.mMinewDevicesMap.get(address);
                for (unobeacTag minewDeviceBind : bindTags) {
                    if (minewDeviceBind.mMinewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue().equals(minewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue())) {
                        minewDeviceBind.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_Connected, true));
                        minewDeviceBind.mMinewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_Distance, minewDevice.getValue(ValueIndex.ValueIndex_Distance).getFloatValue()));
                    }
                }
            }
            mAdapter.setData(bindTags);
        }

        munobeacTagManager.setunobeacTagManagerListener(new unobeacTagManagerListener() {
            /**
             *  if device link state changed , this method will call back
             *
             *  @param device  which device changed
             *  @param status   change to which state
             */
            @Override
            public void onDeviceChangeStatus(unobeacTag device, DeviceLinkStatus status) {
                switch (status) {
                    case DeviceLinkStatus_ConnectFailed:
                        munobeacTagManager.startScan();
                        break;
                    case DeviceLinkStatus_Disconnect:
                        munobeacTagManager.startScan();
                        break;
                }
            }

            /**
             *  the state change of bluetoothstate
             *
             *  @param state   current bluetooth state
             */
            @Override
            public void onUpdateBluetoothState(BluetoothState state) {

            }

            /**
             *  you can get inRange found devices by using this delegate method
             *
             *  @param devices all found devices
             */
            @Override
            public void onRangeDevices(List<unobeacTag> devices) {

            }

            /**
             *  if manager found a new device this method will call back
             *
             *  @param devices all new devices
             */
            @Override
            public void onAppearDevices(List<unobeacTag> devices) {

            }

            /**
             *  if a device can't be scanned again, the manager think it is disappeared.
             *
             *  @param devices all disappeared devices
             */
            @Override
            public void onDisappearDevices(List<unobeacTag> devices) {

            }

            /**
             *  if you bind some devices this method will call back Periodically, you can get newest state of all bind devices
             *
             *  @param devices all bind devices
             */
            @Override
            public void onUpdateBindDevice(final List<unobeacTag> devices) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(devices);
                    }
                });
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private List<unobeacTag> getObject() {
        SharedPreferences mSharedPreferences = getSharedPreferences("devicesdk", MODE_PRIVATE);
        String bindlist = mSharedPreferences.getString("bindlist", "");
        Log.e("tag", "bindlist" + bindlist);
        if ("".equals(bindlist)) {
            return null;
        }
        List<unobeacTag> list = munobeacTagManager.getBindDevicesInnative(bindlist);
        return list;
    }
}
