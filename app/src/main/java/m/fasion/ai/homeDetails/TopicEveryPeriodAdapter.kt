package m.fasion.ai.homeDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.fasion.ai.databinding.ItemImageCorners4Binding

class TopicEveryPeriodAdapter(private val mList: List<String>) :
    RecyclerView.Adapter<TopicEveryPeriodAdapter.TopicEveryPeriodHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicEveryPeriodHolder {
        return TopicEveryPeriodHolder(ItemImageCorners4Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TopicEveryPeriodHolder, position: Int) {

        if (onClickListener != null) {
            holder.itemView.setOnClickListener {
                onClickListener?.onItemClick(position)
            }

            holder.binding.itemImageIvCollect.setOnClickListener {
                onClickListener?.collectionClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class TopicEveryPeriodHolder(val binding: ItemImageCorners4Binding) :
        RecyclerView.ViewHolder(binding.root)

    var onClickListener: OnClickListener? = null

    interface OnClickListener {
        /**
         * 喜欢与取消喜欢
         */
        fun collectionClick(position: Int)

        /**
         * item点击事件
         */
        fun onItemClick(position: Int)
    }
}