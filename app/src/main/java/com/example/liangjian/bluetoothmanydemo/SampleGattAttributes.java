package com.example.liangjian.bluetoothmanydemo;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String MOTOR1_CONTROL = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR2_CONTROL = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR1_CONTROL_1 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR1_CONTROL_2 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR1_CONTROL_3 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR1_CONTROL_4 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR2_CONTROL_1 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR2_CONTROL_2 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR2_CONTROL_3 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MOTOR2_CONTROL_4 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static String SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb";
    public static String FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb";
    public static String HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb";
    public static String SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb";
    public static String MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000fff0-0000-1000-8000-00805f9b34fb", "Motor Control Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(MOTOR1_CONTROL, "Motor1 Control");
        attributes.put(MOTOR2_CONTROL, "Motor2 Control");
        attributes.put(MOTOR1_CONTROL_1, "Motor1 Control 1");
        attributes.put(MOTOR1_CONTROL_2, "Motor1 Control 2");
        attributes.put(MOTOR1_CONTROL_3, "Motor1 Control 3");
        attributes.put(MOTOR1_CONTROL_4, "Motor1 Control 4");
        attributes.put(MOTOR2_CONTROL_1, "Motor2 Control 1");
        attributes.put(MOTOR2_CONTROL_2, "Motor2 Control 2");
        attributes.put(MOTOR2_CONTROL_3, "Motor2 Control 3");
        attributes.put(MOTOR2_CONTROL_4, "Motor2 Control 4");
        attributes.put(MODEL_NUMBER_STRING, "Model Number String");
        attributes.put(SERIAL_NUMBER_STRING, "Serial Number String");
        attributes.put(FIRMWARE_REVISION_STRING, "Firmware Revision String");
        attributes.put(HARDWARE_REVISION_STRING, "Hardware Revision String");
        attributes.put(SOFTWARE_REVISION_STRING, "Software Revision String");
        attributes.put(MANUFACTURER_NAME_STRING, "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
