package com.xfast.ffmpegdemo;

public class FFMpegTransformer {
    static {
        // ffmpeg begin
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
        System.loadLibrary("avdevice");
        System.loadLibrary("postproc");
        // end

        System.loadLibrary("mylib");
    }

    // cd /Users/lizhe/AndroidStudioProjects/Coshow/csffmpeg/src/main/java
    // javah com.xfast.ffmpegdemo.FFMpegTransformer


//    public static native int player(Object surface);
    public static native int pts(String from, String dest, float pts);
    public static native int overlay(String left, String right, String output);
    public static native int concat(String[] input, String output);
    public static native int gif(String input, String output);
    public static native int split(String input, String output, String timeStart, String timeEnd);
}
