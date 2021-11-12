package m.fasion.ai.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import com.google.android.flexbox.FlexboxLayoutManager
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.base.StateView
import m.fasion.ai.databinding.ActivitySearchBinding
import m.fasion.ai.home.HomeChildAdapter
import m.fasion.ai.homeDetails.HomeDetailsActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.RecyclerItemClickListener
import m.fasion.core.util.CoreUtil

/**
 * 搜索页面
 */
class SearchActivity : BaseActivity() {

    private var listAdapter: HomeChildAdapter? = null
    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }
    private val categoryList by lazy {
        resources.getStringArray(R.array.categoryList).toList()
    }
    private val lists = mutableListOf("https://i.stack.imgur.com/GvWB9.png",
        "https://lh3.googleusercontent.com/NSVbWbdKFGRzju5r5XsXKMJ9A41PVdWNhGSxDwxk9aO6o_7SeVMU8z27-GhdNw3uS0PZtLPts5tvaxdsHr--NRXZWfyi=s300",
        "https://t7.baidu.com/it/u=3785402047,1898752523&fm=193&f=GIF", "https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg",
        "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg",
        "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",
        "https://i.stack.imgur.com/GvWB9.png", "https://t7.baidu.com/it/u=3785402047,1898752523&fm=193&f=GIF", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg")


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
        //搜索历史列表
        initSearchHistoryAdapter()

        //搜索结果和热度推荐列表Adapter
        initListAdapter()

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
                    ToastUtils.show(content)
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
    }

    /**
     * 搜索结果和热度推荐列表Adapter
     */
    private fun initListAdapter() {
        listAdapter = HomeChildAdapter(this, 1, lists).apply {
            binding.searchRVAll.adapter = this
            onItemClickListener = object : HomeChildAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    HomeDetailsActivity.startActivity(this@SearchActivity, "")
                }

                override fun onCollectClick(position: Int) {
                    ToastUtils.show("收藏了 $position")
                }
            }
        }
    }

    /**
     * 有数据和没有数据的展示样式
     */
    private fun initStateView() {
        if (lists.isEmpty()) {
            binding.searchStateView.includeStateView.setStateView(StateView.State.empty)
            binding.searchRVAll.visibility = View.GONE
        } else {
            binding.searchStateView.includeStateView.setStateView(StateView.State.done)
            binding.searchRVAll.visibility = View.VISIBLE
        }
    }

    /**
     * 搜索历史列表
     */
    private fun initSearchHistoryAdapter() {
        val adapter = SearchHistoryAdapter(categoryList)
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        binding.searchTvHistoryRV.layoutManager = flexboxLayoutManager
        binding.searchTvHistoryRV.adapter = adapter
        binding.searchTvHistoryRV.addOnItemTouchListener(RecyclerItemClickListener(this, binding.searchTvHistoryRV, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val itemValue = categoryList[position]
                binding.searchEditText.setText(itemValue)
                binding.searchEditText.setSelection(binding.searchEditText.text.toString().length)
            }
        }))
    }

    private fun hideListView() {
        if (binding.searchEditText.text.toString().isNotEmpty()) {
            binding.searchIvDelete.visibility = View.VISIBLE
        }
        binding.searchHistoryAll.visibility = View.VISIBLE
        binding.searchViewLine.setBackgroundColor(ContextCompat.getColor(this@SearchActivity, R.color.color_111111))
        binding.searchListAll.visibility = View.GONE
    }

    private fun showListView() {
        binding.searchHistoryAll.visibility = View.GONE
        binding.searchIvDelete.visibility = View.INVISIBLE
        binding.searchViewLine.setBackgroundColor(ContextCompat.getColor(this@SearchActivity, R.color.color_ECECEC))
        binding.searchListAll.visibility = View.VISIBLE
        CoreUtil.hideKeyBoard(binding.searchEditText)   //隐藏软键盘
        binding.searchEditText.clearFocus() //失去焦点
    }
}