package m.fasion.ai.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.alipay.sdk.app.PayTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import m.fasion.ai.databinding.FragmentHomeItemBinding
import m.fasion.ai.util.LogUtils
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel

/**
 *
 */
class HomeItemFragment : Fragment() {

    private var mTitle: String? = null
    private lateinit var _binding: FragmentHomeItemBinding
    private val viewModel: HomeItemViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mTitle = it.getString("title")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeItemBinding.inflate(inflater, container, false).apply { homteItemViewModel = viewModel }
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.homeItemTvTitle.setText(mTitle)

        //测试支付宝支付
        _binding.homeItemIvDouYin.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val payTask = PayTask(requireActivity())
                payTask.payV2("", true)?.apply {
                    val resultStatus = get("resultStatus")
                    val result = get("result")
                    val memo = get("memo")
                    //返回的结果-- 4000 ,result--  ,memo-- 系统繁忙，请稍后再试
                    withContext(Dispatchers.Main) {
                        when (resultStatus) {
                            "9000" -> { //9000则代表支付成功

                            }
                            "4000" -> {
                                ToastUtils.show(requireContext(), "请安装最新版本支付宝")
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }
}

class HomeItemViewModel() : BaseViewModel() {

    /**
     * 更多点击事件
     */
    /*fun clickMore(view: View) {
        val context = view.context
        viewModelScope.launch(Dispatchers.IO) {
            PayTask(view)
            withContext(Dispatchers.Main) {
                ToastUtils.show(context, "11111")
            }
        }
    }*/

    fun clickMore2(view: View) {
        val context = view.context
        ToastUtils.show(context, "222222")
    }
}