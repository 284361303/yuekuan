package m.fasion.ai.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivitySearchBinding
import m.fasion.ai.home.HomeChildAdapter
import m.fasion.ai.homeDetails.HomeDetailsActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.CustomizeDialog
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.ai.util.database.History
import m.fasion.ai.util.database.HistoryDao
import m.fasion.ai.util.database.HistoryDatabase
import m.fasion.core.base.BaseViewModel
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
    private var searchAdapter: SearchHistoryAdapter? = null
    private var listAdapter: HomeChildAdapter? = null
    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private var categoryList: MutableList<History> = mutableListOf()

    private val viewModel: SearchViewModel by viewModels()

    private val listData: MutableList<Clothes> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.searchIvBack.setOnClickListener {
            finish()
        }
        binding.searchIvDelete.setOnClickListener {
            binding.searchEditText.setText("")
        }
        CoreUtil.setTypeFaceMedium(listOf(binding.searchTvHistory))
        initLayoutManager()
        //搜索历史列表
        initSearchHistoryAdapter()

        //搜索结果和热度推荐列表Adapter
        initListAdapter()
        binding.searchRefresh.setEnableRefresh(false)
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
                    hideListView()
                } else {    //空    ，显示删除按钮 ，显示历史列表  ，隐藏搜索列表
                    showListView()
                }
            }
        })
        // 输入法进行搜索
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //如果是搜索按钮
                val content = binding.searchEditText.text.toString().trim()
                if (content.isNotEmpty()) {
                    viewModel.setSearchContent(History(content))
                    showListView()
                }
            }
            false
        }
        //获取焦点和失去焦点事件监听
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) { //得到焦点
                hideListView()
            } else {  //失去焦点
                showListView()
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

        //热度列表上拉加载更多
        binding.searchRefresh.setOnLoadMoreListener {
            if (listData.size >= totalCount) {
                binding.searchRefresh.finishLoadMore()
            } else {
                currentPage += 1
                getData()
            }
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
    }

    private fun initLayoutManager() {
        if (listData.isEmpty()) {
            binding.searchRVAll.layoutManager = LinearLayoutManager(this)
        } else {
            binding.searchRVAll.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    /**
     * 搜索结果和热度推荐列表Adapter
     */
    private fun initListAdapter() {
        listAdapter = HomeChildAdapter(this, 1, listData).apply {
            binding.searchRVAll.adapter = this
            onItemClickListener = object : HomeChildAdapter.OnItemClickListener {
                override fun onItemClick(model: Clothes, position: Int) {
                    HomeDetailsActivity.startActivity(this@SearchActivity, model.id)
                }

                override fun onCollectClick(model: Clothes, position: Int) {
                    ToastUtils.show("收藏了 $position")
                }
            }
        }
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

                showListView()
            }
        }))
    }

    private fun hideListView() {
        if (binding.searchEditText.text.toString().isNotEmpty()) {
            binding.searchIvDelete.visibility = View.VISIBLE
        }
        binding.searchHistoryAll.visibility = View.VISIBLE
        binding.searchLayout1.visibility = if (categoryList.size > 0) View.VISIBLE else View.GONE
        binding.searchViewLine.setBackgroundColor(ContextCompat.getColor(this@SearchActivity, R.color.color_111111))
        binding.searchRefresh.visibility = View.GONE
    }

    /**
     * 显示删除按钮 ，显示历史列表  ，隐藏搜索列表
     */
    private fun showListView() {
        binding.searchHistoryAll.visibility = View.GONE
        binding.searchIvDelete.visibility = View.INVISIBLE
        binding.searchViewLine.setBackgroundColor(ContextCompat.getColor(this@SearchActivity, R.color.color_ECECEC))
        binding.searchRefresh.visibility = View.VISIBLE
        CoreUtil.hideKeyBoard(binding.searchEditText)   //隐藏软键盘
        binding.searchEditText.clearFocus() //失去焦点
    }

    /**
     * 请求款式最热列表
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        viewModel.getClothesList("heat", "", currentPage)
        viewModel.clothesListData.observe(this, {
            totalPage = it.total_page
            currentPage = it.current_page
            totalCount = it.total_count.toInt()
            if (it.clothes_list.isNotEmpty()) {
                listData.addAll(it.clothes_list)
            }
            initLayoutManager()
            binding.searchRefresh.finishLoadMore()
            listAdapter?.notifyDataSetChanged()
        })

        viewModel.errorLiveData.observe(this, {
            initLayoutManager()
        })
    }
}

class SearchViewModel : BaseViewModel() {

    private var launch: Job? = null
    private val database by lazy { HistoryDatabase.getDatabase() }
    private val roomRepository by lazy { SearchRepository(database.historyDao()) }

    val listAll: LiveData<List<History>> = roomRepository.getListData.asLiveData()

    val clothesListData = MutableLiveData<ClothesList>()
    val errorLiveData = MutableLiveData<String>()

    //获取款式列表
    fun getClothesList(sort: String, categoryId: String, page: Int) {
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