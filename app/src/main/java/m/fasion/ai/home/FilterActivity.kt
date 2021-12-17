package m.fasion.ai.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityFilterBinding
import m.fasion.ai.databinding.ItemFilterBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.Categories
import m.fasion.core.model.ChildX
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.stringSuspending
import m.fasion.core.util.CoreUtil

/**
 * 筛选
 */
class FilterActivity : BaseActivity() {

    private val binding by lazy { ActivityFilterBinding.inflate(layoutInflater) }
    private val viewModel: FilterViewHolder by viewModels()
    private val listsData: MutableList<ChildX> = mutableListOf()

    private var addList: MutableList<String> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(binding.root)

        CoreUtil.setTypeFaceMedium(listOf(binding.filterTvCategory))

        viewModel.getCategories()

        //adapter点击事件
        val adapter = FilterAdapter(listsData)
        binding.filterRV.adapter = adapter
        binding.filterRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.filterRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val itemValue = listsData[position]
                val id = itemValue.id
                if (addList.contains(id)) {
                    addList.remove(id)
                } else {
                    addList.add(id)
                }
                adapter.setSelectedData(addList)
            }
        }))

        //重置按钮
        binding.filterTvReset.setOnClickListener {
            addList.clear()
            adapter.setSelectedData(addList)
            LiveEventBus.get(ConstantsKey.FILTER_KEY, MutableList::class.java).post(addList)
            finish()
        }
        //确定按钮
        binding.filterTvSure.setOnClickListener {
            LiveEventBus.get(ConstantsKey.FILTER_KEY, MutableList::class.java).post(addList)
            finish()
        }
        //左侧点击关闭
        binding.filterViewLeft.setOnClickListener {
            finish()
        }

        //数据回调
        viewModel.categoriesLiveData.observe(this, { lists ->
            lists?.let {
                lists.forEach { one ->
                    val childList = one.child_list
                    childList.forEach { two ->
                        val childList1 = two.child_list
                        childList1.forEach { three ->
                            listsData.add(three)
                        }
                    }
                }
            }
            adapter.setSelectedData(listOf())
        })
    }
}

class FilterAdapter(private val mList: List<ChildX>) :
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
            val lists = mList[position]
            val id = lists.id
            val name = lists.name
            binding.itemFilterTvContent.text = name
            mSelectedList?.let {
                if (it.contains(id)) {
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

class FilterViewHolder : BaseViewModel() {
    private var launch: Job? = null

    val categoriesLiveData = MutableLiveData<Categories>()

    fun getCategories() {
        launch = viewModelScope.launch {
            val categories = repository.getCategories()
            if (categories.isSuccessful) {
                categories.body()?.let { model ->
                    categoriesLiveData.value = model
                }
            } else {
                categories.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        ToastUtils.show(message)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}