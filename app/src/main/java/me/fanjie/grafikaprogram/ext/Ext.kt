package me.fanjie.grafikaprogram.ext

import android.annotation.SuppressLint
import android.app.Application

@SuppressLint("StaticFieldLeak")
/**
 * Created by Victor on 2017/8/18. (ง •̀_•́)ง
 */
object Ext {
    lateinit var ctx: Application

    fun with(app: Application) {
        this.ctx = app
    }
}