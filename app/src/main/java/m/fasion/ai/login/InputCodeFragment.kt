package m.fasion.ai.login

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aisway.faceswap.utils.TimeCountdown
import m.fasion.ai.R
import m.fasion.core.base.ConstantsKey
import m.fasion.ai.databinding.FragmentInputCodeBinding
import m.fasion.ai.util.verifyCode.VerifyCodeView
import m.fasion.core.util.CoreUtil
import m.fasion.core.util.CoreUtil.toSecret
import m.fasion.core.util.SPUtil

/**
 *  输入验证码页面
 *  2021年11月9日10:48:43
 */
class InputCodeFragment : Fragment() {

    private var time: CountDownTimer? = null
    private lateinit var binding: FragmentInputCodeBinding
    private var mPhone: String? = null
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPhone = it.getString("phone")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentInputCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputCodeFragmentTvPhone.text = resources.getString(R.string.input_code_num, mPhone.toSecret())
        CoreUtil.setTypeFaceMedium(listOf(binding.inputCodeFragmentTv1))

        time = TimeCountdown().time(binding.inputCodeFragmentTvResend)

        //输入验证码监听
        binding.inputCodeFragmentVerifyCode.setInputCompleteListener(object :
            VerifyCodeView.InputCompleteListener {
            override fun inputComplete() {
                val code = binding.inputCodeFragmentVerifyCode.editContent
                if (code.isNotEmpty() && code.length >= 4) {
                    mPhone?.let {
                        viewModel.getLogin(it, code)
                    }
                }
            }

            override fun invalidContent() {
            }
        })

        //登录成功
        viewModel.loginLiveData.observe(requireActivity(), {
            it?.let {
                binding.inputCodeFragmentTvError.visibility = View.GONE
                binding.inputCodeFragmentTvError.text = ""
            }
        })
        //登录异常
        viewModel.errorLiveData.observe(requireActivity(), {
            if (it.isNotEmpty()) {
                binding.inputCodeFragmentTvError.text = it
                binding.inputCodeFragmentTvError.visibility = View.VISIBLE
                binding.inputCodeFragmentVerifyCode.clearContent()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        if (time != null) {
            time?.cancel()
            time?.onFinish()
        }
    }
}