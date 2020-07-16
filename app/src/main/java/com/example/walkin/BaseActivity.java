package com.example.walkin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.example.walkin.utils.Util;

public abstract class BaseActivity extends Activity {

    protected AidlDeviceManager manager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();
        Util.Companion.setActivityContext(this);
    }

    protected void bindService() {
        Intent intent = new Intent();
        intent.setPackage("com.centerm.smartposservice");
        intent.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        Intent intent1 = new Intent();
        intent1.setPackage("com.centerm.smartposservice");
        intent1.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        bindService(intent1, conn1, Context.BIND_AUTO_CREATE);

        intent = new Intent();
        intent.setPackage("com.centerm.smartposservice");
        intent.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        bindService(intent, conn2, Context.BIND_AUTO_CREATE);
    }

//    protected void bindServiceSwipe() {
//        Intent intent1 = new Intent();
//        intent1.setPackage("com.centerm.smartposservice");
//        intent1.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
//        bindService(intent1, conn1, Context.BIND_AUTO_CREATE);
//    }

    public ServiceConnection conn1 = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);
            if (null != manager) {
                onDeviceConnectedSwipe(manager);
            }
        }
    };


    protected ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);
            if (null != manager) {
                onDeviceConnected(manager);
            }
        }
    };

    public ServiceConnection conn2 = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);
            com.centerm.centermposoversealib.util.LogUtil.print("success1");
            com.centerm.centermposoversealib.util.LogUtil.print("manager1 = " + manager);
            if (null != manager) {
                onPrintDeviceConnected(manager);
            }
        }
    };

    protected void log(String log) {
        Log.i("Centerm", log);
    }
    protected abstract void onPrintDeviceConnected(AidlDeviceManager manager);
    public abstract void onDeviceConnected(AidlDeviceManager deviceManager);
    public abstract void onDeviceConnectedSwipe(AidlDeviceManager manager);

    protected abstract void showMessage(String str, int black);
}
