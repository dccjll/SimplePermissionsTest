package com.yundushan.dccjll.simplepermissionstest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yundushan.dccjll.library.simplepermissionslibrary.SimplePermissionsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends SimplePermissionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 打电话
     *
     * @param view
     */
    public void onClick1(View view) {
        requestCallPermission(
                new OnRequestPermissionListener() {
                    @Override
                    public void onGranted(int requestCode, String[] requestPermissions) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:18668165281"));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onDenied(int requestCode, String[] requestPermissions) {
                        Log.e("MainActivity", "打电话权限被阻止");
                    }
                }
        );
    }

    /**
     * 写SD卡
     *
     * @param view
     */
    public void onClick2(View view) {
        requestWriteSDCardPermission(
                new OnRequestPermissionListener() {
                    @Override
                    public void onGranted(int requestCode, String[] requestPermissions) {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "permissiontest";
                        File file = new File(path);
                        if(!file.exists()){
                            file.mkdirs();
                        }
                        file = new File(path, "a.txt");
                        FileOutputStream fos;
                        try {
                            fos = new FileOutputStream(file);
                            byte[] data = "这是测试访问SD卡权限的测试写入文字内容，哈哈哈，啦啦啦啦，呜呜哇哇~~~~~~".getBytes();
                            fos.write(data, 0, data.length);
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        Toast.makeText(MainActivity.this, "写入完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied(int requestCode, String[] requestPermissions) {
                        Log.e("MainActivity", "写SD卡权限被阻止");
                    }
                }
        );
    }

    /**
     * 拍照
     *
     * @param view
     */
    public void onClick3(View view) {
        requestCameraPermission(
                new OnRequestPermissionListener() {
                    @Override
                    public void onGranted(int requestCode, String[] requestPermissions) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        String cameraPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "permissiontest" + File.separator + "camera_" + SystemClock.currentThreadTimeMillis() + ".png";
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraPath)));
                        startActivity(intent);
                    }

                    @Override
                    public void onDenied(int requestCode, String[] requestPermissions) {
                        Log.e("MainActivity", "拍照权限被阻止");
                    }
                }
        );
    }
}
