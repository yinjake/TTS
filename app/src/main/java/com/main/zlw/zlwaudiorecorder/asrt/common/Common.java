package com.main.zlw.zlwaudiorecorder.asrt.common;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.main.zlw.zlwaudiorecorder.asrt.Base64Utils;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Common {
    private static final String UTF_8 = "utf-8";


    public static byte[] base64Decode(String base64Str) {
        try {
            byte[] decode = Base64.getDecoder().decode(base64Str.getBytes(UTF_8));
            return decode;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }


    public static String base64Encode(byte[] byteArray) {
        String base64Str = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                base64Str = Base64.getEncoder().encodeToString(byteArray);
            }
            //     base64Str = Base64Utils.encode(byteArray);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return base64Str;
    }

    public static byte[] readBinFile(String filename) {
        FileInputStream input = null;
        try {
            List<Byte> byteList = new ArrayList();
            input = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            while (true) {
                int len = input.read(buffer);
                if (len == -1) {
                    break;
                }
                for(int i = 0; i < len; i++){
                    byteList.add(buffer[i]);
                }
            }
            byte[] byteArr = new byte[byteList.size()];
            for(int i = 0; i< byteArr.length; i++){
                byteArr[i] = byteList.get(i);
            }
            return byteArr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                input.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
