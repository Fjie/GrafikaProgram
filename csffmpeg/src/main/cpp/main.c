#include <jni.h>
#include "log.h"
#include "ffmpeg_cmd/ffmpeg.h"
#include "libavcodec/jni.h"


JNIEXPORT jint JNICALL
Java_com_xfast_ffmpegdemo_FFMpegEntry_ffmpegCmd(JNIEnv *env, jobject object/* this */, jobjectArray commands) {
    //FFmpeg av_log() callback
    int argc = (*env)->GetArrayLength(env, commands);
    char *argv[argc];
    LOGE("---------Kit argc %d\n", argc);
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env, js, 0);
        LOGE("-------------Kit argv %s\n", argv[i]);
    }
    return main(argc, argv);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    av_jni_set_java_vm(vm, NULL);
    return JNI_VERSION_1_6;
}


