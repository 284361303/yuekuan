package m.fasion.ai.login

import android.os.Bundle
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityLoginBinding

/**
 * 登录页面
 */
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var loginFragmnet: LoginFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (loginFragmnet == null) {
            loginFragmnet = LoginFragment()
        }
        supportFragmentManager.beginTransaction().add(R.id.login_frameLayout, loginFragmnet!!)
            .commit()
    }
}