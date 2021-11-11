package m.fasion.ai.share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.common.UiError
import com.sina.weibo.sdk.openapi.IWBAPI
import com.sina.weibo.sdk.openapi.WBAPIFactory
import com.sina.weibo.sdk.share.WbShareCallback
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import m.fasion.ai.BuildConfig
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityShareBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.Config

/**
 * 分享页面
 * 2021年10月11日19:47:38
 */
class ShareActivity : BaseActivity(), WbShareCallback {
    private var message: WeiboMultiMessage? = null
    private var wbApi: IWBAPI? = null
    private var wxApi: IWXAPI? = null
    private val TAG = "ShareActivity"

    private val inflate by lazy {
        ActivityShareBinding.inflate(layoutInflater)
    }

    companion object {

        fun startActivity(context: Context, text: String) {
            val intent = Intent(context, ShareActivity::class.java)
            val bundle = Bundle()
            if (text.isNotEmpty()) {
                bundle.putString("text", text)
            }
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(inflate.root)

        initShareSdk()

        inflate.diaLogShareViewTop.setOnClickListener {
            finish()
        }
        inflate.diaLogShareTvCancel.setOnClickListener {
            finish()
        }

        //微信好友
        inflate.diaLogShareTvWeiChat.setOnClickListener {
            wxApi?.let {
                WXShareUtil.shareText(this, "你好啊", 0, it)
            }
        }

        //微信朋友圈
        inflate.diaLogShareTvWeiChatFriends.setOnClickListener {
            wxApi?.let {
                WXShareUtil.shareText(this, "你好啊", 1, it)
            }
        }
        //微博
        inflate.diaLogShareTvWeiBo.setOnClickListener {
            wbApi?.let {
                if (message == null) {
                    message = WeiboMultiMessage()
                }
                WBShareUtil.shareText("那好啊", message!!, it)
            }
        }

        //取参数
        getExtra()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e(TAG, "onAttachedToWindow: ")
    }

    private fun getExtra() {
        if (intent.extras?.containsKey("text") == true) {
            intent.extras?.getString("text")?.let {
                ToastUtils.show(it)
            }
        }
    }

    private fun initShareSdk() {
        //初始化微信sdk
        wxApi = WXAPIFactory.createWXAPI(this, Config.APP_ID, false)
        //初始化微博sdk
        val authInfo = AuthInfo(this, Config.APP_KEY, Config.REDIRECT_URL, Config.SCOPE)
        wbApi = WBAPIFactory.createWBAPI(this)
        wbApi?.registerApp(this, authInfo)
        //日志
        wbApi?.setLoggerEnable(BuildConfig.DEBUG)
        message = WeiboMultiMessage()
    }

    override fun onComplete() {
        Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show()
    }

    override fun onError(p0: UiError) {
        Toast.makeText(this, "分享失败:" + p0.errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCancel() {
        Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (wbApi != null) {
            wbApi?.doResultIntent(data, this)
        }
    }
}