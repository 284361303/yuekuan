package m.fasion.ai.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.R
import m.fasion.ai.util.LogUtils
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class HomeChildAdapter(private val context: Context, private val mList: List<String>) :
    RecyclerView.Adapter<HomeChildAdapter.HomeChildHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChildHolder {
        return HomeChildHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_staggered, parent, false))
    }

    override fun onBindViewHolder(holder: HomeChildHolder, position: Int) {
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
        val ivBg = itemView.findViewById<AppCompatImageView>(R.id.itemStaggered_iv)
        val ivCollect = itemView.findViewById<AppCompatImageView>(R.id.itemStaggered_ivCollect)
        val space = itemView.findViewById<Space>(R.id.itemStaggered_space)
        val clCollect = itemView.findViewById<ConstraintLayout>(R.id.itemStaggered_clCollect)
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