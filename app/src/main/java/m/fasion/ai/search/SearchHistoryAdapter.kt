package m.fasion.ai.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.fasion.ai.databinding.ItemSearchHistoryBinding
import m.fasion.ai.util.database.History

/**
 * @author shao_g
 * 搜索历史适配器
 * 2021年11月12日16:15:30
 */
class SearchHistoryAdapter(private val mList: List<History>) :
    RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryHolder {
        return SearchHistoryHolder(ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchHistoryHolder, position: Int) {
        with(holder) {
            val value = mList[position].searchName
            binding.itemSearchHistoryTvContent.text = value
        }
    }

    override fun getItemCount(): Int = mList.size

    class SearchHistoryHolder(val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}