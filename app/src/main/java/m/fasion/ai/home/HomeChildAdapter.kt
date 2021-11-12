package m.fasion.ai.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.R
import m.fasion.core.util.CoreUtil
import java.util.concurrent.ThreadLocalRandom

/**
 *  @param type 0、选款首页下面的列表 1、搜索结果的热度推荐列表
 */
class HomeChildAdapter(private val context: Context, private val type: Int, private val mList: List<String>) :
    RecyclerView.Adapter<HomeChildAdapter.HomeChildHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChildHolder {
        return HomeChildHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_staggered, parent, false))
    }

    override fun onBindViewHolder(holder: HomeChildHolder, position: Int) {
        when (type) {   //选款首页的列表和搜索的热度推荐是复用一个adapter,上面的留白高度不一致，所以来判断
            0 -> {
                val layout = holder.space.layoutParams as LinearLayout.LayoutParams
                layout.height = CoreUtil.dp2px(context, 3f)
                holder.space.layoutParams = layout
            }
            1 -> {
                val layout = holder.space.layoutParams as LinearLayout.LayoutParams
                layout.height = CoreUtil.dp2px(context, 20f)
                holder.space.layoutParams = layout
            }
        }

        if (position > 1) {
            holder.space.visibility = View.GONE
        } else {
            holder.space.visibility = View.VISIBLE
        }
        val random = ThreadLocalRandom.current().nextInt(400, 680)
        val layoutParams = holder.ivBg.layoutParams
        layoutParams.height = random
        holder.ivBg.layoutParams = layoutParams

        val url = mList[position]
        Glide.with(context).load(url).into(holder.ivBg)

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(position)
            }

            holder.clCollect.setOnClickListener {
                onItemClickListener?.onCollectClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class HomeChildHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBg: AppCompatImageView = itemView.findViewById(R.id.itemStaggered_iv)
        val ivCollect: AppCompatImageView = itemView.findViewById(R.id.itemStaggered_ivCollect)
        val space: Space = itemView.findViewById(R.id.itemStaggered_space)
        val clCollect: ConstraintLayout = itemView.findViewById(R.id.itemStaggered_clCollect)
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)

        /**
         * 点击收藏/喜欢
         */
        fun onCollectClick(position: Int)
    }
}