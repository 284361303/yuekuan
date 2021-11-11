package m.fasion.ai.util

import android.content.Context
import android.os.Looper
import android.widget.Toast
import m.fasion.ai.MyApp

object ToastUtils {

    fun show(message: String) {
        try {
            val toast = Toast.makeText(MyApp.instance, message, Toast.LENGTH_SHORT)
            toast.setText(message)
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showLong(message: String) {
        try {
            val toast = Toast.makeText(MyApp.instance, message, Toast.LENGTH_LONG)
            toast.setText(message)
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}