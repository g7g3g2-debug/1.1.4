package com.kong.lutech.apartment.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.le.LeConnector;
import com.kong.lutech.apartment.le.NfcReader;
import com.kong.lutech.apartment.model.NFC;
import com.kong.lutech.apartment.model.NFCResponse;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfcActivity extends AppCompatActivity implements LeConnector.LeCallback {
    private static final String TAG = "NfcActivity";

    private BluetoothLeScanner leScanner;
    private LeConnector leConnector;
    private Map<NfcReader, List<Integer>> deviceMap = new HashMap<>();

    private boolean isScanning;

    private String authCode;

    private Handler scanFinishHandler;

    private NfcReader targetNfcReader;

    private boolean isCommnunicate;

    private View animationView;
    private TextView titleView;
    private TextView descriptionView;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            NfcReader device = NfcReader.makeAutoDoorFromScanResult(authCode, result);

            if (device != null) {
                List<Integer> rssiList = null;
                if (deviceMap.containsKey(device)) {
                    rssiList = deviceMap.get(device);
                } else {
                    rssiList = new ArrayList<>();
                    deviceMap.put(device, rssiList);
                }
                rssiList.add(device.getRssi());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            new AlertDialog.Builder(NfcActivity.this)
                    .setMessage("장치 검색에 실패 하였습니다.")
                    .setPositiveButton("확인", null)
                    .setOnDismissListener(dialog -> finish())
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        authCode = Config.getMobile().getAuthCode();

        animationView = findViewById(R.id.anim_view);

        titleView = findViewById(R.id.tv_title);
        descriptionView = findViewById(R.id.tv_description);

        startScan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMessage(String title, String message) {
        titleView.setText(title);
        descriptionView.setText(message);
    }

    private void startScan() {

        setMessage("출입단말기 검색 중", "주변의 출입 단말기를 찾고 있습니다.");
        animationView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_dim));
        leScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        leScanner.startScan(new ArrayList<>(),
                new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
                scanCallback);
        isScanning = true;

        scanFinishHandler = new Handler();
        scanFinishHandler.postDelayed(() -> {
            leScanner.stopScan(scanCallback);
            isScanning = false;
            checkDeviceMap();
        }, 5000);
    }

    private void checkDeviceMap() {
        int maximumRssi = -100;

        for (NfcReader nfcReader : deviceMap.keySet()) {
            List<Integer> rssis = deviceMap.get(nfcReader);
            int finalRssis = 0;
            for (Integer rssi : rssis) {
                finalRssis += rssi;
            }
            finalRssis /= rssis.size();

            if (maximumRssi < finalRssis) {
                maximumRssi = finalRssis;
                targetNfcReader = nfcReader;
            }
        }

        if (targetNfcReader != null && maximumRssi > -80) {
            connect();
        } else {
            deviceNotFound();
        }
    }

    private void connect() {
        setMessage("출입단말기 연결 중", "출입단말기와 연결 중 입니다.");
        leConnector = new LeConnector(this);
        leConnector.connect(targetNfcReader, this);
    }

    private void deviceNotFound() {
        new AlertDialog.Builder(NfcActivity.this)
                .setMessage("장치 검색에 실패 하였습니다.")
                .setPositiveButton("확인", null)
                .setOnDismissListener(dialog -> finish())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        scanFinishHandler.removeCallbacksAndMessages(null);
        if (isScanning)
            leScanner.stopScan(scanCallback);

        if (leConnector != null) {
            leConnector.setLeCallback(null);
            leConnector.disconnect();
        }
    }

    @Override
    public void connected() {
        animationView.clearAnimation();
        setMessage("단말기에 태그하기", "LED가 켜진 단말기에 태크해 주세요.");
    }

    @Override
    public void disconnected(boolean fromConnecting) {
        new AlertDialog.Builder(NfcActivity.this)
                .setTitle("실패")
                .setMessage(fromConnecting ? "출입단말기 연결에 실패하였습니다." : "출입단말기와 연결이 끊어졌습니다.")
                .setPositiveButton("확인", null)
                .setOnDismissListener(dialog -> finish())
                .show();
    }

    @Override
    public void receiveNfc(String nfc) {
        if(leConnector != null) {
            leConnector.setLeCallback(null);
            leConnector.disconnect();
        }

        if (!isCommnunicate) {
            isCommnunicate = true;
            ApartmentRestUtil.getIntsance(this)
                    .postNfc(Config.getAccessToken(), new NFC("Mobile", nfc))
                    .subscribe(nfcResponseResponse -> {
                        if (nfcResponseResponse.isSuccessful()) {
                            new AlertDialog.Builder(NfcActivity.this)
                                    .setTitle("성공")
                                    .setMessage("휴대폰 NFC등록을\n성공적으로 완료하였습니다.")
                                    .setPositiveButton("확인", null)
                                    .setOnDismissListener(dialog -> finish())
                                    .show();
                        } else {
                            String error = nfcResponseResponse.errorBody().string();
                            NFCResponse object = new Gson().fromJson(error, NFCResponse.class);
                            if ("NFC Information already exists.".equals(object.getMessage())) {
                                new AlertDialog.Builder(NfcActivity.this)
                                        .setTitle("실패")
                                        .setMessage("이미 등록된 NFC 입니다.")
                                        .setPositiveButton("확인", null)
                                        .setOnDismissListener(dialog -> finish())
                                        .show();
                            } else {
                                new AlertDialog.Builder(NfcActivity.this)
                                        .setTitle("실패")
                                        .setMessage("휴대폰 NFC등록을 실패하였습니다.\n다시 시도해 주세요.")
                                        .setPositiveButton("확인", null)
                                        .setOnDismissListener(dialog -> finish())
                                        .show();
                            }
                        }
                        isCommnunicate = false;
                    }, throwable -> {
                        new AlertDialog.Builder(NfcActivity.this)
                                .setTitle("실패")
                                .setMessage("휴대폰 NFC등록을 실패하였습니다.\n다시 시도해 주세요.")
                                .setPositiveButton("확인", null)
                                .setOnDismissListener(dialog -> finish())
                                .show();
                        isCommnunicate = false;
                    });
        }
    }
}
