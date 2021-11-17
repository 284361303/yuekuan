package m.fasion.ai.toolbar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.fasion.ai.databinding.ItemAvatarListBinding

/**
 * 编辑信息里面的头像列表
 * 2021年11月17日11:25:03
 */
class AvatarListAdapter(private val mList: List<Int>) :
    RecyclerView.Adapter<AvatarListAdapter.AvatarListHolder>() {

    private var mPosition: Int? = null

    /**
     * 选中某条item刷新
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setSelectItem(position: Int) {
        mPosition = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarListHolder {
        return AvatarListHolder(ItemAvatarListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AvatarListHolder, position: Int) {
        val lists = mList[position]

        holder.binding.itemAvatarIvBG.setImageResource(lists)

        if (mPosition != null && mPosition == position) {
            holder.binding.itemAvatarIvBorder.visibility = View.VISIBLE
        } else {
            holder.binding.itemAvatarIvBorder.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class AvatarListHolder(val binding: ItemAvatarListBinding) :
        RecyclerView.ViewHolder(binding.root)
}