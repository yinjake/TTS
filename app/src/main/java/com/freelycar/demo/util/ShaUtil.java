package com.freelycar.demo.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaUtil {
	private static char[] CHARARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
	'f' };
	
	public static String toSHA1(String str) {
		if (null == str || 0 == str.length()) {
			return null;
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			digest.update(str.getBytes("UTF-8"));
			byte[] md = digest.digest();
			int j = md.length;
			char[] buf = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = CHARARRAY[byte0 >>> 4 & 0xf];
				buf[k++] = CHARARRAY[byte0 & 0xf];
			}
			return new String(buf);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
};