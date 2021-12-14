package m.fasion.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.NonNull
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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

    /**
     * 验证是否是手机号
     */
    fun String.isMobile(): Boolean {
        val pattern =
            Pattern.compile("^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[1,8,9]))\\d{8}$")
        return pattern.matcher(this).matches()
    }

    /**
     * 手机号密文处理  如：150***78901
     */
    fun String?.toSecret(): String {
        return if (this.isNullOrEmpty()) {
            ""
        } else {
            if (this.maybePhone()) {
                this.replaceRange(3..6, "****")
            } else {
                this
            }
        }
    }

    fun String.maybePhone(): Boolean {
        return if (this.isNumeric()) this.length in 7..11 && this.startsWith("1") else false
    }

    fun String.isNumeric(): Boolean {
        val pattern = Pattern.compile("^-?[0-9]+")
        return pattern.matcher(this).matches()
    }

    /**
     * 毫秒转日期
     */
    @SuppressLint("SimpleDateFormat")
    fun millisecond2Date(millisecond: Long): String {
        val date = Date(millisecond)
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }
}