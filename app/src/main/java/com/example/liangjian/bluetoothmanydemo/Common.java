package com.example.liangjian.bluetoothmanydemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.List;

/**
 * Created by LiangJian on 2017/4/25.
 */

public class Common {
     static  List<BluetoothDevice>blueList;
    static String action = "com.action.data";
     static BluetoothGattCharacteristic  CharacteristicSend ;
     static BluetoothGattCharacteristic  CharacteristicNotify ;
}
