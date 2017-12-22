package me.fanjie.grafikaprogram.pages.jniutils
//package me.fanjie.grafikaprogram.pages.jniutils.JNIUtilsActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_jniutils.*
import me.fanjie.grafikaprogram.R

class JNIUtilsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jniutils)

        btn_get.setOnClickListener {
            selectVideoByGallery()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10086 && data != null) {
                imageView.setImageURI(data.data)
            }
        }
    }


    private fun selectVideoByGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "选择视频"), 10086)
    }
}
