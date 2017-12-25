cmake_minimum_required(VERSION 3.4.1)

add_library( mylib

             SHARED

             src/main/cpp/main.c

             src/main/cpp/ffmpeg_cmd/ffmpeg.c
             src/main/cpp/ffmpeg_cmd/ffmpeg_opt.c
             src/main/cpp/ffmpeg_cmd/ffmpeg_filter.c
             src/main/cpp/ffmpeg_cmd/cmdutils.c )

add_library( avcodec    SHARED  IMPORTED )
add_library( avfilter    SHARED  IMPORTED )
add_library( avformat   SHARED  IMPORTED )
add_library( avutil     SHARED  IMPORTED )
add_library( swresample  SHARED  IMPORTED )
add_library( swscale     SHARED  IMPORTED )
add_library( postproc    SHARED  IMPORTED )

set_target_properties( avcodec    PROPERTIES  IMPORTED_LOCATION  ../../../../src/main/jniLibs/armeabi-v7a/libavcodec.so    )
set_target_properties( avfilter    PROPERTIES  IMPORTED_LOCATION  ../../../../src/main/jniLibs/armeabi-v7a/libavfilter.so   )
set_target_properties( avformat   PROPERTIES  IMPORTED_LOCATION  ../../../../src/main/jniLibs/armeabi-v7a/libavformat.so   )
set_target_properties( avutil     PROPERTIES  IMPORTED_LOCATION  ../../../../src/main/jniLibs/armeabi-v7a/libavutil.so     )
set_target_properties( swresample  PROPERTIES  IMPORTED_LOCATION  ../../../../src/main/jniLibs/armeabi-v7a/libswresample.so )
set_target_properties( swscale     PROPERTIES  IMPORTED_LOCATION  ../../../../src/main/jniLibs/armeabi-v7a/libswscale.so    )
set_target_properties( postproc     PROPERTIES  IMPORTED_LOCATION  ../../../../src/main/jniLibs/armeabi-v7a/libpostproc.so    )

include_directories( src/main/jniLibs/include )

find_library( log-lib
              log )

target_link_libraries( mylib

                       avcodec
                       avfilter
                       avformat
                       avutil
                       swresample
                       swscale
                       postproc

                       ${log-lib} )
