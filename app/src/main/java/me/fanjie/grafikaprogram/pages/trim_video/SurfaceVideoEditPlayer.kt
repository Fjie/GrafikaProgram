package me.fanjie.grafikaprogram.pages.trim_video;

import android.media.MediaPlayer
import android.net.Uri
import com.moyushot.moyu.widgets.CSVideoSurfaceView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/** 带裁剪的播放器包装 */
class SurfaceVideoEditPlayer(private val videoView: CSVideoSurfaceView) {

    var seekStart = 0
    var seekRange = 0
    //用来实现选取框内循环播放的时钟
    private var timerDisposable: Disposable? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var uri: Uri? = null
    private var inPlay = false

    fun play(uri: Uri) {
        this.uri = uri
        initMediaPlayer()
    }


    fun onResume() {
        if (!inPlay) {
            initMediaPlayer()
        }
        //这里需要先重置播放器再重新还原surfaceView
        videoView.onResume()
    }

    fun onPause() {
        videoView.onPause()
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
        inPlay = false
    }

    fun setRange(start: Int, range: Int) {
        seekStart = start
        seekRange = range
        replay()
    }

    private fun initMediaPlayer() {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setDataSource(videoView.context, uri)
        videoView.start(mMediaPlayer!!)
        inPlay = true
    }

    private fun replay() {
        timerDisposable?.dispose()
        timerDisposable = Observable.interval(0, seekRange.toLong(), TimeUnit.MILLISECONDS, Schedulers.newThread())
                .subscribe {
                    mMediaPlayer?.seekTo(seekStart)
                    mMediaPlayer?.start()
                }
    }
}