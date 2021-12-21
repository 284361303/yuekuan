package m.fasion.ai.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.jeremyliao.liveeventbus.LiveEventBus
import m.fasion.ai.R
import m.fasion.ai.databinding.FragmentMineBinding
import m.fasion.ai.login.LoginActivity
import m.fasion.ai.mine.userInfo.EditingUserActivity
import m.fasion.ai.mine.userInfo.EditingUserViewModel
import m.fasion.ai.toolbar.*
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.CustomizeDialog
import m.fasion.ai.webView.WebViewActivity
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.UserInfo
import m.fasion.core.model.UserModel
import m.fasion.core.util.CoreUtil
import m.fasion.core.util.SPUtil

/**
 * 我的页面
 * 2021年9月23日
 */
class MineFragment : Fragment() {

    private var _inflate: FragmentMineBinding? = null
    private val viewModel: EditingUserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _inflate = FragmentMineBinding.inflate(inflater, container, false)
        return _inflate?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _inflate?.let {
            CoreUtil.setTypeFaceMedium(listOf(it.mineTvName, it.mineTvAbout, it.mineTvProtocol, it.mineTvPrivacy, it.mineTvFeedbacks, it.mineTvLogout, it.mineTvToolBar))

            if (!SPUtil.getToken().isNullOrEmpty()) {
                viewModel.getUserInfo()
            }

            //退出登录
            it.mineTvLogout.setOnClickListener {
                val beginTransaction = childFragmentManager.beginTransaction()
                val findFragmentByTag = childFragmentManager.findFragmentByTag("logout")
                if (findFragmentByTag != null) {
                    beginTransaction.remove(findFragmentByTag)
                }
                beginTransaction.addToBackStack(null)
                CustomizeDialog(content = getString(R.string.quit), rightStr = getString(R.string.quit1), callback = object :
                    CustomizeDialog.Callback {
                    override fun rightClickCallBack() { //回调事件处
                        viewModel.logout()
                    }
                }).show(childFragmentManager, "logout")
            }

            //意见反馈
            it.mineTvFeedbacks.setOnClickListener {
                startActivity(Intent(requireContext(), FeedBacksActivity::class.java))
            }
            //关于我们
            it.mineTvAbout.setOnClickListener {
                startActivity(Intent(requireContext(), AboutUsActivity::class.java))
            }

            //用户协议
            it.mineTvProtocol.setOnClickListener {
                WebViewActivity.startActivity(requireContext(), "https://www.fasionai.com", "")
            }
            //隐私政策
            it.mineTvPrivacy.setOnClickListener {
                WebViewActivity.startActivity(requireContext(), "https://www.fasionai.com", "")
            }
            //编辑资料按钮事件
            it.mineEditData.setOnClickListener {
                if (SPUtil.getToken().isNullOrEmpty()) {
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                } else {
                    startActivity(Intent(requireContext(), EditingUserActivity::class.java))
                }
            }
            //我的喜欢
            it.mineMyFavorite.setOnClickListener {
                startActivity(Intent(requireContext(), MyFavoriteActivity::class.java))
            }

            //登录成功
            LiveEventBus.get<UserModel>("loginSuccess").observe(requireActivity(), { models ->
                if (models.uid.isNotEmpty() && SPUtil.getToken() != null) {
                    viewModel.getUserInfo()
                }
            })

            //修改用户信息成功就重新请求用户信息接口
            LiveEventBus.get<String>(ConstantsKey.EDIT_SUCCESS).observe(requireActivity(), { str ->
                if (str == "1") {
                    viewModel.getUserInfo()
                }
            })

            viewModel.logoutLiveData.observe(requireActivity(), { num ->
                if (num == 200) {
                    SPUtil.removeKey(ConstantsKey.USER_TOKEN_KEY)
                    loginError()
                    LiveEventBus.get(ConstantsKey.LOGOUT_SUCCESS, String::class.java).post("1")
                    ToastUtils.show("退出登录成功")
                }
            })
            viewModel.userInfoData.observe(requireActivity(), { userInfo ->
                userInfo?.let { model ->
                    loginSuccess(model)
                }
            })

            //我的喜欢数量
            viewModel.favoritesListData.observe(requireActivity(), { favorites ->
                it.mineTvLikeNum.text = favorites.total.toString()
            })
        }
    }

    /**
     * 登录成功
     */
    private fun loginSuccess(userModel: UserInfo) {
        _inflate?.let {
            it.mineTvEdit.visibility = View.VISIBLE
            it.mineTvLogout.visibility = View.VISIBLE
            it.mineTvName.text = userModel.nickname
            Glide.with(requireContext()).load(userModel.avatar).error(R.drawable.mine_pic).into(it.profileImage)
        }
    }

    /**
     * 没有登录
     */
    private fun loginError() {
        _inflate?.let {
            it.mineTvEdit.visibility = View.GONE
            it.mineTvName.text = getString(R.string.please_login)
            it.mineTvLogout.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (!SPUtil.getToken().isNullOrEmpty()) {
            viewModel.getClothesList("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _inflate = null
    }
}