/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.fanjie.grafikaprogram.pages.glesndk

import android.app.Activity
import android.os.Bundle
import android.util.Log


class GL2JNIActivity : Activity() {

    var mJNIView: GL2JNIView? = null

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        mJNIView = GL2JNIView(application)
        setContentView(mJNIView)
    }

    override fun onPause() {
        super.onPause()
        mJNIView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mJNIView?.onResume()
//        doAndComputeTime("JNI", {
//            GL2JNILib.test()
//        })
//        doAndComputeTime("Java", {
//            test()
//        })

    }



    private fun test() {
        var n = 0
        for (i in 0..99999) {
            for (i2 in 0..99999){
                n+= i + i2
            }
        }
        Log.e("xxx","n = $n")
    }
}
