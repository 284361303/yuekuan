package m.fasion.ai.util

import android.util.Log
import m.fasion.ai.BuildConfig

object LogUtils {

    fun log(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
    }
}