package com.aaron.library.frame.util;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Json操作类
 * @author linjinfa@126.com 
 * @version 2012-7-2 上午11:52:00
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class JsonUtil<T> {
	
	private String jsonTarget;	//前缀:rows
	private Type type;
	private Class<?> cls;
	private Gson gson;
	
	/**
	 * 
	 * @param jsonTarget
	 * @param type	new TypeToken<List<T>>(){}.getType()
	 */
	public JsonUtil(String jsonTarget,Type type) {
		this.jsonTarget = jsonTarget;
		this.type = type;
		gson = new Gson();
	}
	
	/**
	 * 
	 * @param jsonTarget
	 * @param cls
	 */
	public JsonUtil(String jsonTarget,Class<?> cls) {
		this.jsonTarget = jsonTarget;
		this.cls = cls;
		gson = new Gson();
	}

	/**
	 * 解析Json字符串返回List对象集合
	 * @param jsonStr
	 * @return
	 */
	public List<T> readArrayByJsonStr(String jsonStr){
		if(jsonTarget==null){
			return readArrayJsonNoTagStr(jsonStr);
		}
		try {
			return readArrayJsonStr(jsonStr);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 解析Json字符串返回List对象集合
	 * @param in
	 * @return
	 */
	public List<T> readArrayJsonByStream(InputStream in){
		try {
			if(jsonTarget==null){
				return readArrayJsonNoTagStream(in);
			}
			return readArrayJsonStream(in);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 解析的Json字符串
	 * @param jsonStr
	 * @return	实体对象
	 * @throws Exception
	 */
	public T readObjectJsonStr(String jsonStr){
		try {
			return readObjectJsonStr(new ByteArrayInputStream(jsonStr.getBytes("UTF-8")));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 解析的Json字符串
	 * @param jsonStr
	 * @return	实体对象
	 * @throws Exception
	 */
	public T readObjectJsonStr(InputStream in) {
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			reader.setLenient(true);
			T t = readObject(reader);
			reader.close();
			return t;
		} catch (Exception e) {} 
		return null;
	}
	
	/**
	 * 解析带前缀的Json字符串
	 * @param jsonStr
	 * @return	List
	 * @throws Exception
	 */
	private List<T> readArrayJsonStr(String jsonStr) throws Exception {
		JsonReader reader = new JsonReader(new StringReader(jsonStr));
		reader.setLenient(true);
		try {
			return readArray(reader);
		} finally {
			reader.close();
		}
	}
	
	/**
	 * 解析不带前缀的Json字符串
	 * @param jsonStr
	 * @return	List
	 */
	private List<T> readArrayJsonNoTagStr(String jsonStr) {
		return  gson.fromJson(jsonStr,type);
	}
	
	/**
	 * 解析带前缀的Json流
	 * @param jsonStr
	 * @return	List
	 * @throws Exception
	 */
	private List<T> readArrayJsonStream(InputStream in) throws Exception {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readArray(reader);
		} finally {
			reader.close();
		}
	}
	
	/**
	 * 解析不带前缀的Json流
	 * @param jsonStr
	 * @return	List
	 */
	private List<T> readArrayJsonNoTagStream(InputStream in) throws Exception {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		return gson.fromJson(reader, type);
	}
	
	private T readObject(JsonReader reader) throws Exception {
		Object object = null;
		if(jsonTarget!=null){
			reader.beginObject();
			while (reader.hasNext()) {
				String parent = reader.nextName();
				if (parent.equals(jsonTarget)) {
					object = gson.fromJson(reader, cls);
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		}else{
			object = gson.fromJson(reader, cls);
		}
		return (T) object;
	}

	private List<T> readArray(JsonReader reader) throws Exception{
		List<T> list = new ArrayList<T>();
		reader.beginObject();
		while (reader.hasNext()) {
			String parent = reader.nextName();
			if(jsonTarget!=null){
				if (parent.equals(jsonTarget)) {
					list = gson.fromJson(reader, type);
				} else {
					reader.skipValue();
				}
			}else{
				list = gson.fromJson(reader, type);
			}
		}
		reader.endObject();
		return list;
	}
	
}
