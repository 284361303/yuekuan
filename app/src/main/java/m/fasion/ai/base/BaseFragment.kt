package m.fasion.ai.base

import android.content.Intent
import androidx.fragment.app.Fragment
import m.fasion.ai.login.LoginActivity
import m.fasion.core.util.SPUtil

open class BaseFragment : Fragment() {


    /**
     * 判断登录，没有登录就跳转到登录页面
     */
    protected open fun checkLogin(callback: () -> Unit) {
        if (!SPUtil.getToken().isNullOrEmpty()) {
            callback.invoke()
        } else {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }
}