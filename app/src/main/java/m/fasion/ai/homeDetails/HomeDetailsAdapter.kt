package m.fasion.ai.homeDetails

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import m.fasion.ai.R
import m.fasion.core.util.CoreUtil

/**
 * 选款详情中的商品图片大图列表
 */
class HomeDetailsAdapter(private val context: Context, private val mList: List<String>) :
    RecyclerView.Adapter<HomeDetailsAdapter.HomeDetailsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeDetailsHolder {
        return HomeDetailsHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_goods_image, parent, false))
    }

    override fun onBindViewHolder(holder: HomeDetailsHolder, position: Int) {
        val url = mList[position]
        val screenWidth = CoreUtil.getScreenWidth(context)
        Glide.with(context).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                //根据宽高比 来显示图片
                val frameLayout = holder.ivBg.layoutParams as LinearLayout.LayoutParams
                val height = resource.height
                val scanHeight = screenWidth / height * height
                frameLayout.width = screenWidth
                frameLayout.height = scanHeight
                holder.ivBg.layoutParams = frameLayout
                holder.ivBg.measure(screenWidth, scanHeight)
                holder.ivBg.setImageBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class HomeDetailsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBg: ImageView = itemView.findViewById(R.id.itemGoodsImage_iv)
    }
}