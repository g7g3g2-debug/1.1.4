package com.kong.lutech.apartment.utils.ble;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimdonghyuk on 2017. 3. 27..
 */

public class BlePermission {

    private Context context;
    private List<String> permissions;
    private String message;
    private int requestCode;

    public BlePermission(Context context, List<String> permission, String message, int requestCode) {
        this.context = context;
        this.permissions = permission;
        this.message = message;
        this.requestCode = requestCode;
    }

    public void check() {
        final List<String> requestPermission = new ArrayList<>();
        for (String permission: permissions) {
            final int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    new AlertDialog.Builder(context)
                            .setMessage(message)
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
                            }).show();
                }
                requestPermission.add(permission);
            }
        }
        if (requestPermission.size() > 0) {
            String[] requestArr = new String[requestPermission.size()];
            ActivityCompat.requestPermissions((Activity) context, requestPermission.toArray(requestArr), requestCode);
        }

        /*final int permissionCheck = ContextCompat.checkSelfPermission(context, permissions);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //없음
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions)) {
                new AlertDialog.Builder(context)
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                ActivityCompat.requestPermissions((Activity) context, new String[]{permissions}, requestCode);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permissions}, requestCode);
            }
        } else {
            //있음
        }*/
    }

    public static class Builder {
        private Context context;

        private List<String> permission;
        private String message;
        private int requestCode;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setPermissions(List<String> permission) {
            this.permission = permission;
            return this;
        }

        public Builder setRequestMessage(String message){
            this.message = message;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public BlePermission build() {
            return new BlePermission(context, permission, message, requestCode);
        }

        public void check() {
            new BlePermission(context, permission, message, requestCode).check();
        }
    }
}
