package m.fasion.ai

import com.jeremyliao.liveeventbus.LiveEventBus
import com.meiqia.core.callback.OnInitCallback
import com.meiqia.meiqiasdk.util.MQConfig
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import m.fasion.ai.util.LogUtils
import m.fasion.core.Config
import m.fasion.core.base.BaseApplication
import m.fasion.core.base.SPUtil

class MyApp : BaseApplication() {

    private val TAG = "MyApp"

    init {
        SPUtil.init(this)
        //下拉刷新
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader(context) //经典刷新头样式
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context) //刷新底部样式
        }
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
        CrashReport.initCrashReport(getApplicationContext(), Config.BUGLY_APP_ID, false)

        //
        LiveEventBus.config()
            //配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）
            .autoClear(true)
    }
}