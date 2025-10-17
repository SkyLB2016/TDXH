package com.sky.base.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sky.base.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by SKY on 2015/11/28.
 */
public class FileUtils {

    /**
     * 从路径中获取最后一个斜杠/之后的名称
     *
     * @param path 文件路径名称
     * @return
     */
    public static String getFileName(String path) {
        int start = path.lastIndexOf(File.separator);
        if (start == -1) return "路径格式错误";
        return path.substring(start + 1, path.length());
    }

    /**
     * 删除文件 或者文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    /**
     * 删除文件 或者文件夹下所有文件
     */
    public static boolean deleteFile(File dirOrFile) {
        if (dirOrFile == null || !dirOrFile.exists()) return false;
        if (dirOrFile.isFile()) {
            dirOrFile.delete();
        } else if (dirOrFile.isDirectory()) {
            for (File file : dirOrFile.listFiles()) {
                deleteFile(file);// 递归
            }
        }
        dirOrFile.delete();
//        if (dirOrFile.isDirectory()) {
//            for (File file : dirOrFile.listFiles()) {
//                deleteFile(file);// 递归
//            }
//        }
//        dirOrFile.delete();
        return true;
    }

    /**
     * 获取文件夹所占空间大小,字节byte
     */
    public static long getDirSize(File dir) {
        long size = 0;
        File[] flist = dir.listFiles();//获取当前文件夹下的文件
        if (flist == null) return size;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) //是文件夹，搜索文件夹内的问价
                size = size + getDirSize(flist[i]);
            else size = size + flist[i].length();
        }
        return size;
    }

    /**
     * 获取文件夹内文件个数
     *
     * @param folder
     * @return
     */
    public static long getNumberOfFiles(File folder) {
        File fileList[] = folder.listFiles();
        long size = fileList.length;
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getNumberOfFiles(fileList[i]);
                size--;
            }
        }

