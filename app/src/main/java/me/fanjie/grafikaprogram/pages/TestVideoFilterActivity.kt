package me.fanjie.grafikaprogram.pages

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import me.fanjie.grafikaprogram.R
import me.fanjie.grafikaprogram.core.generate.TestGenMp4
import me.fanjie.grafikaprogram.other.MiscUtils
import kotlinx.android.synthetic.main.activity_test_video_filter.*
import java.io.File

class TestVideoFilterActivity : Activity() {

    companion object {
        val TAG = "xxxxx"
    }

    private val mMovieFiles: Array<String> by lazy {
        MiscUtils.getFiles(filesDir, "*.mp4")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_video_filter)
        val genMp4 = TestGenMp4()
        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mMovieFiles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner.
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                genMp4.testEncodeVideoByVideo(File(filesDir, mMovieFiles[position]),textureView)
            }
        }

        button.setOnClickListener{
//            TestGenMp4().testEncodeVideoToMp4(filesDir)

        }

        button2.setOnClickListener {
            genMp4.testEncodeVideoToMp4(filesDir)
        }

    }
}
