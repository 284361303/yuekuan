package m.fasion.ai.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.listener.OnPageChangeListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.R
import m.fasion.ai.base.StateView
import m.fasion.ai.databinding.FragmentHomeBinding
import m.fasion.ai.homeDetails.HomeDetailsActivity
import m.fasion.ai.homeDetails.RecommendActivity
import m.fasion.ai.homeDetails.TopicSuitActivity
import m.fasion.ai.search.SearchActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.webView.WebViewActivity
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.*
import m.fasion.core.util.CoreUtil
import m.fasion.core.util.SPUtil
import java.io.Serializable
import kotlin.math.abs

/**
 * 首页Fragment
 * 2021年9月23日
 */
class HomeFragment : Fragment(), StateView.OnRetryListener {

    private var barHeight: Int = 0
    private var searchHeight: Int = 0
    private var currentSelectTab: String = ""
    private lateinit var _binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()

    /**
     * 选择的筛选合集
     */
    private val categories: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            homeViewModel = viewModel
        }
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barHeight = CoreUtil.getStatusBarHeight(requireContext())
        initView()
    }

    private fun initView() {
        //判断是否有网Start
        if (initNetWork()) return
        //判断是否有网End
        _binding.homeFragmentRefresh.setEnableLoadMore(false)   //禁止上拉加载

        _binding.homeFragmentToolBar.post {
            searchHeight = _binding.homeFragmentToolBar.height
        }
        viewModel.getTopBanner("clothes_banner")
        viewModel.getTopBanner("clothes_recommend")

        //轮播Start
        viewModel.bannerData.observe(requireActivity(), {
            val bodyList = it[0].body
            if (bodyList.isNotEmpty()) {
                _binding.homeFragmentTvCurrent.text = "1"
                _binding.homeFragmentBanner.setAdapter(object : BannerImageAdapter<Body>(bodyList) {
                    override fun onBindView(holder: BannerImageHolder?, data: Body?, position: Int, size: Int) {
                        Glide.with(requireContext()).load(data?.head_img).into(holder?.imageView!!)
                    }
                })
                _binding.homeFragmentBanner.addBannerLifecycleObserver(this@HomeFragment)
                _binding.homeFragmentBanner.addOnPageChangeListener(object : OnPageChangeListener {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                    }

                    override fun onPageSelected(position: Int) {
                        _binding.homeFragmentTvCurrent.text = (position + 1).toString()
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                })
            } else {
                _binding.homeFragmentTvCurrent.text = "0"
            }

            _binding.homeFragmentBanner.setOnBannerListener { data, _ -> //点击事件
                val body = data as Body
                when (body.type) {
                    "topics" -> {   //跳转高级西装搭配
                        TopicSuitActivity.startActivity(requireContext(), body.target)
                    }
                    "link" -> {
                        WebViewActivity.startActivity(requireContext(), body.target, "")
                    }
                    "clothes" -> {    //跳转详情
                        HomeDetailsActivity.startActivity(requireContext(), body.target)
                    }
                }
            }
            _binding.homeFragmentBanner.removeIndicator()
            _binding.homeFragmentTvAll.text = _binding.homeFragmentBanner.realCount.toString()  //设置banner总数
            _binding.homeFragmentRefresh.finishRefresh()
        })
        //轮播End

        _binding.homeFragmentAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val toolbarHeight = appBarLayout.totalScrollRange
            val dy = abs(verticalOffset)
            if (dy <= toolbarHeight) {
                val scale = dy.toFloat() / toolbarHeight
                val alpha = scale * 255
                _binding.homeFragmentToolBar.setBackgroundColor(Color.argb(alpha.toInt(), 255, 255, 255))
                _binding.homeFragmentToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))    //设置收缩后Toolbar上字体的颜色
                _binding.homeFragmentTvTopTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))   //字体变白和图标变白
                _binding.homeFragmentIvTopSearch.setImageResource(R.mipmap.icon_search_white)
            }

            if (dy > appBarLayout.totalScrollRange / 2) {   //设置标题
                val scale = dy.toFloat() / toolbarHeight
                val alpha = scale * 255
                _binding.homeFragmentToolBar.setBackgroundColor(Color.argb(alpha.toInt(), 255, 255, 255))
                _binding.homeFragmentTvTopTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))   //字体变黑和图标变黑
                _binding.homeFragmentIvTopSearch.setImageResource(R.mipmap.icon_search_black)
            }
        })

        //计算顶部的距离
        initSearchTop(_binding.homeFragmentToolBarSpace)

        CoreUtil.setTypeFaceMedium(arrayOf(_binding.homeFragmentTvRecommend1, _binding.homeFragmentTvRecommend2,
            _binding.homeFragmentTvMore, _binding.homeFragmentTvDesign1, _binding.homeFragmentTvDesign2, _binding.homeFragmentTvTopTitle).toList())

        //今日推荐数据
        viewModel.recommendDataList.observe(requireActivity(), {
            val bannerModelItem = it[0]
            val id = bannerModelItem.id
            val bodyList = bannerModelItem.body
            if (bodyList.isNotEmpty()) {
                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                _binding.homeFragmentRecommendRV.recyclerView.layoutManager = layoutManager
                _binding.homeFragmentRecommendRV.recyclerView.adapter = HomeRecommendAdapter(bodyList).also { mAdapter ->
                    mAdapter.onItemClickListener = object :
                        HomeRecommendAdapter.OnItemClickListener {
                        override fun onItemClick(model: Body, position: Int) {    //跳转详情
                            HomeDetailsActivity.startActivity(requireContext(), model.target)
                        }
                    }
                }
            }
            //今日推荐更多
            if (id.isNotEmpty()) {
                _binding.homeFragmentCLMore.setOnClickListener {
                    RecommendActivity.startActivity(requireContext(), id)
//            startActivity(Intent(requireContext(), TopicEveryPeriodActivity::class.java))
//            startActivity(Intent(requireContext(), TopicSuitActivity::class.java))
                }
            }
        })

        //tabLayout
        initTabLayoutListener()
        initTabLayoutView()
        currentSelectTab = viewModel.tabMapList.keys.toList()[0]

        //筛选跳转
        _binding.homeFragmentTvFilter.setOnClickListener {
            FilterActivity.startActivity(requireContext(), categories)
        }

        //下拉刷新
        _binding.homeFragmentRefresh.setOnRefreshListener {
            if (initNetWork()) return@setOnRefreshListener
            initView()
        }

        //搜索
        _binding.homeFragmentIvTopSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
        _binding.homeFragmentIncludeState.homeEmptyIvTopSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        viewModel.errorLiveData.observe(requireActivity(), {
            showErrorView()
        })

        //接收筛选条件页面回传的数据
        LiveEventBus.get<MutableList<String>>(ConstantsKey.FILTER_KEY).observe(requireActivity(), {
            categories.clear()
            if (it != null && it.isNotEmpty()) {
                categories.addAll(it)
            }
            initTabLayoutListener(true, it.toList())
        })

        //退出登录成功、登录成功也要重新请求款式列表
        LiveEventBus.get<String>(ConstantsKey.LOGOUT_SUCCESS).observe(this, { num ->
            if (num == "1") {
                initTabLayoutListener(true, listOf())
            }
        })
        //登录成功
        LiveEventBus.get<UserModel>("loginSuccess").observe(requireActivity(), { models ->
            if (models.uid.isNotEmpty() && SPUtil.getToken() != null) {
                initTabLayoutListener()
            }
        })


        //tabLayout点击事件改变字体颜色
        _binding.homeFragmentTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {   //选中
                tab?.customView?.findViewById<TextView>(R.id.itemTab_tvTitle)?.let {
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_111111))
                    CoreUtil.setTypeFaceMedium(arrayOf(it).toList())
                }
                tab?.customView?.findViewById<View>(R.id.itemTab_view)?.visibility = View.VISIBLE
                currentSelectTab = tab?.text.toString()
                System.currentTimeMillis()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { //未选中
                tab?.customView?.findViewById<TextView>(R.id.itemTab_tvTitle)?.let {
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_787878))
                    CoreUtil.setTypeFaceRegular(it)
                }
                tab?.customView?.findViewById<View>(R.id.itemTab_view)?.visibility = View.INVISIBLE
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    /**
     * 判断是否有网Start
     */
    private fun initNetWork(): Boolean {
        val isNetwork = CoreUtil.isNetworkAvailable(requireContext())
        if (!isNetwork) {   //让空布局和网络异常布局显示出来
            showErrorView()
            return true
        } else {    //隐藏空页面和网络错误的布局
            showNoErrorView()
        }
        return false
    }

    /**
     * 加载正常的效果图
     */
    private fun showNoErrorView() {
        _binding.homeFragmentRefresh.visibility = View.VISIBLE
        _binding.homeFragmentIncludeState.homeEmptyLlAll.visibility = View.GONE
    }

    /**
     * 网络异常显示异常图效果
     */
    private fun showErrorView() {
        _binding.homeFragmentRefresh.finishRefresh()
        _binding.homeFragmentRefresh.visibility = View.GONE
        _binding.homeFragmentIncludeState.homeEmptyLlAll.visibility = View.VISIBLE
        initSearchTop(_binding.homeFragmentIncludeState.homeEmptyToolBarSpace)
        _binding.homeFragmentIncludeState.homeEmptyStateView.setStateView(StateView.State.error)
        val layoutParams = _binding.homeFragmentIncludeState.homeEmptyStateView.layoutParams
        layoutParams.height = CoreUtil.getScreenHeight(requireContext()) - barHeight
        _binding.homeFragmentIncludeState.homeEmptyStateView.layoutParams = layoutParams
        _binding.homeFragmentIncludeState.homeEmptyStateView.listener = this
    }

    /**
     * 计算顶部的距离
     */
    private fun initSearchTop(space: Space) {
        val height = CoreUtil.px2dp(requireContext(), barHeight.toFloat())
        if (height > 0) {
            val layoutParams = space.layoutParams as LinearLayout.LayoutParams
            layoutParams.height = barHeight + CoreUtil.dp2px(requireContext(), 10.toFloat())
            space.layoutParams = layoutParams
        }
    }

    private fun initTabLayoutView() {
        TabLayoutMediator(_binding.homeFragmentTab, _binding.homeFragmentVP) { tab, position ->
            tab.text = viewModel.tabMapList.keys.toList()[position]
        }.attach()

        //自定义tabLayout的样式
        viewModel.tabMapList.keys.forEachIndexed { index, _ ->
            _binding.homeFragmentTab.getTabAt(index)?.let {
                it.setCustomView(R.layout.item_tableyout)
                if (index == 0) {
                    it.customView?.findViewById<TextView>(R.id.itemTab_tvTitle)?.let { tv ->
                        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_111111))
                        CoreUtil.setTypeFaceMedium(arrayOf(tv).toList())
                    }
                    it.customView?.findViewById<View>(R.id.itemTab_view)?.visibility = View.VISIBLE
                }
                it.customView?.findViewById<TextView>(R.id.itemTab_tvTitle)?.text = viewModel.tabMapList.keys.toList()[index]
            }
        }

        _binding.homeFragmentVP.offscreenPageLimit = viewModel.tabMapList.size
        _binding.homeFragmentTab.getTabAt(0)?.select()
    }

    /**
     * 初始化tabLayout
     */
    private fun initTabLayoutListener(refresh: Boolean? = false, listId: List<String>? = listOf()) {
        _binding.homeFragmentVP.adapter = object : FragmentStateAdapter(requireActivity()) {
            override fun getItemCount(): Int {
                return viewModel.tabMapList.size
            }

            override fun createFragment(position: Int): Fragment {
                val childFragment = HomeChildFragment()
                val values = viewModel.tabMapList.values
                val keys = viewModel.tabMapList.keys.toList()
                val value = values.toMutableList()[position]

                childFragment.arguments = Bundle().also {
                    it.putString("childTitle", value)
                    if (refresh == true) {
                        it.putSerializable("categoryId", listId as Serializable)
                        //假如如果当前显示是第二个Fragment筛选回来就还显示第二个
                        keys.forEachIndexed { index, s ->
                            if (s == currentSelectTab) {
                                _binding.homeFragmentVP.currentItem = index
                                return@forEachIndexed
                            }
                        }
                    }
                }
                return childFragment
            }
        }
    }

    /**
     * 重新加载请求数据
     */
    override fun onRetry() {
        initView()
    }
}

class HomeViewModel : BaseViewModel() {

    private var launch: Job? = null
    val tabMapList = mapOf("综合" to " ", "上新" to "new", "热度" to "heat")
    val bannerData = MutableLiveData<BannerModel>()
    val recommendDataList = MutableLiveData<BannerModel>()
    val errorLiveData = MutableLiveData<String>()

    fun getTopBanner(type: String) {
        launch = viewModelScope.launch {
            val banner = repository.getBanner(type)
            when (banner.code()) {
                in 200..299 -> {
                    when (type) {
                        "clothes_banner" -> { //顶部banner
                            banner.body()?.let { model ->
                                if (model.isNotEmpty()) {
                                    bannerData.value = model
                                }
                            }
                        }
                        "clothes_recommend" -> {  //即日推荐
                            banner.body()?.let { model ->
                                if (model.isNotEmpty()) {
                                    recommendDataList.value = model
                                }
                            }
                        }
                    }
                }
                408 -> {  //超时
                    errorLiveData.value = "服务器请求超时"
                }
                else -> {
                    banner.errorBody()?.stringSuspending()?.let {
                        Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                            ToastUtils.show(message)
                        }
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