package m.fasion.ai.homeDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.youth.banner.transformer.ScaleInTransformer
import com.youth.banner.util.BannerUtils
import m.fasion.ai.R
import m.fasion.ai.databinding.ActivityRecommendBinding
import m.fasion.ai.databinding.FragmentRecommendBinding
import m.fasion.ai.home.RecommendModel
import m.fasion.core.base.BaseViewModel
import m.fasion.core.util.CoreUtil

/**
 * 今日推荐页面
 */
class RecommendActivity : FragmentActivity() {
    private var currentPosition: Int = 1
    private val viewModel: RecommendViewModel by viewModels()

    private val binding by lazy {
        ActivityRecommendBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.title.inCludeTitleIvBack.setOnClickListener { finish() }
        CoreUtil.setTypeFaceMedium(arrayOf(binding.recommendTvRecommend1, binding.recommendTvRecommend2, binding.recommendTvCurrent).toList())
        initTablet()
        initClick()
    }

    private fun initTablet() {
        //有数据就显示底部切换按钮，否则就隐藏
        binding.recommendLlBtn.visibility = if (viewModel.tabList().isNotEmpty()) View.VISIBLE else View.INVISIBLE

        binding.recommendTvAll.text = viewModel.tabList().size.toString()
        binding.recommendVP.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = viewModel.tabList().size

            override fun createFragment(position: Int): Fragment {
                val recommendFragment = RecommendFragment()
                recommendFragment.arguments = Bundle().also {
                    it.putSerializable("commendItem", viewModel.tabList()[position])
                }
                return recommendFragment
            }
        }

        TabLayoutMediator(binding.recommendTabLayout, binding.recommendVP) { _, _ -> }.attach()

        //tabLayout滑动位置监听
        binding.recommendTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    currentPosition = it.position + 1
                    binding.recommendTvCurrent.text = (currentPosition).toString()
                    //获取当前下标并控制底部按钮点击事件
                    if (currentPosition > 0 && currentPosition < viewModel.tabList().size) {
                        binding.recommendIvLeft.setImageResource(R.mipmap.icon_btn_left_select)
                        binding.recommendIvLeft.isClickable = true
                    } else if (currentPosition == viewModel.tabList().size) {
                        binding.recommendIvLeft.setImageResource(R.mipmap.icon_btn_left_unselect)
                        binding.recommendIvLeft.isClickable = false
                    }
                    if (currentPosition > 1) {
                        binding.recommendIvRight.setImageResource(R.mipmap.icon_btn_right_select)
                        binding.recommendIvRight.isClickable = true
                    } else if (currentPosition == 1) {
                        binding.recommendIvRight.setImageResource(R.mipmap.icon_btn_right_unselect)
                        binding.recommendIvRight.isClickable = false
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        //设置画廊效果
        viewModel.setBannerGalleryEffect(this, binding.recommendVP, 12, 12, 8, .85f)
    }

    private fun initClick() {
        binding.recommendIvLeft.setOnClickListener {
            if (viewModel.tabList().isNotEmpty() && currentPosition > 0 && currentPosition < viewModel.tabList().size) {
                currentPosition += 1
                binding.recommendTabLayout.getTabAt(currentPosition - 1)?.select()
            }
        }
        binding.recommendIvRight.setOnClickListener {
            if (viewModel.tabList().isNotEmpty() && currentPosition > 1 && currentPosition <= viewModel.tabList().size) {
                currentPosition -= 1
                binding.recommendTabLayout.getTabAt(currentPosition - 1)?.select()
            }
        }
    }
}

class RecommendFragment : Fragment() {
    private lateinit var _binding: FragmentRecommendBinding

    private val viewModel: RecommendViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1 = it.getSerializable("commendItem") as RecommendModel
            viewModel.modelLiveData.value = param1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false).apply {
            recommendViewModel = viewModel
        }
        return _binding.root
    }
}

class RecommendViewModel : BaseViewModel() {
    val modelLiveData = MutableLiveData<RecommendModel>()

    fun tabList(): ArrayList<RecommendModel> {
        return arrayListOf(RecommendModel("https://i.stack.imgur.com/GvWB9.png"),
            RecommendModel("https://i.stack.imgur.com/Df5H2.png"),
            RecommendModel("https://lh3.googleusercontent.com/NSVbWbdKFGRzju5r5XsXKMJ9A41PVdWNhGSxDwxk9aO6o_7SeVMU8z27-GhdNw3uS0PZtLPts5tvaxdsHr--NRXZWfyi=s300"),
            RecommendModel("https://i.stack.imgur.com/Df5H2.png"),
            RecommendModel("https://lh3.googleusercontent.com/NSVbWbdKFGRzju5r5XsXKMJ9A41PVdWNhGSxDwxk9aO6o_7SeVMU8z27-GhdNw3uS0PZtLPts5tvaxdsHr--NRXZWfyi=s300"),
            RecommendModel("https://i.stack.imgur.com/GvWB9.png"))
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