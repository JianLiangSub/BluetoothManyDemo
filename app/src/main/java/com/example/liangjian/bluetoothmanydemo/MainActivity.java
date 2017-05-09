package com.example.liangjian.bluetoothmanydemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Handler handler;
    private ListView list;
    private BluetoothList adapter;
    private ScanCallback leScanCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBlueToothManager ;
    private BluetoothLeScanner scanner;
    private TextView text;
    private String action = "com.sendMessage.device.position";
    private StringBuilder builder ;
    private BroadcastReceiver posiRece = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] value = intent.getByteArrayExtra("data");
            builder = new StringBuilder(value.length);
            for (int x = 0; x < value.length; x++){
                builder.append(value[x]);

            }
            text.setText(builder+"");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }
    private void init() {
        if(Build.VERSION.SDK_INT >= 23){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
            }else{
                initaBlue();
            }
        }else{
            initaBlue();
        }
        openService();
    }

    public void initaBlue(){
        list = (ListView) findViewById(R.id.list);
//        text = findViewById()
        text = (TextView) findViewById(R.id.text);
        Common.blueList = new ArrayList<>();
        adapter = new BluetoothList();
        handler = new Handler();
        IntentFilter filter = new IntentFilter(Common.action);
        registerReceiver(posiRece,filter);
//        inter();
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "该设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
         mBlueToothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
         mBluetoothAdapter   = mBlueToothManager.getAdapter();
         /*开启蓝牙*/
        if(mBluetoothAdapter != null || !mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.enable();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
        list.setAdapter(adapter);
        initData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initaBlue();
            }else{
                Toast.makeText(this, "没有获取定位权限", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(this, "没有获取定位权限", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void initData() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                broadCastSend(position);
            }
        });
    }

    private void startScanner() {
        leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if(Common.blueList.contains(result.getDevice())){

                }else{
                    Common.blueList.add(result.getDevice());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }

        };

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mBluetoothAdapter.stopLeScan(leScanCallback);
                if(scanner!= null && leScanCallback != null){
                    scanner.stopScan(leScanCallback);
                }
            }
        },10000);
        scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if(scanner!= null && leScanCallback != null){
            scanner.startScan(leScanCallback);
        }else{
            Toast.makeText(this, "是Null", Toast.LENGTH_SHORT).show();
        }
    }

    public  void openService(){
        Intent intent = new Intent(MainActivity.this,BlurtoothService.class);
        startService(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.disable();
        unregisterReceiver(posiRece);
    }
    void inter(){
//                leScanCallback = new BluetoothAdapter.LeScanCallback() {
//                    @Override
//                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                        if(device!=null){
//                            Log.e("BootoothDevices",device.getAddress()+"____"+device.getName());
//                    Toast.makeText(MainActivity.this, device.getAddress()+"___"+device.getName(), Toast.LENGTH_SHORT).show();
//                            if("liangjian".equalsIgnoreCase(device.getName())){
//                                BluetoothGatt gatt = device.connectGatt(MainActivity.this,false,gattCallback);
////                        if(gatt.connect()){
////                            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
////
////                        }
//                            }
//                        }
//                    }
//                };

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                /*开始扫描*/
                startScanner();

                break;
            default:
                Toast.makeText(this, "拒绝蓝牙开启", Toast.LENGTH_SHORT).show();
                break;
        }
    }

   public class BluetoothList extends BaseAdapter{

       @Override
       public int getCount() {
           return Common.blueList.size();
       }

       @Override
       public Object getItem(int position) {
           return Common.blueList.get(position);
       }

       @Override
       public long getItemId(int position) {
           return position;
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder holder = null;
           if(convertView == null){
               holder = new ViewHolder();
               convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout,null);
               holder.name = (TextView) convertView.findViewById(R.id.name);
               holder.address = (TextView) convertView.findViewById(R.id.address);
               convertView.setTag(holder);
           }else{
               holder = (ViewHolder) convertView.getTag();
           }
           holder.name.setText(Common.blueList.get(position).getName());
           holder.address.setText(Common.blueList.get(position).getAddress());
           return convertView;
       }
   }
   public class ViewHolder{
       private TextView name;
       private TextView address;
   }



    public  void broadCastSend(int position){
        Intent intent = new Intent(action);
        intent.putExtra("devicePosition",position);
        sendBroadcast(intent);
    }
}

