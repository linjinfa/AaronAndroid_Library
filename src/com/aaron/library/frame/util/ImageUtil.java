package com.aaron.library.frame.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.ImageView;

/**
 * 图片操作
 * @author linjinfa 331710168@qq.com
 * @date 2014-4-15
 */
public class ImageUtil {
	
	 /**
     * 以最省内存的方式读取本地资源的图片
     * @param context
     * @param resId
     * @return
     */
	public static Bitmap readBitmap(Context context, int resId) {
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return readBitmap(context,is);
	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 * @param is
	 * @return
	 */
	public static Bitmap readBitmap(Context context, InputStream is) {
		if(is==null){
			return null;
		}
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	/**
	 * 读取assets中的图片
	 * @param context
	 * @param assetsPath
	 * @return
	 */
	public static Bitmap readAssetsBitmap(Context context, String assetsPath){
		try {
			InputStream in = context.getAssets().open(assetsPath);
			return readBitmap(context, in);
		} catch (IOException e) {}
		return null;
	}
	
	/**
	 * 读取assets中的图片
	 * @param context
	 * @param assetsPath
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap readAssetsBitmap(Context context, String assetsPath, int width, int height){
		try {
			InputStream in = context.getAssets().open(assetsPath);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, width, height);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeStream(in, null, options);
			return bm;
		} catch (IOException e) {}
		return null;
	}
	
	/**
	 * 防止Bitmap内存溢出
	 * @param filepath
	 * @param height 要缩放的高度
	 * @return
	 */
	public static Drawable getPhotoItem(String filepath, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();  
		options.inJustDecodeBounds = true;  
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options); //此时返回bm为空   
		options.inJustDecodeBounds = false;
		 //计算缩放比   
		int rate = (int)(options.outHeight / (float)height);
		if (rate <= 0)  
		    rate = 1;  
		options.inSampleSize = rate;  
//		重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap=BitmapFactory.decodeFile(filepath,options);  
		return new BitmapDrawable(bitmap);
	}
	
	/**
	 * 加载本地图片
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath,int width,int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, width, height);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
		if (bm == null) {
			return null;
		}
		int degree = readPictureDegree(filePath);
		bm = rotateBitmap(bm, degree);
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			CompressFormat format = null;
			if(filePath.toLowerCase().endsWith("jpg") || filePath.toLowerCase().endsWith("jpeg")){
				format = Bitmap.CompressFormat.JPEG;
			}else if(filePath.toLowerCase().endsWith("png")){
				format = Bitmap.CompressFormat.PNG;
			}else{
				format = Bitmap.CompressFormat.JPEG;
			}
			bm.compress(format, 100, baos);
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bm;
	}

	/**
	 * 旋转图片
	 * @param bitmap
	 * @param rotate
	 * @return
	 */
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
		if (bitmap == null)
			return null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	/**
	 * 加载图片的degree
	 * @param path
	 * @return
	 */
	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
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

	/**
	 * 计算inSampleSize值
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}
		return inSampleSize;
	}
	
	/**
	 * 图片本身加上圆角
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if(bitmap==null){
			return null;
		}
		return toRoundCorner(bitmap,pixels,bitmap.getWidth(),bitmap.getHeight());
	}
	
	/**
	 * 裁剪圆形图片
	 * @param bitmap
	 * @param pixels
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if(bitmap==null){
			return null;
		}
		int round = Math.min(bitmap.getWidth(), bitmap.getHeight());
		int left = bitmap.getWidth()>bitmap.getHeight()?(bitmap.getWidth()-round)/2:0;
		int top = bitmap.getWidth()>bitmap.getHeight()?0:(bitmap.getWidth()-round)/2;
		int right = left+round;
		int bottom = top+round;
		
		Bitmap output = Bitmap.createBitmap(right-left, bottom-top, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		
		int color = 0xff424242;
		paint.setColor(color);
		
		Rect rect = new Rect(0, 0, round, round);
		RectF rectF = new RectF(rect);
		
		canvas.drawRoundRect(rectF, round/2, round/2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, -left, -top, paint);
		
		return output;
	}
	
	/**
	 * 图片本身加上圆角
	 * @param bitmap
	 * @param pixels
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels, int width, int height) {
		if(bitmap==null){
			return null;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, width, height);
		RectF rectF = new RectF(rect);
		float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/**
	 * 图片加上圆形背景
	 * @param bitmap
	 * @param bgColor	背景颜色
	 * @return
	 */
	public static Bitmap circleBgBitmap(Bitmap bitmap,int bgColor){
		final int SPACE = 5;
		final int width = bitmap.getWidth()+SPACE*2;
		final int height = bitmap.getHeight()+SPACE*2;
		Bitmap output = Bitmap.createBitmap(width,height, Config.ARGB_8888);
		Rect rect = new Rect(SPACE, SPACE, width-SPACE, height-SPACE);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(bgColor);
		canvas.drawCircle(width/2, height/2, width/2, paint);
		canvas.drawBitmap(bitmap, null, rect, paint);
		return output;
	}
	
	/**
	 * 绘制热点图标
	 * @param context
	 * @param topBitmap
	 * @param txt
	 * @return
	 */
	public static Bitmap circleBgBitmapTxt(Context context,Bitmap topBitmap,String txt){
		//文字总长度
		final int txtLength = 7;
		//图标宽度
		final int width = 100;
		//图标高度
		final int height = 100;
		
		//顶部图标宽度
		final int bmWidth = 52;
		//顶部图标高度
		final int bmHeight = 38;
		
		final int bitmapBottom = 46;
		final int bitmapLeft = (width-bmWidth)/2;
		final int bitmapTop = bitmapBottom - bmHeight;
		
		Bitmap output = Bitmap.createBitmap(width,height, Config.ARGB_8888);
		Rect rect = new Rect(bitmapLeft, bitmapTop, width-bitmapLeft, bitmapBottom);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		paint.setAlpha(180);
		canvas.drawCircle(width/2, height/2, width/2, paint);
		canvas.drawBitmap(topBitmap, null, rect, null);
		
		TextPaint textPaint = new TextPaint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(DensityUtil.px2sp(context, 28));
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Align.CENTER);

		if(txt==null)
			txt = "";
		StringBuffer sb = new StringBuffer(txt);
		int topSpace = 7;
		if(txt.length()>4){
			sb.insert(4, "\r\n");
			topSpace = 0;
			sb.setLength(txtLength);
		}
		StaticLayout layout = new StaticLayout(sb.toString().trim(), textPaint, 100,Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
		canvas.save();
		canvas.translate(width/2, height/2+topSpace);
		layout.draw(canvas);
		canvas.restore();
		return output;
	}
	
	/**
	 * Drawable转成Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {  
        // 取 drawable 的长宽  
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight(); 
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;  
        // 建立对应 bitmap  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);  
        // 把 drawable 内容画到画布中  
        drawable.draw(canvas);
        return bitmap;
    }
	
	/**
	 * 获得带倒影的图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {  
	    final int reflectionGap = 4;  
	    int w = bitmap.getWidth();  
	    int h = bitmap.getHeight();  
	  
	    Matrix matrix = new Matrix();  
	    matrix.preScale(1, -1);  
	  
	    Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,  
	            h / 2, matrix, false);  
	  
	    Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),  
	            Config.ARGB_8888);  
	  
	    Canvas canvas = new Canvas(bitmapWithReflection);  
	    canvas.drawBitmap(bitmap, 0, 0, null);  
	    Paint deafalutPaint = new Paint();  
	    canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);  
	  
	    canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);  
	  
	    Paint paint = new Paint();  
	    LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);  
	    paint.setShader(shader);  
	    // Set the Transfer mode to be porter duff and destination in  
	    paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
	    // Draw a rectangle using the paint with our linear gradient  
	    canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()  
	            + reflectionGap, paint);  
	    return bitmapWithReflection;  
	}
	
	/**
	 * 绘制带文字背景的Bitmap
	 * @param txt
	 * @return
	 */
	public static Bitmap getbgTxtBitmap(Context context,String txt,int resId){
		Bitmap bitmap = readBitmap(context, resId);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(width,height, Config.ARGB_8888);
		
		Canvas canvas = new Canvas(output);
		canvas.drawBitmap(bitmap, 0, 0, null);
		
		Paint txtPaint = new Paint();
		txtPaint.setAntiAlias(true);
		txtPaint.setColor(Color.WHITE);
		txtPaint.setStrokeWidth(15);
		txtPaint.setTextAlign(Align.CENTER);
		txtPaint.setTextSize(17);
		
		canvas.drawText(txt, width/2, height/2, txtPaint);
		return output;
	}
	
	/**
	 * Bitmap转成byte	可能所占空间会变大
	 * @param bm
	 * @return
	 */
	public static byte[] BitmapToBytes(Bitmap bm) {
		return BitmapToBytes(bm,"png");
	}
	
	/**
	 * Bitmap转成byte	可能所占空间会变大
	 * @param bm
	 * @param suffix	图片后缀
	 * @return
	 */
	public static byte[] BitmapToBytes(Bitmap bm,String suffix) {
		return BitmapToBytes(bm,suffix,100);
	}
	
	/**
	 * Bitmap转成byte	
	 * @param bm
	 * @param suffix
	 * @param quality	图片质量	0~100 
	 * @return
	 */
	public static byte[] BitmapToBytes(Bitmap bm,String suffix,int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap.CompressFormat format = null;
		if(suffix==null || (suffix!=null && suffix.length()==0)){
			format = Bitmap.CompressFormat.JPEG;
		}else if(suffix.toLowerCase().endsWith("jpg") || suffix.toLowerCase().endsWith("jpeg")){
			format = Bitmap.CompressFormat.JPEG;
		}else if(suffix.toLowerCase().endsWith("png")){
			format = Bitmap.CompressFormat.PNG;
		}else{
			format = Bitmap.CompressFormat.JPEG;
		}
		bm.compress(format, quality, baos);
		return baos.toByteArray();
	}
	
	/**
	 * 回收imageview的bitmap,非background bitmap
	 * @param imageview
	 * */
	public static void recycleImageViewBitmap(ImageView imageview){
		if(imageview != null && imageview.getDrawable() != null){
			BitmapDrawable drawable = (BitmapDrawable)imageview.getDrawable();
			if(drawable != null && drawable.getBitmap() != null){
				drawable.getBitmap().recycle();
			}
			imageview.setImageBitmap(null);
		}
	}
}