//        String paths[] = folder.list();
//
//        int length = paths.length;
//        File dirOrFile;
//        for (int i=0;i<length;i++){
//            dirOrFile=new File(paths[i]);
//            if (dirOrFile.isDirectory()){
//
//            }
//        }





        return size;
    }

    /**
     * 复制文件(以超快的速度复制文件)
     *
     * @param srcFile 源文件File
     * @param destDir 目标目录File
     * @param newName 新文件名
     */
    public static void copyFile(File srcFile, File destDir, String newName) {
        try {
            if (!destDir.exists()) destDir.mkdirs();
            File dstFile = new File(destDir, newName);//创建目标文件
            FileChannel fcin = new FileInputStream(srcFile).getChannel();//源文件通道
            FileChannel fcout = new FileOutputStream(dstFile).getChannel();
            long size = fcin.size();
            fcin.transferTo(0, size, fcout);//写入目标文件
            fcin.close();
            fcout.close();
        } catch (FileNotFoundException e) {
            LogUtils.d(e.toString());
        } catch (IOException e) {
            LogUtils.d(e.toString());
        }
    }

    /**
     * 把序列化的对象保存到本地
     *
     * @param pathname 文件路径
     * @param object   要保存的对象
     */
    public static <T> void serialize(String pathname, T object) {
        ObjectOutputStream oos = null;
        try {
            File file = new File(pathname);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);// 写入到本地
            oos.flush();
        } catch (FileNotFoundException e) {
            LogUtils.d(e.toString());
        } catch (IOException e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (oos != null) oos.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 反序列化
     *
     * @param pathname 文件路径
     * @return 解析好的数据对象
     */
    public static <T> T deserialize(String pathname) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(pathname));
            return (T) ois.readObject();//从本地获取数据并返回
        } catch (IOException e) {
            LogUtils.d(e.toString());
        } catch (ClassNotFoundException e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * @param pathname 绝对路径
     * @param content  要保存的文本内容
     */
    public static void saveCharFile(String pathname, String content) {
        saveCharFile(pathname, content, false);
    }

    /**
     * 保存数据，字符流
     *
     * @param pathname 绝对路径
     * @param content  要保存的文本内容
     * @param content  是否追加,
     */
    public static void saveCharFile(String pathname, String content, Boolean append) {
        BufferedWriter bw = null;
        try {
            File file = new File(pathname);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            bw.flush();
//            int start = 0;
//            int interval = 100;
//            int end = interval;
//            int index = content.length() / interval;
//            for (int i = 0; i < index + 1; i++) {
//                start = (i) * interval;
//                end = (i + 1) * interval;
//                if (content.length() < end) end = content.length();
//                String text = content.substring(start, end);
//                LogUtils.i(text);
//                output.write(text);
//                output.flush();
//            }
        } catch (Exception e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (bw != null) bw.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 保存数据流，字节流
     *
     * @param pathname 文件绝对路径
     * @param stream   数据流
     */

    public static void saveByteFile(String pathname, InputStream stream) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(pathname);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = stream.read(bytes)) > -1) {
                bos.write(bytes, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (bos != null) bos.close();
                if (fos != null) fos.close();
                if (stream != null) stream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 通过字节流读取文件中的内容，适用于流媒体文件
     *
     * @param pathname 绝对路径
     * @return
     */
    public static String readByteFile(String pathname) {
        return readByteFile(new File(pathname));
    }

    /**
     * 通过字节流读取文件中的内容，适用于流媒体文件
     *
     * @param file
     * @return
     */
    @NonNull
    private static String readByteFile(File file) {
        try {
            return readInput(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            LogUtils.d(e.toString());
        }
        return "";
    }

    /**
     * 读取输入流中数据
     *
     * @param in
     * @return
     */
    public static String readInput(InputStream in) {
        StringBuilder result = new StringBuilder();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(in);
            int len;
            byte[] bytes = new byte[1024];
            while ((len = bis.read(bytes)) > -1) {
                result.append(new String(bytes, 0, len));
            }
        } catch (IOException e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (bis != null) bis.close();
                if (in != null) in.close();
            } catch (IOException e) {
            }
        }
        return result.toString();
    }

    /**
     * 通过字符流读取文件中的内容，适用于文本文件的读取
     *
     * @param pathname 绝对路径
     * @return
     */
    public static String readCharFile(String pathname) {
        return readCharFile(new File(pathname));
    }

    /**
     * 通过字符流读取文件中的内容，适用于文本文件的读取
     *
     * @param file 文本文件
     * @return
     */
    public static String readCharFile(File file) {
        try {
            return readCharFile(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readCharFile(InputStream in) {
        return readCharFile(new InputStreamReader(in));
    }

    public static String readCharFile(InputStreamReader in) {
        StringBuilder result = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(in);
            char[] chars = new char[1024];
            int len;
            while ((len = br.read(chars)) > -1) {
                result.append(new String(chars, 0, len));
            }
        } catch (IOException e) {
            LogUtils.d(e.toString());
        } finally {
            try {
                if (br != null) br.close();
                if (in != null) in.close();
            } catch (IOException e) {
            }
        }
        return result.toString();
    }

    /**
     * 读取assest文本文件，并返回中文文本
     *
     * @param context
     * @param pathname 路径
     * @return 字符流
     */
    public static String readAssestToChar(Context context, String pathname) {
        try {
            return readCharFile(context.getAssets().open(pathname));
        } catch (IOException e) {

        }
        return "";
    }

    /**
     * 字节流，返回文本，一般是英文使用的
     *
     * @param context
     * @param fileName
     * @return 字节流
     */
    public static String readAssestToByte(Context context, String fileName) {
        //先初始化输入输出流。防止处理失败，不能关闭
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = context.getAssets().open(fileName);
            //int length = input.available();//输入流的总长度
            baos = new ByteArrayOutputStream();// 创建字节输出流对象
            int len;//每次读取到的长度
            byte bytes[] = new byte[1024];//定义缓冲区
            // 按照缓冲区的大小，循环读取，
            while ((len = is.read(bytes)) != -1) {
                baos.write(bytes, 0, len);//根据读取的长度写入到os对象中
            }
            return new String(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) baos.close();
                if (is != null) is.close();
            } catch (IOException e) {
            }
        }
        return "";
    }
}
