package m.fasion.ai.util.customize

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import m.fasion.ai.R

class GlideEngine : ImageEngine {
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    /* 加载网络图片适配长图方案
      注意：此方法只有加载网络图片才会回调*/
    override fun loadImage(
            context: Context,
            url: String,
            imageView: ImageView,
            longImageView: SubsamplingScaleImageView?,
            callback: OnImageCompleteCallback?
    ) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : ImageViewTarget<Bitmap>(imageView) {
                    override fun onLoadStarted(@Nullable placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        callback?.onShowLoading()
                    }

                    override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        callback?.onHideLoading()
                    }

                    override fun setResource(@Nullable resource: Bitmap?) {
                        callback?.onHideLoading()
                        if (resource != null) {
                            val eqLongImage = MediaUtils.isLongImg(
                                resource.width,
                                resource.height
                            )
                            longImageView!!.setVisibility(if (eqLongImage) View.VISIBLE else View.GONE)
                            imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                            if (eqLongImage) {
                                // 加载长图
                                longImageView.setQuickScaleEnabled(true)
                                longImageView.setZoomEnabled(true)
                                longImageView.setPanEnabled(true)
                                longImageView.setDoubleTapZoomDuration(100)
                                longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                                longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                                longImageView.setImage(
                                    ImageSource.bitmap(resource),
                                    ImageViewState(0f, PointF(0f, 0f), 0)
                                )
                            } else {
                                // 普通图片
                                imageView.setImageBitmap(resource)
                            }
                        }
                    }
                })
    }

    fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .asGif()
                .load(url)
                .into(imageView);
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .load(url)
                .apply(
                    RequestOptions().placeholder(R.drawable.picture_image_placeholder)
                            .override(200, 200).centerCrop()
                ).into(imageView)
    }

    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(
                    RequestOptions().override(180, 180).sizeMultiplier(0.5f)
                            .centerCrop().placeholder(R.drawable.picture_image_placeholder)
                )
                .into(object : BitmapImageViewTarget(imageView) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.resources, resource)
                        circularBitmapDrawable.cornerRadius = 8f
                        imageView.setImageDrawable(circularBitmapDrawable)
                    }
                })
    }

    companion object {
        fun createGlideEngine(): GlideEngine {
            var instance: GlideEngine? = null
            if (null == instance) {
                synchronized(GlideEngine::class.java) {
                    if (null == instance) {
                        instance = GlideEngine()
                    }
                }
            }
            return instance!!
        }
    }
}