package m.fasion.ai.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import m.fasion.ai.R
import m.fasion.ai.databinding.FragmentMineBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.ai.util.customize.CustomizeDialog
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
                val beginTransaction = childFragmentManager.beginTransaction()
                val findFragmentByTag = childFragmentManager.findFragmentByTag("logout")
                if (findFragmentByTag != null) {
                    beginTransaction.remove(findFragmentByTag)
                }
                beginTransaction.addToBackStack(null)
                CustomizeDialog(content = getString(R.string.quit), rightStr = getString(R.string.quit1), callback = object :
                    CustomizeDialog.Callback {
                    override fun rightClickCallBack() { //回调事件处
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