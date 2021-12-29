package m.fasion.ai

import com.jeremyliao.liveeventbus.LiveEventBus
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import m.fasion.core.Config
import m.fasion.core.base.BaseApplication
import m.fasion.core.base.ConstantsKey
import m.fasion.core.util.SPUtil

class MyApp : BaseApplication() {

    init {
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
        instance = this
        initSDK()
    }

    /**
     * 初始化三方sdk
     */
    private fun initSDK() {
        //美洽聊天
        /*MQConfig.init(this, Config.MQ_KEY, object : OnInitCallback {
            override fun onFailure(p0: Int, p1: String?) {
                LogUtils.log(TAG, "onFailure: " + p1)
            }

            override fun onSuccess(p0: String?) {
                LogUtils.log(TAG, "onSuccess-- " + p0)
            }
        })*/
        //腾讯bugly   ,最后一个参数语义：建议在测试阶段建议设置成true，发布时设置为false
        CrashReport.initCrashReport(applicationContext, Config.BUGLY_APP_ID, false)

        //
        LiveEventBus.config()
            //配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）
            .autoClear(true)
        //mmkv初始化
        MMKV.initialize(this)
        //友盟预初始化
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(true)
        UMConfigure.preInit(applicationContext, Config.UMENG_APP_KEY, "Umeng")

        val string = SPUtil.getString(ConstantsKey.PRIVACY_FLAG)
        if (!string.isNullOrEmpty()) {
            //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
            UMConfigure.init(this, Config.UMENG_APP_KEY, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null)
            // 选用AUTO页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        }
    }

    companion object {
        lateinit var instance: MyApp
    }
}