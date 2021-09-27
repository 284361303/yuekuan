package m.fasion.ai.share

import android.graphics.Bitmap
import androidx.annotation.NonNull
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.openapi.IWBAPI
import java.util.*

/**
 * 微博分享
 */
object WBShareUtil {

    /**
     * 分享文字
     * @param str 要分享的文字内容
     */
    fun shareText(
        str: String, message: WeiboMultiMessage,
        api: IWBAPI
    ) {
        val textObject = TextObject()
        textObject.text = str
        message.textObject = textObject
        api.shareMessage(message, true)
    }

    /**
     * 分享图片
     */
    fun shareImg(
        @NonNull bitmap: Bitmap, message: WeiboMultiMessage,
        api: IWBAPI
    ) {
        val imageObject = ImageObject()
        imageObject.setImageData(bitmap)
        message.imageObject = imageObject
        api.shareMessage(message, true)
    }

    /**
     * 分享网页地址
     * @param   url 网页地址
     * @param   title   标题
     * @param   description 简介描述
     */
    fun shareWebpage(
        url: String,
        title: String,
        description: String,
        message: WeiboMultiMessage,
        api: IWBAPI
    ) {
        val webpageObject = WebpageObject()
        webpageObject.identify = UUID.randomUUID().toString()
        webpageObject.title = title
        webpageObject.description = description
        webpageObject.actionUrl = url
        webpageObject.defaultText = "分享网页"
        message.mediaObject = webpageObject
        api.shareMessage(message, true)
    }
}