package m.fasion.ai.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityLoginBinding
import m.fasion.ai.util.ToastUtils
import m.fasion.core.base.BaseViewModel
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.ErrorDataModel
import m.fasion.core.model.UserModel
import m.fasion.core.model.stringSuspending
import m.fasion.core.util.SPUtil

/**
 * 登录页面
 */
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var loginFragmnet: LoginFragment? = null

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.inCludeTitleIvBack.setOnClickListener { finish() }
        if (loginFragmnet == null) {
            loginFragmnet = LoginFragment()
        }
        supportFragmentManager.beginTransaction().add(R.id.login_frameLayout, loginFragmnet!!).commit()

        //验证码发送成功就切换输入验证码页面
        viewModel.replaceFragment.observe(this, { phone ->
            if (phone.isNotEmpty() && phone.length <= 11) {
                val codeFragment = InputCodeFragment()
                codeFragment.arguments = Bundle().also {
                    it.putString("phone", phone) //传送过去是脱敏的
                }
                supportFragmentManager.beginTransaction().replace(R.id.login_frameLayout, codeFragment).commit()
            }
        })

        /**
         * 登录成功就销毁当前页面
         */
        viewModel.loginLiveData.observe(this, {
            it?.let { model ->
                if (model.token.isNotEmpty()) {
                    LiveEventBus.get<UserModel>("loginSuccess").post(model)
                    ToastUtils.show("登录成功")
                    finish()
                }
            }
        })
    }
}

class LoginViewModel : BaseViewModel() {

    private var launch: Job? = null

    /**
     * 登录异常/失败
     */
    val errorLiveData = MutableLiveData<String>()

    //验证码发送成功就切换输入验证码页面
    val replaceFragment = MutableLiveData<String>()

    fun sendCode(phone: String) {
        launch = viewModelScope.launch {
            val sendCode = repository.sendCode(mutableMapOf("phone" to phone))
            if (sendCode.isSuccessful) {
                replaceFragment.value = phone
            } else {
                sendCode.errorBody()?.stringSuspending()?.let {
                    if (it.isEmpty()) {
                        ToastUtils.showLong(sendCode.message())
                    } else {
                        Gson().fromJson(it, ErrorDataModel::class.java)?.apply {
                            ToastUtils.showLong(message)
                        }
                    }
                }
            }
        }
    }


    /**
     * 登录成功
     */
    val loginLiveData = MutableLiveData<UserModel>()

    /**
     * 验证码进行登录
     * @param phone 手机号
     * @param code  验证码
     */
    fun getLogin(phone: String, code: String) {
        launch = viewModelScope.launch {
            val login = repository.getLogin(mutableMapOf("phone" to phone, "code" to code, "channel" to "styled"))
            if (login.isSuccessful) {
                login.body()?.let {
                    SPUtil.put(ConstantsKey.USER_TOKEN_KEY, it.token)
                    loginLiveData.value = it
                }
            } else {
                login.errorBody()?.stringSuspending()?.let {
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