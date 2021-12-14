package m.fasion.ai.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.databinding.FragmentHomeChildBinding
import m.fasion.ai.homeDetails.HomeDetailsActivity
import m.fasion.ai.util.LogUtils
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.model.Clothes
import m.fasion.core.model.ClothesList
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.stringSuspending

/**
 * 款式首页的瀑布流
 */
class HomeChildFragment : Fragment() {

    private var mAdapter: HomeChildAdapter? = null
    private lateinit var _binding: FragmentHomeChildBinding
    private val viewModel: HomeChildViewModel by activityViewModels()
    private var mPage: Int = 0
    private var mSort: String = ""
    private var mCategoryId = ""
    private var listData: MutableList<Clothes> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString("childTitle")?.let { title ->
                mSort = title
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentHomeChildBinding.inflate(inflater, container, false).apply {
            homeChildViewModel = viewModel
        }
        return _binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager()
        initAdapter()
        LogUtils.log("sourt参数",mSort)
        viewModel.getClothesList(mSort, mCategoryId, mPage)

        //列表数据回调
        viewModel.clothesListData.observe(requireActivity(), {
            listData.addAll(it.clothes_list)
            setLayoutManager()
            mAdapter?.notifyDataSetChanged()
        })
    }

    private fun setLayoutManager() {
        if (listData.isEmpty()) {
            _binding.homeChildRV.layoutManager = LinearLayoutManager(requireContext())
        } else {
            _binding.homeChildRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun initAdapter() {
        mAdapter = HomeChildAdapter(requireContext(), 0, listData)
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

class HomeChildViewModel : BaseViewModel() {

    private var launch: Job? = null
    val clothesListData = MutableLiveData<ClothesList>()
    val errorLiveData = MutableLiveData<String>()

    //获取款式列表
    fun getClothesList(sort: String, categoryId: String, page: Int) {
        launch = viewModelScope.launch {
            val clothesList = repository.getClothesList(sort, categoryId, page, 20)
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

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}