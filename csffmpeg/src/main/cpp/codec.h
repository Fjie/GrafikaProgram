#include <libavcodec/avcodec.h>
#include <libavutil/imgutils.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include "ffmpeg_cmd/ffmpeg.h"
#include <sys/msg.h>
#include <stdlib.h>
#include "log.h"

// decode
int decode_video(char *url, int (*handleFrame)(AVFrame));
