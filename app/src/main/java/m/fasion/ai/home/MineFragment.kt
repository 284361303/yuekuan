package m.fasion.ai.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import m.fasion.ai.databinding.FragmentMineBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.LogoutDialog
import m.fasion.core.util.CoreUtil

/**
 * 我的页面
 * 2021年9月23日
 */
class MineFragment : Fragment() {

    private var _inflate: FragmentMineBinding? = null

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
            CoreUtil.setTypeFaceMedium(listOf(it.mineTvName, it.mineTvAbout, it.mineTvProtocol, it.mineTvPrivacy, it.mineTvFeedback, it.mineTvLogout, it.mineTvToolBar))

            it.mineTvLogout.setOnClickListener {
                LogoutDialog(object : LogoutDialog.Callback {
                    override fun logoutCallBack() {
                        ToastUtils.show("退出登录成功")
                    }
                }).show(childFragmentManager, "logout")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _inflate = null
    }
}