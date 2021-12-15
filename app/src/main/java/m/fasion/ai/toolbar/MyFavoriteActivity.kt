package m.fasion.ai.toolbar

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityMyFavoriteBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.core.base.BaseViewModel
import m.fasion.core.model.Data
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.FavoritesListData
import m.fasion.core.model.stringSuspending

/**
 * 我的喜欢页面
 */
class MyFavoriteActivity : BaseActivity() {

    private var total: Int = 0
    private var listData: MutableList<Data> = mutableListOf()

    private val binding by lazy { ActivityMyFavoriteBinding.inflate(layoutInflater) }

    private val viewModel: MyFavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.myFavoriteTitle.inCludeTitleIvBack.setOnClickListener { finish() }
        binding.myFavoriteTitle.inCludeTitleTvCenterTitle.text = "我的喜欢"

        val adapter = MyFavoriteAdapter(this, listData)
        binding.myFavoriteRV.adapter = adapter

        binding.myFavoriteRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.myFavoriteRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                ToastUtils.show(position.toString())
            }
        }))

        binding.myFavoriteRefresh.autoRefresh()

        viewModel.favoritesListData.observe(this, {
            total = it.total
            val data = it.data
            if (data.isNotEmpty()) {
                listData.addAll(data)
            }
            finishRefresh(true)
            setLayoutManager()
            adapter.notifyDataSetChanged()
        })

        viewModel.errorLiveData.observe(this, {
            finishRefresh(false)
            setLayoutManager()
        })

        binding.myFavoriteRefresh.setOnRefreshListener {
            listData.clear()
            viewModel.getClothesList("")
        }

        binding.myFavoriteRefresh.setOnLoadMoreListener {
            if (listData.size <= 0) return@setOnLoadMoreListener
            if (listData.size >= total) {
                finishRefresh(true)
            } else {
                val get = listData.get(listData.size - 1)
                val id = get.head[0].id
                viewModel.getClothesList(id)
            }
        }
    }

    private fun finishRefresh(flag: Boolean) {
        binding.myFavoriteRefresh.finishRefresh(flag)
        binding.myFavoriteRefresh.finishLoadMore(flag)
    }

    private fun setLayoutManager() {
        if (listData.isEmpty()) {
            binding.myFavoriteRV.layoutManager = LinearLayoutManager(this)
        } else {
            binding.myFavoriteRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
}

class MyFavoriteViewModel : BaseViewModel() {

    private var launch: Job? = null
    val favoritesListData = MutableLiveData<FavoritesListData>()
    val errorLiveData = MutableLiveData<String>()

    /**
     * 收藏列表
     * @param id 上一页最后条数据的id
     */
    fun getClothesList(id: String) {
        launch = viewModelScope.launch {
            val favoritesList = repository.getFavoritesList(id)
            if (favoritesList.isSuccessful) {
                favoritesListData.value = favoritesList.body()
            } else {
                favoritesList.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        errorLiveData.value = message
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