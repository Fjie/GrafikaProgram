package com.moyushot.moyu.page.material.edit

import android.media.MediaMetadataRetriever
import android.net.Uri
import me.fanjie.grafikaprogram.pages.trim_video.TrimVideoActivity
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.IllegalArgumentException

/**
 * 素材编辑，视频切割、画面裁剪
 */
class EditMaterialPresenter(val view: TrimVideoActivity) : EditMaterialContract.Presenter {


    /**
     * 查询媒体数据
     */
    override fun queryMetadata(uri: Uri) {
        val retriever = MediaMetadataRetriever()
        println("uri = ${uri}")
        retriever.setDataSource(view, uri)
        val videoSize = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        val videoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
        val videoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
        val videoRot = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION).toInt()

        if (videoSize <= 0 || videoHeight <= 0 || videoWidth <= 0) {
            throw IllegalArgumentException("获取视频信息失败")
        }

        if (videoRot == 90 || videoRot == 270) {
            /* 立起来的视频变化宽高 */
            view.queryMetadataDone(videoSize, videoHeight, videoWidth,uri)//先把信息返回
        } else {
            view.queryMetadataDone(videoSize, videoWidth, videoHeight,uri)//先把信息返回
        }
    }


    /**
     * 逐帧裁剪视频画面
     *     参数：源文件、x,y的平移、缩放
     */
    override fun trimVideo(src: File, posX: Float, posY: Float, scaleX: Float, scaleY: Float) {
        Single
                .create<String> {
                    trimVideoAsync(src, posX, posY, scaleX, scaleY, it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onTrimSuccess(it)
                }, {})
    }

    private fun trimVideoAsync(src: File, posX: Float, posY: Float, scaleX: Float, scaleY: Float, emitter: SingleEmitter<String>) {

//        val externalCacheDir = view.getContext().externalCacheDir
//        // 画面裁剪后的输出
//        val tempFile = ufCreateFileIfNotExist(externalCacheDir, "trimTemp.mp4")
//        // 合并原音轨的输出
//        val outFile = ufCreateFileIfNotExist(externalCacheDir, "trimOut.mp4")
//
//        val videoDrawer = VideoDrawer(src)
//        val filtersDrawer = FiltersDrawer(videoDrawer)
//        filtersDrawer.enableOutputTexture(false)
//        filtersDrawer.setFilter(TrimRecorderFilter(posX, posY, scaleX, scaleY))
//        val encoder = GLEncoder(tempFile, view.outVideoWidth, view.outVideoHeight, filtersDrawer)
//        encoder.init()//顺序不能乱
//        filtersDrawer.init(view.outVideoWidth, view.outVideoHeight)
//        videoDrawer.onDrawFrame()
//                .doOnNext {
//                    logD("draw frame  ${it}")
//                    encoder.onDrawFrame(it)
//                }
//                .doOnComplete {
//                    encoder.done()
//                    /* 检查是否包含音轨 */
//                    if (checkVideoHaveAudioTrack(src.absolutePath)) {
//                        val currentTimeMillis = System.currentTimeMillis()
//                        CSFFMpegManager.joinAudioSync(tempFile, src, outFile, transToTimeStr(getVideoDuration(src)))
//                        logD("join audio ${System.currentTimeMillis() - currentTimeMillis}")
//                        emitter.onSuccess(outFile.absolutePath)
//                    } else {
//                        emitter.onSuccess(tempFile.absolutePath)
//                    }
//
//                }
//                .subscribe()
//        videoDrawer.onDraw()
    }
}

