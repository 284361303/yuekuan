package m.fasion.ai.homeDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.R
import m.fasion.core.model.Clothes
import m.fasion.core.util.CoreUtil
import java.util.concurrent.ThreadLocalRandom

/**
 * 款式详情中的推荐适配器
 */
class RecommendAdapter(private val context: Context, private val mList: List<Clothes>) :
    RecyclerView.Adapter<RecommendAdapter.RecommendHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendHolder {
        return RecommendHolder(LayoutInflater.from(context).inflate(R.layout.item_staggered, parent, false))
    }

    override fun onBindViewHolder(holder: RecommendHolder, position: Int) {
        val layout = holder.space.layoutParams as LinearLayout.LayoutParams
        layout.height = CoreUtil.dp2px(context, 3f)
        holder.space.layoutParams = layout

        val lists = mList[position]
        val ivBg = lists.head_img
        val favourite = lists.favourite
        val num = lists.num

        if (position > 1) {
            holder.space.visibility = View.GONE
        } else {
            holder.space.visibility = View.VISIBLE
        }

        if (ivBg != holder.ivBg.tag) {  //防止图片闪动和布局高度变化
            holder.ivBg.tag = ivBg
            val layoutParams = holder.ivBg.layoutParams

            val random = ThreadLocalRandom.current().nextInt(160, 280)
            layoutParams.height = CoreUtil.dp2px(context, random.toFloat())
            holder.ivBg.layoutParams = layoutParams
            Glide.with(context).load(ivBg).into(holder.ivBg)
        }

        holder.ivCollect.setImageResource(if (favourite) R.mipmap.icon_collect else R.mipmap.icon_uncollect)
        holder.tvNum.text = num

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(lists, position)
            }

            holder.clCollect.setOnClickListener {
                onItemClickListener?.onCollectClick(lists, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class RecommendHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBg: ImageView = itemView.findViewById(R.id.itemStaggered_iv)
        val ivCollect: ImageView = itemView.findViewById(R.id.itemStaggered_ivCollect)
        val space: Space = itemView.findViewById(R.id.itemStaggered_space)
        val clCollect: ConstraintLayout = itemView.findViewById(R.id.itemStaggered_clCollect)
        val tvNum: TextView = itemView.findViewById(R.id.itemStaggered_tvNum)
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(model: Clothes, position: Int)

        /**
         * 点击收藏/喜欢
         */
        fun onCollectClick(model: Clothes, position: Int)
    }
}