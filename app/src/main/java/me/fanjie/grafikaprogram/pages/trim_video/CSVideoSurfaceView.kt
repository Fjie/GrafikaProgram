package com.moyushot.moyu.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.ScaleGestureDetector
import android.view.Surface
import com.coshow.gldemo.util.graphic.gles.MatrixScaleProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 代替VideoView使用，可以缩放平移视频内容的SurfaceView
 *     保留对视频缩放、平移的矩阵参数对外提供
 *     拖动回弹效果
 *     判断原视频是否需要裁剪
 */
class CSVideoSurfaceView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private var mPlayer: MediaPlayer? = null

    /** 平移自己搞 */
    /* 计算后最终平移坐标(像素值) */
    private var mPosX = 0f
    private var mPosY = 0f
    /* 平移手势相关 */
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mActivePointerId = INVALID_POINTER_ID

    /** 缩放手势拿现成的 */
    private val mScaleDetector = ScaleGestureDetector(context, ScaleListener())
    /* 缩放原始倍数 */
    private var mScaleFactor = 1f
    /* 经过换算的XY缩放倍数（匹配上视频宽高比、渲染View宽高比）*/
    private var mScaleX: Float = 1f
    private var mScaleY: Float = 1f
    /* 视频原宽高，外面传 */
    private var videoWidth = 1
    private var videoHeight = 1
    /* 经过缩放计算后的实际视频宽高 */
    private var rVideoWidth: Float = 0f
    private var rVideoHeight: Float = 0f

    private var mRenderer: VideoRender? = null

    private val mMVPMatrix = FloatArray(16)
    private val mSTMatrix = FloatArray(16)

    init {
        setEGLContextClientVersion(2)
        mRenderer = VideoRender()
        setRenderer(mRenderer)
    }

    /** 原视频需要裁剪的各类条件，视频比例不满足aspectRatio，有无平移缩放 */
    fun checkNeedTrim(aspectRatio: Float): Boolean {


        return rVideoWidth / rVideoHeight != aspectRatio ||
                getMatrixScaleX() != 1f ||
                getMatrixScaleY() != 1f ||
                mPosX != 0f ||
                mPosY != 0f
    }

    fun getMatrixTranslateX() = 2 * mPosX / width

    fun getMatrixTranslateY() = 2 * mPosY / -height

    fun getMatrixScaleX() = rVideoWidth / width

    fun getMatrixScaleY() = rVideoHeight / height

    fun setVideoSize(width: Int, height: Int) {
        videoWidth = width
        videoHeight = height
    }

    fun start(player: MediaPlayer) {
        mPlayer = player
        queueEvent { mRenderer?.setMediaPlayer(mPlayer) }
    }


    /** 手势，当平移、缩放事件触发的时候更新矩阵 doMatrix */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev)
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y

                mLastTouchX = x
                mLastTouchY = y
                mActivePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)
                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress) {
                    val dx = x - mLastTouchX
                    val dy = y - mLastTouchY
                    mPosX += dx
                    mPosY += dy
                }
                mLastTouchX = x
                mLastTouchY = y
                doMatrix()
            }

            MotionEvent.ACTION_UP -> {
                checkTranslationBounds()
                mActivePointerId = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    /** 检查平移的边界，如果平移超出边界则还原 */
    private fun checkTranslationBounds() {
        if (!mScaleDetector.isInProgress) {
            /* 给到surface渲染队列，否则线程操作冲突有点诡异*/
            queueEvent {
                /* 计算边界，以真实视频宽高计算 */
                val xBounds = (rVideoWidth - width) / 2
                val yBounds = (rVideoHeight - height) / 2
                mPosX = if (xBounds > 0) {
                    if (mPosX < -xBounds) -xBounds else if (mPosX > xBounds) xBounds else mPosX
                } else 0f
                mPosY = if (yBounds > 0) {
                    if (mPosY < -yBounds) -yBounds else if (mPosY > yBounds) yBounds else mPosY
                } else 0f
                doMatrix()
            }
        }
    }

    /** 矩阵操作，平移缩放 */
    private fun doMatrix() {
        Matrix.setIdentityM(mMVPMatrix, 0)
        Matrix.translateM(mMVPMatrix, 0, getMatrixTranslateX(), getMatrixTranslateY(), 0f)
        /* 还原视频原比例，分别求出XY缩放比例 */
        val viewRadio = width.toDouble() / height.toDouble()
        val videoRadio = videoWidth.toDouble() / videoHeight.toDouble()
        mScaleX = (mScaleFactor * videoRadio / viewRadio).toFloat() * width / videoWidth.toFloat()
        mScaleY = mScaleFactor * height / videoHeight.toFloat()
        /* 计算真实视频宽高 */
        rVideoWidth = mScaleX * videoWidth
        rVideoHeight = mScaleY * videoHeight
        /* 矩阵缩放，根据真实视频宽高与View宽高比例 */
        Matrix.scaleM(mMVPMatrix, 0, getMatrixScaleX(), getMatrixScaleY(), 1f)
    }

    /** 拿现成的处理缩放 */
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            // 设置个最大最小值
            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 1.5f))
            /* 放队列做矩阵操作防止异步问题 */
            queueEvent {
                doMatrix()
            }
            return true
        }
    }


    private inner class VideoRender : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
        private val TAG = "VideoRender"

        private var mSurface: SurfaceTexture? = null
        private var updateSurface = false
        // 封装一些OpenGL操作
        private val mGLProgram = MatrixScaleProgram()
        private var mMediaPlayer: MediaPlayer? = null

        init {
            Matrix.setIdentityM(mSTMatrix, 0)
        }

        fun setMediaPlayer(player: MediaPlayer?) {
            mMediaPlayer = player
        }

        override fun onDrawFrame(glUnused: GL10) {
            synchronized(this) {
                if (updateSurface) {
                    mSurface!!.updateTexImage()
                    /* 根据视频方向变化纹理矩阵，很关键 */
                    mSurface!!.getTransformMatrix(mSTMatrix)
//                    println("mSTMatrix = ${Arrays.toString(mSTMatrix)}")
                    updateSurface = false
                }
            }
            mGLProgram.draw(mMVPMatrix, mSTMatrix)
        }


        // onResume的时候好像会调用，然后MediaPlay又没销毁，搞的mMediaPlayer?.prepare()崩溃
        override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {

            mGLProgram.create()
            // 拿出OpenGL的纹理ID创建一个surface
            mSurface = SurfaceTexture(mGLProgram.mTextureID)
            mSurface!!.setOnFrameAvailableListener(this)

            val surface = Surface(mSurface)
            mMediaPlayer!!.setSurface(surface)
            mMediaPlayer!!.setScreenOnWhilePlaying(true)
            surface.release()

            try {
                mMediaPlayer?.prepare()
            } catch (e: Exception) {
                Log.e(TAG, "media player prepare failed",e)
            }
            synchronized(this) {
                updateSurface = false
            }
            mMediaPlayer!!.start()
            doMatrix()
        }

        @Synchronized
        override fun onFrameAvailable(surface: SurfaceTexture) {
            updateSurface = true
        }

        override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {

        }

    }

}
