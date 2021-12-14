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
import m.fasion.ai.homeDetails.TopicSuitActivity
import m.fasion.ai.search.SearchActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.*
import m.fasion.core.util.CoreUtil
import kotlin.math.abs

/**
 * 首页Fragment
 * 2021年9月23日
 */
class HomeFragment : Fragment(), StateView.OnRetryListener {

    private var barHeight: Int = 0
    private var searchHeight: Int = 0
    private lateinit var _binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()

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

            _binding.homeFragmentBanner.setOnBannerListener { data, _ -> //点击事件
                val body = data as Body
                when (body.type) {
                    "topics" -> {   //高级西装搭配
                        TopicSuitActivity.startActivity(requireContext(), body.target)
                    }
                }
            }
            _binding.homeFragmentBanner.removeIndicator()
            _binding.homeFragmentTvAll.text = _binding.homeFragmentBanner.realCount.toString()  //设置banner总数
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
            val bodyList = it[0].body
            if (bodyList.isNotEmpty()) {
                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                _binding.homeFragmentRecommendRV.recyclerView.layoutManager = layoutManager
                _binding.homeFragmentRecommendRV.recyclerView.adapter = HomeRecommendAdapter(bodyList).also {
                    it.onItemClickListener = object : HomeRecommendAdapter.OnItemClickListener {
                        override fun onItemClick(model: Body, position: Int) {    //点击事件
                            HomeDetailsActivity.startActivity(requireContext(), model.target)
                        }
                    }
                }
            }
        })

        //tabLayout
        initTabLayout()

        //筛选跳转
        _binding.homeFragmentTvFilter.setOnClickListener {
            startActivity(Intent(requireContext(), FilterActivity::class.java))
        }

        //今日推荐更多
        _binding.homeFragmentTvMore.setOnClickListener {
//            startActivity(Intent(requireContext(), RecommendActivity::class.java))
//            startActivity(Intent(requireContext(), TopicEveryPeriodActivity::class.java))
            startActivity(Intent(requireContext(), TopicSuitActivity::class.java))
        }

        //下拉刷新
        _binding.homeFragmentRefresh.setOnRefreshListener {
            if (initNetWork()) return@setOnRefreshListener
            //TODO::请求接口
        }

        //搜索
        _binding.homeFragmentIvTopSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
        _binding.homeFragmentIncludeState.homeEmptyIvTopSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        viewModel.errorLiveData.observe(requireActivity(), {
            System.currentTimeMillis()
        })

        //接收筛选条件页面回传的数据
        LiveEventBus.get(ConstantsKey.FILTER_KEY, List::class.java).observe(requireActivity(), {
            if (it != null && it.isNotEmpty()) {
                ToastUtils.show(it[0].toString())
            } else {
                ToastUtils.show("重置成功")
            }
        })
    }

    /**
     * 判断是否有网Start
     */
    private fun initNetWork(): Boolean {
        val isNetwork = CoreUtil.isNetworkAvailable(requireContext())
        if (!isNetwork) {   //让空布局和网络异常布局显示出来
            _binding.homeFragmentIncludeState.homeEmptyLlAll.visibility = View.VISIBLE
            initSearchTop(_binding.homeFragmentIncludeState.homeEmptyToolBarSpace)
            _binding.homeFragmentIncludeState.homeEmptyStateView.setStateView(StateView.State.error)
            _binding.homeFragmentIncludeState.homeEmptyStateView.listener = this
            _binding.homeFragmentRefresh.finishRefresh()
            _binding.homeFragmentRefresh.visibility = View.GONE
            return true
        } else {    //隐藏空页面和网络错误的布局
            _binding.homeFragmentRefresh.visibility = View.VISIBLE
            _binding.homeFragmentIncludeState.homeEmptyLlAll.visibility = View.GONE
        }
        return false
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

    /**
     * 初始化tabLayout
     */
    private fun initTabLayout() {
        _binding.homeFragmentVP.adapter = object : FragmentStateAdapter(requireActivity()) {
            override fun getItemCount(): Int {
                return viewModel.tabMapList.size
            }

            override fun createFragment(position: Int): Fragment {
                val childFragment = HomeChildFragment()
                val values = viewModel.tabMapList.values
                val value = values.toMutableList()[position]
                childFragment.arguments = Bundle().also {
                    it.putString("childTitle", value)
                }
                return childFragment
            }
        }
        TabLayoutMediator(_binding.homeFragmentTab, _binding.homeFragmentVP) { tab, position ->
//            tab.text = viewModel.tabList[position]
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

        //tabLayout点击事件改变字体颜色
        _binding.homeFragmentTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {   //选中
                tab?.customView?.findViewById<TextView>(R.id.itemTab_tvTitle)?.let {
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_111111))
                    CoreUtil.setTypeFaceMedium(arrayOf(it).toList())
                }
                tab?.customView?.findViewById<View>(R.id.itemTab_view)?.visibility = View.VISIBLE
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
            if (banner.isSuccessful) {
                when (type) {
                    "clothes_banner" -> { //顶部banner
                        bannerData.value = banner.body()
                    }
                    "clothes_recommend" -> {  //即日推荐
                        recommendDataList.value = banner.body()
                    }
                }
            } else {
                banner.errorBody()?.stringSuspending()?.let {
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

data class HomeBannerModel(val id: String, val url: String)