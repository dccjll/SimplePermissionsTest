package com.yundushan.dccjll.library.simplepermissionslibrary;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dccjll on 2017/2/8.
 * 简单权限管理活动
 */

public class SimplePermissionsActivity extends AppCompatActivity {

    private final String TAG = "SimplePermissions";

    private int REQUEST_CODE_PERMISSION_CALL = 0x0001;
    private int REQUEST_CODE_PERMISSION_WRITE_SDCARD = 0x0002;
    private int REQUEST_CODE_PERMISSION_CAMERA = 0x0003;

    private OnRequestPermissionListener onRequestPermissionListener;


    /**
     * 权限请求回调接口
     */
    public interface OnRequestPermissionListener{
        void onGranted(int requestCode, String[] requestPermissions);
        void onDenied(int requestCode, String[] requestPermissions);
    }


    /**
     * 请求打电话权限
     * @param onRequestPermissionListener   权限请求回调接口
     */
    public void requestCallPermission(OnRequestPermissionListener onRequestPermissionListener) {
        String[] permissions = new String[]{Manifest.permission.CALL_PHONE};
        requestPermission(REQUEST_CODE_PERMISSION_CALL, permissions, onRequestPermissionListener);
    }

    /**
     * 请求写存储卡权限
     * @param onRequestPermissionListener   权限请求回调接口
     */
    public void requestWriteSDCardPermission(OnRequestPermissionListener onRequestPermissionListener) {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermission(REQUEST_CODE_PERMISSION_WRITE_SDCARD, permissions, onRequestPermissionListener);
    }

    /**
     * 请求拍照与摄像权限
     * @param onRequestPermissionListener   权限请求回调接口
     */
    public void requestCameraPermission(OnRequestPermissionListener onRequestPermissionListener) {
        String[] permissions = new String[]{Manifest.permission.CAMERA};
        requestPermission(REQUEST_CODE_PERMISSION_CAMERA, permissions, onRequestPermissionListener);
    }

    /**
     * 请求权限
     * @param requestCode   请求权限的代码
     * @param permissions   请求权限的列表
     * @param onRequestPermissionListener   请求权限的回调接口
     */
    private void requestPermission(int requestCode, String[] permissions, OnRequestPermissionListener onRequestPermissionListener) {
        if(onRequestPermissionListener == null){
            throw new NullPointerException("未配置权限请求回调接口");
        }
        this.onRequestPermissionListener = onRequestPermissionListener;
        if (checkPermissions(permissions)) {
            Log.d(TAG, "requestCallPermission, 获取权限成功=" + requestCode);
            onRequestPermissionListener.onGranted(requestCode, permissions);
        } else {
            List<String> needPermissions = getDeniedPermissions(permissions);
            ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]), requestCode);
        }
    }


    /**
     * 检测所有的权限是否都已授权
     *
     * @param permissions   待检查的权限列表
     * @return  true    已授权 false   未授权
     */
    private boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限列表中需要申请权限的列表
     *
     * @param permissions   待搜索的权限列表
     * @return  需要申请权限的权限列表
     */
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }


    /**
     * 系统请求权限回调
     *
     * @param requestCode   请求权限的代码
     * @param permissions   请求权限的列表
     * @param grantResults  请求权限列表的请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_CALL
                || requestCode == REQUEST_CODE_PERMISSION_WRITE_SDCARD
                || requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            if (verifyPermissions(grantResults)) {
                Log.d(TAG, "onRequestPermissionsResult, 获取权限成功=" + requestCode);
                onRequestPermissionListener.onGranted(requestCode, permissions);
            } else {
                Log.d(TAG, "onRequestPermissionsResult, 获取权限失败=" + requestCode);
                onRequestPermissionListener.onDenied(requestCode, permissions);
                showTipsDialog(requestCode);
            }
        }
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults  需要确认的权限列表
     * @return  true    已授权 false 未授权
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示对话框
     * @param requestCode   权限请求代码
     */
    private void showTipsDialog(int requestCode) {
        String permissionDesc = "必要";
        if(requestCode == REQUEST_CODE_PERMISSION_CALL){
            permissionDesc = "打电话";
        }else if(requestCode == REQUEST_CODE_PERMISSION_WRITE_SDCARD){
            permissionDesc = "写存储卡";
        }
        new AlertDialog.Builder(this)
                .setTitle("提示信息")
                .setMessage("当前应用缺少" + permissionDesc + "权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "showTipsDialog, 已取消");
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                }).show();
    }

    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
