package m.fasion.ai.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.base.BaseFragment
import m.fasion.ai.databinding.FragmentHomeChildBinding
import m.fasion.ai.homeDetails.HomeDetailsActivity
import m.fasion.core.base.BaseViewModel
import m.fasion.core.model.Clothes
import m.fasion.core.model.ClothesList
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.stringSuspending

/**
 * 款式首页的瀑布流
 */
class HomeChildFragment : BaseFragment() {

    private var totalPage: Int = 0  //总页数
    private var currentPage: Int = 1    //当前返回的页数
    private var totalCount: Int = 0    //总的数量
    private var mAdapter: HomeChildAdapter? = null
    private lateinit var _binding: FragmentHomeChildBinding
    private val viewModel: HomeChildViewModel by activityViewModels()
    private var mSort: String = ""
    private var mCategoryId = ""
    private var listData: MutableList<Clothes> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString("childTitle")?.let { title ->
                mSort = title
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentHomeChildBinding.inflate(inflater, container, false).apply {
            homeChildViewModel = viewModel
        }
        return _binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager()
        initAdapter()
        getData()
        _binding.homeChildRefresh.setEnableRefresh(false)

        //列表数据回调
        viewModel.clothesListData.observe(requireActivity(), {
            totalPage = it.total_page
            currentPage = it.current_page
            totalCount = it.total_count.toInt()
            if (it.clothes_list.isNotEmpty()) {
                listData.addAll(it.clothes_list)
            }
            setLayoutManager()
            _binding.homeChildRefresh.finishLoadMore()
            mAdapter?.notifyDataSetChanged()
        })

        //上拉加载事件
        _binding.homeChildRefresh.setOnLoadMoreListener {
            if (listData.size >= totalCount) {
                _binding.homeChildRefresh.finishLoadMore()
            } else {
                currentPage += 1
                getData()
            }
        }

        viewModel.errorLiveData.observe(requireActivity(), {
            setLayoutManager()
        })

        //取消收藏成功,刷新首页数据改变收藏状态
        LiveEventBus.get<String>("cancelFavoritesSuccess").observe(requireActivity(), {
            it?.let { mId ->
                if (listData.isNotEmpty()) {
                    val mData = listData.filter { it.id == mId }[0]
                    listData
                }
            }
        })

        //收藏成功
        LiveEventBus.get<String>("addFavoritesSuccess").observe(requireActivity(), {
            it?.let { mId ->

            }
        })
    }

    private fun setLayoutManager() {
        if (listData.isEmpty()) {
            _binding.homeChildRV.layoutManager = LinearLayoutManager(requireContext())
        } else {
            _binding.homeChildRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun initAdapter() {
        mAdapter = HomeChildAdapter(requireContext(), 0, listData)
        _binding.homeChildRV.adapter = mAdapter
        mAdapter?.onItemClickListener = object : HomeChildAdapter.OnItemClickListener {
            override fun onItemClick(model: Clothes, position: Int) {
                HomeDetailsActivity.startActivity(requireContext(), model.id)
            }

            override fun onCollectClick(model: Clothes, position: Int) {
                checkLogin {
                    val favourite = model.favourite
                    val id = model.id
                    if (favourite) {
                        model.favourite = false
                        viewModel.cancelFavorites(id)
                    } else {
                        model.favourite = true
                        viewModel.addFavorites(id)
                    }
                    mAdapter?.notifyItemChanged(position, -1)
                }
            }
        }
    }

    private fun getData() {
        viewModel.getClothesList(mSort, mCategoryId, currentPage)
    }
}

class HomeChildViewModel : BaseViewModel() {

    private var launch: Job? = null
    val clothesListData = MutableLiveData<ClothesList>()
    val errorLiveData = MutableLiveData<String>()

    //获取款式列表
    fun getClothesList(sort: String, categoryId: String, page: Int) {
        launch = viewModelScope.launch {
            val clothesList = repository.getClothesList(sort, categoryId, page, 20)
            if (clothesList.isSuccessful) {
                clothesListData.value = clothesList.body()
            } else {
                clothesList.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        errorLiveData.value = message
                    }
                }
            }
        }
    }

    val addFavoritesOk = MutableLiveData<String>()
    fun addFavorites(id: String) {
        launch = viewModelScope.launch {
            val addFavorites = repository.addFavorites(id)
            if (addFavorites.isSuccessful) {
                addFavoritesOk.value = addFavorites.body()
            }
        }
    }

    val cancelFavoritesOk = MutableLiveData<String>()
    fun cancelFavorites(id: String) {
        launch = viewModelScope.launch {
            val cancelFavorites = repository.cancelFavorites(id)
            if (cancelFavorites.isSuccessful) {
                cancelFavoritesOk.value = cancelFavorites.body()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}