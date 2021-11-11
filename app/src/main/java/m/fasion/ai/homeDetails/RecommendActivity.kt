package m.fasion.ai.homeDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import m.fasion.ai.R
import m.fasion.ai.databinding.ActivityRecommendBinding
import m.fasion.ai.databinding.FragmentRecommendBinding
import m.fasion.ai.home.RecommendModel
import m.fasion.core.base.BaseViewModel
import m.fasion.core.util.CoreUtil

/**
 * 今日推荐
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
            override fun getItemCount(): Int {
                return viewModel.tabList().size
            }

            override fun createFragment(position: Int): Fragment {
                val recommendFragment = RecommendFragment()
                recommendFragment.arguments = Bundle().also {
                    it.putSerializable("commendItem", viewModel.tabList()[position])
                }
                return recommendFragment
            }
        }

        TabLayoutMediator(binding.recommendTabLayout, binding.recommendVP) { _, _ -> }.attach()

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
}