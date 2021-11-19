package m.fasion.ai.util

import android.widget.Toast
import m.fasion.ai.MyApp

object ToastUtils {

    fun show(message: String) {
        if (message.isEmpty()) return
        try {
            val toast = Toast.makeText(MyApp.instance, message, Toast.LENGTH_SHORT)
            toast.setText(message)
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showLong(message: String) {
        if (message.isEmpty()) return
        try {
            val toast = Toast.makeText(MyApp.instance, message, Toast.LENGTH_LONG)
            toast.setText(message)
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}