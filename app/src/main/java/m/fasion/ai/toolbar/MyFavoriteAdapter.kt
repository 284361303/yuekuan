package m.fasion.ai.toolbar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.databinding.ItemEmptyViewBinding
import m.fasion.ai.databinding.ItemMyFavoriteBinding
import m.fasion.core.model.Data

class MyFavoriteAdapter(private val context: Context, private val mList: List<Data>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) EmptyViewHolder(ItemEmptyViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)) else MyFavoriteHolder(ItemMyFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.isEmpty()) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyFavoriteHolder) {
            val data = mList[position]
            val title = data.title
            val path = data.head[0].target.path
            Glide.with(context).load(path).into(holder.binding.itemMyFavoriteIvBG)
            holder.binding.itemMyFavoriteTv1.text = title

            /*val random = ThreadLocalRandom.current().nextInt(160, 280)
            val layoutParams = holder.binding.itemMyFavoriteCard.layoutParams
            layoutParams.height = CoreUtil.dp2px(context, random.toFloat())
            holder.binding.itemMyFavoriteCard.layoutParams = layoutParams*/
        } else if (holder is EmptyViewHolder) {
            holder.binding.emptyViewTvContent.text = "暂无喜欢"
        }
    }

    override fun getItemCount(): Int {
        return if (mList.isEmpty()) 1 else mList.size
    }

    class MyFavoriteHolder(val binding: ItemMyFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)


    class EmptyViewHolder(val binding: ItemEmptyViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}