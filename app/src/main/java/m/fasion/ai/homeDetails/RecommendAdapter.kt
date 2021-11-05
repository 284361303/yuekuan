package m.fasion.ai.homeDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.R

/**
 * 款式详情中的推荐适配器
 */
class RecommendAdapter(private val context: Context, private val mList: List<String>) :
    RecyclerView.Adapter<RecommendAdapter.RecommendHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendHolder {
        return RecommendHolder(LayoutInflater.from(context).inflate(R.layout.item_recommend_staggered, parent, false))
    }

    override fun onBindViewHolder(holder: RecommendHolder, position: Int) {
        val url = mList[position]
        Glide.with(context).load(url).into(holder.ivBg)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class RecommendHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBg: ImageView = itemView.findViewById(R.id.itemRecommendStaggered_iv)
        val ivCollect: ImageView = itemView.findViewById(R.id.itemRecommendStaggered_ivCollect)
    }
}