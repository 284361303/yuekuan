package m.fasion.ai.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.base.StateView
import m.fasion.ai.databinding.ActivitySearchBinding
import m.fasion.ai.homeDetails.HomeDetailsActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.CustomizeDialog
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.ai.util.database.History
import m.fasion.ai.util.database.HistoryDao
import m.fasion.ai.util.database.HistoryDatabase
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.Clothes
import m.fasion.core.model.ClothesList
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.stringSuspending
import m.fasion.core.util.CoreUtil

/**
 * 搜索页面
 */
class SearchActivity : BaseActivity() {

    private var totalPage: Int = 0  //总页数
    private var currentPage: Int = 1    //当前返回的页数
    private var totalCount: Int = 0    //总的数量

    private var backFlag: Boolean = false
    private var searchAdapter: SearchHistoryAdapter? = null
    private var listAdapter: SearchAdapter? = null
    private var categoryList: MutableList<History> = mutableListOf()
    private val listData: MutableList<Clothes> = mutableListOf()
    private val searchListData: MutableList<Clothes> = mutableListOf()

    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val viewModel: SearchViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        CoreUtil.setTypeFaceMedium(listOf(binding.searchTvHistory))
        initLayoutManager(listData)
        //搜索历史列表
        initSearchHistoryAdapter()

        //搜索结果和热度推荐列表Adapter
        initAdapter()
        getData()

