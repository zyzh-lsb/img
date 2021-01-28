package com.hy.mydivapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.div_activity_main);
        iv_show = findViewById(R.id.div_iv_show);
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        //判断
        if (permissions.size() == 0) {//有权限
            DivUtils.choicePhoto(this);
        } else {//没有权限，获取拍照权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(permissions.toArray(new String[permissions.size()]), 999);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 999) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                DivUtils.choicePhoto(this);
            } else {
                Toast.makeText(this, "未同意获取摄像头", Toast.LENGTH_SHORT).show();

            }

        }
        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DivUtils.openAlbum(this);
            } else {
                Toast.makeText(this, "选择图库需要同意权限", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == DivUtils.REQUESTCODE_IMG) {
                if (data != null) {
                    //相册返回
                    Log.e("相册返回", "intent2:" + data.getData().toString());
                    File outputImage = new File(getExternalCacheDir(), "div_image_B.jpg");
                    try {
                        Bitmap bitmap = DivUtils.getBitmapFormUri(this, data.getData());
                        DivUtils.showDIV(bitmap, iv_show, getResources().getColor(R.color.div_b), outputImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("相册返回", "空:");
                }

            }
            if (requestCode == DivUtils.REQUESTCODE_PHOTO) {

                //相机返回
                Log.e("相机返回", "intent2:" + DivUtils.imageUri);
                File outputImage = new File(getExternalCacheDir(), "div_image_B.jpg");
                try {
                    Bitmap bitmap = DivUtils.getBitmapFormUri(this, DivUtils.imageUri);
                    DivUtils.showDIV(bitmap, iv_show, getResources().getColor(R.color.div_b), outputImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "div_image.jpg");
                DivUtils.deleteFile(new File(getExternalCacheDir(), "div_image.jpg"));
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                sendBroadcast(intent);
                DivUtils.deleteFile(file);
            }
        }
    }
}