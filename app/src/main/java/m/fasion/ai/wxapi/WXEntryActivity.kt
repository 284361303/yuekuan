package m.fasion.ai.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import m.fasion.core.Config

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private var api: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, Config.APP_ID, false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    //微信发送的请求的回调
    override fun onReq(p0: BaseReq?) {

    }

    //发送到微信请求的响应结果
    override fun onResp(p0: BaseResp?) {
        val errCode = p0?.errCode
        Log.e("微信分享回调数据", "onRespCode: " + errCode)
        when (errCode) {
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                Toast.makeText(this, "取消分享", Toast.LENGTH_SHORT).show()
            }
            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                Toast.makeText(this, "分享被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }
}