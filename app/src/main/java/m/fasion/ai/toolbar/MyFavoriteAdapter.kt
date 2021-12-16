package m.fasion.ai.toolbar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import m.fasion.ai.databinding.ItemMyFavoriteBinding
import m.fasion.core.model.Data
import m.fasion.core.util.CoreUtil
import java.util.concurrent.ThreadLocalRandom

class MyFavoriteAdapter(private val context: Context, private val mList: List<Data>) :
    RecyclerView.Adapter<MyFavoriteAdapter.MyFavoriteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavoriteHolder {
        return MyFavoriteHolder(ItemMyFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyFavoriteHolder, position: Int) {
        val data = mList[position]
        val title = data.title
        val path = data.head[0].target.path
        Glide.with(context).load(path).into(holder.binding.itemMyFavoriteIvBG)
        holder.binding.itemMyFavoriteTv1.text = title

        val random = ThreadLocalRandom.current().nextInt(160, 280)
        val layoutParams = holder.binding.itemMyFavoriteCard.layoutParams
        layoutParams.height = CoreUtil.dp2px(context, random.toFloat())
        holder.binding.itemMyFavoriteCard.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyFavoriteHolder(val binding: ItemMyFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)
}