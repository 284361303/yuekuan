package m.fasion.ai.homeDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.R
import m.fasion.ai.databinding.ItemTopicSuitLeftBinding
import m.fasion.ai.databinding.ItemTopicSuitRightBinding
import m.fasion.core.model.Body
import m.fasion.core.util.CoreUtil

/**
 * 高级西装搭配
 */
class TopicSuitAdapter(private val mList: List<Body>) :
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
        val body = mList[position]
        if (holder is TopicSuitLeftHolder) {
            val headImg = body.head_img
            val title = body.title
            CoreUtil.setTypeFaceMedium(listOf(holder.leftBinding.itemTopicSuitLeftTvName))
            holder.leftBinding.itemTopicSuitLeftTvName.text = title
            holder.leftBinding.itemTopicSuitLeftIvCollect.setImageResource(if (body.favourite) R.mipmap.icon_collect_22 else R.mipmap.icon_uncollect_22)

            if (headImg != holder.leftBinding.itemTopicSuitLeftIvBg.tag) {  //防止图片闪动和布局高度变化
                holder.leftBinding.itemTopicSuitLeftIvBg.tag = headImg
                Glide.with(holder.itemView.context).load(headImg).into(holder.leftBinding.itemTopicSuitLeftIvBg)
            }
            if (onClickListener != null) {
                holder.itemView.setOnClickListener {
                    onClickListener?.onItemClickListener(body, position)
                }

                //点赞与取消点赞
                holder.leftBinding.itemTopicSuitLeftIvCollect.setOnClickListener {
                    onClickListener?.onCollectClickListener(body, position)
                }
            }
        } else if (holder is TopicSuitRightHolder) {
            val headImg = body.head_img
            CoreUtil.setTypeFaceMedium(listOf(holder.rightBinding.itemTopicSuitRightTvName))
            if (headImg != holder.rightBinding.itemTopicSuitRightIvBg.tag) {
                holder.rightBinding.itemTopicSuitRightIvBg.tag = headImg
                Glide.with(holder.itemView.context).load(headImg).into(holder.rightBinding.itemTopicSuitRightIvBg)
            }
            holder.rightBinding.itemTopicSuitRightTvName.text = body.title
            holder.rightBinding.itemTopicSuitRightIvCollect.setImageResource(if (body.favourite) R.mipmap.icon_collect_22 else R.mipmap.icon_uncollect_22)
            if (onClickListener != null) {
                holder.itemView.setOnClickListener {
                    onClickListener?.onItemClickListener(body, position)
                }

                //点赞与取消点赞
                holder.rightBinding.itemTopicSuitRightIvCollect.setOnClickListener {
                    onClickListener?.onCollectClickListener(body, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class TopicSuitLeftHolder(val leftBinding: ItemTopicSuitLeftBinding) :
        RecyclerView.ViewHolder(leftBinding.root)

    class TopicSuitRightHolder(val rightBinding: ItemTopicSuitRightBinding) :
        RecyclerView.ViewHolder(rightBinding.root)

    var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onItemClickListener(model: Body, position: Int)

        fun onCollectClickListener(model: Body, position: Int)
    }
}