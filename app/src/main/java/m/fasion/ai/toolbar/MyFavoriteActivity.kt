package m.fasion.ai.toolbar

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.base.StateView
import m.fasion.ai.databinding.ActivityMyFavoriteBinding
import m.fasion.ai.homeDetails.HomeDetailsActivity
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
                HomeDetailsActivity.startActivity(this@MyFavoriteActivity, listData[position].id)
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
            isShowEmptyView()
            adapter.notifyDataSetChanged()
        })

        viewModel.errorLiveData.observe(this, {
            finishRefresh(false)
            isShowEmptyView()
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
                val get = listData[listData.size - 1]
                val id = get.head[0].id
                viewModel.getClothesList(id)
            }
        }

        //详情页面取消和收藏成功回来重新刷新数据
        LiveEventBus.get<String>("cancelFavoritesSuccess").observe(this, {
            it?.let { _ ->
                binding.myFavoriteRefresh.autoRefresh()
            }
        })
        LiveEventBus.get<String>("addFavoritesSuccess").observe(this, {
            it?.let { _ ->
                binding.myFavoriteRefresh.autoRefresh()
            }
        })
    }

    private fun isShowEmptyView() {
        if (listData.isEmpty()) {
            binding.myFavoriteRefresh.visibility = View.GONE
            binding.myFavoriteStateView.setStateView(StateView.State.empty, "暂无喜欢")
        } else {
            binding.myFavoriteRefresh.visibility = View.VISIBLE
            binding.myFavoriteStateView.setStateView(StateView.State.done)
        }
    }

    private fun finishRefresh(flag: Boolean) {
        binding.myFavoriteRefresh.finishRefresh(flag)
        binding.myFavoriteRefresh.finishLoadMore(flag)
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