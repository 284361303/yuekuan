package m.fasion.ai.search

import android.annotation.SuppressLint
import android.content.Context
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

class SearchAdapter(private val context: Context) :
    RecyclerView.Adapter<SearchAdapter.HomeChildHolder>() {

    private var mList: MutableList<Clothes> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChildHolder {
        return HomeChildHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_staggered, parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Clothes>) {
        mList.clear()
        if (list.isNotEmpty()) {
            mList.addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HomeChildHolder, position: Int) {
        val layout = holder.space.layoutParams as LinearLayout.LayoutParams
        layout.height = CoreUtil.dp2px(context, 20f)
        holder.space.layoutParams = layout

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
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class HomeChildHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBg: AppCompatImageView = itemView.findViewById(R.id.itemStaggered_iv)
        val ivCollect: AppCompatImageView = itemView.findViewById(R.id.itemStaggered_ivCollect)
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