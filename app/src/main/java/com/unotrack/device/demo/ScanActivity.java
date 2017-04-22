package com.unotrack.device.demo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minew.device.enums.ValueIndex;
import com.unotrack.device.demo.interfaces.unobeacTagManagerListener;
import com.unotrack.device.demo.unobeacTag.unobeacTag;
import com.unotrack.device.demo.unobeacTag.unobeacTagManager;
import com.minew.device.enums.BluetoothState;
import com.minew.device.enums.DeviceLinkStatus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.minew.device.MinewDeviceManager.bindDevices;

public class ScanActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private RecyclerView mRecycle;
    private ScanListAdapter mAdapter;
    private unobeacTagManager munobeacTagManager;
    private String userId;
    Handler mHandler = new Handler();
    UserRssi comp = new UserRssi();

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private String lat;
    private String longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        initView();
        initManager();
        initData();
        initListener();
        database = FirebaseDatabase.getInstance();
        database.getReference("app_name").setValue("Beac");
        myRef = database.getReference("DeviceDetails");


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }




    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.scan_toolbar);
        setSupportActionBar(toolbar);

        mRecycle = (RecyclerView) findViewById(R.id.scan_recyeler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new ScanListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager
                .HORIZONTAL));
    }

    private void initManager() {
        munobeacTagManager = unobeacTagManager.getInstance(this);
    }

    private void initData() {
        munobeacTagManager.startScan();
    }

    private void initListener() {
        munobeacTagManager.setunobeacTagManagerListener(new unobeacTagManagerListener() {
            @Override
            public void onDeviceChangeStatus(unobeacTag device, DeviceLinkStatus status) {

            }

            @Override
            public void onUpdateBluetoothState(BluetoothState state) {

            }

            @Override
            public void onRangeDevices(List<unobeacTag> devices) {
                Collections.sort(devices, comp);
                mAdapter.setData(devices);
            }

            @Override
            public void onAppearDevices(List<unobeacTag> devices) {

            }

            @Override
            public void onDisappearDevices(List<unobeacTag> devices) {

            }

            @Override
            public void onUpdateBindDevice(List<unobeacTag> devices) {

            }
        });

        mAdapter.setOnItemClickLitener(new ScanListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, final int position) {
                munobeacTagManager.stopScan();
                final String s=getUsername();


                Log.d("Hello",s);


                AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                builder.setTitle(R.string.bindDevice)
                        .setMessage(R.string.bindMessage)
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
                                /**
                                 *  you can get a device value instance
                                 *
                                 *  @param index index of this data
                                 *  @param value this param can be data/string/integer/float/bool
                                 *
                                 *  @return a instance of MinewDeviceValue with the giving index and value
                                 */

                                /*minewDevice.setValue(MinewDeviceValue.index(ValueIndex.ValueIndex_Bind, true));
                                MinewDeviceManager.bindDevices.add(minewDevice);*/
                                munobeacTagManager.bindDevice(minewDevice);
                                Log.d("Hello",minewDevice.mMinewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue());
                                userId = myRef.push().getKey();
                                deviceDetail user = new deviceDetail(s, minewDevice.mMinewDevice.getValue(ValueIndex.ValueIndex_MacAddress).getStringValue(),lat,longi);
                                myRef.child(userId).setValue(user);
                                Log.d("Hello","success");
                                //将以绑定设备序列化到本地
                                if (bindDevices != null) {
                                    Log.e("tag", "saveObject");
                                    // all bind devices
                                    saveObject(bindDevices);
                                }

                            }
                        });
                builder.show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_refresh:
                munobeacTagManager.stopScan();
                mAdapter.clear();
                munobeacTagManager.startScan();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type
            // values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)


            {Log.d("ADebugTag", "Value: " + parts[0]);


            return parts[0];}
            else
                return null;
        } else
            return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                lat=  Double.toString(mLastLocation.getLatitude());
                longi=Double.toString(mLastLocation.getLongitude());



            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
