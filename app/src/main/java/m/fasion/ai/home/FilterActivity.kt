package m.fasion.ai.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import m.fasion.ai.R
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
import java.io.Serializable

/**
 * 筛选
 */
class FilterActivity : BaseActivity() {

    private val binding by lazy { ActivityFilterBinding.inflate(layoutInflater) }
    private val viewModel: FilterViewHolder by viewModels()
    private val listsData: MutableList<ChildX> = mutableListOf()
    private val selectIds: MutableList<String> = mutableListOf()

    companion object {
        fun startActivity(context: Context, idList: List<String>) {
            val intent = Intent(context, FilterActivity::class.java)
            val bundle = Bundle()
            if (idList.isNotEmpty()) {
                bundle.putSerializable("selectIds", idList as Serializable)
            }
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.activity_in_from_right, 0)
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(binding.root)

        intent.extras?.containsKey("selectIds")?.let {
            if (it) {
                val lists = intent.extras?.getSerializable("selectIds") as List<String>
                if (lists.isNotEmpty()) {
                    lists.forEach { ids ->
                        selectIds.add(ids)
                    }
                }
            }
        }

        CoreUtil.setTypeFaceMedium(listOf(binding.filterTvCategory))

        viewModel.getCategories()

        //adapter点击事件
        val adapter = FilterAdapter(listsData, selectIds)
        binding.filterRV.adapter = adapter
        binding.filterRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.filterRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val itemValue = listsData[position]
                val id = itemValue.id
                if (selectIds.contains(id)) {
                    selectIds.remove(id)
                } else {
                    selectIds.add(id)
                }
                adapter.notifyDataSetChanged()
            }
        }))

        //重置按钮
        binding.filterTvReset.setOnClickListener {
            selectIds.clear()
            adapter.notifyDataSetChanged()
            LiveEventBus.get(ConstantsKey.FILTER_KEY, MutableList::class.java).post(selectIds)
        }
        //确定按钮
        binding.filterTvSure.setOnClickListener {
            LiveEventBus.get(ConstantsKey.FILTER_KEY, MutableList::class.java).post(selectIds)
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
            adapter.notifyDataSetChanged()
        })
    }
}

class FilterAdapter(private val mList: List<ChildX>, private val mSelectedList: List<String>) :
    RecyclerView.Adapter<FilterAdapter.FilterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {
        return FilterHolder(ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FilterHolder, position: Int) {
        with(holder) {
            val lists = mList[position]
            val id = lists.id
            val name = lists.name
            binding.itemFilterTvContent.text = name
            if (mSelectedList.contains(id)) {
                binding.itemFilterIvSelected.visibility = View.VISIBLE
            } else {
                binding.itemFilterIvSelected.visibility = View.GONE
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