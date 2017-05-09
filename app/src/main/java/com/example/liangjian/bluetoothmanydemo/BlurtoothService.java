package com.example.liangjian.bluetoothmanydemo;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by LiangJian on 2017/4/25.
 */

public class BlurtoothService extends Service {
    private BluetoothGattCallback gattCallback;
    private BluetoothGatt mBlueToothGatt;
    private String action = "com.sendMessage.device.position";
    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    private Handler handler = new Handler();
    private BroadcastReceiver posiRece = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("devicePosition",0);
            mBlueToothGatt =  Common.blueList.get(position).connectGatt(BlurtoothService.this,false,gattCallback);
            mBlueToothGatt.connect();
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        registerBroad();

        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
//                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                final int connecStatus = newState;
//                final BluetoothGatt gattsub = gatt;
                String intentAction;
                if(newState == BluetoothProfile.STATE_CONNECTED){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    /*发现(寻找)服务*/
                    Runnable ru = new Runnable() {
                        @Override
                        public void run() {
                            mBlueToothGatt.discoverServices();
                        }
                    };
                    handler.post(ru);

//                    BluetoothGattCharacteristic bluetoothGattCharacteristic = getBluetoothGattCharacteristic(mBlueToothGatt.getServices());
                    Log.e("lala","成功");
//                            BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattServices.get(0).getCharacteristic(UUID_MOTOR1_CONTROL);
//                            byte[]value = new byte[]{0x01};
//                            bluetoothGattCharacteristic.setValue(value);
//                            gattsub.writeCharacteristic(bluetoothGattCharacteristic);

                }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                }

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.e("lala","服务找到了");
                    /*获取Characteristic*/
                    getBluetoothGattCharacteristic(mBlueToothGatt.getServices());
                    Runnable runable = new Runnable() {
                        @Override
                        public void run() {
                            NotifyData(mBlueToothGatt);
                            try {
                                Thread.sleep(2000);
                                sendOrder(mBlueToothGatt);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    handler.post(runable);
//                    try {
//                        Thread.sleep(200);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

//                    readNotify(mBlueToothGatt);
                } else {
                }
            }

            /*读取数据回调*/
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                super.onCharacteristicRead(gatt, characteristic, status);
                Log.e("lala","read回调了");
            }

            /*当向设备Descriptor中写数据时，会回调该函数*/
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                super.onCharacteristicWrite(gatt, characteristic, status);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    /*读取数据*/
                    Log.e("lala","write回调了");
                }

            }

            /*设备发出通知会调用该接口*/
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//                super.onCharacteristicChanged(gatt, characteristic);
//                Log.e("lala","notify回调了");
                byte[] recData = characteristic.getValue();
                sendBroadCastData(recData);
            }



            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                Log.e("lala","notify回调了onDescriptorRead");
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.e("lala","notify回调了onDescriptorWrite");
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
                Log.e("lala","notify回调了onReliableWriteCompleted");
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
            }

        };
    }

    private void sendBroadCastData(byte[] recData) {
        Intent intent = new Intent(Common.action);
        intent.putExtra("data",recData);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public  void getBluetoothGattCharacteristic(List<BluetoothGattService> bluetoothGattServiceList){
        if(bluetoothGattServiceList != null){
            Log.e("lala",bluetoothGattServiceList.size()+"--服务数量");
            for(BluetoothGattService bluetoothGattService:bluetoothGattServiceList ){
                Log.e("lala",bluetoothGattService.getUuid().toString()+"--UUID");
                /**
                *@data 2017/4/27
                 * 惊帆:service:6e400001-b5a3-f393-e0a9-e50e24dcca9e
                 *读取read:6e400003-b5a3-f393-e0a9-e50e24dcca9e
                 * 写入:write:6e400002-b5a3-f393-e0a9-e50e24dcca9e
                **/
                if(bluetoothGattService.getUuid().toString().equals("6e400001-b5a3-f393-e0a9-e50e24dcca9e")){
                    List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList = bluetoothGattService.getCharacteristics();
                    for(BluetoothGattCharacteristic bluetoothGattCharacteristic:bluetoothGattCharacteristicList){
                        if("6e400002-b5a3-f393-e0a9-e50e24dcca9e".equals(bluetoothGattCharacteristic.getUuid().toString())){
                            int flag = bluetoothGattCharacteristic.getProperties();
                                Common.CharacteristicSend = bluetoothGattCharacteristic;
                        }else if("6e400003-b5a3-f393-e0a9-e50e24dcca9e".equals(bluetoothGattCharacteristic.getUuid().toString())){
                            int flag = bluetoothGattCharacteristic.getProperties();
                             Log.e("perssion",flag+"");
                            Common.CharacteristicNotify= bluetoothGattCharacteristic;
                        }
                        Log.e("uuid-hahaha",bluetoothGattCharacteristic.getUuid().toString());
                    }
                }
            }
        }else{
        }
    }

    public  void  registerBroad(){
        IntentFilter filter = new IntentFilter(action);
        registerReceiver(posiRece,filter);
    }

    public  void sendOrder(BluetoothGatt gatt_gatt){
        if(gatt_gatt != null && Common.CharacteristicSend!= null){
            byte[] order_begin = new byte[]{0x01};
//            byte[] value = {0x01,0x06,0x00,0x00, 0x00, 0x00, 0x00,0x00,(byte) 0xff};
            Common.CharacteristicSend.setValue(order_begin);
            /*避免重复发包*/
//            Common.CharacteristicSend.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mBlueToothGatt.writeCharacteristic(Common.CharacteristicSend);
            Log.e("lala","fasongle");
        }else{
            Log.e("BluService","你没有对象");
        }
    }

//    public  void ReadData(BluetoothGatt gatt_gatt){
//        if(gatt_gatt != null && Common.CharacteristicNotify!= null){
//            gatt_gatt.readCharacteristic(Common.CharacteristicNotify);
//            Log.e("lala","读取了回调");
//        }else{
//            Log.e("BluService","你没有对象");
//        }
//    }

    public void readNotify(BluetoothGatt gatt_gatt){
        if(gatt_gatt != null && Common.CharacteristicNotify!= null){
            gatt_gatt.readCharacteristic(Common.CharacteristicNotify);
//            gatt_gatt.setCharacteristicNotification(Common.CharacteristicNotify,true);
        }
    }
    public  void NotifyData(BluetoothGatt gatt_gatt){
        if(gatt_gatt != null && Common.CharacteristicNotify!= null){

            boolean flag = mBlueToothGatt.setCharacteristicNotification(Common.CharacteristicNotify,true);

            Log.e("lala",flag+"--开启notify");
//            if("00002a37-0000-1000-8000-00805f9b34fb".equals(Common.CharacteristicNotify.getUuid().toString())){
                BluetoothGattDescriptor descriptor = Common.CharacteristicNotify.getDescriptor(
                        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt_gatt.writeDescriptor(descriptor);
//            }
//           Log.e("lala",Common.CharacteristicNotify.getUuid()+"");
//          Log.e("lala",gatt_gatt.setCharacteristicNotification(Common.CharacteristicNotify,true)+"");
//            for(int x = 0; x < Common.CharacteristicNotify.getDescriptors().size(); x++){
//                BluetoothGattDescriptor descriptor = Common.CharacteristicNotify.getDescriptors().get(x);
//                Log.e("lala",descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)+"");
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt_gatt.writeDescriptor(descriptor);
//                Log.e("lala",gatt_gatt.writeDescriptor(descriptor)+"des");
//                gatt_gatt.setCharacteristicNotification(Common.CharacteristicNotify,true);
//                Log.e("lala",gatt_gatt.setCharacteristicNotification(Common.CharacteristicNotify,true)+"gat_Noti");
//                BluetoothGattDescriptor descriptor = Common.CharacteristicNotify.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
//            }


//            byte [] value = new byte[]{0x01};
//            descriptor.setValue(value);

//            if(Common.CharacteristicNotify.getUuid().toString().equals("6e400003-b5a3-f393-e0a9-e50e24dcca9e")){
//
//            }
        }else{
            Log.e("BluService","你没有对象");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(posiRece);
        mBlueToothGatt.disconnect();
        mBlueToothGatt.close();
    }
}
