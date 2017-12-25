package com.xfast.ffmpegdemo;

public class FFMpegEntry {
    static {
        // ffmpeg begin
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
        System.loadLibrary("postproc");
        // end

        System.loadLibrary("mylib");
    }

    public static native int ffmpegCmd(String[] args);
    public static int FFMpegCmd(String cmd) { return ffmpegCmd(cmd.split(" ")); }
}
