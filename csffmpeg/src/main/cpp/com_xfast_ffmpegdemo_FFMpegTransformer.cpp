#include "com_xfast_ffmpegdemo_FFMpegTransformer.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "log.h"

/*
 * Class:     com_xfast_ffmpegdemo_FFMpegTransformer
 * Method:    pts
 * Signature: (Ljava/lang/String;Ljava/lang/String;F)I
 */
JNIEXPORT jint JNICALL Java_com_xfast_ffmpegdemo_FFMpegTransformer_pts
        (JNIEnv *env, jclass cls, jstring from, jstring dest, jfloat pts) {
    // pts(String from, String dest, float pts)
}

/*
 * Class:     com_xfast_ffmpegdemo_FFMpegTransformer
 * Method:    overlay
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_xfast_ffmpegdemo_FFMpegTransformer_overlay
        (JNIEnv *env, jclass cls, jstring left, jstring right, jstring output) {
    // overlay(String left, String right, String output)
}

/*
 * Class:     com_xfast_ffmpegdemo_FFMpegTransformer
 * Method:    concat
 * Signature: ([Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_xfast_ffmpegdemo_FFMpegTransformer_concat
        (JNIEnv *env, jclass cls, jobjectArray input, jstring output) {
    // concat(String[] input, String output)
}

/*
 * Class:     com_xfast_ffmpegdemo_FFMpegTransformer
 * Method:    gif
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_xfast_ffmpegdemo_FFMpegTransformer_gif
        (JNIEnv *env, jclass cls, jstring input, jstring output) {
    // gif(String input, String output)
}

/*
 * Class:     com_xfast_ffmpegdemo_FFMpegTransformer
 * Method:    split
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_xfast_ffmpegdemo_FFMpegTransformer_split
        (JNIEnv *env, jclass cls, jstring input, jstring output, jstring timeStart, jstring timeEnd) {
    // split(String input, String output, String timeStart, String timeEnd)
}