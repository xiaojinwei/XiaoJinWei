package com.cj.chenj.recyclerview_lib.glide;


import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cj.chenj.recyclerview_lib.glide.glide.GlideCircleTransform;
import com.cj.chenj.recyclerview_lib.glide.glide.GlideRoundTransform;


public class ImageUtil {
	
	/**
	 * 设置圆形图片
	 * @param imageView
	 * @param url
	 */
	public static void setCircleImage(final ImageView imageView,String url){
		setCircleImage(imageView,url,-1,true);
	}
	/**
	 * 设置圆形图片
	 * @param imageView
	 * @param url
	 */
	public static void setCircleImage(final ImageView imageView,String url,boolean isCache){
		setCircleImage(imageView,url,-1,isCache);
	}


	/**
	 * 设置圆形图片
	 * @param imageView
	 * @param url
	 * @param defRes
	 * @param isCache  是否缓存
     */
	public static void setCircleImage(final ImageView imageView,final String url,final int defRes,boolean isCache){
		Glide
        .with(imageView.getContext())
        .load(url)
//        .transform(new GlideRoundTransform(mContext, 28))
        .transform(new GlideCircleTransform(imageView.getContext()))
//        .centerCrop() //这个会影响transform
        .placeholder(defRes)
        .error(defRes)
        .crossFade() //淡入淡出
		.skipMemoryCache(isCache)
		.diskCacheStrategy(isCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
        .into(imageView);
	}

	
	/**
	 * 设置圆角图片
	 * @param imageView
	 * @param url
	 */
	public static void setRoundImage(final ImageView imageView,String url){
		setRoundImage(imageView,url,-1,true);
	}
	/**
	 * 设置圆角图片
	 * @param imageView
	 * @param url
	 */
	public static void setRoundImage(final ImageView imageView,String url,boolean isCache){
		setRoundImage(imageView,url,-1,isCache);
	}
	/**
	 * 设置圆形图片
	 * @param imageView
	 * @param url
	 * @param defaultRes
	 * @param isCache  是否缓存
	 */
	public static void setRoundImage(final ImageView imageView,final String url,final int defaultRes,boolean isCache){
		
		//System.out.println("----------------------setRoundImage:url:"+url);
		
		Glide
        .with(imageView.getContext())
        .load(url)
//        .transform(new GlideRoundTransform(mContext, 28))
        .transform(new GlideRoundTransform(imageView.getContext()))
//        .centerCrop() //这个会影响transform
        .placeholder(defaultRes)
        .error(defaultRes)
        .crossFade()
		.skipMemoryCache(isCache)
		.diskCacheStrategy(isCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
        .into(imageView);
	}
	/**
	 * 设置图片
	 * @param imageView
	 * @param url
	 */
	public static void setImage(final ImageView imageView,String url){
		setImage(imageView,url,-1,true);
	}

	/**
	 * 设置图片
	 * @param imageView
	 * @param url
	 */
	public static void setImage(final ImageView imageView,String url,boolean isCache){
		setImage(imageView,url,-1,isCache);
	}
	/**
	 * 设置图片
	 * @param imageView
	 * @param url
	 */
	public static void setImage(final ImageView imageView,final String url,final int defaultRes,boolean isCache){

		//System.out.println("----------------------setRoundImage:url:"+url);

		Glide
				.with(imageView.getContext())
				.load(url)
				.placeholder(defaultRes)
				.error(defaultRes)
				.crossFade()
				.skipMemoryCache(isCache)
				.diskCacheStrategy(isCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
				.into(imageView);
	}
	
	
//	public static void setImageFromSD(String filePath,ImageView imageView){
//		filePath=filePath.replace(" ", "");
//		Util.displayImageSD(filePath, imageView);
//	}
	
	
	
//	/**
//     * 从SD卡中得到图片
//     * @param filepath
//     * @return
//     */
//    public static Bitmap getBitmapFromSD(String filePath){
//        try{
//            filePath=filePath.replace(" ", "");
//            File f=new File(filePath);
//            if(f.exists()){
//                Bitmap bm=BitmapFactory.decodeFile(filePath);
//                return  bm; 
//            }
//            else{
//                return null;
//            }
//        }
//        catch(Exception ex){
//            ex.printStackTrace();
//            return null;
//        }
//    }
}
