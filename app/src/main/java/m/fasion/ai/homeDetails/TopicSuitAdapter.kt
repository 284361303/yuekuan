package m.fasion.ai.homeDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.fasion.ai.databinding.ItemTopicSuitLeftBinding
import m.fasion.ai.databinding.ItemTopicSuitRightBinding
import m.fasion.core.util.CoreUtil

/**
 * 高级西装搭配
 */
class TopicSuitAdapter(private val mList: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) TopicSuitRightHolder(ItemTopicSuitRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)) else
            TopicSuitLeftHolder(ItemTopicSuitLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        //  position % 2 == 0 偶数  else  奇数
        return if (position % 2 == 0) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TopicSuitLeftHolder) {
            CoreUtil.setTypeFaceMedium(listOf(holder.leftBinding.itemTopicSuitLeftTvName))
        } else if (holder is TopicSuitRightHolder) {
            CoreUtil.setTypeFaceMedium(listOf(holder.rightBinding.itemTopicSuitRightTvName))
        }
    }

    override fun getItemCount(): Int {
        return if (mList.isEmpty()) 1 else mList.size
    }

    class TopicSuitLeftHolder(val leftBinding: ItemTopicSuitLeftBinding) :
        RecyclerView.ViewHolder(leftBinding.root)

    class TopicSuitRightHolder(val rightBinding: ItemTopicSuitRightBinding) :
        RecyclerView.ViewHolder(rightBinding.root)
}