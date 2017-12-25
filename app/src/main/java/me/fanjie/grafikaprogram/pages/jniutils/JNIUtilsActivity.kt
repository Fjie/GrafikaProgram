package me.fanjie.grafikaprogram.pages.jniutils

//package me.fanjie.grafikaprogram.pages.jniutils.JNIUtilsActivity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.moyushot.moyu.utils.CSBitmapUtils
import kotlinx.android.synthetic.main.activity_jniutils.*
import me.fanjie.grafikaprogram.R
import me.fanjie.grafikaprogram.other.doAndComputeTime


class JNIUtilsActivity : AppCompatActivity() {


    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jniutils)

        btn_get.setOnClickListener {
            selectVideoByGallery()
        }

        btn_do_java.setOnClickListener {
            doAndComputeTime("java",{
                imageView.setImageBitmap(CSBitmapUtils.blurBitmap(bitmap!!))
            })
        }

        btn_do_jni.setOnClickListener {
            doAndComputeTime("jni",{
                BitmapLib.blurBitmap(bitmap!!, 10)
                imageView.setImageBitmap(bitmap)
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10086 && data != null) {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                imageView.setImageBitmap(bitmap)
            }
        }
    }


    private fun selectVideoByGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "选择照片"), 10086)
    }
}
