package com.kong.lutech.apartment.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

public class LeConnector {
    private static final String TAG = "LeConnector";
    private static final ParcelUuid NFC_SERVICE = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final ParcelUuid NFC_CHAR = ParcelUuid.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    public interface LeCallback {
        void connected();

        void disconnected(boolean fromConnecting);

        void receiveNfc(String nfc);
    }

    private Context context;
    private BluetoothGatt bluetoothGatt;
    private LeCallback leCallback;
    private BluetoothGatt gatt;

    private int retryCount = 0;
    private boolean isConnected = false;

    private Handler mainHandler;

    public LeConnector(Context context) {
        this.context = context;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setLeCallback(LeCallback leCallback) {
        this.leCallback = leCallback;
    }

    public void connect(final NfcReader nfcReader, final LeCallback leCallback) {
        this.leCallback = leCallback;
        gatt = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(nfcReader.getMacAddress())
                .connectGatt(context, false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        try {
                            if (newState == BluetoothGatt.STATE_CONNECTED) {
                                sleep(500);
                                Log.d(TAG, "Connected to GATT server.");
                                Log.d(TAG, "Attempting to start service discovery:"
                                        + gatt.discoverServices());
                            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                                Log.d(TAG, "Disconnected from GATT server.");
                                gatt.close();

                                if (!isConnected && ++retryCount <= 5) {
                                    Log.d(TAG, "connect retry");
                                    sleep(500);
                                    connect(nfcReader, leCallback);
                                } else {
                                    disconnected(!isConnected);
                                    isConnected = false;
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                            // getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show());
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        try {
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                Log.d(TAG, "Discovered to Services success.");
                                sleep(500);

                                BluetoothGattService service = gatt.getService(NFC_SERVICE.getUuid());
                                if (service == null) {
                                    gatt.disconnect();
                                } else {
                                    BluetoothGattCharacteristic bchar = service.getCharacteristic(NFC_CHAR.getUuid());
                                    if (bchar == null) {
                                        gatt.disconnect();
                                    } else {
                                        gatt.setCharacteristicNotification(bchar, true);
                                        BluetoothGattDescriptor descriptor = bchar.getDescriptors().get(0);
                                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        gatt.writeDescriptor(descriptor);
                                    }
                                }
                            } else {
                                Log.d(TAG, "Discovered to Services fail.");
                                gatt.disconnect();
                            }
                        } catch (Exception e) {
                            //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show());
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "Receive NFC data : " + characteristic.getValue().toString());
                        receiveNfc(characteristic.getValue());
                    }

                    @Override
                    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Log.d(TAG, "DescriptorWrite success.");
                            sleep(100);
                            connected();
                        } else {
                            Log.d(TAG, "DescriptorWrite fail.");
                            gatt.disconnect();
                        }
                    }
                });
    }

    public void disconnect() {
        retryCount = 99;
        if (gatt != null) {
            gatt.disconnect();
        }
    }

    private void receiveNfc(byte[] value) {
        if (leCallback != null)
            mainHandler.post(() -> leCallback.receiveNfc(new String(value)));
    }

    private void connected() {
        isConnected = true;
        if (leCallback != null)
            mainHandler.post(() -> leCallback.connected());
    }

    private void disconnected(final boolean fromConnecting) {
        isConnected = false;
        gatt = null;
        if (leCallback != null)
            mainHandler.post(() -> leCallback.disconnected(fromConnecting));
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
