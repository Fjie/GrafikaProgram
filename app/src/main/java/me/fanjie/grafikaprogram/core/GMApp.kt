package me.fanjie.grafikaprogram.core

import android.app.Application
import me.fanjie.grafikaprogram.ext.Ext

/**
 * Created by fanjie on 2017/12/8.
 */
class GMApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Ext.with(this)
    }
}