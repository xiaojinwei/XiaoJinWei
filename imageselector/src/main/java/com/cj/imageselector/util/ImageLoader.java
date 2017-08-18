package com.cj.imageselector.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类
 * Created by chenj on 2017/8/14.
 */

public class ImageLoader {

    private static ImageLoader sInstance;
    /**
     * 图片缓存的核心对象
     */
    private LruCache<String,Bitmap> mLruCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static final int DEFAULT_THREAD_COUNT = 1;

    /**
     * 队列调度方式
     */
    private Type mType = Type.LIFO;

    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;

    /**
     * 给任务队列发送消息
     */
    private Handler mPoolThreadHandler;

    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;

    /**
     * 信号量（并发量）
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    /**
     * 使用几个线程就控制当前有几个任务在执行，不然所有的Runnable都被放入到了ExecutorService所在的任务队列中了，
     * 就会导致这里使用的加载策略无效了，以为每来一个任务瞬间就会被加入到ExecutorService所在的任务队列中了。
     * 使用信号量可以ExecutorService所在的任务队列中最多只有threadCount个任务，执行完再从mTaskQueue中根据加载测量Type去取任务执行
     */
    private Semaphore mSemaphoreThreadPool;

    /**
     * 加载策略
     */
    public enum Type{
        FIFO,LIFO
    }

    private ImageLoader(int threadCount,Type type){
        //后台轮询线程
        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //从线程池取出一个任务进行执行
                        mThreadPool.execute(getTask());

                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        //获取我们应用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getRowBytes() * value.getHeight();
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;

        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 从任务队列取出一个方法
     * @return
     */
    private Runnable getTask() {
        if (mType == Type.LIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.FIFO) {
            return mTaskQueue.removeLast();
        }
        return mTaskQueue.removeFirst();
    }

    public static ImageLoader getInstance() {
        //使用两重判断，可以提高效率和一些问题
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader(DEFAULT_THREAD_COUNT,Type.FIFO);
                }
            }
        }
        return sInstance;
    }

    public static ImageLoader getInstance(int threadCount,Type type) {
        //使用两重判断，可以提高效率和一些问题
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader(threadCount,type);
                }
            }
        }
        return sInstance;
    }

    /**
     * 根据path为imageview设置图片
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //获取得到图片，为imageview回调设置图片
                    ImageHolder holder = (ImageHolder) msg.obj;
                    Bitmap bitmap = holder.bitmap;
                    ImageView imageView = holder.imageView;
                    String path = holder.path;
                    //将path与getTag存储路径进行比较，防止错位错乱
                    if (imageView.getTag().toString().equals(path)) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            };
        }

        Bitmap bitmap = getBitmapFromLruCache(path);

        if (bitmap != null) {
            refreshBitmapToImageView(path,imageView,bitmap);
        }else{
            addTask(new Runnable(){
                @Override
                public void run() {
                    //加载图片
                    //图片的压缩
                    //1.获得图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //2.压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path,imageSize.width,imageSize.height);
                    //3.将图片加入缓存
                    addBitmapToLruCache(path,bm);
                    //4.显示
                    refreshBitmapToImageView(path,imageView,bm);

                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    /**
     * 将获取到的图片添加到要显示的ImageView上
     * @param path
     * @param imageView
     * @param bitmap
     */
    private void refreshBitmapToImageView(String path,ImageView imageView,Bitmap bitmap) {
        Message obtain = Message.obtain();
        ImageHolder holder = new ImageHolder();
        holder.bitmap = bitmap;
        holder.imageView = imageView;
        holder.path = path;
        obtain.obj = holder;
        mUIHandler.sendMessage(obtain);
    }

    /**
     * 把图片加入到缓存
     * @param path
     * @param bm
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLruCache(path) == null) {
            if (bm != null) {
                addBitmapFromLruCache(path,bm);
            }
        }
    }

    /**
     * 根据图片需要显示的宽和高对图片进行压缩
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        //获取图片的宽和高，并不把图片加载到内存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options,width,height);
        //使用获取到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 获取图片的大小
     * 根据ImageView获取适当的压缩的宽和高
     * @param imageView
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();

        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        //获取imageview的实际宽度
        int width = imageView.getWidth();
        if (width <= 0) {
            //获取imageview在layout中声明的宽度
            width = lp.width;
        }
        if (width <= 0) {
            //检测是否设置了最大值
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                width = imageView.getMaxWidth();
            }else{
                width = getImageViewFieldValue(imageView,"mMaxWidth");
            }

        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        //获取imageview的实际高度
        int height = imageView.getHeight();
        if (height <= 0) {
            //获取imageview在layout中声明的宽度
            height = lp.height;
        }
        if (height <= 0) {
            //检测是否设置了最大值
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                height = imageView.getMaxHeight();
            }else{
                height = getImageViewFieldValue(imageView,"mMaxHeight");
            }
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    /**
     * 根据反射获取imageview的某个属性值
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if(mPoolThreadHandler == null) {
                //请求信号量（阻塞）
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 根据path在缓存中获取bitmap
     * @param key
     * @return
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * 将bitmap添加到缓存中
     * @param key
     * @param bitmap
     */
    private void addBitmapFromLruCache(String key, Bitmap bitmap) {
        mLruCache.put(key,bitmap);
    }

    private class ImageSize{
        int width;
        int height;
    }

    private class ImageHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
}
