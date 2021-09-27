package com.aisway.faceswap.douyinapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.open.aweme.CommonConstants
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler
import com.bytedance.sdk.open.aweme.share.Share
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi

class DouYinEntryActivity : AppCompatActivity(), IApiEventHandler {

    private var douYinOpenApi: DouYinOpenApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        douYinOpenApi = DouYinOpenApiFactory.create(this)
        douYinOpenApi?.handleIntent(intent, this)
//        ActivityManager.instance!!.addActivity(this)
    }

    override fun onReq(req: com.bytedance.sdk.open.aweme.common.model.BaseReq?) {
        Log.e("抖音分享", "onReq")
    }

    override fun onResp(resp: com.bytedance.sdk.open.aweme.common.model.BaseResp?) {
        if (resp?.type == CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
            val response = resp as Share.Response
            Log.e("抖音分享", "分享失败errorCode-- " + response.errorCode + " ,Error Msg-- " + response.errorMsg)
            Toast.makeText(this,"分享失败",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onErrorIntent(intent: Intent?) {
        Log.e("抖音分享", "onErrorIntent,Intent出错")
    }
}