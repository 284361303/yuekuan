package m.fasion.ai.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import m.fasion.ai.R
import m.fasion.ai.databinding.ItemImage105140Binding

/**
 * 今日推荐的adapter
 */
class HomeRecommendAdapter(private val mList: List<RecommendModel>) :
    RecyclerView.Adapter<HomeRecommendAdapter.RecommendHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendHolder {
        val binding = DataBindingUtil.inflate<ItemImage105140Binding>(LayoutInflater.from(parent.context), R.layout.item_image_105_140, parent, false)
        return RecommendHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendHolder, position: Int) {
        val recommendModel = mList[position]
        holder.bind(recommendModel, position)

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(recommendModel, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList.isNotEmpty()) mList.size else 0
    }

    class RecommendHolder(private val binding: ItemImage105140Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: RecommendModel, position: Int) {
            binding.listModel = model
            binding.position = position
            binding.executePendingBindings()
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(model: RecommendModel, position: Int)
    }
}