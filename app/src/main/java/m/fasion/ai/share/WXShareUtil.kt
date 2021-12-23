package m.fasion.ai.share

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import m.fasion.ai.util.BitmapUtils

/**
 * 微信分享
 */
object WXShareUtil {

    private const val THUMB_SIZE: Int = 150

    /**
     * 分享微信文本内容
     * @param str 文本内容
     * @param mTargetScene   WXSceneSession = 0(会话)   WXSceneTimeline = 1(朋友圈)
     */
    fun shareText(context: Context, str: String, mTargetScene: Int, api: IWXAPI) {
        if (str.isEmpty()) {
            Toast.makeText(context, "分享内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val wxTextObject = WXTextObject()
        wxTextObject.text = str

        val wxMediaMessage = WXMediaMessage()
        wxMediaMessage.mediaObject = wxTextObject
        wxMediaMessage.description = str

        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("type")
        req.message = wxMediaMessage
        req.scene = mTargetScene

        api.sendReq(req)
    }

    /**
     * 分享图片
     * @param bitmap  图片
     * @param mTargetScene   WXSceneSession = 0(会话)   WXSceneTimeline = 1(朋友圈)
     */
    fun shareImg(bitmap: Bitmap, mTargetScene: Int, api: IWXAPI) {
        val imgObj = WXImageObject(bitmap)

        val msg = WXMediaMessage()
        msg.mediaObject = imgObj

        val thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true)
        bitmap.recycle()
        msg.thumbData = BitmapUtils.bmpToByteArray(thumbBmp, true)

        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene = mTargetScene

        api.sendReq(req)
    }

    /**
     * 发送网页
     * @param   url 网页地址
     * @param   title   标题
     * @param   description 简介描述
     * @param   mTargetScene    WXSceneSession = 0(会话)   WXSceneTimeline = 1(朋友圈)
     */
    fun shareWebpage(
        url: String,
        title: String,
        description: String,
        mTargetScene: Int,
        api: IWXAPI
    ) {
        val wxWebpageObject = WXWebpageObject()
        wxWebpageObject.webpageUrl = url

        val wxMediaMessage = WXMediaMessage(wxWebpageObject)
        wxMediaMessage.title = title
        wxMediaMessage.description = description
//        wxMediaMessage.description = BitmapUtils.bmpToByteArray(thumbBmp, true)   //设置图片
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("webpage")
        req.message = wxMediaMessage
        req.scene = mTargetScene
        api.sendReq(req)
    }

    private fun buildTransaction(type: String): String {
        return if (type.isEmpty()) System.currentTimeMillis()
            .toString() else type + System.currentTimeMillis()
    }
}