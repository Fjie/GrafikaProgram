#include <libavcodec/avcodec.h>
#include <libavutil/imgutils.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include "ffmpeg_cmd/ffmpeg.h"
#include <sys/msg.h>
#include <stdlib.h>
#include "log.h"
#include "waitqueue.c"

#define handle_error() { LOGE("ffmpeg error: %d", result); return result; }
#define handle_null(var_name) { LOGE("ffmpeg null: %s", var_name); return 1; }

// decode
int decode_video(char *url, int (*handleFrame)(AVFrame)) {
    //开始拆包
    AVFormatContext *fmt_ctx;
    av_register_all();
    //打开文件, 并获取头信息
    int result = avformat_open_input(&fmt_ctx, url, NULL, NULL); //后两个参数设为 NULL 将交由系统检测
    if (result != 0) handle_error()
    //获取流信息
    result = avformat_find_stream_info(fmt_ctx, NULL);
    if (result < 0) handle_error()
    //在 STD_ERROR 频道打印流信息.
    av_dump_format(fmt_ctx, 0, url, 0);
    //开始解码
    int index_video_stream = 0;
    AVCodecContext *codec_ctx;
    AVCodecParameters *dec_params = NULL;   //视频数据编码信息
    //选中视频流
    for (int i = 0; i < fmt_ctx->nb_streams; ++i) { //nb_ : number
        if (fmt_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            dec_params = fmt_ctx->streams[i]->codecpar;
            index_video_stream = i;
            break;
        }
    }
    if (!dec_params) handle_null("AVCodecParameters")
    //启用解码器
    AVCodec *dec = avcodec_find_decoder(dec_params->codec_id);
    if (!dec) handle_null("AVCodec")
    codec_ctx = avcodec_alloc_context3(dec);
    if (!codec_ctx) handle_null("AVCodecContext")
    result = avcodec_parameters_to_context(codec_ctx, dec_params);
    if (result < 0) handle_error()
    result = avcodec_open2(codec_ctx, dec, NULL);
    if (result < 0) handle_error()
    //获取每帧数据(YUV格式)
    AVPacket packet;
    while ((result = av_read_frame(fmt_ctx, &packet)) >= 0) {
        AVFrame *frame = av_frame_alloc();
        if (!frame) handle_null("AVFrame");
        if (packet.stream_index == index_video_stream) {
            result = avcodec_send_packet(codec_ctx, &packet);
            // todo 向解码器发包之后, 会有解码时间, 如果有 GOP, 时间估计不短, 应该做成监听回调之类的模式?
            if (result < 0) handle_error()
            while (1) {
                result = avcodec_receive_frame(codec_ctx, frame);
                if (result != 0) {
                    if (result == AVERROR(EAGAIN) || result == AVERROR_EOF) break;
                    else handle_error()
                }
                handleFrame(*frame);
            }
        }
    }
    if (result != AVERROR_EOF) handle_error()
    //释放所有资源
    av_packet_unref(&packet);
    avcodec_close(codec_ctx);
    avcodec_parameters_free(&dec_params);
    avformat_close_input(&fmt_ctx);
    return 0;
}

// 编码
int step3_encode() {

}

// 装包
int step4_mux() {

}
