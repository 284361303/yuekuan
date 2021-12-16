package m.fasion.ai.homeDetails

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.RoundLinesIndicator
import com.youth.banner.util.BannerUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.R
import m.fasion.ai.databinding.ActivityHomeDetailsBinding
import m.fasion.ai.share.ShareActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.model.*
import m.fasion.core.util.CoreUtil

/**
 * 选款详情
 */
class HomeDetailsActivity : m.fasion.ai.base.BaseActivity() {

    companion object {
        fun startActivity(context: Context, id: String) {
            val intent = Intent(context, HomeDetailsActivity::class.java)
            val bundle = Bundle()
            if (id.isNotEmpty()) {
                bundle.putString("id", id)
            }
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    private val binding by lazy {
        ActivityHomeDetailsBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.title.inCludeTitleIvRight.setImageResource(R.mipmap.icon_btn_share)
        initClickListener()

        intent.extras?.containsKey("id")?.let {
            if (it) {
                intent.extras?.getString("id")?.let { mId ->
                    viewModel.getClothesInfo(mId)
                    viewModel.getClothesList("heat")
                }
            }
        }

        CoreUtil.setTypeFaceMedium(listOf(binding.homeDetailsTvDetails, binding.homeDetailsTvRecommended, binding.homeDetailsTvTitle))
        //美洽聊天初始化
//        viewModel.initMeiQia(this)
        initObserver()
    }

    private fun initDetailsList(listImae: List<BodyImg>) {
        binding.homeDetailsRV1.layoutManager = LinearLayoutManager(this)
        binding.homeDetailsRV1.adapter = HomeDetailsAdapter(this, listImae)
    }

    private fun initClickListener() {
        binding.title.inCludeTitleIvBack.setOnClickListener { finish() }
        //分享按钮
        binding.title.inCludeTitleIvRight.setOnClickListener {
            ShareActivity.startActivity(this, "")
        }
        //聊天
        binding.homeDetailsIvChat.setOnClickListener {
//            val intent = MQIntentBuilder(this).build()
//            startActivity(intent)
        }
        binding.homeDetailsTvProduction.setOnClickListener {
//            val intent = MQIntentBuilder(this).build()
//            startActivity(intent)
        }
        //购买
        binding.homeDetailsTvBuy.setOnClickListener {
            viewModel.initAliBaiChuan(this)
        }
    }

    private fun initBanner(listHead: List<HeadImg>) {
        //轮播Start
        binding.homeDetailsBanner.apply {
            setAdapter(object :
                BannerImageAdapter<HeadImg>(listHead) {
                override fun onBindView(holder: BannerImageHolder?, data: HeadImg?, position: Int, size: Int) {
                    Glide.with(this@HomeDetailsActivity).load(data?.link).into(holder?.imageView!!)
                }
            })
            addBannerLifecycleObserver(this@HomeDetailsActivity)
            setOnBannerListener { data, position -> //点击事件
                ToastUtils.show(position.toString())
            }
            indicator = RoundLinesIndicator(this@HomeDetailsActivity)
            //设置指示器选中显示宽度
            setIndicatorSelectedWidth(BannerUtils.dp2px(20f))
            //设置默认指示器颜色
            setIndicatorNormalColor(ContextCompat.getColor(this@HomeDetailsActivity, R.color.color_ECECEC))
            //设置选中指示器颜色
            setIndicatorSelectedColor(ContextCompat.getColor(this@HomeDetailsActivity, R.color.color_CC5A3A))
            //设置距底部的距离
            setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, BannerUtils.dp2px(19f)))
        }
    }