        //输入框事件监听
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().trim()
                if (value.isNotEmpty()) {   //不为空   显示删除按钮，隐藏历史列表，显示出来搜索列表
                    binding.searchIvDelete.visibility = View.VISIBLE
                    binding.searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                } else {    //空    ，显示删除按钮 ，显示历史列表  ，隐藏搜索列表
                    hideListView()
                    binding.searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(this@SearchActivity, R.mipmap.icon_search), null, null, null)
                }
            }
        })
        // 输入法进行搜索
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //如果是搜索按钮
                val content = binding.searchEditText.text.toString().trim()
                if (content.isNotEmpty()) {
                    viewModel.setSearchContent(History(content, System.currentTimeMillis() / 1000))
                    viewModel.getSearchList(content) { flag ->
                        flag?.let {
                            showListView()
                        }
                    }
                }
            }
            false
        }
        //获取焦点和失去焦点事件监听
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) { //得到焦点
                if (!backFlag) {    //搜索结果页面
                    hideListView()  //就显示搜索历史页面
                }
                binding.searchViewLine.setBackgroundColor(ContextCompat.getColor(this@SearchActivity, R.color.color_111111))
            } else {  //失去焦点
                binding.searchViewLine.setBackgroundColor(ContextCompat.getColor(this@SearchActivity, R.color.color_ECECEC))
            }
        }

        //清除按钮
        binding.searchTvClearHistory.setOnClickListener {
            val beginTransaction = supportFragmentManager.beginTransaction()
            val findFragmentByTag = supportFragmentManager.findFragmentByTag("clearHistory")
            if (findFragmentByTag != null) {
                beginTransaction.remove(findFragmentByTag)
            }
            beginTransaction.addToBackStack(null)
            CustomizeDialog(content = getString(R.string.clearHistoryAll), rightStr = getString(R.string.sure), callback = object :
                CustomizeDialog.Callback {
                override fun rightClickCallBack() { //回调事件处
                    viewModel.clearSearch()
                }
            }).show(supportFragmentManager, "clearHistory")
        }

        //搜索历史数据
        viewModel.listAll.observe(this, { lists ->
            categoryList.clear()
            with(lists) {
                categoryList.addAll(this)
            }
            searchAdapter?.notifyDataSetChanged()
            //搜索历史数据为空就显示热度推荐列表
            showListView()
        })

        //收藏成功
        viewModel.addFavoritesOk.observe(this, { mId ->
            if (listData.isNotEmpty()) {
                listData.forEachIndexed { index, _ ->
                    if (listData[index].id == mId) {
                        listData[index].favourite = true
                    }
                }
            }
            if (searchListData.isNotEmpty()) {
                searchListData.forEachIndexed { index, _ ->
                    if (searchListData[index].id == mId) {
                        searchListData[index].favourite = true
                    }
                }
            }
            LiveEventBus.get<String>(ConstantsKey.ADD_FAVORITES_OK).post(mId)
        })
        //取消收藏
        viewModel.cancelFavoritesOk.observe(this, { mId ->
            if (listData.isNotEmpty()) {
                listData.forEachIndexed { index, _ ->
                    if (listData[index].id == mId) {
                        listData[index].favourite = false
                    }
                }
            }
            if (searchListData.isNotEmpty()) {
                searchListData.forEachIndexed { index, _ ->
                    if (searchListData[index].id == mId) {
                        searchListData[index].favourite = false
                    }
                }
            }
            LiveEventBus.get<String>(ConstantsKey.CANCEL_FAVORITES_OK).post(mId)
        })

        //详情页面取消收藏成功,刷新首页数据改变收藏状态
        LiveEventBus.get<String>(ConstantsKey.CANCEL_FAVORITES_OK).observe(this, {
            it?.let { mId ->
                if (listData.isNotEmpty()) {
                    listData.forEachIndexed { index, _ ->
                        if (listData[index].id == mId) {
                            listData[index].favourite = false
                            listAdapter?.notifyItemChanged(index, -1)
                        }
                    }
                }
            }
        })

        //详情页面收藏成功
        LiveEventBus.get<String>(ConstantsKey.ADD_FAVORITES_OK).observe(this, {
            it?.let { mId ->
                if (listData.isNotEmpty()) {
                    listData.forEachIndexed { index, _ ->
                        if (listData[index].id == mId) {
                            listData[index].favourite = true
                            listAdapter?.notifyItemChanged(index, -1)
                        }
                    }
                }
            }
        })

        //返回按钮
        binding.searchIvBack.setOnClickListener {
            backClick()
        }
        //清空搜索框按钮
        binding.searchIvDelete.setOnClickListener {
            binding.searchEditText.setText("")
            hideListView()
        }
    }

    /**
     * 返回的时候进行监听
     */
    private fun backClick() {
        if (backFlag && searchListData.isNotEmpty()) {
            backFlag = false
            initLayoutManager(listData)
            listAdapter?.setData(listData)
            binding.searchStateView.setStateView(StateView.State.done)
            binding.searchEditText.setText("")
        } else {
            finish()
        }
    }

    private fun initLayoutManager(list: List<Clothes>) {
        if (list.isEmpty()) {
            binding.searchRVAll.layoutManager = LinearLayoutManager(this)
        } else {
            binding.searchRVAll.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        CoreUtil.hideKeyBoard(binding.searchEditText)   //隐藏软键盘
        binding.searchEditText.clearFocus() //失去焦点
    }

    /**
     * 搜索结果和热度推荐列表Adapter
     */
    private fun initAdapter() {
        listAdapter = SearchAdapter(this)
        binding.searchRVAll.adapter = listAdapter
        listAdapter?.onItemClickListener = object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(model: Clothes, position: Int) {
                HomeDetailsActivity.startActivity(this@SearchActivity, model.id)
                if (backFlag) {
                    MobclickAgent.onEventObject(this@SearchActivity, "20211213013", mapOf("modelId" to model.id))
                } else {
                    MobclickAgent.onEventObject(this@SearchActivity, "20211213012", mapOf("modelId" to model.id))
                }
            }

            override fun onCollectClick(model: Clothes, position: Int) {
                checkLogin {
                    val favourite = model.favourite
                    val id = model.id
                    if (favourite) {
                        model.favourite = false
                        viewModel.cancelFavorites(id)
                        MobclickAgent.onEventObject(this@SearchActivity, "20211213019", mapOf("modelId" to id))
                    } else {
                        model.favourite = true
                        viewModel.addFavorites(id)
                        MobclickAgent.onEventObject(this@SearchActivity, "20211213014", mapOf("modelId" to id))
                    }
                    listAdapter?.notifyItemChanged(position, -1)
                }
            }
        }
        binding.searchRVAll.addOnScrollListener(LoadMoreListener())
    }

    /**
     * 搜索历史列表
     */
    private fun initSearchHistoryAdapter() {
        searchAdapter = SearchHistoryAdapter(categoryList)
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        binding.searchTvHistoryRV.layoutManager = flexboxLayoutManager
        binding.searchTvHistoryRV.adapter = searchAdapter
        binding.searchTvHistoryRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.searchTvHistoryRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val itemValue = categoryList[position].searchName
                binding.searchEditText.setText(itemValue)
                binding.searchEditText.setSelection(binding.searchEditText.text.toString().length)

                viewModel.getSearchList(itemValue) { flag ->
                    flag?.let {
                        showListView()
                    }
                }
            }
        }))
    }

    private fun hideListView() {
        if (binding.searchEditText.text.toString().isNotEmpty()) {
            binding.searchIvDelete.visibility = View.VISIBLE
        } else {
            binding.searchIvDelete.visibility = View.GONE
        }
        binding.searchRVAll.visibility = View.GONE
        binding.searchHistoryAll.visibility = View.VISIBLE
        //搜索历史列表大于0就显示清楚按钮否则隐藏
        binding.searchTvClearHistory.visibility = if (categoryList.size > 0) View.VISIBLE else View.GONE
        binding.searchLayout1.visibility = View.VISIBLE
        binding.searchStateView.setStateView(StateView.State.done)
    }

    /**
     * 显示删除按钮 ，显示历史列表  ，隐藏搜索列表
     */
    private fun showListView() {
        binding.searchHistoryAll.visibility = View.GONE
        binding.searchIvDelete.visibility = if (binding.searchEditText.text.toString().isEmpty()) View.INVISIBLE else View.VISIBLE
        binding.searchRVAll.visibility = View.VISIBLE
    }

    /**
     * 请求款式最热列表
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        viewModel.getClothesList("heat", currentPage)
        viewModel.clothesListData.observe(this, {
            totalPage = it.total_page
            currentPage = it.current_page
            totalCount = it.total_count.toInt()
            if (it.clothes_list.isNotEmpty()) {
                listData.addAll(it.clothes_list)
            } else {
                listData.clear()
            }
            initLayoutManager(listData)
            listAdapter?.setData(listData)
        })

        /**
         * 关键字搜索结果数据
         */
        viewModel.searchListData.observe(this, {
            searchListData.clear()
            totalPage = it.total_page
            currentPage = it.current_page
            totalCount = it.total_count.toInt()
            if (it.clothes_list.isNotEmpty()) {
                searchListData.addAll(it.clothes_list)
            }
            initLayoutManager(searchListData)
            if (searchListData.isNullOrEmpty()) {
                binding.searchStateView.setStateView(StateView.State.empty, resources.getString(R.string.empty_search))
            } else {
                binding.searchStateView.setStateView(StateView.State.done, resources.getString(R.string.empty_search))
            }
            listAdapter?.setData(searchListData)
            backFlag = true
        })

        viewModel.errorLiveData.observe(this, {
            initLayoutManager(listData)
        })
    }

    /**
     * 上拉加载更多数据
     */
    private inner class LoadMoreListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is StaggeredGridLayoutManager) {
                val visibleItemPositions = layoutManager.findLastCompletelyVisibleItemPositions(null)
                var maxSize = 0
                for (i in visibleItemPositions.indices) {
                    if (i == 0) {
                        maxSize = visibleItemPositions[i]
                    } else if (visibleItemPositions[i] > maxSize) {
                        maxSize = visibleItemPositions[i]
                    }
                }
                if (maxSize + layoutManager.spanCount > listData.size && listData.size < totalCount) {
                    currentPage += 1
                    getData()
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backClick()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

class SearchViewModel : BaseViewModel() {

    private var launch: Job? = null
    private val database by lazy { HistoryDatabase.getDatabase() }
    private val roomRepository by lazy { SearchRepository(database.historyDao()) }

    val listAll: LiveData<List<History>> = roomRepository.getListData.asLiveData()

    val clothesListData = MutableLiveData<ClothesList>()
    val searchListData = MutableLiveData<ClothesList>()
    val errorLiveData = MutableLiveData<String>()

    //获取款式列表
    fun getClothesList(sort: String, page: Int) {
        launch = viewModelScope.launch {
            val clothesList = repository.getClothesList(sort, mutableListOf(), page, 20)
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

    /**
     * 关键字搜索列表
     */
    fun getSearchList(searchName: String, observer: ((flag: String?) -> Unit?)? = null) {
        launch = viewModelScope.launch {
            val searchList = repository.getSearchList(searchName)
            if (searchList.isSuccessful) {
                searchList.body()?.let {
                    searchListData.value = it
                    observer?.invoke("1")
                }
            } else {
                searchList.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        ToastUtils.show(message)
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
                addFavorites.body()?.let {
                    addFavoritesOk.value = id
                }
            }
        }
    }

    val cancelFavoritesOk = MutableLiveData<String>()
    fun cancelFavorites(id: String) {
        launch = viewModelScope.launch {
            val cancelFavorites = repository.cancelFavorites(id)
            if (cancelFavorites.isSuccessful) {
                cancelFavorites.body()?.let {
                    cancelFavoritesOk.value = id
                }
            }
        }
    }

    /**
     * 添加数据
     */
    fun setSearchContent(history: History) {
        viewModelScope.launch {
            roomRepository.insert(history)
        }
    }

    /**
     * 删除数据库数据
     */
    fun clearSearch() {
        viewModelScope.launch {
            roomRepository.delete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}

class SearchRepository(private val dao: HistoryDao) {

    /**
     * 获取数据库列表
     */
    val getListData: Flow<List<History>> = dao.getHistoryList()

    @WorkerThread
    suspend fun insert(history: History) {
        dao.insert(history)
    }

    /**
     * 删除数据库
     */
    suspend fun delete() {
        dao.delete()
    }
}