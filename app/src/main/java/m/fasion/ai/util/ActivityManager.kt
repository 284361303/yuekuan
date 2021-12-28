package m.fasion.ai.util

import android.app.Activity
import android.text.TextUtils
import java.util.concurrent.CopyOnWriteArrayList

open class ActivityManager {

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack =
                CopyOnWriteArrayList()
        }
        activityStack!!.add(activity)
    }

    fun getActivitySize() {
        activityStack?.let { lists ->
            val size = lists.size
            LogUtils.log("ActivityName- ", "$size 个")
            lists.forEach {
                LogUtils.log("ActivityName- ", it.toString())
            }
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return if (activityStack == null) {
            null
        } else activityStack!![activityStack!!.size - 1]
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishCurrentActiviy() {
        if (activityStack == null) {
            return
        }
        if (activityStack != null) {
            if (activityStack!!.size > 0) {
                val activity =
                    activityStack!![activityStack!!.size - 1]
                finishActivity(activity)
            }
        }
    }

    /**
     * 返回到首页，也就是MainActivity页面，把其他的Activity都删除了
     */
    fun finishActivity(activity: Activity?) {
        if (activityStack == null || activity == null) {
            return
        }
        activityStack?.filter {
            activityName(it.toString()) != "MainActivity"
        }?.let { mList ->
            mList.forEach { mActivity ->
                if (!mActivity.isFinishing) {
                    mActivity.finish()
                    activityStack?.remove(mActivity)
                }
            }
        }
        getActivitySize()
    }

    private fun activityName(name: String): String {
        if (name.isEmpty()) return ""
        return name.substring(name.lastIndexOf(".") + 1, name.indexOf("@"))
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        if (activityStack == null) {
            return
        }
        val size = activityStack!!.size
        for (i in size - 1 downTo 0) {
            val activity =
                activityStack!![i]
            if (activity != null && !activity.isFinishing) {
                activity.finish()
            }
        }
        activityStack!!.clear()
    }

    /**
     * 根据ActivityName获取堆中Activity实例
     *
     * @param activityName activity的全路径名
     * @return
     */
    fun getActivity(activityName: String?): Activity? {
        if (activityStack != null) {
            val size =
                activityStack!!.size
            for (i in size - 1 downTo 0) {
                val activity =
                    activityStack!![i]
                if (activity != null && TextUtils.equals(
                        activity.javaClass.name,
                        activityName
                    )
                ) {
                    return activity
                }
            }
        }
        return null
    }

    companion object {
        /**
         * 单一实例
         */
        var instance: ActivityManager? = null
            get() {
                if (field == null) {
                    field =
                        ActivityManager()
                }
                return field
            }
            private set
        private var activityStack: CopyOnWriteArrayList<Activity>? = null
    }
}