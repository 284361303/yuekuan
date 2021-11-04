package m.fasion.ai.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import m.fasion.core.util.CoreUtil

object Bindings {

    /**
     * xml中调用加载网络图片
     */
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun setImageUrl(image: ImageView, url: String?) {
        url?.let { mUrl ->
            Glide.with(image).load(mUrl).into(image)
        }
    }

    /**
     * xml中调用设置图片加粗     设置 苹方-简 中黑体   格式
     * @param type 1、代表中黑体  0、常规的
     */
    @BindingAdapter("myTextStyle")
    @JvmStatic
    fun setTextFaceMedium(textView: TextView, type: Int) {
        when (type) {
            0 -> {
                CoreUtil.setTypeFaceRegular(textView)
            }
            1 -> {
                CoreUtil.setTypeFaceMedium(arrayOf(textView).toList())
            }
        }
    }
}