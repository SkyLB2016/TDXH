package com.sky.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.FileProvider;


import com.sky.base.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by SKY on 2017/8/18.
 * 照片选择器
 */
public class PhotoUtils {
    private AppCompatActivity activity;
    private String photoPath;//图片所在的位置

    public static final int PHOTO = 1501; // 拍照
    public static final int PHOTO_PERMISSIONS = 1502; // 拍照权限请求
    public static final int LOCAL_PHOTO = 1503; // 图库
    public static final int LOCAL_PHOTO_PERMISSIONS = 1504; // 图库相册权限

    @SuppressLint("RestrictedApi")
    public PhotoUtils(AppCompatActivity context, String photoPath) {
        this.activity = context;
        this.photoPath = photoPath;
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialog))
                .setItems(new String[]{"拍照", "本地照片"},
                        (dialog, which) -> {
                            if (which == 0) checkCamera();//拍照
                            else checkAlbum();//本地图库
                        })
                .show();
    }

    public PhotoUtils(AppCompatActivity context, String photoPath, Boolean flag) {
        this.activity = context;
        this.photoPath = photoPath;
        if (flag) checkCamera();//拍照
        else checkAlbum();//本地图库
    }

    //打开相机
    private void checkCamera() {
        //检测是否有相机和读写文件权限

        if (AppUtils.isPermission(activity, Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            AppUtils.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    PHOTO_PERMISSIONS);
        }
    }

    //打开本地图库
    private void checkAlbum() {
        if (AppUtils.isPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            openAlbum();
        } else {
            AppUtils.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    LOCAL_PHOTO_PERMISSIONS);
        }
    }

    //打开相机
    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(photoPath);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //第二参数是在manifest.xml定义 provider的authorities属    性
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
            //兼容版本处理，因为 intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) 只在5.0以上的版本有效
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, PHOTO);
    }

    //打开相册
    private void openAlbum() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        activity.startActivityForResult(galleryIntent, LOCAL_PHOTO);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCAL_PHOTO_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openAlbum();
                else showToast("选择相册需要读写文件权限");
                break;
            case PHOTO_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startCamera();
                else showToast("拍照功能需要相机和读写文件权限");
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
//        String error = activity.getString(R.string.photo_fail);
        String error = "加载图片失败";
        if (StringUtils.notNull(photoPath, error)) return;
        Bitmap bitmap = null;
        switch (requestCode) {
            case PHOTO: //拍照
                bitmap = BitmapUtils.getBitmapFromPath(photoPath, 600, 600);//获取bitmap
                int degree = readPictureDegree(photoPath);
//                LogUtils.i("照片角度==" + degree);
                bitmap = rotaingImageView(degree, bitmap);
                break;
            case LOCAL_PHOTO: //图库选择
                if (data == null) return;
                Uri uri = data.getData(); //获得图片的uri
                if (StringUtils.notNullObj(uri, error)) return;
                String path = BitmapUtils.getRealPathFromURI(activity, uri);//获取路径
                if (StringUtils.notNull(path, error)) return;
                bitmap = BitmapUtils.getBitmapFromPath(path, 600, 600);//获取bitmap
                break;
        }
        BitmapUtils.saveBitmapToFile(bitmap, photoPath);//保存照片到应用缓存文件目录下
        uploadPicture.UpLoadPicture(photoPath, bitmap);
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void showToast(String text) {
        ToastUtils.showShort(activity, text);
    }

    private UploadPictureListener uploadPicture;

    public void setUploadPicture(UploadPictureListener uploadPicture) {
        this.uploadPicture = uploadPicture;
    }

    public interface UploadPictureListener {
        void UpLoadPicture(String photoPath, Bitmap bitmap);
    }
}