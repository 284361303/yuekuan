package m.fasion.ai.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import m.fasion.ai.databinding.FragmentHomeChildBinding
import m.fasion.ai.homeDetails.HomeDetailsActivity
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel

/**
 * 款式首页的瀑布流
 */
class HomeChildFragment : Fragment() {

    private var mAdapter: HomeChildAdapter? = null
    private lateinit var _binding: FragmentHomeChildBinding
    private val viewModel: HomeChildViewModel by activityViewModels()
    private var param1: String? = null
    val lists = mutableListOf("https://i.stack.imgur.com/GvWB9.png",
        "https://lh3.googleusercontent.com/NSVbWbdKFGRzju5r5XsXKMJ9A41PVdWNhGSxDwxk9aO6o_7SeVMU8z27-GhdNw3uS0PZtLPts5tvaxdsHr--NRXZWfyi=s300",
        "https://t7.baidu.com/it/u=3785402047,1898752523&fm=193&f=GIF", "https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg",
        "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg",
        "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",
        "https://i.stack.imgur.com/GvWB9.png", "https://t7.baidu.com/it/u=3785402047,1898752523&fm=193&f=GIF", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("childTitle")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentHomeChildBinding.inflate(inflater, container, false).apply {
            homeChildViewModel = viewModel
        }
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager()
        initAdapter()
    }

    private fun setLayoutManager() {
        if (lists.isEmpty()) {
            _binding.homeChildRV.layoutManager = LinearLayoutManager(requireContext())
        } else {
            _binding.homeChildRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun initAdapter() {
        mAdapter = HomeChildAdapter(requireContext(), 0, lists)
        _binding.homeChildRV.adapter = mAdapter
        mAdapter?.onItemClickListener = object : HomeChildAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                HomeDetailsActivity.startActivity(requireContext(), "")
            }

            override fun onCollectClick(position: Int) {
                ToastUtils.show("收藏了 $position")
            }
        }
    }
}

class HomeChildViewModel : BaseViewModel()