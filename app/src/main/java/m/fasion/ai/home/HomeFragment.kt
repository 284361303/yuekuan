package m.fasion.ai.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.meiqia.core.MQManager
import com.meiqia.core.bean.MQAgent
import com.meiqia.core.bean.MQMessage
import com.meiqia.core.callback.OnClientOnlineCallback
import com.meiqia.meiqiasdk.util.MQConfig
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.databinding.FragmentHomeBinding
import m.fasion.ai.util.LogUtils
import m.fasion.ai.util.ToastUtils
import m.fasion.core.Config
import m.fasion.core.base.BaseViewModel
import m.fasion.core.model.LoginModel

/**
 * 首页Fragment
 * 2021年9月23日
 */
class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private var param1: String? = null

    companion object {
        const val TAG = "HomeFragmentTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreateeeeee: ")
        arguments?.let {
            param1 = it.getString("name")
        }
    }

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
        viewModel.getLogin("admin", "passwd1")

        val instance = MQManager.getInstance(requireContext())
        MQConfig.isShowClientAvatar = true
        instance.setCurrentClientOnline(object : OnClientOnlineCallback {
            override fun onFailure(p0: Int, p1: String?) {
                LogUtils.log(TAG, "上线失败: " + p1)
            }

            override fun onSuccess(p0: MQAgent?, p1: String?, p2: MutableList<MQMessage>?) {
                LogUtils.log(TAG, "上线成功: " + p1)
            }
        })

        viewModel.errorLiveData.observe(requireActivity(), {
            System.currentTimeMillis()
        })

        // viewPager2   初始化适配器
        _binding.homeFragmentVP.adapter = object : FragmentStateAdapter(requireActivity()) {
            override fun getItemCount(): Int {
                return viewModel.tabList.size
            }

            override fun createFragment(position: Int): Fragment {
                val homeItemFragment = HomeItemFragment()
                homeItemFragment.arguments = Bundle().also {
                    it.putString("title", viewModel.tabList[position])
                }
                return homeItemFragment
            }
        }
        //初始化Tablayout和Fragment
        TabLayoutMediator(_binding.homeFragmentTablayout, _binding.homeFragmentVP) { tab, position ->
            tab.text = viewModel.tabList[position]
        }.attach()

        /*_binding.homeFragmentBtn.setOnClickListener {
            val random = java.util.Random()
            val i = random.nextInt(10) + 1
            ShareActivity.startActivity(requireContext(), "")
        }
        //测试聊天
        _binding.homeFragmentChat.setOnClickListener {
            *//*val intent = MQIntentBuilder(requireContext())
//                .setCustomizedId("1") // 相同的 id 会被识别为同一个顾客
                .build()
            startActivity(intent)*//*
            ProgressDialog.showProgress(childFragmentManager)

            CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                ProgressDialog.dismissProgress()
//                ToastUtils.show(requireContext(), "你好安安")
            }
        }*/
    }
}

class HomeViewModel : BaseViewModel() {

    private var launch: Job? = null
    val loginLiveData = MutableLiveData<LoginModel>()
    val errorLiveData = MutableLiveData<String>()

    val tabList = arrayOf("女装", "男装", "童装")

    fun getLogin(name: String, pwd: String) {
        val maps: MutableMap<String, String> = mutableMapOf()
        maps.put("username", name)
        maps.put("password", pwd)

        launch = viewModelScope.launch {
            try {
                val login = repository.getLogin(maps)
                if (login.isSuccessful) {
                    loginLiveData.value = login.body()
                } else {
                    errorLiveData.value = "用户名或密码错误"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 测试微信分享
     */
    fun startWXShare(view: View) {
        /*val context = view.context
        Toast.makeText(context, "点击了", Toast.LENGTH_SHORT).show()
        val createWXAPI = WXAPIFactory.createWXAPI(context, Config.APP_ID, false)
        WXShareUtil.shareText(context, "测试首页", 0, createWXAPI)*/
    }

    /**
     * 测试微信授权登录获取用户token
     */
    fun startWXLogin(view: View) {
        val context = view.context
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"    //应用授权作用域，如获取用户个人信息则填写 snsapi_userinfo
//        req.state = ""
        val createWXAPI = WXAPIFactory.createWXAPI(context, Config.APP_ID, false)
        createWXAPI.sendReq(req)
    }

    override fun onCleared() {
        super.onCleared()
        launch?.cancel()
    }
}