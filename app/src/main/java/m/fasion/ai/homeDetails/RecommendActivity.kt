package m.fasion.ai.homeDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.umeng.analytics.MobclickAgent
import com.youth.banner.transformer.ScaleInTransformer
import com.youth.banner.util.BannerUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.R
import m.fasion.ai.base.BaseFragment
import m.fasion.ai.databinding.ActivityRecommendBinding
import m.fasion.ai.databinding.FragmentRecommendBinding
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.*
import m.fasion.core.util.CoreUtil
import m.fasion.core.util.SPUtil

/**
 * 今日推荐页面
 */
class RecommendActivity : FragmentActivity() {
    private var mId: String = ""
    private var currentPosition: Int = 1
    private var isRefresh = false

    private val viewModel: RecommendViewModel by viewModels()
    private val binding by lazy { ActivityRecommendBinding.inflate(layoutInflater) }
    private val listData: MutableList<Body> = mutableListOf()

    companion object {
        fun startActivity(context: Context, id: String) {
            val intent = Intent(context, RecommendActivity::class.java)
            val bundle = Bundle()
            if (id.isNotEmpty()) {
                bundle.putString("id", id)
            }
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        m.fasion.ai.util.ActivityManager.instance?.addActivity(this)
        intent.extras?.containsKey("id")?.let {
            if (it) {
                intent.extras?.getString("id")?.let { id ->
                    mId = id
                    viewModel.getTopics(id)
                }
            }
        }

        binding.title.inCludeTitleIvBack.setOnClickListener { finish() }
        CoreUtil.setTypeFaceMedium(arrayOf(binding.recommendTvRecommend1, binding.recommendTvRecommend2, binding.recommendTvCurrent).toList())

        viewModel.modelListLiveData.observe(this, {
            listData.clear()
            val body = it.body
            if (body.isNotEmpty()) {
                listData.addAll(body)
                initObserver()
                if (!isRefresh) {
                    initTablet()
                }
                initClick()
            }
        })

        //登录成功
        LiveEventBus.get<UserModel>("loginSuccess").observe(this, { models ->
            if (models.uid.isNotEmpty() && SPUtil.getToken() != null) {
                val collectionId = viewModel.collectionId.value
                if (collectionId.isNullOrEmpty()) { //登录成功判断是否有登录之前的收藏操作，有就登录成功后直接收藏当前的款式
                    viewModel.getTopics(mId)
                } else {
                    viewModel.addFavorites(collectionId) {
                        MobclickAgent.onEventObject(this, "20211213014", mapOf("modelId" to collectionId))
                        isRefresh = true
                        viewModel.getTopics(mId)
                    }
                }
            }
        })
    }

    private fun initObserver() {
        //有数据就显示底部切换按钮，否则就隐藏
        binding.recommendLlBtn.visibility = if (listData.isNotEmpty()) View.VISIBLE else View.INVISIBLE

        binding.recommendTvAll.text = listData.size.toString()
        binding.recommendVP.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount(): Int = listData.size

            override fun createFragment(position: Int): Fragment {
                val recommendFragment = RecommendFragment()
                recommendFragment.arguments = Bundle().also {
                    it.putParcelable("commendItem", listData[position])
                    if (isRefresh && currentPosition > 1) {
                        binding.recommendVP.currentItem = currentPosition - 1
                    }
                }
                return recommendFragment
            }
        }
    }

    private fun initTablet() {
        //tabLayout滑动位置监听
        binding.recommendTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    currentPosition = it.position + 1
                    binding.recommendTvCurrent.text = (currentPosition).toString()
                    //获取当前下标并控制底部按钮点击事件
                    if (currentPosition > 0 && currentPosition < listData.size) {
                        binding.recommendIvRight.setImageResource(R.mipmap.icon_btn_right_select)
                        binding.recommendIvRight.isClickable = true
                    } else if (currentPosition == listData.size) {
                        binding.recommendIvRight.setImageResource(R.mipmap.icon_btn_right_unselect)
                        binding.recommendIvRight.isClickable = false
                    }
                    if (currentPosition > 1) {
                        binding.recommendIvLeft.setImageResource(R.mipmap.icon_btn_left_select)
                        binding.recommendIvLeft.isClickable = true
                    } else if (currentPosition == 1) {
                        binding.recommendIvLeft.setImageResource(R.mipmap.icon_btn_left_unselect)
                        binding.recommendIvLeft.isClickable = false
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


        TabLayoutMediator(binding.recommendTabLayout, binding.recommendVP) { _, _ -> }.attach()

        //设置画廊效果
        viewModel.setBannerGalleryEffect(this, binding.recommendVP, 12, 12, 8, .85f)
    }

    private fun initClick() {
        binding.recommendIvLeft.setOnClickListener {
            if (listData.isNotEmpty() && currentPosition > 1 && currentPosition <= listData.size) {
                currentPosition -= 1
                binding.recommendTabLayout.getTabAt(currentPosition - 1)?.select()
            }
        }
        binding.recommendIvRight.setOnClickListener {
            if (listData.isNotEmpty() && currentPosition > 0 && currentPosition < listData.size) {
                currentPosition += 1
                binding.recommendTabLayout.getTabAt(currentPosition - 1)?.select()
            }
        }
    }
}

class RecommendFragment : BaseFragment() {
    private var model: Body? = null
    private lateinit var _binding: FragmentRecommendBinding

