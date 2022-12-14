package m.fasion.ai.homeDetails

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.umeng.analytics.MobclickAgent
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.RoundLinesIndicator
import com.youth.banner.util.BannerUtils
import kotlinx.coroutines.*
import m.fasion.ai.R
import m.fasion.ai.databinding.ActivityHomeDetailsBinding
import m.fasion.ai.util.ActivityManager
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.*
import m.fasion.core.util.CoreUtil
import m.fasion.core.util.SPUtil
import kotlin.concurrent.thread

/**
 * 选款详情
 */
class HomeDetailsActivity : m.fasion.ai.base.BaseActivity() {

    private var mFavouriteId: String = ""

    /**
     * 热门推荐无登录下进行保存id和位置，登录成功进行自动喜欢
     */
    private var mFavouriteMap: MutableMap<String, Int> = mutableMapOf()
    private var likeNum: Int = 0
    private var recommendListData: MutableList<Clothes> = mutableListOf()
    private var recommendAdapter: RecommendAdapter? = null
    private var mId: String = ""

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
        initAdapter()

        intent.extras?.containsKey("id")?.let {
            if (it) {
                intent.extras?.getString("id")?.let { ids ->
                    mId = ids
                    viewModel.getClothesInfo(mId)
                    viewModel.getClothesList("heat")
                }
            }
        }

        CoreUtil.setTypeFaceMedium(listOf(binding.homeDetailsTvDetails, binding.homeDetailsTvRecommended, binding.homeDetailsTvTitle))
        //美洽聊天初始化
//        viewModel.initMeiQia(this)
        initObserver()

