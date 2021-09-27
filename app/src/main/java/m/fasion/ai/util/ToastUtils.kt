package m.fasion.ai.util

import android.content.Context
import android.os.Looper
import android.widget.Toast

object ToastUtils {

    fun show(context: Context, message: String) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}