package com.sky.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 * Created by SKY on 2017/4/18.
 */
public class MD5Utils {

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return ciphertext 密文
     */
    public final static String encrypt(String plaintext) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(plaintext.getBytes());
            byte[] digest = mdInst.digest();
            int length = digest.length;
            char chars[] = new char[length * 2];
            int k = 0;
            for (int i = 0; i < length; i++) {
                byte byte0 = digest[i];
                chars[k++] = hexDigits[byte0 >>> 4 & 0xf];
                chars[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(chars);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 加密
     *
     * @param plain 明文
     * @return ciphertext 密文
     */
    public static String encryption(String plain) {
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(plain.getBytes());
            byte[] digest = md5.digest();
            for (int i = 0; i < digest.length; i++) {
                String hex = Integer.toHexString(0xFF & digest[i]);
                if (hex.length() == 1) builder.append("0");
                builder.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