    private fun initObserver() {
        //取消收藏与进行收藏
        binding.homeDetailsIvCollect.setOnClickListener {
            viewModel.clothesData.value?.apply {
                if (favourite) {
                    viewModel.cancelFavorites(id)
                    favourite = false

                } else {
                    viewModel.addFavorites(id)
                    favourite = true
                }
                binding.homeDetailsIvCollect.setImageResource(if (favourite) R.mipmap.icon_collect_22 else R.mipmap.icon_uncollect_22)
            }
        }
        viewModel.clothesData.observe(this, {
            val num = it.num    //被收藏的数量
            val createdAt = it.created_at
            val headImgList = it.head_img_list  //轮播图
            val favourite = it.favourite
            val bodyImgList = it.body_img_list
            binding.homeDetailsTvDate.text = CoreUtil.millisecond2Date(createdAt)
            binding.homeDetailsTvLikeNum.text = String.format(resources.getString(R.string.like_num, num))
//            binding.homeDetailsTvPageViews.text = String.format(resources.getString(R.string.pageviews, 121)) //浏览量
            binding.homeDetailsTvTitle.text = it.title
            binding.homeDetailsIvCollect.setImageResource(if (favourite) R.mipmap.icon_collect_22 else R.mipmap.icon_uncollect_22)
            initBanner(headImgList)
            initDetailsList(bodyImgList)
        })
        //为你推荐数据回掉
        viewModel.clothesListData.observe(this, {
            val clothesList = it.clothes_list
            if (clothesList.isNotEmpty()) {
                //推荐列表
                binding.homeDetailsRV2.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                binding.homeDetailsRV2.adapter = RecommendAdapter(this, clothesList)
            }
        })

        //取消收藏成功,刷新首页数据改变收藏状态
        viewModel.cancelFavoritesOk.observe(this, {
            LiveEventBus.get<String>("cancelFavoritesSuccess").post(it)
        })
        //收藏成功
        viewModel.addFavoritesOk.observe(this, {
            LiveEventBus.get<String>("addFavoritesSuccess").post(it)
        })
    }
}

class HomeDetailsViewModel : BaseViewModel() {

    private var launch: Job? = null

    val clothesData = MutableLiveData<ClothesInfo>()

    /**
     * 为你推荐
     */
    val clothesListData = MutableLiveData<ClothesList>()
    val errorLiveData = MutableLiveData<String>()

    /**
     * 获取衣服详情
     */
    fun getClothesInfo(id: String) {
        launch = viewModelScope.launch {
            val clothesInfo = repository.getClothesInfo(id)
            if (clothesInfo.isSuccessful) {
                clothesData.value = clothesInfo.body()
            } else {
                clothesInfo.errorBody()?.stringSuspending()?.let {
                    Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                        errorLiveData.value = message
                    }
                }
            }
        }
    }

    //获取为你推荐列表,和首页的款式列表一个接口，这就是请求的  最热 列表数据
    fun getClothesList(sort: String) {
        launch = viewModelScope.launch {
            val clothesList = repository.getClothesList(sort, "", 0, 20)
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

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }

    /**
     * 美洽聊天初始化
     */
    fun initMeiQia(context: Context) {
//        val instance = MQManager.getInstance(context)
//        MQConfig.isShowClientAvatar = true
    }

    fun initAliBaiChuan(context: Activity) {
        /*val urls = "https://item.taobao.com/item.htm?ft=t&spm=a211oj.20249227.6003353180.ditem1.a1ae6654pHNgkN&id=623849160849&utparam=null"

        val showParams = AlibcShowParams()
        showParams.openType = OpenType.Auto
        showParams.clientType = "taobao"
//        showParams.backUrl = "fasionai://fasionhost"
        showParams.nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpH5

        AlibcTrade.openByUrl(context, "淘宝客基础页面包", urls, null,
            WebViewClient(), WebChromeClient(), showParams,
            AlibcTaokeParams("", "", ""), mapOf(), object : AlibcTradeCallback {
                override fun onTradeSuccess(p0: AlibcTradeResult?) {

                }

                override fun onFailure(p0: Int, p1: String) {
                    if (p0 == -1) {
                        ToastUtils.show(p1)
                    }
                }
            })*/
    }
}