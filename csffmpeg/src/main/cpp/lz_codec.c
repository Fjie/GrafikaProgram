#include <jni.h>
#include <libavcodec/avcodec.h>
#include <libavutil/imgutils.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include "ffmpeg_cmd/ffmpeg.h"
#include "log.h"

// 将帧数据写入文件
void SaveFrame(AVFrame *pFrame, int width, int height, int iFrame) {
    FILE *pFile;
    char szFilename[32];

    //打开文件
    sprintf(szFilename, "frame%02d.ppm", iFrame);
    pFile = fopen(szFilename, "wb");
    if (!pFile) {
        LOGE("fopen");
        return;
    }

    //写入头信息
    fprintf(pFile, "P6\n%d %d\n255\n", width, height);

    //写入像素数据
    for (int y = 0; y < height; ++y) {
        fwrite(pFrame->data[0]+y*pFrame->linesize[0], 1, (size_t) (width * 3), pFile);
    }

    //关闭文件
    fclose(pFile);
}

// todo LOGE 的地方带上返回值, 排错时可以比对 AVERROR
int foo() {
    //注册所有 Formats & Codecs
    av_register_all();

    //打开文件, 并获取头信息
    char *url = "test.mp4";
    AVFormatContext *pFormatCtx = NULL; // 储存.文件.头信息
    if (avformat_open_input(&pFormatCtx, url, NULL, NULL) != 0) { //后两个参数设为 NULL 将交由系统检测
        LOGE("avformat_open_input");
        return -1;
    }

    //读取流信息(每个流都有各自的信息头, 比如视频流, 音频流...), 存入 pFormatCtx->streams
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        LOGE("avformat_find_stream_info");
        return -1;
    }

    //在 STD_ERROR 频道打印流信息.
    av_dump_format(pFormatCtx, 0, url, 0);

    //选中视频流
    int indexOfVideoStream = -1;
    AVCodecParameters *pCodecParams = NULL; //存储.流.编解码信息(Original, 只读的)
    for (int i = 0; i < pFormatCtx->nb_streams; ++i) { //nb_ : number
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            pCodecParams = pFormatCtx->streams[i]->codecpar;
            indexOfVideoStream = i;
            break;
        }
    }
    if (!pCodecParams) {
        LOGE("no_video_stream");
        return -1;
    }

    //启用解码器
    //谢绝修改从视频流读取的数据, 创建一个备份作为容器.
    AVCodec *pCodec = NULL;
    pCodec = avcodec_find_decoder(pCodecParams->codec_id); //获取解码器
    if (!pCodec) {
        LOGE("avcodec_find_decoder");
        return -1;
    }
    AVCodecContext *pCodecCtx = avcodec_alloc_context3(pCodec); //存储.流.编解码信息(当做容器用的)
    if (avcodec_parameters_to_context(pCodecCtx, pCodecParams) < 0) { //备份解码器信息
        LOGE("avcodec_parameters_from_context");
        return -1;
    }
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) { //启用解码器
        LOGE("avcodec_open2");
        return -1;
    }

    //创建每帧数据的容器
    AVFrame *pFrame = NULL;
    pFrame = av_frame_alloc();
    if (!pFrame) {
        LOGE("av_frame_alloc");
        return -1;
    }

    //转换数据格式到PPM(24-bit,RGB)
    AVFrame *pFrameRGB = NULL;
    pFrameRGB = av_frame_alloc();
    if (!pFrameRGB) {
        LOGE("av_frame_alloc");
        return -1;
    }

    //创建原始数据缓冲区
    uint8_t *buffer = NULL;
    int numBytes = av_image_get_buffer_size(
            AV_PIX_FMT_RGB24,
            pCodecCtx->width,
            pCodecCtx->height,
            1);
    buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));

    //关联每帧数据与缓冲区
    av_image_fill_arrays(
            pFrame->data,
            pFrame->linesize,
            buffer, AV_PIX_FMT_RGB24,
            pCodecCtx->width,
            pCodecCtx->height,
            1);

    //读取数据
    struct SwsContext *sws_ctx = NULL;
    AVPacket packet;
    sws_ctx = sws_getContext( //软缩放的信息容器
            pCodecCtx->width,
            pCodecCtx->height, //原始宽高
            pCodecCtx->pix_fmt,
            pCodecCtx->width,  //目标宽高
            pCodecCtx->height,
            AV_PIX_FMT_RGB24,
            SWS_BILINEAR,
            NULL,
            NULL,
            NULL);
    int indexOfFrame = 0;
    while (av_read_frame(pFormatCtx, &packet) >= 0) { //逐帧读取(demux)
        if (packet.stream_index == indexOfVideoStream) { //判定是否为视频文件
            if (avcodec_send_packet(pCodecCtx, &packet) != 0) { //解码(decode)
                LOGE("avcodec_send_packet");
                return -1;
            }
            while (1) { //一个包数据可能包含多个帧
                int result = avcodec_receive_frame(pCodecCtx, pFrame); //获取一帧原始数据
                if (result != 0) {
                    if (result == AVERROR(EAGAIN) || result == AVERROR_EOF) //当前包已读取完成
                        break;
                    else {
                        LOGE("avcodec_receive_frame");
                        return -1;
                    }
                }
                sws_scale( //将帧图片转成RGB格式
                        sws_ctx,
                        (uint8_t const *const *) pFrame->data,
                        pFrame->linesize,
                        0,
                        pCodecCtx->height,
                        pFrameRGB->data,
                        pFrameRGB->linesize);
                SaveFrame(pFrameRGB, pCodecCtx->width, pCodecCtx->height, ++indexOfFrame);//消费数据.
            }
        }
    }

    //释放所有资源
    av_packet_unref(&packet);
    av_free(buffer);
    av_free(pFrameRGB);
    av_free(pFrame);
    avcodec_close(pCodecCtx);
    avcodec_parameters_free(&pCodecParams);
    avformat_close_input(&pFormatCtx);
    return 0;
}

