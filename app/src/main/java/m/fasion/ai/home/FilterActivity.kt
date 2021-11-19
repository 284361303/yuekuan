package m.fasion.ai.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.jeremyliao.liveeventbus.LiveEventBus
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.core.base.ConstantsKey
import m.fasion.ai.databinding.ActivityFilterBinding
import m.fasion.ai.databinding.ItemFilterBinding
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.core.util.CoreUtil

/**
 * 筛选
 */
class FilterActivity : BaseActivity() {

    private val binding by lazy {
        ActivityFilterBinding.inflate(layoutInflater)
    }

    private val categoryList by lazy {
        resources.getStringArray(R.array.categoryList).toList()
    }

    private var addList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(binding.root)

        CoreUtil.setTypeFaceMedium(listOf(binding.filterTvCategory))

        //adapter点击事件
        val adapter = FilterAdapter(categoryList)
        binding.filterRV.adapter = adapter
        binding.filterRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.filterRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val itemValue = categoryList[position]
                if (addList.contains(itemValue)) {
                    addList.remove(itemValue)
                } else {
                    addList.add(itemValue)
                }
                adapter.setSelectedData(addList)
            }
        }))

        //重置按钮
        binding.filterTvReset.setOnClickListener {
            addList.clear()
            adapter.setSelectedData(addList)
            LiveEventBus.get(ConstantsKey.FILTER_KEY, List::class.java).post(addList)
            finish()
        }
        //确定按钮
        binding.filterTvSure.setOnClickListener {
            LiveEventBus.get(ConstantsKey.FILTER_KEY, List::class.java).post(addList)
            finish()
        }
        //左侧点击关闭
        binding.filterViewLeft.setOnClickListener {
            finish()
        }
    }
}

class FilterAdapter(private val mList: List<String>) :
    RecyclerView.Adapter<FilterAdapter.FilterHolder>() {

    private var mSelectedList: List<String>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedData(list: List<String>) {
        mSelectedList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {
        return FilterHolder(ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FilterHolder, position: Int) {
        with(holder) {
            val data = mList[position]
            binding.itemFilterTvContent.text = data
            mSelectedList?.let {
                if (it.contains(data)) {
                    binding.itemFilterIvSelected.visibility = View.VISIBLE
                } else {
                    binding.itemFilterIvSelected.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class FilterHolder(val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root)
}