package com.moyushot.moyu.page.material.edit

import android.net.Uri
import java.io.File

/**
 * Created by fanjie on 2017/8/14.
 */
interface EditMaterialContract {


    interface View {
        fun queryMetadataDone(videoSize: Int, videoWidth: Int, videoHeight: Int, uri: Uri)
        fun onTrimSuccess(path: String)
    }

    interface Presenter {
        fun queryMetadata(uri: Uri)
        /**
         * 裁剪视频，传参分别为裁剪矩阵的平移参数和缩放参数
         */
        fun trimVideo(src: File, posX: Float, posY: Float, scaleX: Float, scaleY: Float)
    }


}