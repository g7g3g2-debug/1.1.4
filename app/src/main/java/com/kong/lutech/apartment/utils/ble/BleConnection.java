package com.kong.lutech.apartment.utils.ble;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.UUID;

public class BleConnection {

    public static final String ACTION_RECEIVE_DATA = "ACTION_RECEIVE_DATA";
    public static final String ACTION_SEND_DATA = "ACTION_SEND_DATA";
    public static final String ACTION_CHANGE_VALUE = "EXTRA_CHANGE_VALUE";
    public static final String ACTION_CHANGE_VALUE_COMPLETE = "EXTRA_CHANGE_VALUE_COMPETE";

    private static final String EXTRA_ACTION_DATA = "EXTRA_ACTION_DATA";
    private static final String EXTRA_ACTION_UUID = "EXTRA_ACTION_UUID";

    public static final UUID UART_SERVICE_UUID = UUID.fromString("9fd45000-e46f-7c9a-57b1-2da365e18fa1");
    public static final UUID UART_TX_UUID = UUID.fromString("9fd45001-e46f-7c9a-57b1-2da365e18fa1");
    public static final UUID UART_RX_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    public static final UUID UUID_KONGTECH_SERVICE = UUID.fromString("9fd42000-e46f-7c9a-57b1-2da365e18fa1");
    public static final UUID UUID_KONGTECH_NAME = UUID.fromString("9fd42006-e46f-7c9a-57b1-2da365e18fa1");



    private Context context;

    private BluetoothGatt bluetoothGatt;
    private ProgressDialog progressDialog;

    private OnStateListener onStateListener;
    private OnChangedValueListener onChangedValueListener;

    public BleConnection(Context context){
        this.context = context;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CHANGE_VALUE);
        intentFilter.addAction(ACTION_SEND_DATA);
        intentFilter.addAction(ACTION_RECEIVE_DATA);

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void connect(String address){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("기기 연결 중 입니다.");
        progressDialog.show();

        bluetoothGatt = device.connectGatt(context, false, gattCallback);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothGatt.STATE_CONNECTED) {
                changeDialogMessage("서비스를 찾는 중입니다.");
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectionEnded();
                bluetoothGatt = null;
                if(onStateListener != null)
                    onStateListener.Disconnected();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                if(onStateListener != null)
                    onStateListener.Connected();
                progressDialog.dismiss();
            }
            else {
                gatt.disconnect();
                connectionEnded();
                bluetoothGatt = null;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] bytes = characteristic.getValue();
            sendReceiveData(bytes);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if(onStateListener != null)
                    onStateListener.Connected();
                progressDialog.dismiss();
                BluetoothGattService service = gatt.getService(UART_SERVICE_UUID);
            } else {
                connectionEnded();
                bluetoothGatt.disconnect();
                bluetoothGatt = null;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            byte[] bytes = characteristic.getValue();
            sendReceiveData(bytes);
        }
    };

    private void connectionEnded() {
        progressDialog.dismiss();

       /* ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("연결에 실패하였습니다. 다시 한번 시도해주세요.");
                builder.setPositiveButton("확인", null).show();
            }
        });*/
    }

    public boolean isConnected(){
        return bluetoothGatt != null;
    }

    public void disconnect(){
        if(bluetoothGatt != null)
            bluetoothGatt.close();
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }

    public void setOnChangedValueListener(OnChangedValueListener onChangedValueListener) {
        this.onChangedValueListener = onChangedValueListener;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION_SEND_DATA)) {
                String msg = intent.getStringExtra(EXTRA_ACTION_DATA);
                if(bluetoothGatt != null){
                    BluetoothGattService service = bluetoothGatt.getService(UART_SERVICE_UUID);
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UART_TX_UUID);
                    characteristic.setValue(msg);
                    bluetoothGatt.writeCharacteristic(characteristic);
                }
            }else if(intent.getAction().equals(ACTION_CHANGE_VALUE)) {
                String msg = intent.getStringExtra(EXTRA_ACTION_DATA);
                String uuidString = intent.getStringExtra(EXTRA_ACTION_UUID);
                UUID uuid = UUID.fromString(uuidString);

                if(bluetoothGatt != null) {
                    BluetoothGattService service = bluetoothGatt.getService(UUID_KONGTECH_SERVICE);
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
                    characteristic.setValue(msg);
                    bluetoothGatt.writeCharacteristic(characteristic);
                }
            }else if(intent.getAction().equals(ACTION_RECEIVE_DATA)){
                byte[] msg = intent.getByteArrayExtra(EXTRA_ACTION_DATA);
                if(onChangedValueListener != null) {
                    onChangedValueListener.onChangedValue(msg);
                }
            }
        }
    };

    private void changeDialogMessage(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(msg);
            }
        });
    }

    private void sendReceiveData(byte[] msg){
        Intent intent = new Intent(ACTION_RECEIVE_DATA);
        intent.putExtra(EXTRA_ACTION_DATA, msg);
        context.sendBroadcast(intent);
    }

    public void close(){
        context.unregisterReceiver(broadcastReceiver);
    }

    public static void sendData(Context context, String msg){
        Intent intent = new Intent(ACTION_SEND_DATA);
        intent.putExtra(EXTRA_ACTION_DATA, msg);
        context.sendBroadcast(intent);
    }

    public static void changeProperty(Context context, UUID uuid, String value){
        Intent intent = new Intent(ACTION_CHANGE_VALUE);
        intent.putExtra(EXTRA_ACTION_DATA, value);
        intent.putExtra(EXTRA_ACTION_UUID, uuid.toString());
        context.sendBroadcast(intent);
    }

    public interface OnStateListener{
        void Connected();
        void Disconnected();
    }

    public interface OnChangedValueListener {
        void onChangedValue(byte[] value);
    }
}
