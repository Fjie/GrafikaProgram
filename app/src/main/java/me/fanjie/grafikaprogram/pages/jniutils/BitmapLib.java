package me.fanjie.grafikaprogram.pages.jniutils;

import android.graphics.Bitmap;

/**
 * Created by fanjie on 2017/12/22.
 */

public class BitmapLib {

    static {
        System.loadLibrary("native-bitmap-lib");
    }

    public static native void blurBitmap(Bitmap bitmap, int r);
}
