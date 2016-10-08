package com.sunlianglong.volleystudy;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by sun liang long on 2016/8/31.
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String,Bitmap>mCache;

    public BitmapCache(){
        int maxSize = 10*1024*1024;
        mCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key,Bitmap bitmap){
                return bitmap.getRowBytes()*bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String s) {
        return null;
    }
    @Override
    public void putBitmap(String s, Bitmap bitmap) {

    }
}