    private val viewModel: RecommendViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getParcelable<Body>("commendItem")?.let { body ->
                model = body
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model?.let { mBody ->
            Glide.with(requireContext()).load(mBody.head_img).into(_binding.fragmentRecommendIv)
            _binding.fragmentRecommendIvCollect.setImageResource(if (mBody.favourite) R.mipmap.icon_collect else R.mipmap.icon_uncollect)

            //取消和进行点赞
            _binding.fragmentRecommendIvCollect.setOnClickListener {
                if (SPUtil.getToken().isNullOrEmpty()) {
                    viewModel.collectionId.value = mBody.target
                }
                checkLogin {
                    val favourite = mBody.favourite
                    val target = mBody.target
                    if (favourite) {
                        mBody.favourite = false
                        viewModel.cancelFavorites(target)
                        MobclickAgent.onEventObject(requireContext(), "20211213019", mapOf("modelId" to target))
                    } else {
                        mBody.favourite = true
                        viewModel.addFavorites(target)
                        MobclickAgent.onEventObject(requireContext(), "20211213014", mapOf("modelId" to target))
                    }
                    _binding.fragmentRecommendIvCollect.setImageResource(if (mBody.favourite) R.mipmap.icon_collect else R.mipmap.icon_uncollect)
                }
            }

            _binding.fragmentRecommendIv.setOnClickListener {
                HomeDetailsActivity.startActivity(requireContext(), mBody.target)
            }
        }
    }
}

class RecommendViewModel : BaseViewModel() {
    val collectionId = MutableLiveData<String>()
    val modelListLiveData = MutableLiveData<RecommendList>()
    val errorLiveData = MutableLiveData<String>()
    private var launch: Job? = null

    /**
     * 根据合集id获取合集信息，如：首页banner跳转到高级西装搭配
     */
    fun getTopics(id: String) {
        launch = viewModelScope.launch {
            val topics = repository.getTopics(id)
            if (topics.isSuccessful) {
                topics.body()?.let { model ->
                    modelListLiveData.value = model
                }
            } else {
                topics.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        errorLiveData.value = message
                    }
                }
            }
        }
    }

    fun addFavorites(id: String, observer: ((flag: String?) -> Unit?)? = null) {
        launch = viewModelScope.launch {
            val addFavorites = repository.addFavorites(id)
            if (addFavorites.isSuccessful) {
                addFavorites.body()?.let {
                    LiveEventBus.get<String>(ConstantsKey.ADD_FAVORITES_OK).post(id)
                    observer?.invoke(id)
                }
            }
        }
    }

    fun cancelFavorites(id: String) {
        launch = viewModelScope.launch {
            val cancelFavorites = repository.cancelFavorites(id)
            if (cancelFavorites.isSuccessful) {
                cancelFavorites.body()?.let {
                    LiveEventBus.get<String>(ConstantsKey.CANCEL_FAVORITES_OK).post(id)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }

    /**
     * 设置画廊效果
     * @param leftItemWidth  item左展示的宽度,单位dp
     * @param rightItemWidth item右展示的宽度,单位dp
     * @param pageMargin     页面间距,单位dp
     * @param scale          缩放[0-1],1代表不缩放
     */
    fun setBannerGalleryEffect(context: Context, viewPage: ViewPager2, leftItemWidth: Int, rightItemWidth: Int, pageMargin: Int, scale: Float) {
        val compositePageTransformer = CompositePageTransformer()
        viewPage.setPageTransformer(compositePageTransformer)
        compositePageTransformer.addTransformer(MarginPageTransformer(CoreUtil.dp2px(context, pageMargin.toFloat())))
        compositePageTransformer.addTransformer(ScaleInTransformer(scale))
        setRecyclerViewPadding(viewPage, if (leftItemWidth > 0) BannerUtils.dp2px((leftItemWidth + pageMargin).toFloat()) else 0,
            if (rightItemWidth > 0) BannerUtils.dp2px((rightItemWidth + pageMargin).toFloat()) else 0)
    }

    private fun setRecyclerViewPadding(viewPage: ViewPager2, leftItemPadding: Int, rightItemPadding: Int) {
        val recyclerView = viewPage.getChildAt(0) as RecyclerView
        if (viewPage.orientation == ViewPager2.ORIENTATION_VERTICAL) {
            recyclerView.setPadding(viewPage.paddingLeft, leftItemPadding, viewPage.paddingRight, rightItemPadding)
        } else {
            recyclerView.setPadding(leftItemPadding, viewPage.paddingTop, rightItemPadding, viewPage.paddingBottom)
        }
        recyclerView.clipToPadding = false
    }
}