package com.hy.mydivapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DivUtils {
    public static final int REQUESTCODE_PHOTO=901;
    public static final int REQUESTCODE_IMG=902;
    public static Uri imageUri;//相机拍照图片保存地址
    public static  File outputImage;
    public static void showDIV(Bitmap bitmap, ImageView iv_show, int color,File file) {
        // 方式一：使用默认参数配置图像分割检测器。
// 默认模式为：人像分割模式 + 精度模式，返回人像分割的所有分割结果（像素级标记信息、背景透明的人像图、人像为白色，背景为黑色的灰度图以及被分割的原图）。
//        MLImageSegmentationAnalyzer analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer();
// 方式二：使用自定义参数MLImageSegmentationSetting配置图像分割检测器。
        MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory()
                // 设置分割精细模式，true为精细分割模式，false为速度优先分割模式。
                .setExact(true)
                // 设置分割模式为人像分割。
                .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                // 设置返回结果种类，
                // MLImageSegmentationScene.ALL: 返回所有分割结果，包括：像素级标记信息、背景透明的人像图和人像为白色，背景为黑色的灰度图以及被分割的原图。
                // MLImageSegmentationScene.MASK_ONLY: 只返回像素级标记信息。
                // MLImageSegmentationScene.FOREGROUND_ONLY: 只返回背景透明的人像图。
                // MLImageSegmentationScene.GRAYSCALE_ONLY: 只返回人像为白色，背景为黑色的灰度图。
//                .setScene(MLImageSegmentationScene.FOREGROUND_ONLY)
                .create();
        MLImageSegmentationAnalyzer analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting);
        // 通过bitmap创建MLFrame，bitmap为输入的Bitmap格式图片数据。
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        // 创建一个task，处理图像分割检测器返回的结果。
        Task<MLImageSegmentation> task = analyzer.asyncAnalyseFrame(frame);
// 异步处理图像分割检测器返回结果。
        task.addOnSuccessListener(segmentation -> {
            Log.e("Div返回", "foreground:"+(segmentation.foreground==null)+"___grayscale:"+(segmentation.grayscale==null)+"___masks:"+(segmentation.masks.toString()));
            iv_show.setBackgroundColor(color);
            iv_show.setImageBitmap(segmentation.foreground);
            iv_show.setDrawingCacheEnabled(true);
            Bitmap bitmap1 = Bitmap.createBitmap(iv_show.getDrawingCache());
            saveBitmap(bitmap1,file);
            iv_show.setDrawingCacheEnabled(false);
            iv_show.destroyDrawingCache();
            if (outputImage!=null){
                deleteFile(outputImage);
            }

            try {
                analyzer.stop();
            } catch (IOException e) {
                Log.e("Div异常", e.toString());
                // 异常处理。
            }
            // 检测成功处理。
        })
                .addOnFailureListener(e -> {
                    // 检测失败处理。
                    Log.e("Div检测失败", e.toString());
                });

    }

    private static void saveBitmap(Bitmap bitmap1,File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream   stream=new FileOutputStream(file);
            bitmap1.compress(Bitmap.CompressFormat.JPEG,80,stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 删除单个文件
     *
     * @param file 要删除的文件
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File file) {

        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }
    /**
     * 打开相机
     * 兼容7.0
     *
     * @param activity
     */
    public static void openCamera(Activity activity) {
        // 创建File对象，用于存储拍照后的图片
         outputImage = new File(activity.getExternalCacheDir(), "div_image.jpg");

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
            //参数二:fileprovider绝对路径 com.dyb.testcamerademo：项目包名  content://bjb.com.zy.zh.zyzh.fileprovider/files_root/cache/output_image.jpg
            imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        activity.startActivityForResult(intent, REQUESTCODE_PHOTO);
    }

    /**
     * 选择图片，从图库、相机
     *
     * @param activity 上下文
     */
    public static void choicePhoto(final Activity activity) {
        //采用的是系统Dialog作为选择弹框
        final String[] title = new String[]{"拍照", "系统相册", "取消"};
        final InsureHelperListDialog insureHelperListDialog = new InsureHelperListDialog(
                activity, null, title, 0, 0);
        insureHelperListDialog.setItemClickListener(new InsureHelperListDialog.ItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (position == title.length - 1) {

                    insureHelperListDialog.dismiss();
                } else {
                    if (position == 0) {
                        ArrayList<String> permissions = new ArrayList<>();
                        if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.CAMERA);
                        }

                        if (permissions.size() == 0) {//有权限,跳转
                            //打开相机-兼容7.0
                            openCamera(activity);
                        } else {
                            activity.requestPermissions(permissions.toArray(new String[permissions.size()]), 999);
                        }
                    } else if (position == 1) {
                        //如果有权限申请，请在Activity中onRequestPermissionsResult权限返回里面重新调用openAlbum()
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 888);
                        } else {
                            openAlbum(activity);
                        }
                    }
                }

            }
        });
        insureHelperListDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        insureHelperListDialog.show();
    }

    /**
     * 打开图库
     *
     * @param activity
     */
    public static void openAlbum(Activity activity) {
        //调用系统图库的意图
        Intent choosePicIntent = new Intent(Intent.ACTION_PICK, null);
        choosePicIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        choosePicIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*");
        activity.startActivityForResult(choosePicIntent, REQUESTCODE_IMG);

        //打开系统默认的软件
        //Intent intent = new Intent("android.intent.action.GET_CONTENT");
        //intent.setType("image/*");
        //activity.startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    /**
     * 图片尺寸压缩
     * <p>
     * 宽度高度不一样：依靠规定的高或宽其一最大值来做界限
     * 高度宽度一样：依照规定的宽度压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws IOException {
        if (uri == null) {
            return null;
        }
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以750x450为标准
        float hh = 800f;//这里设置高度为750f
        float ww = 800f;//这里设置宽度为450f
        float sq = 800f;//这里设置正方形为300f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        Log.e("缩放", originalWidth + "..." + originalHeight);
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大，根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高，根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        } else if (originalWidth == originalHeight && originalWidth > sq) {//如果高度和宽度一样，根据任意一边大小缩放
            //be = (int) (originalHeight / sq);
            be = (int) (originalWidth / sq);
        }
        if (be <= 0) {//如果缩放比比1小，那么保持原图不缩放
            be = 1;
        }
        Log.e("缩放", be + "");
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return bitmap;//再进行质量压缩
    }

}
