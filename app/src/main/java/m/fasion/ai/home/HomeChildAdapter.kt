package m.fasion.ai.home

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.R
import m.fasion.core.model.Clothes
import m.fasion.core.util.CoreUtil
import java.util.concurrent.ThreadLocalRandom

/**
 *  @param type 0、选款首页下面的列表 1、搜索页面搜索结果的热度推荐列表
 */
class HomeChildAdapter(private val context: Context, private val type: Int, private val mList: List<Clothes>, private var emptyHeight: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_empty_view, parent, false))
        else HomeChildHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_staggered, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.isEmpty()) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeChildHolder) {
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

            val clothes = mList[position]
            val favourite = clothes.favourite
            val imageUrl = clothes.head_img
            val num = clothes.num
            if (imageUrl != holder.ivBg.tag) {  //防止图片闪动和布局高度变化
                holder.ivBg.tag = imageUrl
                val random = ThreadLocalRandom.current().nextInt(160, 280)
                val layoutParams = holder.ivBg.layoutParams
                layoutParams.height = CoreUtil.dp2px(context, random.toFloat())
                holder.ivBg.layoutParams = layoutParams
                Glide.with(context).load(imageUrl).into(holder.ivBg)
            }

            holder.ivCollect.setImageResource(if (favourite) R.mipmap.icon_collect else R.mipmap.icon_uncollect)
            holder.tvNum.text = num

            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener {
                    onItemClickListener?.onItemClick(clothes, position)
                }

                holder.clCollect.setOnClickListener {
                    onItemClickListener?.onCollectClick(clothes, position)
                }
            }
        } else if (holder is EmptyViewHolder) {
            val layoutParams = holder.llAll.layoutParams as RecyclerView.LayoutParams
            layoutParams.height = emptyHeight
            holder.llAll.layoutParams = layoutParams

            if (type == 1) {    //搜索页面使用
                holder.tvContent.text = context.getString(R.string.empty_search)
                holder.llAll.gravity = Gravity.CENTER
                val layoutParams1 = holder.ivContent.layoutParams as LinearLayout.LayoutParams
                layoutParams1.setMargins(0, 0, 0, 0)
                holder.ivContent.layoutParams = layoutParams1
                holder.space.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList.isEmpty()) 1 else mList.size
    }

    class HomeChildHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBg: AppCompatImageView = itemView.findViewById(R.id.itemStaggered_iv)
        val ivCollect: AppCompatImageView = itemView.findViewById(R.id.itemStaggered_ivCollect)
        val space: Space = itemView.findViewById(R.id.itemStaggered_space)
        val clCollect: ConstraintLayout = itemView.findViewById(R.id.itemStaggered_clCollect)
        val tvNum: TextView = itemView.findViewById(R.id.itemStaggered_tvNum)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llAll: LinearLayout = itemView.findViewById(R.id.emptyView_llAll)
        val tvContent: TextView = itemView.findViewById(R.id.emptyView_tvContent)
        val ivContent: AppCompatImageView = itemView.findViewById(R.id.emptyView_ivContent)
        val space: Space = itemView.findViewById(R.id.emptyView_space)
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