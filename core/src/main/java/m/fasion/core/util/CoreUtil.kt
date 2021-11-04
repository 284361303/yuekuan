package m.fasion.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.NonNull

object CoreUtil {

    /**
     * 获取app版本号
     */
    fun versionCode(context: Context): String {
        val pm = context.packageManager
        return pm.getPackageInfo(context.packageName, 0).versionName
    }

    /**
     * 判断是否连接网络
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    /**
     * 弹出软键盘
     */
    fun showKeyBoard(et: EditText) {
        et.isFocusable = true
        et.isFocusableInTouchMode = true
        et.requestFocus()
        val systemService = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        systemService.showSoftInput(et, 0)
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyBoard(editView: View) {
        val systemService = editView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        systemService.hideSoftInputFromWindow(editView.windowToken, 0)
    }

    /**
     * 给TextView设置 苹方-简 中黑体   格式
     */
    fun setTypeFaceMedium(textList: List<TextView>) {
        for (textView in textList) {
            val paint = textView.paint
            paint.strokeWidth = 1f
            paint.isFakeBoldText = true
        }
    }

    /**
     * 给TextView设置 常规的字体颜色样式
     */
    fun setTypeFaceRegular(@NonNull textView: TextView) {
        val paint = textView.paint
        paint.strokeWidth = 0f
        paint.isFakeBoldText = false
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * px转dp
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * dp转px
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率宽 单位:px
     */
    fun getScreenWidth(context: Context): Int {
        val appContext = context.applicationContext
        return appContext.resources.displayMetrics.widthPixels
    }
}