package m.fasion.ai

import com.meiqia.core.callback.OnInitCallback
import com.meiqia.meiqiasdk.util.MQConfig
import com.tencent.bugly.crashreport.CrashReport
import m.fasion.ai.util.LogUtils
import m.fasion.core.Config
import m.fasion.core.base.BaseApplication
import m.fasion.core.base.SPUtil

class MyApp : BaseApplication() {

    private val TAG = "MyApp"

    init {
        SPUtil.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        initSDK()
    }

    /**
     * 初始化三方sdk
     */
    private fun initSDK() {
        //美洽聊天
        MQConfig.init(this, Config.MQ_KEY, object : OnInitCallback {
            override fun onFailure(p0: Int, p1: String?) {
                LogUtils.log(TAG, "onFailure: " + p1)
            }

            override fun onSuccess(p0: String?) {
                LogUtils.log(TAG, "onSuccess-- " + p0)
            }
        })
        //腾讯bugly   ,最后一个参数语义：建议在测试阶段建议设置成true，发布时设置为false
        CrashReport.initCrashReport(getApplicationContext(), Config.BUGLY_APP_ID, BuildConfig.DEBUG);
    }
}