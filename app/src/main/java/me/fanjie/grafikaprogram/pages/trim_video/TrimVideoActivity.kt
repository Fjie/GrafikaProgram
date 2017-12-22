package me.fanjie.grafikaprogram.pages.trim_video

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moyushot.moyu.page.material.edit.EditMaterialContract
import com.moyushot.moyu.page.material.edit.EditMaterialPresenter
import kotlinx.android.synthetic.main.activity_trim_video.*
import me.fanjie.grafikaprogram.R
import me.fanjie.grafikaprogram.other.MiscUtils

class TrimVideoActivity : AppCompatActivity(), EditMaterialContract.View {

    private val presenter = EditMaterialPresenter(this)

    private var playerInit = false

    private val player: SurfaceVideoEditPlayer by lazy {
        SurfaceVideoEditPlayer(videoSurfaceView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trim_video)
        btn_get_video.setOnClickListener {
            selectVideoByGallery()
        }
        val path = MiscUtils.getFiles(getFilesDir(), "*.mp4")[0]
        player.play(Uri.parse(path))
        playerInit = true
    }

    override fun onPause() {
        super.onPause()
        if (playerInit) player.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (playerInit) player.onResume()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_VIDEO && data != null) {
                presenter.queryMetadata(data.data)
            }
        }
    }


    override fun queryMetadataDone(videoSize: Int, videoWidth: Int, videoHeight: Int, uri: Uri) {
        player.play(uri)
        playerInit = true
    }

    override fun onTrimSuccess(path: String) {

    }

    private fun selectVideoByGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "选择视频"), REQUEST_CODE_SELECT_VIDEO)
    }

    companion object {
        private val REQUEST_CODE_SELECT_VIDEO = 101
    }
}
