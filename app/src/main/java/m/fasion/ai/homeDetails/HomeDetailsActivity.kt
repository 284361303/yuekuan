package m.fasion.ai.homeDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.meiqia.core.MQManager
import com.meiqia.core.bean.MQAgent
import com.meiqia.core.bean.MQMessage
import com.meiqia.core.callback.OnClientOnlineCallback
import com.meiqia.meiqiasdk.util.MQConfig
import com.meiqia.meiqiasdk.util.MQIntentBuilder
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.RoundLinesIndicator
import com.youth.banner.util.BannerUtils
import m.fasion.ai.R
import m.fasion.ai.databinding.ActivityHomeDetailsBinding
import m.fasion.ai.home.HomeBannerModel
import m.fasion.ai.share.ShareActivity
import m.fasion.ai.util.LogUtils
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.util.CoreUtil

/**
 * 选款详情
 */
class HomeDetailsActivity : m.fasion.ai.base.BaseActivity() {

    companion object {
        fun startActivity(context: Context, text: String) {
            val intent = Intent(context, HomeDetailsActivity::class.java)
            val bundle = Bundle()
            if (text.isNotEmpty()) {
                bundle.putString("text", text)
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

        binding.homeDetailsTvPageViews.text = String.format(resources.getString(R.string.pageviews, 121))
        binding.homeDetailsTvLikeNum.text = String.format(resources.getString(R.string.like_num, 121))
        initBanner()
        CoreUtil.setTypeFaceMedium(listOf(binding.homeDetailsTvDetails, binding.homeDetailsTvRecommended, binding.homeDetailsTvTitle))
        //美洽聊天初始化
        viewModel.initMeiQia(this)
    }

    private fun initBanner() {
        //轮播Start
        val model1 = HomeBannerModel("1", "https://t7.baidu.com/it/u=3785402047,1898752523&fm=193&f=GIF")
        val model2 = HomeBannerModel("2", "https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg")
        val model3 = HomeBannerModel("3", "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg")
        val model4 = HomeBannerModel("4", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg")
        val model5 = HomeBannerModel("5", "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg")

        binding.homeDetailsBanner.apply {
            setAdapter(object :
                BannerImageAdapter<HomeBannerModel>(listOf(model1, model2, model3, model4, model5)) {
                override fun onBindView(holder: BannerImageHolder?, data: HomeBannerModel?, position: Int, size: Int) {
                    Glide.with(this@HomeDetailsActivity).load(data?.url).into(holder?.imageView!!)
                }
            })
            addBannerLifecycleObserver(this@HomeDetailsActivity)
            setOnBannerListener { data, position -> //点击事件
                ToastUtils.show(this@HomeDetailsActivity, position.toString())
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

        //商品介绍的图片列表
        val listImae = listOf("https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",
            "https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg",
            "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg")

        binding.homeDetailsRV1.layoutManager = LinearLayoutManager(this)
        binding.homeDetailsRV1.adapter = HomeDetailsAdapter(this, listImae)

        //推荐列表
        binding.homeDetailsRV2.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.homeDetailsRV2.adapter = RecommendAdapter(this, listImae)
    }

    private fun initClickListener() {
        binding.title.inCludeTitleIvBack.setOnClickListener { finish() }
        //分享按钮
        binding.title.inCludeTitleIvRight.setOnClickListener {
            ShareActivity.startActivity(this, "")
        }
        //聊天
        binding.homeDetailsIvChat.setOnClickListener {
            val intent = MQIntentBuilder(this).build()
            startActivity(intent)
        }
        binding.homeDetailsTvProduction.setOnClickListener {
            val intent = MQIntentBuilder(this).build()
            startActivity(intent)
        }
        //购买
        binding.homeDetailsTvBuy.setOnClickListener {

        }
    }
}

class HomeDetailsViewModel : BaseViewModel() {

    /**
     * 美洽聊天初始化
     */
    fun initMeiQia(context: Context) {
        val instance = MQManager.getInstance(context)
        MQConfig.isShowClientAvatar = true
    }
}