        binding.homeDetailsTitle.inCludeTitleIvBack.setOnClickListener { finish() }
        MobclickAgent.onEventObject(this, "20211213015", mapOf("modelId" to mId))
    }

    private fun initAdapter() {
        binding.homeDetailsRV2.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recommendAdapter = RecommendAdapter(this, recommendListData)
        binding.homeDetailsRV2.adapter = recommendAdapter

        recommendAdapter?.onItemClickListener = object : RecommendAdapter.OnItemClickListener {
            override fun onItemClick(model: Clothes, position: Int) {
                startActivity(this@HomeDetailsActivity, model.id)
                MobclickAgent.onEventObject(this@HomeDetailsActivity, "20211213022", mapOf("modelId" to model.id))
            }

            override fun onCollectClick(model: Clothes, position: Int) {
                if (SPUtil.getToken().isNullOrEmpty()) {
                    mFavouriteMap.put(model.id, position)
                }
                checkLogin {
                    val favourite = model.favourite
                    val id = model.id
                    if (favourite) {
                        model.favourite = false
                        viewModel.cancelFavorites(id)
                        MobclickAgent.onEventObject(this@HomeDetailsActivity, "20211213019", mapOf("modelId" to id))
                    } else {
                        model.favourite = true
                        viewModel.addFavorites(id)
                        MobclickAgent.onEventObject(this@HomeDetailsActivity, "20211213014", mapOf("modelId" to id))
                    }
                    recommendAdapter?.notifyItemChanged(position, -1)
                }
            }
        }
    }

    private fun initDetailsList(listImage: List<BodyImg>?) {
        if (listImage != null && listImage.isNotEmpty()) {
            binding.homeDetailsRV1.layoutManager = LinearLayoutManager(this)
            binding.homeDetailsRV1.adapter = HomeDetailsAdapter(this, listImage)
        } else {
            binding.homeDetailsTvDetails.visibility = View.GONE
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
            indicator = RoundLinesIndicator(this@HomeDetailsActivity)
            //设置指示器选中显示宽度
            setIndicatorSelectedWidth(BannerUtils.dp2px(20f))
            //设置默认指示器颜色
            setIndicatorNormalColor(ContextCompat.getColor(this@HomeDetailsActivity, R.color.color_ECECEC))
            //设置选中指示器颜色
            setIndicatorSelectedColor(ContextCompat.getColor(this@HomeDetailsActivity, R.color.color_CC001E))
            //设置距底部的距离
            setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, BannerUtils.dp2px(19f)))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        //取消收藏与进行收藏
        binding.homeDetailsIvCollect.setOnClickListener {
            if (SPUtil.getToken().isNullOrEmpty()) {
                viewModel.clothesData.value?.apply {
                    mFavouriteId = id
                }
            }
            checkLogin {
                viewModel.clothesData.value?.apply {
                    favourite = if (favourite) {
                        viewModel.cancelFavorites(id)
                        MobclickAgent.onEventObject(this@HomeDetailsActivity, "20211213019", mapOf("modelId" to id))
                        false
                    } else {
                        viewModel.addFavorites(id)
                        MobclickAgent.onEventObject(this@HomeDetailsActivity, "20211213014", mapOf("modelId" to id))
                        true
                    }
                    binding.homeDetailsIvCollect.setImageResource(if (favourite) R.mipmap.icon_collect_22 else R.mipmap.icon_uncollect_22)
                }
            }
        }
        viewModel.clothesData.observe(this, {
            likeNum = it.num    //被收藏的数量
            val createdAt = it.created_at
            val headImgList = it.head_img_list  //轮播图
            val favourite = it.favourite
            val bodyImgList = it.body_img_list
            val tagPos = it.tag_pos  //标签
            val shopUrl = it.shop_url

            binding.homeDetailsTvDate.text = CoreUtil.millisecond2Date(createdAt)
            binding.homeDetailsTvLikeNum.text = String.format(resources.getString(R.string.like_num, likeNum))
//            binding.homeDetailsTvPageViews.text = String.format(resources.getString(R.string.pageviews, 121)) //浏览量
            binding.homeDetailsTvTitle.text = it.title
            binding.homeDetailsIvCollect.setImageResource(if (favourite) R.mipmap.icon_collect_22 else R.mipmap.icon_uncollect_22)
            initBanner(headImgList)
            initDetailsList(bodyImgList)
            if (tagPos != null && tagPos.isNotEmpty()) {
                var addLabel = ""
                for (tagPo in tagPos) {
                    addLabel += resources.getString(R.string.labels, tagPo.label)
                }
                binding.homeDetailsTvLabel.text = addLabel
            } else {
                binding.homeDetailsTvLabel.visibility = View.GONE
            }

            //购买按钮
            if (shopUrl != null && shopUrl.isNotEmpty() && CoreUtil.isValidUrl(shopUrl)) {
                binding.homeDetailsTvBuy.background = ContextCompat.getDrawable(this, R.drawable.shape_333333_2)
                binding.homeDetailsLlBottom.visibility = View.VISIBLE
                binding.homeDetailsTvBuy.setOnClickListener {
                    checkLogin {
                        val checkPackage = CoreUtil.checkInstallSoftware(this@HomeDetailsActivity, "com.taobao.taobao")
                        if (checkPackage) { //隐试打开淘宝详情页
                            thread {
                                packageManager.getLaunchIntentForPackage("com.taobao.taobao")?.apply {
                                    action = "Android.intent.action.VIEW"
                                    val parse = Uri.parse(shopUrl)
                                    data = parse
                                    setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity")
                                    startActivity(this)
                                }
                            }
                        } else {
                            ToastUtils.show("请先安装淘宝")
                        }
                    }
                    MobclickAgent.onEventObject(this, "20211213016", mapOf("url" to shopUrl))
                }
            } else {
                binding.homeDetailsTvBuy.background = ContextCompat.getDrawable(this, R.drawable.shape_dcdddc_2)
            }
        })
        //为你推荐数据回调
        viewModel.clothesListData.observe(this, { lists ->
            val clothesList = lists.data
            if (clothesList.isNotEmpty()) {
                //推荐列表
                val filter = clothesList.filter { it.id != mId }
                if (filter.isNotEmpty()) {
                    recommendListData.addAll(filter)
                }
                recommendAdapter?.notifyDataSetChanged()
            }
        })

        //取消收藏成功,刷新首页数据改变收藏状态
        LiveEventBus.get<String>(ConstantsKey.CANCEL_FAVORITES_OK).observe(this, { favoritesId ->
            favoritesId?.let {
                if (recommendListData.isNotEmpty()) {
                    recommendListData.forEachIndexed { index, _ ->
                        if (recommendListData[index].id == favoritesId) {
                            recommendListData[index].favourite = false
                            recommendAdapter?.notifyItemChanged(index, -1)
                        }
                    }
                }
                likeNum -= 1
                binding.homeDetailsTvLikeNum.text = String.format(resources.getString(R.string.like_num, likeNum))
            }
        })
        //收藏成功
        LiveEventBus.get<String>(ConstantsKey.ADD_FAVORITES_OK).observe(this, { favoritesId ->
            if (recommendListData.isNotEmpty()) {
                recommendListData.forEachIndexed { index, _ ->
                    if (recommendListData[index].id == favoritesId) {
                        recommendListData[index].favourite = true
                        recommendAdapter?.notifyItemChanged(index, -1)
                    }
                }
            }
            likeNum += 1
            binding.homeDetailsTvLikeNum.text = String.format(resources.getString(R.string.like_num, likeNum))
        })

        //登录成功
        LiveEventBus.get<UserModel>("loginSuccess").observe(this, { models ->
            if (models.uid.isNotEmpty() && SPUtil.getToken() != null) {
                if (mId.isNotEmpty()) {
                    if (mFavouriteId.isNotEmpty()) {
                        viewModel.addFavorites(mFavouriteId) {
                            viewModel.clothesData.value?.let {
                                it.favourite = true
                                likeNum += 1
                                binding.homeDetailsTvLikeNum.text = String.format(resources.getString(R.string.like_num, likeNum))
                            }
                        }
                    }

                    if (mFavouriteMap.isNotEmpty()) {
                        val keys = mFavouriteMap.keys.toList()[0]
                        val position = mFavouriteMap.getValue(keys)
                        viewModel.addFavorites(keys) {
                            recommendAdapter?.notifyItemChanged(position, -1)
                        }
                    }
                }
            }
        })

        //回到首页
        binding.homeDetailsTvBackHome.setOnClickListener {
            ActivityManager.instance?.finishActivity(this)
        }
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
            val clothesList = repository.getClothesList(sort, mutableListOf(), 0, 20)
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

    fun addFavorites(id: String, observer: ((flag: String?) -> Unit?)? = null) {
        launch = viewModelScope.launch {
            val addFavorites = repository.addFavorites(id)
            if (addFavorites.isSuccessful) {
                addFavorites.body()?.let {
                    LiveEventBus.get(ConstantsKey.ADD_FAVORITES_OK, String::class.java).post(id)
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
                    LiveEventBus.get(ConstantsKey.CANCEL_FAVORITES_OK, String::class.java).post(id)
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