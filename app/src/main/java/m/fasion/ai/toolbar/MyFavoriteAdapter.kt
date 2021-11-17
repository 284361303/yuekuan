package m.fasion.ai.toolbar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.fasion.ai.databinding.ItemMyFavoriteBinding

class MyFavoriteAdapter(private val mList: List<String>) :
    RecyclerView.Adapter<MyFavoriteAdapter.MyFavoriteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavoriteHolder {
        return MyFavoriteHolder(ItemMyFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyFavoriteHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyFavoriteHolder(val binding: ItemMyFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)
}