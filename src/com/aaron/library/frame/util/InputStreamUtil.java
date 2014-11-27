package com.aaron.library.frame.util;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 数据流工具类
 * @author linjinfa@126.com
 * @date 2013-8-29 上午10:05:29
 */
public class InputStreamUtil {

	private final static int BUFFER_SIZE = 1024;
	
	/**
	 * 将InputStream转换成UTF-8的String
	 * @param in
	 * @return
	 */
	public static String InputStreamTOStringUTF8(InputStream in){
		return InputStreamTOString(in,"UTF-8");
	}

	/**
	 * 将InputStream转换成某种字符编码的String
	 * @param in
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String InputStreamTOString(InputStream in, String encoding) {
		if (in == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}finally{
			try {
				in.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * 将String转换成InputStream
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static InputStream StringTOInputStream(String in,String encoding) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
			return is;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 将InputStream转换成byte数组
	 * 
	 * @param in
	 *            InputStream
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] InputStreamTOByte(InputStream in) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] data = new byte[BUFFER_SIZE];
			int count = -1;
			while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
				outStream.write(data, 0, count);
			data = null;
			in.close();
			return outStream.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 将byte数组转换成InputStream
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static InputStream byteTOInputStream(byte[] in) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(in);
		return is;
	}

	/**
	 * 将byte数组转换成String
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String byteTOString(byte[] in,String encoding) throws Exception {
		InputStream is = byteTOInputStream(in);
		return InputStreamTOString(is,encoding);
	}

}
