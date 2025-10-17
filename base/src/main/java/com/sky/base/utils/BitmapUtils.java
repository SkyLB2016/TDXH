package com.sky.base.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SKY on 2017/6/6.
 * bitmap工具类
 */
public class BitmapUtils {

    /**
     * 从资源中获取Bitmap
     */
    public static Bitmap getBitmapFromId(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Bitmap getBitmapUP(String pathName) {
        if (pathName.startsWith("http")) return getBitmapFromUrl(pathName);
        else return getBitmapFromPath(pathName);
    }

    public static Bitmap getBitmapFromPath(String pathName) {
        return BitmapFactory.decodeFile(pathName);
    }

    /**
     * 获取网络图片
     */
    public static Bitmap getBitmapFromUrl(String pathName) {
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(pathName);
            conn = (HttpURLConnection) url.openConnection();
//            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (IOException e) {
        } finally {
            if (conn != null) conn.disconnect();
        }
        return bitmap;
    }

    /**
     * 从文件路径中获取bitmap,根据比例inSampleSize，来缩放图片
     */
    public static Bitmap getBitmapFromPath(String pathName, int newWidth, int newHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;//设置为ture只获取图片大小
        BitmapFactory.decodeFile(pathName, opts);
        opts.inSampleSize = calculateInSampleSize(opts, newWidth, newHeight);//计算缩放率，缩放图片
        opts.inJustDecodeBounds = false;//至为false
        return BitmapFactory.decodeFile(pathName, opts);

    }

    /**
     * 计算InSampleSize,大于1的整数时等比缩小原图
     */
    private static int calculateInSampleSize(BitmapFactory.Options opts, int newW, int newH) {
        int inSampleSize = 1;//计算缩放率，缩放图片
        int width = opts.outWidth;
        int height = opts.outHeight;
        if (width > newW || height > newH)
            inSampleSize = (int) Math.ceil(Math.min(width * 1d / newW, height * 1d / newH));
//        if (width > newW || height > newH) {
//            int halfW = width / 2;
//            int halfH = height / 2;
//            while ((halfW / inSampleSize) >= newW && (halfH / inSampleSize) >= newH) {
//                inSampleSize *= 2;
//            }
//        }
        return inSampleSize;
    }

    /**
     * 获取bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getAllocationByteCount();
    }

    /**
     * bitmap转换为drawable
     */
    public static Drawable getDrawableFromBitmap(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * matrix 缩放/裁剪图片，根据提供的宽高缩放
     *
     * @return 裁剪后的图片
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int newW, int newH) {
        int bW = bitmap.getWidth();
        int bH = bitmap.getHeight();
        // 计算缩放比例；缩放率X*width =newWidth；
        float scaleW = ((float) newW) / bW;
        float scaleH = ((float) newH) / bH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        return Bitmap.createBitmap(bitmap, 0, 0, bW, bH, matrix, true);
    }

    /**
     * 按大小压缩图片
     *
     * @param targetKB
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int targetKB) {
        int quality = 100;
        byte[] target;
        while ((target = getBytesFromBitmap(bitmap, quality)).length / 1024 > targetKB) {
            quality -= 10;
        }
        return BitmapFactory.decodeByteArray(target, 0, target.length);
    }

    /**
     * base64转bitmap
     *
     * @param base64 base64 的字符串
     * @return
     */
    public static Bitmap getBitmapFromBase64(String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            LogUtils.d(e.toString());
        }
        return null;
    }

    /**
     * 把bitmap转换成base64
     */
    public static String getBase64FromBitmap(Bitmap bitmap) {
        return Base64.encodeToString(getBytesFromBitmap(bitmap, 100), Base64.DEFAULT);
    }

    /**
     * bitmap转bytes
     *
     * @param bitmap
     * @param quality 压缩百分比1-100,100代表不压缩
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (baos == null) baos.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * bitmap 转 file
     *
     * @param bitmap
     * @param pathName 绝对路径
     * @return
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String pathName) {
        File file = new File(pathName);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveBitmapToFile(bitmap, file);
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    static Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFFFFCC44);
        c.drawOval(new RectF(0, 0, w * 3 / 4, h * 3 / 4), p);
        return bm;
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param uri
     */
    public static String getRealPathFromURI(Context context, Uri uri) {
        if (context == null || uri == null)
            return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                if (docId.startsWith("raw:")) {
                    return docId.replaceFirst("raw:", "");
                }
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        String path = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int num = cursor.getColumnCount();
                if (num == 0) {
                    path = uri.getPath();
                } else {
                    int index = cursor.getColumnIndexOrThrow(column);
                    path = cursor.getString(index);
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return path;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
